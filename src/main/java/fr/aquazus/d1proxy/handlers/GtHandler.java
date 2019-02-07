package fr.aquazus.d1proxy.handlers;

import fr.aquazus.d1proxy.Proxy;
import fr.aquazus.d1proxy.network.ProxyClient;
import lombok.extern.slf4j.Slf4j;
import simplenet.packet.Packet;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
public class GtHandler implements PacketHandler {

    private final Proxy proxy;
    private ExecutorService scheduler;

    public GtHandler(Proxy proxy) {
        this.proxy = proxy;
        this.scheduler = Executors.newCachedThreadPool();
    }

    @Override
    public boolean shouldForward(ProxyClient proxyClient, String packet, PacketDestination destination) {
        if (destination != PacketDestination.CLIENT || proxyClient.getGroupLeader() == 0) {
            return true;
        }
        String[] extraData = packet.substring(2).split("\\|");
        int id = Integer.parseInt(extraData[0]);
        ProxyClient client = proxy.getClientByCharacterId(id);
        if (client != null && client.isAutoJoinEnabled() && client.getCharacterId() != proxyClient.getCharacterId() && client.getGroupLeader() == proxyClient.getGroupLeader() && client.getIp().equals(proxyClient.getIp()) && packet.contains(client.getUsername())) {
            String joinPacket = "GA903" + id + ";" + id;
            Executors.newSingleThreadScheduledExecutor().schedule(() -> {
                Packet.builder().putBytes(joinPacket.getBytes(StandardCharsets.UTF_8)).putByte(10).putByte(0).writeAndFlush(proxyClient.getServer());
            }, ThreadLocalRandom.current().nextInt(1000, 4000), TimeUnit.MILLISECONDS);
        }
        return true;
    }
}
