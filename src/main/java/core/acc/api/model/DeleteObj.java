package core.acc.api.model;

import lombok.Data;

@Data
public class DeleteObj {
    private String glVouNo;
    private String compCode;
    private String glCode;
    private Integer deptId;
    private String modifyBy;
}
