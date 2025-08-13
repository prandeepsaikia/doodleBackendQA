package doodle.qa.com.svcuserqa.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Plain POJO representation of a User for test JSON (de)serialization.
 */
@Data
public class User {
    private UUID id;
    private String name;
    private String email;
    private List<UUID> calendarIds;
}
