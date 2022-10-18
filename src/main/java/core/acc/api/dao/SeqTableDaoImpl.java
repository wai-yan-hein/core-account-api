/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.acc.api.dao;

import core.acc.api.entity.SeqKey;
import core.acc.api.entity.SeqTable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author winswe
 */
@Repository
public class SeqTableDaoImpl extends AbstractDao<SeqKey, SeqTable> implements SeqTableDao {

    @Override
    public SeqTable save(SeqTable st) {
        persist(st);
        return st;
    }

    @Override
    public SeqTable findById(SeqKey id) {
        return getByKey(id);
    }

    @Override
    public List<SeqTable> search(String option, String period, String compCode) {
        String strSql = "select o from SeqTable o where o.seqOption = '" + option
                + "' and o.compCode = '" + compCode + "'";

        if (!period.equals("-")) {
            strSql = strSql + " and o.period = '" + period + "'";
        }

        return (List<SeqTable>) findHSQL(strSql);
    }

    @Override
    public SeqTable getSeqTable(String option, String period, String compCode) {
        List<SeqTable> listST = search(option, period, compCode);
        SeqTable st = null;

        if (listST != null) {
            if (!listST.isEmpty()) {
                st = listST.get(0);
            }
        }

        return st;
    }

    @Override
    public int delete(Integer id) {
        String strSql = "delete from SeqTable o where o.id = " + id;
        return execUpdateOrDelete(strSql);
    }

    @Override
    public int getSequence(Integer macId, String option, String period, String compCode) {
        SeqKey key = new SeqKey();
        key.setCompCode(compCode);
        key.setMacId(macId);
        key.setPeriod(period);
        key.setSeqOption(option);
        SeqTable st = findById(key);
        if (st == null) {
            st = new SeqTable();
            st.setKey(key);
            st.setSeqNo(1);
        } else {
            st.setSeqNo(st.getSeqNo() + 1);
        }
        save(st);
        return st.getSeqNo();
    }

    @Override
    public List<SeqTable> findAll() {
        String strSql = "select o from SeqTable o";
        return findHSQL(strSql);
    }
}
