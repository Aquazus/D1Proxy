package fr.aquazus.d1proxy.handlers;

import fr.aquazus.d1proxy.Proxy;
import fr.aquazus.d1proxy.network.ProxyClient;
import simplenet.packet.Packet;

public class PLHandler implements PacketHandler {

    private final Proxy proxy;

    public PLHandler(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public boolean shouldForward(ProxyClient proxyClient, String packet, PacketDestination destination) {
        proxyClient.setGroupLeader(Integer.parseInt(packet.substring(2)));
        return true;
    }
}
