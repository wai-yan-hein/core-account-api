package core.acc.api.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Id;

@Data
public class VDescription {
    @Id
    @Column(name = "description")
    private String description;
    @Column(name = "comp_code")
    private String compCode;

}
