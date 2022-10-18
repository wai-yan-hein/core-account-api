package core.acc.api.dao;

import core.acc.api.entity.Currency;

import java.util.List;

public interface CurrencyDao {
    Currency save(Currency currency);

    List<Currency> getCurrency();
    Currency findByCode(String curCode);

}
