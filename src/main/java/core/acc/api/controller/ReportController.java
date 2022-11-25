package core.acc.api.controller;

import core.acc.api.common.ReturnObject;
import core.acc.api.common.Util1;
import core.acc.api.entity.Gl;
import core.acc.api.entity.VApar;
import core.acc.api.entity.VTriBalance;
import core.acc.api.model.Financial;
import core.acc.api.model.ReportFilter;
import core.acc.api.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/report")
public class ReportController {
    @Autowired
    private ReportService reportService;
    private final ReturnObject ro = new ReturnObject();

    @PostMapping(value = "/get-report", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ReturnObject getReport(@RequestBody ReportFilter filter) {
        String compCode = Util1.isNull(filter.getCompCode(), "-");
        String opDate = reportService.getOpeningDate(compCode);
        String fromDate = filter.getFromDate();
        String toDate = filter.getToDate();
        String des = Util1.isNull(filter.getDesp(), "-");
        String srcAcc = Util1.isNull(filter.getSrcAcc(), "-");
        String acc = Util1.isNull(filter.getAcc(), "-");
        String curCode = Util1.isNull(filter.getCurCode(), "-");
        String reference = Util1.isNull(filter.getReference(), "-");
        String tranSource = Util1.isNull(filter.getTranSource(), "-");
        String traderCode = Util1.isNull(filter.getTraderCode(), "-");
        String traderType = Util1.isNull(filter.getTraderType(), "-");
        String coaCode = Util1.isNull(filter.getCoaCode(), "-");
        String coaLv1 = Util1.isNull(filter.getCoaLv1(), "-");
        String coaLv2 = Util1.isNull(filter.getCoaLv2(), "-");
        String ieProcess = Util1.isNull(filter.getIncomeExpenseProcess(), "-");
        Integer macId = filter.getMacId();
        String reportName = filter.getReportName();
        String exportPath = String.format("temp%s%s.json", File.separator, reportName + filter.getMacId());
        reportService.insertTmp(filter.getListDepartment(), macId, "tmp_dep_filter");
        try {
            switch (reportName) {
                case "TriBalance" -> {
                    List<VTriBalance> triBalance = reportService.getTriBalance(coaCode, coaLv1, coaLv2, macId);
                    Util1.writeJsonFile(triBalance, exportPath);
                }
                case "ARAP" -> {
                    List<VApar> list = reportService.genArAp(compCode, opDate, toDate, curCode, traderCode, coaCode, macId);
                    Util1.writeJsonFile(list, exportPath);
                }
                case "Income&ExpenditureDetail" -> {
                    reportService.genTriBalance(compCode, fromDate, toDate, opDate, "-", true, macId);
                    List<Financial> data = reportService.getIncomeAndExpenditure(ieProcess, true, macId);
                    Util1.writeJsonFile(data, exportPath);
                }
                case "Income&ExpenditureSummary" -> {
                    List<Financial> data = reportService.getIncomeAndExpenditure(ieProcess, false, macId);
                    Util1.writeJsonFile(data, exportPath);
                }
                case "IndividualLedger" -> {
                    List<Gl> Gls = reportService.getIndividualLager(fromDate, toDate, des, srcAcc, acc, curCode, reference, compCode, tranSource, traderCode, traderType, coaLv2, coaLv1, macId);
                    Util1.writeJsonFile(Gls, exportPath);
                }
            }
            try (FileInputStream in = new FileInputStream(exportPath)) {
                byte[] bytes = in.readAllBytes();
                ro.setFile(bytes);
            }

        } catch (Exception e) {
            ro.setErrorMessage(e.getMessage());
            log.error(String.format("getReport: %s", e.getMessage()));
        }
        return ro;
    }

    @PostMapping(path = "/get-tri-balance")
    public ResponseEntity<List<VTriBalance>> getTriBalance(@RequestBody ReportFilter filter) {
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
        reportService.genTriBalance(compCode, stDate, enDate, opDate, currency, netChange, macId);
        List<VTriBalance> triBalance = reportService.getTriBalance(coaCode, coaLv1, coaLv2, macId);
        return ResponseEntity.ok(triBalance);
    }

    @PostMapping(path = "/get-arap")
    public ResponseEntity<List<VApar>> getArap(@RequestBody ReportFilter filter) {
        String compCode = filter.getCompCode();
        String enDate = filter.getToDate();
        String opDate = reportService.getOpeningDate(compCode);
        String traderCode = Util1.isNull(filter.getTraderCode(), "-");
        String currency = Util1.isNull(filter.getCurCode(), "-");
        Integer macId = filter.getMacId();
        String coaCode = Util1.isNull(filter.getCoaCode(), "-");
        reportService.insertTmp(filter.getListDepartment(), macId, "tmp_dep_filter");
        return ResponseEntity.ok(reportService.genArAp(compCode, opDate, enDate, currency, traderCode, coaCode, macId));
    }

    @GetMapping(path = "/get-trader-balance")
    public ResponseEntity<Double> getTraderBalance(@RequestParam String date, @RequestParam String traderCode, @RequestParam String compCode) {
        return ResponseEntity.ok(reportService.getTraderLastBalance(date, traderCode, compCode));
    }
}
