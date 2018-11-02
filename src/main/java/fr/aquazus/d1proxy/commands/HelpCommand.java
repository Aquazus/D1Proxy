package fr.aquazus.d1proxy.commands;

import fr.aquazus.d1proxy.network.ProxyClient;

public class HelpCommand implements Command {
    @Override
    public void execute(ProxyClient proxyClient, String args) {
        proxyClient.sendMessage("<b><u>Commandes :</u></b>\n" +
                "<b>.help</b> - Affiche la liste des commandes\n" +
                "<b>.about</b> - Affiche les informations sur le proxy\n" +
                "<b>.all</b> - Envoie un message aux joueurs connectés au proxy\n" +
                "<b>.mapinfo</b> - Affiche les informations sur la map actuelle\n" +
                "<b>.profile</b> - Affiche votre profil");
    }
}
