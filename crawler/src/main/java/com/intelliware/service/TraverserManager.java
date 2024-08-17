package com.intelliware.service;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import com.intelliware.model.TraverserSharedData;

public class TraverserManager {
    private List<URL> urls;
    private List<Thread> traverserThreads;
    private Thread saverThread;
    private final int traverserThreadsCnt;
    private TraverserSharedData sharedData;

    public TraverserManager(int maxTraverses, List<URL> urls, int traverserThreadsCnt) {
        this.traverserThreadsCnt = traverserThreadsCnt;
        this.urls = urls;
        sharedData = new TraverserSharedData(maxTraverses);
        initThreads();
    }

    private void initThreads() {
        initTraverserThreads();
        initSaverThread();
    }

    private void initTraverserThreads() {
        traverserThreads = new LinkedList<Thread>();
        for (int i = 0; i < traverserThreadsCnt; i++) {
            List<URL> partition = getTraverserThreadPartition(i);
            Traverser traverser = new Traverser(partition, sharedData);
            traverserThreads.add(i, new Thread(traverser));
        }
    }

    private void initSaverThread() {
        FetchedPagesSaver saver = new FetchedPagesSaver(sharedData.getPagesToSave(), sharedData.getPagesToSaveCnt());
        saverThread = new Thread(saver);
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
        startTraverserThreads();
        startSaverThread();
    }

    private void startTraverserThreads() {
        for (int i = 0; i < traverserThreadsCnt; i++) {
            traverserThreads.get(i).start();
        }
    }

    private void startSaverThread() {
        saverThread.start();
    }

    public void waitForFinish() {
        waitForTraverserThreads();
        stopSaverThread();
    }

    private void waitForTraverserThreads() {
        for (int i = 0; i < traverserThreadsCnt; i++) {
            try {
                traverserThreads.get(i).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopSaverThread() {
        saverThread.interrupt();
    }
}
