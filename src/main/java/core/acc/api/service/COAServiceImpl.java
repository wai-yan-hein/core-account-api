package core.acc.api.service;

import core.acc.api.common.Util1;
import core.acc.api.dao.COADao;
import core.acc.api.entity.COAKey;
import core.acc.api.entity.ChartOfAccount;
import core.acc.api.entity.VCOALv3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class COAServiceImpl implements COAService {

    @Autowired
    private COADao dao;
    @Autowired
    private SeqTableService seqService;

    @Override
    public ChartOfAccount save(ChartOfAccount coa) throws Exception {
        if (Util1.isNullOrEmpty(coa.getKey().getCoaCode())) {
            Integer macId = coa.getMacId();
            String compCode = coa.getKey().getCompCode();
            String coaCode = getCOACode(macId, compCode);
            coa.getKey().setCoaCode(coaCode);
            ChartOfAccount valid = findById(coa.getKey());
            if (valid == null) {
                coa.getKey().setCoaCode(coaCode);
            } else {
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
    public int delete(String code, String compCode) {
        return dao.delete(code, compCode);
    }

    private String getCOACode(Integer macId, String compCode) {
        int seqNo = seqService.getSequence(macId, "COA", "-", compCode);
        return String.format("%0" + 3 + "d", macId) + "-" + String.format("%0" + 5 + "d", seqNo);
    }

    @Override
    public List<VCOALv3> getVCOALv3(String compCode) {
        return dao.getVCOALv3(compCode);
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
    public List<VCOALv3> getVCOACurrency(String compCode) {
        return dao.getVCOACurrency(compCode);
    }

}
