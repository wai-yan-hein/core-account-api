package core.acc.api.dao;


import core.acc.api.entity.Gl;
import core.acc.api.entity.GlKey;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GlDaoImpl extends AbstractDao<GlKey, Gl> implements GlDao {

    @Override
    public Gl save(Gl gl) throws Exception {
        persist(gl);
        return gl;
    }

    @Override
    public Gl findByCode(GlKey key) {
        return getByKey(key);
    }

    @Override
    public boolean delete(GlKey key) {
        String sql = "delete from gl where gl_code = '" + key.getGlCode() + "' and comp_code ='" + key.getCompCode() + "'";
        execSQL(sql);
        return true;
    }


    @Override
    public void deleteGl(String vouNo, String tranSource) {
        String sql = "delete from gl where ref_no ='" + vouNo + "' and tran_source='" + tranSource + "'";
        execSQL(sql);
    }
}
