package core.acc.api.service;

import core.acc.api.entity.COAKey;
import core.acc.api.entity.COATemplate;
import core.acc.api.entity.COATemplateKey;
import core.acc.api.entity.ChartOfAccount;

import java.util.List;

public interface COATemplateService {
    COATemplate save(COATemplate obj);

    List<COATemplate> getChild(Integer busId, String coaCode);
    List<COATemplate> getCOATemplateTree(Integer busId, String coaCode);
    COATemplate findById(COATemplateKey key);
}
