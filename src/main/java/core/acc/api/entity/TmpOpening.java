package core.acc.api.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name="tmp_op_cl")
public class TmpOpening implements java.io.Serializable {
    @EmbeddedId
    private TmpOpeningKey key;
    @Column(name="opening")
    private Double opening;
    @Column(name="dr_amt")
    private Double drAmt;
    @Column(name="cr_amt")
    private Double crAmt;
    @Column(name="closing")
    private Double closing;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TmpOpening that = (TmpOpening) o;
        return key != null && Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
