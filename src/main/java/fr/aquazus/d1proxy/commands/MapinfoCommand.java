package fr.aquazus.d1proxy.commands;

import fr.aquazus.d1proxy.Proxy;
import fr.aquazus.d1proxy.network.ProxyClient;
import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MapinfoCommand implements Command {

    private Proxy proxy;

    public MapinfoCommand(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public void execute(ProxyClient proxyClient, String args) {
        Document map = proxy.getDatabase().getMapsCollection().getMap(proxyClient.getCurrentMap());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YY à HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        proxyClient.sendMessage("<b><u>Informations sur la map</u></b>\n" +
                "<b>ID</b> : " + proxyClient.getCurrentMap() + "\n" +
                "<b>Découverte par</b> : " + map.get("discoverer") + "\n" +
                "<b>Découverte le</b> : " + sdf.format(new Date((long) map.get("discovered"))));
    }
}
