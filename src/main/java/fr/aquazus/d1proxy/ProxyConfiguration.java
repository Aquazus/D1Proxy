package fr.aquazus.d1proxy;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.TimeZone;

@Slf4j
public @Data class ProxyConfiguration {

    public static boolean proxyDebug;
    protected String proxyIp;
    protected int proxyPort;
    protected boolean proxySniffing;
    protected TimeZone proxyTimeZone;
    protected SimpleDateFormat fullDateFormat;

    protected String dofusIp;
    protected int dofusPort;

    protected boolean mongoEnabled;
    protected String mongoIp;
    protected int mongoPort;
    protected String mongoDatabase;

    void read() throws IOException, NumberFormatException {
        log.info("Reading d1proxy.properties...");
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream("d1proxy.properties")) {
            properties.load(fileInputStream);
            proxyDebug = Boolean.parseBoolean(properties.getProperty("proxy.debug"));
            this.proxyIp = properties.getProperty("proxy.ip");
            this.proxyPort = Integer.parseInt(properties.getProperty("proxy.port"));
            this.proxySniffing = Boolean.parseBoolean(properties.getProperty("proxy.sniffing"));
            this.proxyTimeZone = TimeZone.getTimeZone(properties.getProperty("proxy.timezone"));
            this.fullDateFormat = new SimpleDateFormat("dd/MM/YY à HH:mm:ss");
            this.fullDateFormat.setTimeZone(this.proxyTimeZone);
            this.dofusIp = properties.getProperty("dofus.ip");
            this.dofusPort = Integer.parseInt(properties.getProperty("dofus.port"));
            this.mongoEnabled = Boolean.parseBoolean(properties.getProperty("mongo.enabled"));
            this.mongoIp = properties.getProperty("mongo.ip");
            this.mongoPort = Integer.parseInt(properties.getProperty("mongo.port"));
            this.mongoDatabase = properties.getProperty("mongo.database");
        }
    }
}
