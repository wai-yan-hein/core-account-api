package core.acc.api.service;

import core.acc.api.common.Util1;
import core.acc.api.dao.COADao;
import core.acc.api.dao.COATemplateDao;
import core.acc.api.entity.COAKey;
import core.acc.api.entity.COATemplate;
import core.acc.api.entity.ChartOfAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@Transactional
public class COAServiceImpl implements COAService {

    @Autowired
    private COADao dao;
    @Autowired
    COATemplateDao coaTemplateDao;
    @Autowired
    private SeqTableService seqService;

    @Override
    public ChartOfAccount save(ChartOfAccount coa) {
        coa.setModifiedDate(LocalDateTime.now());
        if (Util1.isNullOrEmpty(coa.getKey().getCoaCode())) {
            Integer macId = coa.getMacId();
            String compCode = coa.getKey().getCompCode();
            String coaCode = getCOACode(macId, compCode);
            coa.getKey().setCoaCode(coaCode);
            ChartOfAccount valid = findById(coa.getKey());
            if (valid != null) {
                log.info("code " + coa.getKey().getCoaCode());
                log.info("company" + coa.getKey().getCompCode());
                throw new IllegalStateException("Duplicate Account Code");
            }
        }
        return dao.save(coa);
    }

    @Override
    public ChartOfAccount save(ChartOfAccount coa, String opDate) throws Exception {
        return save(coa);
    }

    @Override
    public ChartOfAccount findById(COAKey key) {
        return dao.findById(key);
    }

    @Override
    public List<ChartOfAccount> getCOA(String compCode) {
        return dao.getCOA(compCode);
    }

    @Override
    public List<ChartOfAccount> getCOAByGroup(String groupCode, String compCode) {
        return dao.getCOAByGroup(groupCode, compCode);
    }

    @Override
    public List<ChartOfAccount> getCOAByHead(String headCode, String compCode) {
        return dao.getCOAByHead(headCode, compCode);
    }


    @Override
    public Boolean delete(COAKey key) {
        return dao.delete(key);
    }


    @Override
    public List<ChartOfAccount> searchCOA(String str, Integer level, String compCode) {
        return dao.searchCOA(str, level, compCode);
    }

    private String getCOACode(Integer macId, String compCode) {
        int seqNo = seqService.getSequence(macId, "COA", "-", compCode);
        return String.format("%0" + 3 + "d", macId) + "-" + String.format("%0" + 5 + "d", seqNo);
    }

    @Override
    public List<ChartOfAccount> getCOATree(String compCode) {
        return dao.getCOATree(compCode);
    }

    @Override
    public List<ChartOfAccount> getCOAChild(String parentCode, String compCode) {
        return dao.getCOAChild(parentCode, compCode);
    }

    @Override
    public List<ChartOfAccount> getTraderCOA(String compCode) {
        return dao.getTraderCOA(compCode);
    }

    @Override
    public List<ChartOfAccount> getUpdatedCOA(LocalDateTime updatedDate) {
        return dao.getUpdatedCOA(updatedDate);
    }

    @Override
    public List<ChartOfAccount> unUpload() {
        return dao.unUpload();
    }

    @Override
    public List<ChartOfAccount> findAllActive(String compCode) {
        return dao.findAllActive(compCode);
    }

    @Override
    public Date getMaxDate() {
        return dao.getMaxDate();
    }

    @Override
    public List<ChartOfAccount> saveCOA(Integer busId, String compCode) {
        List<COATemplate> ctList = coaTemplateDao.getAllCOATemplate(busId);
        List<ChartOfAccount> cList = new ArrayList<>();
        ctList.forEach(t -> {
            ChartOfAccount c = new ChartOfAccount();
            COAKey key = new COAKey();
            key.setCoaCode(t.getKey().getCoaCode());
            key.setCompCode(compCode);
            c.setKey(key);
            c.setCoaCodeUsr(t.getCoaCodeUsr());
            c.setCoaNameEng(t.getCoaNameEng());
            c.setCoaNameMya(t.getCoaNameMya());
            c.setActive(t.isActive());
            c.setCreatedBy("1");
            c.setOption("");
            c.setCreatedDate(LocalDateTime.now());
            c.setModifiedDate(LocalDateTime.now());
            c.setCoaParent(t.getCoaParent());
            c.setCoaLevel(t.getCoaLevel());
            c.setCredit(t.isCredit());
            cList.add(save(c));
        });
        return cList;
    }

}
