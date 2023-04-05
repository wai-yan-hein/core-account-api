package core.acc.api.dao;

import core.acc.api.entity.COATemplate;

import java.util.List;

public interface COATemplateDao {
    COATemplate save(COATemplate obj);

    List<COATemplate> getChild(Integer busId, String coaCode);
}
