package core.acc.api.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
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
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "log_date")
    private Date logDate;
    @Column(name = "gl_code")
    private String glCode;
    @Temporal(TemporalType.DATE)
    @Column(name = "gl_date")
    private Date glDate;
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
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_date")
    private Date modifyDate;
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
