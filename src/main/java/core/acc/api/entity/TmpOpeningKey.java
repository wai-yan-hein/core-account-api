package core.acc.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class TmpOpeningKey  {
    @Column(name = "coa_code")
    private String coaCode;
    @Column(name = "cur_code")
    private String curCode;
    @Column(name = "mac_id")
    private Integer macId;
}
