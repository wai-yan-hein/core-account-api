package core.acc.api.dao;

import core.acc.api.entity.Department;
import core.acc.api.entity.DepartmentKey;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class DepartmentDaoImpl extends AbstractDao<DepartmentKey, Department> implements DepartmentDao {

    @Override
    public Department save(Department dept) {
        saveOrUpdate(dept, dept.getKey());
        return dept;
    }

    @Override
    public Department findById(DepartmentKey key) {
        return getByKey(key);
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
                strFilter = "o.key.compCode = '" + compCode + "'";
            } else {
                strFilter = strFilter + " and o.key.compCode = '" + compCode + "'";
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
        String hsql = "select o from Department o where o.parentDept = '#' and o.key.compCode = '" + compCode + "'\n" +
                "and o.active =true and o.deleted = false";
        List<Department> departments = findHSQL(hsql);
        for (Department dep : departments) {
            getChild(dep, compCode);
        }
        return departments;
    }

    private void getChild(Department parent, String compCode) {
        String hsql = "select o from Department o where o.parentDept = '" + parent.getKey().getDeptCode() + "' and o.key.compCode = '" + compCode + "'\n" +
                "and o.active =true and o.deleted = false";
        List<Department> departments = findHSQL(hsql);
        parent.setChild(departments);
        if (!departments.isEmpty()) {
            for (Department child : departments) {
                getChild(child, compCode);
            }
        }
    }

    @Override
    public boolean delete(DepartmentKey key) {
        Department dep = findById(key);
        dep.setDeleted(true);
        dep.setUpdatedDt(LocalDateTime.now());
        update(dep);
        return true;
    }

    @Override
    public List<Department> findAll(String compCode) {
        String hsql = "select o from Department o where o.key.compCode ='" + compCode + "' and o.deleted = false and o.active = true";
        return findHSQL(hsql);
    }

    @Override
    public List<Department> findAllActive(String compCode) {
        return findHSQL("select o from Department o where o.key.compCode ='" + compCode + "' and o.active =1 and o.deleted = false");
    }

    @Override
    public String getDepartment(Integer deptId) {
        List<Department> list = findHSQL("select o from Department o where o.mapDeptId = " + deptId);
        return list.isEmpty() ? null : list.get(0).getKey().getDeptCode();
    }

    @Override
    public List<Department> getUpdatedDepartment(LocalDateTime updatedDate) {
        String hsql = "select o from Department o where updatedDt>:updatedDate";
        return createQuery(hsql).setParameter("updatedDate", updatedDate).getResultList();
    }
}

