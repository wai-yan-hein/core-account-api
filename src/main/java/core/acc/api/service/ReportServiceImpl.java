package core.acc.api.service;

import core.acc.api.common.Util1;
import core.acc.api.dao.ReportDao;
import core.acc.api.entity.*;
import core.acc.api.model.Financial;
import core.acc.api.model.ReturnObject;
import core.acc.api.model.TraderBalance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Transactional
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportDao dao;
    private static final String OP_INV = "Opening Inventory";
    private static final String CL_INV = "Closing Inventory";
    private static final String GP = "Gross Profit (Loss)";
    private static final String NP = "Net Profit (Loss)";
    private static final String COS = "Cost of Sale";
    private final HashMap<Integer, ReturnObject> hmRo = new HashMap<>();

    @Override
    public void insertTmp(List<String> listStr, Integer macId, String taleName) {
        try {
            if (listStr != null) {
                deleteTmp(taleName, macId);
                for (String str : listStr) {
                    String sql = "insert into " + taleName + "(dept_code,mac_id)\n" +
                            "select '" + str + "'," + macId + "";
                    executeSql(sql);
                }
            }
        } catch (Exception e) {
            log.error(String.format("insertTmp: %s", e.getMessage()));
        }
    }

    @Override
    public ResultSet getResult(String sql) {
        return dao.executeSql(sql);
    }


    @Override
    public String getOpeningDate(String compCode) {
        String opDate = null;
        String sql = "select max(op_date) op_date from coa_opening where comp_code ='" + compCode + "'";
        try {
            ResultSet rs = dao.executeSql(sql);
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
    public List<Gl> getIndividualLager(String fromDate, String toDate, String desp, String srcAcc,
                                       String acc, String curCode, String reference,
                                       String compCode, String tranSource, String traderCode, String traderType,
                                       String coaLv2, String coaLv1, boolean summary, Integer macId) throws SQLException {
        String filter = "where  (a.trader_code ='" + traderCode + "' or '-'='" + traderCode + "')\n";
        if (!coaLv2.equals("-")) {
            filter += "and coa3.coa_parent = '" + coaLv2 + "'\n";
        }
        if (!coaLv1.equals("-")) {
            filter += "and coa2.coa_parent = '" + coaLv1 + "'\n";
        }
        if (!tranSource.equals("-")) {
            filter += "and a.tran_source = '" + tranSource + "'\n";
        }
        if (!reference.equals("-")) {
            filter += "and a.reference like '" + reference + "%'\n";
        }
        if (!desp.equals("-")) {
            filter += "and a.description like '" + desp + "%'\n";
        }
        if (!acc.equals("-")) {
            filter += "and (a.account_id = '" + acc + "' or a.source_ac_id ='" + acc + "')";
        }
        if (!traderType.equals("-")) {
            filter += "and  a.discriminator ='" + traderType + "' \n";
        }
        if (!curCode.equals("-")) {
            filter += "and a.cur_code ='" + curCode + "'\n";
        }
        List<Gl> list = new ArrayList<>();
        if (summary) {
            String sql = "select a.*,dep.usr_code d_user_code,coa.coa_name_eng src_acc_name,coa3.coa_name_eng acc_name\n" +
                    "from (\n" +
                    "select gl_date,gl_code,dept_id,cur_code,source_ac_id,account_id,dept_code,trader_code,comp_code,sum(dr_amt) dr_amt,sum(cr_amt) cr_amt\n" +
                    "from gl \n" +
                    "where date(gl_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                    "and comp_code = '" + compCode + "'\n" +
                    "and dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n" +
                    "and (account_id = '" + srcAcc + "' or source_ac_id ='" + srcAcc + "')\n" +
                    "group by source_ac_id,account_id,dept_code\n" +
                    ")a\n" +
                    "join department dep\n" +
                    "on a.dept_code = dep.dept_code\n" +
                    "and a.comp_code = dep.comp_code\n" +
                    "join chart_of_account coa\n" +
                    "on a.source_ac_id = coa.coa_code\n" +
                    "and a.comp_code = coa.comp_code\n" +
                    "left join chart_of_account coa3\n" +
                    "on a.account_id = coa3.coa_code\n" +
                    "and a.comp_code = coa3.comp_code\n" +
                    "left join chart_of_account coa2\n" +
                    "on coa3.coa_parent = coa2.coa_code\n" +
                    "and coa3.comp_code = coa2.comp_code\n" +
                    "" + filter + "\n" +
                    "order by coa.coa_code_usr\n";
            ResultSet rs = dao.executeSql(sql);
            if (!Objects.isNull(rs)) {
                while (rs.next()) {
                    Gl v = new Gl();
                    GlKey key = new GlKey();
                    key.setCompCode(compCode);
                    key.setGlCode(rs.getString("gl_code"));
                    key.setDeptId(rs.getInt("dept_id"));
                    v.setKey(key);
                    v.setGlDate(rs.getDate("gl_date"));
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
                    "from (\n" +
                    "select gl_code, gl_date, created_date, description, source_ac_id, account_id, \n" +
                    "cur_code, dr_amt, cr_amt, reference, dept_code, voucher_no, trader_code, comp_code, tran_source, gl_vou_no,\n" +
                    "remark, mac_id, ref_no,dept_id\n" +
                    "from gl \n" +
                    "where date(gl_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                    "and comp_code = '" + compCode + "'\n" +
                    "and dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n" +
                    "and (account_id = '" + srcAcc + "' or source_ac_id ='" + srcAcc + "')\n" +
                    "order by gl_date,tran_source,gl_code\n" +
                    ")a\n" +
                    "join department dep\n" +
                    "on a.dept_code = dep.dept_code\n" +
                    "and a.comp_code = dep.comp_code\n" +
                    "left join trader t on \n" +
                    "a.trader_code = t.code\n" +
                    "and a.comp_code = t.comp_code\n" +
                    "join chart_of_account coa\n" +
                    "on a.source_ac_id = coa.coa_code\n" +
                    "and a.comp_code = coa.comp_code\n" +
                    "left join chart_of_account coa3\n" +
                    "on a.account_id = coa3.coa_code\n" +
                    "and a.comp_code = coa3.comp_code\n" +
                    "left join chart_of_account coa2\n" +
                    "on coa3.coa_parent = coa2.coa_code\n" +
                    "and coa3.comp_code = coa2.comp_code\n" +
                    "" + filter + "\n" +
                    "order by a.gl_date,a.tran_source,a.gl_code\n";
            ResultSet rs = dao.executeSql(sql);
            if (!Objects.isNull(rs)) {
                while (rs.next()) {
                    Gl v = new Gl();
                    GlKey key = new GlKey();
                    key.setCompCode(compCode);
                    key.setGlCode(rs.getString("gl_code"));
                    key.setDeptId(rs.getInt("dept_id"));
                    v.setKey(key);
                    v.setGlDate(rs.getDate("gl_date"));
                    v.setVouDate(Util1.toDateStr(v.getGlDate(), "dd/MM/yyyy"));
                    v.setDescription((rs.getString("description")));
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
                    list.add(v);
                }
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
                    gl.setAccName(gl.getSrcAccName());
                    gl.setSrcAccName(tmpStr);
                }
                gl.setDrAmt(Util1.toNull(gl.getDrAmt()));
                gl.setCrAmt(Util1.toNull(gl.getCrAmt()));
            });
        }
        return list;
    }

    private void deleteTmp(String tableName, Integer macId) {
        String delSql = "delete from " + tableName + " where mac_id =" + macId + "";
        executeSql(delSql);
    }

    @Override
    public List<Financial> getProfitLost(String plProcess, String opDate, String stDate, String enDate, String invGroup,
                                         boolean detail, String compCode, Integer macId) {
        genTriBalance(compCode, stDate, enDate, opDate, "-", "-", "-", plProcess, "-", true, macId);
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
                    List<Financial> listOP = getInvOpeningDetail(opDate, stDate, enDate, invGroup, compCode, macId);
                    list.addAll(listOP);
                    for (Financial f : listOP) {
                        ttlOpInv += f.getAmount();
                    }
                }
                String head = process[i];
                sql = detail ? getHeadSqlDetail(head, macId) : getHeadSqlSummary(head, macId);
                ResultSet rs = dao.executeSql(sql);
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
                    List<Financial> listCL = getInvClosingDetail(enDate, invGroup, compCode, macId);
                    list.addAll(listCL);
                    for (Financial f : listCL) {
                        ttlClInv += f.getAmount();
                    }
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
    public double getProfit(String opDate, String stDate, String enDate, String invGroup, String plProcess, String compCode, Integer macId) {
        genTriBalance(compCode, stDate, enDate, opDate, "-", "-", "-", plProcess, "-", true, macId);
        AtomicReference<Double> opAmt = new AtomicReference<>(0.0);
        AtomicReference<Double> clAmt = new AtomicReference<>(0.0);
        List<Financial> opList = getInvOpeningDetail(opDate, stDate, enDate, invGroup, compCode, macId);
        opList.forEach(op -> opAmt.updateAndGet(v -> v + op.getAmount()));
        List<Financial> clList = getInvClosingDetail(enDate, invGroup, compCode, macId);
        clList.forEach(op -> clAmt.updateAndGet(v -> v + op.getAmount()));
        double amt = opAmt.get() + clAmt.get();
        String sql = "select sum(dr_amt)-sum(cr_amt)+" + amt + " profit\n" +
                "from tmp_tri \n" +
                "where mac_id =" + macId + "";
        try {
            ResultSet rs = dao.executeSql(sql);
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
    public List<Financial> getBalanceSheet(String bsProcess, String opDate, String stDate, String enDate, String invGroup,
                                           boolean detail, double prvProfit, double curProfit, String compCode, Integer macId) {
        genTriBalance(compCode, stDate, enDate, opDate, "-", "-", "-", "-", bsProcess, false, macId);
        List<Financial> list = new ArrayList<>();
        if (!bsProcess.equals("-")) {
            updateInvClosing(enDate, invGroup, compCode, macId);
            String[] process = bsProcess.split(",");
            //fix,cur,lia,capital
            for (int i = 0; i < process.length; i++) {
                String head = process[i];
                String sql = detail ? getHeadSqlDetail(head, macId) : getHeadSqlSummary(head, macId);
                ResultSet rs = dao.executeSql(sql);
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
                    if (i == 3) {
                        Financial f = new Financial();
                        if (prvProfit != 0) {
                            f.setCoaName("Retained earning");
                            f.setAmount(prvProfit);
                            f.setGroupName(f.getCoaName());
                            f.setHeadName("CAPITAL");
                            f.setTranGroup("TOTAL CAPITAL AND LIABILITIES");
                            list.add(f);
                        }
                        f = new Financial();
                        f.setCoaName("Net Profit (Loss)");
                        f.setAmount(curProfit);
                        f.setGroupName(f.getCoaName());
                        f.setHeadName("CAPITAL");
                        f.setTranGroup("TOTAL CAPITAL AND LIABILITIES");
                        list.add(f);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
        return list;
    }

    private void updateInvClosing(String enDate, String invGroup, String compCode, Integer macId) {
        List<Financial> list = getInvClosingDetail(enDate, invGroup, compCode, macId);
        list.forEach(f -> {
            double amt = f.getAmount() * -1;
            String sql = "update tmp_tri\n" +
                    "set dr_amt = " + amt + "\n" +
                    "where mac_id =" + macId + "\n" +
                    "and comp_code ='" + compCode + "'\n" +
                    "and coa_code ='" + f.getCoaCode() + "'";
            dao.exeSql(sql);
        });
    }

    private List<Financial> getInvOpeningDetail(String opDate, String stDate, String enDate, String invGroup, String compCode, Integer macId) {
        List<Financial> list = new ArrayList<>();
        String sql = "select coa.coa_name_eng,amount*ifnull(tmp.ex_rate,1) amount\n" +
                "from (\n" +
                "select DATE_SUB(op_date, INTERVAL 1 DAY) op_date,source_acc_id,dept_code,cur_code,comp_code,sum(dr_amt) amount\n" +
                "from coa_opening\n" +
                "where source_acc_id in (select coa_code from chart_of_account where coa_parent='" + invGroup + "' and comp_code ='" + compCode + "')\n" +
                "and dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n" +
                "and date(op_date)='" + opDate + "'\n" +
                "and dr_amt>0\n" +
                "and comp_code ='" + compCode + "'\n" +
                "group by source_acc_id,cur_code\n" +
                "\tunion\n" +
                "select date(tran_date)tran_date,coa_code,dept_code,curr_code,comp_code,sum(amount) amount\n" +
                "from stock_op_value\n" +
                "where coa_code in (select coa_code from chart_of_account where coa_parent='" + invGroup + "' and comp_code ='" + compCode + "')\n" +
                "and dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n" +
                "and date(tran_date) between '" + opDate + "' and '" + enDate + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and deleted =0\n" +
                "group by tran_date,coa_code,curr_code\n" +
                ")a\n" +
                "join chart_of_account coa on a.source_acc_id = coa.coa_code\n" +
                "and a.comp_code=coa.comp_code\n" +
                "left join tmp_ex_rate tmp\n" +
                "on a.cur_code = tmp.ex_cur\n" +
                "and a.comp_code = tmp.comp_code\n" +
                "and tmp.mac_id =" + macId + "\n" +
                "where a.op_date = DATE_SUB('" + stDate + "', INTERVAL 1 DAY)\n";
        try {
            ResultSet rs = dao.executeSql(sql);
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
            log.error(e.getMessage());
        }
        return list;
    }

    private List<Financial> getInvClosingDetail(String enDate, String invGroup, String compCode, Integer macId) {
        List<Financial> list = new ArrayList<>();
        String coaFilter = "select coa_code from chart_of_account where coa_parent='" + invGroup + "' and comp_code ='" + compCode + "'";
        String filter = "and comp_code ='" + compCode + "'\n" +
                "and dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n";
        String sql = "select a.coa_code,coa.coa_name_eng,sum(a.amount*ifnull(tmp.ex_rate,1))*-1 amount\n" +
                "from(\n" +
                "select coa_code,curr_code,comp_code,sum(amount) amount\n" +
                "from stock_op_value\n" +
                "where date(tran_date)='" + enDate + "'\n" +
                "and deleted =0\n" +
                "and coa_code in (" + coaFilter + ")\n" +
                "" + filter + "\n" +
                "group by coa_code\n" +
                ")a\n" +
                "join chart_of_account coa\n" +
                "on a.coa_code = coa.coa_code\n" +
                "left join tmp_ex_rate tmp\n" +
                "on a.curr_code = tmp.ex_cur\n" +
                "and a.comp_code = tmp.comp_code\n" +
                "and tmp.mac_id = " + macId + "\n" +
                "and a.comp_code = coa.comp_code\n" +
                "group by a.coa_code";
        try {
            ResultSet rs = dao.executeSql(sql);
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
            log.error(e.getMessage());
        }
        return list;
    }

    private double getInvJournalAmt(String stDate, String enDate, String invGroup, String compCode, Integer macId) {
        String sql = "select sum(amount*ifnull(tmp.ex_rate,1))*-1 amount\n" +
                "from (\n" +
                "select cur_code,comp_code,sum(dr_amt)-sum(cr_amt) amount\n" +
                "from gl \n" +
                "where date(gl_date) between '" + stDate + "' and '" + enDate + "'\n" +
                "and source_ac_id  in (select coa_code from chart_of_account where coa_parent='" + invGroup + "' and comp_code ='" + compCode + "')\n" +
                "and dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n" +
                "group by source_ac_id,cur_code\n" +
                ")a\n" +
                "left join tmp_ex_rate tmp\n" +
                "on a.cur_code = tmp.ex_cur\n" +
                "and a.comp_code = tmp.comp_code\n" +
                "and tmp.mac_id = " + macId + "";
        ResultSet rs = dao.executeSql(sql);
        try {
            if (rs.next()) {
                return rs.getDouble("amount");
            }
        } catch (Exception e) {
            log.error("getInvJournalAmt : " + e.getMessage());
        }
        return 0;
    }


    @Override
    public void executeSql(String... sql) {
        dao.exeSql(sql);
    }


    @Override
    public void genTriBalance(String compCode, String stDate, String enDate,
                              String opDate, String currency, String coaLv1, String coaLv2,
                              String plProcess, String bsProcess,
                              boolean netChange, Integer macId) {
        String delSql1 = "delete from tmp_tri where mac_id =" + macId + "";
        String delSql2 = "delete from tmp_closing where mac_id =" + macId + "";
        dao.exeSql(delSql1, delSql2);
        String coaFilter = "select coa_code from chart_of_account where coa_level >=3 and comp_code='" + compCode + "'";
        if (!coaLv1.equals("-")) {
            coaFilter = "select coa_code \n" +
                    "from chart_of_account \n" +
                    "where coa_parent in (select coa_code from chart_of_account where coa_parent ='" + coaLv1 + "' and comp_code='" + compCode + "')";
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
            coaFilter = "select coa_code \n" +
                    "from chart_of_account \n" +
                    "where coa_parent in (select coa_code from chart_of_account where coa_parent in (" + str + ") and comp_code='" + compCode + "')";
        } else if (!bsProcess.equals("-")) {
            String[] data = bsProcess.split(",");
            for (String coa : data) {
                str.append(String.format("'%s',", coa));
            }
            str = new StringBuilder(str.substring(0, str.length() - 1));
            coaFilter = "select coa_code \n" +
                    "from chart_of_account \n" +
                    "where coa_parent in (select coa_code from chart_of_account where coa_parent in (" + str + ") and comp_code='" + compCode + "')";
        }
        String opSql = "insert into tmp_closing(coa_code, cur_code,dept_code, dr_amt, cr_amt,comp_code,mac_id)\n" +
                "select source_acc_id,cur_code,dept_code,round(if(balance>0,balance,0),2) dr_amt,round(if(balance<0,balance*-1,0),2) cr_amt,'" + compCode + "'," + macId + "\n" +
                "from (\n" +
                "select source_acc_id,cur_code,dept_code,sum(dr_amt)-sum(cr_amt) balance\n" +
                "from (\n" +
                "select source_acc_id, cur_code,sum(ifnull(dr_amt,0)) dr_amt,sum(ifnull(cr_amt,0)) cr_amt,dept_code\n" +
                "from coa_opening\n" +
                "where date(op_date)='" + opDate + "'\n" +
                "and dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n" +
                "and comp_code = '" + compCode + "'\n" +
                "and (cur_code ='" + currency + "' or '-'='" + currency + "')\n" +
                "and (dr_amt>0 or cr_amt>0)\n" +
                "group by source_acc_id, cur_code\n" +
                "\tunion all\n" +
                "select account_id, cur_code,sum(ifnull(cr_amt,0)) dr_amt,sum(ifnull(dr_amt,0)) cr_amt,dept_code\n" +
                "from gl \n" +
                "where account_id in (" + coaFilter + ")\n" +
                "and date(gl_date) >='" + opDate + "' and date(gl_date)<'" + stDate + "'\n" +
                "and dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n" +
                "and comp_code = '" + compCode + "'\n" +
                "and (cur_code ='" + currency + "' or '-'='" + currency + "')\n" +
                "group by account_id, cur_code\n" +
                "\tunion all\n" +
                "select source_ac_id, cur_code,sum(ifnull(dr_amt,0)) dr_amt,sum(ifnull(cr_amt,0)) cr_amt,dept_code\n" +
                "from gl \n" +
                "where source_ac_id in (" + coaFilter + ")\n" +
                "and date(gl_date) >='" + opDate + "' and date(gl_date)<'" + stDate + "'\n" +
                "and dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n" +
                "and comp_code = '" + compCode + "'\n" +
                "and (cur_code ='" + currency + "' or '-'='" + currency + "')\n" +
                "group by source_ac_id, cur_code\n" +
                ")a\n" +
                "group by source_acc_id,cur_code)b";

        String sql = "insert into tmp_tri(coa_code, curr_id,dept_code, dr_amt, cr_amt,comp_code,mac_id)\n" +
                "select coa_code,cur_code,dept_code,round(if(balance>0,balance,0),2) dr_amt,round(if(balance<0,balance*-1,0),2) cr_amt,'" + compCode + "'," + macId + "\n" +
                "from (\n" +
                "select coa_code,cur_code,dept_code,sum(dr_amt)-sum(cr_amt) balance\n" +
                "from (\n" +
                "select coa_code, cur_code,dr_amt,cr_amt,dept_code\n" +
                "from tmp_closing\n" +
                "where mac_id =" + macId + "\n" +
                "and comp_code ='" + compCode + "'\n" +
                "\tunion all\n" +
                "select account_id, cur_code,sum(ifnull(cr_amt,0)) dr_amt,sum(ifnull(dr_amt,0)) cr_amt,dept_code\n" +
                "from gl \n" +
                "where account_id in (" + coaFilter + ")\n" +
                "and date(gl_date) between '" + stDate + "' and '" + enDate + "'\n" +
                "and dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n" +
                "and comp_code = '" + compCode + "'\n" +
                "and (cur_code ='" + currency + "' or '-'='" + currency + "')\n" +
                "group by account_id, cur_code\n" +
                "\tunion all\n" +
                "select source_ac_id, cur_code,sum(ifnull(dr_amt,0)) dr_amt,sum(ifnull(cr_amt,0)) cr_amt,dept_code\n" +
                "from gl \n" +
                "where source_ac_id in (" + coaFilter + ")\n" +
                "and date(gl_date) between '" + stDate + "' and '" + enDate + "'\n" +
                "and dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n" +
                "and comp_code = '" + compCode + "'\n" +
                "and (cur_code ='" + currency + "' or '-'='" + currency + "')\n" +
                "group by source_ac_id, cur_code\n" +
                ")a\n" +
                "group by coa_code,cur_code)b";
        if (!netChange) {
            dao.exeSql(opSql);
        }
        dao.exeSql(sql);
    }

    @Override
    public List<VTriBalance> getTriBalance(String coaCode, String coaLv1, String coaLv2, Integer macId) {
        String sql = "select coa_code, curr_id, mac_id, dr_amt, cr_amt, dept_code, coa_code_usr, coa_name_eng\n" +
                "from (\n" +
                "select tmp.*,coa.coa_code_usr,coa.coa_name_eng,coa.coa_parent coa_lv2,coa1.coa_parent coa_lv1\n" +
                "from tmp_tri tmp join chart_of_account coa\n" +
                "on tmp.coa_code = coa.coa_code\n" +
                "join chart_of_account coa1\n" +
                "on coa.coa_parent = coa1.coa_code\n" +
                "where tmp.mac_id = " + macId + " \n" +
                "and (tmp.coa_code = '" + coaCode + "' or '-' = '" + coaCode + "'))a\n" +
                "where (a.coa_lv2 = '" + coaLv2 + "' or '-' = '" + coaLv2 + "')\n" +
                "and (a.coa_lv1 = '" + coaLv1 + "' or '-' = '" + coaLv1 + "')";
        ResultSet rs = dao.executeSql(sql);
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
                               String traderCode, String coaCode, Integer macId) {
        String coaFilter = "select distinct account_code from trader where comp_code='" + compCode + "' and account_code is not null";
        if (!coaCode.equals("-")) {
            coaFilter = "'" + coaCode + "'";
        }
        String sql = "select trader_code,cur_code,if(balance>0,balance,0) dr_amt,if(balance<0,balance*-1,0)cr_amt,b.comp_code,t.user_code,t.trader_name,t.account_code\n" +
                "from (\n" +
                "select trader_code,cur_code,sum(dr_amt) -sum(cr_amt) balance,comp_code\n" +
                "from (\n" +
                "\tselect trader_code,cur_code,sum(ifnull(dr_amt,0)) dr_amt, sum(ifnull(cr_amt,0)) cr_amt,comp_code\n" +
                "\tfrom  coa_opening \n" +
                "\twhere comp_code = '" + compCode + "'\n" +
                "\tand date(op_date) = '" + opDate + "'\n" +
                "\tand source_acc_id in (" + coaFilter + ")\n" +
                "\tand (trader_code ='" + traderCode + "' or '-' ='" + traderCode + "')\n" +
                "\tand dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n" +
                "\tand trader_code is not null\n" +
                "\tgroup by  cur_code,trader_code\n" +
                "\t\t\tunion all\n" +
                "\tselect trader_code,cur_code,sum(ifnull(dr_amt,0)) dr_amt,sum(ifnull(cr_amt,0)) cr_amt,comp_code\n" +
                "\tfrom gl \n" +
                "\twhere source_ac_id in (" + coaFilter + ")\n" +
                "\tand date(gl_date) between  '" + opDate + "' and '" + clDate + "'\n" +
                "\tand comp_code = '" + compCode + "'\n" +
                "\tand (trader_code ='" + traderCode + "' or '-' ='" + traderCode + "')\n" +
                "\tand dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n" +
                "\tand trader_code is not null\n" +
                "\tgroup by  cur_code,trader_code\n" +
                "\t\t\tunion all\n" +
                "\tselect trader_code,cur_code,sum(ifnull(cr_amt,0)) dr_amt,sum(ifnull(dr_amt,0)) cr_amt,comp_code\n" +
                "\tfrom gl \n" +
                "\twhere account_id in (" + coaFilter + ")\n" +
                "\tand date(gl_date) between  '" + opDate + "' and '" + clDate + "'\n" +
                "\tand comp_code = '" + compCode + "'\n" +
                "\tand (trader_code ='" + traderCode + "' or '-' ='" + traderCode + "')\n" +
                "\tand dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n" +
                "\tand trader_code is not null\n" +
                "\tgroup by cur_code,trader_code\n" +
                ")a\n" +
                "group by trader_code,cur_code\n" +
                ")b\n" +
                "join trader t on b.trader_code = t.code\n" +
                "and b.comp_code = t.comp_code\n" +
                "where balance<>0\n" +
                "order by t.user_code";
        List<VApar> list = new ArrayList<>();
        try {
            ResultSet rs = dao.executeSql(sql);
            if (rs != null) {
                while (rs.next()) {
                    VApar a = new VApar();
                    a.setCoaCode(rs.getString("account_code"));
                    a.setCompCode(compCode);
                    a.setCurCode(rs.getString("cur_code"));
                    a.setTraderCode(rs.getString("trader_code"));
                    a.setUserCode(rs.getString("user_code"));
                    a.setTraderName(rs.getString("trader_name"));
                    a.setDrAmt(rs.getDouble("dr_amt"));
                    a.setCrAmt(rs.getDouble("cr_amt"));
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
        String delSql = "delete from tmp_in_ex where mac_id = " + macId + "";
        dao.exeSql(delSql);
        double ttlIncome = 0.0;
        double ttlExpense = 0.0;
        for (int i = 0; i < in.length; i++) {
            String head = in[i];
            String sql = detail ? getHeadSqlDetail(head, macId) : getHeadSqlSummary(head, macId);
            ResultSet rs = dao.executeSql(sql);
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
    public double getTraderLastBalance(String toDate, String traderCode, String compCode) {
        double lastBalance = 0.0;
        String sql = "select source_acc_id,account_id,cur_code,sum(dr_amt) -sum(cr_amt) balance\n" +
                "from (\n" +
                "\tselect op.source_acc_id,null account_id, op.cur_code,sum(ifnull(op.dr_amt,0)) dr_amt, sum(ifnull(op.cr_amt,0)) cr_amt\n" +
                "\tfrom  coa_opening op\n" +
                "\twhere\n" +
                "\tcomp_code = '" + compCode + "'\n" +
                "\tand source_acc_id in (select distinct account_code from trader where comp_code='" + compCode + "')\n" +
                "\tand trader_code ='" + traderCode + "'\n" +
                "\tgroup by  op.cur_code,op.trader_code\n" +
                "\t\t\tunion all\n" +
                "    select source_ac_id,account_id, cur_code,sum(ifnull(dr_amt,0)) dr_amt,sum(ifnull(cr_amt,0)) cr_amt\n" +
                "\tfrom gl \n" +
                "\twhere source_ac_id in (select distinct account_code from trader where comp_code='" + compCode + "')\n" +
                "\tand date(gl_date) <= '" + toDate + "' \n" +
                "\tand comp_code = '" + compCode + "'\n" +
                "\tand trader_code ='" + traderCode + "'\n" +
                "\tgroup by  cur_code,trader_code\n" +
                "\t\t\tunion all\n" +
                "    select account_id,source_ac_id, cur_code,sum(ifnull(cr_amt,0)) dr_amt,sum(ifnull(dr_amt,0)) cr_amt\n" +
                "\tfrom gl \n" +
                "\twhere account_id in (select distinct account_code from trader where comp_code='" + compCode + "')\n" +
                "\tand date(gl_date) <= '" + toDate + "' \n" +
                "\tand comp_code = '" + compCode + "'\n" +
                "\tand trader_code ='" + traderCode + "'\n" +
                "\tgroup by cur_code,trader_code\n" +
                ")a\n" +
                "group by cur_code";
        ResultSet rs = dao.executeSql(sql);
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
    public List<TraderBalance> getTraderBalance(String traderCode, String accCode, String curCode, String fromDate,
                                                String toDate, String compCode, Integer macId) {
        List<TraderBalance> balances = new ArrayList<>();
        try {
            String sql = "select '" + accCode + "' acc_code ,gl_date,ref_no,description,trader_code, cur_code, \n"
                    + "get_dr_cr_amt(source_ac_id, account_id, '" + accCode + "', ifnull(dr_amt,0), ifnull(cr_amt,0), 'DR')dr_amt,\n"
                    + "get_dr_cr_amt(source_ac_id, account_id, '" + accCode + "', ifnull(dr_amt,0), ifnull(cr_amt,0), 'CR') cr_amt\n"
                    + "from gl\n"
                    + "where  (source_ac_id = '" + accCode + "' or account_id = '" + accCode + "') \n"
                    + "and date(gl_date) between '" + fromDate + "'  and '" + toDate + "' \n"
                    + "and comp_code = '" + compCode + "'\n"
                    + "and (cur_code = '" + curCode + "' or '-' ='" + curCode + "')\n"
                    + "and trader_code = '" + traderCode + "' \n"
                    + "and trader_code is not null\n"
                    + "group by gl_code,gl_date,trader_code,acc_code,cur_code\n"
                    + "order by gl_date";
            ResultSet rs = dao.executeSql(sql);
            if (!Objects.isNull(rs)) {
                while (rs.next()) {
                    TraderBalance b = new TraderBalance();
                    b.setTranDate(Util1.toDateStr(rs.getDate("gl_date"), "dd/MM/yyyy"));
                    b.setOpening(0.0);
                    b.setVouNo(rs.getString("ref_no"));
                    b.setRemark(rs.getString("description"));
                    double drAmt = rs.getDouble("dr_amt");
                    double crAmt = rs.getDouble("cr_amt");
                    b.setDrAmt(drAmt == 0 ? null : drAmt);
                    b.setCrAmt(crAmt == 0 ? null : crAmt);
                    b.setClosing(Util1.getDouble(b.getDrAmt()) - Util1.getDouble(b.getCrAmt()));
                    balances.add(b);
                }
            }
            double opAmt = getTraderLastBalance(fromDate, traderCode, compCode);
            TraderBalance tb = new TraderBalance();
            tb.setRemark("Opening");
            tb.setTranDate(Util1.toDateStr(fromDate, "yyyy-MM-dd", "dd/MM/yyyy"));
            tb.setOpening(opAmt);
            tb.setClosing(opAmt);
            balances.add(0, tb);
            for (int i = 0; i < balances.size(); i++) {
                if (i > 0) {
                    TraderBalance io = balances.get(i - 1);
                    double clAmt = Util1.getDouble(io.getOpening()) + Util1.getDouble(io.getDrAmt()) - Util1.getDouble(io.getCrAmt());
                    TraderBalance io1 = balances.get(i);
                    io1.setOpening(clAmt);
                    io1.setClosing(Util1.getDouble(io1.getOpening()) + Util1.getDouble(io1.getDrAmt()) - Util1.getDouble(io1.getCrAmt()));
                }
            }
            double opening = balances.get(0).getOpening();
            double closing = balances.get(balances.size() - 1).getClosing();
            ReturnObject ro = new ReturnObject();
            ro.setOpAmt(opening);
            ro.setClAmt(closing);
            hmRo.put(macId, ro);

        } catch (Exception ex) {
            log.error(String.format("getTraderBalance :%s", ex.getMessage()));
        }
        return balances;
    }

    @Override
    public List<COAOpening> getOpeningTri(String opDate, String deptCode, String curCode, String compCode) {
        List<COAOpening> list = new ArrayList<>();
        String sql = "select a.cur_code,coa.coa_code_usr,coa.coa_name_eng,dep.usr_code,if(dr_amt-cr_amt>0,dr_amt-cr_amt,0) dr_amt,if(dr_amt-cr_amt<0,(dr_amt-cr_amt)*-1,0) cr_amt\n" +
                "from (\n" +
                "select source_acc_id,cur_code,sum(dr_amt) dr_amt,sum(cr_amt) cr_amt,dept_code,comp_code\n" +
                "from coa_opening \n" +
                "where comp_code ='" + compCode + "'\n" +
                "and date(op_date)='" + opDate + "'\n" +
                "and (cur_code ='" + curCode + "' or '-' ='" + curCode + "')\n" +
                "and (dept_code ='" + deptCode + "' or '-' ='" + deptCode + "')\n" +
                "group by source_acc_id,dept_code,cur_code\n" +
                ")a\n" +
                "join chart_of_account coa\n" +
                "on a.source_acc_id = coa.coa_code\n" +
                "and a.comp_code = coa.comp_code\n" +
                "join department dep\n" +
                "on a.dept_code = dep.dept_code\n" +
                "and a.comp_code = dep.comp_code\n" +
                "where (a.dr_amt >0 or a.cr_amt>0)\n" +
                "order by coa.coa_code_usr";
        ResultSet rs = dao.executeSql(sql);
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

    private String getHeadSqlDetail(String head, Integer macId) {
        return "select tmp.curr_id, tmp.cr_amt-tmp.dr_amt amount,\n" +
                "coa1.coa_name_eng,coa2.coa_name_eng group_name,coa3.coa_name_eng head_name\n" +
                "from tmp_tri tmp \n" +
                "join chart_of_account coa1\n" +
                "on tmp.coa_code = coa1.coa_code\n" +
                "and tmp.comp_code = coa1.comp_code\n" +
                "join chart_of_account coa2\n" +
                "on coa1.coa_parent = coa2.coa_code\n" +
                "and coa1.comp_code = coa2.comp_code\n" +
                "join chart_of_account coa3\n" +
                "on coa2.coa_parent = coa3.coa_code\n" +
                "and coa2.comp_code = coa3.comp_code\n" +
                "where tmp.mac_id =" + macId + "\n" +
                "and coa2.coa_parent='" + head + "'\n" +
                "and (dr_amt>0 or cr_amt>0)\n" +
                "order by coa3.coa_code_usr,coa2.coa_code_usr,group_name,coa3.coa_code_usr,amount desc";
    }

    private String getHeadSqlSummary(String head, Integer macId) {
        return "select tmp.curr_id, sum(tmp.cr_amt-tmp.dr_amt) amount,coa2.coa_name_eng group_name,coa3.coa_name_eng head_name\n" +
                "from tmp_tri tmp \n" +
                "join chart_of_account coa1\n" +
                "on tmp.coa_code = coa1.coa_code\n" +
                "and tmp.comp_code = coa1.comp_code\n" +
                "join chart_of_account coa2\n" +
                "on coa1.coa_parent = coa2.coa_code\n" +
                "and coa1.comp_code = coa2.comp_code\n" +
                "join chart_of_account coa3\n" +
                "on coa2.coa_parent = coa3.coa_code\n" +
                "and coa2.comp_code = coa3.comp_code\n" +
                "where tmp.mac_id =" + macId + "\n" +
                "and coa2.coa_parent='" + head + "'\n" +
                "and (tmp.dr_amt>0 or tmp.cr_amt>0)\n" +
                "group by group_name\n" +
                "order by coa2.coa_code_usr,coa3.coa_code_usr,amount desc";
    }
}
