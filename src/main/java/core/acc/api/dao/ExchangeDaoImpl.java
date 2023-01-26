package core.acc.api.dao;

import core.acc.api.entity.CurExchange;
import core.acc.api.entity.ExchangeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class ExchangeDaoImpl extends AbstractDao<ExchangeKey, CurExchange> implements ExchangeDao {
    @Override
    public CurExchange save(CurExchange ex) {
        persist(ex);
        return ex;
    }

    @Override
    public boolean delete(ExchangeKey key) {
        String sql = "update cur_exchange set deleted =1 where ex_code ='" + key.getExCode() + "' and comp_code ='" + key.getCompCode() + "'";
        execSql(sql);
        return true;
    }

    @Override
    public List<CurExchange> search(String fromDate, String toDate, String compCode) {
        List<CurExchange> list = new ArrayList<>();
        String sql = "select *\n" +
                "from cur_exchange\n" +
                "where deleted = 0\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and date(ex_date) between '" + fromDate + "' and '" + toDate + "'";
        ResultSet rs = getResultSet(sql);
        if (rs != null) {
            try {
                while (rs.next()) {
                    CurExchange ex = new CurExchange();
                    ExchangeKey key = new ExchangeKey();
                    key.setCompCode(rs.getString("comp_code"));
                    key.setExCode(rs.getString("ex_code"));
                    ex.setKey(key);
                    ex.setHomeCur(rs.getString("home_cur"));
                    ex.setExCur(rs.getString("exchange_cur"));
                    ex.setExRate(rs.getDouble("ex_date"));
                    ex.setRemark(rs.getString("remark"));
                    list.add(ex);
                }
            } catch (Exception e) {
                log.error("search : " + e.getMessage());
            }
        }
        return null;
    }
}
