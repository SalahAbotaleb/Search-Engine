package com.intelliware.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class HtmlNormalizer {
    public static String normalize(String content) {
        Document document = Jsoup.parse(content);
        document.select("script, style").remove();
        Element body = document.body();
        String normalizedText = body.text().toLowerCase().trim();
        return normalizedText;
    }
}
