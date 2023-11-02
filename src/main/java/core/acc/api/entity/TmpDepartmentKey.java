package core.acc.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class TmpDepartmentKey implements Serializable {
    @Column(name = "dept_code")
    private String deptCode;
    @Column(name = "mac_id")
    private int macId;
}
