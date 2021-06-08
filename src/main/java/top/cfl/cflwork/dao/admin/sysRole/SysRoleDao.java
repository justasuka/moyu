package top.cfl.cflwork.dao.admin.sysRole;

import top.cfl.cflwork.pojo.admin.SysRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysRoleDao {
    List<SysRole> findSysRoleListByCondition(SysRole sysRole);

    long findSysRoleCountByCondition(SysRole sysRole);

    SysRole findSysRoleById(String id);

    void saveSysRole(SysRole sysRole);

    void updateSysRole(SysRole sysRole);

    void deleteSysRole(String id);

    void batchSaveSysRole(List<SysRole> sysRoles);

    List<SysRole> findRoleByAdminId(String adminId);


}
