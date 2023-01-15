package core.acc.api.dao;


import core.acc.api.common.Util1;
import core.acc.api.entity.Gl;
import core.acc.api.entity.GlKey;
import core.acc.api.entity.VDescription;
import core.acc.api.entity.VRef;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
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
    public Gl findWithSql(GlKey key) {
        try {
            String sql = "select * from gl where gl_code='" + key.getGlCode() + "' and comp_code ='" + key.getCompCode() + "'";
            ResultSet rs = getResultSet(sql);
            if (rs != null) {
                if (rs.next()) {
                    Gl gl = new Gl();
                    gl.setKey(key);
                    gl.setGlDate(rs.getDate("gl_date"));
                    gl.setCreatedDate(rs.getDate("created_date"));
                    gl.setModifyDate(rs.getDate("modify_date"));
                    gl.setModifyBy(rs.getString("modify_by"));
                    gl.setDescription(rs.getString("description"));
                    gl.setSrcAccCode(rs.getString("source_ac_id"));
                    gl.setAccCode(rs.getString("account_id"));
                    gl.setCurCode(rs.getString("cur_code"));
                    gl.setDrAmt(rs.getDouble("dr_amt"));
                    gl.setCrAmt(rs.getDouble("cr_amt"));
                    gl.setReference(rs.getString("reference"));
                    gl.setDeptCode(rs.getString("dept_code"));
                    gl.setVouNo(rs.getString("voucher_no"));
                    gl.setTraderCode(rs.getString("trader_code"));
                    gl.setTranSource(rs.getString("tran_source"));
                    gl.setGlVouNo(rs.getString("gl_vou_no"));
                    gl.setRemark(rs.getString("remark"));
                    gl.setRefNo(rs.getString("ref_no"));
                    gl.setMacId(rs.getInt("mac_id"));
                    return gl;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Override
    public boolean delete(GlKey key, String modifyBy) {
        String sql = "update gl\n" +
                "set deleted =1,modify_by ='" + modifyBy + "'\n" +
                " where gl_code = '" + key.getGlCode() + "'\n" +
                " and comp_code ='" + key.getCompCode() + "'\n" +
                " and dept_id =" + key.getDeptId() + "";
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
                "and deleted =0\n" +
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
                "and deleted =0\n" +
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
                "and deleted =0\n" +
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
    public List<Gl> searchVoucher(String fromDate, String toDate, String vouNo, String description, String reference, String refNo, String compCode, Integer macId) {
        List<Gl> list = new ArrayList<>();
        String filter = " (tran_source ='DR' or tran_source ='CR')\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and deleted =0\n" +
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
        if (!refNo.equals("-")) {
            filter += "and ref_no like '" + refNo + "%'\n";
        }
        String sql = "select gl_date,description,reference,ref_no,gl_vou_no,tran_source,sum(dr_amt) dr_amt,sum(cr_amt) cr_amt\n" +
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
                    g.setRefNo(rs.getString("ref_no"));
                    g.setGlVouNo(rs.getString("gl_vou_no"));
                    g.setDrAmt(rs.getDouble("dr_amt"));
                    g.setCrAmt(rs.getDouble("cr_amt"));
                    g.setTranSource(rs.getString("tran_source"));
                    list.add(g);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return list;
    }

    @Override
    public boolean deleteVoucher(String glVouNo, String compCode) {
        String sql = "delete from gl where gl_vou_no ='" + glVouNo + "' and comp_code ='" + compCode + "'";
        execSql(sql);
        return true;
    }

    @Override
    public void deleteGl(String vouNo, String tranSource, String srcAcc) {
        String sql = "delete from gl where ref_no ='" + vouNo + "' and tran_source='" + tranSource + "' and source_ac_id ='" + srcAcc + "'";
        execSql(sql);
    }

    @Override
    public List<Gl> getJournal(String glVouNo, String compCode) {
        List<Gl> list = new ArrayList<>();
        String sql = "select g.dept_id,g.gl_code,g.dept_code,g.cur_code,g.trader_code,g.gl_date,g.source_ac_id,g.gl_vou_no,g.description,g.reference,g.dr_amt,g.cr_amt,\n" +
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
                    key.setDeptId(rs.getInt("dept_id"));
                    g.setKey(key);
                    g.setGlDate(rs.getDate("gl_date"));
                    g.setGlDateStr(Util1.toDateStr(g.getGlDate(), "dd/MM/yyyy"));
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

    @Override
    public List<Gl> getVoucher(String glVouNo, String compCode) {
        List<Gl> list = new ArrayList<>();
        String sql = "select g.dept_id,g.gl_code,g.dept_code,g.cur_code,g.trader_code,\n" +
                "g.gl_date,g.source_ac_id,g.account_id,g.gl_vou_no,g.description,g.reference,g.ref_no,g.dr_amt,g.cr_amt,\n" +
                "t.user_code t_user_code,t.trader_name,g.tran_source,\n" +
                "d.usr_code d_user_code,coa.coa_name_eng\n" +
                "from gl g\n" +
                "join department d on g.dept_code = d.dept_code\n" +
                "and g.comp_code = d.comp_code\n" +
                "left join trader t on g.trader_code = t.code\n" +
                "and g.comp_code = t.comp_code\n" +
                "join chart_of_account coa on g.account_id = coa.coa_code\n" +
                "and g.comp_code = coa.comp_code\n" +
                "where g.comp_code ='" + compCode + "'\n" +
                "and g.gl_vou_no ='" + glVouNo + "'\n" +
                "and (g.tran_source ='DR' or g.tran_source='CR')\n" +
                "order by g.gl_code";
        try {
            ResultSet rs = getResultSet(sql);
            if (rs != null) {
                while (rs.next()) {
                    Gl g = new Gl();
                    GlKey key = new GlKey();
                    key.setGlCode(rs.getString("gl_code"));
                    key.setCompCode(compCode);
                    key.setDeptId(rs.getInt("dept_id"));
                    g.setKey(key);
                    g.setGlDate(rs.getDate("gl_date"));
                    g.setGlDateStr(Util1.toDateStr(g.getGlDate(), "dd/MM/yyyy"));
                    g.setDescription(rs.getString("description"));
                    g.setReference(rs.getString("reference"));
                    g.setRefNo(rs.getString("ref_no"));
                    g.setGlVouNo(rs.getString("gl_vou_no"));
                    g.setDrAmt(rs.getDouble("dr_amt"));
                    g.setCrAmt(rs.getDouble("cr_amt"));
                    g.setDeptCode(rs.getString("dept_code"));
                    g.setDeptUsrCode(rs.getString("d_user_code"));
                    g.setTraderCode(rs.getString("trader_code"));
                    g.setTraderName(rs.getString("trader_name"));
                    g.setAccCode(rs.getString("account_id"));
                    g.setAccName(rs.getString("coa_name_eng"));
                    g.setSrcAccCode(rs.getString("source_ac_id"));
                    g.setTranSource(rs.getString("tran_source"));
                    g.setCurCode(rs.getString("cur_code"));
                    list.add(g);
                }
            }
        } catch (Exception e) {
            log.error("getVoucher : " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Gl> getTranSource(String compCode) {
        List<Gl> list = new ArrayList<>();
        String sql = "select distinct tran_source from gl where comp_code='" + compCode + "'";
        ResultSet rs = getResultSet(sql);
        try {
            if (rs != null) {
                while (rs.next()) {
                    Gl g = new Gl();
                    g.setTranSource(rs.getString("tran_source"));
                    list.add(g);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return list;
    }

    @Override
    public List<Gl> unUpload(String syncDate) {
        String hql = "select o from Gl o where o.intgUpdStatus is null and date(o.glDate) >= '" + syncDate + "'";
        return findHSQL(hql);
    }

    @Override
    public Date getMaxDate() {
        String sql = "select max(modify_date) date from gl";
        ResultSet rs = getResultSet(sql);
        try {
            if (rs.next()) {
                Date date = rs.getTimestamp("date");
                if (date != null) {
                    return date;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return Util1.getSyncDate();
    }

    @Override
    public List<Gl> search(String updatedDate, String deptCode) {
        List<Gl> list = new ArrayList<>();
        String sql = "select * from gl o where o.dept_code='" + deptCode + "' and o.modify_date > '" + updatedDate + "'";
        ResultSet rs = getResultSet(sql);
        if (rs != null) {
            try {
                //gl_code, gl_date, created_date, modify_date, modify_by, description, source_ac_id,
                // account_id, cur_code, dr_amt, cr_amt, reference, dept_code, voucher_no, user_code,
                // trader_code, comp_code, tran_source, gl_vou_no, split_id, intg_upd_status, remark, naration, ref_no, mac_id, exchange_id
                while (rs.next()) {
                    Gl gl = new Gl();
                    GlKey key = new GlKey();
                    key.setCompCode(rs.getString("comp_code"));
                    key.setGlCode(rs.getString("gl_code"));
                    gl.setKey(key);
                    gl.setGlDate(rs.getDate("gl_date"));
                    gl.setCreatedDate(rs.getTimestamp("created_date"));
                    gl.setModifyDate(rs.getTimestamp("modify_date"));
                    gl.setModifyBy(rs.getString("modify_by"));
                    gl.setDescription(rs.getString("description"));
                    gl.setSrcAccCode(rs.getString("source_ac_id"));
                    gl.setAccCode(rs.getString("account_id"));
                    gl.setCurCode(rs.getString("cur_code"));
                    gl.setDrAmt(rs.getDouble("dr_amt"));
                    gl.setCrAmt(rs.getDouble("cr_amt"));
                    gl.setReference(rs.getString("reference"));
                    gl.setDeptCode(rs.getString("dept_code"));
                    gl.setVouNo(rs.getString("voucher_no"));
                    gl.setCreatedBy(rs.getString("user_code"));
                    gl.setTraderCode(rs.getString("trader_code"));
                    gl.setTranSource(rs.getString("tran_source"));
                    gl.setGlVouNo(rs.getString("gl_vou_no"));
                    gl.setIntgUpdStatus(rs.getString("intg_upd_status"));
                    gl.setRemark(rs.getString("remark"));
                    gl.setRefNo(rs.getString("ref_no"));
                    gl.setMacId(rs.getInt("mac_id"));
                    list.add(gl);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }


        return list;
    }

    @Override
    public void truncate(GlKey key) {
        String sql = "delete from gl where gl_code ='" + key.getCompCode() + "' and comp_code ='" + key.getGlCode() + "'";
        execSql(sql);
    }
}
