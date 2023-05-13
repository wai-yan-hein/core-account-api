package core.acc.api.dao;

import core.acc.api.entity.StockOP;
import core.acc.api.entity.StockOPKey;

import java.util.List;

public interface StockOPDao {
    StockOP save(StockOP op);

    void delete(StockOPKey key);

    List<StockOP> search(String fromDate, String toDate, String deptCode,String curCode,String projectNo, String compCode);
}
