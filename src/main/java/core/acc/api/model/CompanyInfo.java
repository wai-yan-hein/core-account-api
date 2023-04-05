package core.acc.api.model;

import lombok.Data;

import java.util.Date;

@Data
public class CompanyInfo {
    private String compCode;
    private String userCode;
    private String compName;
    private String compAddress;
    private String compPhone;
    private String compEmail;
    private Date startDate;
    private Date endDate;
    private boolean active;
    private String curCode;
    private String exampleCompany;
    private String createdBy;
    private Date createdDate;
}
