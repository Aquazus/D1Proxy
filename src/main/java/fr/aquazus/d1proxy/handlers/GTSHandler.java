package fr.aquazus.d1proxy.handlers;

import fr.aquazus.d1proxy.Proxy;
import fr.aquazus.d1proxy.network.ProxyClient;
import fr.aquazus.d1proxy.network.ProxyClientState;
import simplenet.packet.Packet;

public class GTSHandler implements PacketHandler {

    private Proxy proxy;

    public GTSHandler(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public boolean shouldForward(ProxyClient proxyClient, String packet) {
        String[] extraData = packet.substring(3).split("\\|");
        if (proxyClient.isAutoSkip() && proxyClient.getCharacterId() == Integer.parseInt(extraData[0])) {
            Packet.builder().putBytes("Gt".getBytes()).putByte(0).writeAndFlush(proxyClient.getServer());
            Packet.builder().putBytes("GTF".getBytes()).putBytes(String.valueOf(proxyClient.getCharacterId()).getBytes()).putByte(0).writeAndFlush(proxyClient.getClient());
            Packet.builder().putBytes("GTR".getBytes()).putBytes(String.valueOf(proxyClient.getCharacterId()).getBytes()).putByte(0).writeAndFlush(proxyClient.getClient());
            proxyClient.sendMessage("Tour passé automatiquement");
        }
        return true;
    }
}
