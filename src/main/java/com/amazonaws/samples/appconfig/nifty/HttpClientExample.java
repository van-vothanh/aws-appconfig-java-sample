package com.amazonaws.samples.appconfig.nifty;

/**
 * Example class demonstrating the use of Apache HttpClient 5.x package.
 * 
 * IMPORTANT: To use this class, you must add the following dependency to your pom.xml file:
 * 
 * <dependency>
 *     <groupId>org.apache.httpcomponents.client5</groupId>
 *     <artifactId>httpclient5</artifactId>
 *     <version>5.2.1</version>
 * </dependency>
 * 
 * This example uses the modern Apache HttpClient 5.x which replaces the deprecated
 * commons-httpclient package.
 */

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.net.URIBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class HttpClientExample {

    private static final String USER_AGENT = "AWS-AppConfig-Sample-HttpClient/1.0";
    
    /**
     * Performs an HTTP GET request to the specified URL.
     * 
     * @param url the URL to send the request to
     * @return the response body as a String
     * @throws IOException if an I/O error occurs
     */
    public String performGetRequest(String url) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            
            // Build URI with query parameters
            URI uri = new URIBuilder(url)
                .addParameter("param1", "value1")
                .addParameter("param2", "value2")
                .build();
            
            HttpGet httpGet = new HttpGet(uri);
            
            // Set request headers
            httpGet.setHeader("User-Agent", USER_AGENT);
            httpGet.setHeader("Accept", "application/json");
            
            // Execute the request
            try (CloseableHttpResponse response = client.execute(httpGet)) {
                int statusCode = response.getCode();
                
                // Check status code
                if (statusCode != HttpStatus.SC_OK) {
                    System.err.println("Method failed: " + response.getReasonPhrase());
                }
                
                // Read the response body
                HttpEntity entity = response.getEntity();
                try {
                    return entity != null ? EntityUtils.toString(entity) : "";
                } catch (ParseException e) {
                    throw new IOException("Error parsing response", e);
                }
            }
        } catch (URISyntaxException e) {
            throw new IOException("Invalid URI: " + url, e);
        }
    }
    
    /**
     * Performs an HTTP POST request with JSON data to the specified URL.
     * 
     * @param url the URL to send the request to
     * @param jsonPayload the JSON data to send in the request body
     * @return the response body as a String
     * @throws IOException if an I/O error occurs
     */
    public String performPostRequest(String url, String jsonPayload) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            
            // Set request headers
            httpPost.setHeader("User-Agent", USER_AGENT);
            httpPost.setHeader("Accept", "application/json");
            
            // Set request body
            StringEntity entity = new StringEntity(jsonPayload, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            
            // Execute the request
            try (CloseableHttpResponse response = client.execute(httpPost)) {
                int statusCode = response.getCode();
                
                // Handle different status codes
                handleStatusCode(statusCode, response);
                
                // Read the response body
                HttpEntity responseEntity = response.getEntity();
                try {
                    return responseEntity != null ? EntityUtils.toString(responseEntity) : "";
                } catch (ParseException e) {
                    throw new IOException("Error parsing response", e);
                }
            }
        }
    }
    
    /**
     * Handles HTTP status codes and throws appropriate exceptions for error codes.
     * 
     * @param statusCode the HTTP status code
     * @param response the HTTP response
     * @throws IOException if an error status code is received
     */
    private void handleStatusCode(int statusCode, CloseableHttpResponse response) throws IOException {
        switch (statusCode) {
            case HttpStatus.SC_OK:
            case HttpStatus.SC_CREATED:
            case HttpStatus.SC_ACCEPTED:
                // Success - do nothing
                break;
            case HttpStatus.SC_UNAUTHORIZED:
                throw new IOException("Authentication required: " + response.getReasonPhrase());
            case HttpStatus.SC_FORBIDDEN:
                throw new IOException("Access denied: " + response.getReasonPhrase());
            case HttpStatus.SC_NOT_FOUND:
                throw new IOException("Resource not found: " + response.getReasonPhrase());
            default:
                if (statusCode >= 400) {
                    throw new IOException("HTTP error code: " + statusCode + " - " + response.getReasonPhrase());
                }
        }
    }
    
    /**
     * Extracts and prints headers from an HTTP response.
     * 
     * @param response the HTTP response
     */
    private void printResponseHeaders(CloseableHttpResponse response) {
        System.out.println("Response Headers:");
        Header[] headers = response.getHeaders();
        for (Header header : headers) {
            System.out.println(header.getName() + ": " + header.getValue());
        }
    }
    
    /**
     * Main method demonstrating the use of the HTTP client.
     */
    public static void main(String[] args) {
        HttpClientExample example = new HttpClientExample();
        
        try {
            // Example GET request
            System.out.println("Performing GET request...");
            String getResponse = example.performGetRequest("https://httpbin.org/get");
            System.out.println("GET Response: " + getResponse);
            
            // Example POST request with JSON
            System.out.println("\nPerforming POST request...");
            String jsonPayload = "{\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}";
            String postResponse = example.performPostRequest("https://httpbin.org/post", jsonPayload);
            System.out.println("POST Response: " + postResponse);
            
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

