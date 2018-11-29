package fr.aquazus.d1proxy.handlers;

import fr.aquazus.d1proxy.Proxy;
import fr.aquazus.d1proxy.network.ProxyClient;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.*;

import java.io.File;
import java.io.FileOutputStream;

@Slf4j
public class GDMHandler implements PacketHandler {

    private final Proxy proxy;

    public GDMHandler(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public boolean shouldForward(ProxyClient proxyClient, String packet, PacketDestination destination) {
        if (!proxy.getConfiguration().isProxySniffing()) return true;
        String[] extraData = packet.split("\\|");
        int mapId = Integer.parseInt(extraData[1]);
        String mapDate = extraData[2];
        String mapKey = extraData[3];
        proxyClient.setCurrentMap(mapId);
        if (!proxy.getDatabase().getMapsCollection().mapExists(mapId)) {
            proxy.getDatabase().getMapsCollection().insertNewMap(mapId, proxyClient.getUsername(), mapDate, mapKey);
            proxy.sendMessage("<b>" + proxyClient.getUsername() + "</b> a découvert une nouvelle map ! <i>(ID: " + mapId + ")</i>");
            try {
                downloadMapFile(mapId + "_" + mapDate + (mapKey.isBlank() ? ".swf" : "X.swf"));
            } catch (Exception ex) {
                log.error("An error occurred while downloading a map file.", ex);
            }
        }
        return true;
    }

    private void downloadMapFile(String fileName) throws Exception {
        AsyncHttpClient client = Dsl.asyncHttpClient();
        File directory = new File("maps");
        if (!directory.exists() && !directory.mkdir()) {
            throw new Exception("Creation of the maps folder failed");
        }
        FileOutputStream stream = new FileOutputStream("maps/" + fileName);
        String url = proxy.getConfiguration().getDofusMapsCdn();
        if (!url.endsWith("/")) url = url + "/";
        client.prepareGet(url + fileName).execute(new AsyncCompletionHandler<FileOutputStream>() {
            @Override
            public State onStatusReceived(HttpResponseStatus responseStatus) {
                if (responseStatus.getStatusCode() != 200) {
                    log.error("Response code " + responseStatus.getStatusCode() + " for map request " + fileName);
                    return State.ABORT;
                }
                return State.CONTINUE;
            }

            @Override
            public State onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
                stream.getChannel().write(bodyPart.getBodyByteBuffer());
                return State.CONTINUE;
            }

            @Override
            public FileOutputStream onCompleted(Response response) {
                try {
                    stream.close();
                } catch (Exception ex) {
                    log.error("An error occurred while downloading a map file.", ex);
                    return stream;
                }
                log.info("Downloaded map file " + fileName);
                return stream;
            }
        });
    }
}
