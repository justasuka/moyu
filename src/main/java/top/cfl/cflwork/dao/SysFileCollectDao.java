package top.cfl.cflwork.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.cfl.cflwork.pojo.SysFileCollect;
import top.cfl.cflwork.pojo.ExcelExport;
@Mapper
public interface SysFileCollectDao {
    List<SysFileCollect> findSysFileCollectListByCondition(SysFileCollect sysFileCollect);

    List<SysFileCollect> findSysFileCollectAllList(SysFileCollect sysFileCollect);

    long findSysFileCollectCountByCondition(SysFileCollect sysFileCollect);

    SysFileCollect findOneSysFileCollectByCondition(SysFileCollect sysFileCollect);

    SysFileCollect findSysFileCollectById(@Param("id") String id);

    void saveSysFileCollect(SysFileCollect sysFileCollect);

    void updateSysFileCollect(SysFileCollect sysFileCollect);

    void deleteSysFileCollect(@Param("id") String id);

    void deleteSysFileCollectByCondition(SysFileCollect sysFileCollect);

    void batchSaveSysFileCollect(List<SysFileCollect> sysFileCollects);

    void batchDeleteSysFileCollect(@Param("rowData") String rowData[]);

    void batchUpdateSysFileCollect(SysFileCollect sysFileCollect);

    List<ExcelExport> getSysFileCollectTableField();

    long findSysFileCollectsCreateCount(SysFileCollect sysFileCollect);

}
