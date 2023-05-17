package core.acc.api.dao;

import core.acc.api.entity.COAKey;
import core.acc.api.entity.COATemplate;
import core.acc.api.entity.COATemplateKey;
import core.acc.api.entity.ChartOfAccount;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class COATemplateDaoImpl extends AbstractDao<COATemplateKey, COATemplate> implements COATemplateDao {
    @Override
    public COATemplate save(COATemplate obj) {
        saveOrUpdate(obj, obj.getKey());
        return obj;
    }

    @Override
    public List<COATemplate> getChild(Integer busId, String coaCode) {
        String hsql = "select o from COATemplate o where o.key.busId =" + busId + " and o.coaParent = '" + coaCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public COATemplate findById(COATemplateKey key) {
        return getByKey(key);
    }
}
