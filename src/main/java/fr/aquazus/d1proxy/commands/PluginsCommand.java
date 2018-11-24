package fr.aquazus.d1proxy.commands;

import fr.aquazus.d1proxy.Proxy;
import fr.aquazus.d1proxy.network.ProxyClient;
import fr.aquazus.d1proxy.plugins.ProxyPlugin;
import lombok.Getter;

import java.util.Map;

public class PluginsCommand implements Command {

    @Getter
    private String description = "Affiche la liste des plugins chargés";
    private final Proxy proxy;

    public PluginsCommand(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public void execute(ProxyClient proxyClient, String args) {
        StringBuilder pluginsBuilder = new StringBuilder("<b><u>Plugins chargés :</u></b>");
        if (proxy.getPluginManager().getPlugins().isEmpty()) {
            pluginsBuilder.append("\nAucun");
        }
        for (ProxyPlugin plugins : proxy.getPluginManager().getPlugins()) {
            pluginsBuilder.append("\n<b>");
            pluginsBuilder.append(plugins.getPrettyName());
            pluginsBuilder.append("</b> ");
            pluginsBuilder.append(plugins.getVersion());
        }
        proxyClient.sendMessage(pluginsBuilder.toString());
    }
}
