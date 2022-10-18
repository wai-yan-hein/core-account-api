package core.acc.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "v_coa_lv3")
public class VCOALv3 {

    @Id
    @Column(name = "coa_code")
    private String coaCode;
    @Column(name = "coa_code_usr")
    private String coaUsrCode;
    @Column(name = "coa_name_eng")
    private String coaNameEng;
    @Column(name = "coa_code_2")
    private String coaCodeParent2;
    @Column(name = "coa_code_usr_2")
    private String coaUsrCodeParent2;
    @Column(name = "coa_name_eng_2")
    private String coaNameEngParent2;
    @Column(name = "coa_code_3")
    private String coaCodeParent3;
    @Column(name = "coa_code_usr_3")
    private String coaUsrCodeParent3;
    @Column(name = "coa_name_eng_3")
    private String coaNameEngParent3;
    @Column(name = "comp_code")
    private String compCode;
    @Column(name = "cur_code")
    private String curCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        VCOALv3 vcoaLv3 = (VCOALv3) o;
        return coaCode != null && Objects.equals(coaCode, vcoaLv3.coaCode);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
