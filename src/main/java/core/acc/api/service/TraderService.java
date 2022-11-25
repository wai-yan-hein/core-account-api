package core.acc.api.service;

import core.acc.api.entity.Trader;
import core.acc.api.entity.TraderKey;

import java.util.List;

public interface TraderService {
    Trader save(Trader t);

    List<Trader> getTrader(String compCode);
    List<Trader> getTrader(String text,String compCode);

    List<Trader> getCustomer(String compCode);

    List<Trader> getSupplier(String compCode);

    void delete(TraderKey t);

}
