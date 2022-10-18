package core.acc.api.dao;

import core.acc.api.entity.Gl;
import core.acc.api.entity.VGl;

import java.util.List;

public interface GlDao {
    Gl save(Gl gl) throws Exception;

    Gl findByCode(String glCode);

    boolean delete(Gl gl);

    List<VGl> search(String fromDate, String toDate, String desp, String srcAcc,
                     String acc, String curCode, String reference, String dept,
                     String retNo, String compCode, String tranSource,
                     String glVouNo, String traderCode);


    void deleteGl(String vouNo, String tranSource);
}
