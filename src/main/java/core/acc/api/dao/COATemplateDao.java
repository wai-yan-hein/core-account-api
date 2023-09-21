package core.acc.api.dao;

import core.acc.api.entity.COATemplate;
import core.acc.api.entity.COATemplateKey;

import java.util.List;

public interface COATemplateDao {
    COATemplate save(COATemplate obj);

    List<COATemplate> getCOAChild(Integer busId, String coaCode);
    List<COATemplate> getCOATemplateTree(Integer busId);
    COATemplate findById(COATemplateKey key);

    List<COATemplate> getAllCOATemplate(Integer busId);
}
