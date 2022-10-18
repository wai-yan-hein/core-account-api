package core.acc.api.service;

import core.acc.api.common.Util1;
import core.acc.api.dao.TraderDao;
import core.acc.api.entity.Trader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TraderServiceImpl implements TraderService {
    @Autowired
    private TraderDao traderDao;
    @Autowired
    private SeqTableService seqTableService;

    @Override
    public Trader save(Trader t) {
        if (Util1.isNullOrEmpty(t.getKey().getCode())) {
            t.getKey().setCode(getTraderCode(t.getTraderType(), t.getKey().getCompCode()));
        }
        return traderDao.save(t);
    }

    @Override
    public List<Trader> getTrader(String compCode) {
        return traderDao.getTrader(compCode);
    }

    @Override
    public List<Trader> getCustomer(String compCode) {
        return traderDao.getCustomer(compCode);
    }

    @Override
    public List<Trader> getSupplier(String compCode) {
        return traderDao.getSupplier(compCode);
    }

    private String getTraderCode(String type, String compCode) {
        int seqNo = seqTableService.getSequence(0, type, "-", compCode);
        return type + "-" + String.format("%0" + 5 + "d", seqNo);
    }
}
