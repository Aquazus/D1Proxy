package fr.aquazus.d1proxy.commands;

import fr.aquazus.d1proxy.Proxy;
import fr.aquazus.d1proxy.network.ProxyClient;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AllCommand implements Command {

    @Getter
    private final String description = "Envoie un message aux joueurs connectés au proxy";
    private final Proxy proxy;

    public AllCommand(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public void execute(ProxyClient proxyClient, String args) {
        if (args.isBlank()) {
            proxyClient.sendMessage("<b>Utilisation</b> : .all [message]");
            return;
        }
        proxy.sendMessage("<b>" + proxyClient.getUsername() + "</b> : " + args);
        proxyClient.log("[.all] " + args);
    }
}
