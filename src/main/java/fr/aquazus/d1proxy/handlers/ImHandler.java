package fr.aquazus.d1proxy.handlers;

import fr.aquazus.d1proxy.Proxy;
import fr.aquazus.d1proxy.network.ProxyClient;
import fr.aquazus.d1proxy.network.ProxyClientState;
import simplenet.packet.Packet;

public class ImHandler implements PacketHandler {

    private Proxy proxy;

    public ImHandler(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public boolean shouldForward(ProxyClient proxyClient, String packet) {
        if (packet.startsWith("Im0153;")) {
            proxyClient.setState(ProxyClientState.INGAME);
            Packet.builder().putBytes(packet.getBytes()).putByte(0).writeAndFlush(proxyClient.getClient());
            proxyClient.sendMessage("Bienvenue sur D1Proxy " + proxy.getVersion() + " !");
            return false;
        }
        return true;
    }
}
