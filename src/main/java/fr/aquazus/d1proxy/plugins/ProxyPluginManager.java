package fr.aquazus.d1proxy.plugins;

import fr.aquazus.d1proxy.Proxy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class ProxyPluginManager {

    private final Proxy proxy;
    private final PluginManager manager;
    @Getter
    private final List<ProxyPlugin> plugins;

    public ProxyPluginManager(Proxy proxy) {
        log.info("Loading plugin system...");
        this.proxy = proxy;
        this.manager = new DefaultPluginManager();
        this.plugins = Collections.synchronizedList(new ArrayList<>());
    }

    public void loadPlugins() {
        log.info("Loading plugins...");
        manager.loadPlugins();
        manager.startPlugins();
        plugins.addAll(manager.getExtensions(ProxyPlugin.class));
        if (plugins.isEmpty()) {
            log.info("No plugins found");
            return;
        }
        log.info("Found " + plugins.size() + " plugins, enabling them...");
        plugins.forEach(plugin -> plugin.main(proxy));
    }

    public void stopPlugins() {
        log.info("Stopping plugins...");
        manager.stopPlugins();
    }
}
