package core.acc.api.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "cur_exchange")
public class CurExchange {
    @EmbeddedId
    private ExchangeKey key;
    @Column(name = "ex_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date exDate;
    @ManyToOne
    @JoinColumn(name = "home_cur")
    private Currency homeCur;
    @ManyToOne
    @JoinColumn(name = "exchange_cur")
    private Currency exCur;
    @Column(name = "remark")
    private String remark;
    @Column(name = "ex_rate")
    private Double exRate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_by")
    private String updatedBy;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "deleted")
    private boolean deleted;
}
