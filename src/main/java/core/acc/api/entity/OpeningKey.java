package core.acc.api.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;

@Embeddable
@Data
public class OpeningKey implements Serializable {
    @Column(name = "coa_op_id")
    private String opId;
    @Column(name = "comp_code")
    private String compCode;
}
