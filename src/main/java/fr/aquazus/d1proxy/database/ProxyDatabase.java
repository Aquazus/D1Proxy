package fr.aquazus.d1proxy.database;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProxyDatabase {
    @Getter
    private final MapsCollection mapsCollection;
    @Getter
    private final ProfilesCollection profilesCollection;

    public ProxyDatabase(String ip, int port, String database) {
        log.info("Connecting to MongoDB...");
        MongoClient mongoClient = new MongoClient(ip, port);
        MongoDatabase mongoDatabase = mongoClient.getDatabase(database);
        this.mapsCollection = new MapsCollection(mongoDatabase.getCollection("maps"));
        this.profilesCollection = new ProfilesCollection(mongoDatabase.getCollection("profiles"));
    }
}
