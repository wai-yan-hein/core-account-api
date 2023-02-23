package core.acc.api.service;

import core.acc.api.common.Util1;
import core.acc.api.dao.GlDao;
import core.acc.api.dao.GlLogDao;
import core.acc.api.entity.*;
import core.acc.api.model.ReturnObject;
import core.acc.api.model.VDescription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Transactional
public class GlServiceImpl implements GlService {
    @Autowired
    private GlDao glDao;
    @Autowired
    private GlLogDao logDao;
    @Autowired
    private SeqTableService seqService;

    @Override
    public Gl save(Gl gl, boolean backup) throws Exception {
        String updatedBy = gl.getModifyBy();
        if (Util1.isNull(gl.getKey().getGlCode())) {
            Integer macId = gl.getMacId();
            String compCode = gl.getKey().getCompCode();
            String glCode = getGLCode(gl.getGlDate(), gl.getKey().getDeptId(), macId, compCode);
            GlKey key = gl.getKey();
            key.setGlCode(glCode);
            Gl valid = findByCode(key);
            if (Objects.isNull(valid)) {
                gl.getKey().setGlCode(glCode);
            } else {
                log.info(valid.getKey().getGlCode());
                throw new IllegalStateException("Duplication Occur in Gl");
            }
        } else {
            if (backup) backupGl(gl.getKey(), updatedBy, false);
        }
        if (gl.getDelList() != null) {
            for (GlKey key : gl.getDelList()) {
                glDao.delete(key, updatedBy);
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
            Date glDate = tmp.getGlDate();
            boolean delete = tmp.isDeleted();
            String glVouNo = tmp.getGlVouNo();
            if (tmp.isEdit()) {
                backupGl(tmp.getKey(), tmp.getModifyBy(), false);
            }
            switch (tranSource) {
                case "GV", "DR", "CR" -> {
                    if (Util1.isNullOrEmpty(glVouNo)) {
                        glVouNo = getVouNo(glDate, tmp.getKey().getDeptId(), tmp.getMacId(), tmp.getKey().getCompCode(), tranSource);
                    }
                }
                default -> glDao.deleteInvVoucher(vouNo, tranSource, compCode);
            }
            if (!delete) {
                for (Gl gl : glList) {
                    //convert to uni code
                    gl.setDescription(Util1.convertToUniCode(gl.getDescription()));
                    gl.setReference(Util1.convertToUniCode(gl.getReference()));
                    if (gl.getSrcAccCode() != null) {
                        if (Util1.isMultiCur()) {
                            if (gl.isCash()) {
                                gl.setSrcAccCode(Util1.getProperty(gl.getCurCode()));
                            }
                        }
                        double amt = Util1.getDouble(gl.getDrAmt()) + Util1.getDouble(gl.getCrAmt());
                        if (amt > 0) {
                            gl.setGlVouNo(glVouNo);
                            save(gl, false);
                        }
                    }
                }
            }
            ro.setGlVouNo(glVouNo);
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
    public boolean delete(GlKey key, String modifyBy) {
        return glDao.delete(key, modifyBy);
    }


    @Override
    public List<VDescription> getDescription(String str, String compCode) {
        return glDao.getDescription(str, compCode);
    }

    @Override
    public List<VDescription> getReference(String str, String compCode) {
        return glDao.getReference(str, compCode);
    }

    @Override
    public List<VDescription> getBatchNo(String str, String compCode) {
        return glDao.getBatchNo(str, compCode);
    }

    @Override
    public List<Gl> searchJournal(String fromDate, String toDate, String vouNo, String description, String reference, String compCode, Integer macId) {
        return glDao.searchJournal(fromDate, toDate, vouNo, description, reference, compCode, macId);
    }

    @Override
    public List<Gl> searchVoucher(String fromDate, String toDate, String vouNo, String description, String reference, String refNo, String compCode, Integer macId) {
        return glDao.searchVoucher(fromDate, toDate, vouNo, description, reference, refNo, compCode, macId);
    }

    @Override
    public boolean deleteVoucher(String glVouNo, String compCode, String modifyBy) {
        List<Gl> list = getJournal(glVouNo, compCode);
        for (Gl gl : list) {
            backupGl(gl.getKey(), modifyBy, true);
        }
        return glDao.deleteVoucher(glVouNo, compCode);
    }

    @Override
    public List<Gl> getJournal(String glVouNo, String compCode) {
        return glDao.getJournal(glVouNo, compCode);
    }

    @Override
    public List<Gl> getVoucher(String glVouNo, String compCode) {
        return glDao.getVoucher(glVouNo, compCode);

    }

    @Override
    public List<Gl> getTranSource(String compCode) {
        return glDao.getTranSource(compCode);
    }

    @Override
    public List<Gl> unUpload(String syncDate) {
        return glDao.unUpload(syncDate);
    }

    @Override
    public Date getMaxDate() {
        return glDao.getMaxDate();
    }

    @Override
    public List<Gl> search(String updatedDate, String deptCode) {
        return glDao.search(updatedDate, deptCode);
    }


    @Override
    public void deleteInvVoucher(String refNo, String tranSource, String compCode) {
        glDao.deleteInvVoucher(refNo, tranSource, compCode);
    }

    @Override
    public void deleteVoucherByAcc(String vouNo, String tranSource, String srcAcc) {
        glDao.deleteVoucherByAcc(vouNo, tranSource, srcAcc);
    }

    @Override
    public List<String> shootTri() {
        return glDao.shootTri();
    }


    private void backupGl(GlKey key, String updatedBy, boolean del) {
        if (key != null) {
            Gl gl = glDao.findWithSql(key);
            if (gl != null) {
                Integer deptId = gl.getKey().getDeptId();
                String compCode = gl.getKey().getCompCode();
                Integer macId = gl.getMacId();
                String type = gl.getTranSource();
                GlLog l = new GlLog();
                GlLogKey logKey = new GlLogKey();
                logKey.setLogGlCode(getGlLogCode(deptId, macId, compCode));
                logKey.setDeptId(deptId);
                logKey.setCompCode(compCode);
                l.setKey(logKey);
                l.setTraderCode(gl.getTraderCode());
                l.setTranSource(gl.getTranSource());
                l.setSrcAccCode(gl.getSrcAccCode());
                l.setAccCode(gl.getAccCode());
                l.setCrAmt(gl.getCrAmt());
                l.setDrAmt(gl.getDrAmt());
                l.setCreatedBy(gl.getCreatedBy());
                l.setCreatedDate(gl.getCreatedDate());
                l.setCurCode(gl.getCurCode());
                l.setDeptCode(gl.getDeptCode());
                l.setDescription(gl.getDescription());
                l.setGlCode(gl.getKey().getGlCode());
                l.setGlDate(gl.getGlDate());
                l.setGlVouNo(gl.getGlVouNo());
                l.setReference(gl.getReference());
                l.setRefNo(gl.getRefNo());
                l.setLogDate(Util1.getTodayDate());
                l.setLogMac(macId);
                l.setMacId(gl.getMacId());
                l.setLogStatus(del ? "DEL-" + type : "EDIT-" + type);
                l.setLogUser(updatedBy);
                logDao.save(l);
            }
        }
    }

    private String getGLCode(Date date, Integer deptId, Integer macId, String compCode) {
        String period = Util1.toDateStr(date, "ddMMyy");
        int seqNo = seqService.getSequence(macId, "GL", period, compCode);
        String deptCode = String.format("%0" + 2 + "d", deptId) + "-";
        return deptCode + String.format("%0" + 2 + "d", macId) + period + String.format("%0" + 5 + "d", seqNo);
    }

    private String getGlLogCode(Integer deptId, Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqService.getSequence(macId, "GL-LOG", period, compCode);
        String deptCode = String.format("%0" + 2 + "d", deptId) + "-";
        return "L-" + deptCode + String.format("%0" + 2 + "d", macId) + period + String.format("%0" + 5 + "d", seqNo);
    }

    private String getVouNo(Date glDate, Integer deptId, Integer macId, String compCode, String type) {
        String period = Util1.toDateStr(glDate, "MMyy");
        int seqNo = seqService.getSequence(macId, type, period, compCode);
        String deptCode = String.format("%0" + 2 + "d", deptId);
        return type + deptCode + String.format("%0" + 2 + "d", macId) + "-" + period + "-" + String.format("%0" + 5 + "d", seqNo);
    }

}
