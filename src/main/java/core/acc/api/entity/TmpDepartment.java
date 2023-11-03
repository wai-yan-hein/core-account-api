package core.acc.api.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tmp_dep_filter")
public class TmpDepartment {
    @EmbeddedId
    private TmpDepartmentKey key;
}
