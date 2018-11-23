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
        this.connectTunnel();
    }

    private void connectTunnel() {
        this.server = new Client(1024);
        server.onConnect(() -> {
            this.log("tunnel opened!");
            ByteArrayOutputStream clientStream = new ByteArrayOutputStream();
            client.readByteAlways(data -> {
                if (data == (byte) 0) {
                    String packet = new String(clientStream.toByteArray(), StandardCharsets.UTF_8);
                    clientStream.reset();
                    this.log("--> " + packet.substring(0, packet.length() - 1));
                    //if (server.getChannel().isOpen() && shouldForward(packet)) Packet.builder().putBytes(packet.getBytes()).putByte(0).writeAndFlush(server);
                    if (server.getChannel().isOpen() && shouldForward(packet)) splitAndFlush(packet, server);
                    return;
                }
                clientStream.write(data);
            });
            ByteArrayOutputStream gameStream = new ByteArrayOutputStream();
            server.readByteAlways(data -> {
                if (data == (byte) 0) {
                    String packet = new String(gameStream.toByteArray(), StandardCharsets.UTF_8);
                    gameStream.reset();
                    this.log("<-- " + packet);
                    //if (client.getChannel().isOpen() && shouldForward(packet)) Packet.builder().putBytes(packet.getBytes()).putByte(0).writeAndFlush(client);
                    if (client.getChannel().isOpen() && shouldForward(packet)) splitAndFlush(packet, client);
                    return;
                }
                gameStream.write(data);
            });
        });
        client.postDisconnect(() -> {
            this.log("disconnected!");
            this.disconnect();
        });
        server.postDisconnect(() -> {
            this.log("tunnel closed!");
            this.disconnect();
        });
        if (proxy.getExchangeCache().containsKey(ip)) {
            this.log("Found game address in exchange cache, tunneling to the right server...");
            String address[] = proxy.getExchangeCache().get(ip).split(":");
            proxy.getExchangeCache().remove(ip);
            server.connect(address[0], Integer.parseInt(address[1]));
        } else {
            server.connect(proxy.getConfiguration().getDofusIp(), proxy.getConfiguration().getDofusPort());
        }
    }

    @Synchronized
    private void disconnect() {
        if (client.getChannel().isOpen()) client.close();
        if (server.getChannel().isOpen()) server.close();
        if (state != ProxyClientState.DISCONNECTED) {
            if (state == ProxyClientState.INGAME) {
                state = ProxyClientState.DISCONNECTED;
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
            ex.printStackTrace();
        }
    }
}
