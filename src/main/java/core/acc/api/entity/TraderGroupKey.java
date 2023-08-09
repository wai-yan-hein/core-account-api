package core.acc.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class TraderGroupKey implements Serializable {
    @Column(name = "group_code")
    private String groupCode;
    @Column(name = "comp_code")
    private String compCode;
}
