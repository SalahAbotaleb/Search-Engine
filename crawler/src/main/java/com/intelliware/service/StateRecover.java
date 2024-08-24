package com.intelliware.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.intelliware.model.UrlState;
import com.intelliware.repository.Repository;
import com.intelliware.repository.RepositoryImpl;

public class StateRecover {
    private Repository repository;

    public StateRecover() {
        this.repository = RepositoryImpl.getInstance();
    }

    public List<URL> getUrls() {
        List<UrlState> urls = repository.findAll(UrlState.class, "visited", false);
        if (urls == null) {
            return null;
        }
        List<URL> convertedUrls = urls.parallelStream().map((url) -> {
            try {
                return new URL(url.getUrl());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return null;
        }).toList();
        return convertedUrls;
    }
}
