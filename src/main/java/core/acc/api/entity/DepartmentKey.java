package core.acc.api.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class DepartmentKey implements Serializable {
    @Column(name = "dept_code")
    private String deptCode;
    @Column(name = "comp_code")
    private String compCode;
}
