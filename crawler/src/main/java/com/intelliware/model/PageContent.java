package com.intelliware.model;

import org.bson.types.ObjectId;

import dev.morphia.annotations.*;

@Entity("FetchedPages")
public class PageContent {
    @Id
    private ObjectId id;
    private String content;

    public PageContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int hashCode() {
        return content.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final PageContent other = (PageContent) obj;
        return this.getContent().equals(other.getContent());
    }

}
