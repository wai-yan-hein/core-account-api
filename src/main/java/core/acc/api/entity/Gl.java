package core.acc.api.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "gl")
public class Gl {
    @EmbeddedId
    private GlKey key;
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
    private String glVouNo;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Column(name = "remark")
    private String remark;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "ref_no")
    private String refNo;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "from_des")
    private String fromDes;
    @Column(name = "for_des")
    private String forDes;
    @Column(name = "narration")
    private String narration;
    @Transient
    private String glDateStr;
    @Transient
    private List<GlKey> delList;
    @Transient
    private boolean cash = false;
    @Transient
    private String deptUsrCode;
    @Transient
    private String traderName;
    @Transient
    private String srcAccName;
    @Transient
    private String accName;
    @Transient
    private String vouDate;
    @Transient
    private boolean edit;
    @Transient
    private double opening;
    @Transient
    private double closing;
    @Transient
    private double amount;


    public Gl(Date modifyDate, String deptCode) {
        this.modifyDate = modifyDate;
        this.deptCode = deptCode;
    }

    public Gl() {
    }
}
