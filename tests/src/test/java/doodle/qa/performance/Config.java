package doodle.qa.performance;

import io.gatling.javaapi.http.HttpProtocolBuilder;
import lombok.Getter;

import static io.gatling.javaapi.http.HttpDsl.http;

/**
 * Centralized configuration for performance (Gatling) tests.
 */
@Getter
public class Config {

    private final String userBaseUrl;
    private final String userBasePath;

    public Config() {
        this.userBaseUrl = System.getProperty("api.userBaseURL", "http://localhost:8080");
        this.userBasePath = System.getProperty("api.userBasePath", "/api/users");
    }

    public HttpProtocolBuilder httpProtocol() {
        return http
                .baseUrl(userBaseUrl)
                .acceptHeader("application/json")
                .contentTypeHeader("application/json");
    }
}
