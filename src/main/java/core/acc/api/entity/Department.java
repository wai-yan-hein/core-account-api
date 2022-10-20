package core.acc.api.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Data
@Entity
@Table(name = "department")
public class Department implements java.io.Serializable {

    @EmbeddedId
    private DepartmentKey key;
    @Column(name = "dept_name")
    private String deptName;
    @Column(name = "parent_dept")
    private String parentDept;
    @Column(name = "active")
    private boolean active;
    @Column(name = "created_by")
    private String createdBy;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_dt")
    private Date createdDt;
    @Column(name = "updated_by")
    private String updatedBy;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_dt")
    private Date updatedDt;
    @Column(name = "usr_code")
    private String userCode;
    @Column(name = "mac_id")
    private Integer macId;
    @Transient
    private List<Department> child;
}

