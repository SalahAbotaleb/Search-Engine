package com.intelliware.service;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import com.intelliware.model.TraverserAlgoConfig;
import com.intelliware.model.TraverserSharedData;

public class TraverserManager {
    private List<URL> urls;
    private List<Thread> traverserThreads;
    private List<DbConsumer> saverThreads;
    private final int traverserThreadsCnt;
    private TraverserSharedData sharedData;
    private TraverserAlgoConfig algoConfig;

    public TraverserManager(TraverserAlgoConfig algoConfig, List<URL> urls, int traverserThreadsCnt) {
        this.traverserThreadsCnt = traverserThreadsCnt;
        this.urls = urls;
        this.algoConfig = algoConfig;
        sharedData = new TraverserSharedData(algoConfig.getMaxTraverses());
        initThreads();
    }

    private void initThreads() {
        initTraverserThreads();
        initConsumerThreads();
    }

    private void initTraverserThreads() {
        traverserThreads = new LinkedList<Thread>();
        for (int i = 0; i < traverserThreadsCnt; i++) {
            List<URL> partition = getTraverserThreadPartition(i);
            Traverser traverser = new Traverser(partition, sharedData, algoConfig);
            traverserThreads.add(i, new Thread(traverser));
        }
    }

    private void initConsumerThreads() {
        saverThreads = new LinkedList<>();
        saverThreads.add(new FetchedPagesSaver(sharedData.getPagesToSave(), sharedData.getPagesToSaveCnt()));
        saverThreads.add(new QueuedUrlStateSaver(sharedData.getUrlsToSave(), sharedData.getUrlsToSaveCnt()));
        saverThreads.add(
                new VisitedUrlStateSaver(sharedData.getVisitedUrlsToDelete(), sharedData.getVisitedUrlsToDeleteCnt()));

    }

    private List<URL> getTraverserThreadPartition(int idx) {
        int urlsCnt = urls.size();
        int slice = (int) Math.floor(urlsCnt / (double) traverserThreadsCnt);
        int stIdx = slice * idx;
        int endIdxExclusive = slice * idx + slice;
        if (idx == traverserThreadsCnt - 1) {
            endIdxExclusive = urlsCnt;
        }
        return urls.subList(stIdx, endIdxExclusive);
    }

    public void start() {
        startSaverThreads();
        startTraverserThreads();
    }

    private void startTraverserThreads() {
        for (Thread traverser : traverserThreads) {
            traverser.start();
        }
    }

    private void startSaverThreads() {
        for (DbConsumer consumer : saverThreads) {
            consumer.start();
        }
    }

    public void waitForFinish() {
        waitForTraverserThreads();
        stopSaverThreads();
    }

    private void waitForTraverserThreads() {
        for (Thread traverser : traverserThreads) {
            try {
                traverser.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopSaverThreads() {
        for (DbConsumer consumer : saverThreads) {
            consumer.interrupt();
        }
    }
}
