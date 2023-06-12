package core.acc.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Notification {
    private String title;
    private String description;
    private String type;
    private String tranSource;
    private String action;
}
