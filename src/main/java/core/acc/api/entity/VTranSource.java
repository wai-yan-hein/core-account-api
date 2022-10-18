package core.acc.api.entity;

import lombok.*;
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
@Table(name = "v_tran_source")
public class VTranSource {
    @Id
    @Column(name = "tran_source")
    private String tranSource;
    @Column(name = "comp_code")
    private String compCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        VTranSource that = (VTranSource) o;
        return tranSource != null && Objects.equals(tranSource, that.tranSource);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
