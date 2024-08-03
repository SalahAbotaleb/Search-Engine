package com.intelliware;

import java.io.File;
import java.net.URL;
import java.util.List;

import com.intelliware.service.HtmlNormalizer;
import com.intelliware.service.SeedsParser;
import com.intelliware.service.UrlInvoker;

public class Main {
    public static void main(String[] args) {
        SeedsParser parser = new SeedsParser("seeds.txt");
        List<URL> urls = parser.parse();
        for (URL u : urls) {
            System.out.println(u);
        }
        String content = UrlInvoker.invoke(urls.get(0));
        String normalized = HtmlNormalizer.normalize(content);
        System.out.println(normalized);

    }
}