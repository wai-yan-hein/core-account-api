package core.acc.api.dao;

import core.acc.api.entity.Trader;
import core.acc.api.entity.TraderKey;

import java.time.LocalDateTime;
import java.util.List;

public interface TraderDao {
    Trader save(Trader t);
    Trader findById(TraderKey key);

    List<Trader> getTrader(String compCode);
    List<Trader> getTrader(String text,String compCode);

    List<Trader> getCustomer(String compCode);

    List<Trader> getSupplier(String compCode);
    List<Trader> getTrader(LocalDateTime updDate);
    void delete(TraderKey t);

}
