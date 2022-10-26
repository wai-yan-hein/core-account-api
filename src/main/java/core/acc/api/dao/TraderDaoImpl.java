package core.acc.api.dao;

import core.acc.api.entity.Trader;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TraderDaoImpl extends AbstractDao<String, Trader> implements TraderDao {
    @Override
    public Trader save(Trader t) {
        persist(t);
        return t;
    }

    @Override
    public List<Trader> getTrader(String compCode) {
        String hsql = "select o from Trader o where o.active = true and o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public List<Trader> getCustomer(String compCode) {
        String hsql = "select o from Trader o where o.active = true and o.traderType = 'C' and and o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public List<Trader> getSupplier(String compCode) {
        String hsql = "select o from Trader o where o.active = true and o.traderType = 'S' and o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }
}
