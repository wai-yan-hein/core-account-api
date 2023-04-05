package core.acc.api.service;

import core.acc.api.entity.COATemplate;

import java.util.List;

public interface COATemplateService {
    COATemplate save(COATemplate obj);

    List<COATemplate> getChild(Integer busId, String coaCode);
}
