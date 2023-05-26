package core.acc.api.dao;

import core.acc.api.entity.Trader;
import core.acc.api.entity.TraderKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class TraderDaoImpl extends AbstractDao<TraderKey, Trader> implements TraderDao {
    @Override
    public Trader save(Trader t) {
        persist(t);
        return t;
    }

    @Override
    public Trader findById(TraderKey key) {
        return getByKey(key);
    }

    @Override
    public List<Trader> getTrader(String compCode) {
        String hsql = "select o from Trader o where o.active = true and o.deleted =0 and o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public List<Trader> getTrader(String text, String compCode) {
        String filter = "where active =1\n" +
                "and deleted =0\n"+
                "and comp_code ='" + compCode + "'\n" +
                "and (user_code like '" + text + "%' or trader_name like '" + text + "%') \n";
        String sql = "select code trader_code,user_code,trader_name,account_code,discriminator\n" +
                "from trader\n" + filter + "\n" +
                "order by user_code,trader_name\n" +
                "limit 100\n";
        ResultSet rs = getResult(sql);
        List<Trader> list = new ArrayList<>();
        try {
            if (rs != null) {
                while (rs.next()) {
                    Trader t = new Trader();
                    TraderKey key = new TraderKey();
                    key.setCompCode(compCode);
                    key.setCode(rs.getString("trader_code"));
                    t.setKey(key);
                    t.setUserCode(rs.getString("user_code"));
                    t.setTraderName(rs.getString("trader_name"));
                    t.setAccount(rs.getString("account_code"));
                    t.setTraderType(rs.getString("discriminator"));
                    list.add(t);
                }
            }
        } catch (Exception ignored) {
        }
        return list;
    }

    @Override
    public List<Trader> getCustomer(String compCode) {
        String hsql = "select o from Trader o where o.active = true and o.deleted =0 and o.traderType = 'C' and and o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public List<Trader> getSupplier(String compCode) {
        String hsql = "select o from Trader o where o.active = true and o.deleted =0 and o.traderType = 'S' and o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public List<Trader> SearchByDate(String updDate) {
        String hsql = "select o from Trader o where o.active = true and o.deleted =0 and updatedDate>'" + updDate + "'";
        return findHSQL(hsql);
    }

    @Override
    public void delete(TraderKey key) {
        String sql = "update trader set deleted =1 where comp_code ='" + key.getCompCode() + "' and code ='" + key.getCode() + "'";
        execSql(sql);
    }
}
