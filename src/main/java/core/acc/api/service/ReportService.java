package core.acc.api.service;

import core.acc.api.entity.Gl;
import core.acc.api.entity.VApar;
import core.acc.api.entity.VTriBalance;
import core.acc.api.model.BalanceSheetRetObj;
import core.acc.api.model.ProfitAndLostRetObj;

import java.sql.SQLException;
import java.util.List;

public interface ReportService {
    void insertTmp(List<String> listStr, Integer macId, String taleName);

    List<Gl> getIndividualLager(String fromDate, String toDate, String desp, String srcAcc,
                                String acc, String curCode, String reference,
                                String compCode, String tranSource, String traderCode, String traderType,
                                String coaLv2, String coaLv1, Integer macId) throws SQLException;

    void getProfitLost(String plProcess, String from, String to, String dept,
                       String currency, String comp, String userCode, String macId, String invCOA) throws Exception;

    void getProfitLostMultiCurrency(String plProcess, String stDate,
                                    String enDate, String dept, String reqCurrency, String comp,
                                    String userCode, String macId, String inventory) throws Exception;

    ProfitAndLostRetObj getPLCalculateValue(String compCode, String macId, boolean multiCur);

    BalanceSheetRetObj getBSCalculateValue(String compCode, double prvProfit,
                                           double profit, String macId) throws Exception;

    void genBalanceSheet(String toDate, String compCode, String curr,
                         String macId, String process, String inventory, String strDep) throws Exception;

    void genBalanceSheetDetail(String toDate, String compCode, String curr,
                               String macId, String process, String inventory, String strDep) throws Exception;

    void genIncomeAndExpense(String process, String compCode, String strDep,
                             String macId, String currency, boolean multiCur);

    ProfitAndLostRetObj calculateIncomeExpense(String compCode, String macId, boolean multiCur);

    void deleteOpTemp(String macId) throws Exception;

    double genOpBalance(String process, String opDate, String clDate,
                        String endDate, String curr, String compCode, String dept, String macId) throws Exception;

    void executeSql(String... sql) throws Exception;

    double genCash(String stDate, String endDate, String cashGroup, String deptStr, String compCode, String macId) throws Exception;

    void genTriBalance(String compCode, String stDate, String enDate,
                       String opDate, String currency, boolean closing, Integer macId);

    List<VTriBalance> getTriBalance(String coaCode, String coaLv1, String coaLv2, Integer macId);

    void genArAp(String compCode, String opDate, String stDate,
                 String enDate, String currency, String traderCode, Integer macId);

    List<VApar> getApAr(String traderCode, String traderType, Integer macId);

    List<Gl> getIncomeAndExpenditure(String incomeGroup, String expenseGroup, Integer macId);

    double getTraderLastBalance(String date, String traderCode, String compCode);

}
