package fr.aquazus.d1proxy;

import lombok.Data;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public @Data class ProxyConfiguration {

    protected boolean proxyDebug;
    protected String proxyIp;
    protected int proxyPort;
    protected int proxyBuffer;
    protected boolean proxySniffing;

    protected String dofusIp;
    protected int dofusPort;

    protected String mongoIp;
    protected int mongoPort;
    protected String mongoDatabase;

    void read() throws IOException, NumberFormatException {
        System.out.println("Reading d1proxy.properties...");
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream("d1proxy.properties")) {
            properties.load(fileInputStream);
            this.proxyDebug = Boolean.parseBoolean(properties.getProperty("proxy.debug"));
            this.proxyIp = properties.getProperty("proxy.ip");
            this.proxyPort = Integer.parseInt(properties.getProperty("proxy.port"));
            this.proxyBuffer = Integer.parseInt(properties.getProperty("proxy.buffer"));
            this.proxySniffing = Boolean.parseBoolean(properties.getProperty("proxy.sniffing"));
            this.dofusIp = properties.getProperty("dofus.ip");
            this.dofusPort = Integer.parseInt(properties.getProperty("dofus.port"));
            this.mongoIp = properties.getProperty("mongo.ip");
            this.mongoPort = Integer.parseInt(properties.getProperty("mongo.port"));
            this.mongoDatabase = properties.getProperty("mongo.database");
        }
    }
}
