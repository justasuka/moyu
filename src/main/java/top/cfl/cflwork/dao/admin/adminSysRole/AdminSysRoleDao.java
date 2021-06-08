package top.cfl.cflwork.dao.admin.adminSysRole;

import top.cfl.cflwork.pojo.admin.AdminSysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminSysRoleDao {
    List<AdminSysRole> findAdminSysRoleListByCondition(AdminSysRole adminSysRole);

    AdminSysRole findOneAdminSysRoleByCondition(AdminSysRole adminSysRole);

    long findAdminSysRoleCountByCondition(AdminSysRole adminSysRole);

    AdminSysRole findAdminSysRoleById(@Param("id") String id);

    void saveAdminSysRole(AdminSysRole adminSysRole);

    void updateAdminSysRole(AdminSysRole adminSysRole);

    void deleteAdminSysRole(@Param("id") String id);

    void deleteAdminSysRoleByCondition(AdminSysRole adminSysRole);

    void batchSaveAdminSysRole(List<AdminSysRole> adminSysRoles);

    void deleteAdminSysRoleByAdminId(@Param("adminId") String adminId);

    void deleteAdminSysRoleByRoleId(@Param("roleId") String roleId);
}
