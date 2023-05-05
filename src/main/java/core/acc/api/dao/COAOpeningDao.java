package core.acc.api.dao;

import core.acc.api.entity.COAOpening;
import core.acc.api.entity.OpeningKey;
import core.acc.api.entity.TmpOpening;

import java.sql.ResultSet;
import java.util.List;

public interface COAOpeningDao {
    boolean delete(OpeningKey key);
    COAOpening save(COAOpening op);
    void executeAndResult(String... sql);


}
