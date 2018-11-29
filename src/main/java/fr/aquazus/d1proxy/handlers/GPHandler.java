package fr.aquazus.d1proxy.handlers;

import fr.aquazus.d1proxy.Proxy;
import fr.aquazus.d1proxy.network.ProxyClient;
import org.bson.Document;

public class GPHandler implements PacketHandler {

    private final Proxy proxy;

    public GPHandler(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public boolean shouldForward(ProxyClient proxyClient, String packet, PacketDestination destination) {
        if (!proxy.getConfiguration().isProxySniffing() || packet.length() < 4) return true;
        String extraData[] = packet.substring(2).split("\\|");
        if (proxy.getDatabase().getMapsCollection().mapHasFightCells(proxyClient.getCurrentMap())) {
            return true;
        }
        proxy.getDatabase().getMapsCollection().updateMap(proxyClient.getCurrentMap(), new Document().append("fightCells", new Document().append("red", proxy.getProxyCipher().decodeGP(extraData, 0)).append("blue", proxy.getProxyCipher().decodeGP(extraData, 1))));
        proxy.getDatabase().getProfilesCollection().incrementFightCells(proxyClient.getUsername());
        proxy.sendMessage("<b>" + proxyClient.getUsername() + "</b> a découvert de nouvelles cellules de combat ! <i>(MapID: " + proxyClient.getCurrentMap() + ")</i>");
        return true;
    }
}
