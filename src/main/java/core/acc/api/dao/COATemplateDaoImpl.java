package core.acc.api.dao;

import core.acc.api.entity.COAKey;
import core.acc.api.entity.COATemplate;
import core.acc.api.entity.COATemplateKey;
import core.acc.api.entity.ChartOfAccount;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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
    public List<COATemplate> getCOATemplateTree(Integer busId, String compCode) {
        List<COATemplate> list = getChild(busId, compCode);
        List<COATemplate> result = new ArrayList<>(list);
        for(COATemplate t: list) {
            result.addAll(getChild(busId, t.getKey().getCoaCode()));
        }
        return result;
    }

    @Override
    public List<COATemplate> getAllCOATemplate(Integer busId) {
        String hsql = "select o from COATemplate o where o.key.busId =" + busId;
        return findHSQL(hsql);
    }

    @Override
    public COATemplate findById(COATemplateKey key) {
        return getByKey(key);
    }
}
