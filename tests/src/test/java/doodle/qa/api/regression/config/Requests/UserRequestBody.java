package doodle.qa.api.regression.config.Requests;

import lombok.Builder;
import lombok.Value;
import net.datafaker.Faker;

import java.util.List;
import java.util.UUID;
import java.util.Collections;
import java.util.Locale;

@Value
@Builder(toBuilder = true)
public class UserRequestBody {

    String name;
    String email;

    @Builder.Default
    String locale = "en"; // Default locale is "en"

    @Builder.Default
    List<String> calendarIds = Collections.singletonList(UUID.randomUUID().toString());

    // Custom Builder method to generate Faker defaults based on the locale
    public static UserRequestBodyBuilder withDefaults(String locale) {
        Faker faker = new Faker(new Locale(locale));
        return builder()
                .locale(locale)
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress());
    }

    public static UserRequestBodyBuilder withDefaults() {
        return withDefaults("en");
    }
}
