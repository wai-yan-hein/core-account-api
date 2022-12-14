package core.acc.api.dao;

import core.acc.api.entity.StockOP;
import core.acc.api.entity.StockOPKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class StockOPDaoImpl extends AbstractDao<StockOPKey, StockOP> implements StockOPDao {
    @Override
    public StockOP save(StockOP op) {
        persist(op);
        return op;
    }

    @Override
    public void delete(StockOPKey key) {
        String sql = "update stock_op_value set deleted = 1 where tran_code= '" + key.getTranCode() + "' and comp_code='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        execSql(sql);
    }

    @Override
    public List<StockOP> search(String fromDate, String toDate, String deptCode, String curCode, String compCode) {
        List<StockOP> list = new ArrayList<>();
        String sql = "select a.*,dep.usr_code d_user_code,coa.coa_code_usr,coa.coa_name_eng\n" +
                "from (\n" +
                "select * \n" +
                "from stock_op_value\n" +
                "where comp_code ='" + compCode + "'\n" +
                "and deleted =0\n" +
                "and date(tran_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and (curr_code ='" + curCode + "' or '-' ='" + curCode + "')\n" +
                "and (dept_code ='" + deptCode + "' or '-' ='" + deptCode + "')\n" +
                ")a\n" +
                "join department dep \n" +
                "on a.dept_code = dep.dept_code\n" +
                "and a.comp_code = dep.comp_code\n" +
                "join chart_of_account coa\n" +
                "on a.coa_code = coa.coa_code\n" +
                "and a.comp_code = coa.comp_code\n" +
                "order by tran_date\n";
        ResultSet rs = getResultSet(sql);
        if (rs != null) {
            try {
                while (rs.next()) {
                    StockOP op = new StockOP();
                    StockOPKey key = new StockOPKey();
                    key.setTranCode(rs.getString("tran_code"));
                    key.setCompCode(rs.getString("comp_code"));
                    key.setDeptId(rs.getInt("dept_id"));
                    op.setKey(key);
                    op.setTranDate(rs.getTimestamp("tran_date"));
                    op.setDeleted(rs.getBoolean("deleted"));
                    op.setCurCode(rs.getString("curr_code"));
                    op.setRemark(rs.getString("remark"));
                    op.setCoaCode(rs.getString("coa_code"));
                    op.setCoaCodeUser(rs.getString("coa_code_usr"));
                    op.setCoaNameEng(rs.getString("coa_name_eng"));
                    op.setDeptCode(rs.getString("dept_code"));
                    op.setDeptUsrCode(rs.getString("d_user_code"));
                    op.setCreatedBy(rs.getString("created_by"));
                    op.setCreatedDate(rs.getTimestamp("created_date"));
                    op.setUpdatedBy(rs.getString("updated_by"));
                    op.setUpdatedDate(rs.getTimestamp("updated_date"));
                    op.setClAmt(rs.getDouble("amount"));
                    list.add(op);
                }
            } catch (Exception e) {
                log.error("search : " + e.getMessage());
            }
        }
        return list;
    }
}
