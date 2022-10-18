package core.acc.api.dao;

import core.acc.api.entity.Department;

import java.util.List;

public interface DepartmentDao {

    Department save(Department dept);

    Department findById(String id);

    List<Department> search(String code, String name, String compCode,
                            String usrCode, String parentId, boolean active);

    List<Department> getDepartmentTree(String compCode);

    int delete(String code);

    List<Department> findAll(String compCode);
}
