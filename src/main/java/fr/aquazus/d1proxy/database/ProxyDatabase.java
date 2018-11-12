package fr.aquazus.d1proxy.database;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;

public class ProxyDatabase {
    @Getter
    private MapsCollection mapsCollection;
    @Getter
    private ProfilesCollection profilesCollection;

    public ProxyDatabase(String ip, int port, String database) {
        System.out.println("Connecting to MongoDB...");
        MongoClient mongoClient = new MongoClient(ip, port);
        MongoDatabase mongoDatabase = mongoClient.getDatabase(database);
        this.mapsCollection = new MapsCollection(mongoDatabase.getCollection("maps"));
        this.profilesCollection = new ProfilesCollection(mongoDatabase.getCollection("profiles"));
    }
}
