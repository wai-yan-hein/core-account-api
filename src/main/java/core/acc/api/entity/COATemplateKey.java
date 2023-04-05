package core.acc.api.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class COATemplateKey implements Serializable {
    @Column(name = "coa_code")
    private String coaCode;
    @Column(name = "bus_id")
    private Integer busId;
}
