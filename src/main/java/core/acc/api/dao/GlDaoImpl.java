package core.acc.api.dao;


import core.acc.api.entity.Gl;
import core.acc.api.entity.GlKey;
import core.acc.api.entity.VDescription;
import core.acc.api.entity.VRef;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
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

    @Override
    public List<VDescription> getDescription(String str, String compCode) {
        List<VDescription> list = new ArrayList<>();
        String sql = "select distinct description\n" +
                "from gl\n" +
                "where comp_code ='" + compCode + "'\n" +
                "and (description like '" + str + "%')\n" +
                "limit 20";
        try {
            ResultSet rs = getResultSet(sql);
            while (rs.next()) {
                VDescription v = new VDescription();
                v.setDescription(rs.getString("description"));
                list.add(v);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return list;
    }

    @Override
    public List<VRef> getReference(String str, String compCode) {
        List<VRef> list = new ArrayList<>();
        String sql = "select distinct reference\n" +
                "from gl\n" +
                "where comp_code ='" + compCode + "'\n" +
                "and (reference like '" + str + "%')\n" +
                "limit 20";
        try {
            ResultSet rs = getResultSet(sql);
            while (rs.next()) {
                VRef v = new VRef();
                v.setReference(rs.getString("reference"));
                list.add(v);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return list;
    }
}
