package com.intelliware;

import java.io.File;
import java.net.URL;
import java.util.List;

import com.intelliware.model.PageContent;
import com.intelliware.service.HtmlNormalizer;
import com.intelliware.service.SeedsParser;
import com.intelliware.service.TraverserManager;
import com.intelliware.service.UrlInvoker;

public class Main {
    public static void main(String[] args) {
        SeedsParser parser = new SeedsParser("seeds.txt");
        List<URL> urls = parser.parse();
        TraverserManager manager = new TraverserManager(4, urls, 2);
        manager.start();
        manager.waitForFinish();
        System.out.println(manager.getVisitedPages().size());
    }
}