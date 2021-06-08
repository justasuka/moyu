package top.cfl.cflwork.dao.admin.sysPerm;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import top.cfl.cflwork.pojo.admin.SysPerm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysPermDao {
    List<SysPerm> findSysPermListByCondition(SysPerm sysPerm);

    long findSysPermCountByCondition(SysPerm sysPerm);

    SysPerm findSysPermById(String id);

    void saveSysPerm(SysPerm sysPerm);

    void updateSysPerm(SysPerm sysPerm);

    void deleteSysPerm(String id);

    void batchSaveSysPerm(List<SysPerm> sysPerms);

    List<SysPerm> findRootSysPerm();

    List<SysPerm> findSysPermByPId(String id);

    List<SysPerm> findAdminRootSysPermMenu(String pId);

    List<SysPerm> findAdminSysPermMenuByPId(@Param("pId") String pId, @Param("adminId") String adminId);

    List<SysPerm> findSysPermsByPId(@Param("pId") String pId);

    List<String> findSysPermChecked(String roleId);

    List<SysPerm> findAminTreeMenuV2(@Param("adminId") String adminId);

}
