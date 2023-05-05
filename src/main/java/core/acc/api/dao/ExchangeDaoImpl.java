package core.acc.api.dao;

import core.acc.api.common.Util1;
import core.acc.api.entity.CurExchange;
import core.acc.api.entity.ExchangeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        List<Map<String,Object>> result = getList(sql);
        result.forEach((rs)->{
            CurExchange ex = new CurExchange();
            ExchangeKey key = new ExchangeKey();
            key.setCompCode(Util1.getString(rs.get("comp_code")));
            key.setExCode(Util1.getString(rs.get("ex_code")));
            ex.setKey(key);
            ex.setHomeCur(Util1.getString(rs.get("home_cur")));
            ex.setExCur(Util1.getString(rs.get("exchange_cur")));
            ex.setExRate(Util1.getDouble(rs.get("ex_date")));
            ex.setRemark(Util1.getString(rs.get("remark")));
            list.add(ex);
        });
        return null;
    }
}
