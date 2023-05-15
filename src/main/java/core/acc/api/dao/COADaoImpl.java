package core.acc.api.dao;

import core.acc.api.common.Util1;
import core.acc.api.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class COADaoImpl extends AbstractDao<COAKey, ChartOfAccount> implements COADao {
    @Override
    public ChartOfAccount save(ChartOfAccount coa) {
        saveOrUpdate(coa, coa.getKey());
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
                "on c1.coa_parent = c2.coa_code\n" + "and c1.comp_code = c2.comp_code\n" +
                "where c1.deleted =0\n" + "and c1.active =1\n" +
                "and c1.comp_code ='" + compCode + "'\n" +
                "order by c1.coa_level,c1.coa_code_usr,c1.coa_name_eng";
        List<Map<String, Object>> result = getList(sql);
        result.forEach(rs -> {
            ChartOfAccount coa = new ChartOfAccount();
            COAKey key = new COAKey();
            key.setCompCode(compCode);
            key.setCoaCode(Util1.getString(rs.get("coa_code")));
            coa.setKey(key);
            coa.setCoaCodeUsr(Util1.getString(rs.get("coa_code_usr")));
            coa.setCoaNameEng(Util1.getString(rs.get("coa_name_eng")));
            coa.setGroupName(Util1.getString(rs.get("parent_name")));
            coa.setCoaLevel(Util1.getInteger(rs.get("coa_level")));
            list.add(coa);
        });
        return list;
    }

    @Override
    public Boolean delete(COAKey key) {
        String sql = "update chart_of_account set deleted =1 where comp_code ='" + key.getCompCode() + "' and coa_code='" + key.getCoaCode() + "'";
        execSql(sql);
        return true;
    }


    @Override
    public List<ChartOfAccount> getUnusedCOA(String compCode) {
        return null;
    }

    @Override
    public List<ChartOfAccount> getCOAChild(String parentCode, String compCode) {
        String hsql = "select o from ChartOfAccount o where o.coaParent = '" + parentCode + "' and o.key.compCode = '" + compCode + "' and o.deleted =0 order by o.coaCodeUsr";
        return findHSQL(hsql);
    }

    @Override
    public List<ChartOfAccount> searchCOA(String str, Integer level, String compCode) {
        List<ChartOfAccount> list = new ArrayList<>();
        String sql = "select a.*,c1.coa_code group_code,c1.coa_code_usr group_usr_code,c1.coa_name_eng group_name,c2.coa_code head_code,c2.coa_code_usr head_usr_code,c2.coa_name_eng head_name\n" +
                "from (\n" +
                "select coa_code,coa_code_usr,coa_name_eng,coa_parent,comp_code,coa_level\n" +
                "from chart_of_account\n" +
                "where active = 1\n" +
                "and deleted = 0\n" +
                "and (coa_level =" + level + " or 0 =" + level + ")\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and (coa_code_usr like '" + str + "%' or coa_name_eng like '" + str + "%')\n" +
                "limit 20\n" + ")a\n" +
                "left join chart_of_account c1\n" +
                "on a.coa_parent = c1.coa_code\n" +
                "and a.comp_code = c1.comp_code\n" +
                "left join chart_of_account c2\n" +
                "on c1.coa_parent = c2.coa_code\n" +
                "and c1.comp_code = c2.comp_code";
        List<Map<String, Object>> result = getList(sql);
        result.forEach((rs) -> {
            ChartOfAccount coa = new ChartOfAccount();
            //coa_code, coa_code_usr, coa_name_eng, group_code, group_usr_code, group_name, head_code, head_usr_code, head_name
            COAKey key = new COAKey();
            key.setCoaCode(Util1.getString(rs.get("coa_code")));
            key.setCompCode(compCode);
            coa.setKey(key);
            coa.setCoaCodeUsr(Util1.getString(rs.get("coa_code_usr")));
            coa.setCoaNameEng(Util1.getString(rs.get("coa_name_eng")));
            coa.setGroupCode(Util1.getString(rs.get("group_code")));
            coa.setGroupUsrCode(Util1.getString(rs.get("group_usr_code")));
            coa.setGroupName(Util1.getString(rs.get("group_name")));
            coa.setHeadCode(Util1.getString(rs.get("head_code")));
            coa.setHeadUsrCode(Util1.getString(rs.get("head_usr_code")));
            coa.setHeadName(Util1.getString(rs.get("head_name")));
            coa.setCoaLevel(Util1.getInteger(rs.get("coa_level")));
            list.add(coa);

        });
        return list;
    }


    @Override
    public List<ChartOfAccount> getCOATree(String compCode) {
        String hsql = "select o from ChartOfAccount o where  o.coaParent = '#' and o.key.compCode = '" + compCode + "' and o.deleted =0";
        List<ChartOfAccount> chart = findHSQL(hsql);
        for (ChartOfAccount coa : chart) {
            getChild(coa, compCode);
        }
        return chart;
    }


    @Override
    public List<ChartOfAccount> getTraderCOA(String compCode) {
        List<ChartOfAccount> list = new ArrayList<>();
        String sql = "select a.*,coa.coa_code_usr,coa.coa_name_eng,coa1.coa_name_eng group_name\n" + "from (\n" + "select distinct account_code,comp_code\n" + "from trader \n" + "where comp_code='" + compCode + "' \n" + "and account_code is not null\n" + ")a\n" + "join chart_of_account coa on a.account_code = coa.coa_code\n" + "and a.comp_code = coa.comp_code\n" + "join chart_of_account coa1 on coa.coa_parent = coa1.coa_code\n" + "and coa.comp_code = coa1.comp_code";
        try {
            List<Map<String, Object>> result = getList(sql);
            result.forEach((rs) -> {
                ChartOfAccount coa = new ChartOfAccount();
                COAKey key = new COAKey();
                key.setCoaCode(Util1.getString(rs.get("account_code")));
                key.setCompCode(compCode);
                coa.setKey(key);
                coa.setCoaCodeUsr(Util1.getString(rs.get("coa_code_usr")));
                coa.setCoaNameEng(Util1.getString(rs.get("coa_name_eng")));
                coa.setGroupName(Util1.getString(rs.get("group_name")));
                list.add(coa);

            });
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return list;
    }

    @Override
    public List<ChartOfAccount> search(String updatedDate) {
        List<ChartOfAccount> list = new ArrayList<>();
        String sql = "select * from chart_of_account where modify_date > '" + updatedDate + "'";
        List<Map<String, Object>> result = getList(sql);
        result.forEach((rs) -> {
            ChartOfAccount coa = new ChartOfAccount();
            COAKey key = new COAKey();
            key.setCoaCode(Util1.getString(rs.get("coa_code")));
            key.setCompCode(Util1.getString(rs.get("comp_code")));
            coa.setKey(key);
            coa.setCoaCodeUsr(Util1.getString(rs.get("coa_code_usr")));
            coa.setCoaNameEng(Util1.getString(rs.get("coa_name_eng")));
            coa.setCoaNameMya(Util1.getString(rs.get("coa_name_mya")));
            coa.setActive(Util1.getBoolean(rs.get("active")));
            coa.setCreatedDate(Util1.toDate(rs.get("created_date")));
            coa.setModifiedDate(Util1.toDate(rs.get("modify_date")));
            coa.setCreatedBy(Util1.getString(rs.get("created_by")));
            coa.setModifiedBy(Util1.getString(rs.get("updated_by")));
            coa.setCoaParent(Util1.getString(rs.get("coa_parent")));
            coa.setOption(Util1.getString(rs.get("coa_option")));
            coa.setCoaLevel(Util1.getInteger(rs.get("coa_level")));
            coa.setCurCode(Util1.getString(rs.get("cur_code")));
            coa.setMarked(Util1.getBoolean(rs.get("marked")));
            coa.setDeptCode(Util1.getString(rs.get("dept_code")));
            coa.setDeleted(Util1.getBoolean(rs.get("deleted")));
            list.add(coa);
        });
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
        List<Map<String, Object>> result = getList(sql);
        if (!result.isEmpty()) {
            Map<String, Object> rs = result.get(0);
            Date date = Util1.toDate(rs.get("date"));
            if (date != null) {
                return date;
            }
        }
        return Util1.getOldDate();
    }

    @Override
    public List<ChartOfAccount> findAllActive(String compCode) {
        String hsql = "select o from ChartOfAccount o where o.key.compCode ='" + compCode + "' and o.active =1 and o.deleted =0";
        return findHSQL(hsql);
    }

    @Override
    public List<ChartOfAccount> getCOA(String headCode, String compCode) {
        String sql = "select coa.coa_code,coa.comp_code,coa.coa_name_eng\n" + "from chart_of_account coa join(\n" + "select coa_code,comp_code\n" + "from chart_of_account\n" + "where coa_parent ='" + headCode + "'\n" + "and comp_code ='" + compCode + "'\n" + ")a\n" + "on coa.coa_parent = a.coa_code\n" + "and coa.comp_code = a.comp_code\n" + "where coa.coa_level =3";
        List<Map<String, Object>> result = getList(sql);
        List<ChartOfAccount> list = new ArrayList<>();
        result.forEach((row) -> {
            ChartOfAccount c = new ChartOfAccount();
            COAKey key = new COAKey();
            key.setCoaCode(Util1.getString(row.get("coa_code")));
            key.setCompCode(Util1.getString(row.get("comp_code")));
            c.setKey(key);
            c.setCoaNameEng(Util1.getString(row.get("coa_name_eng")));
            list.add(c);
        });
        return list;
    }

    private void getChild(List<ChartOfAccount> listAllChild, String parent, String compCode) {
        String strSql = "select o from ChartOfAccount o where o.key.compCode = '" + compCode + "' and o.coaParent = '" + parent + "'";
        List<ChartOfAccount> listCOA = findHSQL(strSql);
        if (!listCOA.isEmpty()) {
            listAllChild.addAll(listCOA);
        }
        listCOA.forEach(coa -> getChild(listAllChild, coa.getKey().getCoaCode(), compCode));
    }

    private void getChild(ChartOfAccount parent, String compCode) {
        String hsql = "select o from ChartOfAccount o where o.coaParent = '" + parent.getKey().getCoaCode() + "' and o.key.compCode = '" + compCode + "' and o.deleted =0";
        List<ChartOfAccount> chart = findHSQL(hsql);
        parent.setChild(chart);
        if (!chart.isEmpty()) {
            for (ChartOfAccount coa : chart) {
                getChild(coa, compCode);
            }
        }
    }
}
