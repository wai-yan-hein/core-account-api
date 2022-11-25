package core.acc.api.service;

import core.acc.api.entity.Gl;
import core.acc.api.entity.VApar;
import core.acc.api.entity.VTriBalance;
import core.acc.api.model.BalanceSheetRetObj;
import core.acc.api.model.Financial;
import core.acc.api.model.ProfitAndLostRetObj;

import java.sql.SQLException;
import java.util.List;

public interface ReportService {
    void insertTmp(List<String> listStr, Integer macId, String taleName);
    String getOpeningDate(String compCode);
    List<Gl> getIndividualLager(String fromDate, String toDate, String desp, String srcAcc,
                                String acc, String curCode, String reference,
                                String compCode, String tranSource, String traderCode, String traderType,
                                String coaLv2, String coaLv1, Integer macId) throws SQLException;

    List<Financial> getProfitLost(String plProcess, String stDate, String enDate, boolean detail, String compCode, Integer macId) throws Exception;

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

    List<VApar> genArAp(String compCode, String opDate,
                        String clDate, String currency, String traderCode, String coaCode, Integer macId);

    List<Financial> getIncomeAndExpenditure(String process, boolean detail, Integer macId);

    double getTraderLastBalance(String date, String traderCode, String compCode);

}
