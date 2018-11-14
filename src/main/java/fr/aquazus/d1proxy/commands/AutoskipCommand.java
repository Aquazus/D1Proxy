package fr.aquazus.d1proxy.commands;

import fr.aquazus.d1proxy.network.ProxyClient;
import lombok.Getter;

public class AutoskipCommand implements Command {

    @Getter
    private String description = "Fait passer vos tours automatiquement";

    @Override
    public void execute(ProxyClient proxyClient, String args) {
        if (args.isBlank()) {
            proxyClient.sendMessage("<b>Utilisation :</b> .autoskip on/off");
            return;
        }
        if (args.split(" ")[0].toLowerCase().equalsIgnoreCase("off")) {
            proxyClient.sendMessage(proxyClient.isAutoSkip() ? "Le mode autoskip est désormais <b>désactivé</b>" : "Vous n'êtes pas en mode autoskip");
            proxyClient.setAutoSkip(false);
        } else if (args.split(" ")[0].toLowerCase().equalsIgnoreCase("on")) {
            proxyClient.sendMessage(proxyClient.isAutoSkip() ? "Vous êtes déjà en mode autoskip" : "Le mode autoskip est désormais <b>activé</b>");
            proxyClient.setAutoSkip(true);
        } else {
            proxyClient.sendMessage("<b>Utilisation :</b> .autoskip on/off");
        }
    }
}
