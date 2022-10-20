package core.acc.api.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class COAKey implements Serializable {
    @Column(name = "coa_code")
    private String coaCode;
    @Column(name = "comp_code")
    private String compCode;
}
