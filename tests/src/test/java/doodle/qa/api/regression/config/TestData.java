package doodle.qa.api.regression.config;

import doodle.qa.api.regression.config.Requests.CalendarRequestBody;
import doodle.qa.api.regression.config.Requests.UserRequestBody;
import doodle.qa.com.svcproviderqa.entity.Calendar;
import doodle.qa.com.svcuserqa.entity.User;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.stream.IntStream;

import static io.restassured.RestAssured.given;


public class TestData extends Config{

    public List<Calendar>  getCalendars() {

        Response response = given().get(Endpoints.GET_ALL_CALENDARS).then().statusCode(HttpStatus.OK.value()).extract().response();
        List<Calendar> calendars = response.jsonPath().getList("calendars", Calendar.class);

        if (calendars.isEmpty()) {
                return IntStream.range(0, 2)
                        .mapToObj(i -> given()
                                .body(CalendarRequestBody.builder().build())
                                .when()
                                .post(Endpoints.CREATE_MEETING)
                                .then()
                                .statusCode(HttpStatus.CREATED.value()).extract()
                                .as(Calendar.class))
                        .toList();
        }
        return calendars;
    }

    public List<User> getUsers()  {

        Response response = given().get(Endpoints.GET_ALL_USERS).then().statusCode(HttpStatus.OK.value()).extract().response();
        List<User> users = response.jsonPath().getList("users", User.class);

        if (users.isEmpty()) {
            return IntStream.range(0, 2)
                    .mapToObj(i -> given()
                            .body(UserRequestBody.withDefaults().build())
                            .when()
                            .post(Endpoints.CREATE_USER)
                            .then()
                            .statusCode(HttpStatus.CREATED.value())
                            .extract()
                            .as(User.class)
                    )
                    .peek(System.out::println)
                    .toList();
        }
        return users;
    }
}
