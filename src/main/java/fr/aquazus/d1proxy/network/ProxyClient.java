package fr.aquazus.d1proxy.network;

import fr.aquazus.d1proxy.Proxy;
import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;
import simplenet.Client;
import simplenet.packet.Packet;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class ProxyClient {

    private Proxy proxy;
    @Getter @Setter
    private ProxyClientState state;
    @Getter
    private Client client;
    private Client server;
    @Getter
    private String ip;
    @Getter @Setter
    private String username;
    @Getter @Setter
    private int currentMap;

    public ProxyClient(Proxy proxy, Client client, String ip) {
        this.proxy = proxy;
        this.state = ProxyClientState.INITIALIZING;
        this.client = client;
        this.ip = ip;
        this.connectTunnel();
    }

    private void connectTunnel() {
        this.server = new Client(8192); //TODO Reduce to the default buffer size of 4096 and split if needed
        server.onConnect(() -> {
            System.out.println("[" + ip + "] tunnel opened!");
            ByteArrayOutputStream clientStream = new ByteArrayOutputStream();
            client.readByteAlways(data -> {
                if (data == (byte) 0) {
                    String packet = new String(clientStream.toByteArray(), StandardCharsets.UTF_8);
                    clientStream.reset();
                    if (proxy.isDebug()) System.out.print("[" + ip + "] --> " + packet);
                    if (shouldForward(packet) && server.getChannel().isOpen()) Packet.builder().putBytes(packet.getBytes()).putByte(0).writeAndFlush(server);
                    return;
                }
                clientStream.write(data);
            });
            ByteArrayOutputStream gameStream = new ByteArrayOutputStream();
            server.readByteAlways(data -> {
                if (data == (byte) 0) {
                    String packet = new String(gameStream.toByteArray(), StandardCharsets.UTF_8);
                    gameStream.reset();
                    if (proxy.isDebug()) System.out.println("[" + ip + "] <-- " + packet);
                    if (shouldForward(packet) && client.getChannel().isOpen()) Packet.builder().putBytes(packet.getBytes()).putByte(0).writeAndFlush(client);
                    return;
                }
                gameStream.write(data);
            });
        });
        client.postDisconnect(() -> {
            System.out.println("[" + ip + "] disconnected!");
            this.disconnect();
        });
        server.postDisconnect(() -> {
            System.out.println("[" + ip + "] tunnel closed!");
            this.disconnect();
        });
        if (proxy.getExchangeCache().containsKey(ip)) {
            System.out.println("[" + ip + "] Found game address in exchange cache, tunneling to the right server...");
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

        String id = packet.substring(0, 2);
        if (proxy.getHandlers().containsKey(id)) {
            return proxy.getHandlers().get(id).shouldForward(this, packet);
        }

        if (packet.length() > 2) {
            String longerId = packet.substring(0, 3);
            if (proxy.getHandlers().containsKey(longerId)) {
                return proxy.getHandlers().get(longerId).shouldForward(this, packet);
            }
        }
        return true;
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
}


