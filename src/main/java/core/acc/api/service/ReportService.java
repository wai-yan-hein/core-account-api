package core.acc.api.service;

import core.acc.api.entity.COAOpening;
import core.acc.api.entity.Gl;
import core.acc.api.entity.VApar;
import core.acc.api.entity.VTriBalance;
import core.acc.api.model.Financial;
import core.acc.api.model.ReturnObject;
import core.acc.api.model.VoucherInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface ReportService {
    void insertTmp(List<String> listStr, Integer macId, String compCode);

    ResultSet getResult(String sql);

    String getOpeningDate(String compCode);

    List<Gl> getIndividualLedger(String fromDate, String toDate, String desp, String srcAcc,
                                 String acc, String curCode, String reference,
                                 String compCode, String tranSource, String traderCode, String traderType,
                                 String coaLv2, String coaLv1, String batchNo, String projectNo,
                                 boolean summary, Integer macId) throws SQLException;

    List<Financial> getProfitLost(String plProcess, String opDate, String stDate, String enDate, String invGroup,
                                  boolean detail, String projectNo, String compCode, Integer macId);

    double getProfit(String opDate, String stDate, String enDate, String invGroup, String plProcess, String projectNo,
                     String compCode, Integer macId);

    List<Financial> getBalanceSheet(String bsProcess, String opDate, String stDate, String enDate, String invGroup, String reAcc, String plAcc,
                                    boolean detail, double prvProfit, double curProfit,
                                    String projectNo, String compCode, Integer macId);

    List<Financial> getOpeningBalanceSheet(String bsProcess, String opDate, boolean detail, String compCode);

    void executeAndResult(String... sql) throws Exception;


    void genTriBalance(String compCode, String stDate, String enDate,
                       String opDate, String currency, String coaLv1, String coaLv2, String plProcess, String bsProcess,
                       String projectNo, String tranSource, boolean netChange, Integer macId);

    List<VTriBalance> getTriBalance(String coaCode, String coaLv1, String coaLv2, String compCode, Integer macId);

    List<VApar> genArAp(String compCode, String opDate,
                        String clDate, String currency, String traderCode, String coaCode, String projectNo, Integer macId);

    List<Financial> getIncomeAndExpenditure(String process, boolean detail, Integer macId);

    double getTraderLastBalance(String opDate, String toDate, String curCode, String traderCode, String compCode);

    ReturnObject getReportResult(Integer macId);

    List<Gl> getTraderBalance(String traderCode, String accCode,
                              String curCode,String opDate, String fromDate, String toDate, String compCode, Integer macId);

    List<Gl> getIndividualStatement(String sourceAcc, String curCode, String opDate, String fromDate, String toDate, String compCode, Integer macId);

    List<COAOpening> getOpeningTri(String opDate, String deptCode, String curCode, String compCode);

    List<Gl> getAllCashDaily(String opDate, String fromDate, String toDate, String curCode, String cashGroup, String compCode, Integer mac_id);

    List<Financial> getCOAList(String compCode);

    Gl getCashBook(String startDate, String endDate, String srcAcc, String curCode, String compCode);
    List<VoucherInfo> getIntegrationVoucher(String fromDate,String toDate,String tranSource,String compCode);
}
