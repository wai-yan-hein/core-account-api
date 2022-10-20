package core.acc.api.dao;


import core.acc.api.entity.Gl;
import core.acc.api.entity.GlKey;
import core.acc.api.entity.VGl;
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
    public boolean delete(Gl gl) {
        String sql = "delete from gl where gl_code = '" + gl.getKey().getGlCode() + "'";
        execSQL(sql);
        return true;
    }

    @Override
    public List<VGl> search(String fromDate, String toDate, String desp, String srcAcc, String acc, String curCode, String reference, String deptCode, String retNo, String compCode, String tranSource, String glVouNo, String traderCode) {
        String hsql = "select o from VGl o where date(o.glDate) between '" + fromDate + "' and '" + toDate + "'";
        String strFilter = "";

        if (!tranSource.equals("-")) {
            strFilter = " and o.tranSource = '" + tranSource + "'";
        }
        if (!desp.equals("-")) {
            strFilter = strFilter + " and o.description like '" + desp + "%'";
        }

        if (!reference.equals("-")) {
            strFilter = strFilter + " and o.reference like '" + reference + "%'";
        }

        if (!srcAcc.equals("-")) {
            strFilter = strFilter + " and (o.accCode = '" + srcAcc + "' or o.sourceAcId = '" + srcAcc + "')";
        }
        if (!acc.equals("-")) {
            strFilter = strFilter + " and (o.accCode in (" + acc + ") or o.sourceAcId in (" + acc + "))";
        }
        if (!retNo.equals("-")) {
            strFilter = strFilter + " and o.refNo =" + retNo + "'";
        }
        if (!traderCode.equals("-")) {
            strFilter = strFilter + " and o.traderCode = '" + traderCode + "'";
        }

        if (!compCode.equals("-")) {
            strFilter = strFilter + " and o.compCode = '" + compCode + "'";
        }
        if (!glVouNo.equals("-")) {
            strFilter = strFilter + " and o.vouNo = '" + glVouNo + "'";

        }
        if (!curCode.equals("-")) {
            strFilter = strFilter + " and o.curCode = '" + curCode + "'";
        }
        hsql = hsql + strFilter + " order by o.glDate,o.tranSource,o.glCode";
        return getSession().createQuery(hsql, VGl.class).list();
    }

    @Override
    public void deleteGl(String vouNo, String tranSource) {
        String sql = "delete from gl where ref_no ='" + vouNo + "' and tran_source='" + tranSource + "'";
        execSQL(sql);
    }
}
