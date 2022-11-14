package core.acc.api.dao;

import core.acc.api.entity.Gl;
import core.acc.api.entity.GlKey;
import core.acc.api.entity.VDescription;
import core.acc.api.entity.VRef;

import java.util.List;

public interface GlDao {
    Gl save(Gl gl) throws Exception;

    Gl findByCode(GlKey key);

    boolean delete(GlKey key);

    void deleteGl(String vouNo, String tranSource);

    List<VDescription> getDescription(String str, String compCode);

    List<VRef> getReference(String str, String compCode);
}
