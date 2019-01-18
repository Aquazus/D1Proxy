package fr.aquazus.d1proxy.commands;

import fr.aquazus.d1proxy.Proxy;
import fr.aquazus.d1proxy.network.ProxyClient;
import lombok.Getter;
import simplenet.packet.Packet;

import java.nio.charset.StandardCharsets;

public class ReceiveCommand implements Command {

    @Getter
    private final String description = "[DEBUG] Simule la réception d'un packet";
    private final Proxy proxy;

    public ReceiveCommand(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public void execute(ProxyClient proxyClient, String args) {
        if (args.isBlank()) {
            proxyClient.sendMessage("<b>Utilisation</b> : " + proxy.getConfiguration().getProxyPrefix() + "receive [packet]");
            return;
        }
        Packet.builder().putBytes(args.getBytes(StandardCharsets.UTF_8)).putByte(0).writeAndFlush(proxyClient.getClient());
    }
}
