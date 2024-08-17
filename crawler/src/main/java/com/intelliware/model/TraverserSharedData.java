package com.intelliware.model;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class TraverserSharedData {
    private final AtomicInteger remainingTraverses;
    private final ConcurrentHashMap<Integer, Boolean> visitedPages;
    private final Semaphore pagesToSaveCnt;
    private final Queue<PageContent> pagesToSave;

    public TraverserSharedData(int maxTraverses) {
        remainingTraverses = new AtomicInteger(maxTraverses);
        visitedPages = new ConcurrentHashMap<>();
        pagesToSaveCnt = new Semaphore(0);
        pagesToSave = new ConcurrentLinkedQueue<>();
    }

    public AtomicInteger getRemainingTraverses() {
        return remainingTraverses;
    }

    public ConcurrentHashMap<Integer, Boolean> getVisitedPages() {
        return visitedPages;
    }

    public Semaphore getPagesToSaveCnt() {
        return pagesToSaveCnt;
    }

    public Queue<PageContent> getPagesToSave() {
        return pagesToSave;
    }

}
