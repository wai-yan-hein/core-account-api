package core.acc.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReturnObject {
    private Double openInv;
    private Double clInv;
    private Double cos;
    private String cosPercent;
    private Double ttlIncome;
    private Double ttlPurchase;
    private Double ttlExpense;
    private Double ttlOtherIncome;
    private Double grossProfit;
    private String gpPercent;
    private Double netProfit;
    private String npPercent;
    private Double ttlFixAss;
    private Double ttlCurAss;
    private Double ttlCapital;
    private Double ttlLia;
    private String status;
    private String message;
    private String errorMessage;
    private List<Object> list;
    private Object data;
    private byte[] file;
    private String vouNo;
    private String tranSource;
    private String compCode;
    private double opAmt;
    private double clAmt;
}
