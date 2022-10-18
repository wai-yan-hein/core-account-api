package core.acc.api.service;

import core.acc.api.entity.Currency;

import java.util.List;

public interface CurrencyService {
    Currency save(Currency currency);

    List<Currency> getCurrency();

    Currency findByCode(String curCode);
}
