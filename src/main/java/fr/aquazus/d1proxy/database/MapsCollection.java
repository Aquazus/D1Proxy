package fr.aquazus.d1proxy.database;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Objects;

import static com.mongodb.client.model.Filters.*;

public class MapsCollection {

    private final MongoCollection<Document> collection;

    MapsCollection(MongoCollection<Document> collection) {
        this.collection = collection;
    }

    public boolean mapExists(int mapId) {
        return (collection.find(eq("id", mapId)).first() != null);
    }

    public void insertNewMap(int mapId, String discoverer, String date, String key) {
        Document map = new Document();
        map.put("id", mapId);
        map.put("discoverer", discoverer);
        map.put("discovered", System.currentTimeMillis());
        map.put("date", date);
        map.put("key", key);
        collection.insertOne(map);
    }

    public Document getMap(int mapId) {
        FindIterable<Document> queryResult = collection.find(eq("id", mapId));
        return queryResult.first();
    }

    public boolean mapHasFightCells(int mapId) {
        return (collection.find(and(eq("id", mapId), exists("fightCells"))).first() != null);
    }

    public void updateMap(int mapId, Document updateQuery) {
        collection.updateOne(eq("id", mapId), new Document().append("$set", updateQuery));
    }

    public long countMaps(Bson filter) {
        return collection.countDocuments(filter);
    }

    public String getMapDate(int mapId) {
        return Objects.requireNonNull(collection.find(eq("id", mapId)).first()).getString("date");
    }
}
