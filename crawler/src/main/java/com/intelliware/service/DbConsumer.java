package com.intelliware.service;

import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import com.intelliware.repository.Repository;
import com.intelliware.repository.RepositoryImpl;

public abstract class DbConsumer extends Thread {
    protected Repository repo;
    protected Queue<? extends Object> ItemsToProcess;
    protected Semaphore ItemsToProcessCnt;
    private final int DB_THREADS_CNT = 1;
    private ExecutorService pool;

    public DbConsumer(Queue<? extends Object> ItemsToProcess, Semaphore ItemsToProcessCnt) {
        repo = RepositoryImpl.getInstance();
        this.ItemsToProcess = ItemsToProcess;
        this.ItemsToProcessCnt = ItemsToProcessCnt;
        this.pool = Executors.newFixedThreadPool(DB_THREADS_CNT);
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                ItemsToProcessCnt.acquire();
                saveQueueFront();
            } catch (InterruptedException e) {
                break;
            }
        }
        pool.shutdown();
    }

    protected void submitJobToPool(Runnable job) {
        pool.execute(job);
    }

    protected abstract void saveQueueFront();
}
