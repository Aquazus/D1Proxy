package fr.aquazus.d1proxy.commands;

import fr.aquazus.d1proxy.network.ProxyClient;

public interface Command {
    void execute(ProxyClient proxyClient, String args);
}
