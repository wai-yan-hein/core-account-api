package core.acc.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Financial {
    private String tranGroup;
    private String coaCode;
    private String coaName;
    private String groupCode;
    private String groupName;
    private String headCode;
    private String headName;
    private String curCode;
    private Double amount;
    private String order;
    private Double totalIncome;
    private Double totalPurchase;
    private Double totalExpense;
    private Double totalCos;
    private Double totalOtherIncome;
    private Double opInv;
    private Double clInv;
    private Double grossProfit;
    private Double netProfit;
    private Double profit;
}
