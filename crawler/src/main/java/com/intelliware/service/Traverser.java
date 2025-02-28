package com.intelliware.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.HashSet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

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
    private HashMap<String, HashSet<String>> robotExclusionPages;

    public Traverser(List<URL> urls, TraverserSharedData sharedData, TraverserAlgoConfig algoConfig) {
        this.urls = new LinkedList<>(urls);
        this.normalizer = new HtmlNormalizer();
        this.domainCrawledUrlsCnt = new HashMap<>();
        this.algoConfig = algoConfig;
        this.sharedData = sharedData;
        this.robotExclusionPages = new HashMap<>();
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
        if (isPageVisited(pageContent) || normalizer.isLangEnglish() == false) {
            urls.remove();
            return;
        }
        addPage(pageContent);
        addPagesToExclude();
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

    private void addPagesToExclude() {
        if (isRobotFileReadBefore()) {
            return;
        }
        PageContent content = readRobotFile();
        parseRobotFile(content);
    }

    private boolean isRobotFileReadBefore() {
        URL url = urls.peek();
        String host = url.getHost();
        return robotExclusionPages.containsKey(host);
    }

    private PageContent readRobotFile() {
        String host = getCurrPageHostName();
        try {
            return getUrlContent(new URL(host));
        } catch (MalformedURLException e) {
        }
        return new PageContent("");
    }

    private void parseRobotFile(PageContent content) {
        BufferedReader reader = new BufferedReader(new StringReader(content.toString()));
        String line;
        String host = getCurrPageHostName();
        robotExclusionPages.put(host, new HashSet<>());
        try {
            boolean isAnyCrawlerSection = false;
            boolean isFirstTime = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstTime == false && isAnyCrawlerSection == false) {
                    break;
                }
                if (line.startsWith("User-agent: *")) {
                    isFirstTime = false;
                    isAnyCrawlerSection = true;
                } else if (line.startsWith("Disallow:")) {
                    String subPath = line.split(" ")[1];
                    String fullPath = host + subPath;
                    robotExclusionPages.get(host).add(fullPath);
                } else if (line.startsWith("User-agent:") && isAnyCrawlerSection == true) {
                    isAnyCrawlerSection = false;
                }
            }
        } catch (IOException e) {
        }
    }

    private String getCurrPageHostName() {
        URL url = urls.peek();
        String host = url.getHost();
        return host;
    }

    private void addUrls() {
        List<URL> nextUrls = normalizer.getLinks();
        nextUrls = nextUrls.stream().filter(url -> isValidToAddUrl(url)).toList();
        urls.addAll(nextUrls);
        removeVisitedUrlFromDb();
        addLatestQueuedUrlsInDb(nextUrls);
    }

    private boolean isValidToAddUrl(URL url) {
        String host = url.getHost();
        int cnt = domainCrawledUrlsCnt.getOrDefault(host, 0);
        if (isPageExcluded(url)) {
            return false;
        }

        if (cnt < algoConfig.getMaxNumberOfDomainCrawledUrls()) {
            domainCrawledUrlsCnt.put(host, cnt + 1);
            return true;
        }
        return false;
    }

    private boolean isPageExcluded(URL url) {
        String host = url.getHost();
        HashSet<String> toExclude = robotExclusionPages.getOrDefault(host, null);
        if (toExclude != null && toExclude.contains(url.toString())) {
            return true;
        }
        return false;
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
