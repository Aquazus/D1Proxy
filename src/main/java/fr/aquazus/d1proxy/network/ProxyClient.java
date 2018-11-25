package fr.aquazus.d1proxy.network;

import fr.aquazus.d1proxy.Proxy;
import fr.aquazus.d1proxy.handlers.PacketHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import simplenet.Client;
import simplenet.packet.Packet;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Slf4j
public class ProxyClient {

    private final Proxy proxy;
    @Getter @Setter
    private ProxyClientState state;
    @Getter
    private final Client client;
    @Getter
    private Client server;
    @Getter
    private final String ip;
    @Getter @Setter
    private String authTicket;
    @Getter @Setter
    private int characterId;
    @Getter @Setter
    private String username;
    @Getter @Setter
    private int currentMap;
    @Getter @Setter
    private boolean autoSkipEnabled;
    @Getter @Setter
    private boolean antiAfkEnabled;

    public ProxyClient(Proxy proxy, Client client, String ip) {
        this.proxy = proxy;
        this.state = ProxyClientState.INITIALIZING;
        this.client = client;
        this.ip = ip;
        this.connectAuth();
    }

    private void connectAuth() {
        Client authServer = new Client(1024);
        authServer.onConnect(() -> {
            this.log("auth tunnel opened!");
            server = authServer;
            handleClient(client);
            handleServer(authServer);
        });
        authServer.postDisconnect(() -> this.log("auth tunnel closed!"));
        authServer.connect(proxy.getConfiguration().getDofusIp(), proxy.getConfiguration().getDofusPort());
    }

    private void connectGame(String ip, int port) {
        Client gameServer = new Client(1024);
        gameServer.onConnect(() -> {
            this.log("game tunnel opened!");
            server = gameServer;
            handleServer(gameServer);
        });
        gameServer.postDisconnect(() -> {
            this.log("game tunnel closed!");
            this.disconnect();
        });
        gameServer.connect(ip, port);
    }

    private void handleClient(Client client) {
        ByteArrayOutputStream clientStream = new ByteArrayOutputStream();
        client.readByteAlways(data -> {
            if (data == (byte) 0) {
                String packet = new String(clientStream.toByteArray(), StandardCharsets.UTF_8);
                clientStream.reset();
                this.log("--> " + packet.substring(0, packet.length() - 1));
                if (server.getChannel().isOpen() && shouldForward(packet)) splitAndFlush(packet, server);
                return;
            }
            clientStream.write(data);
        });
        client.postDisconnect(() -> {
            this.log("disconnected!");
            this.disconnect();
        });
    }

    private void handleServer(Client server) {
        ByteArrayOutputStream gameStream = new ByteArrayOutputStream();
        server.readByteAlways(data -> {
            if (data == (byte) 0) {
                String packet = new String(gameStream.toByteArray(), StandardCharsets.UTF_8);
                gameStream.reset();
                this.log("<-- " + packet);
                if (client.getChannel().isOpen() && shouldForward(packet)) splitAndFlush(packet, client);
                return;
            }
            gameStream.write(data);
        });
    }

    public void switchToGame(String ip, int port) {
        log.info("Switching to game server " + ip + ":" + port);
        state = ProxyClientState.SERVER_CONNECTING;
        connectGame(ip, port);
    }

    @Synchronized
    private void disconnect() {
        if (client.getChannel().isOpen()) client.close();
        if (server.getChannel().isOpen()) server.close();
        if (state != ProxyClientState.DISCONNECTED) {
            if (state == ProxyClientState.INGAME) {
                proxy.sendMessage("<b>" + username + "</b> vient de se déconnecter du proxy.");
            }
            state = ProxyClientState.DISCONNECTED;
            proxy.getClients().remove(this);
        }
    }

    private boolean shouldForward(String packet) {
        if (packet.length() < 2) {
            return true;
        }

        boolean forward = true;

        String id = packet.substring(0, 2);
        if (proxy.getHandlers().containsKey(id)) {
            for (PacketHandler handlers : proxy.getHandlers().get(id)) {
                if (!handlers.shouldForward(this, packet)) {
                    forward = false;
                }
            }
        }

        if (packet.length() > 2) {
            String longerId = packet.substring(0, 3);
            if (proxy.getHandlers().containsKey(longerId)) {
                for (PacketHandler handlers : proxy.getHandlers().get(longerId)) {
                    if (!handlers.shouldForward(this, packet)) {
                        forward = false;
                    }
                }
            }
        }
        return forward;
    }

    public boolean executeCommand(String command) {
        String prefix = command.split(" ")[0].toLowerCase();
        if (proxy.getCommands().containsKey(prefix)) {
            proxy.getCommands().get(prefix).execute(this, (command.length() >= prefix.length() + 1 ? command.substring(prefix.length() + 1) : command.substring(prefix.length())));
            return true;
        }
        return false;
    }

    public void sendMessage(String message) {
        if (state == ProxyClientState.INGAME && client.getChannel().isOpen()) Packet.builder().putBytes(("cs<font color='#2C49D7'>" + message + "</font>").getBytes(StandardCharsets.UTF_8)).putByte(0).writeAndFlush(client);
    }

    public void log(String message) {
        String format = "[" + ip + (username == null ? "" : " - " + username) + "] " + message;
        if (message.startsWith("-->") || message.startsWith("<--")) {
            log.debug(format);
        } else {
            log.info(format);
        }
    }

    private void splitAndFlush(String packet, Client destination) {
        try {
            StringReader reader = new StringReader(packet);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream(2048);
            OutputStreamWriter writer = new OutputStreamWriter(buffer, StandardCharsets.UTF_8);
            char[] cbuf = new char[1024];
            byte[] tempBuf;
            int len;
            while ((len = reader.read(cbuf, 0, cbuf.length)) > 0) {
                writer.write(cbuf, 0, len);
                writer.flush();
                if (buffer.size() >= 1024) {
                    tempBuf = buffer.toByteArray();
                    Packet.builder().putBytes(buffer.toByteArray()).writeAndFlush(destination);
                    buffer.reset();
                    if (tempBuf.length > 1024) {
                        buffer.write(tempBuf, 1024, tempBuf.length - 1024);
                    }
                }
            }
            if (buffer.size() >= 1024) {
                Packet.builder().putBytes(buffer.toByteArray()).writeAndFlush(destination);
                Packet.builder().putByte(0).writeAndFlush(destination);
            } else {
                Packet.builder().putBytes(buffer.toByteArray()).putByte(0).writeAndFlush(destination);
            }

        } catch (Exception ex) {
            log.error("An error occurred while splitting a packet", ex);
        }
    }
}
