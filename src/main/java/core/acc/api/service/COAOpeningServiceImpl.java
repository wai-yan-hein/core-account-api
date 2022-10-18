package core.acc.api.service;

import core.acc.api.dao.COAOpeningDao;
import core.acc.api.entity.TmpOpening;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class COAOpeningServiceImpl implements COAOpeningService {
    @Autowired
    private COAOpeningDao coaOpeningDao;

    @Override
    public List<TmpOpening> getCOAOpening(String coaCode, String opDate, String clDate,
                                          int level, String curr, String compCode, List<String> department,
                                          Integer macId, String traderCode) {
        deleteTmp(macId);
        insertDep(department, macId);
        String opSql = "insert into tmp_op_cl(coa_code, cur_code,opening,mac_id) \n"
                + "select a.acc_code, a.cur_code, sum(a.balance) balance, " + macId + "\n"
                + "from (\n"
                + "select source_acc_id acc_code,cur_code,sum(ifnull(dr_amt,0))-sum(ifnull(cr_amt,0)) balance,\n"
                + "		sum(ifnull(dr_amt,0)) dr_amt, sum(ifnull(cr_amt,0)) cr_amt,trader_code\n"
                + "	from coa_opening \n"
                + "	where source_acc_id = '" + coaCode + "'\n"
                + "        and (dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + "))\n"
                + "        and comp_code = '" + compCode + "'\n"
                + "        and date(op_date) = '" + opDate + "'\n"
                + "        and (cur_code = '" + curr + "' or '-' ='" + curr + "')\n"
                + "        and (trader_code = '" + traderCode + "' or '-' = '" + traderCode + "')\n"
                + "      group by acc_code,cur_code\n"
                + "             union all\n"
                + "select '" + coaCode + "' acc_code ,cur_code cur_code, sum(get_dr_cr_amt(source_ac_id, account_id, '" + coaCode + "', \n"
                + "		ifnull(dr_amt,0), ifnull(cr_amt,0), 'DR')-get_dr_cr_amt(source_ac_id, \n"
                + "             account_id, '" + coaCode + "', ifnull(dr_amt,0), ifnull(cr_amt,0), 'CR')) balance, \n"
                + "		sum(ifnull(dr_amt,0)) dr_amt, sum(ifnull(cr_amt,0)) cr_amt,trader_code \n"
                + "     from gl\n"
                + "	where  (source_ac_id = '" + coaCode + "' or account_id = '" + coaCode + "') \n"
                + "		and date(gl_date)>= '" + opDate + "'\n"
                + "        and date(gl_date) < '" + clDate + "' \n"
                + "        and (dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + "))\n"
                + "        and comp_code = '" + compCode + "'\n"
                + "        and (cur_code = '" + curr + "' or '-' ='" + curr + "')\n"
                + "        and (trader_code = '" + traderCode + "' or '-' = '" + traderCode + "')\n"
                + "	group by acc_code,cur_code) a \n"
                + "group by a.acc_code, a.cur_code";
        coaOpeningDao.executeSql(opSql);
        return coaOpeningDao.getOpening(coaCode, macId);
    }

    private void deleteTmp(Integer machineId) {
        String delSql1 = "delete from tmp_op_cl where mac_id =" + machineId + "";
        String delSql2 = "delete from tmp_dep_filter where mac_id = " + machineId + "";
        coaOpeningDao.executeSql(delSql1, delSql2);
    }

    private void insertDep(List<String> department, Integer macId) {
        if (!department.isEmpty()) {
            for (String code : department) {
                String sql = "insert into tmp_dep_filter(dept_code,mac_id)\n" +
                        "select '" + code + "'," + macId + "";
                coaOpeningDao.executeSql(sql);
            }
        }
    }
}
