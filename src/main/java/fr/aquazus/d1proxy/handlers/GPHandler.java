package fr.aquazus.d1proxy.handlers;

import fr.aquazus.d1proxy.Proxy;
import fr.aquazus.d1proxy.network.ProxyClient;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GPHandler implements PacketHandler {

    private Proxy proxy;
    private List<Character> zkArray;

    public GPHandler(Proxy proxy) {
        this.proxy = proxy;
        this.zkArray = Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_');
    }

    @Override
    public boolean shouldForward(ProxyClient proxyClient, String packet) {
        if (!proxy.isSniffing()) return true;
        String extraData[] = packet.substring(2).split("\\|");
        if (proxy.getDatabase().getMapsCollection().mapHasFightCells(proxyClient.getCurrentMap())) {
            return true;
        }
        List<Integer> redCells = new ArrayList<>();
        List<Integer> blueCells = new ArrayList<>();
        for (int i = 0; i < extraData[0].length(); i += 2) {
            int cellId = zkArray.indexOf(extraData[0].charAt(i)) << 6;
            cellId += zkArray.indexOf(extraData[0].charAt(i + 1));
            redCells.add(cellId);
        }
        for (int i = 0; i < extraData[1].length(); i += 2) {
            int cellId = zkArray.indexOf(extraData[1].charAt(i)) << 6;
            cellId += zkArray.indexOf(extraData[1].charAt(i + 1));
            blueCells.add(cellId);
        }
        proxy.getDatabase().getMapsCollection().updateMap(proxyClient.getCurrentMap(), new Document().append("fightCells", new Document().append("red", redCells).append("blue", blueCells)));
        proxy.getDatabase().getProfilesCollection().incrementFightCells(proxyClient.getUsername());
        proxy.sendMessage("<b>" + proxyClient.getUsername() + "</b> a découvert de nouvelles cellules de combat ! <i>(MapID: " + proxyClient.getCurrentMap() + ")</i>");
        return true;
    }
}
