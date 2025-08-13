package doodle.qa.performance;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import doodle.qa.api.regression.config.Requests.UserRequestBody;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Session;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.util.function.Function;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;


public class UserApiPerfTest extends Simulation {


    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    Config config = new Config();

    private final Function<Session, String> userRequestBody = session -> {
        UserRequestBody user = UserRequestBody.withDefaults().build();
        return GSON.toJson(user);
    };

    HttpProtocolBuilder httpProtocol = config.httpProtocol();

    // =============================
    // 1. Create → Get → Delete Flow
    // =============================
    ScenarioBuilder createGetDeleteFlow = scenario("Create → Get → Delete User")
            .exec(
                    http("Create User")
                            .post(config.getUserBasePath())
                            .body(StringBody(userRequestBody))
                            .check(status().is(201))
                            .check(jsonPath("$.id").saveAs("userId"))
            )
            .exec(
                    http("Get User By Id")
                            .get(session -> config.getUserBasePath() + "/" + session.getString("userId"))
                            .check(status().is(200))
                            .check(jsonPath("$.id").ofString().is(session -> session.getString("userId")))
            )
            .exec(
                    http("Delete User")
                            .delete(session -> config.getUserBasePath() + "/" + session.getString("userId"))
                            .check(status().is(204))
            );

    // =============================
    // 2. Bulk User Creation
    // =============================
    ScenarioBuilder bulkUserCreation = scenario("Bulk Create Users")
            .repeat(1)
            .on(
                    exec(
                            http("Bulk Create User")
                                    .post(config.getUserBasePath())
                                    .body(StringBody(userRequestBody))
                                    .check(status().is(201))
                    )
            );

    // =============================
    // 3. Concurrent Updates
    // =============================
    ScenarioBuilder concurrentUpdates = scenario("Concurrent Updates on Same User")
            .exec(
                    // Create user first
                    http("Create Base User")
                            .post(config.getUserBasePath())
                            .body(StringBody(userRequestBody))
                            .check(status().is(201))
                            .check(jsonPath("$.id").saveAs("sharedUserId"))
            )
            .pause(1)
            .repeat(1).on(
                    exec(
                            http("Update User Concurrently")
                                    .put(session -> config.getUserBasePath() + "/" + session.getString("sharedUserId"))
                                    .body(StringBody(userRequestBody))
                                    .check(status().in(200, 409)) // Expect possible concurrency conflict
                    )
            );

    // =============================
    // 4. Pagination Tests
    // =============================
    ScenarioBuilder paginationTest = scenario("Pagination Performance Test")
            .repeat(3)
            .on(
                    exec(
                            http("Get Users Paginated")
                                    .get(config.getUserBasePath() + "?page=0&size=50")
                                    .check(status().is(200))
                    )
            );

    // =============================
    // 5. Soak Test (1 minute constant load)
    // =============================
    ScenarioBuilder soakTest = scenario("Soak Test - under Constant Load")
            .exec(
                    http("Create User - Soak")
                            .post(config.getUserBasePath())
                            .body(StringBody(userRequestBody))
                            .check(status().is(201))
            );

    // =============================
    // 6. Spike Test
    // =============================
    ScenarioBuilder spikeTest = scenario("Spike Test")
            .exec(
                    http("Spike Create User")
                            .post(config.getUserBasePath())
                            .body(StringBody(userRequestBody))
                            .check(status().is(201))
            );

    // =============================
    // Setup with Multiple Scenarios
    // =============================
    {
        setUp(
                createGetDeleteFlow.injectOpen(atOnceUsers(1)),
                bulkUserCreation.injectOpen(atOnceUsers(2)),
                concurrentUpdates.injectOpen(atOnceUsers(1)),
                paginationTest.injectOpen(constantUsersPerSec(2).during(1)),
                soakTest.injectOpen(constantUsersPerSec(0.25).during(1)),
                spikeTest.injectOpen(atOnceUsers(1))
        )
                .protocols(httpProtocol)
                .assertions(
                        global().successfulRequests().percent().gt(99.0),
                        global().responseTime().max().lt(20000),
                        global().responseTime().mean().lt(5000)
                );
    }
}