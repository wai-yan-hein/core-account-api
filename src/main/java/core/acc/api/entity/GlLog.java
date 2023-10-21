package core.acc.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Entity
@Table(name = "gl_log")
public class GlLog {

    @EmbeddedId
    private GlLogKey key;
    @Column(name = "log_user_code")
    private String logUser;
    @Column(name = "log_mac_id")
    private Integer logMac;
    @Column(name = "log_status")
    private String logStatus;
    @Column(name = "log_date",columnDefinition = "TIMESTAMP")
    private LocalDateTime logDate;
    @Column(name = "gl_code")
    private String glCode;
    @Column(name = "gl_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime glDate;
    @Column(name = "description")
    private String description;
    @Column(name = "source_ac_id")
    private String srcAccCode;
    @Column(name = "account_id")
    private String accCode;
    @Column(name = "cur_code")
    private String curCode;
    @Column(name = "dr_amt")
    private Double drAmt;
    @Column(name = "cr_amt")
    private Double crAmt;
    @Column(name = "reference")
    private String reference;
    @Column(name = "dept_code")
    private String deptCode;
    @Column(name = "voucher_no")
    private String vouNo;
    @Column(name = "trader_code")
    private String traderCode;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdDate;
    @Column(name = "modify_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime modifyDate;
    @Column(name = "modify_by")
    private String modifyBy;
    @Column(name = "user_code")
    private String createdBy;
    @Column(name = "tran_source")
    private String tranSource;
    @Column(name = "gl_vou_no")
    private String glVouNo; //For general voucher system id
    @Column(name = "remark")
    private String remark;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "ref_no")
    private String refNo;
}
