package core.acc.api.service;

import core.acc.api.common.Util1;
import core.acc.api.dao.COAOpeningDao;
import core.acc.api.dao.ReportDao;
import core.acc.api.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class COAOpeningServiceImpl implements COAOpeningService {
    @Autowired
    private COAOpeningDao coaOpeningDao;
    @Autowired
    private SeqTableService seqService;
    @Autowired
    private TraderService traderService;
    @Autowired
    private ReportDao reportDao;

    @Override
    public COAOpening save(COAOpening op) {
        if (Util1.isNull(op.getKey().getCoaOpId())) {
            op.getKey().setCoaOpId(getCode(op.getKey().getCompCode()));
        }
        if (op.getTraderCode() != null) {
            if (op.getSourceAccId() == null) {
                TraderKey key = new TraderKey();
                key.setCode(op.getTraderCode());
                key.setCompCode(op.getKey().getCompCode());
                Trader t = traderService.findById(key);
                if (t != null) {
                    op.setSourceAccId(t.getAccount());
                }
            }
        }
        return coaOpeningDao.save(op);
    }

    @Override
    public boolean delete(OpeningKey key) {
        return coaOpeningDao.delete(key);
    }

    private String getCode(String compCode) {
        int seqNo = seqService.getSequence(0, "OPENING", "-", compCode);
        return compCode + "-" + String.format("%0" + 4 + "d", seqNo);
    }
    @Override
    public TmpOpening getCOAOpening(String coaCode, String opDate, String clDate, String curr, String compCode, Integer macId, String traderCode) {
        String opSql = "select source_acc_id,cur_code,balance\n" +
                "from (\n" +
                "select source_acc_id,cur_code,sum(dr_amt)-sum(cr_amt) balance\n" +
                "from (\n" + "select source_acc_id, cur_code,sum(ifnull(dr_amt,0)) dr_amt,sum(ifnull(cr_amt,0)) cr_amt\n" +
                "from coa_opening\n" +
                "where date(op_date)='" + opDate + "'\n" +
                "and deleted = false\n" + "and dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n" +
                "and source_acc_id ='" + coaCode + "'\n" +
                "and comp_code = '" + compCode + "'\n" +
                "and cur_code ='" + curr + "'\n" +
                "and (trader_code ='" + traderCode + "' or '-'='" + traderCode + "')\n" +
                "and (dr_amt>0 or cr_amt>0)\n" + "group by source_acc_id\n" +
                "\tunion all\n" +
                "select account_id, cur_code,sum(ifnull(cr_amt,0)) dr_amt,sum(ifnull(dr_amt,0)) cr_amt\n" +
                "from gl \n" + "where account_id ='" + coaCode + "'\n" +
                "and date(gl_date) >='" + opDate + "' and date(gl_date)<'" + clDate + "'\n" +
                "and dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n" +
                "and comp_code = '" + compCode + "'\n" +
                "and deleted = false\n" +
                "and cur_code ='" + curr + "'\n" +
                "and (trader_code ='" + traderCode + "' or '-'='" + traderCode + "')\n" +
                "group by account_id\n" +
                "\tunion all\n" +
                "select source_ac_id, cur_code,sum(ifnull(dr_amt,0)) dr_amt,sum(ifnull(cr_amt,0)) cr_amt\n" +
                "from gl \n" +
                "where source_ac_id ='" + coaCode + "'\n" +
                "and date(gl_date) >='" + opDate + "' and date(gl_date)<'" + clDate + "'\n" +
                "and dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n" +
                "and comp_code = '" + compCode + "'\n" +
                "and deleted = false\n" +
                "and cur_code ='" + curr + "'\n" +
                "and (trader_code ='" + traderCode + "' or '-'='" + traderCode + "')\n" +
                "group by source_ac_id\n" + ")a\n" +
                "group by source_acc_id)b";
        ResultSet rs = reportDao.executeAndResult(opSql);
        try {
            if (rs.next()) {
                TmpOpening op = new TmpOpening();
                TmpOpeningKey key = new TmpOpeningKey();
                key.setCoaCode(rs.getString("source_acc_id"));
                key.setCurCode(rs.getString("cur_code"));
                key.setMacId(macId);
                op.setKey(key);
                op.setOpening(rs.getDouble("balance"));
                return op;
            }
        } catch (Exception e) {
            log.error("getCOAOpening : " + e.getMessage());
        }
        TmpOpening op = new TmpOpening();
        TmpOpeningKey key = new TmpOpeningKey();
        key.setCoaCode(coaCode);
        key.setCurCode(curr);
        key.setMacId(macId);
        op.setKey(key);
        op.setOpening(0.0);
        return op;
    }

    @Override
    public List<COAOpening> searchOpening(String opDate, String deptCode, String curCode,
                                          String traderType, String coaLv1, String coaLv2, String coaLv3,
                                          String projectNo, String compCode) {
        String sql = "select op.*,c1.coa_code_usr,c1.coa_name_eng,c1.coa_parent coa_lv2,c2.coa_parent coa_lv1,t.user_code t_user_code,t.trader_name,t.discriminator,d.usr_code\n" +
                "from coa_opening op\n" +
                "join chart_of_account c1 on op.source_acc_id = c1.coa_code\n" +
                "and op.comp_code = c1.comp_code\n" +
                "join chart_of_account c2 on c1.coa_parent = c2.coa_code\n" +
                "and c1.comp_code = c2.comp_code\n" +
                "left join trader t\n" +
                "on op.trader_code = t.code\n" +
                "and op.comp_code = t.comp_code\n" +
                "join department d on op.dept_code = d.dept_code\n" +
                "and op.comp_code = d.comp_code\n" +
                "where (op.dept_code ='" + deptCode + "' or '-' ='" + deptCode + "')\n" +
                "and op.deleted = false\n" +
                "and (op.source_acc_id ='" + coaLv3 + "' or '-'='" + coaLv3 + "')\n" +
                "and (c1.coa_parent ='" + coaLv2 + "' or '-'='" + coaLv2 + "')\n" +
                "and (c2.coa_parent ='" + coaLv1 + "' or '-'='" + coaLv1 + "')\n" +
                "and (op.cur_code ='" + curCode + "' or '-'='" + curCode + "')\n" +
                "and (op.project_no ='" + projectNo + "' or '-'='" + projectNo + "')\n" +
                "and op.comp_code ='" + compCode + "'\n" +
                "and op.op_date ='" + opDate + "'\n" +
                "and (t.discriminator='" + traderType + "' or '-' ='" + traderType + "')\n" +
                "order by c1.coa_code_usr,t.user_code";
        ResultSet rs = reportDao.executeAndResult(sql);
        List<COAOpening> list = new ArrayList<>();
        try {
            if (rs != null) {
                //op_date, source_acc_id, cur_code, dr_amt, cr_amt, user_code, comp_code,
                // created_date, dept_code, coa_op_id, tran_source, trader_code,
                // coa_name_eng, coa_lv2, coa_lv1, trader_name, discriminator
                while (rs.next()) {
                    COAOpening coa = new COAOpening();
                    OpeningKey key = new OpeningKey();
                    key.setCompCode(compCode);
                    key.setCoaOpId(rs.getString("coa_op_id"));
                    coa.setKey(key);
                    coa.setCoaUsrCode(rs.getString("coa_code_usr"));
                    coa.setSourceAccId(rs.getString("source_acc_id"));
                    coa.setCurCode(rs.getString("cur_code"));
                    coa.setDrAmt(rs.getDouble("dr_amt"));
                    coa.setCrAmt(rs.getDouble("cr_amt"));
                    coa.setUserCode(rs.getString("user_code"));
                    coa.setCreatedDate(rs.getTimestamp("created_date").toLocalDateTime());
                    coa.setDeptCode(rs.getString("dept_code"));
                    coa.setTraderCode(rs.getString("trader_code"));
                    coa.setSrcAccName(rs.getString("coa_name_eng"));
                    coa.setTraderName(rs.getString("trader_name"));
                    coa.setTraderUsrCode(rs.getString("t_user_code"));
                    coa.setDeptUsrCode(rs.getString("usr_code"));
                    coa.setProjectNo(rs.getString("project_no"));
                    list.add(coa);
                }
            }
        } catch (Exception e) {
            log.error("searchOpening : " + e.getMessage());
        }
        return list;
    }

}
