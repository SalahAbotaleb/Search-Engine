package com.intelliware.service;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.intelliware.model.PageContent;

public class TraverserManager {
    private List<URL> urls;
    private AtomicInteger remainingTraverses;
    private ConcurrentHashMap<PageContent, Boolean> visitedPages;
    private List<Thread> threads;
    private final int threadsCnt;

    public TraverserManager(int maxTraverses, List<URL> urls, int threadsCnt) {
        this.threadsCnt = threadsCnt;
        this.urls = urls;
        remainingTraverses = new AtomicInteger(maxTraverses);
        visitedPages = new ConcurrentHashMap<>();
        initThreads();
    }

    private void initThreads() {
        System.out.println(urls.subList(0, 0));
        threads = new LinkedList<Thread>();
        for (int i = 0; i < threadsCnt; i++) {
            List<URL> partition = getThreadPartition(i);
            Traverser traverser = new Traverser(partition, visitedPages, remainingTraverses);
            threads.add(i, new Thread(traverser));
        }
    }

    private List<URL> getThreadPartition(int idx) {
        int urlsCnt = urls.size();
        int slice = (int) Math.floor(urlsCnt / (double) threadsCnt);
        int stIdx = slice * idx;
        int endIdxExclusive = slice * idx + slice;
        if (idx == threadsCnt - 1) {
            endIdxExclusive = urlsCnt;
        }
        return urls.subList(stIdx, endIdxExclusive);
    }

    public void start() {
        for (int i = 0; i < threadsCnt; i++) {
            threads.get(i).start();
        }
    }

    public List<PageContent> getVisitedPages() {
        return visitedPages.keySet().stream().toList();
    }

    public void waitForFinish() {
        for (int i = 0; i < threadsCnt; i++) {
            try {
                threads.get(i).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
