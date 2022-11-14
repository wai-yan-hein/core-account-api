package core.acc.api.entity;

import lombok.Data;

@Data
public class VApar {
    private String traderCode;
    private String compCode;
    private String curCode;
    private String coaCode;
    private String userCode;
    private String traderName;
    private Double drAmt;
    private Double crAmt;
}
