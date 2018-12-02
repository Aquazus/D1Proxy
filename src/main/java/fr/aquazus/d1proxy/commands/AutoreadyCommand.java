package fr.aquazus.d1proxy.commands;

import fr.aquazus.d1proxy.Proxy;
import fr.aquazus.d1proxy.network.ProxyClient;
import lombok.Getter;

public class AutoreadyCommand implements Command {

    @Getter
    private String description = "Vos mules se mettent prêtes en même temps que vous";
    private final Proxy proxy;

    public AutoreadyCommand(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public void execute(ProxyClient proxyClient, String args) {
        if (args.isBlank()) {
            proxyClient.sendMessage("<b>Utilisation :</b> " + proxy.getConfiguration().getProxyPrefix() + "autoready on/off");
            return;
        }
        if (args.split(" ")[0].toLowerCase().equalsIgnoreCase("off")) {
            proxyClient.sendMessage(proxyClient.isAutoReadyEnabled() ? "Le mode autoready est désormais <b>désactivé</b>" : "Vous n'êtes pas en mode autoready");
            proxyClient.setAutoReadyEnabled(false);
        } else if (args.split(" ")[0].toLowerCase().equalsIgnoreCase("on")) {
            proxyClient.sendMessage(proxyClient.isAutoReadyEnabled() ? "Vous êtes déjà en mode autoready" : "Le mode autoready est désormais <b>activé</b>");
            proxyClient.setAutoReadyEnabled(true);
        } else {
            proxyClient.sendMessage("<b>Utilisation :</b> .autoready on/off");
        }
    }
}
