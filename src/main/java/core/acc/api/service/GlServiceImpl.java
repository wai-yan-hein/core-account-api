package core.acc.api.service;

import core.acc.api.common.Util1;
import core.acc.api.dao.GlDao;
import core.acc.api.dao.ReportDao;
import core.acc.api.entity.Gl;
import core.acc.api.entity.GlKey;
import core.acc.api.entity.VDescription;
import core.acc.api.entity.VRef;
import core.acc.api.model.ReturnObject;
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
        if (Util1.isNull(gl.getKey().getGlCode())) {
            Integer macId = gl.getMacId();
            String compCode = gl.getKey().getCompCode();
            String glCode = getGLCode(macId, compCode);
            GlKey key = new GlKey();
            key.setGlCode(glCode);
            key.setCompCode(compCode);
            Gl valid = findByCode(key);
            if (Objects.isNull(valid)) {
                gl.getKey().setGlCode(glCode);
            } else {
                throw new IllegalStateException("Duplication Occur in Gl");
            }
        } else {
            backupGl(gl, "EDIT");
        }
        if (gl.getDelList() != null) {
            for (String code : gl.getDelList()) {
                GlKey key = new GlKey();
                key.setCompCode(gl.getKey().getCompCode());
                key.setGlCode(code);
                Gl gv = findByCode(key);
                backupGl(gv, "GV_DELETE");
                glDao.delete(key);
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
            String compCode = tmp.getKey().getCompCode();
            boolean delete = tmp.isDeleted();
            String glVouNo = tmp.getGlVouNo();
            if (tranSource.equals("GV")) {
                if (Util1.isNullOrEmpty(glVouNo)) {
                    glVouNo = getVouNo(tmp.getMacId(), tmp.getKey().getCompCode());
                }
            }
            glDao.deleteGl(vouNo, tranSource);
            if (!delete) {
                for (Gl gl : glList) {
                    if (gl.getSrcAccCode() != null) {
                        if (Util1.isMultiCur()) {
                            if (gl.isCash()) {
                                gl.setSrcAccCode(Util1.getProperty(gl.getCurCode()));
                            }
                        }
                        double amt = Util1.getDouble(gl.getDrAmt()) + Util1.getDouble(gl.getCrAmt());
                        if (amt > 0) {
                            gl.setGlVouNo(glVouNo);
                            save(gl);
                        }
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
    public Gl findByCode(GlKey key) {
        return glDao.findByCode(key);
    }

    @Override
    public boolean delete(GlKey key) {
        backupGl(glDao.findByCode(key), "DELETE");
        return glDao.delete(key);
    }

    @Override
    public List<VDescription> getDescription(String str, String compCode) {
        return glDao.getDescription(str, compCode);
    }

    @Override
    public List<VRef> getReference(String str, String compCode) {
        return glDao.getReference(str, compCode);
    }

    @Override
    public List<Gl> searchJournal(String fromDate, String toDate, String vouNo, String description, String reference, String compCode, Integer macId) {
        return glDao.searchJournal(fromDate, toDate, vouNo, description, reference, compCode, macId);
    }

    @Override
    public List<Gl> getJournal(String glVouNo, String compCode) {
        return glDao.getJournal(glVouNo, compCode);
    }

    private void backupGl(Gl gl, String option) {
    }


    private String getGLCode(Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqService.getSequence(macId, "GL", period, compCode);
        return String.format("%0" + 3 + "d", macId) + period + String.format("%0" + 5 + "d", seqNo);
    }

    private String getVouNo(Integer macId, String compCode) {
        String type = "GV";
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqService.getSequence(macId, "GV", period, compCode);
        return type + "-" + String.format("%0" + 5 + "d", seqNo) + period;
    }

    private String getGLLogCode(Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqService.getSequence(macId, "GL-LOG", period, compCode);
        return String.format("%0" + 3 + "d", macId) + period + String.format("%0" + 5 + "d", seqNo);
    }
}
