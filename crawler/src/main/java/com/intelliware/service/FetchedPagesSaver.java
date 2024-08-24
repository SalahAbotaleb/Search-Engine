package com.intelliware.service;

import java.util.Queue;
import java.util.concurrent.Semaphore;
import com.intelliware.model.PageContent;

public class FetchedPagesSaver extends DbConsumer {

    public FetchedPagesSaver(Queue<PageContent> pagesToSave, Semaphore pagesToSaveCnt) {
        super(pagesToSave, pagesToSaveCnt);
    }

    protected void saveQueueFront() {
        submitJobToPool(() -> {
            PageContent front = (PageContent) ItemsToProcess.poll();
            repo.save(front);
        });
    }
}
