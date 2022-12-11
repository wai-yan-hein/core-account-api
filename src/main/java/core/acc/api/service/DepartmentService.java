package core.acc.api.service;

import core.acc.api.entity.Department;
import core.acc.api.entity.DepartmentKey;

import java.util.List;

public interface DepartmentService {

    Department save(Department dept);

    Department findById(DepartmentKey key);

    List<Department> search(String code, String name,
                            String compCode, String userCode, String parentId, boolean active);

    List<Department> getDepartmentTree(String compCode);

    int delete(String code);

    List<Department> findAll(String compCode);

    List<Department> findAll();

    String getDepartment(Integer deptId);
}
