package com.intelliware.service;

import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
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
        threads = new LinkedList<Thread>();
        for (int i = 0; i < threadsCnt; i++) {
            List<URL> partition = getThreadPartition(i);
            Traverser traverser = new Traverser(partition, visitedPages, remainingTraverses);
            threads.add(i, new Thread(traverser));
        }
    }

    private List<URL> getThreadPartition(int idx) {
        int urlsCnt = urls.size();
        int slice = (int) Math.ceil(urlsCnt / (double) threadsCnt);
        int stIdx = slice * idx;
        int endIdx = Math.min(slice * idx + slice - 1, urlsCnt - 1);
        return urls.subList(stIdx, endIdx);
    }

    public void start() {
        for (int i = 0; i < threadsCnt; i++) {
            threads.get(i).start();
        }
    }

    public List<PageContent> getVisitedPages() {
        return visitedPages.keySet().stream().toList();
    }
}
