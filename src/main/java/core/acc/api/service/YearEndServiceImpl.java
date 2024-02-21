package core.acc.api.service;

import core.acc.api.entity.*;
import core.acc.api.model.YearEnd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
public class YearEndServiceImpl implements YearEndService {
    @Autowired
    private COAService coaService;
    @Autowired
    private TraderService traderService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private SeqTableService seqTableService;

    @Override
    public YearEnd yearEnd(YearEnd end) {
        copyDepartment(end);
        copyCOA(end);
        copyTrader(end);
        copyOpening();
        copySeq(end);
        end.setMessage("year end in account.");
        return end;
    }

    private void copyOpening() {
        log.info("copied opening.");
    }

    private void copyCOA(YearEnd end) {
        String compCode = end.getCompCode();
        String yeCompCode = end.getYeCompCode();
        Integer macId = end.getMacId();
        coaService.findAllActive(yeCompCode)
                .forEach(yn -> {
                    ChartOfAccount coa = new ChartOfAccount();
                    COAKey key = new COAKey();
                    key.setCompCode(compCode);
                    key.setCoaCode(yn.getKey().getCoaCode());
                    coa.setKey(key);
                    coa.setCoaLevel(yn.getCoaLevel());
                    coa.setCoaNameEng(yn.getCoaNameEng());
                    coa.setCoaCodeUsr(yn.getCoaCodeUsr());
                    coa.setGroupName(yn.getGroupName());
                    coa.setActive(yn.isActive());
                    coa.setCoaNameMya(yn.getCoaNameMya());
                    coa.setCoaParent(yn.getCoaParent());
                    coa.setCreatedBy(end.getCreateBy());
                    coa.setCreatedDate(LocalDateTime.now());
                    coa.setDeleted(yn.isDeleted());
                    coa.setCurCode(yn.getCurCode());
                    coa.setDeptCode(yn.getDeptCode());
                    coa.setCoaOption(yn.getCoaOption());
                    coa.setCredit(yn.isCredit());
                    coa.setMacId(macId);
                    coaService.save(coa);
                });
        log.info("copied coa.");

    }

    private void copyTrader(YearEnd end) {
        String compCode = end.getCompCode();
        String yeCompCode = end.getYeCompCode();
        Integer macId = end.getMacId();
        traderService.getTrader(yeCompCode).forEach(yn -> {
            Trader t = new Trader();
            TraderKey key = new TraderKey();
            key.setCompCode(compCode);
            key.setCode(yn.getKey().getCode());
            t.setKey(key);
            t.setAccount(yn.getAccount());
            t.setTraderType(yn.getTraderType());
            t.setTraderName(yn.getTraderName());
            t.setActive(yn.isActive());
            t.setUserCode(yn.getUserCode());
            t.setDeleted(yn.isDeleted());
            t.setCreatedBy(end.getCreateBy());
            t.setAddress(yn.getAddress());
            t.setEmail(yn.getEmail());
            t.setCreatedDate(LocalDateTime.now());
            t.setMacId(macId);
            t.setPhone(yn.getPhone());
            t.setRegCode(yn.getRegCode());
            t.setRemark(yn.getRemark());
            traderService.save(t);
        });
        log.info("copied trader.");
    }

    private void copyDepartment(YearEnd end) {
        String compCode = end.getCompCode();
        String yeCompCode = end.getYeCompCode();
        Integer macId = end.getMacId();
        departmentService.findAllActive(yeCompCode).forEach(dep -> {
            Department d = new Department();
            DepartmentKey key = new DepartmentKey();
            key.setCompCode(compCode);
            key.setDeptCode(dep.getKey().getDeptCode());
            d.setKey(key);
            d.setUserCode(dep.getUserCode());
            d.setDeptName(dep.getDeptName());
            d.setActive(dep.isActive());
            d.setDeleted(dep.isDeleted());
            d.setCreatedDt(LocalDateTime.now());
            d.setCreatedBy(end.getCreateBy());
            d.setMacId(macId);
            departmentService.save(d);
        });
        log.info("copied department.");
    }

    private void copySeq(YearEnd end) {
        String compCode = end.getCompCode();
        String yeCompCode = end.getYeCompCode();
        seqTableService.findAll(yeCompCode).forEach(seq -> {
            SeqTable s = new SeqTable();
            SeqKey key = new SeqKey();
            key.setCompCode(compCode);
            key.setMacId(seq.getKey().getMacId());
            key.setPeriod(seq.getKey().getPeriod());
            key.setSeqOption(seq.getKey().getSeqOption());
            s.setKey(key);
            s.setSeqNo(seq.getSeqNo());
            seqTableService.save(s);
        });
        log.info("copied sequence.");
    }
}
