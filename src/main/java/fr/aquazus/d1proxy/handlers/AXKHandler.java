package fr.aquazus.d1proxy.handlers;

import fr.aquazus.d1proxy.Proxy;
import fr.aquazus.d1proxy.network.ProxyClient;
import simplenet.packet.Packet;

public class AXKHandler implements PacketHandler {

    private final Proxy proxy;

    public AXKHandler(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public boolean shouldForward(ProxyClient proxyClient, String packet, PacketDestination destination) {
        proxyClient.setAuthTicket(packet.substring(14));
        String[] decoded = proxy.getProxyCipher().decodeAXK(packet).split(":");
        proxyClient.switchToGame(decoded[0], Integer.parseInt(decoded[1]));
        return false;
    }

}
