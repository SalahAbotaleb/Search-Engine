package com.intelliware.service;

import java.util.Queue;
import java.util.concurrent.Semaphore;

import com.intelliware.model.UrlState;

public class VisitedUrlStateSaver extends DbConsumer {

    public VisitedUrlStateSaver(Queue<UrlState> urlsToRemove, Semaphore urlsToRemoveCnt) {
        super(urlsToRemove, urlsToRemoveCnt);
    }

    protected void saveQueueFront() {
        submitJobToPool(() -> {
            UrlState front = (UrlState) ItemsToProcess.poll();
            UrlState dbItem = repo.findOne(UrlState.class, "url", front.getUrl());
            if (dbItem == null) {
                repo.save(front);
                return;
            }
            dbItem.setVisited(true);
            repo.save(dbItem);
        });
    }
}
