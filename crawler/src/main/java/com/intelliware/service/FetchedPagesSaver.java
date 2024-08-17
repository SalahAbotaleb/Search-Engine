package com.intelliware.service;

import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import com.intelliware.model.PageContent;
import com.intelliware.repository.FetchedPagesRepo;

public class FetchedPagesSaver extends Thread {
    private FetchedPagesRepo repo;
    private Queue<PageContent> pagesToSave;
    private Semaphore pagesToSaveCnt;
    private final int DB_THREADS_CNT = 1;
    ExecutorService pool;

    public FetchedPagesSaver(Queue<PageContent> pagesToSave, Semaphore pagesToSaveCnt) {
        repo = FetchedPagesRepo.getInstance();
        this.pagesToSave = pagesToSave;
        this.pagesToSaveCnt = pagesToSaveCnt;
        this.pool = Executors.newFixedThreadPool(DB_THREADS_CNT);
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                pagesToSaveCnt.acquire();
                saveQueueFront();
            } catch (InterruptedException e) {
                break;
            }
        }
        pool.shutdown();
    }

    private void saveQueueFront() {
        pool.execute(() -> {
            PageContent front = pagesToSave.poll();
            repo.save(front);
        });
    }
}
