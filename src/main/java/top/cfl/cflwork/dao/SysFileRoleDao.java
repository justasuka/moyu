package top.cfl.cflwork.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.cfl.cflwork.pojo.SysFileRole;
import top.cfl.cflwork.pojo.ExcelExport;
@Mapper
public interface SysFileRoleDao {
    List<SysFileRole> findSysFileRoleListByCondition(SysFileRole sysFileRole);

    List<SysFileRole> findSysFileRoleAllList(SysFileRole sysFileRole);

    long findSysFileRoleCountByCondition(SysFileRole sysFileRole);

    SysFileRole findOneSysFileRoleByCondition(SysFileRole sysFileRole);

    SysFileRole findSysFileRoleById(@Param("id") String id);

    void saveSysFileRole(SysFileRole sysFileRole);

    void updateSysFileRole(SysFileRole sysFileRole);

    void deleteSysFileRole(@Param("id") String id);

    void deleteSysFileRoleByCondition(SysFileRole sysFileRole);

    void batchSaveSysFileRole(List<SysFileRole> sysFileRoles);

    void batchDeleteSysFileRole(@Param("rowData") String rowData[]);

    void batchUpdateSysFileRole(SysFileRole sysFileRole);

    List<ExcelExport> getSysFileRoleTableField();

    void deleteRoleByRoleId(@Param("roleId") String roleId);

    List<SysFileRole> findSysFileRoleByRoleId(@Param("rowData") String rowData[]);
}
