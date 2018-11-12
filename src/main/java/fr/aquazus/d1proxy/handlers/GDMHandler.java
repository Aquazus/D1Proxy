package fr.aquazus.d1proxy.handlers;

import fr.aquazus.d1proxy.Proxy;
import fr.aquazus.d1proxy.network.ProxyClient;

public class GDMHandler implements PacketHandler {

    private Proxy proxy;

    public GDMHandler(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public boolean shouldForward(ProxyClient proxyClient, String packet) {
        if (!proxy.isSniffing()) return true;
        String[] extraData = packet.split("\\|");
        int mapId = Integer.parseInt(extraData[1]);
        String mapDate = extraData[2];
        String mapKey = extraData[3];
        proxyClient.setCurrentMap(mapId);
        if (!proxy.getDatabase().getMapsCollection().mapExists(mapId)) {
            proxy.getDatabase().getMapsCollection().insertNewMap(mapId, proxyClient.getUsername(), mapDate, mapKey);
            proxy.sendMessage("<b>" + proxyClient.getUsername() + "</b> a découvert une nouvelle map ! <i>(ID: " + mapId + ")</i>");
        }
        return true;
    }
}
