package core.acc.api.service;

import core.acc.api.entity.Gl;
import core.acc.api.entity.VApar;
import core.acc.api.entity.VTriBalance;
import core.acc.api.model.Financial;
import core.acc.api.model.ReturnObject;
import core.acc.api.model.TraderBalance;

import java.sql.SQLException;
import java.util.List;

public interface ReportService {
    void insertTmp(List<String> listStr, Integer macId, String taleName);

    String getOpeningDate(String compCode);

    List<Gl> getIndividualLager(String fromDate, String toDate, String desp, String srcAcc,
                                String acc, String curCode, String reference,
                                String compCode, String tranSource, String traderCode, String traderType,
                                String coaLv2, String coaLv1, Integer macId) throws SQLException;

    List<Financial> getProfitLost(String plProcess, String opDate, String stDate, String enDate, String invGroup,
                                  boolean detail, String compCode, Integer macId);

    double getProfit(String opDate, String stDate, String enDate, String invGroup,String plProcess, String compCode, Integer macId);

    List<Financial> getBalanceSheet(String bsProcess, String opDate, String stDate, String enDate, String invGroup,
                                    boolean detail, double prvProfit, double curProfit, String compCode, Integer macId);

    double genOpBalance(String process, String opDate, String clDate,
                        String endDate, String curr, String compCode, String dept, String macId) throws Exception;

    void executeSql(String... sql) throws Exception;


    void genTriBalance(String compCode, String stDate, String enDate,
                       String opDate, String currency, String coaLv1, String coaLv2, String plProcess, String bsProcess,
                       boolean netChange, Integer macId);

    List<VTriBalance> getTriBalance(String coaCode, String coaLv1, String coaLv2, Integer macId);

    List<VApar> genArAp(String compCode, String opDate,
                        String clDate, String currency, String traderCode, String coaCode, Integer macId);

    List<Financial> getIncomeAndExpenditure(String process, boolean detail, Integer macId);

    double getTraderLastBalance(String date, String traderCode, String compCode);

    ReturnObject getReportResult(Integer macId);
    List<TraderBalance> getTraderBalance(String traderCode, String accCode,
                                         String curCode, String fromDate, String toDate, String compCode, Integer macId);
}
