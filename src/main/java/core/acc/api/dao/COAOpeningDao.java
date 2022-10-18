package core.acc.api.dao;

import core.acc.api.entity.TmpOpening;

import java.sql.ResultSet;
import java.util.List;

public interface COAOpeningDao {
    ResultSet getResult(String sql);

    void executeSql(String... sql);

    List<TmpOpening> getOpening(String coaCode, Integer macId);
}
