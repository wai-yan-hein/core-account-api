package core.acc.api.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;

@Embeddable
@Data
public class DepartmentKey implements Serializable {
    @Column(name = "dept_code")
    private String deptCode;
    @Column(name = "comp_code")
    private String compCode;
}
