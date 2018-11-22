package fr.aquazus.d1proxy;

import fr.aquazus.d1proxy.commands.*;
import fr.aquazus.d1proxy.database.ProxyDatabase;
import fr.aquazus.d1proxy.handlers.*;
import fr.aquazus.d1proxy.network.ProxyCipher;
import fr.aquazus.d1proxy.network.ProxyClient;
import fr.aquazus.d1proxy.network.ProxyClientState;
import fr.aquazus.d1proxy.plugins.ProxyPluginManager;
import lombok.Getter;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import simplenet.Server;

import java.util.*;

@Slf4j
public class Proxy {

    private static Proxy instance = null;

    private static Proxy getInstance() {
        if (instance == null) instance = new Proxy();
        return instance;
    }

    public static void main(String[] args) {
        Proxy.getInstance().init();
    }

    @Getter
    private final String version = "1.7.1-dev";
    @Getter
    private ProxyConfiguration configuration;
    @Getter
    private ProxyCipher proxyCipher;
    @Getter
    private ProxyDatabase database;
    @Getter
    private Map<String, List<PacketHandler>> handlers;
    @Getter
    private Map<String, Command> commands;
    @Getter
    private List<ProxyClient> clients;
    @Getter
    private Map<String, String> exchangeCache; //TODO: Maybe improve the exchange cache system...
    private long startTime;
    private ProxyPluginManager pluginManager;

    private void init() {
        log.info("Initializing D1Proxy...");
        startTime = System.currentTimeMillis();
        clients = Collections.synchronizedList(new ArrayList<>());
        exchangeCache = Collections.synchronizedMap(new HashMap<>());
        try {
            configuration = new ProxyConfiguration();
            configuration.read();
        } catch (Exception ex) {
            log.error("An error occurred while reading the configuration file. Aborting startup.");
            ex.printStackTrace();
            System.exit(0);
        }
        proxyCipher = new ProxyCipher();
        registerHandlers();
        registerCommands();
        database = new ProxyDatabase(configuration.getMongoIp(), configuration.getMongoPort(), configuration.getMongoDatabase());
        pluginManager = new ProxyPluginManager(this);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> pluginManager.stopPlugins()));
        pluginManager.loadPlugins();
        startServer();
    }

    private void registerHandlers() {
        log.info("Registering handlers...");
        handlers = Collections.synchronizedMap(new HashMap<>());
        addHandler("AXK", new AXKHandler(this)); //<-- Selected server address + client ticket
        addHandler("Im", new ImHandler(this)); //<-- Ingame message from lang files
        addHandler("BM", new BMHandler()); //--> Chat message
        addHandler("ASK", new ASKHandler(this)); //<-- Character name
        addHandler("GDM", new GDMHandler(this)); //<-- Map data
        addHandler("Ax", new AxHandler()); //--> Cache OK, request character list
        addHandler("GP", new GPHandler(this)); //<-- Fight cells & team id
        addHandler("GTS", new GTSHandler()); //<-- Game Turn Start
        log.info(handlers.size() + " packets handled!");
    }

    private void registerCommands() {
        log.info("Registering commands...");
        commands = Collections.synchronizedMap(new HashMap<>());
        commands.put("help", new HelpCommand(this));
        commands.put("about", new AboutCommand(this));
        commands.put("all", new AllCommand(this));
        if (configuration.isProxySniffing()) commands.put("mapinfo", new MapinfoCommand(this));
        commands.put("profile", new ProfileCommand(this));
        commands.put("autoskip", new AutoskipCommand());
        commands.put("antiafk", new AntiafkCommand(this));
        log.info(commands.size() + " commands registered!");
    }

    private void startServer() {
        log.info("Starting proxy server...");
        Server server = new Server(configuration.getProxyBuffer());
        server.bind(configuration.getProxyIp(), configuration.getProxyPort());
        server.onConnect(client -> {
            String clientIp;
            try {
                clientIp = client.getChannel().getRemoteAddress().toString().substring(1).split(":")[0];
            } catch (Exception ex) {
                ex.printStackTrace();
                if (client.getChannel().isOpen()) client.close();
                return;
            }
            log.info("[" + clientIp + "] connected!");
            this.clients.add(new ProxyClient(this, client, clientIp));
        });
    }

    public long getOnlineCount() {
        return clients.stream().map(ProxyClient::getState).filter(s -> s == ProxyClientState.INGAME).count();
    }

    public String getUptime() {
        long uptime = System.currentTimeMillis() - startTime;
        int d = (int) (uptime / (1000 * 3600 * 24));
        uptime %= (1000 * 3600 * 24);
        int h = (int) (uptime / (1000 * 3600));
        uptime %= (1000 * 3600);
        int m = (int) (uptime / (1000 * 60));
        uptime %= (1000 * 60);
        int s = (int) (uptime / (1000));
        return (d > 0 ? d + "j " : "") + (h > 0 ? h + "h " : "") + (m > 0 ? m + "m " : "") + s + "s";
    }

    @Synchronized("clients")
    public void sendMessage(String message) {
        clients.forEach(client -> client.sendMessage(message));
    }

    @SuppressWarnings("WeakerAccess")
    public void addHandler(String packetId, PacketHandler handler) {
        if (handlers.containsKey(packetId)) {
            handlers.get(packetId).add(handler);
        } else {
            handlers.put(packetId, Collections.synchronizedList(new ArrayList<>(List.of(handler))));
        }
    }
}
