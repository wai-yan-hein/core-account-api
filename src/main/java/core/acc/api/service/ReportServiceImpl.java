package core.acc.api.service;

import core.acc.api.common.Util1;
import core.acc.api.dao.COADao;
import core.acc.api.dao.ReportDao;
import core.acc.api.entity.*;
import core.acc.api.model.BalanceSheetRetObj;
import core.acc.api.model.ProfitAndLostRetObj;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportDao dao;
    @Autowired
    private COADao coaDao;

    @Override
    public void insertTmp(List<String> listStr, Integer macId, String taleName) {
        try {
            deleteTmp(taleName, macId);
            for (String str : listStr) {
                String sql = "insert into " + taleName + "(dept_code,mac_id)\n" +
                        "select '" + str + "'," + macId + "";
                executeSql(sql);
            }
            log.info(String.format("insertTmp: %s", taleName));
        } catch (Exception e) {
            log.error(String.format("insertTmp: %s", e.getMessage()));
        }
    }

    @Override
    public List<VGl> getIndividualLager(String fromDate, String toDate, String desp, String srcAcc,
                                        String acc, String curCode, String reference,
                                        String compCode, String tranSource, String traderCode, String traderType,
                                        String coaLv2, String coaLv1, Integer macId) throws SQLException {
        String filter = "where date(gl_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and comp_code = '" + compCode + "'\n" +
                "and dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n" +
                "and (account_id = '" + srcAcc + "' or source_ac_id ='" + srcAcc + "')\n";
        String sql = "select gl_code, gl_date, created_date, description, source_ac_id, account_id, \n" +
                "cur_code, dr_amt, cr_amt, reference, dept_code, voucher_no,\n" +
                "dep_usr_code, trader_code,trader_name, comp_code, tran_source, gl_vou_no,\n" +
                "remark, mac_id, ref_no, trader_name, discriminator, \n" +
                "src_usr_code, src_acc_name, src_parent_2, src_parent_1, acc_usr_code, acc_name, acc_parent_2, acc_parent_1\n" +
                "from v_gl \n" +
                ""+filter+"\n"+
                "and ((account_id = '" + acc + "' or '-' = '" + acc + "') or (source_ac_id ='" + acc + "' or '-' ='" + acc + "'))\n" +
                "and ((src_parent_2 = '" + coaLv2 + "' or '-' = '" + coaLv2 + "') or (acc_parent_2 ='" + coaLv2 + "' or '-'='" + coaLv2 + "'))\n" +
                "and ((src_parent_1 = '" + coaLv1 + "' or '-' = '" + coaLv1 + "') or (acc_parent_1 = '" + coaLv1 + "' or '-' = '" + coaLv1 + "'))\n" +
                "and (tran_source = '" + tranSource + "' or '-' ='" + tranSource + "')\n" +
                "and (description = '" + desp + "' or '-' = '" + desp + "')\n" +
                "and (reference = '" + reference + "' or '-' = '" + reference + "')\n" +
                "and  (trader_code ='" + traderCode + "' or '-' ='" + traderCode + "')\n" +
                "and  (discriminator ='" + traderType + "' or '-' ='" + traderType + "')\n" +
                "and (cur_code ='" + curCode + "' or '-' = '" + curCode + "')\n" +
                "order by gl_date,tran_source,gl_code\n";
        ResultSet rs = dao.executeSql(sql);
        List<VGl> vGls = new ArrayList<>();
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VGl v = new VGl();
                v.setGlCode(rs.getString("gl_code"));
                v.setGlDate(rs.getDate("gl_date"));
                v.setFormatDate(Util1.toDateStr(rs.getDate("gl_date"), "dd/MM/yyyy"));
                v.setDescription((rs.getString("description")));
                v.setSourceAcId(rs.getString("source_ac_id"));
                v.setAccCode(rs.getString("account_id"));
                v.setCurCode(rs.getString("cur_code"));
                v.setDrAmt(Util1.toNull(rs.getDouble("dr_amt")));
                v.setCrAmt((Util1.toNull(rs.getDouble("cr_amt"))));
                v.setReference(rs.getString("reference"));
                v.setRefNo(rs.getString("ref_no"));
                v.setDeptCode(rs.getString("dept_code"));
                v.setVouNo(rs.getString("voucher_no"));
                v.setDeptUsrCode(rs.getString("dep_usr_code"));
                v.setTraderCode(rs.getString("trader_code"));
                v.setTraderName(rs.getString("trader_name"));
                v.setCompCode(rs.getString("comp_code"));
                v.setTranSource(rs.getString("tran_source"));
                v.setGlVouNo(rs.getString("gl_vou_no"));
                v.setSrcAccName(rs.getString("src_acc_name"));
                v.setAccName(rs.getString("acc_name"));
                vGls.add(v);
            }
        }
        if (!vGls.isEmpty()) {
            vGls.forEach(vgl -> {
                String account = Util1.isNull(vgl.getAccCode(), "-");
                if (account.equals(srcAcc)) {
                    //swap amt
                    double tmpDrAmt = Util1.getDouble(vgl.getDrAmt());
                    vgl.setDrAmt(vgl.getCrAmt());
                    vgl.setCrAmt(tmpDrAmt);
                    //swap acc
                    String tmpStr = vgl.getAccName();
                    vgl.setAccName(vgl.getSrcAccName());
                    vgl.setSrcAccName(tmpStr);
                }
            });
        }
        return vGls;
    }

    private void deleteTmp(String tableName, Integer macId) {
        String delSql = "delete from " + tableName + " where mac_id =" + macId + "";
        executeSql(delSql);
    }

    @Override
    public void getProfitLost(String plProcess, String stDate, String enDate, String dept,
                              String reqCurrency, String comp, String userCode, String macId, String inventory) {
        long start = new GregorianCalendar().getTimeInMillis();
        dao.execSQLRpt("delete from tmp_profit_lost where mac_id = " + macId + "");
        String strInsert = "insert into tmp_profit_lost(acc_code,curr_code,dept_code,\n "
                + "acc_total,comp_code, sort_order,mac_id)";
        String[] process = plProcess.split(",");
        int sortOrder = 1;
        stDate = Util1.toDateStrMYSQL(stDate, "dd/MM/yyyy");
        enDate = Util1.toDateStrMYSQL(enDate, "dd/MM/yyyy");
        String invCode = getCOACode(inventory, comp);
        inventory = invCode.equals("-") ? "'" + inventory + "'" : invCode;
        //Sales Income
        for (String tmp : process) {
            switch (tmp) {
                case "os" -> {
                    try {
                        String sql = "select coa_code,curr_code,dept_code,sum(amount)amount,comp_code," + sortOrder + "," + macId + "\n"
                                + "	from stock_op_value\n"
                                + "	where coa_code in(" + inventory + ")\n"
                                + "    and date(tran_date) =(select max(tran_date)\n"
                                + "	from stock_op_value\n"
                                + "	where date(tran_date) <'" + stDate + "'\n"
                                + "    )\n"
                                + "and (dept_code in (" + dept + "))\n"
                                + "and (comp_code ='" + comp + "' or '-' ='" + comp + "')\n"
                                + "group by coa_code,curr_code";
                        ResultSet rs = dao.executeSql(sql);
                        if (rs.next()) {
                            dao.execSQLRpt(strInsert + "\n" + sql);
                        } else {
                            String sql1 = "select source_acc_id coa_code,cur_code,dept_code,sum(dr_amt) op_amt,comp_code," + sortOrder + "," + macId + "\n"
                                    + "	from coa_opening \n"
                                    + "	where source_acc_id in(" + inventory + ")\n"
                                    + "	and date(op_date) ='" + stDate + "'\n"
                                    + " and (dept_code in (" + dept + "))\n"
                                    + "	and (comp_code ='" + comp + "' or '-' ='" + comp + "')\n"
                                    + "group by op_date, coa_code,cur_code,comp_code";
                            dao.execSQLRpt(strInsert + "\n" + sql1);
                        }
                    } catch (Exception e) {
                        throw new IllegalStateException("Multiple Opening Problem.Fix in Journal Stock Opening.");
                    }
                }
                case "cs" -> {
                    try {
                        String strCS = "select coa_code,curr_code,dept_code,sum(amount) amount,\n"
                                + "'" + comp + "'," + sortOrder + "," + macId + "\n"
                                + "from stock_op_value\n"
                                + "where  coa_code in (" + inventory + ")\n"
                                + "and (dept_code in (" + dept + "))\n"
                                + "and comp_code = '" + comp + "'\n"
                                + "and date(tran_date) = '" + enDate + "'\n"
                                + "group by coa_code,curr_code";
                        dao.execSQLRpt(strInsert + "\n" + strCS);
                    } catch (Exception e) {
                        throw new IllegalStateException("Closing Calculation Error.");
                    }
                }
                default -> {
                    try {
                        String sql = "select coa_code,curr_id,ifnull(dept_code,'-'),if(dr_amt>0,dr_amt*-1,cr_amt) acc_total,\n"
                                + "'" + comp + "'," + sortOrder + "," + macId + "\n"
                                + "from tmp_tri\n"
                                + "where mac_id = " + macId + " \n"
                                + "and dept_code in (" + dept + ")\n"
                                + "and coa_code in (select coa_code from v_coa_lv3 where coa_code_3 = '" + tmp + "' and comp_code = '" + comp + "')";
                        dao.execSQLRpt(strInsert + "\n" + sql);
                    } catch (Exception e) {
                        throw new IllegalStateException("Profit And Lost Error.");
                    }
                }
            }
            sortOrder++;
        }
        long end = new GregorianCalendar().getTimeInMillis();
        log.info(String.format("getProfitLost %s ms", end - start));
    }

    public String getCOACode(String code, String compCode) {
        String tmp = "-";
        List<ChartOfAccount> listCoA = coaDao.getAllChild(code, compCode);
        if (!listCoA.isEmpty()) {
            tmp = "";
            tmp = listCoA.stream().map(coa -> String.format("'%s',", coa.getKey().getCoaCode())).reduce(tmp, String::concat);
        }
        tmp = tmp.substring(0, tmp.length() - 1);
        return Util1.isNull(tmp, "-");
    }

    @Override
    public ProfitAndLostRetObj getPLCalculateValue(String compCode, String macId, boolean multiCur) {
        ProfitAndLostRetObj obj = new ProfitAndLostRetObj();
        String sql = "select abs(sum(acc_total)) acc_total,sort_order\n"
                + "from tmp_profit_lost\n"
                + "where mac_id = " + macId + " and comp_code ='" + compCode + "'\n"
                + "group by sort_order";
        ResultSet rs;
        try {
            rs = dao.executeSql(sql);
            if (rs != null) {
                while (rs.next()) {
                    double ttl = rs.getDouble("acc_total");
                    int order = rs.getInt("sort_order");
                    switch (order) {
                        case 1 -> //Sale Income
                                obj.addSaleIncome(ttl);
                        case 2 -> //Opening Stock
                                obj.addOPStock(ttl);
                        case 3 -> //Purchase
                                obj.addPurchase(ttl);
                        case 4 -> //Closing Stock
                                obj.addCLStock(ttl);
                        case 5 -> //Other Income
                                obj.addOtherIncome(ttl);
                        case 6 -> //Other Expense
                                obj.addOtherExpense(ttl);
                    }
                }
            }
        } catch (Exception ex) {
            log.error("getPLCalculateValue : " + ex.getMessage());
        }
        return obj;
    }

    @Override
    public void genBalanceSheet(String toDate, String compCode,
                                String curr, String macId, String blProcess, String inventory, String strDep) {
        long start = new GregorianCalendar().getTimeInMillis();
        String strSqlDelete = "delete from tmp_balance_sheet where mac_id = '" + macId + "'";
        dao.execSQLRpt(strSqlDelete);
        String strInsert = "insert into tmp_balance_sheet(acc_code,curr_code,dept_code,\n "
                + "acc_total, comp_code, sort_order,mac_id)";
        String[] process = blProcess.split(",");
        int sortOrder = 1;
        for (String tmp : process) {
            String sql = "select coa_code,curr_id,ifnull(dept_code,'-'),if(dr_amt>0,dr_amt*-1,cr_amt) acc_total,\n"
                    + "'" + compCode + "'," + sortOrder + "," + macId + "\n"
                    + "from tmp_tri\n"
                    + "where mac_id = " + macId + "\n"
                    + "and dept_code in (" + strDep + ")"
                    + "and coa_code in (select coa_code from v_coa_lv3 where coa_code_3 = '" + tmp + "' and comp_code = '" + compCode + "')";
            dao.execSQLRpt(strInsert + "\n" + sql);
            sortOrder++;
        }
        String invCode = getCOACode(inventory, compCode);
        inventory = invCode.equals("-") ? "'" + inventory + "'" : invCode;
        String delSql = "delete from tmp_balance_sheet where mac_id = " + macId + " and acc_code in (" + inventory + ")";
        String invSql = "select  coa_code,curr_code,dept_code,amount*(-1) acc_total,comp_code,2," + macId + "\n"
                + "from stock_op_value \n"
                + "where date(tran_date) = '" + toDate + "' and dept_code in (" + strDep + ")";
        dao.execSQLRpt(delSql);
        dao.execSQLRpt(strInsert + "\n" + invSql);
        long end = new GregorianCalendar().getTimeInMillis();
        log.info(String.format("genBalanceSheet %s ms", end - start));
    }

    @Override
    public BalanceSheetRetObj getBSCalculateValue(String compCode,
                                                  double prvProfit, double profit, String macId) {
        //Previous PL
        String insertPPL = "insert into tmp_balance_sheet(acc_code,curr_code,dept_code,acc_total,comp_code, sort_order,mac_id)\n"
                + "select '-','-','-'," + prvProfit + ",'" + compCode + "',9," + macId + "";
        //Current PL
        String insertCPL = "insert into tmp_balance_sheet(acc_code,curr_code,dept_code,acc_total,comp_code, sort_order,mac_id)\n"
                + "select '-','-','-'," + profit + ",'" + compCode + "',10," + macId + "";
        dao.execSQLRpt(insertPPL, insertCPL);
        //
        BalanceSheetRetObj bs = new BalanceSheetRetObj();
        bs.setRetailed(prvProfit);
        bs.setProfit(profit);
        String sql = "select abs(sum(acc_total)) acc_total,sort_order\n"
                + "from tmp_balance_sheet\n"
                + "where mac_id = " + macId + " and comp_code ='" + compCode + "'\n"
                + "group by sort_order";
        ResultSet rs;
        try {
            rs = dao.executeSql(sql);
            if (rs != null) {
                while (rs.next()) {
                    double ttl = rs.getDouble("acc_total");
                    int order = rs.getInt("sort_order");
                    switch (order) {
                        case 1 -> bs.setFixedAss(ttl);
                        case 2 -> bs.setCurrentAss(ttl);
                        case 3 -> bs.setLiability(ttl);
                        case 4 -> bs.setCapital(ttl);
                    }
                }
            }
        } catch (Exception ex) {
            log.error("getPLCalculateValue : " + ex.getMessage());
        }
        return bs;
    }

    @Override
    public void genIncomeAndExpense(String process, String compCode, String strDep, String macId, String currency, boolean multiCur) {
        try {
            int sortOrder = 1;
            String[] tmp = process.split(",");
            String del1 = "delete from tmp_in_ex where mac_id = '" + macId + "'";
            String del2 = "delete from tmp_in_ex_detail where mac_id = '" + macId + "'";
            dao.execSQLRpt(del1, del2);
            if (multiCur) {
                String strInsert = "insert into tmp_in_ex_detail(gl_date,coa_code,cur_code,dept_code,\n "
                        + "acc_total, comp_code, sort_order,mac_id,ex_cur)";
                for (String code : tmp) {
                    String sql = "select gl_date,coa_code,curr_id,ifnull(dept_code,'-'),if(dr_amt>0,dr_amt*-1,cr_amt) acc_total,\n"
                            + "'" + compCode + "'," + sortOrder + "," + macId + ",'" + currency + "'\n"
                            + "from tmp_tri_detail\n"
                            + "where mac_id = " + macId + " \n"
                            + "and dept_code in (" + strDep + ")"
                            + "and coa_code in (select coa_code from v_coa_lv3 where coa_code_3 = '" + code + "' and comp_code = '" + compCode + "')";
                    dao.execSQLRpt(strInsert + "\n" + sql);
                    sortOrder++;
                }
                String convertSql = "insert into tmp_in_ex(coa_code, cur_code, acc_total, comp_code, sort_order, mac_id, dept_code,ex_cur)\n"
                        + "select coa_code,cur_code,sum(ifnull(tmp_amt,acc_total)) acc_total,comp_code,sort_order,mac_id,dept_code,ex_cur \n"
                        + "from (\n"
                        + "select tmp.gl_date,tmp.coa_code,tmp.dept_code,tmp.cur_code,tmp.ex_cur,\n"
                        + "tmp.acc_total,(tmp.acc_total*ifnull(r.avg_rate,1)) tmp_amt,ifnull(r.avg_rate,1) avg_rate,\n"
                        + "tmp.sort_order,tmp.mac_id,tmp.comp_code\n"
                        + "from tmp_in_ex_detail tmp \n"
                        + "left  join v_cur_rate r\n"
                        + "on tmp.cur_code = r.currency and \n"
                        + "tmp.gl_date = r.ex_date \n"
                        + "where tmp.mac_id = " + macId + "\n"
                        + ")a\n"
                        + "group by coa_code,ex_cur,comp_code";
                dao.execSQLRpt(convertSql);
            } else {
                String strInsert = "insert into tmp_in_ex(coa_code,cur_code,dept_code,\n "
                        + "acc_total, comp_code, sort_order,mac_id)";
                for (String code : tmp) {
                    String sql = "select coa_code,curr_id,ifnull(dept_code,'-'),if(dr_amt>0,dr_amt*-1,cr_amt) acc_total,\n"
                            + "'" + compCode + "'," + sortOrder + "," + macId + "\n"
                            + "from tmp_tri\n"
                            + "where mac_id = " + macId + " \n"
                            + "and dept_code in (" + strDep + ")"
                            + "and coa_code in (select coa_code from v_coa_lv3 where coa_code_3 = '" + code + "' and comp_code = '" + compCode + "')";
                    dao.execSQLRpt(strInsert + "\n" + sql);
                    sortOrder++;
                }
            }
        } catch (Exception e) {
            log.error("genIncomeAndExpense : " + e.getMessage());
        }
    }

    @Override
    public ProfitAndLostRetObj calculateIncomeExpense(String compCode, String macId, boolean multiCur) {
        ProfitAndLostRetObj pl = new ProfitAndLostRetObj();
        String sql = "select sum(acc_total) acc_total,sort_order\n"
                + "	from tmp_in_ex\n"
                + "	where mac_id = " + macId + "\n"
                + "		and comp_code = '" + compCode + "'\n"
                + "group by sort_order";
        ResultSet rs;
        try {
            rs = dao.executeSql(sql);
            if (rs != null) {
                while (rs.next()) {
                    double ttl = rs.getDouble("acc_total");
                    int order = rs.getInt("sort_order");
                    switch (order) {
                        case 1 -> pl.addSaleIncome(ttl);
                        case 2 -> pl.addOtherIncome(ttl);
                        case 3 -> pl.addPurchase(ttl);
                        case 4 -> pl.addOtherExpense(ttl);
                    }
                    //curr
                    //lia
                    //capital
                }
            }
        } catch (Exception ex) {
            log.error("calculateIncomeExpense : " + ex.getMessage());
        }
        return pl;
    }

    @Override
    public void deleteOpTemp(String macId) {
        String delSql = "delete from tmp_op_cl where mac_id = " + macId;
        String delSql1 = "delete from tmp_tri where mac_id = " + macId;
        dao.execSQLRpt(delSql, delSql1);
    }

    @Override
    public double genOpBalance(String process, String opDate,
                               String clDate,
                               String endDate,
                               String curr, String compCode,
                               String dept, String macId) throws Exception {
        String[] coaCodes = process.split(",");
        double ttlOP = 0;
        if (coaCodes.length > 0) {
            for (String coaCode : coaCodes) {
                String opSql = "insert into tmp_op_cl(coa_code, cur_code,opening,mac_id) \n"
                        + "select a.acc_code, a.cur_code, sum(a.balance) balance, " + macId + "\n"
                        + "from (\n"
                        + "select source_acc_id acc_code,cur_code,sum(ifnull(dr_amt,0))-sum(ifnull(cr_amt,0)) balance,\n"
                        + "		sum(ifnull(dr_amt,0)) dr_amt, sum(ifnull(cr_amt,0)) cr_amt,trader_code\n"
                        + "	from coa_opening \n"
                        + "	where source_acc_id = '" + coaCode + "'\n"
                        + "        and (dept_code in (" + dept + "))\n"
                        + "        and comp_code = '" + compCode + "'\n"
                        + "        and date(op_date) = '" + opDate + "'\n"
                        + "        and (cur_code = '" + curr + "' or '-' ='" + curr + "')\n"
                        + "      group by acc_code,cur_code\n"
                        + "             union all\n"
                        + "select '" + coaCode + "' acc_code ,cur_code cur_code, sum(get_dr_cr_amt(source_ac_id, account_id, '" + coaCode + "', \n"
                        + "		ifnull(dr_amt,0), ifnull(cr_amt,0), 'DR')-get_dr_cr_amt(source_ac_id, \n"
                        + "             account_id, '" + coaCode + "', ifnull(dr_amt,0), ifnull(cr_amt,0), 'CR')) balance, \n"
                        + "		sum(ifnull(dr_amt,0)) dr_amt, sum(ifnull(cr_amt,0)) cr_amt,trader_code \n"
                        + "     from gl\n"
                        + "	where  (source_ac_id = '" + coaCode + "' or account_id = '" + coaCode + "') \n"
                        + "		and date(gl_date)>= '" + opDate + "'\n"
                        + "        and date(gl_date) < '" + clDate + "' \n"
                        + "        and (dept_code in (" + dept + "))\n"
                        + "        and comp_code = '" + compCode + "'\n"
                        + "        and (cur_code = '" + curr + "' or '-' ='" + curr + "')\n"
                        + "	group by acc_code,cur_code) a \n"
                        + "group by a.acc_code, a.cur_code";
                dao.execSQLRpt(opSql);
            }

            String strSql = "insert into tmp_tri(coa_code, curr_id, dept_code, mac_id, dr_amt, cr_amt)\n"
                    + "select toc.coa_code, toc.cur_code, gl.dept_code, " + macId + " as mac_id,\n"
                    + "sum(get_dr_cr_amt(gl.source_ac_id, gl.account_id, toc.coa_code, gl.dr_amt, gl.cr_amt, 'DR')) dr_amt,\n"
                    + "sum(get_dr_cr_amt(gl.source_ac_id, gl.account_id, toc.coa_code, gl.dr_amt, gl.cr_amt, 'CR')) cr_amt\n"
                    + "from tmp_op_cl toc\n"
                    + "join gl on (toc.coa_code = gl.source_ac_id or toc.coa_code = gl.account_id) and toc.cur_code = gl.cur_code\n"
                    + "where gl.gl_date between '" + clDate + "' and '" + endDate + "' and toc.mac_id = " + macId + " \n"
                    + "group by toc.coa_code, toc.cur_code, gl.dept_code";
            dao.execSQLRpt(strSql);

            strSql = "select sum(ifnull(opening,0)) as ttl_op\n"
                    + "from tmp_op_cl\n"
                    + "where mac_id = " + macId;
            ResultSet rs = dao.executeSql(strSql);

            if (rs != null) {
                if (rs.next()) {
                    ttlOP = rs.getDouble("ttl_op");
                }
            }
        }
        return ttlOP;
    }

    @Override
    public void executeSql(String... sql) {
        dao.execSQLRpt(sql);
    }

    @Override
    public void getProfitLostMultiCurrency(String plProcess, String stDate,
                                           String enDate, String dept,
                                           String reqCurrency, String comp,
                                           String userCode, String macId,
                                           String inventory) {
        long start = new GregorianCalendar().getTimeInMillis();
        String delSql = "delete from tmp_profit_lost where mac_id = '" + macId + "'";
        dao.execSQLRpt(delSql);
        dao.execSQLRpt("delete from tmp_profit_lost_detail where mac_id = '" + macId + "'");
        String strInsert = "insert into tmp_profit_lost_detail(gl_date,acc_code,curr_code,dept_code,\n "
                + "acc_total,comp_code, sort_order,mac_id,ex_cur)";
        String[] process = plProcess.split(",");
        int sortOrder = 1;
        stDate = Util1.toDateStrMYSQL(stDate, "dd/MM/yyyy");
        enDate = Util1.toDateStrMYSQL(enDate, "dd/MM/yyyy");
        String invCode = getCOACode(inventory, comp);
        inventory = invCode.equals("-") ? "'" + inventory + "'" : invCode;
        //Sales Income
        for (String tmp : process) {
            switch (tmp) {
                case "os" -> {
                    try {
                        String sql = "select tran_date,coa_code,curr_code,dept_code,sum(amount)amount,comp_code,\n"
                                + "" + sortOrder + "," + macId + ",'" + reqCurrency + "'\n"
                                + "	from stock_op_value\n"
                                + "	where coa_code in(" + inventory + ")\n"
                                + "    and date(tran_date) =(select max(tran_date)\n"
                                + "	from stock_op_value\n"
                                + "	where date(tran_date) <'" + stDate + "'\n"
                                + "    )\n"
                                + "and (dept_code in (" + dept + "))\n"
                                + "and (comp_code ='" + comp + "' or '-' ='" + comp + "')\n"
                                + "group by tran_date,coa_code,curr_code";
                        ResultSet rs = dao.executeSql(sql);
                        if (rs.next()) {
                            dao.execSQLRpt(strInsert + "\n" + sql);
                        } else {
                            String sql1 = "select op_date,source_acc_id coa_code,cur_code,dept_code,\n"
                                    + "sum(dr_amt) op_amt,comp_code," + sortOrder + "," + macId + ",'" + reqCurrency + "'\n"
                                    + "	from coa_opening \n"
                                    + "	where source_acc_id in(" + inventory + ")\n"
                                    + "	and date(op_date) ='" + stDate + "'\n"
                                    + " and (dept_code in (" + dept + "))\n"
                                    + "	and (comp_code ='" + comp + "' or '-' ='" + comp + "')\n"
                                    + "group by op_date, coa_code,cur_code,comp_code";
                            dao.execSQLRpt(strInsert + "\n" + sql1);
                        }
                    } catch (Exception e) {
                        throw new IllegalStateException("Multiple Opening Problem.Fix in Journal Stock Opening.");
                    }
                }
                case "cs" -> {
                    try {
                        String strCS = "select tran_date,coa_code,curr_code,dept_code,sum(amount) amount,\n"
                                + "'" + comp + "'," + sortOrder + "," + macId + ",'" + reqCurrency + "'\n"
                                + "from stock_op_value\n"
                                + "where  coa_code in (" + inventory + ")\n"
                                + "and (dept_code in (" + dept + "))\n"
                                + "and comp_code = '" + comp + "'\n"
                                + "and date(tran_date) = '" + enDate + "'\n"
                                + "group by tran_date,coa_code,curr_code";
                        dao.execSQLRpt(strInsert + "\n" + strCS);
                    } catch (Exception e) {
                        throw new IllegalStateException("Closing Calculation Error.");
                    }
                }
                default -> {
                    try {
                        String sql = "select gl_date,coa_code,curr_id,ifnull(dept_code,'-'),if(dr_amt>0,dr_amt*-1,cr_amt) acc_total,\n"
                                + "'" + comp + "'," + sortOrder + "," + macId + ",'" + reqCurrency + "'\n"
                                + "from tmp_tri_detail\n"
                                + "where mac_id = " + macId + " \n"
                                + "and dept_code in (" + dept + ")\n"
                                + "and coa_code in (select coa_code from v_coa_lv3 where coa_code_3 = '" + tmp + "' and comp_code = '" + comp + "')";
                        dao.execSQLRpt(strInsert + "\n" + sql);
                    } catch (Exception e) {
                        throw new IllegalStateException("Profit And Lost Error.");
                    }
                }
            }
            sortOrder++;
        }
        String convertSql = "insert into tmp_profit_lost(acc_code, curr_code, acc_total, comp_code, sort_order, mac_id, dept_code,ex_cur)\n"
                + "select acc_code,curr_code,sum(ifnull(tmp_amt,acc_total)) acc_total,comp_code,sort_order,mac_id,dept_code,ex_cur \n"
                + "from (\n"
                + "select tmp.gl_date,tmp.acc_code,tmp.dept_code,tmp.curr_code,tmp.ex_cur,\n"
                + "tmp.acc_total,(tmp.acc_total*ifnull(r.avg_rate,1)) tmp_amt,ifnull(r.avg_rate,1) avg_rate,\n"
                + "tmp.sort_order,tmp.mac_id,tmp.comp_code\n"
                + "from tmp_profit_lost_detail tmp \n"
                + "left  join v_cur_rate r\n"
                + "on tmp.curr_code = r.currency and \n"
                + "tmp.gl_date = r.ex_date \n"
                + "where tmp.mac_id = " + macId + "\n"
                + ")a\n"
                + "group by acc_code,ex_cur,comp_code";
        dao.execSQLRpt(convertSql);
        long end = new GregorianCalendar().getTimeInMillis();
        log.info(String.format("getProfitLostMultiCurrency %s ms", end - start));

    }

    @Override
    public void genBalanceSheetDetail(String toDate, String compCode, String curr, String macId, String blProcess,
                                      String inventory, String strDep) {
        long start = new GregorianCalendar().getTimeInMillis();
        String del1 = "delete from tmp_balance_sheet_detail where mac_id = '" + macId + "'";
        String del2 = "delete from tmp_balance_sheet where mac_id = '" + macId + "'";
        dao.execSQLRpt(del1, del2);
        String strInsert = "insert into tmp_balance_sheet_detail(gl_date,acc_code,curr_code,dept_code,\n "
                + "acc_total, comp_code, sort_order,mac_id,ex_cur)";
        String[] process = blProcess.split(",");
        int sortOrder = 1;
        for (String tmp : process) {
            String sql = "select gl_date,coa_code,curr_id,ifnull(dept_code,'-'),if(dr_amt>0,dr_amt*-1,cr_amt) acc_total,\n"
                    + "'" + compCode + "'," + sortOrder + "," + macId + ",'" + curr + "'\n"
                    + "from tmp_tri_detail\n"
                    + "where mac_id = " + macId + "\n"
                    + "and dept_code in (" + strDep + ")"
                    + "and coa_code in (select coa_code from v_coa_lv3 where coa_code_3 = '" + tmp + "' and comp_code = '" + compCode + "')";
            dao.execSQLRpt(strInsert + "\n" + sql);
            sortOrder++;
        }
        String updateSql = "update tmp_balance_sheet_detail tmp,\n"
                + "(select amount*(-1) acc_total,coa_code from stock_op_value where date(tran_date) = '" + toDate + "') op\n"
                + "set tmp.acc_total = op.acc_total\n"
                + "where tmp.acc_code = op.coa_code ";
        dao.execSQLRpt(updateSql);
        String convertSql = "insert into tmp_balance_sheet(acc_code, curr_code, acc_total, comp_code, sort_order, mac_id, dept_code,ex_cur)\n"
                + "select acc_code,curr_code,sum(ifnull(tmp_amt,acc_total)) acc_total,comp_code,sort_order,mac_id,dept_code,ex_cur \n"
                + "from (\n"
                + "select tmp.gl_date,tmp.acc_code,tmp.dept_code,tmp.curr_code,tmp.ex_cur,\n"
                + "tmp.acc_total,(tmp.acc_total*ifnull(r.avg_rate,1)) tmp_amt,ifnull(r.avg_rate,1) avg_rate,\n"
                + "tmp.sort_order,tmp.mac_id,tmp.comp_code\n"
                + "from tmp_balance_sheet_detail tmp \n"
                + "left  join v_cur_rate r\n"
                + "on tmp.curr_code = r.currency and \n"
                + "tmp.gl_date = r.ex_date \n"
                + "where tmp.mac_id = " + macId + "\n"
                + ")a\n"
                + "group by acc_code,ex_cur,comp_code";
        dao.execSQLRpt(convertSql);
        long end = new GregorianCalendar().getTimeInMillis();
        log.info(String.format("genBalanceSheet %s ms", end - start));
    }

    @Override
    public double genCash(String stDate, String endDate, String cashGroup, String deptStr, String compCode, String macId) throws Exception {
        double balAmt = 0.0;
        String delSql = "delete from tmp_cash_io where mac_id = " + macId + "";
        String insertSql = "insert into tmp_cash_io(source_acc,coa_code,cur_code,dept_code,dr_amt,cr_amt,mac_id)\n"
                + "select source_ac_id,account_id, cur_code,dept_code,if(sum(dr_amt-cr_amt)>0, sum(dr_amt-cr_amt),0) dr_amt\n"
                + ",if(sum(dr_amt-cr_amt)<0, sum(dr_amt-cr_amt)*-1,0) cr_amt," + macId + "\n"
                + "from (\n"
                + "	select gl.source_ac_id,gl.account_id, gl.cur_code,sum(get_dr_cr_amt(gl.source_ac_id, gl.account_id, \n"
                + "			gl.source_ac_id, gl.dr_amt, gl.cr_amt, 'DR')) dr_amt,\n"
                + "                     sum(get_dr_cr_amt(gl.source_ac_id, gl.account_id, gl.source_ac_id, gl.dr_amt, \n"
                + "			gl.cr_amt, 'CR')) cr_amt,dept_code\n"
                + "	from (	select source_ac_id,account_id, cur_code,sum(ifnull(dr_amt,0)) dr_amt,sum(ifnull(cr_amt,0)) cr_amt,\n"
                + "			dept_code\n"
                + "			from gl \n"
                + "			where source_ac_id in (select coa_code from v_coa_lv3 where coa_code_2 = '" + cashGroup + "')\n"
                + "			and date(gl_date) between '" + stDate + "' \n"
                + "			and '" + endDate + "'\n"
                + "                     and (dept_code in (" + deptStr + "))\n"
                + "			and comp_code = '" + compCode + "' and (cur_code = '-' or '-'='-')\n"
                + "			group by account_id, cur_code, source_ac_id,dept_code) gl\n"
                + "	group by gl.account_id, gl.cur_code, gl.source_ac_id,dept_code\n"
                + "			union all \n"
                + "    select gl.account_id ,gl.source_ac_id, gl.cur_code,sum(get_dr_cr_amt(gl.source_ac_id, gl.account_id, \n"
                + "			gl.account_id, gl.dr_amt, gl.cr_amt, 'DR')) dr_amt,\n"
                + "                     sum(get_dr_cr_amt(gl.source_ac_id, gl.account_id, gl.account_id, gl.dr_amt, \n"
                + "			gl.cr_amt, 'CR')) cr_amt,dept_code\n"
                + "     from (	select source_ac_id,account_id, cur_code,sum(ifnull(dr_amt,0)) dr_amt,sum(ifnull(cr_amt,0)) cr_amt,\n"
                + "			dept_code\n"
                + "			from gl \n"
                + "			where account_id in (select coa_code from v_coa_lv3 where coa_code_2 = '" + cashGroup + "')\n"
                + "			and date(gl_date) between '" + stDate + "' \n"
                + "			and '" + endDate + "'\n"
                + "                     and (dept_code in (" + deptStr + "))\n"
                + "			and comp_code = '" + compCode + "' and (cur_code = '-' or '-'='-')\n"
                + "			group by account_id, cur_code, source_ac_id,dept_code) gl\n"
                + "	group by gl.account_id, gl.cur_code, gl.source_ac_id,dept_code) a\n"
                + "group by source_ac_id,account_id, cur_code,dept_code";
        executeSql(delSql, insertSql);
        String balSql = "select sum(dr_amt-cr_amt) balance\n"
                + "from tmp_cash_io \n"
                + "where mac_id =" + macId + "";
        ResultSet rs = dao.executeSql(balSql);
        if (rs.next()) {
            balAmt = rs.getDouble("balance");
        }
        return balAmt;
    }

    @Override
    public void genTriBalance(String compCode, String stDate, String enDate,
                              String opDate, String currency, boolean closing, Integer macId) {
        String delSql1 = "delete from tmp_tri where mac_id =" + macId + "";
        String delSql2 = "delete from tmp_closing where mac_id =" + macId + "";
        dao.execSQLRpt(delSql1, delSql2);
        String opSql = "insert into tmp_closing(coa_code, cur_code, dr_amt, cr_amt, dept_code,comp_code, mac_id)\n"
                + "select coa_code, cur_code,if(sum(dr_amt-cr_amt)>0, sum(dr_amt-cr_amt),0) dr_amt, \n"
                + "		if(sum(dr_amt-cr_amt)<0, sum(dr_amt-cr_amt)*-1,0) cr_amt,dept_code,'" + compCode + "'," + macId + "\n"
                + "from (\n"
                + "select op.source_acc_id as coa_code, op.cur_code,\n"
                + "		   sum(ifnull(op.dr_amt,0)) dr_amt, sum(ifnull(op.cr_amt,0)) cr_amt,dept_code\n"
                + "	from  coa_opening op\n"
                + "	where date(op.op_date) = '" + opDate + "' \n"
                + "		and (op.dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + "))\n"
                + "             and (cur_code = '" + currency + "' or '-'='" + currency + "')\n"
                + "             and comp_code = '" + compCode + "'\n"
                + "	group by op.source_acc_id, op.cur_code\n"
                + "			union all\n"
                + "	select gl.source_ac_id coa_code, gl.cur_code,sum(get_dr_cr_amt(gl.source_ac_id, gl.account_id, \n"
                + "			gl.source_ac_id, gl.dr_amt, gl.cr_amt, 'DR')) dr_amt,\n"
                + "         sum(get_dr_cr_amt(gl.source_ac_id, gl.account_id, gl.source_ac_id, gl.dr_amt, \n"
                + "			gl.cr_amt, 'CR')) cr_amt,dept_code\n"
                + "	from (	select source_ac_id,account_id, cur_code,sum(ifnull(dr_amt,0)) dr_amt,\n"
                + "			sum(ifnull(cr_amt,0)) cr_amt,dept_code\n"
                + "			from gl \n"
                + "			where source_ac_id in (select coa_code from chart_of_account where coa_level >=3 and comp_code='" + compCode + "')\n"
                + "			and date(gl_date) >= '" + opDate + "'\n"
                + "			and date(gl_date) <'" + stDate + "' \n"
                + "         and (dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + "))\n"
                + "			and comp_code = '" + compCode + "'\n"
                + "         and (cur_code = '" + currency + "' or '-'='" + currency + "')\n"
                + "			group by account_id, cur_code, source_ac_id) gl\n"
                + "	group by gl.account_id, gl.cur_code, gl.source_ac_id\n"
                + "			union all \n"
                + "    select gl.account_id coa_code, gl.cur_code,sum(get_dr_cr_amt(gl.source_ac_id, gl.account_id, \n"
                + "			gl.account_id, gl.dr_amt, gl.cr_amt, 'DR')) dr_amt,\n"
                + "         sum(get_dr_cr_amt(gl.source_ac_id, gl.account_id, gl.account_id, gl.dr_amt, \n"
                + "			gl.cr_amt, 'CR')) cr_amt,dept_code\n"
                + "     from (	select source_ac_id,account_id, cur_code,sum(ifnull(dr_amt,0)) dr_amt,sum(ifnull(cr_amt,0)) cr_amt,\n"
                + "			dept_code\n"
                + "			from gl \n"
                + "			where account_id in (select coa_code from chart_of_account where coa_level >=3 and comp_code='" + compCode + "')\n"
                + "         and date(gl_date) >= '" + opDate + "' \n"
                + "			and date(gl_date) <'" + stDate + "' \n"
                + "         and (dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + "))\n"
                + "			and comp_code = '" + compCode + "'\n"
                + "         and (cur_code = '" + currency + "' or '-'='" + currency + "')\n"
                + "			group by account_id, cur_code, source_ac_id) gl\n"
                + "	group by gl.account_id, gl.cur_code, gl.source_ac_id\n"
                + ")a\n"
                + "group by coa_code, cur_code";

        String sql = "insert into tmp_tri(coa_code, curr_id, dr_amt, cr_amt,dept_code,comp_code,mac_id)\n"
                + "select coa_code, cur_code,if(sum(dr_amt-cr_amt)>0, sum(dr_amt-cr_amt),0) dr_amt, \n"
                + "		if(sum(dr_amt-cr_amt)<0, sum(dr_amt-cr_amt)*-1,0) cr_amt,dept_code,'" + compCode + "'," + macId + "\n"
                + "from (\n"
                + "     select coa_code,cur_code,dr_amt,cr_amt,dept_code\n"
                + "     from tmp_closing \n"
                + "     where mac_id = " + macId + "\n"
                + "        union all\n"
                + "    	select gl.source_ac_id coa_code, gl.cur_code,sum(get_dr_cr_amt(gl.source_ac_id, gl.account_id, \n"
                + "			gl.source_ac_id, gl.dr_amt, gl.cr_amt, 'DR')) dr_amt,\n"
                + "                     sum(get_dr_cr_amt(gl.source_ac_id, gl.account_id, gl.source_ac_id, gl.dr_amt, \n"
                + "			gl.cr_amt, 'CR')) cr_amt,dept_code\n"
                + "	from (	select source_ac_id,account_id, cur_code,sum(ifnull(dr_amt,0)) dr_amt,sum(ifnull(cr_amt,0)) cr_amt,\n"
                + "			dept_code\n"
                + "			from gl \n"
                + "			where source_ac_id in (select coa_code from chart_of_account where coa_level >=3 and comp_code='" + compCode + "')\n"
                + "			and date(gl_date) between '" + stDate + "' \n"
                + "			and '" + enDate + "' and (dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + "))\n"
                + "			and comp_code = '" + compCode + "' and (cur_code = '" + currency + "' or '-'='" + currency + "')\n"
                + "			group by account_id, cur_code, source_ac_id) gl\n"
                + "	group by gl.account_id, gl.cur_code, gl.source_ac_id\n"
                + "			union all \n"
                + "    select gl.account_id coa_code, gl.cur_code,sum(get_dr_cr_amt(gl.source_ac_id, gl.account_id, \n"
                + "			gl.account_id, gl.dr_amt, gl.cr_amt, 'DR')) dr_amt,\n"
                + "                     sum(get_dr_cr_amt(gl.source_ac_id, gl.account_id, gl.account_id, gl.dr_amt, \n"
                + "			gl.cr_amt, 'CR')) cr_amt,dept_code\n"
                + "     from (	select source_ac_id,account_id, cur_code,sum(ifnull(dr_amt,0)) dr_amt,sum(ifnull(cr_amt,0)) cr_amt,\n"
                + "			dept_code\n"
                + "			from gl \n"
                + "			where account_id in (select coa_code from chart_of_account where coa_level >=3 and comp_code='" + compCode + "')\n"
                + "			and date(gl_date) between '" + stDate + "' \n"
                + "			and '" + enDate + "'\n"
                + "                     and (dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + "))\n"
                + "			and comp_code = '" + compCode + "' and (cur_code = '" + currency + "' or '-'='" + currency + "')\n"
                + "			group by account_id, cur_code, source_ac_id) gl\n"
                + "	group by gl.account_id, gl.cur_code, gl.source_ac_id) a\n"
                + "group by coa_code, cur_code";
        if (!closing) {
            dao.execSQLRpt(opSql);
        }
        dao.execSQLRpt(sql);
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
                    b.setUsrCoaCode(rs.getString("coa_code_usr"));
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
    public void genArAp(String compCode, String opDate, String stDate, String enDate, String currency, String traderCode, Integer macId) {
        String delSql = "delete from tmp_op_cl_apar where mac_id = " + macId + "";
        dao.execSQLRpt(delSql);
        String sql = "insert into tmp_op_cl_apar(coa_code, cur_code, dr_amt, cr_amt,dept_code,trader_code,mac_id,comp_code) \n"
                + "select coa_code, cur_code,if(sum(dr_amt-cr_amt)>0, sum(dr_amt-cr_amt),0) dr_amt, \n"
                + "		if(sum(dr_amt-cr_amt)<0, sum(dr_amt-cr_amt)*-1,0) cr_amt,dept_code,trader_code," + macId + ",'" + compCode + "'\n"
                + "from (\n"
                + "	#cal opening(\n"
                + "	select op.source_acc_id as coa_code, op.cur_code,\n"
                + "		   sum(ifnull(op.dr_amt,0)) dr_amt, sum(ifnull(op.cr_amt,0)) cr_amt,\n"
                + "           dept_code,trader_code\n"
                + "	from  coa_opening op\n"
                + "	where date(op.op_date) = '" + opDate + "' \n"
                + "	and (op.dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + "))\n"
                + "	and (cur_code = '" + currency + "' or '-'='" + currency + "')\n"
                + " and comp_code = '" + compCode + "'\n"
                + "	and source_acc_id in (select distinct account_code from trader where comp_code='" + compCode + "')\n"
                + " and trader_code is not null \n"
                + "	and (trader_code = '" + traderCode + "' or '-' = '" + traderCode + "')\n"
                + "	group by op.source_acc_id, op.cur_code,op.trader_code\n"
                + "			union all\n"
                + "	select gl.source_ac_id coa_code, gl.cur_code,sum(get_dr_cr_amt(gl.source_ac_id, gl.account_id, \n"
                + "			gl.source_ac_id, gl.dr_amt, gl.cr_amt, 'DR')) dr_amt,\n"
                + "                     sum(get_dr_cr_amt(gl.source_ac_id, gl.account_id, gl.source_ac_id, gl.dr_amt, \n"
                + "			gl.cr_amt, 'CR')) cr_amt,gl.dept_code,gl.trader_code\n"
                + "	from (	select source_ac_id,account_id, cur_code,sum(ifnull(dr_amt,0)) dr_amt,\n"
                + "			sum(ifnull(cr_amt,0)) cr_amt,dept_code,trader_code\n"
                + "			from gl \n"
                + "			where source_ac_id in (select distinct account_code from trader where comp_code='" + compCode + "')\n"
                + "			and date(gl_date) >= '" + opDate + "' \n"
                + "			and date(gl_date) <'" + stDate + "' \n"
                + "			and (dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + "))\n"
                + "			and comp_code = '" + compCode + "'\n"
                + "			and (cur_code = '" + currency + "' or '-'='" + currency + "')\n"
                + "         and trader_code is not null \n"
                + "	        and (trader_code = '" + traderCode + "' or '-' = '" + traderCode + "')\n"
                + "			group by account_id, cur_code, source_ac_id,trader_code) gl\n"
                + "	group by gl.account_id, gl.cur_code, gl.source_ac_id,gl.trader_code\n"
                + "			union all \n"
                + "    select gl.account_id coa_code, gl.cur_code,sum(get_dr_cr_amt(gl.source_ac_id, gl.account_id, \n"
                + "			gl.account_id, gl.dr_amt, gl.cr_amt, 'DR')) dr_amt,\n"
                + "			sum(get_dr_cr_amt(gl.source_ac_id, gl.account_id, gl.account_id, gl.dr_amt, \n"
                + "			gl.cr_amt, 'CR')) cr_amt,gl.dept_code,gl.trader_code\n"
                + "     from (	select source_ac_id,account_id, cur_code,sum(ifnull(dr_amt,0)) dr_amt,sum(ifnull(cr_amt,0)) cr_amt,\n"
                + "			dept_code,trader_code\n"
                + "			from gl \n"
                + "			where account_id in (select distinct account_code from trader where comp_code='" + compCode + "')\n"
                + "                     and date(gl_date) >= '" + opDate + "' \n"
                + "			and date(gl_date) <'" + stDate + "' \n"
                + "			and (dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + "))\n"
                + "			and comp_code='" + compCode + "'\n"
                + "			and (cur_code = '" + currency + "' or '-'='" + currency + "')\n"
                + "         and trader_code is not null \n"
                + "	        and (trader_code = '" + traderCode + "' or '-' = '" + traderCode + "')\n"
                + "			group by account_id, cur_code, source_ac_id,trader_code) gl\n"
                + "	group by gl.account_id, gl.cur_code, gl.source_ac_id,gl.trader_code\n"
                + "    #)\n"
                + "        union all\n"
                + "    	select gl.source_ac_id coa_code, gl.cur_code,sum(get_dr_cr_amt(gl.source_ac_id, gl.account_id, \n"
                + "			gl.source_ac_id, gl.dr_amt, gl.cr_amt, 'DR')) dr_amt,\n"
                + "                     sum(get_dr_cr_amt(gl.source_ac_id, gl.account_id, gl.source_ac_id, gl.dr_amt, \n"
                + "			gl.cr_amt, 'CR')) cr_amt,gl.dept_code,gl.trader_code\n"
                + "		from (	select source_ac_id,account_id, cur_code,sum(ifnull(dr_amt,0)) dr_amt,sum(ifnull(cr_amt,0)) cr_amt,\n"
                + "			dept_code,trader_code\n"
                + "			from gl \n"
                + "			where source_ac_id in (select distinct account_code from trader where comp_code='" + compCode + "')\n"
                + "			and date(gl_date) between '" + stDate + "' \n"
                + "			and '" + enDate + "'\n"
                + "			and (dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + "))\n"
                + "			and comp_code = '" + compCode + "' and (cur_code = '" + currency + "' or '-'='" + currency + "')\n"
                + "         and trader_code is not null \n"
                + "	        and (trader_code = '" + traderCode + "' or '-' = '" + traderCode + "')\n"
                + "			group by account_id, cur_code, source_ac_id,trader_code) gl\n"
                + "	group by gl.account_id, gl.cur_code, gl.source_ac_id,gl.trader_code\n"
                + "			union all \n"
                + "    select gl.account_id coa_code, gl.cur_code,sum(get_dr_cr_amt(gl.source_ac_id, gl.account_id, \n"
                + "			gl.account_id, gl.dr_amt, gl.cr_amt, 'DR')) dr_amt,\n"
                + "                     sum(get_dr_cr_amt(gl.source_ac_id, gl.account_id, gl.account_id, gl.dr_amt, \n"
                + "			gl.cr_amt, 'CR')) cr_amt,gl.dept_code,gl.trader_code\n"
                + "     from (	select source_ac_id,account_id, cur_code,sum(ifnull(dr_amt,0)) dr_amt,sum(ifnull(cr_amt,0)) cr_amt,\n"
                + "			dept_code,trader_code\n"
                + "			from gl \n"
                + "			where account_id in (select distinct account_code from trader where comp_code='" + compCode + "')\n"
                + "			and date(gl_date) between '" + stDate + "' \n"
                + "			and '" + enDate + "'\n"
                + "			and (dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + "))\n"
                + "			and comp_code = '" + compCode + "' and (cur_code = '" + currency + "' or '-'='" + currency + "')\n"
                + "         and trader_code is not null \n"
                + "	        and (trader_code = '" + traderCode + "' or '-' = '" + traderCode + "')\n"
                + "			group by account_id, cur_code, source_ac_id,trader_code) gl\n"
                + "	group by gl.account_id, gl.cur_code, gl.source_ac_id,gl.trader_code) a\n"
                + "group by coa_code, cur_code,trader_code";
        dao.execSQLRpt(sql);
    }

    @Override
    public List<VApar> getApAr(String traderCode, String traderType, Integer macId) {
        String sql = "select tmp.trader_code,t.user_code,t.discriminator,t.trader_name,tmp.cur_code,tmp.dr_amt,tmp.cr_amt\n" +
                "from tmp_op_cl_apar tmp join trader t\n" +
                "on tmp.trader_code = t.code\n" +
                "where (t.discriminator = '" + traderType + "' or '-' = '" + traderType + "')\n" +
                "and tmp.mac_id = " + macId + "";
        ResultSet rs = dao.executeSql(sql);
        List<VApar> aparList = new ArrayList<>();
        try {
            if (!Objects.isNull(rs)) {
                while (rs.next()) {
                    VAparKey key = new VAparKey();
                    key.setTraderCode(rs.getString("trader_code"));
                    key.setCurCode(rs.getString("cur_code"));
                    VApar apar = new VApar();
                    apar.setKey(key);
                    apar.setUserCode(rs.getString("t.user_code"));
                    apar.setTraderName(rs.getString("trader_name"));
                    apar.setDrAmt(rs.getDouble("dr_amt"));
                    apar.setCrAmt(rs.getDouble("cr_amt"));
                    aparList.add(apar);
                }
            }
        } catch (Exception e) {
            log.error(String.format("getApAr: %s", e.getMessage()));
        }
        return aparList;
    }

    @Override
    public List<VGl> getIncomeAndExpenditure(String incomeGroup, String expenseGroup, Integer macId) {
        String[] i = incomeGroup.split(",");
        String[] e = expenseGroup.split(",");
        String delSql = "delete from tmp_in_ex where mac_id = " + macId + "";
        dao.execSQLRpt(delSql);
        for (String coa : i) {
            String sql = "insert into tmp_in_ex(sort_order,coa_code,cur_code,acc_total,dept_code,mac_id)\n" +
                    "select 1,tmp.coa_code, tmp.curr_id, (tmp.cr_amt-tmp.dr_amt) amt, tmp.dept_code,tmp.mac_id\n" +
                    "from tmp_tri tmp join chart_of_account coa\n" +
                    "on tmp.coa_code = coa.coa_code\n" +
                    "join chart_of_account coa1 \n" +
                    "on coa.coa_parent = coa1.coa_code\n" +
                    "where tmp.mac_id = " + macId + "\n" +
                    "and (dr_amt > 0 or cr_amt >0)\n" +
                    "and coa1.coa_parent = '" + coa + "'";
            dao.execSQLRpt(sql);
        }
        for (String coa : e) {
            String sql = "insert into tmp_in_ex(sort_order,coa_code,cur_code,acc_total,dept_code,mac_id)\n" +
                    "select 2,tmp.coa_code, tmp.curr_id, (tmp.dr_amt -tmp.cr_amt) amt, tmp.dept_code,tmp.mac_id\n" +
                    "from tmp_tri tmp join chart_of_account coa\n" +
                    "on tmp.coa_code = coa.coa_code\n" +
                    "join chart_of_account coa1 \n" +
                    "on coa.coa_parent = coa1.coa_code\n" +
                    "where tmp.mac_id = " + macId + "\n" +
                    "and (dr_amt > 0 or cr_amt >0)\n" +
                    "and coa1.coa_parent = '" + coa + "'";
            dao.execSQLRpt(sql);
        }
        return null;
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
}
