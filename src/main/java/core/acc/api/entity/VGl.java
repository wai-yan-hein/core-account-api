package core.acc.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VGl implements java.io.Serializable {
    private String glCode;
    private String formatDate;
    private Date glDate;
    private String description;
    private String sourceAcId;
    private String accCode;
    private String curCode;
    private Double drAmt;
    private Double crAmt;
    private String reference;
    private String deptCode;
    private String vouNo;
    private String traderCode;
    private String compCode;
    private String tranSource;
    private String srcAccName;
    private String accName;
    private String deptUsrCode;
    private String traderName;
    private String traderType;
    private String glVouNo;
    private String remark;
    private String refNo;

}
