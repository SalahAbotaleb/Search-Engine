package com.intelliware.model;

public class TraverserAlgoConfig {
    private int maxNumberOfDomainCrawledUrls;
    private int maxTraverses;

    public TraverserAlgoConfig(int maxNumberOfVisitedPagesPerDomain, int maxTraverses) {
        this.maxNumberOfDomainCrawledUrls = maxNumberOfVisitedPagesPerDomain;
        this.maxTraverses = maxTraverses;
    }

    public int getMaxNumberOfDomainCrawledUrls() {
        return maxNumberOfDomainCrawledUrls;
    }

    public void setMaxNumberOfDomainCrawledUrls(int maxNumberOfDomainCrawledUrls) {
        this.maxNumberOfDomainCrawledUrls = maxNumberOfDomainCrawledUrls;
    }

    public int getMaxTraverses() {
        return maxTraverses;
    }

    public void setMaxTraverses(int maxTraverses) {
        this.maxTraverses = maxTraverses;
    }

}
