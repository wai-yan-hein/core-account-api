package core.acc.api.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;

@Data
public class VTranSource {
    @Id
    @Column(name = "tran_source")
    private String tranSource;
    @Column(name = "comp_code")
    private String compCode;
}
