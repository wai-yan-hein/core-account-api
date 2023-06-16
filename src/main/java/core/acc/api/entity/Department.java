package core.acc.api.entity;

import lombok.*;
import org.hibernate.Hibernate;

import jakarta.persistence.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
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
    @Column(name = "created_dt",columnDefinition = "TIMESTAMP")
    private LocalDateTime createdDt;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "updated_dt",columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDt;
    @Column(name = "usr_code")
    private String userCode;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "map_dept_id")
    private Integer mapDeptId;
    @Column(name = "deleted")
    private boolean deleted;
    @Transient
    private List<Department> child;
}

