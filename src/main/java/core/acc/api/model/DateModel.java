package core.acc.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DateModel {
    private String description;
    private String monthName;
    private Integer month;
    private Integer year;
    private String startDate;
    private String endDate;
}
