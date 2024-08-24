package com.intelliware;

import java.net.URL;
import java.util.List;

import com.intelliware.model.TraverserAlgoConfig;
import com.intelliware.service.SeedsParser;
import com.intelliware.service.StateRecover;
import com.intelliware.service.TraverserManager;

public class Main {
    public static void main(String[] args) {
        SeedsParser parser = new SeedsParser("seeds.txt");
        StateRecover recover = new StateRecover();
        List<URL> urls = recover.getUrls();
        if (urls == null) {
            urls = parser.parse();
        }
        TraverserAlgoConfig algoConfig = new TraverserAlgoConfig((int) 1e5, (int) 1e5);
        TraverserManager manager = new TraverserManager(algoConfig, urls, 10);
        manager.start();
        manager.waitForFinish();
    }
}