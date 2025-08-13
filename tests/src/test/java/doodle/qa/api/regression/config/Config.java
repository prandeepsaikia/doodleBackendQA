package doodle.qa.api.regression.config;


import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.qameta.allure.restassured.AllureRestAssured;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class Config {

    @Value("${api.userBaseURL}")
    private String userBaseURL;

    @Value("${api.userBasePath}")
    private String userBasePath;

    @Value("${api.calendarBaseURL}")
    private String calendarBaseURL;

    @Value("${api.calendarMeetingBasePath}")
    private String calendarMeetingBasePath;

    @Value("${api.providerBaseURL}")
    private String providerBaseURL;

    @Value("${api.providerEventBasePath}")
    private String providerEventsBasePath;

    @Value("${api.providerCalendarBasePath}")
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
