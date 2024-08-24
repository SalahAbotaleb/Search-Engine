package com.intelliware.model;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class TraverserSharedData {
    private final AtomicInteger remainingTraverses;
    private final ConcurrentHashMap<Integer, Boolean> visitedPages;
    private final Queue<PageContent> pagesToSave;
    private final Semaphore pagesToSaveCnt;
    private final Queue<UrlState> urlsToSave;
    private final Semaphore urlsToSaveCnt;
    private final Queue<UrlState> visitedUrlsToDelete;
    private final Semaphore visitedUrlsToDeleteCnt;

    public TraverserSharedData(int maxTraverses) {
        remainingTraverses = new AtomicInteger(maxTraverses);
        visitedPages = new ConcurrentHashMap<>();
        pagesToSave = new ConcurrentLinkedQueue<>();
        pagesToSaveCnt = new Semaphore(0);
        urlsToSave = new ConcurrentLinkedQueue<>();
        urlsToSaveCnt = new Semaphore(0);
        visitedUrlsToDelete = new ConcurrentLinkedQueue<>();
        visitedUrlsToDeleteCnt = new Semaphore(0);
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

    public Queue<UrlState> getUrlsToSave() {
        return urlsToSave;
    }

    public Semaphore getUrlsToSaveCnt() {
        return urlsToSaveCnt;
    }

    public Queue<UrlState> getVisitedUrlsToDelete() {
        return visitedUrlsToDelete;
    }

    public Semaphore getVisitedUrlsToDeleteCnt() {
        return visitedUrlsToDeleteCnt;
    }

}
