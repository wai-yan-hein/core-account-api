package core.acc.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "coa_template")
public class COATemplate {

    @EmbeddedId
    private COATemplateKey key;
    @Column(name = "coa_name_eng")
    private String coaNameEng;
    @Column(name = "coa_name_mya")
    private String coaNameMya;
    @Column(name = "active")
    private boolean active;
    @Column(name = "coa_parent")
    private String coaParent;
    @Column(name = "coa_level")
    private Integer coaLevel;
    @Column(name = "coa_code_usr")
    private String coaCodeUsr;
    @Column(name = "cur_code")
    private String curCode;
    @Column(name = "dept_code")
    private String deptCode;
    @Column(name = "credit")
    private boolean credit;

}
