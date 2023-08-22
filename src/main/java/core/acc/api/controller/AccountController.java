package core.acc.api.controller;

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
    private ReportService reportService;
    @Autowired
    private StockOPService stockOPService;
    @Autowired
    private ConverterService converterService;
    @Autowired
    private YearEndService yearEndService;
    @Autowired
    private TraderGroupService traderGroupService;

    @Autowired

    @GetMapping(path = "/hello")
    public Mono<?> hello() {
        return Mono.just("Hello");
    }

    @GetMapping(path = "/get-department")
    public Flux<?> getDepartment(@RequestParam String compCode) {
        return Flux.fromIterable(departmentService.findAll(compCode)).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/find-department")
    public Mono<?> findDepartment(@RequestBody DepartmentKey key) {
        return Mono.justOrEmpty(departmentService.findById(key));
    }

    @GetMapping(path = "/get-department-tree")
    public Flux<Department> getDepartmentTree(@RequestParam String compCode) {
        return Flux.fromIterable(departmentService.getDepartmentTree(compCode)).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/save-department")
    public Mono<?> saveDepartment(@RequestBody Department department) {
        return Mono.just(departmentService.save(department));
    }

    @GetMapping(path = "/getUpdatedDepartment")
    public Flux<?> searchByDate(@RequestParam String updatedDate) {
        return Flux.fromIterable(departmentService.getUpdatedDepartment(Util1.toLocalDateTime(updatedDate))).onErrorResume(throwable -> Flux.empty());
    }


    //Chart Of Account
    @GetMapping(path = "/get-coa-tree")
    public Flux<?> getCOATree(@RequestParam String compCode) {
        return Flux.fromIterable(coaService.getCOATree(compCode));
    }

    @GetMapping(path = "/get-coa")
    public Flux<?> getCOAGroup(@RequestParam String compCode) {
        return Flux.fromIterable(coaService.getCOA(compCode)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getCOAByGroup")
    public Flux<?> getCOAByGroup(@RequestParam String groupCode, @RequestParam String compCode) {
        return Flux.fromIterable(coaService.getCOAByGroup(groupCode, compCode)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getCOAByHead")
    public Flux<?> getCOAByHead(@RequestParam String headCode, @RequestParam String compCode) {
        return Flux.fromIterable(coaService.getCOAByHead(headCode, compCode)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/search-coa")
    public Flux<?> searchCOA(@RequestParam String str, @RequestParam Integer level, @RequestParam String compCode) {
        return Flux.fromIterable(coaService.searchCOA(Util1.cleanStr(str), level, compCode)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/get-trader-coa")
    public Flux<?> getTraderCOA(@RequestParam String compCode) {
        return Flux.fromIterable(coaService.getTraderCOA(compCode)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getTraderGroup")
    public Flux<?> getTraderGroup(@RequestParam String compCode) {
        return Flux.fromIterable(traderGroupService.getTraderGroup(compCode)).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/findTraderGroup")
    public Mono<?> findTraderGroup(@RequestBody TraderGroupKey key) {
        return Mono.justOrEmpty(traderGroupService.findById(key));
    }


    @PostMapping(path = "/saveTraderGroup")
    public Mono<?> saveTraderGroup(@RequestBody TraderGroup group) {
        return Mono.justOrEmpty(traderGroupService.save(group));
    }

    @GetMapping(path = "/get-coa-child")
    public Flux<?> getCOAChild(@RequestParam String coaCode, @RequestParam String compCode) {
        return Flux.fromIterable(coaService.getCOAChild(coaCode, compCode)).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/find-coa")
    public Mono<?> findCOA(@RequestBody COAKey key) {
        return Mono.justOrEmpty(coaService.findById(key));
    }

    @PostMapping(path = "/save-coa")
    public Mono<?> saveCOA(@RequestBody ChartOfAccount coa) {
        return Mono.justOrEmpty(coaService.save(coa));
    }

    @GetMapping(path = "/saveCOAFromTemplate")
    public ResponseEntity<?> saveCOAFromTemplate(@RequestParam Integer busId, String compCode) {
        return ResponseEntity.ok(coaService.saveCOA(busId, compCode));
    }

    @GetMapping(path = "/getUpdatedCOA")
    public Flux<?> getCOAByDate(@RequestParam String updatedDate) {
        return Flux.fromIterable(coaService.getUpdatedCOA(Util1.toLocalDateTime(updatedDate))).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/process-coa")
    public Mono<?> processCOA(@RequestBody ChartOfAccount coa) {
        if (coa.getMigCode() != null) {
            coa = coaService.save(coa);
            String code = coa.getMigCode() + "," + coa.getKey().getCoaCode();
            return Mono.just(code);
        }
        return null;
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
        return Flux.fromIterable(list).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/get-coa-opening")
    public Mono<TmpOpening> getCOAOpening(@RequestBody ReportFilter filter) {
        String compCode = Util1.isNull(filter.getCompCode(), "-");
        String opDate = reportService.getOpeningDate(compCode);
        String fromDate = filter.getFromDate();
        String curCode = Util1.isNull(filter.getCurCode(), "-");
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
        return Flux.fromIterable(coaOpeningService.searchOpening(opDate, deptCode, curCode, traderType, coaLv1, coaLv2, coaLv3, projectNo, compCode))
                .onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/save-opening")
    public Mono<?> saveOpening(@RequestBody COAOpening op) {
        return Mono.just(coaOpeningService.save(op));
    }

    @PostMapping(path = "/save-gl")
    public Mono<Gl> saveGl(@RequestBody Gl gl) {
        return Mono.justOrEmpty(glService.save(gl, true));
    }

    @PostMapping(path = "/save-gl-list")
    public Mono<?> saveGl(@RequestBody List<Gl> gl) {
        return Mono.justOrEmpty(glService.save(gl));
    }

    @PostMapping(path = "/delete-coa")
    public Mono<Boolean> deleteCOA(@RequestBody COAKey key) {
        return Mono.just(coaService.delete(key));
    }

    @PostMapping(path = "/delete-gl")
    public Mono<Boolean> deleteGL(@RequestBody DeleteObj obj) {
        GlKey key = new GlKey();
        key.setGlCode(obj.getGlCode());
        key.setCompCode(obj.getCompCode());
        key.setDeptId(obj.getDeptId());
        return Mono.justOrEmpty(glService.delete(key, obj.getModifyBy()));
    }

    @PostMapping(path = "/delete-gl-by-account")
    public Mono<?> deleteGlByAccount(@RequestBody Gl gl) {
        glService.deleteVoucherByAcc(gl.getRefNo(), gl.getTranSource(), gl.getSrcAccCode(), gl.getKey().getCompCode());
        return Mono.justOrEmpty("deleted.");
    }

    @PostMapping(path = "/delete-gl-by-voucher")
    public Mono<?> deleteGlByInvVoucher(@RequestBody Gl gl) {
        glService.deleteInvVoucher(gl.getRefNo(), gl.getTranSource(), gl.getKey().getCompCode());
        return Mono.just("deleted.");
    }

    //Trader
    @PostMapping(path = "/save-trader")
    public Mono<?> getTrader(@RequestBody Trader t) {
        t.setTraderName(Util1.convertToUniCode(t.getTraderName()));
        return Mono.justOrEmpty(traderService.save(t));
    }

    @PostMapping(path = "/delete-trader")
    public Mono<?> deleteTrader(@RequestBody TraderKey key) {
        traderService.delete(key);
        ro.setMessage("Deleted.");
        return Mono.justOrEmpty(ro);
    }

    @GetMapping(path = "/get-trader")
    public Flux<?> getTrader(@RequestParam String compCode) {
        return Flux.fromIterable(traderService.getTrader(compCode)).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/findTrader")
    public Mono<?> findTrader(@RequestBody TraderKey key) {
        return Mono.justOrEmpty(traderService.findById(key));
    }


    @GetMapping(path = "/search-trader")
    public Flux<Trader> getTrader(@RequestParam String text, @RequestParam String compCode) {
        return Flux.fromIterable(traderService.getTrader(Util1.cleanStr(text), compCode)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getUpdatedTrader")
    public Flux<Trader> getTraderByDate(@RequestParam String updatedDate) {
        return Flux.fromIterable(traderService.getTrader(Util1.toLocalDateTime(updatedDate))).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/get-supplier")
    public Flux<?> getSupplier(@RequestParam String compCode) {
        return Flux.fromIterable(traderService.getSupplier(compCode)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/get-customer")
    public Flux<?> getCustomer(@RequestParam String compCode) {
        return Flux.fromIterable(traderService.getCustomer(compCode)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/get-description")
    public Flux<?> getDescription(@RequestParam String str, @RequestParam String compCode) {
        return Flux.fromIterable(glService.getDescription(Util1.cleanStr(str), compCode));
    }

    @GetMapping(path = "/get-batch-no")
    public Flux<?> getBatchNo(@RequestParam String str, @RequestParam String compCode) {
        return Flux.fromIterable(glService.getBatchNo(Util1.cleanStr(str), compCode)).onErrorResume(throwable -> Flux.empty());
    }

    //Ref
    @GetMapping(path = "/get-reference")
    public Flux<?> getRef(@RequestParam String str, @RequestParam String compCode) {
        return Flux.fromIterable(glService.getReference(Util1.cleanStr(str), compCode));
    }

    //TranSource
    @GetMapping(path = "/get-tran-source")
    public Flux<?> getTranSource(@RequestParam String compCode) {
        return Flux.fromIterable(glService.getTranSource(compCode)).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/search-journal")
    public Flux<?> searchJournal(@RequestBody ReportFilter filter) {
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
        return Flux.fromIterable(glService.searchJournal(fromDate, toDate, vouNo, description, reference, coaCode, projectNo, compCode, macId)).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/search-voucher")
    public Flux<?> searchVoucher(@RequestBody ReportFilter filter) {
        Integer macId = filter.getMacId();
        String fromDate = filter.getFromDate();
        String toDate = filter.getToDate();
        String vouNo = Util1.isNull(filter.getGlVouNo(), "-");
        String description = Util1.isAll(filter.getDesp());
        String reference = Util1.isAll(filter.getReference());
        String refNo = Util1.isNull(filter.getRefNo(), "-");
        String compCode = filter.getCompCode();
        reportService.insertTmp(filter.getListDepartment(), macId, compCode);
        return Flux.fromIterable(glService.searchVoucher(fromDate, toDate, vouNo, description, reference, refNo, compCode, macId)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/get-journal")
    public Flux<?> getJournal(@RequestParam String glVouNo, @RequestParam String compCode) {
        return Flux.fromIterable(glService.getJournal(glVouNo, compCode)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/get-voucher")
    public Flux<?> getVoucher(@RequestParam String glVouNo, @RequestParam String compCode) {
        return Flux.fromIterable(glService.getVoucher(glVouNo, compCode)).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/delete-voucher")
    public Mono<?> deleteVoucher(@RequestBody DeleteObj obj) {
        return Mono.justOrEmpty(glService.deleteVoucher(obj.getGlVouNo(), obj.getCompCode(), obj.getModifyBy()));
    }

    @PostMapping(path = "/save-stock-op")
    public Mono<?> saveStockOP(@RequestBody StockOP op) {
        return Mono.justOrEmpty(stockOPService.save(op));
    }

    @PostMapping(path = "/delete-stock-op")
    public Mono<?> deleteStockOP(@RequestBody StockOPKey key) {
        stockOPService.delete(key);
        return Mono.justOrEmpty(true);
    }

    @PostMapping(path = "/delete-op")
    public Mono<?> deleteOP(@RequestBody OpeningKey key) {
        return Mono.just(coaOpeningService.delete(key));
    }

    @PostMapping(path = "/search-stock-op")
    public Flux<?> searchStockOp(@RequestBody ReportFilter filter) {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String compCode = Util1.isNull(filter.getCompCode(), "-");
        String curCode = Util1.isNull(filter.getCurCode(), "-");
        String deptCode = Util1.isNull(filter.getDeptCode(), "-");
        String projectNo = Util1.isAll(filter.getProjectNo());
        return Flux.fromIterable(stockOPService.search(fromDate, toDate, deptCode, curCode, projectNo, compCode)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/convert-to-unicode")
    public Mono<?> convertToUniCode() {
        converterService.convertToUnicode();
        return Mono.justOrEmpty("converted.");
    }
    @GetMapping(path = "/shoot-tri")
    public Mono<?> shootTri() {
        return Mono.justOrEmpty(glService.shootTri());
    }

    @PostMapping(path = "/yearEnd")
    public Mono<?> yearEnd(@RequestBody YearEnd yearEnd) {
        return Mono.justOrEmpty(yearEndService.yearEnd(yearEnd));
    }

    @GetMapping(path = "/getDate")
    public Flux<?> getDate(@RequestParam String startDate, @RequestParam String compCode, @RequestParam boolean isAll) {
        String opDate = reportService.getOpeningDate(compCode);
        opDate = opDate.equals("1998-10-07") ? startDate : opDate;
        return Flux.fromIterable(Util1.generateDate(startDate, opDate, isAll));
    }

    @PostMapping(path = "/getCashBook")
    public Flux<?> getCashBook(@RequestBody ReportFilter filter) {
        String compCode = filter.getCompCode();
        String startDate = filter.getFromDate();
        String endDate = filter.getToDate();
        String cashGroup = filter.getCashGroup();
        String curCode = filter.getCurCode();
        Integer macId = Util1.getInteger(filter.getMacId());
        reportService.insertTmp(filter.getListDepartment(), macId, compCode);
        String opDate = reportService.getOpeningDate(compCode);
        List<Gl> list = new ArrayList<>();
        List<ChartOfAccount> chart = coaService.getCOAByGroup(cashGroup, compCode);
        chart.forEach(coa -> {
            String srcAcc = coa.getKey().getCoaCode();
            Gl gl = new Gl();
            gl.setGlDateStr(Util1.toDateStr(Util1.toDate(startDate), "dd/MM/yyyy"));
            gl.setCurCode(curCode);
            gl.setSrcUserCode(coa.getCoaCodeUsr());
            gl.setSrcAccCode(coa.getKey().getCoaCode());
            gl.setSrcAccName(coa.getCoaNameEng());
            TmpOpening op = coaOpeningService.getCOAOpening(srcAcc, opDate, startDate, curCode, compCode, macId, "-");
            gl.setOpening(op.getOpening());
            Gl obj = reportService.getCashBook(startDate, endDate, srcAcc, curCode, compCode);
            gl.setDrAmt(obj == null ? 0 : obj.getDrAmt());
            gl.setCrAmt(obj == null ? 0 : obj.getCrAmt());
            gl.setClosing(gl.getDrAmt() - gl.getCrAmt() + gl.getOpening());
            list.add(gl);
        });
        return Flux.fromIterable(list).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getIntegrationVoucher")
    private Flux<?> getIntegrationVoucher(@RequestParam String fromDate, @RequestParam String toDate
            , @RequestParam String tranSource, @RequestParam String compCode) {
        return Flux.fromIterable(reportService.getIntegrationVoucher(fromDate, toDate, tranSource, compCode)).onErrorResume(throwable -> Flux.empty());
    }

}
