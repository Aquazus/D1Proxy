package fr.aquazus.d1proxy.handlers;

import fr.aquazus.d1proxy.Proxy;
import fr.aquazus.d1proxy.network.ProxyClient;

public class ASKHandler implements PacketHandler {

    private Proxy proxy;

    public ASKHandler(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public boolean shouldForward(ProxyClient proxyClient, String packet) {
        String[] extraData = packet.split("\\|");
        System.out.println("[" + proxyClient.getIp() + "] Setting username as: " + extraData[2]);
        proxyClient.setUsername(extraData[2]);
        if (proxy.getDatabase().getProfilesCollection().profileExists(extraData[2])) {
            proxy.sendMessage("<b>" + extraData[2] + "</b> vient de se connecter via le proxy.");
        } else {
            proxy.getDatabase().getProfilesCollection().insertNewProfile(extraData[2]);
            proxy.sendMessage("<b>" + extraData[2] + "</b> est nouveau sur D1Proxy, bienvenue !");
        }
        return true;
    }
}
