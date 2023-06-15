package core.acc.api.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;

@Embeddable
@Data
public class COATemplateKey implements Serializable {
    @Column(name = "coa_code")
    private String coaCode;
    @Column(name = "bus_id")
    private Integer busId;
}
