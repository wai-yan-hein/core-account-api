package core.acc.api.dao;

import core.acc.api.entity.COAOpening;
import core.acc.api.entity.OpeningKey;

public interface COAOpeningDao {
    boolean delete(OpeningKey key);
    COAOpening save(COAOpening op);
    void executeAndResult(String... sql);


}
