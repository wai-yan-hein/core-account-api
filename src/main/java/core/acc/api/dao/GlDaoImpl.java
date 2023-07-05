package core.acc.api.dao;


import core.acc.api.common.Util1;
import core.acc.api.entity.Gl;
import core.acc.api.entity.GlKey;
import core.acc.api.model.VDescription;
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
    public Gl save(Gl gl) {
        saveOrUpdate(gl, gl.getKey());
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
            ResultSet rs = getResult(sql);
            if (rs.next()) {
                Gl gl = new Gl();
                gl.setKey(key);
                gl.setGlDate(rs.getTimestamp("gl_date").toLocalDateTime());
                gl.setCreatedDate(rs.getTimestamp("created_date").toLocalDateTime());
                gl.setModifyDate(rs.getTimestamp("modify_date").toLocalDateTime());
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
                gl.setProjectNo(rs.getString("project_no"));
                gl.setBatchNo(rs.getString("batch_no"));
                return gl;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Override
    public boolean delete(GlKey key, String modifyBy) {
        String sql = "update gl\n" + " set deleted = true,intg_upd_status = null,modify_by ='" + modifyBy + "'\n" + " where gl_code = '" + key.getGlCode() + "'\n" + " and comp_code ='" + key.getCompCode() + "'\n" + " and dept_id =" + key.getDeptId();
        execSql(sql);
        return true;
    }


    @Override
    public void deleteGl(String vouNo, String tranSource) {
        String sql = "update  gl set deleted = true,intg_upd_status = null where ref_no ='" + vouNo + "' and tran_source='" + tranSource + "'";
        execSql(sql);
    }

    @Override
    public List<VDescription> getDescription(String str, String compCode) {
        List<VDescription> list = new ArrayList<>();
        String sql = """
                select distinct description
                from gl
                where comp_code =?
                and (description like ?)
                and deleted = false
                limit 20""";
        try {
            ResultSet rs = getResult(sql, compCode, str + "%");
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
    public List<VDescription> getReference(String str, String compCode) {
        List<VDescription> list = new ArrayList<>();
        String sql = """
                select distinct reference
                from gl
                where comp_code =?
                and (reference like ?)
                and deleted = false
                limit 20""";
        try {
            ResultSet rs = getResult(sql, compCode, str + "%");
            while (rs.next()) {
                VDescription v = new VDescription();
                v.setDescription(rs.getString("reference"));
                list.add(v);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return list;
    }

    @Override
    public List<VDescription> getBatchNo(String str, String compCode) {
        List<VDescription> list = new ArrayList<>();
        String sql = """
                select distinct batch_no
                from gl
                where comp_code =?
                and (batch_no like ?)
                and deleted = false
                limit 20""";
        try {
            ResultSet rs = getResult(sql, compCode, str + "%");
            while (rs.next()) {
                VDescription v = new VDescription();
                v.setDescription(rs.getString("batch_no"));
                list.add(v);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return list;
    }

    @Override
    public List<Gl> searchJournal(String fromDate, String toDate, String vouNo, String description, String reference, String coaCode, String projectNo, String compCode, Integer macId) {
        List<Gl> list = new ArrayList<>();
        String filter = " tran_source ='GV'\n" + "and comp_code ='" + compCode + "'\n" + "and deleted = false\n" + "and date(gl_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n";
        if (!vouNo.equals("-")) {
            filter += "and gl_vou_no ='" + vouNo + "'\n";
        }
        if (!description.equals("-")) {
            filter += "and description like '" + description + "%'\n";
        }
        if (!reference.equals("-")) {
            filter += "and reference like '" + reference + "%'\n";
        }
        if (!coaCode.equals("-")) {
            filter += "and (source_ac_id = '" + coaCode + "' or account_id ='" + coaCode + "')\n";
        }
        if (!projectNo.equals("-")) {
            filter += "and project_no = '" + projectNo + "'\n";
        }
        String sql = "select gl_date,description,reference,gl_vou_no,project_no,sum(dr_amt) amount\n" + "from gl\n" + "where " + filter + "group by gl_vou_no\n" + "order by gl_date";
        try {
            ResultSet rs = getResult(sql);
            while (rs.next()) {
                Gl g = new Gl();
                g.setGlDate(rs.getTimestamp("gl_date").toLocalDateTime());
                g.setDescription(rs.getString("description"));
                g.setReference(rs.getString("reference"));
                g.setGlVouNo(rs.getString("gl_vou_no"));
                g.setProjectNo(rs.getString("project_no"));
                g.setDrAmt(rs.getDouble("amount"));
                list.add(g);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return list;
    }

    @Override
    public List<Gl> searchVoucher(String fromDate, String toDate, String vouNo, String description, String reference, String refNo, String compCode, Integer macId) {
        List<Gl> list = new ArrayList<>();
        String filter = " (tran_source ='DR' or tran_source ='CR')\n" + "and comp_code ='" + compCode + "'\n" + "and deleted = false\n" + "and date(gl_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n";
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
        String sql = "select gl_date,description,reference,for_des,from_des,narration,\n" + "gl_vou_no,tran_source,sum(dr_amt) dr_amt,sum(cr_amt) cr_amt\n" + "from gl\n" + "where " + filter + "group by gl_vou_no\n" + "order by gl_date,tran_source,gl_vou_no";
        try {
            ResultSet rs = getResult(sql);
            while (rs.next()) {
                Gl g = new Gl();
                g.setGlDate(rs.getTimestamp("gl_date").toLocalDateTime());
                g.setDescription(rs.getString("description"));
                g.setReference(rs.getString("reference"));
                g.setForDes(rs.getString("for_des"));
                g.setFromDes(rs.getString("from_des"));
                g.setNarration(rs.getString("narration"));
                g.setGlVouNo(rs.getString("gl_vou_no"));
                g.setDrAmt(rs.getDouble("dr_amt"));
                g.setCrAmt(rs.getDouble("cr_amt"));
                g.setTranSource(rs.getString("tran_source"));
                g.setAmount(Util1.getDouble(g.getDrAmt()) + Util1.getDouble(g.getCrAmt()));
                list.add(g);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return list;
    }

    @Override
    public boolean deleteInvVoucher(String refNo, String tranSource, String compCode) {
        String sql = "update gl set deleted = true,intg_upd_status = null where ref_no ='" + refNo + "' and tran_source='" + tranSource + "' and comp_code ='" + compCode + "'";
        execSql(sql);
        return true;
    }

    @Override
    public boolean deleteVoucher(String glVouNo, String compCode) {
        String sql = "update gl set deleted = true where gl_vou_no ='" + glVouNo + "' and comp_code ='" + compCode + "'";
        execSql(sql);
        return true;
    }

    @Override
    public void deleteVoucherByAcc(String vouNo, String tranSource, String srcAcc, String compCode) {
        String sql = "update gl set deleted = true where ref_no ='" + vouNo + "' and tran_source='" + tranSource + "' and source_ac_id ='" + srcAcc + "' and comp_code ='" + compCode + "'";
        execSql(sql);
    }

    @Override
    public List<Gl> getJournal(String glVouNo, String compCode) {
        List<Gl> list = new ArrayList<>();
        String sql = "select g.comp_code,g.dept_id,g.gl_code,g.dept_code,g.cur_code,g.trader_code,g.gl_date,g.source_ac_id,g.gl_vou_no,g.description,g.reference,g.dr_amt,g.cr_amt,\n" + "t.user_code t_user_code,t.trader_name,g.tran_source,\n" + "d.usr_code d_user_code,coa.coa_name_eng,g.project_no\n" + "from gl g\n" + "join department d on g.dept_code = d.dept_code\n" + "and g.comp_code = d.comp_code\n" + "left join trader t on g.trader_code = t.code\n" + "and g.comp_code = t.comp_code\n" + "join chart_of_account coa on g.source_ac_id = coa.coa_code\n" + "and g.comp_code = coa.comp_code\n" + "where g.comp_code ='" + compCode + "'\n" + "and g.gl_vou_no ='" + glVouNo + "'\n" + "and g.tran_source ='GV'\n" + "and g.deleted = false\n" + "order by g.gl_code";
        try {
            ResultSet rs = getResult(sql);
            while (rs.next()) {
                Gl g = new Gl();
                GlKey key = new GlKey();
                key.setGlCode(rs.getString("gl_code"));
                key.setCompCode(rs.getString("comp_code"));
                key.setDeptId(rs.getInt("dept_id"));
                g.setKey(key);
                g.setGlDate(rs.getTimestamp("gl_date").toLocalDateTime());
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
                g.setProjectNo(rs.getString("project_no"));
                list.add(g);
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return list;
    }

    @Override
    public List<Gl> getVoucher(String glVouNo, String compCode) {
        List<Gl> list = new ArrayList<>();
        String sql = "select g.comp_code,g.dept_id,g.gl_code,g.dept_code,g.cur_code,g.trader_code,\n" + "g.gl_date,g.source_ac_id,g.account_id,g.gl_vou_no,g.description,g.reference,g.ref_no,g.dr_amt,g.cr_amt,\n" + "g.for_des,g.from_des,g.narration,\n" + "t.user_code t_user_code,t.trader_name,g.tran_source,\n" + "d.usr_code d_user_code,coa.coa_name_eng\n" + "from gl g\n" + "join department d on g.dept_code = d.dept_code\n" + "and g.comp_code = d.comp_code\n" + "left join trader t on g.trader_code = t.code\n" + "and g.comp_code = t.comp_code\n" + "join chart_of_account coa on g.account_id = coa.coa_code\n" + "and g.comp_code = coa.comp_code\n" + "where g.comp_code ='" + compCode + "'\n" + "and g.gl_vou_no ='" + glVouNo + "'\n" + "and (g.tran_source ='DR' or g.tran_source='CR')\n" + "order by g.gl_code";
        try {
            ResultSet rs = getResult(sql);
            while (rs.next()) {
                Gl g = new Gl();
                GlKey key = new GlKey();
                key.setGlCode(rs.getString("gl_code"));
                key.setCompCode(rs.getString("comp_code"));
                key.setDeptId(rs.getInt("dept_id"));
                g.setKey(key);
                g.setGlDate(rs.getTimestamp("gl_date").toLocalDateTime());
                g.setGlDateStr(Util1.toDateStr(g.getGlDate(), "dd/MM/yyyy"));
                g.setDescription(rs.getString("description"));
                g.setReference(rs.getString("reference"));
                g.setForDes(rs.getString("for_des"));
                g.setFromDes(rs.getString("from_des"));
                g.setNarration(rs.getString("narration"));
                g.setRefNo(rs.getString("ref_no"));
                g.setGlVouNo(rs.getString("gl_vou_no"));
                g.setDrAmt(rs.getDouble("dr_amt"));
                g.setCrAmt(rs.getDouble("cr_amt"));
                g.setAmount(Util1.getDouble(g.getDrAmt()) + Util1.getDouble(g.getCrAmt()));
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
        } catch (Exception e) {
            log.error("getVoucher : " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Gl> getTranSource(String compCode) {
        List<Gl> list = new ArrayList<>();
        String sql = "select distinct tran_source from gl where comp_code='" + compCode + "'";
        ResultSet rs = getResult(sql);
        try {
            while (rs.next()) {
                Gl g = new Gl();
                g.setTranSource(rs.getString("tran_source"));
                list.add(g);
            }
        } catch (Exception e) {
            log.error("getTranSource : " + e.getMessage());
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
        ResultSet rs = getResult(sql);
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
    public List<String> shootTri() {
        List<String> logs = new ArrayList<>();
        try {
            //check source acc
            String sql = "select distinct source_ac_id,tran_source  from gl where source_ac_id not in(\n" + "select coa_code from chart_of_account) ";
            ResultSet rs = getResult(sql);
            while (rs.next()) {
                String sourceAcc = rs.getString("source_ac_id");
                String tranSource = rs.getString("tran_source");
                logs.add(tranSource + " : Gl take Source Account which not exist in Chart Of Account : " + sourceAcc);
            }
            //check account acc
            String sql1 = "select distinct account_id,tran_source  from gl where account_id not in(\n" + "select coa_code from chart_of_account) ";
            ResultSet rs1 = getResult(sql1);
            while (rs1.next()) {
                String account = rs1.getString("account_id");
                String tranSource = rs1.getString("tran_source");
                logs.add(tranSource + " : Gl take Account which not exist in Chart Of Account : " + account);
            }
            //check gl date
            String sql2 = "select gl_code,tran_source from gl where (gl_date is null or gl_date = '') ";
            ResultSet rs2 = getResult(sql2);
            while (rs2.next()) {
                String glCode = rs2.getString("gl_code");
                String tranSource = rs2.getString("tran_source");
                logs.add(tranSource + " : Gl date is null in Gl Code : " + glCode);
            }
            //check same account
            String sql3 = "select gl_code,tran_source from gl where source_ac_id = account_id ";
            ResultSet rs3 = getResult(sql3);
            while (rs3.next()) {
                String glCode = rs3.getString("gl_code");
                String tranSource = rs3.getString("rs3");
                logs.add(tranSource + " : Source Account Code and Account Code are the same in Gl Code : " + glCode);
            }

            //check gl date
            String sql4 = "select gl_code,tran_source from gl where (dept_code is null or dept_code = '') ";
            ResultSet rs4 = getResult(sql4);
            while (rs4.next()) {
                String glCode = rs4.getString("gl_code");
                String tranSource = rs4.getString("tran_source");
                logs.add(tranSource + " : Department is null in Gl Code : " + glCode);
            }

            String sql5 = """
                    select gl_code,tran_source\s
                    from gl
                    where source_ac_id  in(
                    select coa_code
                    from chart_of_account coa
                    where coa_level <=2
                    )
                    or
                    account_id in (
                    select coa_code
                    from chart_of_account coa
                    where coa_level <=2
                    )""";
            ResultSet rs5 = getResult(sql5);
            while (rs5.next()) {
                String glCode = rs5.getString("gl_code");
                String tranSource = rs5.getString("tran_source");
                logs.add(tranSource + " : Chart of Account in GL is Above Level 3 : " + glCode);
            }

            return logs;
        } catch (Exception e) {
            log.error("shootTri : " + e.getMessage());
        }
        return logs;
    }


}
