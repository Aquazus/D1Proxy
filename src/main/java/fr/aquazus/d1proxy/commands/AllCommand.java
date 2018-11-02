package fr.aquazus.d1proxy.commands;

import fr.aquazus.d1proxy.Proxy;
import fr.aquazus.d1proxy.network.ProxyClient;

public class AllCommand implements Command {

    private Proxy proxy;

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
        System.out.println("[" + proxyClient.getIp() + "] [.all] " + proxyClient.getUsername() + " : " + args);
    }
}
