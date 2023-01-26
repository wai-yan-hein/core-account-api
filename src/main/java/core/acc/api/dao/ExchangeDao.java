package core.acc.api.dao;

import core.acc.api.entity.CurExchange;
import core.acc.api.entity.ExchangeKey;

import java.util.List;

public interface ExchangeDao {
    CurExchange save(CurExchange ex);

    boolean delete(ExchangeKey key);

    List<CurExchange> search(String fromDate, String toDate,String compCode);
}
