package doodle.qa.com.svcproviderqa.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Plain POJO representation of a Calendar for test JSON (de)serialization.
 */
@Data
public class Calendar {
    private UUID id;
    private String name;
    private String description;
}
