package core.acc.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class ExchangeKey implements Serializable {
    @Column(name = "ex_code")
    private String exCode;
    @Column(name = "comp_code")
    private String compCode;
}
