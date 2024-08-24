package com.intelliware.repository;

import java.util.List;

public interface Repository {
    public <T> void save(T item);

    public <T> void delete(T item);

    public <T> T findOne(Class<T> clazz, String field, String value);

    public <T, K extends Object> List<T> findAll(Class<T> clazz, String field, K value);
}
