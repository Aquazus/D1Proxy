package fr.aquazus.d1proxy.commands;

import fr.aquazus.d1proxy.Proxy;
import fr.aquazus.d1proxy.network.ProxyClient;
import lombok.Getter;
import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MapinfoCommand implements Command {

    @Getter
    private String description = "Affiche les informations sur la map actuelle";
    private Proxy proxy;
    private SimpleDateFormat dateFormat;

    public MapinfoCommand(Proxy proxy) {
        this.proxy = proxy;
        this.dateFormat = new SimpleDateFormat("dd/MM/YY à HH:mm:ss");
        this.dateFormat.setTimeZone(proxy.getConfiguration().getProxyTimeZone());
    }

    @Override
    public void execute(ProxyClient proxyClient, String args) {
        Document map = proxy.getDatabase().getMapsCollection().getMap(proxyClient.getCurrentMap());
        proxyClient.sendMessage("<b><u>Informations sur la map</u></b>\n" +
                "<b>ID</b> : " + proxyClient.getCurrentMap() + "\n" +
                "<b>Découverte par</b> : " + map.get("discoverer") + "\n" +
                "<b>Découverte le</b> : " + dateFormat.format(new Date((long) map.get("discovered"))));
    }
}
