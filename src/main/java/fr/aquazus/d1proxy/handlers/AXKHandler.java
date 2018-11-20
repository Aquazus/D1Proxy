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
    public boolean shouldForward(ProxyClient proxyClient, String packet) {
        proxyClient.log("Intercepted AXK. Forging and flushing a new one!");
        proxy.getExchangeCache().put(proxyClient.getIp(), proxy.getProxyCipher().decodeAXK(packet));
        String newPacket = "AXK" + proxy.getProxyCipher().encodeAXK(proxy.getConfiguration().getProxyIp() + ":" + proxy.getConfiguration().getProxyPort()) + packet.substring(14);
        Packet.builder().putBytes(newPacket.getBytes()).putByte(0).writeAndFlush(proxyClient.getClient());
        return false;
    }

}
