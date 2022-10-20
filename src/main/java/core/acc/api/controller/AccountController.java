package core.acc.api.controller;

import core.acc.api.common.ReturnObject;
import core.acc.api.common.Util1;
import core.acc.api.entity.*;
import core.acc.api.model.ReportFilter;
import core.acc.api.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
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
    private VDespService despService;
    @Autowired
    private VRefService refService;
    @Autowired
    private VTranSourceService tranSourceService;
    @Autowired
    private ReportService reportService;

    @GetMapping(path = "/get-department")
    public ResponseEntity<?> getDepartment(@RequestParam String compCode) {
        return ResponseEntity.ok(departmentService.findAll(compCode));
    }

    @PostMapping(path = "/find-department")
    public ResponseEntity<?> findDepartment(@RequestParam DepartmentKey key) {
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

    @GetMapping(path = "/get-coa-group")
    public ResponseEntity<List<ChartOfAccount>> getCOAGroup(@RequestParam String compCode) {
        List<ChartOfAccount> chart = coaService.getCOA(compCode);
        return ResponseEntity.ok(chart);
    }

    @GetMapping(path = "/get-coa")
    public ResponseEntity<List<VCOALv3>> getCOA(@RequestParam String compCode) {
        List<VCOALv3> chart = coaService.getVCOALv3(compCode);
        return ResponseEntity.ok(chart);
    }

    @PostMapping(path = "/get-coa-child")
    public ResponseEntity<?> getCOAChild(@RequestBody COAKey key) {
        return ResponseEntity.ok(coaService.getCOAChild(key.getCoaCode(), key.getCompCode()));
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
        reportService.insertTmp(filter.getDepartments(), macId, "tmp_dep_filter");
        List<VGl> vGls = reportService.getIndividualLager(fromDate, toDate, desp, srcAcc, acc, curCode, reference, compCode, tranSource, traderCode, traderType, coaLv2, coaLv1, macId);
        String fileName = "Ledger" + macId.toString() + ".json";
        String exportPath = "temp";
        String path = String.format("%s%s%s", exportPath, File.separator, fileName);
        Util1.writeJsonFile(vGls, path);
        ro.setFile(Util1.zipJsonFile(path));
        return ResponseEntity.ok(ro);
    }

    @PostMapping(path = "/get-coa-opening")
    public ResponseEntity<List<TmpOpening>> getCOAOpening(@RequestBody ReportFilter filter) {
        String opDate = filter.getOpeningDate();
        String clDate = filter.getClosingDate();
        String curCode = Util1.isNull(filter.getCurCode(), "-");
        String compCode = Util1.isNull(filter.getCompCode(), "-");
        String traderCode = Util1.isNull(filter.getTraderCode(), "-");
        String coaCode = Util1.isNull(filter.getCoaCode(), "-");
        Integer macId = filter.getMacId();
        List<String> department = filter.getDepartments();
        List<TmpOpening> openings = new ArrayList<>();
        try {
            openings = coaOpeningService.getCOAOpening(coaCode, opDate, clDate, 3, curCode, compCode, department, macId, traderCode);
        } catch (Exception e) {
            log.error(String.format("getCOAOpening: %s", e.getMessage()));
        }
        return ResponseEntity.ok(openings);
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
    public ResponseEntity<Boolean> deleteGL(@RequestBody Gl gl) {
        return ResponseEntity.ok(glService.delete(gl));
    }

    //Trader
    @PostMapping(path = "/save-trader")
    public ResponseEntity<?> getTrader(@RequestBody Trader trader) {
        trader = traderService.save(trader);
        ro.setCompCode(trader.getKey().getCompCode());
        ro.setVouNo(trader.getKey().getCode());
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/get-trader")
    public ResponseEntity<List<Trader>> getTrader(@RequestParam String compCode) {
        return ResponseEntity.ok(traderService.getTrader(compCode));
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
    public ResponseEntity<List<VDescription>> getDesp(@RequestParam String compCode) {
        return ResponseEntity.ok(despService.getDesp(compCode));
    }

    //Ref
    @GetMapping(path = "/get-reference")
    public ResponseEntity<List<VRef>> getRef(@RequestParam String compCode) {
        return ResponseEntity.ok(refService.getRef(compCode));
    }

    //TranSource
    @GetMapping(path = "/get-tran-source")
    public ResponseEntity<List<VTranSource>> getTranSource(@RequestParam String compCode) {
        return ResponseEntity.ok(tranSourceService.getTranSource(compCode));
    }

}
