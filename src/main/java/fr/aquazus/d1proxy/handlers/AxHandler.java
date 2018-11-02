package fr.aquazus.d1proxy.handlers;

import fr.aquazus.d1proxy.network.ProxyClient;
import fr.aquazus.d1proxy.network.ProxyClientState;

public class AxHandler implements PacketHandler {

    @Override
    public boolean shouldForward(ProxyClient proxyClient, String packet) {
        proxyClient.setState(ProxyClientState.SERVER_SELECT);
        return true;
    }
}
