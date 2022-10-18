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
@Table(name = "v_ref")
public class VRef {
    @Id
    @Column(name = "reference")
    private String reference;
    @Column(name = "comp_code")
    private String compCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        VRef vRef = (VRef) o;
        return reference != null && Objects.equals(reference, vRef.reference);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
