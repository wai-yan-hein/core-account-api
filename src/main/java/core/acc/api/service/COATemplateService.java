package core.acc.api.service;

import core.acc.api.entity.COATemplate;
import core.acc.api.entity.COATemplateKey;

import java.util.List;

public interface COATemplateService {
    COATemplate save(COATemplate obj);

    List<COATemplate> getCOAChild(Integer busId, String coaCode);
    List<COATemplate> getCOATemplateTree(Integer busId);
    List<COATemplate> getCOATemplate(Integer busId);
    COATemplate findById(COATemplateKey key);
}
