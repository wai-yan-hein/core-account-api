package core.acc.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "chart_of_account")
public class ChartOfAccount implements java.io.Serializable {

    @Id
    @Column(name = "coa_code", unique = true, nullable = false)
    private String coaCode;
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
    @Column(name = "comp_code")
    private String compCode;
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

    public ChartOfAccount(String coaCode, String coaNameEng) {
        this.coaCode = coaCode;
        this.coaNameEng = coaNameEng;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ChartOfAccount that = (ChartOfAccount) o;
        return coaCode != null && Objects.equals(coaCode, that.coaCode);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
