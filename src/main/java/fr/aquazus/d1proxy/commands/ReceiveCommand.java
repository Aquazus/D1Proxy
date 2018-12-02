package fr.aquazus.d1proxy.commands;

import fr.aquazus.d1proxy.network.ProxyClient;
import lombok.Getter;
import simplenet.packet.Packet;

public class ReceiveCommand implements Command {

    @Getter
    private final String description = "[DEBUG] Simule la réception d'un packet";

    @Override
    public void execute(ProxyClient proxyClient, String args) {
        if (args.isBlank()) {
            proxyClient.sendMessage("<b>Utilisation</b> : .receive [packet]");
            return;
        }
        Packet.builder().putBytes(args.getBytes()).putByte(0).writeAndFlush(proxyClient.getClient());
    }
}
