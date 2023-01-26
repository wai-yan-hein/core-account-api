package core.acc.api.service;

import core.acc.api.dao.ExchangeDao;
import core.acc.api.entity.CurExchange;
import core.acc.api.entity.ExchangeKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ExchangeServiceImpl implements ExchangeService {
    @Autowired
    private ExchangeDao dao;

    @Override
    public CurExchange save(CurExchange ex) {
        return dao.save(ex);
    }

    @Override
    public boolean delete(ExchangeKey key) {
        return dao.delete(key);
    }

    @Override
    public List<CurExchange> search(String fromDate, String toDate, String compCode) {
        return dao.search(fromDate, toDate, compCode);
    }
}
