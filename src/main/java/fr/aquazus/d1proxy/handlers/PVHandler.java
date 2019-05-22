package fr.aquazus.d1proxy.handlers;

import fr.aquazus.d1proxy.network.ProxyClient;

public class PVHandler implements PacketHandler {

    @Override
    public boolean shouldForward(ProxyClient proxyClient, String packet, PacketDestination destination) {
        if (destination == PacketDestination.CLIENT) proxyClient.setGroupLeader(0);
        return true;
    }
}
