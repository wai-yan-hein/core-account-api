package core.acc.api.entity;

import lombok.Data;

@Data
public class VApar {
    private VAparKey key;
    private String userCode;
    private String traderName;
    private Double drAmt;
    private Double crAmt;
}
