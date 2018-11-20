package fr.aquazus.d1proxy.commands;

import com.mongodb.client.model.Filters;
import fr.aquazus.d1proxy.Proxy;
import fr.aquazus.d1proxy.network.ProxyClient;
import lombok.Getter;
import org.bson.Document;

import java.util.Date;

public class ProfileCommand implements Command {

    @Getter
    private final String description = "Affiche votre profil";
    private final Proxy proxy;

    public ProfileCommand(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public void execute(ProxyClient proxyClient, String args) {
        Document profile = proxy.getDatabase().getProfilesCollection().getProfile(proxyClient.getUsername());
        StringBuilder messageBuilder = new StringBuilder("<b><u>Votre profil</u></b>");
        messageBuilder.append("\n<b>Première connexion le</b> : ");
        messageBuilder.append(proxy.getConfiguration().getFullDateFormat().format(new Date((long) profile.get("joined"))));
        if (proxy.getConfiguration().isProxySniffing()) {
            long mapsCount = proxy.getDatabase().getMapsCollection().countMaps(Filters.eq("discoverer", proxyClient.getUsername()));
            messageBuilder.append("\n<b>Maps découvertes</b> : ");
            messageBuilder.append(mapsCount);
            messageBuilder.append("\n<b>Cellules de combat découvertes</b> : ");
            messageBuilder.append((profile.get("fightCells") == null ? "0" : profile.get("fightCells")));
        }
        proxyClient.sendMessage(messageBuilder.toString());
    }
}
