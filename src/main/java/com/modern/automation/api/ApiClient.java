package com.modern.automation.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Backend ApiClient using RestAssured for hybrid authentication.
 */
public class ApiClient {

    private static final Logger logger = LoggerFactory.getLogger(ApiClient.class);
    private final String baseApiUrl;

    public ApiClient(String baseApiUrl) {
        this.baseApiUrl = baseApiUrl;
    }

    /**
     * Authenticates via API and returns the session cookies.
     * Note: Implementation details may vary based on the target application's auth mechanism.
     */
    public Map<String, String> getSessionCookies(String username, String password) {
        logger.info("Fetching session cookies via API for user: {}", username);

        // OrangeHRM specific: Needs CSRF token usually, but we'll mock/simplify the flow
        // or assume a standard REST API auth if applicable.
        // For demonstration, we assume a POST to /web/index.php/auth/validate
        
        Response response = RestAssured.given()
                .baseUri(baseApiUrl)
                .contentType(ContentType.URLENC)
                .formParam("_token", "dummy_token") // Usually fetched from a GET first
                .formParam("username", username)
                .formParam("password", password)
                .post("/web/index.php/auth/validate");

        logger.info("API Auth Status Code: {}", response.getStatusCode());
        
        return response.getCookies();
    }
}
