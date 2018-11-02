package fr.aquazus.d1proxy.handlers;

import fr.aquazus.d1proxy.network.ProxyClient;

public interface PacketHandler {
    boolean shouldForward(ProxyClient proxyClient, String packet);
}
