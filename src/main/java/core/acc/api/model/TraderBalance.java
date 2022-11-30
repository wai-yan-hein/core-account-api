package core.acc.api.model;

import lombok.Data;

@Data
public class TraderBalance {
    private String traderCode;
    private String traderName;
    private String tranDate;
    private String vouNo;
    private String remark;
    private Double opening;
    private Double drAmt;
    private Double crAmt;
    private Double closing;
}
