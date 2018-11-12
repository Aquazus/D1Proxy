package fr.aquazus.d1proxy.commands;

import com.mongodb.client.model.Filters;
import fr.aquazus.d1proxy.Proxy;
import fr.aquazus.d1proxy.network.ProxyClient;
import lombok.Getter;
import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ProfileCommand implements Command {

    @Getter
    private String description = "Affiche votre profil";
    private Proxy proxy;
    private SimpleDateFormat dateFormat;

    public ProfileCommand(Proxy proxy) {
        this.proxy = proxy;
        this.dateFormat = new SimpleDateFormat("dd/MM/YY à HH:mm:ss");
        this.dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
    }

    @Override
    public void execute(ProxyClient proxyClient, String args) {
        Document profile = proxy.getDatabase().getProfilesCollection().getProfile(proxyClient.getUsername());
        StringBuilder messageBuilder = new StringBuilder("<b><u>Votre profil</u></b>");
        messageBuilder.append("\n<b>Première connexion le</b> : " + dateFormat.format(new Date((long) profile.get("joined"))));
        if (proxy.isSniffing()) {
            long mapsCount = proxy.getDatabase().getMapsCollection().countMaps(Filters.eq("discoverer", proxyClient.getUsername()));
            messageBuilder.append("\n<b>Maps découvertes</b> : " + mapsCount);
            messageBuilder.append("\n<b>Cellules de combat découvertes</b> : " + (profile.get("fightCells") == null ? "0" : profile.get("fightCells")));
        }
        proxyClient.sendMessage(messageBuilder.toString());
    }
}
