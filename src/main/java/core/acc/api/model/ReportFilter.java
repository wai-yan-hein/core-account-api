package core.acc.api.model;

import lombok.Data;

import java.util.List;

@Data
public class ReportFilter {
    private String reportName;
    private String fromDate;
    private String toDate;
    private String openingDate;
    private String closingDate;
    private String desp;
    private String srcAcc;
    private String acc;
    private String curCode;
    private String reference;
    private String refNo;
    private String compCode;
    private String tranSource;
    private String glVouNo;
    private String traderCode;
    private String coaCode;
    private String traderType;
    private String coaLv2;
    private String coaLv1;
    private String incomeExpenseProcess;
    private Integer macId;
    private boolean closing;
    private List<String> listDepartment;
    private String deptCode;
}
