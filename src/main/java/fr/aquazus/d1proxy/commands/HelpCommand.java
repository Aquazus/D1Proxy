package fr.aquazus.d1proxy.commands;

import fr.aquazus.d1proxy.Proxy;
import fr.aquazus.d1proxy.network.ProxyClient;
import lombok.Getter;

import java.util.Map;

public class HelpCommand implements Command {

    @Getter
    private String description = "Affiche la liste des commandes";
    private final Proxy proxy;

    public HelpCommand(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public void execute(ProxyClient proxyClient, String args) {
        StringBuilder helpBuilder = new StringBuilder("<b><u>Commandes :</u></b>");
        for (Map.Entry<String, Command> command : proxy.getCommands().entrySet()) {
            helpBuilder.append("\n<b>");
            helpBuilder.append(proxy.getConfiguration().getProxyPrefix());
            helpBuilder.append(command.getKey());
            helpBuilder.append("</b> - ");
            helpBuilder.append(command.getValue().getDescription());
        }
        proxyClient.sendMessage(helpBuilder.toString());
    }
}
