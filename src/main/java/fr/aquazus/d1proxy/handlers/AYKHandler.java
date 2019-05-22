package fr.aquazus.d1proxy.handlers;

import fr.aquazus.d1proxy.network.ProxyClient;

public class AYKHandler implements PacketHandler {

    @Override
    public boolean shouldForward(ProxyClient proxyClient, String packet, PacketDestination destination) {
        String[] extraData = packet.substring(3).split(";");
        String[] address = extraData[0].split(":");
        proxyClient.setAuthTicket(extraData[1]);
        proxyClient.switchToGame(address[0], Integer.parseInt(address[1]));
        return false;
    }

}
