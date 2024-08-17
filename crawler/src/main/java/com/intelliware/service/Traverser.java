package com.intelliware.service;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.intelliware.model.PageContent;
import com.intelliware.model.TraverserSharedData;

public class Traverser implements Runnable {
    private Queue<URL> urls;
    private TraverserSharedData sharedData;
    private HtmlNormalizer normalizer;

    public Traverser(List<URL> urls, TraverserSharedData sharedData) {
        this.urls = new LinkedList<>(urls);
        this.normalizer = new HtmlNormalizer();
        this.sharedData = sharedData;
    }

    public void run() {
        while (!urls.isEmpty()) {
            if (sharedData.getRemainingTraverses().get() <= 0) {
                return;
            }
            processFront();
            sharedData.getRemainingTraverses().decrementAndGet();
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
        int hashKey = pageContent.getContent().hashCode();
        return sharedData.getVisitedPages().containsKey(hashKey) == true;
    }

    private void addPage(PageContent pageContent) {
        if (pageContent.getContent().equals("") == true) {
            return;
        }
        sharedData.getVisitedPages().put(pageContent.hashCode(), true);
        sharedData.getPagesToSave().add(pageContent);
        sharedData.getPagesToSaveCnt().release();
    }

    private void addUrls() {
        urls.addAll(normalizer.getLinks());
    }
}
