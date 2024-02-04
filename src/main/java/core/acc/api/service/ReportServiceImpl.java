package core.acc.api.service;

import core.acc.api.common.Util1;
import core.acc.api.dao.ReportDao;
import core.acc.api.dao.TmpDepartmentDao;
import core.acc.api.entity.*;
import core.acc.api.model.Financial;
import core.acc.api.model.ReturnObject;
import core.acc.api.model.VoucherInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportDao dao;
    private final TmpDepartmentDao tmpDao;
    private final COAOpeningService coaOpeningService;
    private static final String OP_INV = "Opening Inventory";
    private static final String CL_INV = "Closing Inventory";
    private static final String GP = "Gross Profit (Loss)";
    private static final String NP = "Net Profit (Loss)";
    private static final String COS = "Cost of Sale";
    private final HashMap<Integer, ReturnObject> hmRo = new HashMap<>();

    @Override
    public void insertTmp(List<String> listStr, Integer macId, String compCode) {
        try {
            deleteTmp(macId);
            if (listStr == null || listStr.isEmpty()) {
                String sql = """
                        select dept_code
                        from department
                        where comp_code=?""";
                ResultSet rs = dao.executeAndResult(sql, compCode);
                while (rs.next()) {
                    TmpDepartment tmp = new TmpDepartment();
                    TmpDepartmentKey key = new TmpDepartmentKey();
                    key.setDeptCode(rs.getString("dept_code"));
                    key.setMacId(macId);
                    tmp.setKey(key);
                    tmpDao.save(tmp);
                }
            } else {
                for (String deptCode : listStr) {
                    TmpDepartment tmp = new TmpDepartment();
                    TmpDepartmentKey key = new TmpDepartmentKey();
                    key.setDeptCode(deptCode);
                    key.setMacId(macId);
                    tmp.setKey(key);
                    tmpDao.save(tmp);
                }
            }
        } catch (Exception e) {
            log.error(String.format("insertTmp: %s", e.getMessage()));
        }
    }

    @Override
    public ResultSet getResult(String sql) {
        return dao.executeAndResult(sql);
    }


    @Override
    public String getOpeningDate(String compCode) {
        String opDate = null;
        String sql = "select max(op_date) op_date from coa_opening where comp_code ='" + compCode + "' and deleted = false";
        try {
            ResultSet rs = dao.executeAndResult(sql);
            if (rs != null) {
                while (rs.next()) {
                    Date date = rs.getDate("op_date");
                    if (date != null) {
                        opDate = Util1.toDateStr(date, "yyyy-MM-dd");
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return Util1.isNull(opDate, "1998-10-07");
    }

    @Override
    public List<Gl> getIndividualLedger(String fromDate, String toDate, String desp, String srcAcc, String acc, String curCode, String reference, String compCode, String tranSource, String traderCode, String traderType, String coaLv2, String coaLv1, String batchNo, String projectNo, boolean summary, Integer macId) throws SQLException {
        String coaFilter = "";
        if (!coaLv2.equals("-")) {
            coaFilter += "where coa3.coa_parent = '" + coaLv2 + "'\n";
        }
        if (!coaLv1.equals("-")) {
            if (coaFilter.isEmpty()) {
                coaFilter += "where coa2.coa_parent = '" + coaLv1 + "'\n";
            } else {
                coaFilter += "and coa2.coa_parent = '" + coaLv1 + "'\n";
            }
        }
        String filter = "";
        if (!traderCode.equals("-")) {
            filter += "and trader_code = '" + traderCode + "'\n";
        }
        if (!tranSource.equals("-")) {
            filter += "and tran_source = '" + tranSource + "'\n";
        }
        if (!reference.equals("-")) {
            filter += "and reference like '" + reference.replaceAll("'", "''") + "%'\n";
        }
        if (!desp.equals("-")) {
            filter += "and description like '" + desp.replaceAll("'", "''") + "%'\n";
        }
        if (!acc.equals("-")) {
            filter += "and (account_id = '" + acc + "' or source_ac_id ='" + acc + "')";
        }
        if (!traderType.equals("-")) {
            filter += "and  discriminator ='" + traderType + "' \n";
        }
        if (!curCode.equals("-")) {
            filter += "and cur_code ='" + curCode + "'\n";
        }
        if (!batchNo.equals("-")) {
            filter += "and batch_no ='" + batchNo + "'\n";
        }
        if (!projectNo.equals("-")) {
            filter += "and project_no ='" + projectNo + "'\n";
        }
        List<Gl> list = new ArrayList<>();
        if (summary) {
            String sql = "select a.*,dep.usr_code d_user_code,coa.coa_name_eng src_acc_name,coa3.coa_name_eng acc_name\n" +
                    "from (\n" + "select gl_date,gl_code,dept_id,cur_code,source_ac_id,account_id,dept_code,trader_code,comp_code,sum(dr_amt) dr_amt,sum(cr_amt) cr_amt\n" +
                    "from gl \n" +
                    "where date(gl_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                    "and comp_code = '" + compCode + "'\n" +
                    "and deleted = false\n" +
                    "and dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n" +
                    "and (account_id = '" + srcAcc + "' or source_ac_id ='" + srcAcc + "')\n" + filter + "\n" +
                    "group by source_ac_id,account_id,dept_code\n" + ")a\n" +
                    "join department dep\n" +
                    "on a.dept_code = dep.dept_code\n" + "and a.comp_code = dep.comp_code\n" +
                    "join chart_of_account coa\n" + "on a.source_ac_id = coa.coa_code\n" +
                    "and a.comp_code = coa.comp_code\n" + "left join chart_of_account coa3\n" +
                    "on a.account_id = coa3.coa_code\n" +
                    "and a.comp_code = coa3.comp_code\n" +
                    "left join chart_of_account coa2\n" +
                    "on coa3.coa_parent = coa2.coa_code\n" +
                    "and coa3.comp_code = coa2.comp_code\n" + coaFilter + "\n" +
                    "order by coa.coa_code_usr\n";
            ResultSet rs = dao.executeAndResult(sql);
            if (!Objects.isNull(rs)) {
                while (rs.next()) {
                    Gl v = new Gl();
                    GlKey key = new GlKey();
                    key.setCompCode(compCode);
                    key.setGlCode(rs.getString("gl_code"));
                    key.setDeptId(rs.getInt("dept_id"));
                    v.setKey(key);
                    v.setGlDate(rs.getTimestamp("gl_date").toLocalDateTime());
                    v.setVouDate(Util1.toDateStr(v.getGlDate(), "dd/MM/yyyy"));
                    v.setCurCode(rs.getString("cur_code"));
                    v.setSrcAccCode(rs.getString("source_ac_id"));
                    v.setAccCode(rs.getString("account_id"));
                    v.setDrAmt(rs.getDouble("dr_amt"));
                    v.setCrAmt(rs.getDouble("cr_amt"));
                    v.setDeptUsrCode(rs.getString("d_user_code"));
                    v.setSrcAccName(rs.getString("src_acc_name"));
                    v.setAccName(rs.getString("acc_name"));
                    v.setTranSource("Report");
                    list.add(v);
                }
            }
        } else {
            String sql = "select a.*,dep.usr_code d_user_code,t.user_code t_user_code,t.discriminator,t.trader_name,coa.coa_name_eng src_acc_name,coa3.coa_name_eng acc_name\n" +
                    "from (\n" + "select gl_code, gl_date, created_date, description, source_ac_id, account_id, \n" +
                    "cur_code, dr_amt, cr_amt, reference, dept_code, voucher_no, trader_code, comp_code, tran_source, gl_vou_no,\n" +
                    "remark, mac_id, ref_no,dept_id,batch_no,project_no\n" +
                    "from gl \n" +
                    "where date(gl_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                    "and comp_code = '" + compCode + "'\n" + "and deleted = false\n" +
                    "and dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n" +
                    "and (account_id = '" + srcAcc + "' or source_ac_id ='" + srcAcc + "')\n" + filter + "\n" +
                    "order by gl_date,tran_source,gl_code\n" + ")a\n" +
                    "join department dep\n" +
                    "on a.dept_code = dep.dept_code\n" +
                    "and a.comp_code = dep.comp_code\n" +
                    "left join trader t on \n" +
                    "a.trader_code = t.code\n" + "and a.comp_code = t.comp_code\n" +
                    "join chart_of_account coa\n" +
                    "on a.source_ac_id = coa.coa_code\n" +
                    "and a.comp_code = coa.comp_code\n" + "left join chart_of_account coa3\n" +
                    "on a.account_id = coa3.coa_code\n" +
                    "and a.comp_code = coa3.comp_code\n" +
                    "left join chart_of_account coa2\n" +
                    "on coa3.coa_parent = coa2.coa_code\n" +
                    "and coa3.comp_code = coa2.comp_code\n" + coaFilter + "\n" +
                    "order by a.gl_date,a.tran_source,a.gl_code\n";
            ResultSet rs = dao.executeAndResult(sql);
            while (rs.next()) {
                Gl v = new Gl();
                GlKey key = new GlKey();
                key.setCompCode(rs.getString("comp_code"));
                key.setGlCode(rs.getString("gl_code"));
                key.setDeptId(rs.getInt("dept_id"));
                v.setKey(key);
                v.setGlDate(rs.getTimestamp("gl_date").toLocalDateTime());
                v.setCreatedDate(rs.getTimestamp("created_date").toLocalDateTime());
                v.setVouDate(Util1.toDateStr(v.getGlDate(), "dd/MM/yyyy"));
                v.setDescription(rs.getString("description"));
                v.setSrcAccCode(rs.getString("source_ac_id"));
                v.setAccCode(rs.getString("account_id"));
                v.setCurCode(rs.getString("cur_code"));
                v.setDrAmt(rs.getDouble("dr_amt"));
                v.setCrAmt(rs.getDouble("cr_amt"));
                v.setReference(rs.getString("reference"));
                v.setRefNo(rs.getString("ref_no"));
                v.setDeptCode(rs.getString("dept_code"));
                v.setVouNo(rs.getString("voucher_no"));
                v.setDeptUsrCode(rs.getString("d_user_code"));
                v.setTraderCode(rs.getString("trader_code"));
                v.setTraderName(rs.getString("trader_name"));
                v.setTranSource(rs.getString("tran_source"));
                v.setGlVouNo(rs.getString("gl_vou_no"));
                v.setSrcAccName(rs.getString("src_acc_name"));
                v.setAccName(rs.getString("acc_name"));
                v.setMacId(rs.getInt("mac_id"));
                v.setBatchNo(rs.getString("batch_no"));
                v.setProjectNo(rs.getString("project_no"));
                list.add(v);
            }
        }
        if (!list.isEmpty()) {
            list.forEach(gl -> {
                String account = Util1.isNull(gl.getAccCode(), "-");
                if (account.equals(srcAcc)) {
                    //swap amt
                    double tmpDrAmt = Util1.getDouble(gl.getDrAmt());
                    gl.setDrAmt(gl.getCrAmt());
                    gl.setCrAmt(tmpDrAmt);
                    //swap acc
                    String tmpStr = gl.getAccName();
                    String tmpAcc = gl.getAccCode();
                    gl.setAccName(gl.getSrcAccName());
                    gl.setAccCode(gl.getSrcAccCode());
                    gl.setSrcAccName(tmpStr);
                    gl.setSrcAccCode(tmpAcc);
                }
                gl.setDrAmt(Util1.toNull(gl.getDrAmt()));
                gl.setCrAmt(Util1.toNull(gl.getCrAmt()));
            });
        }
        return list;
    }

    private void deleteTmp(Integer macId) {
        String delSql = "delete from " + "tmp_dep_filter" + " where mac_id =" + macId;
        executeAndResult(delSql);
    }

    @Override
    public List<Financial> getProfitLost(String plProcess, String opDate, String stDate, String enDate, String invGroup, boolean detail, String projectNo, String compCode, Integer macId) {
        genTriBalance(compCode, stDate, enDate, opDate, "-", "-", "-", plProcess, "-", projectNo, "-", true, macId);
        double ttlIncome = 0.0;
        double ttlOpInv = 0.0;
        double ttlClInv = 0.0;
        double ttlPurchase = 0.0;
        double ttlOtherIncome = 0.0;
        double ttlExpense = 0.0;
        List<Financial> list = new ArrayList<>();
        //income,purchase,other income,expense
        if (!plProcess.equals("-")) {
            String sql;
            String[] process = plProcess.split(",");
            for (int i = 0; i < process.length; i++) {
                int index = i + 1;
                //opening
                if (index == 2) {
                    List<Financial> listOP = getInvOpeningDetail(opDate, stDate, invGroup, compCode, macId);
                    //List<Financial> listJournal = getInvJournal(stDate,enDate,invGroup,compCode,macId);
                    list.addAll(listOP);
                    //list.addAll(listJournal);
                    for (Financial f : listOP) {
                        ttlOpInv += f.getAmount();
                    }
                }
                String head = process[i];
                sql = detail ? getHeadSqlDetail(head, macId) : getHeadSqlSummary(head, macId);
                ResultSet rs = dao.executeAndResult(sql);
                try {
                    if (rs != null) {
                        while (rs.next()) {
                            Financial f = new Financial();
                            f.setAmount(rs.getDouble("amount"));
                            if (detail) {
                                f.setCoaName(rs.getString("coa_name_eng"));
                            }
                            f.setGroupName(rs.getString("group_name"));
                            f.setHeadName(rs.getString("head_name"));
                            switch (index) {
                                case 1 -> {
                                    f.setTranGroup(GP);
                                    f.setOrder("1");
                                    ttlIncome += f.getAmount();
                                }
                                case 2 -> {
                                    f.setTranGroup(GP);
                                    f.setOrder("1");
                                    f.setHeadName(COS);
                                    f.setAmount(f.getAmount() * -1);
                                    ttlPurchase += f.getAmount();
                                }
                                case 3 -> {
                                    f.setTranGroup(NP);
                                    ttlOtherIncome += f.getAmount();
                                }
                                case 4 -> {
                                    f.setTranGroup(NP);
                                    ttlExpense += f.getAmount();
                                }

                            }
                            list.add(f);
                        }
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
                //closing
                if (index == 2) {
                    List<Financial> listCL = getInvClosing(stDate, enDate, invGroup, compCode, macId, detail);
                    for (Financial f : listCL) {
                        f.setAmount(f.getAmount() * -1);
                        ttlClInv += f.getAmount();
                    }
                    list.addAll(listCL);
                }
            }
            if (!list.isEmpty()) {
                ReturnObject ro = new ReturnObject();
                ro.setOpenInv(ttlOpInv);
                ro.setClInv(ttlClInv);
                ro.setTtlIncome(ttlIncome);
                ro.setTtlPurchase(ttlPurchase);
                ro.setTtlOtherIncome(ttlOtherIncome);
                ro.setTtlExpense(ttlExpense);
                double cos = ttlPurchase + ttlOpInv + ttlClInv;
                double gp = ttlIncome - cos;
                double np = gp + ttlOtherIncome + ttlExpense;
                ro.setCos(cos);
                ro.setGrossProfit(gp);
                ro.setNetProfit(np);
                ro.setCosPercent(Util1.getPercent(cos / ttlIncome));
                ro.setGpPercent(Util1.getPercent(gp / ttlIncome));
                ro.setNpPercent(Util1.getPercent(np / ttlIncome));
                hmRo.put(macId, ro);
            }
        }
        return list;
    }

    @Override
    public double getProfit(String opDate, String stDate, String enDate, String invGroup, String plProcess, String projectNo, String compCode, Integer macId) {
        genTriBalance(compCode, stDate, enDate, opDate, "-", "-", "-", plProcess, "-", projectNo, "-", true, macId);
        AtomicReference<Double> opAmt = new AtomicReference<>(0.0);
        AtomicReference<Double> clAmt = new AtomicReference<>(0.0);
        List<Financial> opList = getInvOpeningDetail(opDate, stDate, invGroup, compCode, macId);
        opList.forEach(op -> opAmt.updateAndGet(v -> v + op.getAmount()));
        List<Financial> clList = getInvClosing(stDate, enDate, invGroup, compCode, macId, false);
        clList.forEach(op -> clAmt.updateAndGet(v -> v + op.getAmount() * -1));
        double amt = opAmt.get() + clAmt.get();
        String sql = "select sum(dr_amt)-sum(cr_amt)+" + amt + " profit\n" + "from tmp_tri \n" + "where mac_id =" + macId;
        try {
            ResultSet rs = dao.executeAndResult(sql);
            if (rs != null) {
                if (rs.next()) {
                    return rs.getDouble("profit") * -1;
                }
            }
        } catch (Exception e) {
            log.error("getProfit : " + e.getMessage());
        }
        return 0;
    }

    @Override
    public List<Financial> getBalanceSheet(String bsProcess, String opDate, String stDate, String enDate, String invGroup, String reAcc, String plAcc, boolean detail, double prvProfit, double curProfit, String projectNo, String compCode, Integer macId) {
        genTriBalance(compCode, stDate, enDate, opDate, "-", "-", "-", "-", bsProcess, projectNo, "-", false, macId);
        List<Financial> list = new ArrayList<>();
        if (!bsProcess.equals("-")) {
            updateInvClosing(stDate, enDate, invGroup, compCode, macId);
            updateRetainEarning(reAcc, prvProfit, compCode, macId);
            updatePl(plAcc, curProfit, compCode, macId);
            String[] process = bsProcess.split(",");
            //fix,cur,lia,capital
            for (int i = 0; i < process.length; i++) {
                String head = process[i];
                String sql = detail ? getHeadSqlDetail(head, macId) : getHeadSqlSummary(head, macId);
                ResultSet rs = dao.executeAndResult(sql);
                try {
                    if (rs != null) {
                        while (rs.next()) {
                            Financial f = new Financial();
                            f.setCoaCode(rs.getString("coa_code"));
                            f.setAmount(rs.getDouble("amount"));
                            if (detail) {
                                f.setCoaName(rs.getString("coa_name_eng"));
                            }
                            f.setGroupName(rs.getString("group_name"));
                            f.setHeadName(rs.getString("head_name"));
                            f.setAmount(i < 2 ? f.getAmount() * -1 : f.getAmount());
                            f.setTranGroup(i < 2 ? "TOTAL ASSETS" : "TOTAL CAPITAL AND LIABILITIES");
                            list.add(f);
                        }
                    }
                    if (i == 3) {
                        if (isEmpty(reAcc, compCode, macId)) {
                            Financial re = new Financial();
                            if (prvProfit != 0) {
                                re.setCoaName("Retained Earning");
                                re.setAmount(prvProfit);
                                re.setGroupName(re.getCoaName());
                                re.setHeadName("CAPITAL");
                                re.setTranGroup("TOTAL CAPITAL AND LIABILITIES");
                                list.add(re);
                            }
                        }
                        if (isEmpty(plAcc, compCode, macId)) {
                            Financial pl = new Financial();
                            pl.setCoaName("Net Profit (Loss)");
                            pl.setAmount(curProfit);
                            pl.setGroupName(pl.getCoaName());
                            pl.setHeadName("CAPITAL");
                            pl.setTranGroup("TOTAL CAPITAL AND LIABILITIES");
                            list.add(pl);
                        }
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
        return list;
    }

    @Override
    public List<Financial> getOpeningBalanceSheet(String bsProcess, String opDate, boolean detail, String compCode) {
        List<Financial> list = new ArrayList<>();
        if (!bsProcess.equals("-")) {
            String[] process = bsProcess.split(",");
            //fix,cur,lia,capital
            for (int i = 0; i < process.length; i++) {
                String head = process[i];
                String sql = detail ? getOpeningHeadDetail(opDate, head, compCode) : getOpeningHeadSummary(opDate, head, compCode);
                ResultSet rs = dao.executeAndResult(sql);
                try {
                    if (rs != null) {
                        while (rs.next()) {
                            Financial f = new Financial();
                            f.setAmount(rs.getDouble("amount"));
                            if (detail) {
                                f.setCoaName(rs.getString("coa_name_eng"));
                            }
                            f.setGroupName(rs.getString("group_name"));
                            f.setHeadName(rs.getString("head_name"));
                            f.setAmount(i < 2 ? f.getAmount() * -1 : f.getAmount());
                            f.setTranGroup(i < 2 ? "TOTAL ASSETS" : "TOTAL CAPITAL AND LIABILITIES");
                            list.add(f);
                        }
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
        return list;
    }

    private void updateInvClosing(String stDate, String enDate, String invGroup, String compCode, Integer macId) {
        List<Financial> list = getInvClosing(stDate, enDate, invGroup, compCode, macId, true);
        list.forEach(f -> {
            double amt = f.getAmount();
            String coaCode = f.getCoaCode();
            String c = "select * \n" + "from tmp_tri\n" + "where mac_id =" + macId + "\n" + "and comp_code ='" + compCode + "'\n" + "and coa_code ='" + coaCode + "'";
            ResultSet rs = getResult(c);
            try {
                if (rs.next()) {
                    String sql = "update tmp_tri\n" + "set dr_amt = " + amt + "\n" + "where mac_id =" + macId + "\n" + "and comp_code ='" + compCode + "'\n" + "and coa_code ='" + coaCode + "'";
                    dao.exeSql(sql);
                } else {
                    String sql = "insert into tmp_tri\n" + "select '" + coaCode + "','-'," + macId + "," + amt + ",0,'-','" + compCode + "'";
                    dao.exeSql(sql);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        });
        List<Financial> list1 = getInvJournal(stDate, enDate, invGroup, compCode, macId);
        list1.forEach(f -> {
            double amt = f.getAmount() * -1;
            String sql = "update tmp_tri\n" + "set cr_amt = " + amt + "\n" + "where mac_id =" + macId + "\n" + "and comp_code ='" + compCode + "'\n" + "and coa_code ='" + f.getCoaCode() + "'";
            dao.exeSql(sql);
        });
    }

    private boolean isEmpty(String coaCode, String compCode, Integer macId) {
        String sql = "select coa_code from tmp_tri where coa_code ='" + coaCode + "' and comp_code ='" + compCode + "' and mac_id =" + macId;
        try {
            return !dao.executeAndResult(sql).next();
        } catch (Exception e) {
            log.error("isEmpty : " + e.getMessage());
        }
        return true;
    }

    private void updateRetainEarning(String reAcc, double reAmt, String compCode, Integer macId) {
        if (!reAcc.equals("-")) {
            String sql = "update tmp_tri set cr_amt=cr_amt+" + reAmt + " where coa_code='" + reAcc + "' and mac_id =" + macId + " and comp_code ='" + compCode + "'";
            dao.exeSql(sql);
        }
    }

    private void updatePl(String plAcc, double plAmt, String compCode, Integer macId) {
        if (!plAcc.equals("-")) {
            String sql = "update tmp_tri set dr_amt=dr_amt+" + plAmt * -1 + " where coa_code='" + plAcc + "' and mac_id =" + macId + " and comp_code ='" + compCode + "'";
            dao.exeSql(sql);
        }
    }

    private List<Financial> getInvOpeningDetail(String opDate, String stDate, String invGroup, String compCode, Integer macId) {
        List<Financial> list = new ArrayList<>();
        String coaFilter = "select coa_code from chart_of_account where coa_parent='" + invGroup + "' and comp_code ='" + compCode + "'";
        String depFilter = "select dept_code from tmp_dep_filter where mac_id =" + macId;
        String sql = "select coa.coa_name_eng,sum(amount)*ifnull(tmp.ex_rate,1) amount\n" + "from (\n" + "select a.*\n" + "from (\n" + "select DATE_SUB(op_date, INTERVAL 1 DAY) op_date,source_acc_id,dept_code,cur_code,comp_code,sum(dr_amt) amount\n" + "from coa_opening\n" + "where source_acc_id in (" + coaFilter + ")\n" + "and deleted = false\n" + "and dept_code in (" + depFilter + ")\n" + "and date(op_date)>='" + opDate + "'\n" + "and dr_amt>0\n" + "and comp_code ='" + compCode + "'\n" + "group by source_acc_id,cur_code\n" + "\tunion all\n" + "select date(tran_date)tran_date,coa_code,dept_code,curr_code,comp_code,sum(amount) amount\n" + "from stock_op_value\n" + "where coa_code in (" + coaFilter + ")\n" + "and dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n" + "and date(tran_date) > '" + opDate + "'\n" + "and comp_code ='" + compCode + "'\n" + "and deleted = false\n" + "group by tran_date,coa_code,curr_code\n" + ")a\n" + "where a.op_date = DATE_SUB('" + stDate + "', INTERVAL 1 DAY)\n" + ")b\n" + "join chart_of_account coa on b.source_acc_id = coa.coa_code\n" + "and b.comp_code=coa.comp_code\n" + "left join tmp_ex_rate tmp\n" + "on b.cur_code = tmp.ex_cur\n" + "and b.comp_code = tmp.comp_code\n" + "and tmp.mac_id =" + macId + "\n" + "group by source_acc_id\n";
        try {
            ResultSet rs = dao.executeAndResult(sql);
            if (rs != null) {
                while (rs.next()) {
                    Financial f = new Financial();
                    f.setCoaName(rs.getString("coa_name_eng"));
                    f.setAmount(rs.getDouble("amount"));
                    f.setHeadName(COS);
                    f.setGroupName(OP_INV);
                    f.setTranGroup(GP);
                    f.setOrder("1");
                    list.add(f);
                }
            }
        } catch (Exception e) {
            log.error("getInvOpeningDetail :" + e.getMessage());
        }
        return list;
    }

    private List<Financial> getInvJournal(String stDate, String enDate, String invGroup, String compCode, Integer macId) {
        List<Financial> list = new ArrayList<>();
        String sql = "select source_ac_id,coa.coa_name_eng,sum(amount)*ifnull(tmp.ex_rate,1) amount\n" + "from(\n" + "select source_ac_id,cur_code,comp_code,sum(dr_amt)-sum(cr_amt) amount\n" + "from gl \n" + "where date(gl_date) between '" + stDate + "' and '" + enDate + "'\n" + "and deleted = false\n" + "and source_ac_id  in (select coa_code from chart_of_account where coa_parent='" + invGroup + "' and comp_code ='" + compCode + "')\n" + "and dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n" + "group by source_ac_id,cur_code\n" + ")a\n" + "join chart_of_account coa on a.source_ac_id = coa.coa_code\n" + "and a.comp_code=coa.comp_code\n" + "left join tmp_ex_rate tmp\n" + "on a.cur_code = tmp.ex_cur\n" + "and a.comp_code = tmp.comp_code\n" + "and tmp.mac_id =" + macId + "\n" + "\n";
        try {
            ResultSet rs = dao.executeAndResult(sql);
            if (rs != null) {
                while (rs.next()) {
                    Financial f = new Financial();
                    f.setCoaCode(rs.getString("source_ac_id"));
                    f.setCoaName(rs.getString("coa_name_eng"));
                    f.setAmount(rs.getDouble("amount"));
                    f.setHeadName(COS);
                    f.setGroupName(CL_INV);
                    f.setTranGroup(GP);
                    if (f.getAmount() != 0) list.add(f);
                }
            }
        } catch (Exception e) {
            log.error("getInvJournal : " + e.getMessage());
        }
        return list;
    }

    private List<Financial> getInvClosing(String stDate, String enDate, String invGroup, String compCode, Integer macId, boolean detail) {
        List<Financial> list = new ArrayList<>();
        String groupBy = "group by a.coa_code";
        if (!detail) {
            groupBy = "";
        }
        String coaFilter = "select coa_code from chart_of_account where coa_parent='" + invGroup + "' and comp_code ='" + compCode + "'";
        String filter = "and comp_code ='" + compCode + "'\n" + "and dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n";
        String sql = "select a.coa_code,coa.coa_name_eng,sum(a.amount*ifnull(tmp.ex_rate,1)) amount\n" + "from(\n" + "select coa_code,curr_code,comp_code,sum(amount) amount\n" + "from stock_op_value\n" + "where date(tran_date)='" + enDate + "'\n" + "and deleted = false\n" + "and coa_code in (" + coaFilter + ")\n" + filter + "\n" + "group by coa_code\n" + "\tunion all\n" + "select source_ac_id,cur_code,comp_code,amount\n" + "from(\n" + "select source_ac_id,cur_code,comp_code,sum(cr_amt)-sum(dr_amt) amount\n" + "from gl \n" + "where date(gl_date) between '" + stDate + "' and '" + enDate + "'\n" + "and source_ac_id  in (" + coaFilter + ")\n" + "and comp_code ='" + compCode + "'\n" + "and deleted = false\n" + "and dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n" + ")b\n" + "where b.amount<>0" + ")a\n" + "join chart_of_account coa\n" + "on a.coa_code = coa.coa_code\n" + "and a.comp_code = coa.comp_code\n" + "left join tmp_ex_rate tmp\n" + "on a.curr_code = tmp.ex_cur\n" + "and a.comp_code = tmp.comp_code\n" + "and tmp.mac_id = " + macId + "\n" + "and a.comp_code = coa.comp_code\n" + groupBy;
        try {
            ResultSet rs = dao.executeAndResult(sql);
            if (rs != null) {
                while (rs.next()) {
                    Financial f = new Financial();
                    f.setCoaCode(rs.getString("coa_code"));
                    f.setCoaName(rs.getString("coa_name_eng"));
                    f.setAmount(rs.getDouble("amount"));
                    f.setHeadName(COS);
                    f.setGroupName(CL_INV);
                    f.setTranGroup(GP);
                    list.add(f);
                }
            }
        } catch (Exception e) {
            log.error("getInvClosing : " + e.getMessage());
        }
        return list;
    }


    @Override
    public void executeAndResult(String... sql) {
        dao.exeSql(sql);
    }


    @Override
    public void genTriBalance(String compCode, String stDate, String enDate, String opDate, String currency, String coaLv1, String coaLv2, String plProcess, String bsProcess, String projectNo, String tranSource, boolean netChange, Integer macId) {
        String delSql1 = "delete from tmp_tri where mac_id =" + macId;
        String delSql2 = "delete from tmp_closing where mac_id =" + macId;
        dao.exeSql(delSql1, delSql2);
        String coaFilter = "select coa_code from chart_of_account where coa_level >=3 and comp_code='" + compCode + "'";
        if (!coaLv1.equals("-")) {
            coaFilter = "select coa_code \n" + "from chart_of_account \n" + "where coa_parent in (select coa_code from chart_of_account where coa_parent ='" + coaLv1 + "' and comp_code='" + compCode + "')";
        } else if (!coaLv2.equals("-")) {
            coaFilter = "select coa_code from chart_of_account where coa_parent ='" + coaLv2 + "' and comp_code='" + compCode + "'";
        }
        StringBuilder str = new StringBuilder();
        if (!plProcess.equals("-")) {
            String[] data = plProcess.split(",");
            for (String coa : data) {
                str.append(String.format("'%s',", coa));
            }
            str = new StringBuilder(str.substring(0, str.length() - 1));
            coaFilter = "select coa_code \n" + "from chart_of_account \n" + "where coa_parent in (select coa_code from chart_of_account where coa_parent in (" + str + ") and comp_code='" + compCode + "')";
        } else if (!bsProcess.equals("-")) {
            String[] data = bsProcess.split(",");
            for (String coa : data) {
                str.append(String.format("'%s',", coa));
            }
            str = new StringBuilder(str.substring(0, str.length() - 1));
            coaFilter = "select coa_code \n" + "from chart_of_account \n" + "where coa_parent in (select coa_code from chart_of_account where coa_parent in (" + str + ") and comp_code='" + compCode + "')";
        }
        String opSql = "insert into tmp_closing(coa_code, cur_code,dept_code, dr_amt, cr_amt,comp_code,mac_id)\n" +
                "select source_acc_id,cur_code,dept_code,round(if(balance>0,balance,0),2) dr_amt,round(if(balance<0,balance*-1,0),2) cr_amt,'" + compCode + "'," + macId + "\n" +
                "from (\n" + "select source_acc_id,cur_code,dept_code,sum(dr_amt)-sum(cr_amt) balance\n" +
                "from (\n" + "select source_acc_id, cur_code,sum(ifnull(dr_amt,0)) dr_amt,sum(ifnull(cr_amt,0)) cr_amt,dept_code\n" +
                "from coa_opening\n" + "where date(op_date)='" + opDate + "'\n" +
                "and deleted = false\n" +
                "and dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n" +
                "and comp_code = '" + compCode + "'\n" +
                "and (cur_code ='" + currency + "' or '-'='" + currency + "')\n" +
                "and (project_no ='" + projectNo + "' or '-'='" + projectNo + "')\n" +
                "group by source_acc_id, cur_code\n" + "\tunion all\n" + "select account_id, cur_code,sum(ifnull(cr_amt,0)) dr_amt,sum(ifnull(dr_amt,0)) cr_amt,dept_code\n" + "from gl \n" + "where account_id in (" + coaFilter + ")\n" + "and date(gl_date) >='" + opDate + "' and date(gl_date)<'" + stDate + "'\n" + "and dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n" + "and comp_code = '" + compCode + "'\n" + "and deleted = false\n" + "and (cur_code ='" + currency + "' or '-'='" + currency + "')\n" + "and (project_no ='" + projectNo + "' or '-'='" + projectNo + "')\n" + "group by account_id, cur_code\n" + "\tunion all\n" + "select source_ac_id, cur_code,sum(ifnull(dr_amt,0)) dr_amt,sum(ifnull(cr_amt,0)) cr_amt,dept_code\n" + "from gl \n" + "where source_ac_id in (" + coaFilter + ")\n" + "and date(gl_date) >='" + opDate + "' and date(gl_date)<'" + stDate + "'\n" + "and dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n" + "and comp_code = '" + compCode + "'\n" + "and deleted = false\n" + "and (cur_code ='" + currency + "' or '-'='" + currency + "')\n" + "and (project_no ='" + projectNo + "' or '-'='" + projectNo + "')\n" + "group by source_ac_id, cur_code\n" + ")a\n" + "group by source_acc_id,cur_code)b";
        String sql = "insert into tmp_tri(coa_code, curr_id,dept_code, dr_amt, cr_amt,comp_code,mac_id)\n" +
                "select coa_code,cur_code,dept_code,round(if(balance>0,balance,0),2) dr_amt,round(if(balance<0,balance*-1,0),2) cr_amt,'" + compCode + "'," + macId + "\n" + "from (\n" + "select coa_code,cur_code,dept_code,sum(dr_amt)-sum(cr_amt) balance\n" +
                "from (\n" +
                "select coa_code, cur_code,dr_amt,cr_amt,dept_code\n" +
                "from tmp_closing\n" +
                "where mac_id =" + macId + "\n" +
                "and comp_code ='" + compCode + "'\n" +
                "\tunion all\n" + "select account_id, cur_code,sum(ifnull(cr_amt,0)) dr_amt,sum(ifnull(dr_amt,0)) cr_amt,dept_code\n" +
                "from gl \n" +
                "where account_id in (" + coaFilter + ")\n" +
                "and date(gl_date) between '" + stDate + "' and '" + enDate + "'\n" +
                "and dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n" +
                "and comp_code = '" + compCode + "'\n" + "and deleted = false\n" +
                "and (cur_code ='" + currency + "' or '-'='" + currency + "')\n" +
                "and (project_no ='" + projectNo + "' or '-'='" + projectNo + "')\n" +
                "and (tran_source ='" + tranSource + "' or '-'='" + tranSource + "')\n" +
                "group by account_id, cur_code\n" +
                "\tunion all\n" +
                "select source_ac_id, cur_code,sum(ifnull(dr_amt,0)) dr_amt,sum(ifnull(cr_amt,0)) cr_amt,dept_code\n" +
                "from gl \n" +
                "where source_ac_id in (" + coaFilter + ")\n" +
                "and date(gl_date) between '" + stDate + "' and '" + enDate + "'\n" +
                "and dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n" +
                "and comp_code = '" + compCode + "'\n" + "and deleted = false\n" +
                "and (cur_code ='" + currency + "' or '-'='" + currency + "')\n" +
                "and (project_no ='" + projectNo + "' or '-'='" + projectNo + "')\n" +
                "and (tran_source ='" + tranSource + "' or '-'='" + tranSource + "')\n" +
                "group by source_ac_id, cur_code\n" + ")a\n" +
                "group by coa_code,cur_code)b";
        if (!netChange) {
            dao.exeSql(opSql);
        }
        dao.exeSql(sql);
    }


    @Override
    public List<VTriBalance> getTriBalance(String coaCode, String coaLv1, String coaLv2, String compCode, Integer macId) {
        String sql = "select coa_code, curr_id, mac_id, dr_amt, cr_amt, dept_code, coa_code_usr, coa_name_eng\n" +
                "from (\n" +
                "select tmp.*,coa.coa_code_usr,coa.coa_name_eng,coa.coa_parent coa_lv2,coa1.coa_parent coa_lv1\n" +
                "from tmp_tri tmp join chart_of_account coa\n" +
                "on tmp.coa_code = coa.coa_code\n" +
                "and tmp.comp_code = coa.comp_code\n" +
                "join chart_of_account coa1\n" + "on coa.coa_parent = coa1.coa_code\n" +
                "and coa.comp_code =coa1.comp_code\n" +
                "where tmp.mac_id = " + macId + " \n" +
                "and tmp.comp_code='" + compCode + "'\n" +
                "and (tmp.coa_code = '" + coaCode + "' or '-' = '" + coaCode + "'))a\n" +
                "where (a.coa_lv2 = '" + coaLv2 + "' or '-' = '" + coaLv2 + "')\n" +
                "and (a.coa_lv1 = '" + coaLv1 + "' or '-' = '" + coaLv1 + "')\n" +
                "order by coa_code_usr,coa_name_eng";
        ResultSet rs = dao.executeAndResult(sql);
        List<VTriBalance> balances = new ArrayList<>();
        if (!Objects.isNull(rs)) {
            try {
                while (rs.next()) {
                    VTriBalance b = new VTriBalance();
                    b.setCurCode(rs.getString("curr_id"));
                    b.setCoaCode(rs.getString("coa_code"));
                    b.setDrAmt(Util1.toNull(rs.getDouble("dr_amt")));
                    b.setCrAmt(Util1.toNull(rs.getDouble("cr_amt")));
                    b.setCoaUsrCode(rs.getString("coa_code_usr"));
                    b.setCoaName(rs.getString("coa_name_eng"));
                    balances.add(b);
                }
            } catch (Exception e) {
                log.error(String.format("getTriBalance: %s", e.getMessage()));
            }
        }
        return balances;
    }

    @Override
    public List<VApar> genArAp(String compCode, String opDate, String clDate, String currency,
                               String traderCode, String coaCode, String projectNo, Integer macId) {
        String coaFilter = "select distinct account_code from trader where comp_code='" + compCode + "' and account_code is not null";
        if (!coaCode.equals("-")) {
            coaFilter = "'" + coaCode + "'";
        }
        String sql = "select source_acc_id,trader_code,b.cur_code,if(balance>0,balance,0) dr_amt,if(balance<0,balance*-1,0)cr_amt,b.comp_code,t.user_code,t.trader_name,coa.coa_name_eng\n" +
                "from (\n" +
                "select source_acc_id,trader_code,cur_code,round(sum(dr_amt),2) -round(sum(cr_amt),2) balance,comp_code\n" +
                "from (\n" +
                "\tselect source_acc_id,trader_code,cur_code,sum(ifnull(dr_amt,0)) dr_amt, sum(ifnull(cr_amt,0)) cr_amt,comp_code\n" +
                "\tfrom  coa_opening \n" +
                "\twhere comp_code = '" + compCode + "'\n" +
                "\tand deleted = false\n" +
                "\tand date(op_date) = '" + opDate + "'\n" +
                "\tand source_acc_id in (" + coaFilter + ")\n" +
                "\tand (trader_code ='" + traderCode + "' or '-' ='" + traderCode + "')\n" +
                "\tand (project_no ='" + projectNo + "' or '-' ='" + projectNo + "')\n" +
                "\tand (cur_code ='" + currency + "' or '-' ='" + currency + "')\n" +
                "\tand dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n" +
                "\tand trader_code is not null\n" +
                "\tgroup by  cur_code,trader_code,source_acc_id\n" +
                "\t\t\tunion all\n" +
                "\tselect source_ac_id,trader_code,cur_code,sum(ifnull(dr_amt,0)) dr_amt,sum(ifnull(cr_amt,0)) cr_amt,comp_code\n" +
                "\tfrom gl \n" +
                "\twhere source_ac_id in (" + coaFilter + ")\n" +
                "\tand date(gl_date) between  '" + opDate + "' and '" + clDate + "'\n" +
                "\tand comp_code = '" + compCode + "'\n" +
                "\tand deleted = false\n" +
                "\tand (trader_code ='" + traderCode + "' or '-' ='" + traderCode + "')\n" +
                "\tand (project_no ='" + projectNo + "' or '-' ='" + projectNo + "')\n" +
                "\tand (cur_code ='" + currency + "' or '-' ='" + currency + "')\n" +
                "\tand dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n" +
                "\tand trader_code is not null\n" +
                "\tgroup by  cur_code,trader_code,source_ac_id\n" +
                "\t\t\tunion all\n" +
                "\tselect account_id,trader_code,cur_code,sum(ifnull(cr_amt,0)) dr_amt,sum(ifnull(dr_amt,0)) cr_amt,comp_code\n" +
                "\tfrom gl \n" +
                "\twhere account_id in (" + coaFilter + ")\n" +
                "\tand date(gl_date) between  '" + opDate + "' and '" + clDate + "'\n" +
                "\tand comp_code = '" + compCode + "'\n" +
                "\tand deleted = false\n" +
                "\tand (trader_code ='" + traderCode + "' or '-' ='" + traderCode + "')\n" +
                "\tand (project_no ='" + projectNo + "' or '-' ='" + projectNo + "')\n" +
                "\tand (cur_code ='" + currency + "' or '-' ='" + currency + "')\n" +
                "\tand dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n" +
                "\tand trader_code is not null\n" +
                "\tgroup by cur_code,trader_code,account_id\n" + ")a\n" +
                "group by source_acc_id,trader_code,cur_code\n" + ")b\n" +
                "join trader t on b.trader_code = t.code\n" +
                "and b.comp_code = t.comp_code\n" +
                "join chart_of_account coa on b.source_acc_id = coa.coa_code\n" +
                "and b.comp_code = coa.comp_code\n" +
                "order by t.user_code";
        List<VApar> list = new ArrayList<>();
        try {
            ResultSet rs = dao.executeAndResult(sql);
            if (rs != null) {
                while (rs.next()) {
                    VApar a = new VApar();
                    a.setCoaCode(rs.getString("source_acc_id"));
                    a.setCoaName(rs.getString("coa_name_eng"));
                    a.setCompCode(rs.getString("comp_code"));
                    a.setCurCode(rs.getString("cur_code"));
                    a.setTraderCode(rs.getString("trader_code"));
                    a.setUserCode(rs.getString("user_code"));
                    a.setTraderName(rs.getString("trader_name"));
                    a.setDrAmt(Util1.toNull(rs.getDouble("dr_amt")));
                    a.setCrAmt(Util1.toNull(rs.getDouble("cr_amt")));
                    list.add(a);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return list;
    }

    @Override
    public List<Financial> getIncomeAndExpenditure(String process, boolean detail, Integer macId) {
        String[] in = process.split(",");
        List<Financial> list = new ArrayList<>();
        double ttlIncome = 0.0;
        double ttlExpense = 0.0;
        for (int i = 0; i < in.length; i++) {
            String head = in[i];
            String sql = detail ? getHeadSqlDetail(head, macId) : getHeadSqlSummary(head, macId);
            ResultSet rs = dao.executeAndResult(sql);
            if (rs != null) {
                try {
                    while (rs.next()) {
                        Financial f = new Financial();
                        f.setAmount(rs.getDouble("amount"));
                        if (detail) {
                            f.setCoaName(rs.getString("coa_name_eng"));
                        }
                        f.setGroupName(rs.getString("group_name"));
                        f.setHeadName(rs.getString("head_name"));
                        if (i < 2) {
                            ttlIncome += f.getAmount();
                            f.setTranGroup("Income");
                        } else {
                            ttlExpense += f.getAmount();
                            f.setTranGroup("Expense");
                        }
                        list.add(f);
                    }
                    if (!list.isEmpty()) {
                        list.get(0).setTotalIncome(ttlIncome);
                        list.get(0).setTotalExpense(ttlExpense * -1);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
        return list;
    }

    @Override
    public double getTraderLastBalance(String opDate, String toDate, String curCode, String traderCode, String compCode) {
        double lastBalance = 0.0;
        String sql = "select source_acc_id,account_id,cur_code,sum(dr_amt) -sum(cr_amt) balance\n" +
                "from (\n" +
                "\tselect op.source_acc_id,null account_id, op.cur_code,sum(ifnull(op.dr_amt,0)) dr_amt, sum(ifnull(op.cr_amt,0)) cr_amt\n" +
                "\tfrom  coa_opening op\n" +
                "\twhere comp_code = '" + compCode + "'\n" +
                "\tand date(op_date) ='" + opDate + "'\n" +
                "\tand deleted = false\n" +
                "\tand source_acc_id in (select distinct account_code from trader where comp_code='" + compCode + "')\n" +
                "\tand trader_code ='" + traderCode + "'\n" +
                "\tgroup by  op.cur_code,op.trader_code\n" +
                "\t\t\tunion all\n" +
                "select source_ac_id,account_id, cur_code,sum(ifnull(dr_amt,0)) dr_amt,sum(ifnull(cr_amt,0)) cr_amt\n" +
                "\tfrom gl \n" +
                "\twhere source_ac_id in (select distinct account_code from trader where comp_code='" + compCode + "')\n" +
                "\tand date(gl_date) >= '" + opDate + "' \n" +
                "\tand date(gl_date) < '" + toDate + "' \n" +
                "\tand comp_code = '" + compCode + "'\n" +
                "\tand deleted = false\n" +
                "\tand trader_code ='" + traderCode + "'\n" +
                "\tand cur_code ='" + curCode + "'\n" +
                "\tgroup by  cur_code,trader_code\n" +
                "\t\t\tunion all\n" +
                "select account_id,source_ac_id, cur_code,sum(ifnull(cr_amt,0)) dr_amt,sum(ifnull(dr_amt,0)) cr_amt\n" +
                "\tfrom gl \n" +
                "\twhere account_id in (select distinct account_code from trader where comp_code='" + compCode + "')\n" +
                "\tand date(gl_date) >= '" + opDate + "' \n" +
                "\tand date(gl_date) < '" + toDate + "' \n" +
                "\tand comp_code = '" + compCode + "'\n" + "\tand deleted = false\n" +
                "\tand trader_code ='" + traderCode + "'\n" +
                "\tand cur_code ='" + curCode + "'\n" +
                "\tgroup by cur_code,trader_code\n" + ")a\n" +
                "group by cur_code";
        ResultSet rs = dao.executeAndResult(sql);
        try {
            while (rs.next()) {
                lastBalance = rs.getDouble("balance");
            }
        } catch (SQLException e) {
            log.error("getTraderLastBalance: " + e.getMessage());
        }
        return lastBalance;
    }

    @Override
    public ReturnObject getReportResult(Integer macId) {
        return hmRo.get(macId) == null ? new ReturnObject() : hmRo.get(macId);
    }


    @Override
    public List<Gl> getTraderBalance(String traderCode, String accCode, String curCode, String opDate,
                                     String fromDate, String toDate, String compCode, Integer macId) {
        List<Gl> list = new ArrayList<>();
        try {
            String sql = "select source_ac_id,account_id,gl_date,ref_no,description,trader_code, cur_code,\n" +
                    "dr_amt,cr_amt\n" +
                    "from gl\n" +
                    "where  (source_ac_id = '" + accCode + "' or account_id = '" + accCode + "') \n" +
                    "and date(gl_date) between '" + fromDate + "'  and '" + toDate + "' \n" +
                    "and comp_code = '" + compCode + "'\n" +
                    "and deleted = false\n" +
                    "and (cur_code = '" + curCode + "' or '-' ='" + curCode + "')\n" +
                    "and trader_code = '" + traderCode + "' \n" +
                    "and trader_code is not null\n" +
                    "order by gl_date,gl_code";
            ResultSet rs = dao.executeAndResult(sql);
            if (!Objects.isNull(rs)) {
                while (rs.next()) {
                    Gl b = new Gl();
                    b.setSrcAccCode(rs.getString("source_ac_id"));
                    b.setAccCode(rs.getString("account_id"));
                    b.setGlDateStr(Util1.toDateStr(rs.getDate("gl_date"), "dd/MM/yyyy"));
                    b.setOpening(0.0);
                    b.setVouNo(rs.getString("ref_no"));
                    b.setDescription(rs.getString("description"));
                    b.setDrAmt(rs.getDouble("dr_amt"));
                    b.setCrAmt(rs.getDouble("cr_amt"));
                    list.add(b);
                }
            }
            if (!list.isEmpty()) {
                list.forEach(gl -> {
                    String account = Util1.isNull(gl.getAccCode(), "-");
                    if (account.equals(accCode)) {
                        //swap amt
                        double tmpDrAmt = Util1.getDouble(gl.getDrAmt());
                        gl.setDrAmt(gl.getCrAmt());
                        gl.setCrAmt(tmpDrAmt);
                    }
                    gl.setDrAmt(Util1.toNull(gl.getDrAmt()));
                    gl.setCrAmt(Util1.toNull(gl.getCrAmt()));

                });
                double opAmt = getTraderLastBalance(opDate, fromDate, curCode, traderCode, compCode);
                Gl tb = new Gl();
                tb.setRemark("Opening");
                tb.setGlDateStr(Util1.toDateStr(fromDate, "yyyy-MM-dd", "dd/MM/yyyy"));
                tb.setOpening(opAmt);
                tb.setClosing(opAmt);
                list.add(0, tb);
                for (int i = 0; i < list.size(); i++) {
                    if (i > 0) {
                        Gl io = list.get(i - 1);
                        double clAmt = Util1.getDouble(io.getOpening()) + Util1.getDouble(io.getDrAmt()) - Util1.getDouble(io.getCrAmt());
                        Gl io1 = list.get(i);
                        io1.setOpening(clAmt);
                        io1.setClosing(Util1.getDouble(io1.getOpening()) + Util1.getDouble(io1.getDrAmt()) - Util1.getDouble(io1.getCrAmt()));
                    }
                }
                double opening = list.get(0).getOpening();
                double closing = list.get(list.size() - 1).getClosing();
                ReturnObject ro = new ReturnObject();
                ro.setOpAmt(opening);
                ro.setClAmt(closing);
                hmRo.put(macId, ro);
            }

        } catch (Exception ex) {
            log.error(String.format("getTraderBalance :%s", ex.getMessage()));
        }
        return list;
    }

    @Override
    public List<Gl> getSharerHolderStatement(String traderCode, String accCode, String curCode, String opDate, String fromDate, String toDate, String compCode, Integer macId) {
        List<Gl> list = new ArrayList<>();
        try {
            String sql = "select a.*,c1.coa_name_eng source_acc_name,c2.coa_name_eng acc_name\n" +
                    "from (\n" +
                    "select gl_code,source_ac_id,account_id,gl_date,ref_no,description,trader_code, cur_code,dr_amt,cr_amt,comp_code\n" +
                    "from gl\n" +
                    "where  (source_ac_id = ? or account_id = ?) \n" +
                    "and date(gl_date) between ?  and ? \n" +
                    "and comp_code = ?\n" +
                    "and deleted = false\n" +
                    "and cur_code = ?\n" +
                    "and trader_code = ? \n" +
                    "and trader_code is not null\n" +
                    ")a\n" +
                    "join chart_of_account c1 on a.source_ac_id = c1.coa_code\n" +
                    "and a.comp_code = c1.comp_code\n" +
                    "join chart_of_account c2 on a.account_id = c2.coa_code\n" +
                    "and a.comp_code = c2.comp_code\n" +
                    "order by a.gl_date,a.gl_code";
            ResultSet rs = dao.executeAndResult(sql, accCode, accCode, fromDate, toDate, compCode, curCode, traderCode);
            if (!Objects.isNull(rs)) {
                while (rs.next()) {
                    Gl b = new Gl();
                    b.setSrcAccCode(rs.getString("source_ac_id"));
                    b.setAccCode(rs.getString("account_id"));
                    b.setGlDateStr(Util1.toDateStr(rs.getDate("gl_date"), "dd/MM/yyyy"));
                    b.setOpening(0.0);
                    b.setVouNo(rs.getString("ref_no"));
                    b.setDescription(rs.getString("description"));
                    b.setDrAmt(rs.getDouble("dr_amt"));
                    b.setCrAmt(rs.getDouble("cr_amt"));
                    b.setSrcAccName(rs.getString("source_acc_name"));
                    b.setAccName(rs.getString("acc_name"));
                    list.add(b);
                }
            }
            if (!list.isEmpty()) {
                list.forEach(gl -> {
                    String account = Util1.isNull(gl.getAccCode(), "-");
                    if (account.equals(accCode)) {
                        //swap amt
                        double tmpDrAmt = Util1.getDouble(gl.getDrAmt());
                        gl.setDrAmt(gl.getCrAmt());
                        gl.setCrAmt(tmpDrAmt);
                    }
                    gl.setDrAmt(Util1.toNull(gl.getDrAmt()));
                    gl.setCrAmt(Util1.toNull(gl.getCrAmt()));

                });
                double opAmt = getTraderLastBalance(opDate, fromDate, curCode, traderCode, compCode);
                Gl tb = new Gl();
                tb.setRemark("Opening");
                tb.setGlDateStr(Util1.toDateStr(fromDate, "yyyy-MM-dd", "dd/MM/yyyy"));
                tb.setOpening(opAmt);
                tb.setClosing(opAmt);
                if (opAmt != 0) {
                    list.add(0, tb);
                }
                for (int i = 0; i < list.size(); i++) {
                    if (i > 0) {
                        Gl io = list.get(i - 1);
                        double clAmt = Util1.getDouble(io.getOpening()) + Util1.getDouble(io.getDrAmt()) - Util1.getDouble(io.getCrAmt());
                        Gl io1 = list.get(i);
                        io1.setOpening(clAmt);
                        io1.setClosing(Util1.getDouble(io1.getOpening()) + Util1.getDouble(io1.getDrAmt()) - Util1.getDouble(io1.getCrAmt()));
                    }
                }
                double opening = list.get(0).getOpening();
                double closing = list.get(list.size() - 1).getClosing();
                ReturnObject ro = new ReturnObject();
                ro.setOpAmt(opening);
                ro.setClAmt(closing);
                hmRo.put(macId, ro);
            }

        } catch (Exception ex) {
            log.error(String.format("getTraderBalance :%s", ex.getMessage()));
        }
        return list;
    }

    @Override
    public List<Gl> getIndividualStatement(String sourceAcc, String curCode, String opDate, String fromDate, String toDate, String compCode, Integer macId) {
        List<Gl> list = new ArrayList<>();
        String sql = "select a.*,dep.usr_code d_user_code\n" + "from (\n" + "select gl_code,gl_date, description, source_ac_id, account_id, \n" + "cur_code, dr_amt, cr_amt,  dept_code, comp_code\n" + "from gl \n" + "where date(gl_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and comp_code = '" + compCode + "'\n" + "and deleted = false\n" + "and dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n" + "and (account_id = '" + sourceAcc + "' or source_ac_id ='" + sourceAcc + "')\n" + "order by gl_date,tran_source,gl_code\n" + ")a\n" + "join department dep\n" + "on a.dept_code = dep.dept_code\n" + "and a.comp_code = dep.comp_code\n" + "order by a.gl_date,a.gl_code\n";
        ResultSet rs = dao.executeAndResult(sql);
        if (rs != null) {
            try {
                while (rs.next()) {
                    //gl_code, gl_date, description, source_ac_id, account_id, cur_code, dr_amt, cr_amt, dept_code, comp_code, d_user_code
                    Gl gl = new Gl();
                    gl.setGlDateStr(Util1.toDateStr(rs.getDate("gl_date"), "dd/MM/yyyy"));
                    gl.setDescription(rs.getString("description"));
                    gl.setSrcAccCode(rs.getString("source_ac_id"));
                    gl.setAccCode(rs.getString("account_id"));
                    gl.setCurCode(rs.getString("cur_code"));
                    gl.setDrAmt(rs.getDouble("dr_amt"));
                    gl.setCrAmt(rs.getDouble("cr_amt"));
                    gl.setDeptUsrCode(rs.getString("d_user_code"));
                    list.add(gl);
                }
            } catch (Exception e) {
                log.error("getIndividualStatement : " + e.getMessage());
            }
            //swap
            if (!list.isEmpty()) {
                list.forEach(gl -> {
                    String account = Util1.isNull(gl.getAccCode(), "-");
                    if (account.equals(sourceAcc)) {
                        //swap amt
                        double tmpDrAmt = Util1.getDouble(gl.getDrAmt());
                        gl.setDrAmt(gl.getCrAmt());
                        gl.setCrAmt(tmpDrAmt);
                    }
                    gl.setDrAmt(Util1.toNull(gl.getDrAmt()));
                    gl.setCrAmt(Util1.toNull(gl.getCrAmt()));
                });
                try {
                    double opAmt = 0;
                    TmpOpening op = coaOpeningService.getCOAOpening(sourceAcc, opDate, fromDate, curCode, compCode, macId, "-");
                    if (op != null) {
                        opAmt = op.getOpening();
                    }
                    Gl tb = new Gl();
                    tb.setDescription("Opening");
                    tb.setGlDateStr(Util1.toDateStr(fromDate, "yyyy-MM-dd", "dd/MM/yyyy"));
                    tb.setOpening(opAmt);
                    tb.setClosing(opAmt);
                    list.add(0, tb);
                    for (int i = 0; i < list.size(); i++) {
                        if (i > 0) {
                            Gl io = list.get(i - 1);
                            double clAmt = Util1.getDouble(io.getOpening()) + Util1.getDouble(io.getDrAmt()) - Util1.getDouble(io.getCrAmt());
                            Gl io1 = list.get(i);
                            io1.setOpening(clAmt);
                            io1.setClosing(Util1.getDouble(io1.getOpening()) + Util1.getDouble(io1.getDrAmt()) - Util1.getDouble(io1.getCrAmt()));
                        }
                    }
                    double opening = list.get(0).getOpening();
                    double closing = list.get(list.size() - 1).getClosing();
                    ReturnObject ro = new ReturnObject();
                    ro.setOpAmt(opening);
                    ro.setClAmt(closing);
                    hmRo.put(macId, ro);
                } catch (Exception e) {
                    log.error("getIndividualStatement : " + e.getMessage());
                }
            }
        }
        return list;
    }

    @Override
    public List<COAOpening> getOpeningTri(String opDate, String deptCode, String curCode, String compCode) {
        List<COAOpening> list = new ArrayList<>();
        String sql = "select a.cur_code,coa.coa_code_usr,coa.coa_name_eng,dep.usr_code,if(dr_amt-cr_amt>0,dr_amt-cr_amt,0) dr_amt,if(dr_amt-cr_amt<0,(dr_amt-cr_amt)*-1,0) cr_amt\n" +
                "from (\n" +
                "select source_acc_id,cur_code,sum(ifnull(dr_amt,0)) dr_amt,sum(ifnull(cr_amt,0)) cr_amt,dept_code,comp_code\n" +
                "from coa_opening \n" +
                "where comp_code ='" + compCode + "'\n" +
                "and deleted = false\n" +
                "and date(op_date)='" + opDate + "'\n" +
                "and (cur_code ='" + curCode + "' or '-' ='" + curCode + "')\n" +
                "and (dept_code ='" + deptCode + "' or '-' ='" + deptCode + "')\n" +
                "group by source_acc_id,dept_code,cur_code\n" + ")a\n" +
                "join chart_of_account coa\n" +
                "on a.source_acc_id = coa.coa_code\n" +
                "and a.comp_code = coa.comp_code\n" +
                "join department dep\n" +
                "on a.dept_code = dep.dept_code\n" +
                "and a.comp_code = dep.comp_code\n" +
                "where (a.dr_amt >0 or a.cr_amt>0)\n" +
                "order by coa.coa_code_usr";
        ResultSet rs = dao.executeAndResult(sql);
        if (rs != null) {
            try {
                while (rs.next()) {
                    //source_acc_id, cur_code, dr_amt, cr_amt, dept_code, comp_code, coa_name_eng, usr_code
                    COAOpening op = new COAOpening();
                    op.setCoaUsrCode(rs.getString("coa_code_usr"));
                    op.setSrcAccName(rs.getString("coa_name_eng"));
                    op.setDeptUsrCode(rs.getString("usr_code"));
                    op.setCurCode(rs.getString("cur_code"));
                    op.setDrAmt(Util1.toNull(rs.getDouble("dr_amt")));
                    op.setCrAmt(Util1.toNull(rs.getDouble("cr_amt")));
                    list.add(op);
                }
            } catch (Exception e) {
                log.error("getOpeningTri : " + e.getMessage());
            }
        }
        return list;
    }

    @Override
    public List<Gl> getAllCashDaily(String opDate, String fromDate, String toDate, String curCode, String cashGroup, String compCode, Integer macId) {
        List<Gl> list = new ArrayList<>();
        List<String> listCOA = new ArrayList<>();
        String delSql = "delete from tmp_op_cl where mac_id =" + macId;
        dao.exeSql(delSql);
        String sql = """
                select coa_code
                from chart_of_account
                where coa_parent=?
                and comp_code =?
                and active = true
                and deleted = false""";
        ResultSet rs = dao.executeAndResult(sql, cashGroup, compCode);
        if (rs != null) {
            try {
                while (rs.next()) {
                    listCOA.add(rs.getString("coa_code"));
                }
            } catch (Exception e) {
                log.error("getAllCashDaily : " + e.getMessage());
            }
        }
        List<Gl> listGl;
        for (String coaCode : listCOA) {
            try {
                listGl = new ArrayList<>();
                sql = "select a.*,dep.dept_name,coa.coa_name_eng src_acc_name,coa1.coa_name_eng coa_name\n" +
                        "from (\n" +
                        "select source_ac_id,account_id,dept_code,comp_code,sum(dr_amt) dr_amt,sum(cr_amt) cr_amt\n" +
                        "from gl \n" +
                        "where date(gl_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                        "and comp_code = '" + compCode + "'\n" +
                        "and deleted = false\n" +
                        "and dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n" +
                        "and (account_id ='" + coaCode + "' or source_ac_id ='" + coaCode + "')\n" +
                        "group by source_ac_id,dept_code\n" + ")a\n" +
                        "join department dep\n" +
                        "on a.dept_code = dep.dept_code\n" +
                        "and a.comp_code = dep.comp_code\n" +
                        "left join chart_of_account coa\n" +
                        "on a.source_ac_id = coa.coa_code\n" +
                        "and a.comp_code = coa.comp_code\n" +
                        "left join chart_of_account coa1\n" +
                        "on a.account_id = coa1.coa_code\n" +
                        "and a.comp_code = coa1.comp_code";
                rs = getResult(sql);
                if (rs != null) {
                    while (rs.next()) {
                        Gl gl = new Gl();
                        gl.setSrcAccCode(rs.getString("source_ac_id"));
                        gl.setAccCode(rs.getString("account_id"));
                        gl.setSrcAccName(rs.getString("src_acc_name"));
                        gl.setAccName(rs.getString("coa_name"));
                        gl.setDrAmt(rs.getDouble("dr_amt"));
                        gl.setCrAmt(rs.getDouble("cr_amt"));
                        gl.setDeptUsrCode(rs.getString("dept_name"));
                        String account = Util1.isNull(gl.getAccCode(), "-");
                        if (account.equals(coaCode)) {
                            //swap amt
                            double tmpDrAmt = Util1.getDouble(gl.getDrAmt());
                            gl.setDrAmt(gl.getCrAmt());
                            gl.setCrAmt(tmpDrAmt);
                            String name = gl.getSrcAccName();
                            gl.setSrcAccName(gl.getAccName());
                            gl.setAccName(name);
                        }
                        gl.setDrAmt(Util1.toNull(gl.getDrAmt()));
                        gl.setCrAmt(Util1.toNull(gl.getCrAmt()));
                        listGl.add(gl);
                    }
                    if (!listGl.isEmpty()) {
                        TmpOpening tmp = coaOpeningService.getCOAOpening(coaCode, opDate, fromDate, curCode, compCode, macId, "-");
                        if (tmp != null) {
                            listGl.get(0).setOpening(tmp.getOpening());
                        } else {
                            listGl.get(0).setOpening(0.0);
                        }
                        list.addAll(listGl);
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return list;
    }

    @Override
    public List<Financial> getCOAList(String compCode) {
        String sql = "select c1.coa_code_usr h_code,c1.coa_name_eng h_name,\n" + "c2.coa_code_usr g_code,c2.coa_name_eng g_name,\n" + "c3.coa_code_usr user_code,c3.coa_name_eng\n" + "from chart_of_account c3\n" + "join chart_of_account c2\n" + "on c3.coa_parent = c2.coa_code\n" + "and c3.comp_code = c2.comp_code\n" + "join chart_of_account c1\n" + "on c2.coa_parent = c1.coa_code\n" + "and c2.comp_code = c1.comp_code\n" + "and c3.comp_code ='" + compCode + "'\n" + "and c3.coa_level =3\n" + "and c3.active =1\n" + "and c3.deleted = false\n" + "order by c1.coa_code_usr,c1.coa_name_eng,c2.coa_code_usr,c2.coa_name_eng,c3.coa_code_usr,c3.coa_name_eng";
        List<Financial> list = new ArrayList<>();
        ResultSet rs = getResult(sql);
        try {
            while (rs.next()) {
                Financial f = new Financial();
                f.setHeadCode(rs.getString("h_code"));
                f.setHeadName(rs.getString("h_name"));
                f.setGroupCode(rs.getString("g_code"));
                f.setGroupName(rs.getString("g_name"));
                f.setCoaCode(rs.getString("user_code"));
                f.setCoaName(rs.getString("coa_name_eng"));
                list.add(f);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return list;
    }

    @Override
    public Gl getCashBook(String startDate, String endDate, String srcAcc, String curCode, String compCode) {
        String sql = "select sum(a.dr_amt) dr_amt,sum(a.cr_amt) cr_amt\n" +
                "from (\n" +
                "select source_ac_id,sum(dr_amt) dr_amt,sum(cr_amt) cr_amt\n" +
                "from gl \n" +
                "where date(gl_date) between '" + startDate + "' and '" + endDate + "'\n" +
                "and comp_code = '" + compCode + "'\n" +
                "and deleted = false\n" +
                "and source_ac_id ='" + srcAcc + "'\n" +
                "and cur_code ='" + curCode + "'\n" +
                "group by source_ac_id\n" +
                "\tunion all\n" +
                "select account_id,sum(cr_amt) dr_amt,sum(dr_amt) cr_amt\n" +
                "from gl \n" +
                "where date(gl_date) between '" + startDate + "' and '" + endDate + "'\n" +
                "and comp_code = '" + compCode + "'\n" +
                "and deleted = false\n" +
                "and account_id ='" + srcAcc + "'\n" +
                "and cur_code ='" + curCode + "'\n" +
                "group by account_id\n" + ")a\n" +
                "group by source_ac_id\n";
        ResultSet rs = getResult(sql);
        try {
            if (rs.next()) {
                Gl gl = new Gl();
                gl.setDrAmt(Util1.getDouble(rs.getDouble("dr_amt")));
                gl.setCrAmt(Util1.getDouble(rs.getDouble("cr_amt")));
                return gl;
            }
        } catch (Exception e) {
            log.error("getCashBook : " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<VoucherInfo> getIntegrationVoucher(String fromDate, String toDate, String tranSource, String compCode) {
        List<VoucherInfo> list = new ArrayList<>();
        String sql = "";
        if (tranSource.equals("OPD") || tranSource.equals("OT") || tranSource.equals("DC")) {
            sql = "select ref_no,sum(ifnull(dr_amt,0)) amt\n" +
                    "from gl\n" +
                    "where tran_source='" + tranSource + "'\n" +
                    "and deleted =false\n" +
                    "and date(gl_date)  between '" + fromDate + "' and '" + toDate + "'\n" +
                    "and comp_code='" + compCode + "'\n" +
                    "and ifnull(dr_amt,0)>0\n" +
                    "group by ref_no\n" +
                    "order by ref_no;";
        } else {
            sql = "select ref_no,ifnull(dr_amt,0)+ifnull(cr_amt,0) amt\n" +
                    "from gl\n" +
                    "where tran_source='" + tranSource + "'\n" +
                    "and deleted =false\n" +
                    "and date(gl_date)  between '" + fromDate + "' and '" + toDate + "'\n" +
                    "and comp_code='" + compCode + "'\n" +
                    "order by ref_no;";
        }
        ResultSet rs = getResult(sql);
        try {
            while (rs.next()) {
                VoucherInfo info = VoucherInfo.builder()
                        .vouNo(rs.getString("ref_no"))
                        .vouTotal(rs.getDouble("amt"))
                        .build();
                list.add(info);
            }
        } catch (Exception e) {
            log.error("getVoucherInfo : " + e.getMessage());
        }
        return list;
    }


    private String getHeadSqlDetail(String head, Integer macId) {
        return "select tmp.coa_code,tmp.curr_id, tmp.cr_amt-tmp.dr_amt amount,\n" +
                "coa1.coa_name_eng,coa2.coa_name_eng group_name,coa3.coa_name_eng head_name\n" +
                "from tmp_tri tmp \n" + "join chart_of_account coa1\n" + "on tmp.coa_code = coa1.coa_code\n" + "and tmp.comp_code = coa1.comp_code\n" + "join chart_of_account coa2\n" + "on coa1.coa_parent = coa2.coa_code\n" + "and coa1.comp_code = coa2.comp_code\n" + "join chart_of_account coa3\n" + "on coa2.coa_parent = coa3.coa_code\n" + "and coa2.comp_code = coa3.comp_code\n" + "where tmp.mac_id =" + macId + "\n" + "and coa2.coa_parent='" + head + "'\n" + "and (dr_amt<>0 or cr_amt<>0)\n" + "order by coa3.coa_code_usr,coa3.coa_name_eng,coa2.coa_code_usr,coa2.coa_name_eng,coa1.coa_code_usr,coa1.coa_name_eng";
    }

    private String getHeadSqlSummary(String head, Integer macId) {
        return "select tmp.coa_code,tmp.curr_id, sum(tmp.cr_amt-tmp.dr_amt) amount,coa2.coa_name_eng group_name,coa3.coa_name_eng head_name\n" + "from tmp_tri tmp \n" + "join chart_of_account coa1\n" + "on tmp.coa_code = coa1.coa_code\n" + "and tmp.comp_code = coa1.comp_code\n" + "join chart_of_account coa2\n" + "on coa1.coa_parent = coa2.coa_code\n" + "and coa1.comp_code = coa2.comp_code\n" + "join chart_of_account coa3\n" + "on coa2.coa_parent = coa3.coa_code\n" + "and coa2.comp_code = coa3.comp_code\n" + "where tmp.mac_id =" + macId + "\n" + "and coa2.coa_parent='" + head + "'\n" + "and (tmp.dr_amt<>0 or tmp.cr_amt<>0)\n" + "group by group_name\n" + "order by coa3.coa_code_usr,coa3.coa_name_eng,coa2.coa_code_usr,coa2.coa_name_eng,coa1.coa_code_usr,coa1.coa_name_eng";
    }

    private String getOpeningHeadDetail(String opDate, String headCode, String compCode) {
        return "select op.op_date,op.source_acc_id,op.trader_code,op.cur_code,sum(ifnull(cr_amt,0)-ifnull(dr_amt,0)) amount,\n" + "coa3.coa_name_eng,coa2.coa_name_eng group_name,coa1.coa_name_eng head_name\n" + "from coa_opening op join chart_of_account coa3 on \n" + "op.source_acc_id = coa3.coa_code\n" + "and op.comp_code = coa3.comp_code\n" + "join chart_of_account coa2 on coa3.coa_parent = coa2.coa_code\n" + "and coa3.comp_code = coa2.comp_code\n" + "join chart_of_account coa1 on coa2.coa_parent = coa1.coa_code\n" + "and coa2.comp_code = coa1.comp_code\n" + "and coa1.coa_code='" + headCode + "'\n" + "where op.deleted = false\n" + "and op.comp_code ='" + compCode + "'\n" + "and (ifnull(dr_amt,0) >0 or ifnull(cr_amt,0))\n" +
                "and date(op.op_date)='" + opDate + "'\n" +
                "group by source_acc_id\n" +
                "order by coa3.coa_code_usr,coa2.coa_code_usr,coa2.coa_name_eng";
    }

    private String getOpeningHeadSummary(String opDate, String headCode, String compCode) {
        return "select op.op_date,op.source_acc_id,op.trader_code,op.cur_code,sum(ifnull(cr_amt,0)-ifnull(dr_amt,0)) amount,\n" + "coa2.coa_name_eng group_name,coa1.coa_name_eng head_name\n" + "from coa_opening op join chart_of_account coa3 on \n" + "op.source_acc_id = coa3.coa_code\n" + "and op.comp_code = coa3.comp_code\n" + "join chart_of_account coa2 on coa3.coa_parent = coa2.coa_code\n" + "and coa3.comp_code = coa2.comp_code\n" + "join chart_of_account coa1 on coa2.coa_parent = coa1.coa_code\n" + "and coa2.comp_code = coa1.comp_code\n" + "and coa1.coa_code='" + headCode + "'\n" + "where op.deleted = false\n" + "and op.comp_code ='" + compCode + "'\n" + "and (ifnull(dr_amt,0) >0 or ifnull(cr_amt,0))\n" + "and date(op.op_date)='" + opDate + "'\n" + "group by coa2.coa_code\n" + "order by coa3.coa_code_usr,coa2.coa_code_usr,coa2.coa_name_eng";
    }
}
