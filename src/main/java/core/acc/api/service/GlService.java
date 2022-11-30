package core.acc.api.service;

import core.acc.api.entity.Gl;
import core.acc.api.entity.GlKey;
import core.acc.api.entity.VDescription;
import core.acc.api.entity.VRef;
import core.acc.api.model.ReturnObject;

import java.util.List;

public interface GlService {
    Gl save(Gl gl) throws Exception;

    ReturnObject save(List<Gl> gl) throws Exception;

    Gl findByCode(GlKey key);

    boolean delete(GlKey key);

    List<VDescription> getDescription(String str, String compCode);

    List<VRef> getReference(String str, String compCode);

    List<Gl> searchJournal(String fromDate, String toDate, String vouNo, String description, String reference, String compCode,Integer macId);

    List<Gl> getJournal(String glVouNo,String compCode);


}
