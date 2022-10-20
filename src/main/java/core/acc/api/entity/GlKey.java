package core.acc.api.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class GlKey implements Serializable {
    @Column(name = "gl_code")
    private String glCode;
    @Column(name = "comp_code")
    private String compCode;

}
