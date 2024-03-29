package core.acc.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "gl")
public class Gl {
    @EmbeddedId
    private GlKey key;
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
    @Column(name = "batch_no")
    private String batchNo;
    @Column(name = "project_no")
    private String projectNo;
    @Column(name = "patient_no")
    private String patientNo;
    @Column(name = "doctor_id")
    private String doctorId;
    @Column(name = "service_id")
    private String serviceId;
    @Column(name = "ex_code")
    private String exCode;
    @Column(name = "order_id")
    private Integer orderId;
    @Column(name = "qty")
    private Double qty;
    @Column(name = "price")
    private Double price;
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
    private String srcUserCode;
    @Transient
    private String accName;
    @Transient
    private String vouDate;
    @Transient
    private boolean edit;
    @Transient
    private Double opening;
    @Transient
    private Double closing;
    @Transient
    private Double amount;
    @Transient
    private String event;



    public Gl() {
    }
}
