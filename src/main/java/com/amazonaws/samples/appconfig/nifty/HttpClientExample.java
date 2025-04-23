package com.amazonaws.samples.appconfig.nifty;

/**
 * Example class demonstrating the use of org.apache.commons.httpclient package.
 * 
 * IMPORTANT: To use this class, you must add the following dependency to your pom.xml file:
 * 
 * <dependency>
 *     <groupId>commons-httpclient</groupId>
 *     <artifactId>commons-httpclient</artifactId>
 *     <version>3.1</version>
 * </dependency>
 * 
 * Note: The org.apache.commons.httpclient package has been deprecated and replaced by 
 * Apache HttpComponents HttpClient in newer applications. Consider using that instead
 * for new development.
 */

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

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
        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(url);
        
        try {
            // Set request headers
            method.setRequestHeader("User-Agent", USER_AGENT);
            method.addRequestHeader("Accept", "application/json");
            
            // Configure timeout (in milliseconds)
            method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
            
            // Optional: Add query parameters
            NameValuePair[] params = {
                new NameValuePair("param1", "value1"),
                new NameValuePair("param2", "value2")
            };
            method.setQueryString(params);
            
            // Execute the method
            int statusCode = client.executeMethod(method);
            
            // Check status code
            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("Method failed: " + method.getStatusLine());
            }
            
            // Read the response body
            return readResponseBody(method);
            
        } finally {
            // Release the connection
            method.releaseConnection();
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
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(url);
        
        try {
            // Set request headers
            method.setRequestHeader("User-Agent", USER_AGENT);
            method.setRequestHeader("Content-Type", "application/json");
            method.setRequestHeader("Accept", "application/json");
            
            // Set request body
            StringRequestEntity requestEntity = new StringRequestEntity(
                jsonPayload,
                "application/json",
                "UTF-8");
            method.setRequestEntity(requestEntity);
            
            // Execute the method
            int statusCode = client.executeMethod(method);
            
            // Handle different status codes
            handleStatusCode(statusCode, method);
            
            // Read the response body
            return readResponseBody(method);
            
        } catch (UnsupportedEncodingException e) {
            System.err.println("Error encoding JSON payload: " + e.getMessage());
            throw e;
        } finally {
            // Release the connection
            method.releaseConnection();
        }
    }
    
    /**
     * Handles HTTP status codes and throws appropriate exceptions for error codes.
     * 
     * @param statusCode the HTTP status code
     * @param method the HTTP method that was executed
     * @throws HttpException if an error status code is received
     */
    private void handleStatusCode(int statusCode, HttpMethod method) throws HttpException {
        switch (statusCode) {
            case HttpStatus.SC_OK:
            case HttpStatus.SC_CREATED:
            case HttpStatus.SC_ACCEPTED:
                // Success - do nothing
                break;
            case HttpStatus.SC_UNAUTHORIZED:
                throw new HttpException("Authentication required: " + method.getStatusLine());
            case HttpStatus.SC_FORBIDDEN:
                throw new HttpException("Access denied: " + method.getStatusLine());
            case HttpStatus.SC_NOT_FOUND:
                throw new HttpException("Resource not found: " + method.getStatusLine());
            default:
                if (statusCode >= 400) {
                    throw new HttpException("HTTP error code: " + statusCode + " - " + method.getStatusLine());
                }
        }
    }
    
    /**
     * Reads and returns the response body from an HTTP method.
     * 
     * @param method the HTTP method that was executed
     * @return the response body as a String
     * @throws IOException if an I/O error occurs
     */
    private String readResponseBody(HttpMethod method) throws IOException {
        BufferedReader reader = null;
        
        try {
            reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            
            return stringBuilder.toString().trim();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // Log this but don't throw
                    System.err.println("Error closing reader: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Extracts and prints headers from an HTTP response.
     * 
     * @param method the HTTP method that was executed
     */
    private void printResponseHeaders(HttpMethod method) {
        System.out.println("Response Headers:");
        Header[] headers = method.getResponseHeaders();
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
            
        } catch (HttpException e) {
            System.err.println("HTTP protocol error: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

