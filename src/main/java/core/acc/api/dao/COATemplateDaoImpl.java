package core.acc.api.dao;

import core.acc.api.entity.COATemplate;
import core.acc.api.entity.COATemplateKey;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class COATemplateDaoImpl extends AbstractDao<COATemplateKey, COATemplate> implements COATemplateDao {
    @Override
    public COATemplate save(COATemplate obj) {
        persist(obj);
        return obj;
    }

    @Override
    public List<COATemplate> getChild(Integer busId, String coaCode) {
        String hsql = "select o from COATemplate o where o.key.busId =" + busId + " and o.coaParent = '" + coaCode + "'";
        return findHSQL(hsql);
    }
}
