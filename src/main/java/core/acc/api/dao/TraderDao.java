package core.acc.api.dao;

import core.acc.api.entity.Trader;

import java.util.List;

public interface TraderDao {
    Trader save(Trader t);

    List<Trader> getTrader(String compCode);

    List<Trader> getCustomer(String compCode);

    List<Trader> getSupplier(String compCode);
}
