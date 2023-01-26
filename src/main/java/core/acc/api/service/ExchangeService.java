package core.acc.api.service;

import core.acc.api.entity.CurExchange;
import core.acc.api.entity.ExchangeKey;

import java.util.List;

public interface ExchangeService {
    CurExchange save(CurExchange ex);

    boolean delete(ExchangeKey key);

    List<CurExchange> search(String fromDate, String toDate, String compCode);
}
