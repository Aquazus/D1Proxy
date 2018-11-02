package fr.aquazus.d1proxy.commands;

import com.mongodb.client.model.Filters;
import fr.aquazus.d1proxy.Proxy;
import fr.aquazus.d1proxy.network.ProxyClient;
import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ProfileCommand implements Command {

    private Proxy proxy;

    public ProfileCommand(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public void execute(ProxyClient proxyClient, String args) {
        Document profile = proxy.getDatabase().getProfilesCollection().getProfile(proxyClient.getUsername());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YY à HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        long mapsCount = proxy.getDatabase().getMapsCollection().countMaps(Filters.eq("discoverer", proxyClient.getUsername()));
        proxyClient.sendMessage("<b><u>Votre profil</u></b>\n" +
                "<b>Première connexion le</b> : " + sdf.format(new Date((long) profile.get("joined"))) + "\n" +
                "<b>Maps découvertes</b> : " + mapsCount + "\n" +
                "<b>Cellules de combat découvertes</b> : " + (profile.get("fightCells") == null ? "0" : profile.get("fightCells")));
    }
}
