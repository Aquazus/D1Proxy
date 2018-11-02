package fr.aquazus.d1proxy.handlers;

import fr.aquazus.d1proxy.Proxy;
import fr.aquazus.d1proxy.network.ProxyClient;
import simplenet.packet.Packet;

public class BMHandler implements PacketHandler {

    private Proxy proxy;

    public BMHandler(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public boolean shouldForward(ProxyClient proxyClient, String packet) {
        String message = packet.split("\\|")[1];
        if (message.startsWith(".")) {
            System.out.println("[" + proxyClient.getIp() + "] Executing command " + message);
            Packet.builder().putBytes("BN".getBytes()).putByte(0).writeAndFlush(proxyClient.getClient());
            if (!proxyClient.executeCommand(message.substring(1))) {
                proxyClient.sendMessage("Commande introuvable. Tapez <b>.help</b> pour obtenir la liste des commandes.");
            }
            return false;
        }
        System.out.println("[" + proxyClient.getIp() + "] " + proxyClient.getUsername() + " : " + message);
        return true;
    }
}
