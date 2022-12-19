package core.acc.api.entity;

import lombok.Data;

@Data
public class TmpOpening {
    private TmpOpeningKey key;
    private Double opening;
    private Double drAmt;
    private Double crAmt;
    private Double closing;


}
