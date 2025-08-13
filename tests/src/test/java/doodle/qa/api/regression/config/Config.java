package doodle.qa.api.regression.config;


import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.qameta.allure.restassured.AllureRestAssured;
import org.springframework.beans.factory.annotation.Value;


public class Config {

    @Value("${api.userBaseURL:http://localhost:8080}")
    private String userBaseURL;

    @Value("${api.userBasePath:api/users}")
    private String userBasePath;

    @Value("${api.calendarBaseURL:http://localhost:8082}")
    private String calendarBaseURL;

    @Value("${api.calendarMeetingBasePath:meeting}")
    private String calendarMeetingBasePath;

    @Value("${api.providerBaseURL:http://localhost:8083}")
    private String providerBaseURL;

    @Value("${api.providerEventBasePath:api/events}")
    private String providerEventsBasePath;

    @Value("${api.providerCalendarBasePath:api/calendars}")
    private String providerCalendarBasePath;


    public void userSetUp() {


        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBaseUri(userBaseURL)
                .setBasePath(userBasePath)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .build();
    }

    public void meetingsSetUp() {

        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBaseUri(calendarBaseURL)
                .setBasePath(calendarMeetingBasePath)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .build();
    }

    public void calendarSetUp() {

        if (providerBaseURL == null) {
            System.out.println("Provider Base URL is null");
        }

        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBaseUri(providerBaseURL)
                .setBasePath(providerCalendarBasePath)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }

    public void eventsSetUp() {

        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBaseUri(providerBaseURL)
                .setBasePath(providerEventsBasePath)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }
}
