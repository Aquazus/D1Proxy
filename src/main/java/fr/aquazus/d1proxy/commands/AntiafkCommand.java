package fr.aquazus.d1proxy.commands;

import fr.aquazus.d1proxy.Proxy;
import fr.aquazus.d1proxy.network.ProxyClient;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import simplenet.packet.Packet;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AntiafkCommand implements Command, Runnable {

    @Getter
    private String description = "Empêche le serveur de vous kick pour afk";
    private Proxy proxy;
    private ScheduledExecutorService scheduler;

    public AntiafkCommand(Proxy proxy) {
        this.proxy = proxy;
        log.debug("Starting the Antiafk scheduler...");
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this, 5, 5, TimeUnit.MINUTES);
    }

    @Override
    public void execute(ProxyClient proxyClient, String args) {
        if (args.isBlank()) {
            proxyClient.sendMessage("<b>Utilisation :</b> " + proxy.getConfiguration().getProxyPrefix() + "antiafk on/off");
            return;
        }
        if (args.split(" ")[0].toLowerCase().equalsIgnoreCase("off")) {
            proxyClient.sendMessage(proxyClient.isAntiAfkEnabled() ? "L'antiafk est désormais <b>désactivé</b>" : "L'antiafk n'est pas activé");
            proxyClient.setAntiAfkEnabled(false);
        } else if (args.split(" ")[0].toLowerCase().equalsIgnoreCase("on")) {
            proxyClient.sendMessage(proxyClient.isAntiAfkEnabled() ? "L'antiafk est déjà activé" : "L'antiafk est désormais <b>activé</b>");
            proxyClient.setAntiAfkEnabled(true);
        } else {
            proxyClient.sendMessage("<b>Utilisation :</b> " + proxy.getConfiguration().getProxyPrefix() + "antiafk on/off");
        }
    }

    @Override
    public void run() {
        log.debug("Running the Antiafk task...");
        Packet ping = Packet.builder().putBytes("QL".getBytes()).putByte(10).putByte(0);
        for (ProxyClient clients : proxy.getClients()) {
            if (clients.isAntiAfkEnabled()) {
                ping.writeAndFlush(clients.getServer());
            }
        }
    }
}
