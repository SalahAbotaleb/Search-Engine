package com.intelliware.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlNormalizer {
    private String content;
    private Element body;
    private Element htmlTag;

    public HtmlNormalizer() {
    }

    public HtmlNormalizer(String content) {
        setContent(content);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        this.parse();
    }

    private void parse() {
        Document document = Jsoup.parse(content);
        htmlTag = document.selectFirst("html");
        document.select("script, style").remove();
        body = document.body();
    }

    public boolean isLangEnglish() {
        if (htmlTag == null) {
            return true;
        }
        String language = htmlTag.attr("lang");
        if (language.equals("")) {
            return true;
        }
        if (language.equals("en")) {
            return true;
        }
        return false;
    }

    public String normalize() {
        if (body == null)
            return null;
        return body.text().toLowerCase().trim();
    }

    public List<URL> getLinks() {
        if (body == null)
            return null;
        List<URL> parsedLinks = new ArrayList<>();
        Elements links = body.select("a[href]");
        for (Element link : links) {
            addLinkToList(link, parsedLinks);
        }
        return parsedLinks;
    }

    private void addLinkToList(Element link, List<URL> parsedLinks) {
        String parsedLink = parseLink(link);
        if (parsedLink.length() != 0) {
            URL url;
            try {
                url = new URL(parsedLink);
            } catch (MalformedURLException ex) {
                return;
            }
            parsedLinks.add(url);
        }
    }

    private String parseLink(Element link) {
        return link.attr("abs:href");
    }
}
