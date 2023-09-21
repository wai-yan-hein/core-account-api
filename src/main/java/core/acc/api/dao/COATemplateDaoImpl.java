package core.acc.api.dao;

import core.acc.api.entity.COATemplate;
import core.acc.api.entity.COATemplateKey;
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
    public List<COATemplate> getCOAChild(Integer busId, String coaCode) {
        String hsql = "select o from COATemplate o where o.key.busId =" + busId + " and o.coaParent = '" + coaCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public List<COATemplate> getCOATemplateTree(Integer busId) {
        String hsql = "select o from COATemplate o where  o.coaParent = '#' and o.key.busId = " + busId;
        List<COATemplate> list = findHSQL(hsql);
        for (COATemplate coa : list) {
            getChild(coa);
        }
        return list;
    }

    private void getChild(COATemplate parent) {
        String coaCode = parent.getKey().getCoaCode();
        Integer busId = parent.getKey().getBusId();
        String hsql = "select o from COATemplate o where o.coaParent = '" + coaCode + "' and o.key.busId = " + busId;
        List<COATemplate> chart = findHSQL(hsql);
        parent.setChild(chart);
        if (!chart.isEmpty()) {
            for (COATemplate coa : chart) {
                getChild(coa);
            }
        }
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
