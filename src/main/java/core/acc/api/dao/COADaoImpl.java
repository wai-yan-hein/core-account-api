package core.acc.api.dao;

import core.acc.api.entity.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class COADaoImpl extends AbstractDao<String, ChartOfAccount> implements COADao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public ChartOfAccount save(ChartOfAccount coa) {
        persist(coa);
        return coa;
    }

    @Override
    public ChartOfAccount findById(String id) {
        return getByKey(id);
    }

    @Override
    public List<ChartOfAccount> getCOA(String compCode) {
        String hsql = "select o from ChartOfAccount o where o.active = true and  o.compCode = '" + compCode + "' order by o.coaLevel,o.coaCodeUsr";
        return findHSQL(hsql);
    }

    @Override
    public int delete(String code, String compCode) {
        int status = 10;
        //check gl
        String delSql = "delete from ChartOfAccount o where o.code = '"
                + code + "' and o.compCode = '" + compCode + "'";
        String vSql = "select distinct o.sourceAcId,o.accCode from Gl o where (o.sourceAcId ='" + code + "' or o.accCode = '" + code + "')"
                + " and o.compCode = '" + compCode + "'";
        //check opening
        String opSql = "select distinct o.sourceAccId from COAOpening o where o.sourceAccId = '" + code + "' and o.compCode = '" + compCode + "'\n"
                + "and (o.drAmt<>0 or o.crAmt<>0)";
        //check trader
        String tSql = "select distinct o.account.code from Trader o where o.account.code = '" + code + "' and o.compCode = '" + compCode + "'";
        List<COAOpening> listOP = getSession().createQuery(opSql, COAOpening.class).list();
        List<Gl> listGl = getSession().createQuery(vSql, Gl.class).list();
        List<Trader> listTrader = getSession().createQuery(tSql, Trader.class).list();
        if (listOP.isEmpty() && listGl.isEmpty() && listTrader.isEmpty()) {
            status = execUpdateOrDelete(delSql);
        }
        return status;
    }

    @Override
    public List<ChartOfAccount> getUnusedCOA(String compCode) {
        List<ChartOfAccount> unused = new ArrayList<>();
        List<VCOALv3> listCOA = getVCOALv3(compCode);
        if (!listCOA.isEmpty()) {
            listCOA.forEach(coa -> {
                String code = coa.getCoaCode();
                String hsql = "select distinct o.sourceAcId,o.accCode from Gl o "
                        + "where (o.sourceAcId = '" + code + "' or o.accCode = '" + code + "') "
                        + " and o.compCode = '" + compCode + "'";
                List<Gl> list = getSession().createQuery(hsql, Gl.class).list();
                if (list.isEmpty()) {
                    //unused
                    String hsql1 = "select distinct o.sourceAccId from COAOpening o "
                            + " where o.sourceAccId = '" + code + "' and o.compCode = '" + compCode + "'\n"
                            + "and (o.drAmt<>0 or o.crAmt <> 0)";
                    List<COAOpening> list1 = getSession().createQuery(hsql1, COAOpening.class).list();
                    if (list1.isEmpty()) {
                        unused.add(new ChartOfAccount(code, coa.getCoaNameEng()));
                    }
                }
            });
        }
        return unused;
    }

    @Override
    public List<ChartOfAccount> getCOAChild(String parentCode, String compCode) {
        String hsql = "select o from ChartOfAccount o where o.coaParent = '" + parentCode + "' and o.compCode = '" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public List<VCOALv3> getVCOALv3(String compCode) {
        String hsql = "select o from VCOALv3 o where o.compCode = '" + compCode + "'";
        return getSession().createQuery(hsql, VCOALv3.class).list();
    }

    @Override
    public List<VCOALv3> getVCOACurrency(String compCode) {
        String hsql = "select o from VCOALv3 o where o.curCode is not null";
        return getSession().createQuery(hsql, VCOALv3.class).list();
    }

    @Override
    public VCOALv3 findByCode(String code) {
        return getSession().get(VCOALv3.class, code);
    }

    @Override
    public List<ChartOfAccount> getCOATree(String compCode) {
        String hsql = "select o from ChartOfAccount o where  o.coaParent = '#' and o.compCode = '" + compCode + "'";
        List<ChartOfAccount> chart = findHSQL(hsql);
        for (ChartOfAccount coa : chart) {
            getChild(coa, compCode);
        }
        return chart;
    }

    @Override
    public List<ChartOfAccount> getAllChild(String parent, String compCode) {
        String strSql = "select o from ChartOfAccount o where o.compCode = '"
                + compCode + "' and o.code = '" + parent + "'";
        List<ChartOfAccount> listAllChild = findHSQL(strSql);
        getChild(listAllChild, parent, compCode);
        return listAllChild;
    }

    private void getChild(List<ChartOfAccount> listAllChild, String parent, String compCode) {
        String strSql = "select o from ChartOfAccount o where o.compCode = '"
                + compCode + "' and o.coaParent = '" + parent + "'";
        List<ChartOfAccount> listCOA = findHSQL(strSql);
        if (!listCOA.isEmpty()) {
            listAllChild.addAll(listCOA);
        }
        listCOA.forEach(coa -> getChild(listAllChild, coa.getCoaCode(), compCode));
    }

    private void getChild(ChartOfAccount parent, String compCode) {
        String hsql = "select o from ChartOfAccount o where o.coaParent = '" + parent.getCoaCode()
                + "' and o.compCode = '" + compCode + "'";
        List<ChartOfAccount> chart = findHSQL(hsql);
        parent.setChild(chart);
        if (!chart.isEmpty()) {
            for (ChartOfAccount coa : chart) {
                getChild(coa, compCode);
            }
        }
    }

    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }

}
