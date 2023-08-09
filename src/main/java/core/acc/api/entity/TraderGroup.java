package core.acc.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "trader_group")
public class TraderGroup {
    @EmbeddedId
    private TraderGroupKey key;
    @Column(name = "user_code")
    private String userCode;
    @Column(name = "group_name")
    private String groupName;
}
