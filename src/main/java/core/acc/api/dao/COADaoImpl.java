package core.acc.api.dao;

import core.acc.api.common.Util1;
import core.acc.api.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
@Slf4j
public class COADaoImpl extends AbstractDao<COAKey, ChartOfAccount> implements COADao {
    @Override
    public ChartOfAccount save(ChartOfAccount coa) {
        persist(coa);
        return coa;
    }

    @Override
    public ChartOfAccount findById(COAKey key) {
        return getByKey(key);
    }

    @Override
    public List<ChartOfAccount> getCOA(String compCode) {
        List<ChartOfAccount> list = new ArrayList<>();
        String sql = "select c1.coa_code,c1.coa_code_usr,c1.coa_name_eng,ifnull(c2.coa_name_eng,'Head') parent_name,c1.coa_level\n" +
                "from chart_of_account c1\n" +
                "left join chart_of_account c2\n" +
                "on c1.coa_parent = c2.coa_code\n" +
                "and c1.comp_code = c2.comp_code\n" +
                "where c1.deleted =0\n" +
                "and c1.active =1\n" +
                "and c1.comp_code ='" + compCode + "'\n" +
                "order by c1.coa_level,c1.coa_code_usr,c1.coa_name_eng";
        try {
            ResultSet rs = getResultSet(sql);
            if (rs != null) {
                while (rs.next()) {
                    ChartOfAccount coa = new ChartOfAccount();
                    COAKey key = new COAKey();
                    key.setCompCode(compCode);
                    key.setCoaCode(rs.getString("coa_code"));
                    coa.setKey(key);
                    coa.setCoaCodeUsr(rs.getString("coa_code_usr"));
                    coa.setCoaNameEng(rs.getString("coa_name_eng"));
                    coa.setGroupName(rs.getString("parent_name"));
                    coa.setCoaLevel(rs.getInt("coa_level"));
                    list.add(coa);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return list;
    }

    @Override
    public int delete(String code, String compCode) {
        int status = 10;
        //check gl
        String delSql = "delete from ChartOfAccount o where o.code = '"
                + code + "' and o.key.compCode = '" + compCode + "'";
        String vSql = "select distinct o.sourceAcId,o.accCode from Gl o where (o.sourceAcId ='" + code + "' or o.accCode = '" + code + "')"
                + " and o.key.compCode = '" + compCode + "'";
        //check opening
        String opSql = "select distinct o.sourceAccId from COAOpening o where o.sourceAccId = '" + code + "' and o.key.compCode = '" + compCode + "'\n"
                + "and (o.drAmt<>0 or o.crAmt<>0)";
        //check trader
        String tSql = "select distinct o.account.code from Trader o where o.account.code = '" + code + "' and o.key.compCode = '" + compCode + "'";
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
        return null;
    }

    @Override
    public List<ChartOfAccount> getCOAChild(String parentCode, String compCode) {
        String hsql = "select o from ChartOfAccount o where o.coaParent = '" + parentCode + "' and o.key.compCode = '" + compCode + "' order by o.coaCodeUsr";
        return findHSQL(hsql);
    }

    @Override
    public List<ChartOfAccount> searchCOA(String str, Integer level, String compCode) {
        List<ChartOfAccount> list = new ArrayList<>();
        String sql = "select a.*,c1.coa_code group_code,c1.coa_code_usr group_usr_code,c1.coa_name_eng group_name,c2.coa_code head_code,c2.coa_code_usr head_usr_code,c2.coa_name_eng head_name\n" +
                "from (\n" +
                "select coa_code,coa_code_usr,coa_name_eng,coa_parent,comp_code\n" +
                "from chart_of_account\n" +
                "where active = 1\n" +
                "and deleted = 0\n" +
                "and coa_level =" + level + "\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and (coa_code_usr like '" + str + "%' or coa_name_eng like '" + str + "%')\n" +
                "limit 20\n" +
                ")a\n" +
                "left join chart_of_account c1\n" +
                "on a.coa_parent = c1.coa_code\n" +
                "and a.comp_code = c1.comp_code\n" +
                "left join chart_of_account c2\n" +
                "on c1.coa_parent = c2.coa_code\n" +
                "and c1.comp_code = c2.comp_code";
        try {
            ResultSet rs = getResultSet(sql);
            if (rs != null) {
                while (rs.next()) {
                    ChartOfAccount coa = new ChartOfAccount();
                    //coa_code, coa_code_usr, coa_name_eng, group_code, group_usr_code, group_name, head_code, head_usr_code, head_name
                    COAKey key = new COAKey();
                    key.setCoaCode(rs.getString("coa_code"));
                    key.setCompCode(compCode);
                    coa.setKey(key);
                    coa.setCoaCodeUsr(rs.getString("coa_code_usr"));
                    coa.setCoaNameEng(rs.getString("coa_name_eng"));
                    coa.setGroupCode(rs.getString("group_code"));
                    coa.setGroupUsrCode(rs.getString("group_usr_code"));
                    coa.setGroupName(rs.getString("group_name"));
                    coa.setHeadCode(rs.getString("head_code"));
                    coa.setHeadUsrCode(rs.getString("head_usr_code"));
                    coa.setHeadName(rs.getString("head_name"));
                    list.add(coa);
                }
            }
        } catch (Exception e) {
            log.error("searchCOA : " + e.getMessage());
        }
        return list;
    }


    @Override
    public List<ChartOfAccount> getCOATree(String compCode) {
        String hsql = "select o from ChartOfAccount o where  o.coaParent = '#' and o.key.compCode = '" + compCode + "'";
        List<ChartOfAccount> chart = findHSQL(hsql);
        for (ChartOfAccount coa : chart) {
            getChild(coa, compCode);
        }
        return chart;
    }

    @Override
    public List<ChartOfAccount> getAllChild(String parent, String compCode) {
        String strSql = "select o from ChartOfAccount o where o.key.compCode = '"
                + compCode + "' and o.code = '" + parent + "'";
        List<ChartOfAccount> listAllChild = findHSQL(strSql);
        getChild(listAllChild, parent, compCode);
        return listAllChild;
    }

    @Override
    public List<ChartOfAccount> getTraderCOA(String compCode) {
        List<ChartOfAccount> list = new ArrayList<>();
        String sql = "select a.*,coa.coa_code_usr,coa.coa_name_eng,coa1.coa_name_eng group_name\n" +
                "from (\n" +
                "select distinct account_code,comp_code\n" +
                "from trader \n" +
                "where comp_code='" + compCode + "' \n" +
                "and account_code is not null\n" +
                ")a\n" +
                "join chart_of_account coa on a.account_code = coa.coa_code\n" +
                "and a.comp_code = coa.comp_code\n" +
                "join chart_of_account coa1 on coa.coa_parent = coa1.coa_code\n" +
                "and coa.comp_code = coa1.comp_code";
        try {
            ResultSet rs = getResultSet(sql);
            if (rs != null) {
                while (rs.next()) {
                    ChartOfAccount coa = new ChartOfAccount();
                    COAKey key = new COAKey();
                    key.setCoaCode(rs.getString("account_code"));
                    key.setCompCode(compCode);
                    coa.setKey(key);
                    coa.setCoaCodeUsr(rs.getString("coa_code_usr"));
                    coa.setCoaNameEng(rs.getString("coa_name_eng"));
                    coa.setGroupName(rs.getString("group_name"));
                    list.add(coa);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return list;
    }

    @Override
    public List<ChartOfAccount> search(String updatedDate) {
        List<ChartOfAccount> list = new ArrayList<>();
        String sql = "select * from chart_of_account where modify_date > '" + updatedDate + "'";
        ResultSet rs = getResultSet(sql);
        if (rs != null) {
            try {
                //coa_code, coa_code_usr, coa_name_eng, coa_name_mya, active, created_date, modify_date, sort_order_id, created_by, updated_by, coa_parent,
                // coa_option, comp_code, coa_level, parent_usr_code, app_short_name, mig_code, mac_id, cur_code, marked, dept_code, deleted
                while (rs.next()) {
                    ChartOfAccount coa = new ChartOfAccount();
                    COAKey key = new COAKey();
                    key.setCoaCode(rs.getString("coa_code"));
                    key.setCompCode(rs.getString("comp_code"));
                    coa.setKey(key);
                    coa.setCoaCodeUsr(rs.getString("coa_code_usr"));
                    coa.setCoaNameEng(rs.getString("coa_name_eng"));
                    coa.setCoaNameMya(rs.getString("coa_name_mya"));
                    coa.setActive(rs.getBoolean("active"));
                    coa.setCreatedDate(rs.getTimestamp("created_date"));
                    coa.setModifiedDate(rs.getTimestamp("modify_date"));
                    coa.setCreatedBy(rs.getString("created_by"));
                    coa.setModifiedBy(rs.getString("updated_by"));
                    coa.setCoaParent(rs.getString("coa_parent"));
                    coa.setOption(rs.getString("coa_option"));
                    coa.setCoaLevel(rs.getInt("coa_level"));
                    coa.setCurCode(rs.getString("cur_code"));
                    coa.setMarked(rs.getBoolean("marked"));
                    coa.setDeptCode(rs.getString("dept_code"));
                    coa.setDeleted(rs.getBoolean("deleted"));
                    list.add(coa);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return list;
    }

    @Override
    public List<ChartOfAccount> unUpload() {
        String hsql = "select o from ChartOfAccount o where o.intgUpdStatus is null";
        return findHSQL(hsql);
    }

    @Override
    public Date getMaxDate() {
        String sql = "select max(modify_date) date from chart_of_account";
        ResultSet rs = getResultSet(sql);
        try {
            if (rs.next()) {
                Date date = rs.getTimestamp("date");
                if (date != null) {
                    return date;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return Util1.getOldDate();
    }

    private void getChild(List<ChartOfAccount> listAllChild, String parent, String compCode) {
        String strSql = "select o from ChartOfAccount o where o.key.compCode = '"
                + compCode + "' and o.coaParent = '" + parent + "'";
        List<ChartOfAccount> listCOA = findHSQL(strSql);
        if (!listCOA.isEmpty()) {
            listAllChild.addAll(listCOA);
        }
        listCOA.forEach(coa -> getChild(listAllChild, coa.getKey().getCoaCode(), compCode));
    }

    private void getChild(ChartOfAccount parent, String compCode) {
        String hsql = "select o from ChartOfAccount o where o.coaParent = '" + parent.getKey().getCoaCode()
                + "' and o.key.compCode = '" + compCode + "'";
        List<ChartOfAccount> chart = findHSQL(hsql);
        parent.setChild(chart);
        if (!chart.isEmpty()) {
            for (ChartOfAccount coa : chart) {
                getChild(coa, compCode);
            }
        }
    }
}
