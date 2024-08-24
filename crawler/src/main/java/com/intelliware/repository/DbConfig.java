package com.intelliware.repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import dev.morphia.Datastore;
import dev.morphia.Morphia;

public class DbConfig {
    private static DbConfig db = null;
    private final String dbUri;
    private final String dbName = "searchEngine";
    private final Datastore datastore;

    private DbConfig() {
        dbUri = System.getenv("MONGODB_URI");
        MongoClient client = MongoClients.create(dbUri);
        datastore = Morphia.createDatastore(client, dbName);
    }

    public static Datastore getDataStore() {
        if (db == null) {
            db = new DbConfig();
        }
        return db.datastore;
    }

}
