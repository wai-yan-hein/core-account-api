package core.acc.api.dao;

import core.acc.api.common.Util1;
import core.acc.api.entity.Trader;
import core.acc.api.entity.TraderKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class TraderDaoImpl extends AbstractDao<TraderKey, Trader> implements TraderDao {
    @Override
    public Trader save(Trader t) {
        saveOrUpdate(t, t.getKey());
        return t;
    }

    @Override
    public Trader findById(TraderKey key) {
        return getByKey(key);
    }

    @Override
    public List<Trader> getTrader(String compCode) {
        String hsql = "select o from Trader o where o.active = true and o.deleted = false and o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public List<Trader> getTrader(String text, String compCode) {
        text = Util1.cleanStr(text);
        text = text + "%";
        String filter = """
                where active = true
                and deleted = false
                and comp_code = ?
                and (LOWER(REPLACE(user_code, ' ', '')) LIKE ? OR LOWER(REPLACE(trader_name, ' ', '')) LIKE ?)\s
                """;
        String sql = "SELECT code AS trader_code, user_code, trader_name, account_code, discriminator\n" +
                "FROM trader\n" +
                filter + "\n" +
                "ORDER BY user_code, trader_name\n" +
                "LIMIT 100";
        ResultSet rs = getResult(sql, compCode, text, text);
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
        String hsql = "select o from Trader o where o.active = true and o.deleted = false and o.traderType = 'C' and and o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public List<Trader> getSupplier(String compCode) {
        String hsql = "select o from Trader o where o.active = true and o.deleted = false and o.traderType = 'S' and o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public List<Trader> getTrader(LocalDateTime updatedDate) {
        String hsql = "select o from Trader o where  o.updatedDate>:updatedDate";
        return createQuery(hsql).setParameter("updatedDate", updatedDate).getResultList();
    }

    @Override
    public void delete(TraderKey key) {
        String sql = "update trader set deleted = true where comp_code ='" + key.getCompCode() + "' and code ='" + key.getCode() + "'";
        execSql(sql);
    }
}
