package core.acc.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class COAKey implements Serializable {
    @Column(name = "coa_code")
    private String coaCode;
    @Column(name = "comp_code")
    private String compCode;
}
