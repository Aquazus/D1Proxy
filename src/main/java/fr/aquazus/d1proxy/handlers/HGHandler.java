package fr.aquazus.d1proxy.handlers;

import fr.aquazus.d1proxy.network.ProxyClient;
import simplenet.packet.Packet;

public class HGHandler implements PacketHandler {

    @Override
    public boolean shouldForward(ProxyClient proxyClient, String packet, PacketDestination destination) {
        Packet.builder().putBytes("AT".getBytes()).putBytes(proxyClient.getAuthTicket().getBytes()).putByte(10).putByte(0).writeAndFlush(proxyClient.getServer());
        return false;
    }
}
