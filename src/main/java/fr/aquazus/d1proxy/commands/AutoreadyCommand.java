package fr.aquazus.d1proxy.commands;

import fr.aquazus.d1proxy.network.ProxyClient;
import lombok.Getter;

public class AutoreadyCommand implements Command {

    @Getter
    private String description = "Vos mules se mettent prêtes en même temps que vous";

    @Override
    public void execute(ProxyClient proxyClient, String args) {
        if (args.isBlank()) {
            proxyClient.sendMessage("<b>Utilisation :</b> .autoready on/off");
            return;
        }
        if (args.split(" ")[0].toLowerCase().equalsIgnoreCase("off")) {
            proxyClient.sendMessage(proxyClient.isAutoSkipEnabled() ? "Le mode autoready est désormais <b>désactivé</b>" : "Vous n'êtes pas en mode autoready");
            //TODO: Logic
        } else if (args.split(" ")[0].toLowerCase().equalsIgnoreCase("on")) {
            proxyClient.sendMessage(proxyClient.isAutoSkipEnabled() ? "Vous êtes déjà en mode autoready" : "Le mode autoready est désormais <b>activé</b>");
            //TODO: Logic
        } else {
            proxyClient.sendMessage("<b>Utilisation :</b> .autoready on/off");
        }
    }
}
