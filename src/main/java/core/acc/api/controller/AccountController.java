package core.acc.api.controller;

import core.acc.api.cloud.CloudMQSender;
import core.acc.api.common.Util1;
import core.acc.api.entity.*;
import core.acc.api.model.DeleteObj;
import core.acc.api.model.ReportFilter;
import core.acc.api.model.ReturnObject;
import core.acc.api.model.YearEnd;
import core.acc.api.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    @Autowired
    private StockOPService stockOPService;
    @Autowired
    private ConverterService converterService;
    @Autowired
    private ExchangeService exchangeService;
    @Autowired(required = false)
    private CloudMQSender cloudMQSender;
    @Autowired
    private YearEndService yearEndService;

    @GetMapping(path = "/get-department")
    public Flux<?> getDepartment(@RequestParam String compCode) {
        return Flux.fromIterable(departmentService.findAll(compCode));
    }

    @PostMapping(path = "/find-department")
    public Mono<?> findDepartment(@RequestBody DepartmentKey key) {
        return Mono.justOrEmpty(departmentService.findById(key));
    }

    @GetMapping(path = "/get-department-tree")
    public Flux<Department> getDepartmentTree(@RequestParam String compCode) {
        return Flux.fromIterable(departmentService.getDepartmentTree(compCode));
    }

    @PostMapping(path = "/save-department")
    public Mono<?> saveDepartment(@RequestBody Department department) {
        return Mono.just(departmentService.save(department));
    }

    //Currency
    @GetMapping(path = "/find-currency")
    public Mono<Currency> findCurrency(@RequestParam String curCode) {
        return Mono.just(currencyService.findByCode(curCode));
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

    @GetMapping(path = "/get-coa3")
    public ResponseEntity<List<ChartOfAccount>> getCOA3(@RequestParam String headCode, @RequestParam String compCode) {
        List<ChartOfAccount> chart = coaService.getCOA(headCode, compCode);
        return ResponseEntity.ok(chart);
    }

    @GetMapping(path = "/search-coa")
    public Flux<?> searchCOA(@RequestParam String str, @RequestParam Integer level, @RequestParam String compCode) {
        return Flux.fromIterable(coaService.searchCOA(Util1.cleanStr(str), level, compCode));
    }

    @GetMapping(path = "/get-trader-coa")
    public ResponseEntity<?> getTraderCOA(@RequestParam String compCode) {
        return ResponseEntity.ok(coaService.getTraderCOA(compCode));
    }

    @GetMapping(path = "/get-coa-child")
    public Flux<?> getCOAChild(@RequestParam String coaCode, @RequestParam String compCode) {
        return Flux.fromIterable(coaService.getCOAChild(coaCode, compCode));
    }

    @PostMapping(path = "/find-coa")
    public ResponseEntity<?> findCOA(@RequestBody COAKey key) {
        return ResponseEntity.ok(coaService.findById(key));
    }

    @PostMapping(path = "/save-coa")
    public ResponseEntity<?> saveCOA(@RequestBody ChartOfAccount coa) {
        return ResponseEntity.ok(coaService.save(coa));
    }

    @PostMapping(path = "/process-coa")
    public ResponseEntity<?> processCOA(@RequestBody ChartOfAccount coa) {
        if (coa.getMigCode() != null) {
            coa = coaService.save(coa);
            String code = String.format("%s,%s", coa.getMigCode(), coa.getKey().getCoaCode());
            return ResponseEntity.ok(code);
        }
        return ResponseEntity.ok(null);
    }

    @PostMapping(path = "/search-gl")
    public Flux<?> searchGl(@RequestBody ReportFilter filter) throws SQLException {
        String fromDate = filter.getFromDate();
        String toDate = filter.getToDate();
        String des = Util1.isNull(filter.getDesp(), "-");
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
        String batchNo = Util1.isNull(filter.getBatchNo(), "-");
        String projectNo = Util1.isAll("-");
        Integer macId = filter.getMacId();
        boolean summary = filter.isSummary();
        reportService.insertTmp(filter.getListDepartment(), macId, compCode);
        List<Gl> list = reportService.getIndividualLedger(fromDate, toDate, des, srcAcc, acc, curCode, reference, compCode, tranSource, traderCode, traderType, coaLv2, coaLv1, batchNo, projectNo, summary, macId);
        return Flux.fromIterable(list);
    }

    @PostMapping(path = "/get-coa-opening")
    public Mono<TmpOpening> getCOAOpening(@RequestBody ReportFilter filter) throws Exception {
        String opDate = filter.getOpeningDate();
        String fromDate = filter.getFromDate();
        String curCode = Util1.isNull(filter.getCurCode(), "-");
        String compCode = Util1.isNull(filter.getCompCode(), "-");
        String traderCode = Util1.isNull(filter.getTraderCode(), "-");
        String coaCode = Util1.isNull(filter.getCoaCode(), "-");
        Integer macId = filter.getMacId();
        reportService.insertTmp(filter.getListDepartment(), macId, compCode);
        return Mono.justOrEmpty(coaOpeningService.getCOAOpening(coaCode, opDate, fromDate, curCode, compCode, macId, traderCode));
    }

    @PostMapping(path = "/get-opening")
    public Flux<?> getOpening(@RequestBody ReportFilter filter) {
        String curCode = Util1.isNull(filter.getCurCode(), "-");
        String compCode = Util1.isNull(filter.getCompCode(), "-");
        String deptCode = Util1.isNull(filter.getDeptCode(), "-");
        String coaLv1 = Util1.isNull(filter.getCoaLv1(), "-");
        String coaLv2 = Util1.isNull(filter.getCoaLv2(), "-");
        String coaLv3 = Util1.isNull(filter.getCoaCode(), "-");
        String traderType = Util1.isNull(filter.getTraderType(), "-");
        String opDate = Util1.isNull(filter.getOpeningDate(), "-");
        String projectNo = Util1.isAll(filter.getProjectNo());
        return Flux.fromIterable(coaOpeningService.searchOpening(opDate, deptCode, curCode, traderType, coaLv1, coaLv2, coaLv3, projectNo, compCode));
    }

    @PostMapping(path = "/save-opening")
    public Mono<?> saveOpening(@RequestBody COAOpening op) {
        return Mono.just(coaOpeningService.save(op));
    }

    @PostMapping(path = "/save-gl")
    public ResponseEntity<Gl> saveGl(@RequestBody Gl gl) throws Exception {
        gl = glService.save(gl, false);
        return ResponseEntity.ok(gl);
    }

    @PostMapping(path = "/save-gl-list")
    public ResponseEntity<?> saveGl(@RequestBody List<Gl> gl) throws Exception {
        ReturnObject ro = glService.save(gl);
        return ResponseEntity.ok(ro);
    }

    @PostMapping(path = "/delete-coa")
    public Mono<Boolean> deleteCOA(@RequestBody COAKey key) {
        return Mono.just(coaService.delete(key));
    }

    @PostMapping(path = "/delete-gl")
    public ResponseEntity<Boolean> deleteGL(@RequestBody DeleteObj obj) {
        GlKey key = new GlKey();
        key.setGlCode(obj.getGlCode());
        key.setCompCode(obj.getCompCode());
        key.setDeptId(obj.getDeptId());
        return ResponseEntity.ok(glService.delete(key, obj.getModifyBy()));
    }

    @PostMapping(path = "/delete-gl-by-account")
    public ResponseEntity<?> deleteGlByAccount(@RequestBody Gl gl) {
        glService.deleteVoucherByAcc(gl.getRefNo(), gl.getTranSource(), gl.getSrcAccCode(), gl.getKey().getCompCode());
        return ResponseEntity.ok("deleted.");
    }

    @PostMapping(path = "/delete-gl-by-voucher")
    public Mono<?> deleteGlByInvVoucher(@RequestBody Gl gl) {
        glService.deleteInvVoucher(gl.getRefNo(), gl.getTranSource(), gl.getKey().getCompCode());
        return Mono.just("deleted.");
    }

    //Trader
    @PostMapping(path = "/save-trader")
    public ResponseEntity<?> getTrader(@RequestBody Trader t) {
        t.setTraderName(Util1.convertToUniCode(t.getTraderName()));
        return ResponseEntity.ok(traderService.save(t));
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
    public Flux<Trader> getTrader(@RequestParam String text, @RequestParam String compCode) {
        return Flux.fromIterable(traderService.getTrader(Util1.cleanStr(text), compCode));
    }

    @GetMapping(path = "/get-supplier")
    public ResponseEntity<List<Trader>> getSupplier(@RequestParam String compCode) {
        return ResponseEntity.ok(traderService.getSupplier(compCode));
    }

    @GetMapping(path = "/get-customer")
    public ResponseEntity<List<Trader>> getCustomer(@RequestParam String compCode) {
        return ResponseEntity.ok(traderService.getCustomer(compCode));
    }

    @GetMapping(path = "/get-description")
    public Flux<?> getDescription(@RequestParam String str, @RequestParam String compCode) {
        return Flux.fromIterable(glService.getDescription(Util1.cleanStr(str), compCode));
    }

    @GetMapping(path = "/get-batch-no")
    public ResponseEntity<?> getBatchNo(@RequestParam String str, @RequestParam String compCode) {
        return ResponseEntity.ok(glService.getBatchNo(Util1.cleanStr(str), compCode));
    }

    //Ref
    @GetMapping(path = "/get-reference")
    public Flux<?> getRef(@RequestParam String str, @RequestParam String compCode) {
        return Flux.fromIterable(glService.getReference(Util1.cleanStr(str), compCode));
    }

    //TranSource
    @GetMapping(path = "/get-tran-source")
    public ResponseEntity<?> getTranSource(@RequestParam String compCode) {
        return ResponseEntity.ok(glService.getTranSource(compCode));
    }

    @PostMapping(path = "/search-journal")
    public ResponseEntity<?> searchJournal(@RequestBody ReportFilter filter) {
        Integer macId = filter.getMacId();
        String fromDate = filter.getFromDate();
        String toDate = filter.getToDate();
        String vouNo = Util1.isNull(filter.getGlVouNo(), "-");
        String description = Util1.isAll(filter.getDesp());
        String reference = Util1.isAll(filter.getReference());
        String projectNo = Util1.isAll(filter.getProjectNo());
        String coaCode = Util1.isAll(filter.getCoaCode());
        String compCode = filter.getCompCode();
        reportService.insertTmp(filter.getListDepartment(), macId, compCode);
        return ResponseEntity.ok(glService.searchJournal(fromDate, toDate, vouNo, description, reference, coaCode, projectNo, compCode, macId));
    }

    @PostMapping(path = "/search-voucher")
    public ResponseEntity<?> searchVoucher(@RequestBody ReportFilter filter) {
        Integer macId = filter.getMacId();
        String fromDate = filter.getFromDate();
        String toDate = filter.getToDate();
        String vouNo = Util1.isNull(filter.getGlVouNo(), "-");
        String description = Util1.isAll(filter.getDesp());
        String reference = Util1.isAll(filter.getReference());
        String refNo = Util1.isNull(filter.getRefNo(), "-");
        String compCode = filter.getCompCode();
        reportService.insertTmp(filter.getListDepartment(), macId, compCode);
        return ResponseEntity.ok(glService.searchVoucher(fromDate, toDate, vouNo, description, reference, refNo, compCode, macId));
    }

    @GetMapping(path = "/get-journal")
    public ResponseEntity<?> getJournal(@RequestParam String glVouNo, @RequestParam String compCode) {
        return ResponseEntity.ok(glService.getJournal(glVouNo, compCode));
    }

    @GetMapping(path = "/get-voucher")
    public ResponseEntity<?> getVoucher(@RequestParam String glVouNo, @RequestParam String compCode) {
        return ResponseEntity.ok(glService.getVoucher(glVouNo, compCode));
    }

    @PostMapping(path = "/delete-voucher")
    public ResponseEntity<?> deleteVoucher(@RequestBody DeleteObj obj) {
        return ResponseEntity.ok(glService.deleteVoucher(obj.getGlVouNo(), obj.getCompCode(), obj.getModifyBy()));
    }

    @PostMapping(path = "/save-stock-op")
    public ResponseEntity<?> saveStockOP(@RequestBody StockOP op) {
        return ResponseEntity.ok(stockOPService.save(op));
    }

    @PostMapping(path = "/delete-stock-op")
    public ResponseEntity<?> deleteStockOP(@RequestBody StockOPKey key) {
        stockOPService.delete(key);
        return ResponseEntity.ok(true);
    }

    @PostMapping(path = "/delete-op")
    public Mono<?> deleteOP(@RequestBody OpeningKey key) {
        return Mono.just(coaOpeningService.delete(key));
    }

    @PostMapping(path = "/search-stock-op")
    public ResponseEntity<?> searchStockOp(@RequestBody ReportFilter filter) {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String compCode = Util1.isNull(filter.getCompCode(), "-");
        String curCode = Util1.isNull(filter.getCurCode(), "-");
        String deptCode = Util1.isNull(filter.getDeptCode(), "-");
        String projectNo = Util1.isAll(filter.getProjectNo());
        return ResponseEntity.ok(stockOPService.search(fromDate, toDate, deptCode, curCode, projectNo, compCode));
    }

    @PostMapping(path = "/search-exchange")
    public Flux<?> searchExchange(@RequestBody ReportFilter filter) {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String compCode = Util1.isNull(filter.getCompCode(), "-");
        return Flux.fromIterable(exchangeService.search(fromDate, toDate, compCode));
    }

    @GetMapping(path = "/convert-to-unicode")
    public ResponseEntity<?> convertToUniCode() {
        converterService.convertToUnicode();
        return ResponseEntity.ok("converted.");
    }

    @GetMapping(path = "/shoot-tri")
    public ResponseEntity<?> shootTri() {
        return ResponseEntity.ok(glService.shootTri());
    }

    @PostMapping(path = "/yearEnd")
    public Mono<?> yearEnd(@RequestBody YearEnd yearEnd) {
        return Mono.justOrEmpty(yearEndService.yearEnd(yearEnd));
    }

    @GetMapping(path = "/getDate")
    public Flux<?> getDate(@RequestParam String startDate, @RequestParam String compCode) {
        String opDate = reportService.getOpeningDate(compCode);
        return Flux.fromIterable(Util1.generateDate(startDate, opDate));
    }

    @PostMapping(path = "/getCashBook")
    public Flux<?> getCashBook(@RequestBody ReportFilter filter) {
        String compCode = filter.getCompCode();
        String startDate = filter.getFromDate();
        String endDate = filter.getToDate();
        String cashGroup = filter.getCashGroup();
        String curCode = filter.getCurCode();
        Integer macId = Util1.getInteger(filter.getMacId());
        reportService.insertTmp(filter.getListDepartment(),macId,compCode);
        String opDate = reportService.getOpeningDate(compCode);
        List<Gl> list = reportService.getCashBook(startDate, endDate, cashGroup, curCode, compCode);
        list.forEach(gl -> {
            TmpOpening op = coaOpeningService.getCOAOpening(gl.getSrcAccCode(), opDate, startDate, curCode, compCode, macId, "-");
            gl.setOpening(op.getOpening());
            gl.setClosing(gl.getDrAmt() - gl.getCrAmt() + gl.getOpening());
        });
        return Flux.fromIterable(list);
    }

}
