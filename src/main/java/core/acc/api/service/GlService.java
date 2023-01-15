package core.acc.api.service;

import core.acc.api.entity.Gl;
import core.acc.api.entity.GlKey;
import core.acc.api.entity.VDescription;
import core.acc.api.entity.VRef;
import core.acc.api.model.ReturnObject;

import java.util.Date;
import java.util.List;

public interface GlService {
    Gl save(Gl gl, boolean backup) throws Exception;

    ReturnObject save(List<Gl> gl) throws Exception;

    Gl findByCode(GlKey key);

    boolean delete(GlKey key,String modifyBy);

    List<VDescription> getDescription(String str, String compCode);

    List<VRef> getReference(String str, String compCode);

    List<Gl> searchJournal(String fromDate, String toDate, String vouNo, String description, String reference, String compCode, Integer macId);

    List<Gl> searchVoucher(String fromDate, String toDate, String vouNo, String description, String reference,String refNo, String compCode, Integer macId);

    boolean deleteVoucher(String glVouNo, String compCode, String modifyBy);

    List<Gl> getJournal(String glVouNo, String compCode);
    List<Gl> getVoucher(String glVouNo, String compCode);


    List<Gl> getTranSource(String compCode);

    List<Gl> unUpload(String syncDate);

    Date getMaxDate();

    List<Gl> search(String updatedDate, String deptCode);


    void deleteGl(String vouNo, String tranSource);
    void deleteGl(String vouNo, String tranSource,String srcAcc);

}
