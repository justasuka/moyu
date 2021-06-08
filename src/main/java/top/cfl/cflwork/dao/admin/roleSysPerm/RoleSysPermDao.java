package top.cfl.cflwork.dao.admin.roleSysPerm;

import top.cfl.cflwork.pojo.admin.RoleSysPerm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleSysPermDao {
    List<RoleSysPerm> findRoleSysPermListByCondition(RoleSysPerm roleSysPerm);

    long findRoleSysPermCountByCondition(RoleSysPerm roleSysPerm);

    RoleSysPerm findRoleSysPermById(String id);

    void saveRoleSysPerm(RoleSysPerm roleSysPerm);

    void updateRoleSysPerm(RoleSysPerm roleSysPerm);

    void deleteRoleSysPerm(String id);

    void batchSaveRoleSysPerm(List<RoleSysPerm> roleSysPerms);

    void deleteRoleSysPermByRoleId(@Param("roleId") String roleId);

    void deleteRoleSysPermByPermId(@Param("permId") String permId);
}
