package core.acc.api.dao;

import core.acc.api.entity.Department;
import core.acc.api.entity.DepartmentKey;

import java.util.List;

public interface DepartmentDao {

    Department save(Department dept);

    Department findById(DepartmentKey key);

    List<Department> search(String code, String name, String compCode,
                            String usrCode, String parentId, boolean active);

    List<Department> getDepartmentTree(String compCode);

    int delete(String code);

    List<Department> findAll(String compCode);
    List<Department> findAll();
    String getDepartment(Integer deptId);

}
