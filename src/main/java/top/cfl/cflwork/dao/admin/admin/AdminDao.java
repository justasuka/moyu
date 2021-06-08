package top.cfl.cflwork.dao.admin.admin;

import top.cfl.cflwork.pojo.ExcelExport;
import top.cfl.cflwork.pojo.admin.Admin;
import top.cfl.cflwork.pojo.admin.SysPerm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminDao {
    List<Admin> findAdminListByCondition(Admin admin);

    Admin findOneAdminByCondition(Admin admin);

    long findAdminCountByCondition(Admin admin);

    Admin findAdminById(@Param("id") String id);

    void saveAdmin(Admin admin);

    void updateAdmin(Admin admin);

    void deleteAdmin(@Param("id") String id);

    void deleteAdminByCondition(Admin admin);

    void batchSaveAdmin(List<Admin> admins);

    void batchUpdateAdmin(Admin admin);

    List<ExcelExport> getAdminTableField();

    List<String> findCheckedRoloIdsByAdminId(@Param("adminId") String adminId);

    List<SysPerm> findSysFuncPermsByAdminId(@Param("adminId") String adminId);

    List<Admin> findSysDepartmentList();

    List<Admin> findAdminListById(String[] id);

}
