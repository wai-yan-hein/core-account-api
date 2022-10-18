package core.acc.api.entity;

import lombok.Data;

@Data
public class VTriBalance {
    private String coaCode;
    private String curCode;
    private String compCode;
    private Double drAmt;
    private Double crAmt;
    private String coaName;
    private String usrCoaCode;
    private Integer macId;
}
