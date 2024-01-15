package core.acc.api.dao;

import core.acc.api.common.Util1;
import core.acc.api.entity.COAKey;
import core.acc.api.entity.ChartOfAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        String sql = """
                select a.*,c1.coa_code group_code,c1.coa_code_usr group_usr_code,c1.coa_name_eng group_name,c2.coa_code head_code,c2.coa_code_usr head_usr_code,c2.coa_name_eng head_name
                from (
                select coa_code,coa_code_usr,coa_name_eng,coa_parent,comp_code,coa_level
                from chart_of_account
                where active = true
                and deleted = false
                and coa_level = 3
                and comp_code =?)a
                left join chart_of_account c1
                on a.coa_parent = c1.coa_code
                and a.comp_code = c1.comp_code
                left join chart_of_account c2
                on c1.coa_parent = c2.coa_code
                and c1.comp_code = c2.comp_code""";
        ResultSet rs = getResult(sql, compCode);
        try {
            while (rs.next()) {
                ChartOfAccount coa = new ChartOfAccount();
                COAKey key = new COAKey();
                key.setCompCode(compCode);
                key.setCoaCode(rs.getString("coa_code"));
                coa.setKey(key);
                coa.setCoaCodeUsr(rs.getString("coa_code_usr"));
                coa.setCoaNameEng(rs.getString("coa_name_eng"));
                coa.setGroupCode(rs.getString("group_code"));
                coa.setGroupUsrCode(rs.getString("group_usr_code"));
                coa.setGroupName(rs.getString("group_name"));
                coa.setHeadCode(rs.getString("head_code"));
                coa.setHeadUsrCode(rs.getString("head_usr_code"));
                coa.setHeadName(rs.getString("head_name"));
                coa.setCoaLevel(Util1.getInteger(rs.getInt("coa_level")));
                list.add(coa);
            }
        } catch (Exception e) {
            log.error("");
        }
        return list;
    }

    @Override
    public Boolean delete(COAKey key) {
        String sql = "update chart_of_account set deleted = true where comp_code ='" + key.getCompCode() + "' and coa_code='" + key.getCoaCode() + "'";
        execSql(sql);
        return true;
    }


    @Override
    public List<ChartOfAccount> getUnusedCOA(String compCode) {
        return null;
    }

    @Override
    public List<ChartOfAccount> getCOAChild(String parentCode, String compCode) {
        String hsql = "select o from ChartOfAccount o where o.coaParent = '" + parentCode + "' and o.key.compCode = '" + compCode + "' and o.deleted = false order by o.coaCodeUsr";
        return findHSQL(hsql);
    }

    @Override
    public List<ChartOfAccount> searchCOA(String str, Integer level, String compCode) {
        str = Util1.cleanStr(str);
        str = str + "%";
        List<ChartOfAccount> list = new ArrayList<>();
        String sql = "select a.*,c1.coa_code group_code,c1.coa_code_usr group_usr_code,c1.coa_name_eng group_name,c2.coa_code head_code,c2.coa_code_usr head_usr_code,c2.coa_name_eng head_name\n" +
                "from (\n" +
                "select coa_code,coa_code_usr,coa_name_eng,coa_parent,comp_code,coa_level\n" +
                "from chart_of_account\n" +
                "where active = true\n" +
                "and deleted = false\n" +
                "and (coa_level =? or 0 =?)\n" +
                "and comp_code =?\n" +
                "and (LOWER(REPLACE(coa_code_usr, ' ', '')) like ? or LOWER(REPLACE(coa_name_eng, ' ', '')) like ?)\n" +
                "limit 20\n" + ")a\n" +
                "left join chart_of_account c1\n" +
                "on a.coa_parent = c1.coa_code\n" +
                "and a.comp_code = c1.comp_code\n" +
                "left join chart_of_account c2\n" +
                "on c1.coa_parent = c2.coa_code\n" +
                "and c1.comp_code = c2.comp_code\n"+
                "order by coa_code_usr,coa_name_eng";
        ResultSet rs = getResult(sql, level, level, compCode, str, str);
        try {
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
                coa.setCoaLevel(rs.getInt("coa_level"));
                list.add(coa);
            }
        } catch (Exception e) {
            log.error("searchCOA : " + e.getMessage());
        }
        return list;
    }


    @Override
    public List<ChartOfAccount> getCOATree(String compCode) {
        String hsql = "select o from ChartOfAccount o where  o.coaParent = '#' and o.key.compCode = '" + compCode + "' and o.deleted = false";
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
            ResultSet rs = getResult(sql);
            while (rs.next()) {
                ChartOfAccount coa = new ChartOfAccount();
                COAKey key = new COAKey();
                key.setCoaCode(rs.getString("account_code"));
                key.setCompCode(rs.getString("comp_code"));
                coa.setKey(key);
                coa.setCoaCodeUsr(rs.getString("coa_code_usr"));
                coa.setCoaNameEng(rs.getString("coa_name_eng"));
                coa.setGroupName(rs.getString("group_name"));
                list.add(coa);
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return list;
    }

    @Override
    public List<ChartOfAccount> getUpdatedCOA(LocalDateTime updatedDate) {
        String hsql = "select o from ChartOfAccount o where modifiedDate>:updatedDate";
        return createQuery(hsql).setParameter("updatedDate", updatedDate).getResultList();
    }

    @Override
    public List<ChartOfAccount> unUpload() {
        String hsql = "select o from ChartOfAccount o where o.intgUpdStatus is null";
        return findHSQL(hsql);
    }

    @Override
    public Date getMaxDate() {
        String sql = "select max(modify_date) date from chart_of_account";
        ResultSet rs = getResult(sql);
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

    @Override
    public List<ChartOfAccount> findAllActive(String compCode) {
        String hsql = "select o from ChartOfAccount o where o.key.compCode ='" + compCode + "' and o.active =true and o.deleted = false";
        return findHSQL(hsql);
    }

    @Override
    public List<ChartOfAccount> getCOAByGroup(String groupCode, String compCode) {
        String sql = """
                select coa_code,coa_name_eng,comp_code
                from chart_of_account
                where coa_parent =?
                and comp_code =?
                and active = true and deleted = false
                order by coa_code_usr,coa_name_eng""";
        ResultSet rs = getResult(sql, groupCode, compCode);
        List<ChartOfAccount> list = new ArrayList<>();
        try {
            while (rs.next()) {
                ChartOfAccount c = new ChartOfAccount();
                COAKey key = new COAKey();
                key.setCoaCode(rs.getString("coa_code"));
                key.setCompCode(rs.getString("comp_code"));
                c.setKey(key);
                c.setCoaNameEng(rs.getString("coa_name_eng"));
                list.add(c);
            }
        } catch (Exception e) {
            log.error("getCOAByGroup : " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<ChartOfAccount> getCOAByHead(String headCode, String compCode) {
        List<ChartOfAccount> list = new ArrayList<>();
        String sql = """
                select coa.coa_code,coa.coa_code_usr,coa.coa_name_eng,coa.comp_code
                from(
                select coa_code,comp_code
                from chart_of_account
                where coa_parent =?
                and comp_code =?
                )a
                join chart_of_account coa on a.coa_code = coa.coa_parent
                and a.comp_code=coa.comp_code
                and coa.active =true and coa.deleted =false
                order by coa.coa_code_usr,coa.coa_name_eng""";
        ResultSet rs = getResult(sql, headCode, compCode);
        try {
            while (rs.next()) {
                ChartOfAccount coa = new ChartOfAccount();
                COAKey key = new COAKey();
                key.setCoaCode(rs.getString("coa_code"));
                key.setCompCode(rs.getString("comp_code"));
                coa.setKey(key);
                coa.setCoaNameEng(rs.getString("coa_name_eng"));
                coa.setCoaCodeUsr(rs.getString("coa_code_usr"));
                list.add(coa);
            }
        } catch (Exception e) {
            log.error("getCOAByHead : " + e.getMessage());
        }

        return list;
    }


    private void getChild(ChartOfAccount parent, String compCode) {
        String hsql = "select o from ChartOfAccount o where o.coaParent = '" + parent.getKey().getCoaCode() + "' and o.key.compCode = '" + compCode + "' and o.deleted = false";
        List<ChartOfAccount> chart = findHSQL(hsql);
        parent.setChild(chart);
        if (!chart.isEmpty()) {
            for (ChartOfAccount coa : chart) {
                getChild(coa, compCode);
            }
        }
    }
}
