package fr.aquazus.d1proxy.plugins;

import fr.aquazus.d1proxy.Proxy;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProxyPluginManager {

    private Proxy proxy;
    private PluginManager manager;
    private List<ProxyPlugin> plugins;

    public ProxyPluginManager(Proxy proxy) {
        System.out.println("Loading plugin system...");
        this.proxy = proxy;
        this.manager = new DefaultPluginManager();
        this.plugins = Collections.synchronizedList(new ArrayList<>());
    }

    public void loadPlugins() {
        System.out.println("Loading plugins...");
        manager.loadPlugins();
        manager.startPlugins();
        plugins.addAll(manager.getExtensions(ProxyPlugin.class));
        System.out.println("Found " + plugins.size() + " plugins, enabling them if needed...");
        plugins.forEach(plugin -> plugin.main(proxy));
    }

    public void stopPlugins() {
        System.out.println("Stopping plugins...");
        manager.stopPlugins();
    }
}
