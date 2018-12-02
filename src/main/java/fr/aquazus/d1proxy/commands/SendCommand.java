package fr.aquazus.d1proxy.commands;

import fr.aquazus.d1proxy.network.ProxyClient;
import lombok.Getter;
import simplenet.packet.Packet;

public class SendCommand implements Command {

    @Getter
    private final String description = "[DEBUG] Envoie un packet";

    @Override
    public void execute(ProxyClient proxyClient, String args) {
        if (args.isBlank()) {
            proxyClient.sendMessage("<b>Utilisation</b> : .send [packet]");
            return;
        }
        Packet.builder().putBytes(args.getBytes()).putByte(10).putByte(0).writeAndFlush(proxyClient.getServer());
    }
}
