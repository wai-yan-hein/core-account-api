package core.acc.api.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "stock_op_value")
public class StockOP {
    @EmbeddedId
    private StockOPKey key;
    @Temporal(TemporalType.DATE)
    @Column(name = "tran_date")
    private Date tranDate;
    @Column(name = "coa_code")
    private String coaCode;
    @Column(name = "dept_code")
    private String deptCode;
    @Column(name = "curr_code")
    private String curCode;
    @Column(name = "remark")
    private String remark;
    @Column(name = "amount")
    private Double clAmt;
    @Column(name = "created_by")
    private String createdBy;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "updated_by")
    private String updatedBy;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "project_no")
    private String projectNo;
    @Transient
    private String coaCodeUser;
    @Transient
    private String coaNameEng;
    @Transient
    private String deptUsrCode;
}
