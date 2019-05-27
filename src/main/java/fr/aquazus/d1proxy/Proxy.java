package fr.aquazus.d1proxy;

import fr.aquazus.d1proxy.commands.*;
import fr.aquazus.d1proxy.database.ProxyDatabase;
import fr.aquazus.d1proxy.handlers.*;
import fr.aquazus.d1proxy.logging.UncaughtExceptionLogger;
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
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionLogger());
        Proxy.getInstance().init();
    }

    @Getter
    private final String version = "1.10.6-dev";
    @Getter
    private ProxyConfiguration configuration;
    @Getter
    private ProxyDatabase database;
    @Getter
    private ProxyPluginManager pluginManager;
    @Getter
    private Map<String, List<PacketHandler>> handlers;
    @Getter
    private Map<String, Command> commands;
    @Getter
    private List<ProxyClient> clients;
    @Getter
    private long startTime;

    private void init() {
        log.info("Initializing D1Proxy...");
        startTime = System.currentTimeMillis();
        clients = Collections.synchronizedList(new ArrayList<>());
        try {
            configuration = new ProxyConfiguration();
            configuration.read();
        } catch (Exception ex) {
            log.error("An error occurred while reading the configuration file. Aborting startup.", ex);
            System.exit(0);
        }
        if (!configuration.isMongoEnabled() && configuration.isProxySniffing()) {
            log.warn("Sniffing mode is enabled but mongo is disabled. Sniffing mode requires Mongo. Disabling sniffing mode.");
            configuration.setProxySniffing(false);
        }
        registerHandlers();
        registerCommands();
        if (configuration.isMongoEnabled()) {
            database = new ProxyDatabase(configuration.getMongoIp(), configuration.getMongoPort(), configuration.getMongoDatabase());
        }
        pluginManager = new ProxyPluginManager(this);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> pluginManager.stopPlugins()));
        pluginManager.loadPlugins();
        startServer();
    }

    private void registerHandlers() {
        log.info("Registering handlers...");
        handlers = Collections.synchronizedMap(new HashMap<>());
        addHandler("AXK", new AXKHandler()); //<-- Selected server address + client ticket
        addHandler("AYK", new AYKHandler()); //<-- Selected server address + client ticket
        addHandler("Im", new ImHandler(this)); //<-- Ingame message from lang files
        addHandler("BM", new BMHandler(this)); //--> Chat message
        addHandler("ASK", new ASKHandler(this)); //<-- Character name
        addHandler("GDM", new GDMHandler(this)); //<-- Map data
        addHandler("Ax", new AxHandler()); //--> Cache OK, request character list
        addHandler("GP", new GPHandler(this)); //<-- Fight cells & team id
        addHandler("GTS", new GTSHandler()); //<-- Game Turn Start
        addHandler("HG", new HGHandler()); //<-- Hello Game
        addHandler("PL", new PLHandler()); //<-- Group Leader
        addHandler("PV", new PVHandler()); //<-> Leave Group
        addHandler("GR1", new GR1Handler(this)); //<-> Character Ready
        addHandler("Gt", new GtHandler(this)); //<-- Fight created on map
        log.info(handlers.size() + " packets handled!");
    }

    private void registerCommands() {
        log.info("Registering commands...");
        commands = Collections.synchronizedMap(new HashMap<>());
        addCommand("help", new HelpCommand(this));
        addCommand("about", new AboutCommand(this));
        addCommand("all", new AllCommand(this));
        if (configuration.isProxySniffing()) addCommand("mapinfo", new MapinfoCommand(this));
        if (configuration.isMongoEnabled()) addCommand("profile", new ProfileCommand(this));
        addCommand("autoskip", new AutoskipCommand(this));
        addCommand("antiafk", new AntiafkCommand(this));
        addCommand("plugins", new PluginsCommand(this));
        addCommand("autoready", new AutoreadyCommand(this));
        addCommand("autojoin", new AutojoinCommand(this));
        if (ProxyConfiguration.proxyDebug) addCommand("send", new SendCommand(this));
        if (ProxyConfiguration.proxyDebug) addCommand("receive", new ReceiveCommand(this));
        log.info(commands.size() + " commands registered!");
    }

    private void startServer() {
        log.info("Starting proxy server...");
        Server server = new Server(1024);
        server.bind(configuration.getProxyIp(), configuration.getProxyPort());
        server.onConnect(client -> {
            String clientIp;
            try {
                clientIp = client.getChannel().getRemoteAddress().toString().substring(1).split(":")[0];
            } catch (Exception ex) {
                log.error("An error occurred while parsing an user IP", ex);
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

    @Synchronized("clients")
    public ProxyClient getClientByCharacterId(int characterId) {
        ProxyClient result = null;
        for (ProxyClient client : clients) {
            if (client.getCharacterId() == characterId) {
                result = client;
                break;
            }
        }
        return result;
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

    @SuppressWarnings("WeakerAccess")
    public void addCommand(String commandName, Command command) {
        if (!commands.containsKey(commandName)) {
            commands.put(commandName, command);
        }
    }
}
