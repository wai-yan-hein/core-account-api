package core.acc.api.dao;

import core.acc.api.entity.COATemplate;
import core.acc.api.entity.COATemplateKey;

import java.util.List;

public interface COATemplateDao {
    COATemplate save(COATemplate obj);

    List<COATemplate> getChild(Integer busId, String coaCode);

    COATemplate findById(COATemplateKey key);
}
