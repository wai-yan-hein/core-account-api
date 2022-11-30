package core.acc.api.dao;


import core.acc.api.entity.Gl;
import core.acc.api.entity.GlKey;
import core.acc.api.entity.VDescription;
import core.acc.api.entity.VRef;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class GlDaoImpl extends AbstractDao<GlKey, Gl> implements GlDao {

    @Override
    public Gl save(Gl gl) throws Exception {
        persist(gl);
        return gl;
    }

    @Override
    public Gl findByCode(GlKey key) {
        return getByKey(key);
    }

    @Override
    public boolean delete(GlKey key) {
        String sql = "delete from gl where gl_code = '" + key.getGlCode() + "' and comp_code ='" + key.getCompCode() + "'";
        execSql(sql);
        return true;
    }


    @Override
    public void deleteGl(String vouNo, String tranSource) {
        String sql = "delete from gl where ref_no ='" + vouNo + "' and tran_source='" + tranSource + "'";
        execSql(sql);
    }

    @Override
    public List<VDescription> getDescription(String str, String compCode) {
        List<VDescription> list = new ArrayList<>();
        String sql = "select distinct description\n" +
                "from gl\n" +
                "where comp_code ='" + compCode + "'\n" +
                "and (description like '" + str + "%')\n" +
                "limit 20";
        try {
            ResultSet rs = getResultSet(sql);
            while (rs.next()) {
                VDescription v = new VDescription();
                v.setDescription(rs.getString("description"));
                list.add(v);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return list;
    }

    @Override
    public List<VRef> getReference(String str, String compCode) {
        List<VRef> list = new ArrayList<>();
        String sql = "select distinct reference\n" +
                "from gl\n" +
                "where comp_code ='" + compCode + "'\n" +
                "and (reference like '" + str + "%')\n" +
                "limit 20";
        try {
            ResultSet rs = getResultSet(sql);
            while (rs.next()) {
                VRef v = new VRef();
                v.setReference(rs.getString("reference"));
                list.add(v);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return list;
    }

    @Override
    public List<Gl> searchJournal(String fromDate, String toDate, String vouNo, String description, String reference, String compCode, Integer macId) {
        List<Gl> list = new ArrayList<>();
        String filter = " tran_source ='GV'\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and date(gl_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n";
        if (!vouNo.equals("-")) {
            filter += "and gl_vou_no ='" + vouNo + "'\n";
        }
        if (!description.equals("-")) {
            filter += "and description like '" + description + "%'\n";
        }
        if (!reference.equals("-")) {
            filter += "and reference like '" + reference + "%'\n";
        }
        String sql = "select gl_date,description,reference,gl_vou_no,sum(dr_amt) amount\n" +
                "from gl\n" +
                "where " + filter + "" +
                "group by gl_vou_no\n" +
                "order by gl_date";
        try {
            ResultSet rs = getResultSet(sql);
            if (rs != null) {
                while (rs.next()) {
                    Gl g = new Gl();
                    g.setGlDate(rs.getDate("gl_date"));
                    g.setDescription(rs.getString("description"));
                    g.setReference(rs.getString("reference"));
                    g.setGlVouNo(rs.getString("gl_vou_no"));
                    g.setDrAmt(rs.getDouble("amount"));
                    list.add(g);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return list;
    }

    @Override
    public List<Gl> getJournal(String glVouNo, String compCode) {
        List<Gl> list = new ArrayList<>();
        String sql = "select g.gl_code,g.dept_code,g.cur_code,g.trader_code,g.gl_date,g.source_ac_id,g.gl_vou_no,g.description,g.reference,g.dr_amt,g.cr_amt,\n" +
                "t.user_code t_user_code,t.trader_name,g.tran_source,\n" +
                "d.usr_code d_user_code,coa.coa_name_eng\n" +
                "from gl g\n" +
                "join department d on g.dept_code = d.dept_code\n" +
                "and g.comp_code = d.comp_code\n" +
                "left join trader t on g.trader_code = t.code\n" +
                "and g.comp_code = t.comp_code\n" +
                "join chart_of_account coa on g.source_ac_id = coa.coa_code\n" +
                "and g.comp_code = coa.comp_code\n" +
                "where g.comp_code ='" + compCode + "'\n" +
                "and g.gl_vou_no ='" + glVouNo + "'\n" +
                "and g.tran_source ='GV'\n" +
                "order by g.gl_code";
        try {
            ResultSet rs = getResultSet(sql);
            if (rs != null) {
                while (rs.next()) {
                    Gl g = new Gl();
                    GlKey key = new GlKey();
                    key.setGlCode(rs.getString("gl_code"));
                    key.setCompCode(compCode);
                    g.setKey(key);
                    g.setGlDate(rs.getDate("gl_date"));
                    g.setDescription(rs.getString("description"));
                    g.setReference(rs.getString("reference"));
                    g.setGlVouNo(rs.getString("gl_vou_no"));
                    g.setDrAmt(rs.getDouble("dr_amt"));
                    g.setCrAmt(rs.getDouble("cr_amt"));
                    g.setDeptCode(rs.getString("dept_code"));
                    g.setDeptUsrCode(rs.getString("d_user_code"));
                    g.setTraderCode(rs.getString("trader_code"));
                    g.setTraderName(rs.getString("trader_name"));
                    g.setSrcAccName(rs.getString("coa_name_eng"));
                    g.setSrcAccCode(rs.getString("source_ac_id"));
                    g.setTranSource(rs.getString("tran_source"));
                    g.setCurCode(rs.getString("cur_code"));
                    list.add(g);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return list;
    }
}
