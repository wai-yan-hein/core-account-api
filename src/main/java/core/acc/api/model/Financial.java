package core.acc.api.model;

import lombok.Data;

@Data
public class Financial {
    private String tranGroup;
    private String coaCode;
    private String coaName;
    private String groupCode;
    private String groupName;
    private String headCode;
    private String headName;
    private String curCode;
    private double amount;
    private double totalIncome;
    private double totalExpense;
}
