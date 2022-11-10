package core.acc.api.dao;

import core.acc.api.entity.Gl;
import core.acc.api.entity.GlKey;

import java.util.List;

public interface GlDao {
    Gl save(Gl gl) throws Exception;

    Gl findByCode(GlKey key);

    boolean delete(GlKey key);

    void deleteGl(String vouNo, String tranSource);
}
