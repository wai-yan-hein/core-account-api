package core.acc.api.dao;

import core.acc.api.entity.COAOpening;
import core.acc.api.entity.TmpOpening;

import java.sql.ResultSet;
import java.util.List;

public interface COAOpeningDao {
    ResultSet getResult(String sql);
    COAOpening save(COAOpening op);
    void executeSql(String... sql);


}
