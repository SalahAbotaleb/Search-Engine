package com.intelliware.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UrlInvoker {
    private static int MAX_CONNECTION_TIMEOUT = 3000;
    private static int MAX_READ_TIMEOUT = 3000;
    private static String METHOD = "GET";

    public static String invoke(URL url) {
        StringBuilder content = new StringBuilder();
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(METHOD);
            connection.setReadTimeout(MAX_CONNECTION_TIMEOUT);
            connection.setReadTimeout(MAX_READ_TIMEOUT);
            int responseCode = connection.getResponseCode();
            System.out.println(connection.getContentLength() / (double) 1e6 + " MBs");
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append(System.lineSeparator());
                }
            } else {
                System.out.println("Can't read " + url + " ,response code: " + responseCode);
            }
        } catch (Exception e) {
            System.out.println("Error while reading " + url);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return content.toString();
    }
}
