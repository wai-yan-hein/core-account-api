package core.acc.api.dao;

import core.acc.api.entity.Trader;
import core.acc.api.entity.TraderKey;
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
        String hsql = "select o from Trader o where o.active = true and o.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public List<Trader> getCustomer(String compCode) {
        String hsql = "select o from Trader o where o.active = true and o.traderType = 'C' and and o.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public List<Trader> getSupplier(String compCode) {
        String hsql = "select o from Trader o where o.active = true and o.traderType = 'S' and o.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public void delete(TraderKey key) {
        String sql = "delete from trader where comp_code ='" + key.getCompCode() + "' and code ='" + key.getCode() + "'";
        execSQL(sql);
    }
}
