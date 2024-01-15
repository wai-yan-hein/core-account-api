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
    private String coaLv3;
    private String invGroup;
    private Integer macId;
    private boolean closing;
    private List<String> listDepartment;
    private String deptCode;
    private boolean summary;
    private String cashGroup;
    private String batchNo;
    private String fixedAcc;
    private String currentAcc;
    private String capitalAcc;
    private String liaAcc;
    private String incomeAcc;
    private String otherIncomeAcc;
    private String purchaseAcc;
    private String expenseAcc;
    private String plAcc;
    private String reAcc;
    private String projectNo;
    private List<String> listCOAGroup;
}
