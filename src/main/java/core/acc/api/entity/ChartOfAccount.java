package core.acc.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "chart_of_account")
public class ChartOfAccount implements java.io.Serializable {

   @EmbeddedId
    private COAKey key;
    @Column(name = "coa_name_eng")
    private String coaNameEng;
    @Column(name = "coa_name_mya")
    private String coaNameMya;
    @Column(name = "active")
    private boolean active;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_date")
    private Date modifiedDate;
    @Column(name = "created_by", length = 15)
    private String createdBy;
    @Column(name = "updated_by", length = 15)
    private String modifiedBy;
    @Column(name = "coa_parent")
    private String coaParent;
    @Column(name = "coa_option", length = 5)
    private String option;
    @Column(name = "coa_level")
    private Integer coaLevel;
    @Column(name = "coa_code_usr")
    private String coaCodeUsr;
    @Column(name = "parent_usr_code")
    private String parentUsrCode;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "marked")
    private boolean marked;
    @Column(name = "cur_code")
    private String curCode;

    @Transient
    private List<ChartOfAccount> child;
}
