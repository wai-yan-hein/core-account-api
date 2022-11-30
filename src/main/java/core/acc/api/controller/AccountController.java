package core.acc.api.controller;

import core.acc.api.common.Util1;
import core.acc.api.entity.*;
import core.acc.api.model.ReportFilter;
import core.acc.api.model.ReturnObject;
import core.acc.api.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/account")
@Slf4j
public class AccountController {
    private final ReturnObject ro = new ReturnObject();
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private COAService coaService;
    @Autowired
    private GlService glService;
    @Autowired
    private COAOpeningService coaOpeningService;
    @Autowired
    private TraderService traderService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private ReportService reportService;

    @GetMapping(path = "/get-department")
    public ResponseEntity<?> getDepartment(@RequestParam String compCode) {
        return ResponseEntity.ok(departmentService.findAll(compCode));
    }

    @PostMapping(path = "/find-department")
    public ResponseEntity<?> findDepartment(@RequestBody DepartmentKey key) {
        return ResponseEntity.ok(departmentService.findById(key));
    }

    @GetMapping(path = "/get-department-tree")
    public ResponseEntity<List<Department>> getDepartmentTree(@RequestParam String compCode) {
        List<Department> listCat = departmentService.getDepartmentTree(compCode);
        return ResponseEntity.ok(listCat);
    }

    @PostMapping(path = "/save-department")
    public ResponseEntity<ReturnObject> saveDepartment(@RequestBody Department department) {
        Department dep = departmentService.save(department);
        ro.setData(dep);
        ro.setMessage("Save Department");
        return ResponseEntity.ok(ro);
    }

    //Currency
    @GetMapping(path = "/find-currency")
    public ResponseEntity<Currency> findCurrency(@RequestParam String curCode) {
        Currency cur = currencyService.findByCode(curCode);
        return ResponseEntity.ok(cur);
    }

    @PostMapping(path = "/save-currency")
    public ResponseEntity<ReturnObject> saveCurrency(@RequestBody Currency currency) {
        Currency c = currencyService.save(currency);
        ro.setData(c);
        ro.setMessage("Save Currency");
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/get-currency")
    public ResponseEntity<List<Currency>> getCurrency() {
        List<Currency> currency = currencyService.getCurrency();
        return ResponseEntity.ok(currency);
    }

    //Chart Of Account
    @GetMapping(path = "/get-coa-tree")
    public ResponseEntity<List<ChartOfAccount>> getCOATree(@RequestParam String compCode) {
        List<ChartOfAccount> chart = coaService.getCOATree(compCode);
        return ResponseEntity.ok(chart);
    }

    @GetMapping(path = "/get-coa")
    public ResponseEntity<List<ChartOfAccount>> getCOAGroup(@RequestParam String compCode) {
        List<ChartOfAccount> chart = coaService.getCOA(compCode);
        return ResponseEntity.ok(chart);
    }

    @GetMapping(path = "/get-coa-lv3")
    public ResponseEntity<?> getCOALv3(@RequestParam String str, @RequestParam String compCode) {
        return ResponseEntity.ok(coaService.searchCOA3(str, compCode));
    }

    @GetMapping(path = "/get-trader-coa")
    public ResponseEntity<?> getTraderCOA(@RequestParam String compCode) {
        return ResponseEntity.ok(coaService.getTraderCOA(compCode));
    }


    @GetMapping(path = "/get-coa-child")
    public ResponseEntity<?> getCOAChild(@RequestParam String coaCode, @RequestParam String compCode) {
        return ResponseEntity.ok(coaService.getCOAChild(coaCode, compCode));
    }

    @PostMapping(path = "/find-coa")
    public ResponseEntity<?> findCOA(@RequestBody COAKey key) {
        return ResponseEntity.ok(coaService.findById(key));
    }

    @PostMapping(path = "/save-coa")
    public ResponseEntity<ChartOfAccount> saveCOA(@RequestBody ChartOfAccount coa) throws Exception {
        return ResponseEntity.ok(coaService.save(coa));
    }

    @PostMapping(path = "/search-gl")
    public ResponseEntity<ReturnObject> searchGl(@RequestBody ReportFilter filter) throws SQLException, IOException {
        String fromDate = filter.getFromDate();
        String toDate = filter.getToDate();
        String desp = Util1.isNull(filter.getDesp(), "-");
        String srcAcc = Util1.isNull(filter.getSrcAcc(), "-");
        String acc = Util1.isNull(filter.getAcc(), "-");
        String curCode = Util1.isNull(filter.getCurCode(), "-");
        String reference = Util1.isNull(filter.getReference(), "-");
        String compCode = Util1.isNull(filter.getCompCode(), "-");
        String tranSource = Util1.isNull(filter.getTranSource(), "-");
        String traderCode = Util1.isNull(filter.getTraderCode(), "-");
        String traderType = Util1.isNull(filter.getTraderType(), "-");
        String coaLv2 = Util1.isNull(filter.getCoaLv2(), "-");
        String coaLv1 = Util1.isNull(filter.getCoaLv1(), "-");
        Integer macId = filter.getMacId();
        reportService.insertTmp(filter.getListDepartment(), macId, "tmp_dep_filter");
        List<Gl> Gls = reportService.getIndividualLager(fromDate, toDate, desp, srcAcc, acc, curCode, reference, compCode, tranSource, traderCode, traderType, coaLv2, coaLv1, macId);
        String fileName = "Ledger" + macId.toString() + ".json";
        String exportPath = "temp";
        String path = String.format("%s%s%s", exportPath, File.separator, fileName);
        Util1.writeJsonFile(Gls, path);
        ro.setFile(Util1.zipJsonFile(path));
        return ResponseEntity.ok(ro);
    }

    @PostMapping(path = "/get-coa-opening")
    public ResponseEntity<List<TmpOpening>> getCOAOpening(@RequestBody ReportFilter filter) throws Exception {
        String opDate = filter.getOpeningDate();
        String clDate = filter.getClosingDate();
        String curCode = Util1.isNull(filter.getCurCode(), "-");
        String compCode = Util1.isNull(filter.getCompCode(), "-");
        String traderCode = Util1.isNull(filter.getTraderCode(), "-");
        String coaCode = Util1.isNull(filter.getCoaCode(), "-");
        Integer macId = filter.getMacId();
        List<String> department = filter.getListDepartment();
        return ResponseEntity.ok(coaOpeningService.getCOAOpening(coaCode, opDate, clDate, 3, curCode, compCode, department, macId, traderCode));
    }

    @PostMapping(path = "/get-opening")
    public ResponseEntity<?> getOpening(@RequestBody ReportFilter filter) {
        String curCode = Util1.isNull(filter.getCurCode(), "-");
        String compCode = Util1.isNull(filter.getCompCode(), "-");
        String deptCode = Util1.isNull(filter.getDeptCode(), "-");
        String coaLv1 = Util1.isNull(filter.getCoaLv1(), "-");
        String coaLv2 = Util1.isNull(filter.getCoaLv2(), "-");
        String coaLv3 = Util1.isNull(filter.getCoaCode(), "-");
        String traderType = Util1.isNull(filter.getTraderType(), "-");
        return ResponseEntity.ok(coaOpeningService.searchOpening(deptCode, curCode, traderType, coaLv1, coaLv2, coaLv3, compCode));
    }

    @PostMapping(path = "/save-opening")
    public ResponseEntity<?> saveOpening(@RequestBody COAOpening op) {
        return ResponseEntity.ok(coaOpeningService.save(op));
    }

    @PostMapping(path = "/save-gl")
    public ResponseEntity<Gl> saveGl(@RequestBody Gl gl) throws Exception {
        return ResponseEntity.ok(glService.save(gl));
    }

    @PostMapping(path = "/save-gl-list")
    public ResponseEntity<?> saveGl(@RequestBody List<Gl> gl) throws Exception {
        return ResponseEntity.ok(glService.save(gl));
    }

    @PostMapping(path = "/delete-gl")
    public ResponseEntity<Boolean> deleteGL(@RequestBody GlKey key) {
        return ResponseEntity.ok(glService.delete(key));
    }

    //Trader
    @PostMapping(path = "/save-trader")
    public ResponseEntity<?> getTrader(@RequestBody Trader trader) {
        return ResponseEntity.ok(traderService.save(trader));
    }

    @PostMapping(path = "/delete-trader")
    public ResponseEntity<?> deleteTrader(@RequestBody TraderKey key) {
        traderService.delete(key);
        ro.setMessage("Deleted.");
        log.info("deleted trader.");
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/get-trader")
    public ResponseEntity<List<Trader>> getTrader(@RequestParam String compCode) {
        return ResponseEntity.ok(traderService.getTrader(compCode));
    }
    @GetMapping(path = "/search-trader")
    public ResponseEntity<List<Trader>> getTrader(@RequestParam String text,@RequestParam String compCode) {
        return ResponseEntity.ok(traderService.getTrader(text,compCode));
    }

    @GetMapping(path = "/get-supplier")
    public ResponseEntity<List<Trader>> getSupplier(@RequestParam String compCode) {
        return ResponseEntity.ok(traderService.getSupplier(compCode));
    }

    @GetMapping(path = "/get-customer")
    public ResponseEntity<List<Trader>> getCustomer(@RequestParam String compCode) {
        return ResponseEntity.ok(traderService.getCustomer(compCode));
    }

    //Desp
    @GetMapping(path = "/get-description")
    public ResponseEntity<List<VDescription>> getDescription(@RequestParam String str, @RequestParam String compCode) {
        return ResponseEntity.ok(glService.getDescription(str, compCode));
    }

    //Ref
    @GetMapping(path = "/get-reference")
    public ResponseEntity<List<VRef>> getRef(@RequestParam String str, @RequestParam String compCode) {
        return ResponseEntity.ok(glService.getReference(str, compCode));
    }

    //TranSource
    @GetMapping(path = "/get-tran-source")
    public ResponseEntity<List<VTranSource>> getTranSource(@RequestParam String compCode) {
        return ResponseEntity.ok(null);
    }

    @PostMapping(path = "/search-journal")
    public ResponseEntity<?> searchJournal(@RequestBody ReportFilter filter) {
        Integer macId = filter.getMacId();
        String fromDate = filter.getFromDate();
        String toDate = filter.getToDate();
        String vouNo = Util1.isNull(filter.getGlVouNo(), "-");
        String description = Util1.isAll(filter.getDesp());
        String reference = Util1.isAll(filter.getReference());
        String compCode = filter.getCompCode();
        reportService.insertTmp(filter.getListDepartment(), macId, "tmp_dep_filter");
        return ResponseEntity.ok(glService.searchJournal(fromDate, toDate, vouNo, description, reference, compCode, macId));
    }

    @GetMapping(path = "/get-journal")
    public ResponseEntity<?> getJournal(@RequestParam String glVouNo, @RequestParam String compCode) {
        return ResponseEntity.ok(glService.getJournal(glVouNo, compCode));
    }
}
