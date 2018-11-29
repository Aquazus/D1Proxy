package fr.aquazus.d1proxy.handlers;

import fr.aquazus.d1proxy.network.ProxyClient;
import simplenet.packet.Packet;

public class GTSHandler implements PacketHandler {

    @Override
    public boolean shouldForward(ProxyClient proxyClient, String packet, PacketDestination destination) {
        String[] extraData = packet.substring(3).split("\\|");
        if (proxyClient.isAutoSkipEnabled() && proxyClient.getCharacterId() == Integer.parseInt(extraData[0])) {
            Packet.builder().putBytes("Gt".getBytes()).putByte(10).putByte(0).writeAndFlush(proxyClient.getServer());
            proxyClient.sendMessage("Tour passé automatiquement");
        }
        return true;
    }
}
