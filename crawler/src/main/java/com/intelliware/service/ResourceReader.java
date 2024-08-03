package com.intelliware.service;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class ResourceReader {
    private String path;

    public ResourceReader(String path) {
        this.path = path;
    }

    public List<String> read() {
        List<String> lines = new LinkedList<>();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(path);
                Scanner reader = new Scanner(input);) {
            while (reader.hasNextLine()) {
                lines.add(reader.nextLine());
            }
        } catch (Exception e) {
            System.out.println("Problem in opening resource " + path);
        }
        return lines;
    }
}
