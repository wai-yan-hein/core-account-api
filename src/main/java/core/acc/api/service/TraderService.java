package core.acc.api.service;

import core.acc.api.entity.Trader;
import core.acc.api.entity.TraderKey;

import java.time.LocalDateTime;
import java.util.List;

public interface TraderService {
    Trader save(Trader t);
    Trader findById(TraderKey key);

    List<Trader> getTrader(String compCode);
    List<Trader> getTrader(String text,String compCode);

    List<Trader> getCustomer(String compCode);

    List<Trader> getSupplier(String compCode);

    List<Trader> getTrader(LocalDateTime updatedDate);

    void delete(TraderKey t);

}
