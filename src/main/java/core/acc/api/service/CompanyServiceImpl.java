package core.acc.api.service;

import core.acc.api.dao.COADao;
import core.acc.api.dao.TraderDao;
import core.acc.api.entity.COAKey;
import core.acc.api.entity.ChartOfAccount;
import core.acc.api.entity.TraderKey;
import core.acc.api.model.CompanyInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class CompanyServiceImpl implements CompanyService {
    @Autowired
    private COADao coaDao;
    @Autowired
    private TraderDao traderDao;

    @Override
    public boolean createCompany(CompanyInfo info) {
        String exCode = info.getExampleCompany();
        String compCode = info.getCompCode();
        coaDao.getCOA(exCode).forEach(coa -> {
            COAKey key = new COAKey();
            key.setCompCode(compCode);
            key.setCoaCode(coa.getKey().getCoaCode());
            coa.setKey(key);
            coaDao.save(coa);
        });
        log.info("coa.");
        traderDao.getTrader(compCode).forEach(trader -> {
            TraderKey key = new TraderKey();
            key.setCompCode(compCode);
            key.setCode(trader.getKey().getCode());
            traderDao.save(trader);
        });
        log.info("trader");
        return false;
    }
}
