package fr.aquazus.d1proxy.commands;

import fr.aquazus.d1proxy.Proxy;
import fr.aquazus.d1proxy.network.ProxyClient;
import lombok.Getter;
import org.bson.Document;

import java.util.Date;

public class MapinfoCommand implements Command {

    @Getter
    private final String description = "Affiche les informations sur la map actuelle";
    private final Proxy proxy;

    public MapinfoCommand(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public void execute(ProxyClient proxyClient, String args) {
        Document map = proxy.getDatabase().getMapsCollection().getMap(proxyClient.getCurrentMap());
        proxyClient.sendMessage("<b><u>Informations sur la map</u></b>\n" +
                "<b>ID</b> : " + proxyClient.getCurrentMap() + "\n" +
                "<b>Découverte par</b> : " + map.get("discoverer") + "\n" +
                "<b>Découverte le</b> : " + proxy.getConfiguration().getFullDateFormat().format(new Date((long) map.get("discovered"))));
    }
}
