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
        str = Util1.cleanStr(str);
        String sql = """
                select distinct description
                from gl
                where comp_code =?
                and lower(replace(description, ' ', '')) like ?
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
        str = Util1.cleanStr(str);
        List<VDescription> list = new ArrayList<>();
        String sql = """
                select distinct reference
                from gl
                where comp_code =?
                and lower(replace(reference, ' ', '')) like ?
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
        String filter = " (tran_source ='GV' or tran_source ='EX')\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and deleted = false\n" +
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
        if (!coaCode.equals("-")) {
            filter += "and (source_ac_id = '" + coaCode + "' or account_id ='" + coaCode + "')\n";
        }
        if (!projectNo.equals("-")) {
            filter += "and project_no = '" + projectNo + "'\n";
        }
        String sql = "select gl_date,description,reference,gl_vou_no,project_no,tran_source,sum(dr_amt) amount\n" +
                "from gl\n" +
                "where " + filter +
                "group by gl_vou_no\n" +
                "order by gl_date";
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
                g.setTranSource(rs.getString("tran_source"));
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
        String sql = """
                select g.comp_code,g.dept_id,g.gl_code,g.dept_code,g.cur_code,g.trader_code,
                g.gl_date,g.source_ac_id,g.account_id,g.gl_vou_no,g.description,g.reference,g.dr_amt,g.cr_amt,
                t.user_code t_user_code,t.trader_name,g.tran_source,
                d.usr_code d_user_code,coa.coa_name_eng,g.project_no,g.ex_code
                from gl g
                join department d on g.dept_code = d.dept_code
                and g.comp_code = d.comp_code
                left join trader t on g.trader_code = t.code
                and g.comp_code = t.comp_code
                join chart_of_account coa on g.source_ac_id = coa.coa_code
                and g.comp_code = coa.comp_code
                where g.comp_code =?
                and g.gl_vou_no =?
                and (g.tran_source ='GV' or g.tran_source ='EX')
                and g.deleted = false
                order by g.order_id,g.gl_code""";
        try {
            ResultSet rs = getResult(sql, compCode, glVouNo);
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
                g.setDrAmt(Util1.toNull(rs.getDouble("dr_amt")));
                g.setCrAmt(Util1.toNull(rs.getDouble("cr_amt")));
                g.setDeptCode(rs.getString("dept_code"));
                g.setDeptUsrCode(rs.getString("d_user_code"));
                g.setTraderCode(rs.getString("trader_code"));
                g.setTraderName(rs.getString("trader_name"));
                g.setSrcAccName(rs.getString("coa_name_eng"));
                g.setSrcAccCode(rs.getString("source_ac_id"));
                g.setAccCode(rs.getString("account_id"));
                g.setTranSource(rs.getString("tran_source"));
                g.setCurCode(rs.getString("cur_code"));
                g.setProjectNo(rs.getString("project_no"));
                g.setExCode(rs.getString("ex_code"));
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
        String sql = "select g.comp_code,g.dept_id,g.gl_code,g.dept_code,g.cur_code,g.trader_code,\n" +
                "g.gl_date,g.source_ac_id,g.account_id,g.gl_vou_no,g.description,g.reference,g.ref_no,g.dr_amt,g.cr_amt,\n" +
                "g.for_des,g.from_des,g.narration,\n" +
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
                "and g.deleted = false\n" +
                "and (g.tran_source ='DR' or g.tran_source='CR')\n" +
                "order by g.gl_code";
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
    public List<String> shootTri(String compCode) {
        List<String> logs = new ArrayList<>();
        logs.addAll(checkSourceAccExist(compCode));
        logs.addAll(checkAccountExist(compCode));
        logs.addAll(checkGlDate(compCode));
        logs.addAll(checkSameAccount(compCode));
        logs.addAll(checkDepartment(compCode));
        logs.addAll(checkAccount(compCode));
        logs.addAll(checkSourceAcc(compCode));
        logs.addAll(checkMinusOpening(compCode));
        logs.addAll(checkTrader(compCode));
        return logs;
    }

    private List<String> checkSourceAccExist(String compCode) {
        List<String> logs = new ArrayList<>();
        String sql = """
                select distinct source_ac_id,tran_source
                from gl
                where deleted =false
                and comp_code =?
                and source_ac_id not in(
                select coa_code from chart_of_account)""";
        ResultSet rs = getResult(sql,compCode);
        try {
            while (rs.next()) {
                String sourceAcc = rs.getString("source_ac_id");
                String tranSource = "<font color='red'>" + rs.getString("tran_source") + "</font>";
                logs.add(tranSource + " : Gl take Source Account which not exist in Chart Of Account : " + sourceAcc + "<br>");
            }
        } catch (Exception e) {
            log.error("checkSourceAccExist : " + e.getMessage());
        }
        return logs;
    }

    private List<String> checkAccountExist(String compCode) {
        List<String> logs = new ArrayList<>();
        //check account acc
        String sql = """
                select distinct account_id,tran_source
                from gl
                where deleted =false
                and comp_code =?
                and account_id not in(
                select coa_code from chart_of_account)""";
        ResultSet rs = getResult(sql,compCode);
        try {
            while (rs.next()) {
                String account = rs.getString("account_id");
                String tranSource = "<font color='red'>" + rs.getString("tran_source") + "</font>";
                logs.add(tranSource + " : Gl take Account which not exist in Chart Of Account : " + account + "<br>");
            }
        } catch (Exception e) {
            log.error("checkAccountExist : " + e.getMessage());
        }
        return logs;
    }

    private List<String> checkGlDate(String compCode) {
        List<String> logs = new ArrayList<>();
        String sql2 = """
                select gl_code,tran_source
                from gl
                where deleted =false
                and comp_code =?
                and (gl_date is null or gl_date = '')""";
        ResultSet rs = getResult(sql2,compCode);
        try {
            while (rs.next()) {
                String glCode = rs.getString("gl_code");
                String tranSource = "<font color='red'>" + rs.getString("tran_source") + "</font>";
                logs.add(tranSource + " : Gl date is null in Gl Code : " + glCode + "<br>");
            }
        } catch (Exception e) {
            log.error("checkGLDate : " + e.getMessage());
        }
        return logs;
    }

    private List<String> checkSameAccount(String compCode) {
        List<String> logs = new ArrayList<>();
        String sql = """
                select gl_code,tran_source
                from gl
                where deleted =false
                and comp_code =?
                and source_ac_id = account_id""";
        ResultSet rs = getResult(sql,compCode);
        try {
            while (rs.next()) {
                String glCode = rs.getString("gl_code");
                String tranSource = "<font color='red'>" + rs.getString("tran_source") + "</font>";
                logs.add(tranSource + " : Source Account Code and Account Code are the same in Gl Code : " + glCode + "<br>");
            }
        } catch (Exception e) {
            log.error("checkSameAccount : " + e.getMessage());
        }
        return logs;
    }

    private List<String> checkDepartment(String compCode) {
        List<String> logs = new ArrayList<>();
        String sql = """
                select gl_code,tran_source
                from gl
                where deleted =false
                and comp_code =?
                and (dept_code is null or dept_code = '')""";
        ResultSet rs = getResult(sql,compCode);
        try {
            while (rs.next()) {
                String glCode = rs.getString("gl_code");
                String tranSource = "<font color='red'>" + rs.getString("tran_source") + "</font>";
                logs.add(tranSource + " : Department is null in Gl Code : " + glCode + "<br>");
            }
        } catch (Exception e) {
            log.error("checkDepartment : " + e.getMessage());
        }
        return logs;
    }

    private List<String> checkSourceAcc(String compCode) {
        List<String> logs = new ArrayList<>();
        String sql = """
                select gl_code,tran_source\s
                from gl
                where deleted =false
                and comp_code =?
                and source_ac_id  in(
                select coa_code
                from chart_of_account coa
                where coa_level <=2)""";
        ResultSet rs = getResult(sql,compCode);
        try {
            while (rs.next()) {
                String glCode = rs.getString("gl_code");
                String tranSource = "<font color='red'>" + rs.getString("tran_source") + "</font>";
                logs.add(tranSource + " : Source Account in GL is Above Level 3 : " + glCode + "<br>");
            }
        } catch (Exception e) {
            log.error("checkSourceAcc : " + e.getMessage());
        }
        return logs;
    }

    private List<String> checkAccount(String compCode) {
        List<String> logs = new ArrayList<>();
        String sql = """
                select gl_code,tran_source
                from gl
                where deleted =false
                and comp_code =?
                and account_id  in(
                select coa_code
                from chart_of_account coa
                where coa_level <=2)""";
        ResultSet rs = getResult(sql,compCode);
        try {
            while (rs.next()) {
                String glCode = rs.getString("gl_code");
                String tranSource = "<font color='red'>" + rs.getString("tran_source") + "</font>";
                logs.add(tranSource + " : Account in GL is Above Level 3 : " + glCode + "<br>");
            }
        } catch (Exception e) {
            log.error("checkAccount : " + e.getMessage());
        }
        return logs;
    }

    private List<String> checkMinusOpening(String compCode) {
        List<String> logs = new ArrayList<>();
        String sql = """
                select coa_op_id,dr_amt,cr_amt
                from coa_opening
                where deleted =false
                and comp_code =?
                and (ifnull(dr_amt,0)<0 or ifnull(cr_amt,0)<0)""";
        ResultSet rs = getResult(sql,compCode);
        try {
            while (rs.next()) {
                String opId = rs.getString("coa_op_id");
                double drAmt = rs.getDouble("dr_amt");
                double crAmt = rs.getDouble("cr_amt");
                String tranSource = "<font color='red'>Opening Minus Error</font>";
                logs.add(tranSource + " : " + opId + " - " + "Dr Amt : " + drAmt + " - " + "Cr Amt : " + crAmt + "<br>");
            }
        } catch (Exception e) {
            log.error("checkMinusOpening : " + e.getMessage());
        }
        return logs;
    }

    private List<String> checkTrader(String compCode) {
        List<String> logs = new ArrayList<>();
        String sql = """
                select code,discriminator
                from trader
                where deleted =false
                and comp_code =?
                and account_code  in(
                select coa_code
                from chart_of_account coa
                where coa_level <=2
                )""";
        ResultSet rs = getResult(sql,compCode);
        try {
            while (rs.next()) {
                String traderCode = rs.getString("code");
                String tranSource = "<font color='red'>Trader Account Error : </font>";
                logs.add(tranSource + traderCode + "<br>");
            }
        } catch (Exception e) {
            log.error("checkTrader : " + e.getMessage());
        }
        return logs;
    }


}
