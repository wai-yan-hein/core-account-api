package core.acc.api.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Data
@Embeddable
public class TmpOpeningKey implements java.io.Serializable {
    @Column(name = "coa_code")
    private String coaCode;
    @Column(name = "cur_code")
    private String curCode;
    @Column(name = "mac_id")
    private Integer macId;
}
