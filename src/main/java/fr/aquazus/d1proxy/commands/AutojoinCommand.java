package fr.aquazus.d1proxy.commands;

import fr.aquazus.d1proxy.network.ProxyClient;
import lombok.Getter;

public class AutojoinCommand implements Command {

    @Getter
    private String description = "Vos mules rejoignent vos combats automatiquement";

    @Override
    public void execute(ProxyClient proxyClient, String args) {
        if (args.isBlank()) {
            proxyClient.sendMessage("<b>Utilisation :</b> .autojoin on/off");
            return;
        }
        if (args.split(" ")[0].toLowerCase().equalsIgnoreCase("off")) {
            proxyClient.sendMessage(proxyClient.isAutoSkipEnabled() ? "Le mode autojoin est désormais <b>désactivé</b>" : "Vous n'êtes pas en mode autojoin");
            //TODO: Logic
        } else if (args.split(" ")[0].toLowerCase().equalsIgnoreCase("on")) {
            proxyClient.sendMessage(proxyClient.isAutoSkipEnabled() ? "Vous êtes déjà en mode autojoin" : "Le mode autojoin est désormais <b>activé</b>");
            //TODO: Logic
        } else {
            proxyClient.sendMessage("<b>Utilisation :</b> .autojoin on/off");
        }
    }
}
