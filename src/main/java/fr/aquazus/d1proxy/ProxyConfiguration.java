package fr.aquazus.d1proxy;

import lombok.Data;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public @Data class ProxyConfiguration {

    private boolean proxyDebug;
    private String proxyIp;
    private int proxyPort;

    private String dofusIp;
    private int dofusPort;

    private String mongoIp;
    private int mongoPort;
    private String mongoDatabase;

    public void readFile(String fileName) throws IOException, NumberFormatException {
        System.out.println("Reading " + fileName + "...");
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(fileName)) {
            properties.load(fileInputStream);
            this.proxyDebug = Boolean.parseBoolean(properties.getProperty("proxy.debug"));
            this.proxyIp = properties.getProperty("proxy.ip");
            this.proxyPort = Integer.parseInt(properties.getProperty("proxy.port"));
            this.dofusIp = properties.getProperty("dofus.ip");
            this.dofusPort = Integer.parseInt(properties.getProperty("dofus.port"));
            this.mongoIp = properties.getProperty("mongo.ip");
            this.mongoPort = Integer.parseInt(properties.getProperty("mongo.port"));
            this.mongoDatabase = properties.getProperty("mongo.database");
        }
    }
}
