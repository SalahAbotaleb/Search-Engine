package com.intelliware.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class SeedsParser {
    private String resource;

    public SeedsParser(String resource) {
        this.resource = resource;
    }

    public String getResource() {
        return resource;
    }

    public List<URL> parse() {
        List<URL> urls = new LinkedList<>();
        ResourceReader resourceReader = new ResourceReader(resource);
        List<String> fileLines = resourceReader.read();
        for (String line : fileLines) {
            try {
                URL parsedLine = new URL(line);
                urls.add(parsedLine);
            } catch (MalformedURLException e) {
                System.out.println("Invalid URL " + line + " in seeds file");
            }
        }
        return urls;
    }

}
