package core.acc.api.dao;

import core.acc.api.entity.Currency;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CurrencyDaoImpl extends AbstractDao<String, Currency> implements CurrencyDao {
    @Override
    public Currency save(Currency currency) {
        persist(currency);
        return currency;
    }

    @Override
    public List<Currency> getCurrency() {
        String hsql = "select o from Currency o";
        return findHSQL(hsql);
    }

    @Override
    public Currency findByCode(String curCode) {
        return getByKey(curCode);
    }
}
