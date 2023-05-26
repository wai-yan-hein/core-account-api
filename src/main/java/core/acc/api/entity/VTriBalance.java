package core.acc.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VTriBalance {
    private String coaCode;
    private String curCode;
    private String compCode;
    private Double drAmt;
    private Double crAmt;
    private String coaName;
    private String coaUsrCode;
    private Integer macId;
}
