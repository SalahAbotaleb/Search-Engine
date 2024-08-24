package com.intelliware.service;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.intelliware.model.PageContent;
import com.intelliware.model.TraverserAlgoConfig;
import com.intelliware.model.UrlState;
import com.intelliware.model.TraverserSharedData;

public class Traverser implements Runnable {
    private Queue<URL> urls;
    private TraverserSharedData sharedData;
    private TraverserAlgoConfig algoConfig;
    private HtmlNormalizer normalizer;
    private HashMap<String, Integer> domainCrawledUrlsCnt;

    public Traverser(List<URL> urls, TraverserSharedData sharedData, TraverserAlgoConfig algoConfig) {
        this.urls = new LinkedList<>(urls);
        this.normalizer = new HtmlNormalizer();
        this.domainCrawledUrlsCnt = new HashMap<>();
        this.algoConfig = algoConfig;
        this.sharedData = sharedData;
    }

    public void run() {
        while (!urls.isEmpty()) {
            if (sharedData.getRemainingTraverses().get() <= 0) {
                return;
            }
            processFront();
        }
    }

    private void processFront() {
        URL url = urls.peek();
        PageContent pageContent = getUrlContent(url);
        if (isPageVisited(pageContent)) {
            urls.remove();
            return;
        }
        addPage(pageContent);
        addUrls();
        urls.remove();
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
        sharedData.getRemainingTraverses().decrementAndGet();
        sharedData.getVisitedPages().put(pageContent.hashCode(), true);
        sharedData.getPagesToSave().add(pageContent);
        sharedData.getPagesToSaveCnt().release();
    }

    private void addUrls() {
        List<URL> nextUrls = normalizer.getLinks();
        nextUrls = nextUrls.stream().filter((url) -> {
            String host = url.getHost();
            int cnt = domainCrawledUrlsCnt.getOrDefault(host, 0);
            if (cnt < algoConfig.getMaxNumberOfDomainCrawledUrls()) {
                domainCrawledUrlsCnt.put(host, cnt + 1);
                return true;
            }
            return false;
        }).toList();
        urls.addAll(nextUrls);
        removeVisitedUrlFromDb();
        addLatestQueuedUrlsInDb(nextUrls);
    }

    private void removeVisitedUrlFromDb() {
        UrlState removeUrl = new UrlState(urls.peek().toString());
        removeUrl.setVisited(true);
        sharedData.getVisitedUrlsToDelete().add(removeUrl);
        sharedData.getVisitedUrlsToDeleteCnt().release();
    }

    private void addLatestQueuedUrlsInDb(List<URL> nextUrls) {
        if (nextUrls == null) {
            return;
        }
        sharedData.getUrlsToSave().addAll(nextUrls.stream().map((url) -> {
            return new UrlState(url.toString());
        }).toList());
        sharedData.getUrlsToSaveCnt().release(nextUrls.size());
    }
}
