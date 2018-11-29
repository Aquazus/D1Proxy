package fr.aquazus.d1proxy.handlers;

import fr.aquazus.d1proxy.Proxy;
import fr.aquazus.d1proxy.network.ProxyClient;
import fr.aquazus.d1proxy.network.ProxyClientState;
import simplenet.packet.Packet;

public class HGHandler implements PacketHandler {

    private final Proxy proxy;

    public HGHandler(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public boolean shouldForward(ProxyClient proxyClient, String packet, PacketDestination destination) {
        Packet.builder().putBytes("AT".getBytes()).putBytes(proxyClient.getAuthTicket().getBytes()).putByte(10).putByte(0).writeAndFlush(proxyClient.getServer());
        return false;
    }
}
