package core.acc.api.controller;

import core.acc.api.common.Util1;
import core.acc.api.entity.COAOpening;
import core.acc.api.entity.Gl;
import core.acc.api.entity.VApar;
import core.acc.api.model.Financial;
import core.acc.api.model.ReportFilter;
import core.acc.api.model.ReturnObject;
import core.acc.api.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    private ReturnObject ro = new ReturnObject();

    @PostMapping(value = "/getReport", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<?> getReport(@RequestBody ReportFilter filter) {
        String compCode = Util1.isNull(filter.getCompCode(), "-");
        String opDate = reportService.getOpeningDate(compCode);
        String fromDate = filter.getFromDate();
        String toDate = filter.getToDate();
        String srcAcc = Util1.isNull(filter.getSrcAcc(), "-");
        String curCode = Util1.isNull(filter.getCurCode(), "-");
        String traderCode = Util1.isNull(filter.getTraderCode(), "-");
        String coaCode = Util1.isNull(filter.getCoaCode(), "-");
        String fixAcc = Util1.isNull(filter.getFixedAcc(), "-");
        String curAcc = Util1.isNull(filter.getCurrentAcc(), "-");
        String liaAcc = Util1.isNull(filter.getLiaAcc(), "-");
        String capitalAcc = Util1.isNull(filter.getCapitalAcc(), "-");
        String incomeAcc = Util1.isNull(filter.getIncomeAcc(), "-");
        String otherIncomeAcc = Util1.isNull(filter.getOtherIncomeAcc(), "-");
        String purAcc = Util1.isNull(filter.getPurchaseAcc(), "-");
        String expenseAcc = Util1.isNull(filter.getExpenseAcc(), "-");

        String ieProcess = String.format("%s,%s,%s,%s", incomeAcc, otherIncomeAcc, purAcc, expenseAcc);
        String plProcess = String.format("%s,%s,%s,%s", incomeAcc, purAcc, otherIncomeAcc, expenseAcc);
        String bsProcess = String.format("%s,%s,%s,%s", fixAcc, curAcc, liaAcc, capitalAcc);
        String invGroup = Util1.isNull(filter.getInvGroup(), "-");
        String deptCode = Util1.isNull(filter.getDeptCode(), "-");
        String cashGroup = Util1.isNull(filter.getCashGroup(), "-");
        String reAcc = Util1.isNull(filter.getReAcc(), "-");
        String plAcc = Util1.isNull(filter.getPlAcc(), "-");
        Integer macId = filter.getMacId();
        String projectNo = Util1.isAll(filter.getProjectNo());
        String reportName = filter.getReportName();
        String exportPath = String.format("temp%s%s.json", File.separator, reportName + filter.getMacId());
        createFilePath(exportPath);
        reportService.insertTmp(filter.getListDepartment(), macId, compCode);
        try {
            switch (reportName) {
                case "OpeningTri" -> {
                    List<COAOpening> list = reportService.getOpeningTri(opDate, deptCode, curCode, compCode);
                    Util1.writeJsonFile(list, exportPath);
                }
                case "OpeningBalanceSheetDetail" ->
                        Util1.writeJsonFile(reportService.getOpeningBalanceSheet(bsProcess, opDate, true, compCode), exportPath);
                case "OpeningBalanceSheetSummary" ->
                        Util1.writeJsonFile(reportService.getOpeningBalanceSheet(bsProcess, opDate, false, compCode), exportPath);
                case "Income&ExpenditureDetail" -> {
                    reportService.genTriBalance(compCode, fromDate, toDate, opDate, "-", "-", "-", plProcess, bsProcess, projectNo, "-", true, macId);
                    List<Financial> data = reportService.getIncomeAndExpenditure(ieProcess, true, macId);
                    Util1.writeJsonFile(data, exportPath);
                }
                case "Income&ExpenditureSummary" -> {
                    List<Financial> data = reportService.getIncomeAndExpenditure(ieProcess, false, macId);
                    Util1.writeJsonFile(data, exportPath);
                }
                case "Profit&LossDetail" -> {
                    List<Financial> data = calPl(plProcess, opDate, fromDate, toDate, invGroup, true, projectNo, compCode, macId);
                    Util1.writeJsonFile(data, exportPath);
                }
                case "Profit&LossSummary" -> {
                    List<Financial> data = calPl(plProcess, opDate, fromDate, toDate, invGroup, false, projectNo, compCode, macId);
                    Util1.writeJsonFile(data, exportPath);
                }
                case "BalanceSheetDetail" -> {
                    List<Financial> data = calBS(fromDate, toDate, opDate, invGroup, reAcc, plAcc, plProcess, bsProcess, true, projectNo, compCode, macId);
                    Util1.writeJsonFile(data, exportPath);
                }
                case "BalanceSheetSummary" -> {
                    List<Financial> data = calBS(fromDate, toDate, opDate, invGroup, reAcc, plAcc, plProcess, bsProcess, false, projectNo, compCode, macId);
                    Util1.writeJsonFile(data, exportPath);
                }
                case "CreditDetail" -> {
                    List<Gl> data = reportService.getTraderBalance(traderCode, coaCode, curCode, opDate, fromDate, toDate, compCode, macId);
                    Util1.writeJsonFile(data, exportPath);
                }
                case "SharerHolderStatement" -> {
                    List<Gl> data = reportService.getSharerHolderStatement(traderCode, coaCode, curCode, opDate, fromDate, toDate, compCode, macId);
                    Util1.writeJsonFile(data, exportPath);
                }
                case "IndividualStatement" -> {
                    List<Gl> data = reportService.getIndividualStatement(srcAcc, curCode, opDate, fromDate, toDate, compCode, macId);
                    Util1.writeJsonFile(data, exportPath);
                }
                case "AllCashDaily" -> {
                    List<Gl> data = reportService.getAllCashDaily(opDate, fromDate, toDate, curCode, cashGroup, compCode, macId);
                    Util1.writeJsonFile(data, exportPath);
                }
                case "COA" -> {
                    List<Financial> list = reportService.getCOAList(compCode);
                    Util1.writeJsonFile(list, exportPath);
                }
            }
            try (FileInputStream in = new FileInputStream(exportPath)) {
                byte[] bytes = in.readAllBytes();
                ro = reportService.getReportResult(macId);
                ro.setOpDate(Util1.toDateStr(Util1.toDate(opDate), "dd/MM/yyyy"));
                ro.setFile(bytes);
            }
        } catch (Exception e) {
            ro.setErrorMessage(e.getMessage());
            log.error(String.format("getReport: %s", e.getMessage()));
        }
        return Mono.justOrEmpty(ro);
    }

    private void createFilePath(String path) {
        File file = new File(path);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            if (!created) {
                log.error("Failed to create the parent directory for the file.");
                // You might want to throw an exception or handle the failure in an appropriate way
            }
        }
    }

    private List<Financial> calPl(String plProcess, String opDate, String fromDate, String toDate, String invGroup,
                                  boolean detail, String projectNo, String compCode, Integer macId) {
        return reportService.getProfitLost(plProcess, opDate, fromDate, toDate, invGroup, detail, projectNo, compCode, macId);
    }

    private List<Financial> calBS(String fromDate, String toDate, String opDate, String invGroup, String reAcc,
                                  String plAcc, String plProcess, String bsProcess, boolean detail,
                                  String projectNo, String compCode, Integer macId) {
        double prvProfit = 0.0;
        if (!fromDate.equals(opDate)) {
            prvProfit = reportService.getProfit(opDate, opDate, Util1.minusDay(fromDate, 1), invGroup, plProcess, projectNo, compCode, macId);
        }
        double curProfit = reportService.getProfit(opDate, fromDate, toDate, invGroup, plProcess, projectNo, compCode, macId);
        return reportService.getBalanceSheet(bsProcess, opDate, fromDate, toDate, invGroup, reAcc, plAcc, detail, prvProfit, curProfit, projectNo, compCode, macId);
    }

    @GetMapping(path = "/getReportResult")
    public Mono<?> getReportResult(@RequestParam Integer macId) {
        return Mono.justOrEmpty(reportService.getReportResult(macId));
    }

    @PostMapping(path = "/getTriBalance")
    public Flux<?> getTriBalance(@RequestBody ReportFilter filter) throws IOException {
        String coaCode = Util1.isNull(filter.getCoaCode(), "-");
        String coaLv1 = Util1.isNull(filter.getCoaLv1(), "-");
        String coaLv2 = Util1.isNull(filter.getCoaLv2(), "-");
        String compCode = filter.getCompCode();
        String stDate = filter.getFromDate();
        String enDate = filter.getToDate();
        String opDate = reportService.getOpeningDate(compCode);
        String currency = filter.getCurCode();
        boolean netChange = filter.isClosing();
        Integer macId = filter.getMacId();
        String projectNo = Util1.isAll(filter.getProjectNo());
        String tranSource = Util1.isAll(filter.getTranSource());
        reportService.insertTmp(filter.getListDepartment(), macId, compCode);
        reportService.genTriBalance(compCode, stDate, enDate, opDate, currency, coaLv1,
                coaLv2, "-", "-", projectNo, tranSource, netChange, macId);
        return Flux.fromIterable(reportService.getTriBalance(coaCode, coaLv1, coaLv2, compCode, macId)).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/getArAp")
    public Flux<?> getArAp(@RequestBody ReportFilter filter) {
        String compCode = filter.getCompCode();
        String enDate = filter.getToDate();
        String opDate = reportService.getOpeningDate(compCode);
        String traderCode = Util1.isNull(filter.getTraderCode(), "-");
        String currency = Util1.isNull(filter.getCurCode(), "-");
        Integer macId = filter.getMacId();
        String coaCode = Util1.isNull(filter.getCoaCode(), "-");
        String projectNo = Util1.isAll(filter.getProjectNo());
        reportService.insertTmp(filter.getListDepartment(), macId, compCode);
        List<VApar> list = reportService.genArAp(compCode, opDate, enDate, currency, traderCode, coaCode, projectNo, macId);
        return Flux.fromIterable(list).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getTraderBalance")
    public Mono<Double> getTraderBalance(@RequestParam String date,
                                         @RequestParam String traderCode,
                                         @RequestParam String curCode,
                                         @RequestParam String compCode) {
        String opDate = reportService.getOpeningDate(compCode);
        return Mono.justOrEmpty(reportService.getTraderLastBalance(opDate, date, curCode, traderCode, compCode));
    }
}