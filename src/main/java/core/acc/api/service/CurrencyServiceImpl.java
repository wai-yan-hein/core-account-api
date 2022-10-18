package core.acc.api.service;

import core.acc.api.dao.CurrencyDao;
import core.acc.api.entity.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CurrencyServiceImpl implements CurrencyService {
    @Autowired
    private CurrencyDao currencyDao;

    @Override
    public Currency save(Currency currency) {
        return currencyDao.save(currency);
    }

    @Override
    public List<Currency> getCurrency() {
        return currencyDao.getCurrency();
    }

    @Override
    public Currency findByCode(String curCode) {
        return currencyDao.findByCode(curCode);
    }
}
