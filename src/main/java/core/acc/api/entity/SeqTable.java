/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.acc.api.entity;

import lombok.Data;
import jakarta.persistence.*;

/**
 * @author winswe
 */
@Data
@Entity
@Table(name = "seq_table")
public class SeqTable implements java.io.Serializable {
    @EmbeddedId
    private SeqKey key;
    @Column(name = "seq_no")
    private Integer seqNo;

}
