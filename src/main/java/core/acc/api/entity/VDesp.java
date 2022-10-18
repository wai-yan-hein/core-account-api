package core.acc.api.entity;

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
@Entity
@Table(name = "v_desp")
public class VDesp {
    @Id
    @Column(name = "description")
    private String description;
    @Column(name = "comp_code")
    private String compCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        VDesp vDesp = (VDesp) o;
        return description != null && Objects.equals(description, vDesp.description);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
