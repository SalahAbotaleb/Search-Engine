package com.intelliware.repository;

import dev.morphia.query.filters.Filters;

import java.util.List;

import dev.morphia.Datastore;
import dev.morphia.query.Query;

public class RepositoryImpl implements Repository {
    private final Datastore datastore;
    private static RepositoryImpl repo = null;

    private RepositoryImpl() {
        datastore = DbConfig.getDataStore();
    }

    public static RepositoryImpl getInstance() {
        if (repo == null) {
            repo = new RepositoryImpl();
        }
        return repo;
    }

    @Override
    public <T> void save(T item) {
        datastore.save(item);
    }

    @Override
    public <T> void delete(T item) {
        datastore.delete(item);

    }

    @Override
    public <T> T findOne(Class<T> clazz, String field, String value) {
        Query<T> query = datastore.find(clazz).filter(Filters.eq(field, value));
        while (query.iterator().hasNext()) {
            return query.iterator().next();
        }
        return null;
    }

    @Override
    public <T, K extends Object> List<T> findAll(Class<T> clazz, String field, K value) {
        Query<T> query = datastore.find(clazz).filter(Filters.eq(field, value));
        if (!query.iterator().hasNext()) {
            return null;
        }
        return query.iterator().toList();
    }
}
