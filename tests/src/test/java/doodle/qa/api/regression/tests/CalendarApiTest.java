package doodle.qa.api.regression.tests;

import doodle.qa.DoodleTest;
import doodle.qa.api.regression.config.Config;
import doodle.qa.api.regression.config.Endpoints;
import doodle.qa.api.regression.config.Requests.MeetingRequestBody;
import doodle.qa.api.regression.config.TestData;
import doodle.qa.com.svcuserqa.entity.User;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static doodle.qa.api.regression.config.Requests.MeetingRequestBody.DATE_TIME_FORMATTER;
import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;


@SpringBootTest(classes = DoodleTest.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public class CalendarApiTest {

    @Autowired
    private Config config;
    @Autowired
    private TestData testData;

    String meetingId;
    String userId;
    String calendarId;
    MeetingRequestBody requestBody;
    List<User> users;


    @BeforeAll
    void setup() {
        config.userSetUp();
        users = testData.getUsers();
        calendarId = users.get(0).getCalendarIds().get(0).toString();
        userId = users.get(0).getId().toString();
        config.meetingsSetUp();
    }

    @Test
    @Order(1)
    @Tag("Positive")
    @Tag("Regression")
    @DisplayName("Create a New Meeting")
    void createNewMeeting() {

        requestBody = MeetingRequestBody.builder().calendarId(calendarId).build();

        Response response =
                given()
                        .queryParam("userId", userId)
                        .body(requestBody)
                        .when()
                        .post(Endpoints.CREATE_MEETING)
                        .then()
                        .statusCode(HttpStatus.CREATED.value())
                        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("MeetingResponseBodySchema_Create_Update.json"))
                        .body("title", equalTo(requestBody.getTitle()),
                                "description", equalTo(requestBody.getDescription()))
                        .extract().response();

        meetingId = response.getBody().jsonPath().get("id").toString();
    }

    @Test
    @Order(2)
    @Tag("Negative")
    @Tag("Regression")
    @DisplayName("Create a New Meeting with Invalid User Id")
    void createNewMeetingWithInvalidUser() {

        requestBody = MeetingRequestBody.builder().calendarId(calendarId).build();

        given()
                .queryParam("userId", UUID.randomUUID().toString())
                .body(requestBody)
                .when()
                .post(Endpoints.CREATE_MEETING)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @Order(3)
    @Tag("Positive")
    @Tag("Regression")
    @DisplayName("Get a New Meeting By Id")
    void getMeetingById() {

        given()
                .pathParam("id", meetingId)
                .queryParam("userId", userId)
                .queryParam("calendarId", calendarId)
                .when()
                .get(Endpoints.GET_MEETING_BY_ID)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("MeetingResponseBodySchema_Create_Update.json"));
    }

    @Test
    @Order(4)
    @Tag("Negative")
    @Tag("Regression")
    @DisplayName("Get a Meeting By Invalid MeetingId")
    void getMeetingByInvalidId() {

        given()
                .pathParam("id", UUID.randomUUID().toString())
                .queryParam("userId", userId)
                .queryParam("calendarId", calendarId)
                .when()
                .get(Endpoints.GET_MEETING_BY_ID)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test()
    @Tag("Positive")
    @Tag("Regression")
    @DisplayName("Update an existing Meeting")
    @Order(5)
    void updateMeeting() {

        requestBody = MeetingRequestBody.builder().calendarId(calendarId).build();

        given()
                .queryParam("userId", userId)
                .pathParam("id", meetingId)
                .body(requestBody)
                .when()
                .put(Endpoints.UPDATE_MEETING)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("MeetingResponseBodySchema_Create_Update.json"))
                .body("title", equalTo(requestBody.getTitle()),
                        "description", equalTo(requestBody.getDescription()));
    }

    @Test
    @Order(6)
    @Tag("Positive")
    @Tag("Regression")
    @DisplayName("Get All Meetings")
    void getAllMeetings() {

        given()
                .queryParam("userId", userId)
                .queryParam("calendarId", calendarId)
                .queryParam("from", LocalDateTime.now().minusDays(1).format(DATE_TIME_FORMATTER))
                .queryParam("to", LocalDateTime.now().plusDays(1).format(DATE_TIME_FORMATTER))
                .when()
                .get(Endpoints.GET_ALL_MEETINGS)
                .then()
                .statusCode(HttpStatus.OK.value());
    }


    @Test
    @Tag("Positive")
    @Tag("Regression")
    @DisplayName("Delete a Meeting")
    @Order(7)
    void deleteMeeting() {

        given()
                .pathParam("id", meetingId)
                .queryParam("userId", userId)
                .queryParam("calendarId", calendarId)
                .when()
                .delete(Endpoints.DELETE_MEETING)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @Order(8)
    @Tag("Positive")
    @Tag("Regression")
    @DisplayName("Get Available Meeting Time slots")
    void getMeetingSlots() {

                given()
                        .queryParam("userId", userId)
                        .queryParam("calendarId", calendarId)
                        .queryParam("from", LocalDateTime.now().format(DATE_TIME_FORMATTER))
                        .queryParam("to", LocalDateTime.now().plusHours(10).format(DATE_TIME_FORMATTER))
                        .queryParam("slotDuration", 60)
                        .queryParam("page", 1)
                        .queryParam("size", 3)
                        .when()
                        .get(Endpoints.GET_MEETING_TIME_SLOTS)
                        .then()
                        .statusCode(HttpStatus.OK.value());
    }
}