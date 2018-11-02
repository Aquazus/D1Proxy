package fr.aquazus.d1proxy;

import fr.aquazus.d1proxy.commands.*;
import fr.aquazus.d1proxy.database.ProxyDatabase;
import fr.aquazus.d1proxy.handlers.*;
import fr.aquazus.d1proxy.network.ProxyClient;
import fr.aquazus.d1proxy.network.ProxyClientState;
import lombok.Getter;
import lombok.Synchronized;
import simplenet.Server;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Proxy {

    private static Proxy instance = null;

    public static Proxy getInstance() {
        if (instance == null) instance = new Proxy();
        return instance;
    }

    public static void main(String[] args) {
        Proxy.getInstance().init();
    }

    @Getter
    private String version = "1.3.1";
    @Getter
    private ProxyConfiguration configuration;
    @Getter
    private ProxyDatabase database;
    @Getter
    private boolean debug;
    @Getter
    private HashMap<String, PacketHandler> handlers;
    @Getter
    private HashMap<String, Command> commands;
    @Getter
    private List<ProxyClient> clients;
    @Getter
    private Map<String, String> exchangeCache; //TODO: Maybe improve the exchange cache system...
    private long startTime;

    private void init() {
        System.out.println("Initializing D1Proxy...");
        startTime = System.currentTimeMillis();
        clients = Collections.synchronizedList(new ArrayList<>());
        exchangeCache = Collections.synchronizedMap(new HashMap<>());
        System.out.println("Registering handlers...");
        registerHandlers();
        System.out.println("Registering commands...");
        registerCommands();
        System.out.println(handlers.size() + " handlers registered!");
        try {
            configuration = new ProxyConfiguration();
            configuration.readFile("d1proxy.properties");
            debug = configuration.isProxyDebug();
        } catch (Exception ex) {
            System.out.println("An error occurred while reading the configuration file. Aborting startup.");
            ex.printStackTrace();
            System.exit(0);
        }
        System.out.println("Connecting to MongoDB...");
        if (debug) Logger.getLogger("org.mongodb.driver").setLevel(Level.ALL);
        database = new ProxyDatabase(configuration.getMongoIp(), configuration.getMongoPort(), configuration.getMongoDatabase());
        System.out.println("Starting proxy server...");
        startServer();
    }

    private void registerHandlers() {
        handlers = new HashMap<>();
        handlers.put("AXK", new AXKHandler(this)); //<-- Selected server address + client ticket
        handlers.put("Im", new ImHandler(this)); //<-- Ingame message from lang files
        handlers.put("BM", new BMHandler(this)); //--> Chat message
        handlers.put("ASK", new ASKHandler(this)); //<-- Character name
        handlers.put("GDM", new GDMHandler(this)); //<-- Map data
        handlers.put("Ax", new AxHandler()); //--> Cache OK, request character list
        handlers.put("GP", new GPHandler(this)); //<-- Fight cells & team id
    }

    private void registerCommands() {
        commands = new HashMap<>();
        commands.put("help", new HelpCommand());
        commands.put("about", new AboutCommand(this));
        commands.put("all", new AllCommand(this));
        commands.put("mapinfo", new MapinfoCommand(this));
        commands.put("profile", new ProfileCommand(this));
    }

    private void startServer() {
        Server server = new Server(65536);
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
            System.out.println("[" + clientIp + "] connected!");
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
}
