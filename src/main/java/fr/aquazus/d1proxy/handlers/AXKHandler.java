package fr.aquazus.d1proxy.handlers;

import fr.aquazus.d1proxy.network.ProxyCipher;
import fr.aquazus.d1proxy.network.ProxyClient;

public class AXKHandler implements PacketHandler {

    @Override
    public boolean shouldForward(ProxyClient proxyClient, String packet, PacketDestination destination) {
        proxyClient.setAuthTicket(packet.substring(14));
        String[] decoded = ProxyCipher.decodeAXK(packet).split(":");
        proxyClient.switchToGame(decoded[0], Integer.parseInt(decoded[1]));
        return false;
    }

}
