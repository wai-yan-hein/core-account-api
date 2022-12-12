package core.acc.api.model;

import lombok.Data;

@Data
public class DepartmentUser {
    private Integer deptId;
    private String userCode;
    private String deptName;
    private String inventoryQ;
    private String accountQ;


}
