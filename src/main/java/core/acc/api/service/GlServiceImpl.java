package core.acc.api.service;

import core.acc.api.common.ReturnObject;
import core.acc.api.common.Util1;
import core.acc.api.dao.GlDao;
import core.acc.api.dao.ReportDao;
import core.acc.api.entity.Gl;
import core.acc.api.entity.VGl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class GlServiceImpl implements GlService {
    @Autowired
    private GlDao glDao;
    @Autowired
    private SeqTableService seqService;
    @Autowired
    private ReportDao reportDao;

    @Override
    public Gl save(Gl gl) throws Exception {
        if (Util1.isNull(gl.getGlCode())) {
            Integer macId = gl.getMacId();
            String compCode = gl.getCompCode();
            String glCode = getGLCode(macId, compCode);
            Gl valid = findByCode(glCode);
            if (Objects.isNull(valid)) {
                gl.setGlCode(glCode);
            } else {
                throw new IllegalStateException("Duplication Occur in Gl");
            }
        } else {
            backupGl(gl, "EDIT");
        }
        if (gl.getDelList() != null) {
            for (String code : gl.getDelList()) {
                Gl gv = findByCode(code);
                backupGl(gv, "GV_DELETE");
                glDao.delete(gv);
            }
        }
        return glDao.save(gl);
    }

    @Override
    public ReturnObject save(List<Gl> glList) throws Exception {
        ReturnObject ro = new ReturnObject();
        if (!glList.isEmpty()) {
            Gl tmp = glList.get(0);
            String vouNo = tmp.getRefNo();
            String tranSource = tmp.getTranSource();
            String compCode = tmp.getCompCode();
            boolean delete = tmp.isDeleted();
            glDao.deleteGl(vouNo, tranSource);
            if (!delete) {
                for (Gl gl : glList) {
                    if (Util1.isMultiCur()) {
                        if (gl.isCash()) {
                            gl.setSrcAccCode(Util1.getProperty(gl.getCurCode()));
                        }
                    }
                    double amt = Util1.getDouble(gl.getDrAmt()) + Util1.getDouble(gl.getCrAmt());
                    if (amt > 0) {
                        save(gl);
                    }
                }
            }
            ro.setVouNo(vouNo);
            ro.setTranSource(tranSource);
            ro.setCompCode(compCode);
        }
        return ro;
    }

    @Override
    public Gl findByCode(String glCode) {
        return glDao.findByCode(glCode);
    }

    @Override
    public boolean delete(Gl gl) {
        backupGl(gl, "DELETE");
        return glDao.delete(gl);
    }

    private void backupGl(Gl gl, String option) {
        String userCode = gl.getModifyBy();
        Integer macId = gl.getMacId();
        String logCode = getGLLogCode(macId, gl.getCompCode());
        String sql = "insert into gl_log(gl_code, gl_date, created_date, modify_date, modify_by, \n"
                + "description, source_ac_id, account_id, cur_code, dr_amt, cr_amt, \n"
                + "reference, dept_code, voucher_no, user_code, trader_code, comp_code, \n"
                + "tran_source, gl_vou_no, split_id, intg_upd_status, remark, \n"
                + "ref_no, mac_id,log_status,log_user_code,log_mac_id,log_gl_code)\n"
                + "select gl_code, gl_date, created_date, modify_date, modify_by, \n"
                + "description, source_ac_id, account_id, cur_code, dr_amt, cr_amt, \n"
                + "reference, dept_code, voucher_no, user_code, trader_code, comp_code, \n"
                + "tran_source, gl_vou_no, split_id, intg_upd_status, remark, \n"
                + "ref_no, mac_id,'" + option + "','" + userCode + "'," + macId + ",'" + logCode + "'\n"
                + "from gl\n"
                + "where gl_code = '" + gl.getGlCode() + "'";
        reportDao.execSQLRpt(sql);
    }

    @Override
    public List<VGl> search(String fromDate, String toDate, String desp, String srcAcc, String acc, String curCode,
                            String reference, String dept, String retNo, String compCode, String tranSource,
                            String glVouNo, String traderCode) {
        return glDao.search(fromDate, toDate, desp, srcAcc, acc, curCode,
                reference, dept, retNo, compCode, tranSource,
                glVouNo, traderCode);
    }

    private String getGLCode(Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqService.getSequence(macId, "GL", period, compCode);
        return String.format("%0" + 3 + "d", macId) + period + String.format("%0" + 5 + "d", seqNo);
    }

    private String getGLLogCode(Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqService.getSequence(macId, "GL-LOG", period, compCode);
        return String.format("%0" + 3 + "d", macId) + period + String.format("%0" + 5 + "d", seqNo);
    }
}
