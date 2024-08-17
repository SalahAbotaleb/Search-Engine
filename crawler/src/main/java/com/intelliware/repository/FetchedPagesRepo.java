package com.intelliware.repository;

import com.intelliware.model.PageContent;
import com.mongodb.client.MongoClients;

import dev.morphia.Datastore;
import dev.morphia.Morphia;

public class FetchedPagesRepo {
    private final String dbUri;
    private final String dbName = "searchEngine";
    private final Datastore datastore;
    private static FetchedPagesRepo repo = null;

    private FetchedPagesRepo() {
        dbUri = System.getenv("MONGODB_URI");
        datastore = Morphia.createDatastore(MongoClients.create(dbUri), dbName);
    }

    public static FetchedPagesRepo getInstance() {
        if (repo == null) {
            repo = new FetchedPagesRepo();
        }
        return repo;
    }

    public void save(PageContent pageContent) {
        datastore.save(pageContent);
    }
}
