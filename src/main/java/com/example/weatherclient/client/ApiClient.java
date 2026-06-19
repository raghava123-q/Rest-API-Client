package com.example.weatherclient.client;

import com.example.weatherclient.exception.ApiException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reusable API client that manages and executes HTTP GET requests.
 */
public class ApiClient {
    private static final Logger LOGGER = Logger.getLogger(ApiClient.class.getName());
    private final HttpClient httpClient;
    private final Duration timeout;

    public ApiClient() {
        this(Duration.ofSeconds(10));
    }

    public ApiClient(Duration timeout) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(timeout)
                .build();
        this.timeout = timeout;
    }

    /**
     * Sends a GET request to the specified URL and returns the response body.
     *
     * @param url the full URL to query
     * @return the raw string response body
     * @throws ApiException if request fails, times out, or returns a non-200 code
     */
    public String executeGetRequest(String url) throws ApiException {
        LOGGER.info("Sending GET request to URL: " + url);
        long startTime = System.currentTimeMillis();

        try {
            URI uri = new URI(url);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .timeout(timeout)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            long responseTime = System.currentTimeMillis() - startTime;

            LOGGER.info("Response Status Code: " + response.statusCode());
            LOGGER.info("Response Received in: " + responseTime + " ms");

            if (response.statusCode() != 200) {
                String errorMsg = "HTTP request failed with status code: " + response.statusCode() + ". Response: " + response.body();
                LOGGER.severe(errorMsg);
                throw new ApiException(errorMsg);
            }

            return response.body();

        } catch (URISyntaxException e) {
            String errorMsg = "Invalid URI provided: " + url;
            LOGGER.log(Level.SEVERE, errorMsg, e);
            throw new ApiException(errorMsg, e);
        } catch (HttpTimeoutException e) {
            String errorMsg = "HTTP connection timed out for URL: " + url;
            LOGGER.log(Level.SEVERE, errorMsg, e);
            throw new ApiException(errorMsg, e);
        } catch (IOException e) {
            String errorMsg = "Network failure or disconnected during connection to: " + url;
            LOGGER.log(Level.SEVERE, errorMsg, e);
            throw new ApiException(errorMsg, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            String errorMsg = "Request execution was interrupted for URL: " + url;
            LOGGER.log(Level.SEVERE, errorMsg, e);
            throw new ApiException(errorMsg, e);
        }
    }
}
