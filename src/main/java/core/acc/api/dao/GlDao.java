package core.acc.api.dao;

import core.acc.api.entity.Gl;
import core.acc.api.entity.GlKey;
import core.acc.api.entity.VDescription;
import core.acc.api.entity.VRef;

import java.util.Date;
import java.util.List;

public interface GlDao {
    Gl save(Gl gl) throws Exception;

    Gl findByCode(GlKey key);

    Gl findWithSql(GlKey key);

    boolean delete(GlKey key);

    void deleteGl(String vouNo, String tranSource);

    List<VDescription> getDescription(String str, String compCode);

    List<VRef> getReference(String str, String compCode);

    List<Gl> searchJournal(String fromDate, String toDate, String vouNo, String description, String reference, String compCode, Integer macId);
    List<Gl> searchVoucher(String fromDate, String toDate, String vouNo, String description, String reference,String refNo, String compCode, Integer macId);

    boolean deleteVoucher(String glVouNo,String compCode);
    void deleteGl(String vouNo, String tranSource,String srcAcc);

    List<Gl> getJournal(String glVouNo, String compCode);
    List<Gl> getVoucher(String glVouNo, String compCode);

    List<Gl> getTranSource(String compCode);

    List<Gl> unUpload(String syncDate);

    Date getMaxDate();

    List<Gl> search(String updatedDate,String deptCode);
    List<Gl> search(String vouNo, String tranSource, String compCode);

    void truncate(GlKey key);

}
