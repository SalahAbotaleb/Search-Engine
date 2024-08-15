package com.intelliware.service;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.intelliware.model.PageContent;

public class Traverser implements Runnable {
    private Queue<URL> urls;
    private AtomicInteger remainingTraverses;
    private ConcurrentHashMap<PageContent, Boolean> visitedPages;
    private HtmlNormalizer normalizer;

    public Traverser(List<URL> urls, ConcurrentHashMap visitedPages, AtomicInteger remainingTraverses) {
        this.urls = new LinkedList<>(urls);
        this.remainingTraverses = remainingTraverses;
        this.normalizer = new HtmlNormalizer();
        this.visitedPages = visitedPages;
    }

    public void run() {
        while (!urls.isEmpty()) {
            if (remainingTraverses.get() <= 0) {
                return;
            }
            processFront();
            remainingTraverses.decrementAndGet();
        }
    }

    private void processFront() {
        URL url = urls.peek();
        urls.remove();
        PageContent pageContent = getUrlContent(url);
        if (isPageVisited(pageContent)) {
            return;
        }
        addPage(pageContent);
        addUrls();
    }

    private PageContent getUrlContent(URL url) {
        String content = UrlInvoker.invoke(url);
        normalizer.setContent(content);
        PageContent pageContent = new PageContent(normalizer.normalize());
        return pageContent;
    }

    private boolean isPageVisited(PageContent pageContent) {
        return visitedPages.containsKey(pageContent) == true;
    }

    private void addPage(PageContent pageContent) {
        visitedPages.put(pageContent, true);
    }

    private void addUrls() {
        urls.addAll(normalizer.getLinks());
    }
}
