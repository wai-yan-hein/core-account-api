package core.acc.api.dao;

import core.acc.api.entity.Department;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DepartmentDaoImpl extends AbstractDao<String, Department> implements DepartmentDao {

    @Override
    public Department save(Department dept) {
        persist(dept);
        return dept;
    }

    @Override
    public Department findById(String id) {
        return getByKey(id);
    }

    @Override
    public List<Department> search(String code, String name, String compCode,
                                   String usrCode, String parentId, boolean active) {
        String strSql = "select o from Department o ";
        String strFilter = "";

        if (!code.equals("-")) {
            strFilter = "o.deptCode like '" + code + "%'";
        }

        if (!name.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.deptName like '%" + name + "%'";
            } else {
                strFilter = strFilter + " and o.deptName like '%" + name + "%'";
            }
        }

        if (!compCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.compCode = " + compCode;
            } else {
                strFilter = strFilter + " and o.compCode = " + compCode;
            }
        }

        if (!usrCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.usrCode like '" + usrCode + "%'";
            } else {
                strFilter = strFilter + " and o.usrCode like '" + usrCode + "%'";
            }
        }
        if (!parentId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.parentDept like '" + parentId + "%'";
            } else {
                strFilter = strFilter + " and o.parentDept like '" + parentId + "%'";
            }
        }

        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
        }
        if (active) {
            strSql += " and o.active = true";
        }
        strSql = strSql + " order by o.deptName";

        return findHSQL(strSql);
    }

    @Override
    public List<Department> getDepartmentTree(String compCode) {
        String hsql = "select o from Department o where  o.parentDept = '#' and o.compCode = '" + compCode + "'";
        List<Department> departments = findHSQL(hsql);
        for (Department dep : departments) {
            getChild(dep, compCode);
        }
        return departments;
    }

    private void getChild(Department parent, String compCode) {
        String hsql = "select o from Department o where o.parentDept = '" + parent.getDeptCode()
                + "' and o.compCode = '" + compCode + "'";
        List<Department> departments = findHSQL(hsql);
        parent.setChild(departments);
        if (!departments.isEmpty()) {
            for (Department child : departments) {
                getChild(child, compCode);
            }
        }
    }

    @Override
    public int delete(String code) {
        String strSql = "delete from Department o where o.deptCode = '" + code + "'";
        return execUpdateOrDelete(strSql);
    }

    @Override
    public List<Department> findAll(String compCode) {
        String hsql = "select o from Department o where o.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }
}

