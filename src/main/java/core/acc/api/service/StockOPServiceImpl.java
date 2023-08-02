package core.acc.api.service;

import core.acc.api.common.Util1;
import core.acc.api.dao.StockOPDao;
import core.acc.api.entity.StockOP;
import core.acc.api.entity.StockOPKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class StockOPServiceImpl implements StockOPService {
    @Autowired
    private StockOPDao dao;
    @Autowired
    private SeqTableService seqService;

    @Override
    public StockOP save(StockOP op) {
        op.setUpdatedDate(LocalDateTime.now());
        StockOPKey key = op.getKey();
        if (Util1.isNull(key.getTranCode())) {
            key.setTranCode(getTranCode(key.getDeptId(), key.getCompCode()));
        }
        return dao.save(op);
    }

    @Override
    public void delete(StockOPKey key) {
        dao.delete(key);
    }

    @Override
    public List<StockOP> search(String fromDate, String toDate, String deptCode, String curCode,String projectNo, String compCode) {
        return dao.search(fromDate, toDate, deptCode, curCode,projectNo, compCode);
    }

    private String getTranCode(Integer deptId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqService.getSequence(0, "GL", period, compCode);
        String deptCode = String.format("%0" + 2 + "d", deptId) + "-";
        return deptCode + period + String.format("%0" + 4 + "d", seqNo);
    }
}
