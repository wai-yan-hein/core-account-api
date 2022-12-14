package core.acc.api.controller;

import core.acc.api.common.Util1;
import core.acc.api.entity.COAOpening;
import core.acc.api.entity.Gl;
import core.acc.api.entity.VApar;
import core.acc.api.entity.VTriBalance;
import core.acc.api.model.Financial;
import core.acc.api.model.ReportFilter;
import core.acc.api.model.ReturnObject;
import core.acc.api.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/report")
public class ReportController {
    @Autowired
    private ReportService reportService;
    private final String exportPath = "temp/";

    private ReturnObject ro = new ReturnObject();

    @PostMapping(value = "/get-report", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ReturnObject getReport(@RequestBody ReportFilter filter) {
        String compCode = Util1.isNull(filter.getCompCode(), "-");
        String opDate = reportService.getOpeningDate(compCode);
        String fromDate = filter.getFromDate();
        String toDate = filter.getToDate();
        String srcAcc = Util1.isNull(filter.getSrcAcc(), "-");
        String curCode = Util1.isNull(filter.getCurCode(), "-");
        String traderCode = Util1.isNull(filter.getTraderCode(), "-");
        String coaCode = Util1.isNull(filter.getCoaCode(), "-");
        String ieProcess = Util1.isNull(filter.getIncomeExpenseProcess(), "-");
        String plProcess = Util1.isNull(filter.getPlProcess(), "-");
        String bsProcess = Util1.isNull(filter.getBsProcess(), "-");
        String invGroup = Util1.isNull(filter.getInvGroup(), "-");
        String deptCode = Util1.isNull(filter.getDeptCode(), "-");
        Integer macId = filter.getMacId();
        String reportName = filter.getReportName();
        String exportPath = String.format("temp%s%s.json", File.separator, reportName + filter.getMacId());
        reportService.insertTmp(filter.getListDepartment(), macId, "tmp_dep_filter");
        try {
            switch (reportName) {
                case "OpeningTri" -> {
                    List<COAOpening> list = reportService.getOpeningTri(opDate, deptCode, curCode, compCode);
                    Util1.writeJsonFile(list, exportPath);
                }
                case "Income&ExpenditureDetail" -> {
                    reportService.genTriBalance(compCode, fromDate, toDate, opDate, "-", "-", "-", plProcess, bsProcess, true, macId);
                    List<Financial> data = reportService.getIncomeAndExpenditure(ieProcess, true, macId);
                    Util1.writeJsonFile(data, exportPath);
                }
                case "Income&ExpenditureSummary" -> {
                    List<Financial> data = reportService.getIncomeAndExpenditure(ieProcess, false, macId);
                    Util1.writeJsonFile(data, exportPath);
                }
                case "Profit&LossDetail" -> {
                    List<Financial> data = calPl(plProcess, opDate, fromDate, toDate, invGroup, true, compCode, macId);
                    Util1.writeJsonFile(data, exportPath);
                }
                case "Profit&LossSummary" -> {
                    List<Financial> data = calPl(plProcess, opDate, fromDate, toDate, invGroup, false, compCode, macId);
                    Util1.writeJsonFile(data, exportPath);
                }
                case "BalanceSheetDetail" -> {
                    List<Financial> data = calBS(fromDate, toDate, opDate, invGroup, plProcess, bsProcess, true, compCode, macId);
                    Util1.writeJsonFile(data, exportPath);
                }
                case "BalanceSheetSummary" -> {
                    List<Financial> data = calBS(fromDate, toDate, opDate, invGroup, plProcess, bsProcess, false, compCode, macId);
                    Util1.writeJsonFile(data, exportPath);
                }
                case "CreditDetail" -> {
                    List<Gl> data = reportService.getTraderBalance(traderCode, coaCode, curCode, fromDate, toDate, compCode, macId);
                    Util1.writeJsonFile(data, exportPath);
                }
                case "IndividualStatement" -> {
                    List<Gl> data = reportService.getIndividualStatement(srcAcc,curCode,opDate,fromDate,toDate,compCode,macId);
                    Util1.writeJsonFile(data, exportPath);
                }
            }
            try (FileInputStream in = new FileInputStream(exportPath)) {
                byte[] bytes = in.readAllBytes();
                ro = reportService.getReportResult(macId);
                ro.setFile(bytes);
            }
        } catch (Exception e) {
            ro.setErrorMessage(e.getMessage());
            log.error(String.format("getReport: %s", e.getMessage()));
        }
        return ro;
    }

    private List<Financial> calPl(String plProcess, String opDate, String fromDate, String toDate,
                                  String invGroup, boolean detail, String compCode, Integer macId) {
        return reportService.getProfitLost(plProcess, opDate, fromDate, toDate, invGroup, detail, compCode, macId);
    }

    private List<Financial> calBS(String fromDate, String toDate, String opDate, String invGroup, String plProcess,
                                  String bsProcess, boolean detail, String compCode, Integer macId) {
        double prvProfit = 0.0;
        if (!fromDate.equals(opDate)) {
            prvProfit = reportService.getProfit(opDate, opDate, Util1.minusDay(fromDate, 1), invGroup, plProcess, compCode, macId);
        }
        double curProfit = reportService.getProfit(opDate, fromDate, toDate, invGroup, plProcess, compCode, macId);
        return reportService.getBalanceSheet(bsProcess, opDate, fromDate, toDate, invGroup, detail, prvProfit, curProfit, compCode, macId);
    }

    @GetMapping(path = "/get-report-result")
    public ResponseEntity<?> getResult(@RequestParam Integer macId) {
        return ResponseEntity.ok(reportService.getReportResult(macId));
    }

    @PostMapping(path = "/get-tri-balance")
    public ResponseEntity<?> getTriBalance(@RequestBody ReportFilter filter) throws IOException {
        String coaCode = filter.getCoaCode();
        String coaLv1 = filter.getCoaLv1();
        String coaLv2 = filter.getCoaLv2();
        String compCode = filter.getCompCode();
        String stDate = filter.getFromDate();
        String enDate = filter.getToDate();
        String opDate = reportService.getOpeningDate(compCode);
        String currency = filter.getCurCode();
        boolean netChange = filter.isClosing();
        Integer macId = filter.getMacId();
        reportService.insertTmp(filter.getListDepartment(), macId, "tmp_dep_filter");
        reportService.genTriBalance(compCode, stDate, enDate, opDate, currency, coaLv1, coaLv2, "-", "-", netChange, macId);
        List<VTriBalance> list = reportService.getTriBalance(coaCode, coaLv1, coaLv2, macId);
        String fileName = "TRI" + macId.toString() + ".json";
        String path = exportPath + fileName;
        Util1.writeJsonFile(list, path);
        ro.setFile(Util1.zipJsonFile(path));
        return ResponseEntity.ok(ro);
    }

    @PostMapping(path = "/get-arap")
    public ResponseEntity<?> getArap(@RequestBody ReportFilter filter) throws IOException {
        String compCode = filter.getCompCode();
        String enDate = filter.getToDate();
        String opDate = reportService.getOpeningDate(compCode);
        String traderCode = Util1.isNull(filter.getTraderCode(), "-");
        String currency = Util1.isNull(filter.getCurCode(), "-");
        Integer macId = filter.getMacId();
        String coaCode = Util1.isNull(filter.getCoaCode(), "-");
        reportService.insertTmp(filter.getListDepartment(), macId, "tmp_dep_filter");
        List<VApar> list = reportService.genArAp(compCode, opDate, enDate, currency, traderCode, coaCode, macId);
        String fileName = "ARAP" + macId.toString() + ".json";
        String path = exportPath + fileName;
        Util1.writeJsonFile(list, path);
        ro.setFile(Util1.zipJsonFile(path));
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/get-trader-balance")
    public ResponseEntity<Double> getTraderBalance(@RequestParam String date, @RequestParam String traderCode, @RequestParam String compCode) {
        return ResponseEntity.ok(reportService.getTraderLastBalance(date, traderCode, compCode));
    }
}
