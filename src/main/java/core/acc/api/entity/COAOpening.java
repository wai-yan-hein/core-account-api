package core.acc.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(name = "coa_opening")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class COAOpening {
    @EmbeddedId
    private OpeningKey key;
    @Temporal(TemporalType.DATE)
    @Column(name = "op_date")
    private Date opDate;
    @Column(name = "source_acc_id")
    private String sourceAccId;
    @Column(name = "cur_code")
    private String curCode;
    @Column(name = "cr_amt")
    private Double crAmt;
    @Column(name = "dr_amt")
    private Double drAmt;
    @Column(name = "user_code")
    private String userCode;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdDate;
    @Column(name = "dept_code")
    private String deptCode;
    @Column(name = "trader_code")
    private String traderCode;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "project_no")
    private String projectNo;
    @Transient
    private String coaUsrCode;
    @Transient
    private String srcAccName;
    @Transient
    private String traderName;
    @Transient
    private String traderUsrCode;
    @Transient
    private String deptUsrCode;
}
