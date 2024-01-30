package core.acc.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class OpeningKey implements Serializable {
    @Column(name = "coa_op_id")
    private String coaOpId;
    @Column(name = "comp_code")
    private String compCode;
}
