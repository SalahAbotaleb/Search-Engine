package com.intelliware.service;

import java.util.Queue;
import java.util.concurrent.Semaphore;

import com.intelliware.model.UrlState;

public class QueuedUrlStateSaver extends DbConsumer {

    public QueuedUrlStateSaver(Queue<UrlState> urlsToSave, Semaphore urlsToSaveCnt) {
        super(urlsToSave, urlsToSaveCnt);
    }

    protected void saveQueueFront() {
        submitJobToPool(() -> {
            UrlState front = (UrlState) ItemsToProcess.poll();
            UrlState dbItem = repo.findOne(UrlState.class, "url", front.getUrl());
            if (dbItem == null) {
                repo.save(front);
            }
        });
    }
}
