package doodle.qa.api.regression.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import doodle.qa.DoodleTest;
import doodle.qa.api.regression.config.Config;
import doodle.qa.api.regression.config.Endpoints;
import doodle.qa.api.regression.config.Requests.UserRequestBody;
import doodle.qa.api.regression.config.TestData;
import doodle.qa.com.svcproviderqa.entity.Calendar;
import doodle.qa.com.svcuserqa.entity.User;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;
import java.util.UUID;


@SpringBootTest(classes = DoodleTest.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public class UserApiTest {

    @Autowired
    private Config config;
    @Autowired
    private TestData testData;

    String userId;
    String calendarId;
    UserRequestBody requestBody;
    List<User> users;
    List<Calendar> calendars;

    @BeforeAll
    void setup() {
        config.calendarSetUp();
        calendars = testData.getCalendars();
        calendarId = calendars.get(0).getId().toString();
        config.userSetUp();
        users = testData.getUsers();
    }

    @Test
    @Tag("Positive")
    @Tag("Regression")
    @DisplayName("Create User")
    @Order(1)
    void createNewUser() {

        requestBody = UserRequestBody.withDefaults().build();
        Response response =
                given()
                        .body(requestBody)
                        .when()
                        .post(Endpoints.CREATE_USER)
                        .then()
                        .statusCode(HttpStatus.CREATED.value())
                        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("UserResponseBodySchema_Create_Update.json"))
                        .body("name", equalTo(requestBody.getName()),
                                "email", equalTo(requestBody.getEmail()))
                        .extract().response();

        userId = response.getBody().jsonPath().get("id").toString();
    }

    @Test
    @Tag("Positive")
    @Tag("Regression")
    @DisplayName("Get User By User Id")
    @Order(2)
    void getUserById() {

        given()
                .pathParam("userId", userId)
                .when()
                .get(Endpoints.GET_USER_BY_ID)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("UserResponseBodySchema_Create_Update.json"))
                .body("id", equalTo(userId));
    }

    @Test
    @Tag("Negative")
    @Tag("Regression")
    @DisplayName("Get User By Invalid UserId")
    @Order(3)
    void getUserByInvalidId() {

        given()
                .pathParam("userId", UUID.randomUUID().toString())
                .when()
                .get(Endpoints.GET_USER_BY_ID)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @Tag("Positive")
    @Tag("Regression")
    @DisplayName("Update User")
    @Order(4)
    void updateUser() {

        requestBody = UserRequestBody.withDefaults().build();

        given()
                .pathParam("userId", userId)
                .body(requestBody)
                .when()
                .put(Endpoints.UPDATE_USER)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("UserResponseBodySchema_Create_Update.json"))
                .body("name", equalTo(requestBody.getName()),
                        "email", equalTo(requestBody.getEmail()));
    }

    @Test
    @Tag("Positive")
    @Tag("Regression")
    @DisplayName("Add Calendar to a User")
    @Order(5)
    void addCalendarToUser() {

        given()
                .pathParam("userId", userId)
                .pathParam("calendarId", calendarId)
                .when()
                .post(Endpoints.ADD_CALENDAR_TO_USER)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("UserResponseBodySchema_Create_Update.json"))
                .body("id", equalTo(userId), "calendarIds", hasItems(calendarId));
    }

    @Test
    @Tag("Positive")
    @Tag("Regression")
    @DisplayName("Remove Calendar From a User")
    @Order(6)
    void deleteCalendarFromUser() {

        given()
                .pathParam("userId", userId)
                .pathParam("calendarId", calendarId)
                .when()
                .delete(Endpoints.DELETE_CALENDAR_FROM_USER)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("UserResponseBodySchema_Create_Update.json"))
                .body("id", equalTo(userId), "calendarIds", not(hasItems(calendarId)));
    }

    @Test
    @Tag("Positive")
    @Tag("Regression")
    @DisplayName("Delete a User")
    @Order(7)
    void deleteUser() {

        given()
                .pathParam("userId", userId)
                .when()
                .delete(Endpoints.DELETE_USER)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @Tag("Positive")
    @Tag("Regression")
    @DisplayName("Get All Users")
    @Order(8)
    void getAllUsers() {

        List<User> actualUsers =
                given()
                        .when()
                        .get(Endpoints.GET_ALL_USERS)
                        .then()
                        .statusCode(HttpStatus.OK.value())
                        .extract().jsonPath().getList("users", User.class);

        assertEquals(users, actualUsers);
    }

    @Test
    @Tag("Positive")
    @Tag("Regression")
    @DisplayName("Create User with Chinese characters ")
    @Order(9)
    void createNewUserWithChineseCharacters() {

        requestBody = UserRequestBody.withDefaults("zh-CN").build();

        given()
                .body(requestBody)
                .when()
                .post(Endpoints.CREATE_USER)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("UserResponseBodySchema_Create_Update.json"))
                .body("name", equalTo(requestBody.getName()),
                        "email", equalTo(requestBody.getEmail()));
    }

    @Test
    @Tag("Positive")
    @Tag("Regression")
    @DisplayName("Create User with Russian characters ")
    @Order(10)
    void createNewUserWithRussianCharacters() {

        requestBody = UserRequestBody.withDefaults("ru").build();

        given()
                .body(requestBody)
                .when()
                .post(Endpoints.CREATE_USER)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("UserResponseBodySchema_Create_Update.json"))
                .body("name", equalTo(requestBody.getName()),
                        "email", equalTo(requestBody.getEmail()));
    }

    @Test
    @Tag("Negative")
    @Tag("Regression")
    @DisplayName("Create User with Invalid Email")
    @Order(11)
    void createNewUserWithInvalidEmail() {

        requestBody = UserRequestBody.withDefaults().email("InvalidEmail").build();

        given()
                .body(requestBody)
                .when()
                .post(Endpoints.CREATE_USER)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @Tag("Negative")
    @Tag("Regression")
    @DisplayName("Create User with Empty name field")
    @Order(12)
    void createNewUserWithNoName() {

        requestBody = UserRequestBody.withDefaults().name("").build();

        given()
                .body(requestBody)
                .when()
                .post(Endpoints.CREATE_USER)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}