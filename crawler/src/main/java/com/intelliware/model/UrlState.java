package com.intelliware.model;

import org.bson.types.ObjectId;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

@Entity("QueuedUrls")
public class UrlState {
    @Id
    private ObjectId id;
    private String url;
    private Boolean visited = false;

    public UrlState() {
    }

    public UrlState(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getVisited() {
        return visited;
    }

    public void setVisited(Boolean visited) {
        this.visited = visited;
    }

    @Override
    public String toString() {
        return url;
    }
}
