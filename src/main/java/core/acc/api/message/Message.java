package core.acc.api.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message {
    private String header;
    private String entity;
    private String message;
}
