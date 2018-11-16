package fr.aquazus.d1proxy.commands;

import fr.aquazus.d1proxy.network.ProxyClient;
import lombok.Getter;

public class MasterCommand implements Command {

    @Getter
    private String description = "Réplique les actions d'un de vos personnage";

    @Override
    public void execute(ProxyClient proxyClient, String args) {

    }
}
