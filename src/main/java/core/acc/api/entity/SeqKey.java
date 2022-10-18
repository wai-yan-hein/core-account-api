/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.acc.api.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author SAI
 */
@Data
@Embeddable
public class SeqKey implements Serializable {
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "seq_option")
    private String seqOption;
    @Column(name = "period")
    private String period;
    @Column(name = "comp_code")
    private String compCode;
}
