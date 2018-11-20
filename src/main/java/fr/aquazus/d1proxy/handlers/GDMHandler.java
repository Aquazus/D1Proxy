package fr.aquazus.d1proxy.handlers;

import fr.aquazus.d1proxy.Proxy;
import fr.aquazus.d1proxy.network.ProxyClient;
import org.asynchttpclient.*;

import java.io.File;
import java.io.FileOutputStream;

public class GDMHandler implements PacketHandler {

    private Proxy proxy;

    public GDMHandler(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public boolean shouldForward(ProxyClient proxyClient, String packet) {
        if (!proxy.isSniffing()) return true;
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
                System.out.println("An error occurred while downloading a map file.");
                ex.printStackTrace();
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
        client.prepareGet("http://staticns.ankama.com/dofus/gamedata/dofus/maps/" + fileName).execute(new AsyncCompletionHandler<FileOutputStream>() {
            @Override
            public State onStatusReceived(HttpResponseStatus responseStatus) {
                if (responseStatus.getStatusCode() != 200) {
                    System.out.println("Response code " + responseStatus.getStatusCode() + " for map request " + fileName);
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
                    System.out.println("An error occurred while downloading a map file.");
                    ex.printStackTrace();
                    return stream;
                }
                System.out.println("Downloaded map file " + fileName);
                return stream;
            }
        });
    }
}
