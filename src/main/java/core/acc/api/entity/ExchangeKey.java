package core.acc.api.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;

@Data
@Embeddable
public class ExchangeKey implements Serializable {
    @Column(name = "ex_code")
    private String exCode;
    @Column(name = "comp_code")
    private String compCode;
}
