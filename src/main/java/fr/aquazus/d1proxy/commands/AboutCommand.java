package fr.aquazus.d1proxy.commands;

import fr.aquazus.d1proxy.Proxy;
import fr.aquazus.d1proxy.network.ProxyClient;
import lombok.Getter;

public class AboutCommand implements Command {

    @Getter
    private final String description = "Affiche les informations sur le proxy";
    private final Proxy proxy;

    public AboutCommand(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public void execute(ProxyClient proxyClient, String args) {
        proxyClient.sendMessage("<b><u>D1Proxy " + proxy.getVersion() + " par Aquazus</u></b>\n" +
                "<b>Joueurs connectés</b> : " + proxy.getOnlineCount() + "\n" +
                "<b>Uptime</b> : " + proxy.getUptime() + "\n" +
                "<b>Plugins chargés</b> : " + proxy.getPluginManager().getPlugins().size() + "\n" +
                "<b>Credits :</b> Jacob");
    }
}
