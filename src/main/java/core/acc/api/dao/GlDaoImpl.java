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
import java.util.Map;

@Repository
@Slf4j
public class GlDaoImpl extends AbstractDao<GlKey, Gl> implements GlDao {

    @Override
    public Gl save(Gl gl) throws Exception {
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
            List<Map<String, Object>> result = getList(sql);
            if (!result.isEmpty()) {
                Map<String, Object> rs = result.get(0);
                Gl gl = new Gl();
                gl.setKey(key);
                gl.setGlDate(Util1.toDate(rs.get("gl_date")));
                gl.setCreatedDate(Util1.toDate(rs.get("created_date")));
                gl.setModifyDate(Util1.toDate(rs.get("modify_date")));
                gl.setModifyBy(Util1.getString(rs.get("modify_by")));
                gl.setDescription(Util1.getString(rs.get("description")));
                gl.setSrcAccCode(Util1.getString(rs.get("source_ac_id")));
                gl.setAccCode(Util1.getString(rs.get("account_id")));
                gl.setCurCode(Util1.getString(rs.get("cur_code")));
                gl.setDrAmt(Util1.getDouble(rs.get("dr_amt")));
                gl.setCrAmt(Util1.getDouble(rs.get("cr_amt")));
                gl.setReference(Util1.getString(rs.get("reference")));
                gl.setDeptCode(Util1.getString(rs.get("dept_code")));
                gl.setVouNo(Util1.getString(rs.get("voucher_no")));
                gl.setTraderCode(Util1.getString(rs.get("trader_code")));
                gl.setTranSource(Util1.getString(rs.get("tran_source")));
                gl.setGlVouNo(Util1.getString(rs.get("gl_vou_no")));
                gl.setRemark(Util1.getString(rs.get("remark")));
                gl.setRefNo(Util1.getString(rs.get("ref_no")));
                gl.setMacId(Util1.getInteger(rs.get("mac_id")));
                return gl;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Override
    public boolean delete(GlKey key, String modifyBy) {
        String sql = "update gl\n" + " set deleted =1,intg_upd_status = null,modify_by ='" + modifyBy + "'\n" + " where gl_code = '" + key.getGlCode() + "'\n" + " and comp_code ='" + key.getCompCode() + "'\n" + " and dept_id =" + key.getDeptId() + "";
        execSql(sql);
        return true;
    }


    @Override
    public void deleteGl(String vouNo, String tranSource) {
        String sql = "update  gl set deleted =1,intg_upd_status = null where ref_no ='" + vouNo + "' and tran_source='" + tranSource + "'";
        execSql(sql);
    }

    @Override
    public List<VDescription> getDescription(String str, String compCode) {
        List<VDescription> list = new ArrayList<>();
        String sql = "select distinct description\n" + "from gl\n" + "where comp_code ='" + compCode + "'\n" + "and (description like '" + str + "%')\n" + "and deleted =0\n" + "limit 20";
        try {
            List<Map<String, Object>> result = getList(sql);
            result.forEach((rs) -> {
                VDescription v = new VDescription();
                v.setDescription(Util1.getString(rs.get("description")));
                list.add(v);
            });
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return list;
    }

    @Override
    public List<VDescription> getReference(String str, String compCode) {
        List<VDescription> list = new ArrayList<>();
        String sql = "select distinct reference\n" + "from gl\n" + "where comp_code ='" + compCode + "'\n" + "and (reference like '" + str + "%')\n" + "and deleted =0\n" + "limit 20";
        try {
            List<Map<String, Object>> result = getList(sql);
            result.forEach((rs) -> {
                VDescription v = new VDescription();
                v.setDescription(Util1.getString(rs.get("reference")));
                list.add(v);
            });
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return list;
    }

    @Override
    public List<VDescription> getBatchNo(String str, String compCode) {
        List<VDescription> list = new ArrayList<>();
        String sql = "select distinct batch_no\n" + "from gl\n" + "where comp_code ='" + compCode + "'\n" + "and (batch_no like '" + str + "%')\n" + "and deleted =0\n" + "limit 20";
        try {
            List<Map<String, Object>> result = getList(sql);
            result.forEach((rs) -> {
                VDescription v = new VDescription();
                v.setDescription(Util1.getString(rs.get("batch_no")));
                list.add(v);
            });
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return list;
    }

    @Override
    public List<Gl> searchJournal(String fromDate, String toDate, String vouNo, String description, String reference, String coaCode, String projectNo, String compCode, Integer macId) {
        List<Gl> list = new ArrayList<>();
        String filter = " tran_source ='GV'\n" + "and comp_code ='" + compCode + "'\n" + "and deleted =0\n" + "and date(gl_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n";
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
        String sql = "select gl_date,description,reference,gl_vou_no,project_no,sum(dr_amt) amount\n" + "from gl\n" + "where " + filter + "" + "group by gl_vou_no\n" + "order by gl_date";
        try {
            List<Map<String, Object>> result = getList(sql);
            result.forEach((rs) -> {
                Gl g = new Gl();
                g.setGlDate(Util1.toDate(rs.get("gl_date")));
                g.setDescription(Util1.getString(rs.get("description")));
                g.setReference(Util1.getString(rs.get("reference")));
                g.setGlVouNo(Util1.getString(rs.get("gl_vou_no")));
                g.setProjectNo(Util1.getString(rs.get("project_no")));
                g.setDrAmt(Util1.getDouble(rs.get("amount")));
                list.add(g);
            });
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return list;
    }

    @Override
    public List<Gl> searchVoucher(String fromDate, String toDate, String vouNo, String description, String reference, String refNo, String compCode, Integer macId) {
        List<Gl> list = new ArrayList<>();
        String filter = " (tran_source ='DR' or tran_source ='CR')\n" + "and comp_code ='" + compCode + "'\n" + "and deleted =0\n" + "and date(gl_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n";
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
        String sql = "select gl_date,description,reference,for_des,from_des,narration,\n" + "gl_vou_no,tran_source,sum(dr_amt) dr_amt,sum(cr_amt) cr_amt\n" + "from gl\n" + "where " + filter + "" + "group by gl_vou_no\n" + "order by gl_date,tran_source,gl_vou_no";
        try {
            List<Map<String, Object>> result = getList(sql);
            result.forEach((rs) -> {
                Gl g = new Gl();
                g.setGlDate(Util1.toDate(rs.get("gl_date")));
                g.setDescription(Util1.getString(rs.get("description")));
                g.setReference(Util1.getString(rs.get("reference")));
                g.setForDes(Util1.getString(rs.get("for_des")));
                g.setFromDes(Util1.getString(rs.get("from_des")));
                g.setNarration(Util1.getString(rs.get("narration")));
                g.setGlVouNo(Util1.getString(rs.get("gl_vou_no")));
                g.setDrAmt(Util1.getDouble(rs.get("dr_amt")));
                g.setCrAmt(Util1.getDouble(rs.get("cr_amt")));
                g.setTranSource(Util1.getString(rs.get("tran_source")));
                g.setAmount(Util1.getDouble(g.getDrAmt()) + Util1.getDouble(g.getCrAmt()));
                list.add(g);
            });
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return list;
    }

    @Override
    public boolean deleteInvVoucher(String refNo, String tranSource, String compCode) {
        String sql = "update gl set deleted =1,intg_upd_status = null where ref_no ='" + refNo + "' and tran_source='" + tranSource + "' and comp_code ='" + compCode + "'";
        execSql(sql);
        return true;
    }

    @Override
    public boolean deleteVoucher(String glVouNo, String compCode) {
        String sql = "update gl set deleted =1 where gl_vou_no ='" + glVouNo + "' and comp_code ='" + compCode + "'";
        execSql(sql);
        return true;
    }

    @Override
    public void deleteVoucherByAcc(String vouNo, String tranSource, String srcAcc, String compCode) {
        String sql = "update gl set deleted = 1 where ref_no ='" + vouNo + "' and tran_source='" + tranSource + "' and source_ac_id ='" + srcAcc + "' and comp_code ='" + compCode + "'";
        execSql(sql);
    }

    @Override
    public List<Gl> getJournal(String glVouNo, String compCode) {
        List<Gl> list = new ArrayList<>();
        String sql = "select g.comp_code,g.dept_id,g.gl_code,g.dept_code,g.cur_code,g.trader_code,g.gl_date,g.source_ac_id,g.gl_vou_no,g.description,g.reference,g.dr_amt,g.cr_amt,\n" +
                "t.user_code t_user_code,t.trader_name,g.tran_source,\n" +
                "d.usr_code d_user_code,coa.coa_name_eng,g.project_no\n" +
                "from gl g\n" +
                "join department d on g.dept_code = d.dept_code\n" +
                "and g.comp_code = d.comp_code\n" + "left join trader t on g.trader_code = t.code\n" + "and g.comp_code = t.comp_code\n" + "join chart_of_account coa on g.source_ac_id = coa.coa_code\n" + "and g.comp_code = coa.comp_code\n" + "where g.comp_code ='" + compCode + "'\n" + "and g.gl_vou_no ='" + glVouNo + "'\n" + "and g.tran_source ='GV'\n" + "and g.deleted =0\n" + "order by g.gl_code";
        try {
            List<Map<String, Object>> result = getList(sql);
            result.forEach((rs) -> {
                Gl g = new Gl();
                GlKey key = new GlKey();
                key.setGlCode(Util1.getString(rs.get("gl_code")));
                key.setCompCode(Util1.getString(rs.get("comp_code")));
                key.setDeptId(Util1.getInteger(rs.get("dept_id")));
                g.setKey(key);
                g.setGlDate(Util1.toDate(rs.get("gl_date")));
                g.setGlDateStr(Util1.toDateStr(g.getGlDate(), "dd/MM/yyyy"));
                g.setDescription(Util1.getString(rs.get("description")));
                g.setReference(Util1.getString(rs.get("reference")));
                g.setGlVouNo(Util1.getString(rs.get("gl_vou_no")));
                g.setDrAmt(Util1.getDouble(rs.get("dr_amt")));
                g.setCrAmt(Util1.getDouble(rs.get("cr_amt")));
                g.setDeptCode(Util1.getString(rs.get("dept_code")));
                g.setDeptUsrCode(Util1.getString(rs.get("d_user_code")));
                g.setTraderCode(Util1.getString(rs.get("trader_code")));
                g.setTraderName(Util1.getString(rs.get("trader_name")));
                g.setSrcAccName(Util1.getString(rs.get("coa_name_eng")));
                g.setSrcAccCode(Util1.getString(rs.get("source_ac_id")));
                g.setTranSource(Util1.getString(rs.get("tran_source")));
                g.setCurCode(Util1.getString(rs.get("cur_code")));
                g.setProjectNo(Util1.getString(rs.get("project_no")));
                list.add(g);
            });
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
            List<Map<String, Object>> result = getList(sql);
            result.forEach((rs) -> {
                Gl g = new Gl();
                GlKey key = new GlKey();
                key.setGlCode(Util1.getString(rs.get("gl_code")));
                key.setCompCode(Util1.getString(rs.get("comp_code")));
                key.setDeptId(Util1.getInteger(rs.get("dept_id")));
                g.setKey(key);
                g.setGlDate(Util1.toDate(rs.get("gl_date")));
                g.setGlDateStr(Util1.toDateStr(g.getGlDate(), "dd/MM/yyyy"));
                g.setDescription(Util1.getString(rs.get("description")));
                g.setReference(Util1.getString(rs.get("reference")));
                g.setForDes(Util1.getString(rs.get("for_des")));
                g.setFromDes(Util1.getString(rs.get("from_des")));
                g.setNarration(Util1.getString(rs.get("narration")));
                g.setRefNo(Util1.getString(rs.get("ref_no")));
                g.setGlVouNo(Util1.getString(rs.get("gl_vou_no")));
                g.setDrAmt(Util1.getDouble(rs.get("dr_amt")));
                g.setCrAmt(Util1.getDouble(rs.get("cr_amt")));
                g.setAmount(Util1.getDouble(g.getDrAmt()) + Util1.getDouble(g.getCrAmt()));
                g.setDeptCode(Util1.getString(rs.get("dept_code")));
                g.setDeptUsrCode(Util1.getString(rs.get("d_user_code")));
                g.setTraderCode(Util1.getString(rs.get("trader_code")));
                g.setTraderName(Util1.getString(rs.get("trader_name")));
                g.setAccCode(Util1.getString(rs.get("account_id")));
                g.setAccName(Util1.getString(rs.get("coa_name_eng")));
                g.setSrcAccCode(Util1.getString(rs.get("source_ac_id")));
                g.setTranSource(Util1.getString(rs.get("tran_source")));
                g.setCurCode(Util1.getString(rs.get("cur_code")));

                list.add(g);
            });
        } catch (Exception e) {
            log.error("getVoucher : " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Gl> getTranSource(String compCode) {
        List<Gl> list = new ArrayList<>();
        String sql = "select distinct tran_source from gl where comp_code='" + compCode + "'";
        List<Map<String, Object>> result = getList(sql);
        result.forEach((rs) -> {
            Gl g = new Gl();
            g.setTranSource(Util1.getString(rs.get("tran_source")));
            list.add(g);
        });
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
        List<Map<String, Object>> result = getList(sql);
        if (!result.isEmpty()) {
            Map<String, Object> rs = result.get(0);
            Date date = Util1.toDate(rs.get("date"));
            if (date != null) {
                return date;
            }
        }
        return Util1.getSyncDate();
    }

    @Override
    public List<Gl> search(String updatedDate, String deptCode) {
        List<Gl> list = new ArrayList<>();
        String sql = "select * from gl o where o.dept_code='" + deptCode + "' and o.intg_upd_status is null";
        List<Map<String, Object>> result = getList(sql);
        result.forEach((rs) -> {
            Gl gl = new Gl();
            GlKey key = new GlKey();
            key.setCompCode(Util1.getString(rs.get("gl_code")));
            key.setDeptId(Util1.getInteger(rs.get("dept_id")));
            key.setCompCode(Util1.getString(rs.get("comp_code")));
            gl.setKey(key);
            gl.setGlDate(Util1.toDate(rs.get("gl_date")));
            gl.setCreatedDate(Util1.toDate(rs.get("created_date")));
            gl.setModifyDate(Util1.toDate(rs.get("modify_date")));
            gl.setModifyBy(Util1.getString(rs.get("modify_by")));
            gl.setDescription(Util1.getString(rs.get("description")));
            gl.setSrcAccCode(Util1.getString(rs.get("source_ac_id")));
            gl.setAccCode(Util1.getString(rs.get("account_id")));
            gl.setCurCode(Util1.getString(rs.get("cur_code")));
            gl.setDrAmt(Util1.getDouble(rs.get("dr_amt")));
            gl.setCrAmt(Util1.getDouble(rs.get("cr_amt")));
            gl.setReference(Util1.getString(rs.get("reference")));
            gl.setDeptCode(Util1.getString(rs.get("dept_code")));
            gl.setVouNo(Util1.getString(rs.get("voucher_no")));
            gl.setTraderCode(Util1.getString(rs.get("trader_code")));
            gl.setTranSource(Util1.getString(rs.get("tran_source")));
            gl.setGlVouNo(Util1.getString(rs.get("gl_vou_no")));
            gl.setRemark(Util1.getString(rs.get("remark")));
            gl.setRefNo(Util1.getString(rs.get("ref_no")));
            gl.setMacId(Util1.getInteger(rs.get("mac_id")));
            list.add(gl);
        });
        return list;
    }

    @Override
    public List<String> shootTri() {
        List<String> logs = new ArrayList<>();
        try {
            //check source acc
            String sql = "select distinct source_ac_id,tran_source  from gl where source_ac_id not in(\n" + "select coa_code from chart_of_account) ";
            List<Map<String, Object>> result = getList(sql);
            result.forEach((rs) -> {
                String sourceAcc = Util1.getString(rs.get("source_ac_id"));
                String tranSource = Util1.getString(rs.get("tran_source"));
                logs.add(tranSource + " : Gl take Source Account which not exist in Chart Of Account : " + sourceAcc);
            });
            //check account acc
            String sql1 = "select distinct account_id,tran_source  from gl where account_id not in(\n" + "select coa_code from chart_of_account) ";
            List<Map<String, Object>> result1 = getList(sql1);
            result1.forEach(rs1 -> {
                String account = Util1.getString(rs1.get("account_id"));
                String tranSource = Util1.getString(rs1.get("tran_source"));
                logs.add(tranSource + " : Gl take Account which not exist in Chart Of Account : " + account);
            });
            //check gl date
            String sql2 = "select gl_code,tran_source from gl where (gl_date is null or gl_date = '') ";
            List<Map<String, Object>> result2 = getList(sql2);
            result2.forEach(rs2 -> {
                String glCode = Util1.getString(rs2.get("gl_code"));
                String tranSource = Util1.getString(rs2.get("tran_source"));
                logs.add(tranSource + " : Gl date is null in Gl Code : " + glCode);

            });
            //check same account
            String sql3 = "select gl_code,tran_source from gl where source_ac_id = account_id ";
            List<Map<String, Object>> result3 = getList(sql3);
            result3.forEach(rs3 -> {
                String glCode = Util1.getString(rs3.get("gl_code"));
                String tranSource = Util1.getString(rs3.get("tran_source"));
                logs.add(tranSource + " : Source Account Code and Accound Code are the same in Gl Code : " + glCode);

            });
            //check gl date
            String sql4 = "select gl_code,tran_source from gl where (dept_code is null or dept_code = '') ";
            List<Map<String, Object>> result4 = getList(sql4);
            result4.forEach(rs4 -> {
                String glCode = Util1.getString(rs4.get("gl_code"));
                String tranSource = Util1.getString(rs4.get("tran_source"));
                logs.add(tranSource + " : Department is null in Gl Code : " + glCode);
            });
            String sql5 = "select gl_code,tran_source \n" + "from gl\n" + "where source_ac_id  in(\n" + "select coa_code\n" + "from chart_of_account coa\n" + "where coa_level <=2\n" + ")\n" + "or\n" + "account_id in (\n" + "select coa_code\n" + "from chart_of_account coa\n" + "where coa_level <=2\n" + ")";
            List<Map<String, Object>> result5 = getList(sql5);
            result5.forEach(rs5 -> {
                String glCode = Util1.getString(rs5.get("gl_code"));
                String tranSource = Util1.getString(rs5.get("tran_source"));
                logs.add(tranSource + " : Chart of Account in GL is Above Level 3 : " + glCode);
            });

            return logs;
        } catch (Exception e) {
            log.error("shootTri : " + e.getMessage());
        }
        return logs;
    }


}
