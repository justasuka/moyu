package top.cfl.cflwork.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.cfl.cflwork.pojo.SysSearch;
import top.cfl.cflwork.pojo.ExcelExport;
@Mapper
public interface SysSearchDao {
    List<SysSearch> findSysSearchListByCondition(SysSearch sysSearch);

    List<SysSearch> findSysSearchAllList(SysSearch sysSearch);

    long findSysSearchCountByCondition(SysSearch sysSearch);

    SysSearch findOneSysSearchByCondition(SysSearch sysSearch);

    SysSearch findSysSearchById(@Param("id") String id);

    void saveSysSearch(SysSearch sysSearch);

    void updateSysSearch(SysSearch sysSearch);

    void deleteSysSearch(@Param("id") String id);

    void deleteSysSearchByCondition(SysSearch sysSearch);

    void batchSaveSysSearch(List<SysSearch> sysSearchs);

    void batchDeleteSysSearch(@Param("rowData") String rowData[]);

    void batchUpdateSysSearch(SysSearch sysSearch);

    List<ExcelExport> getSysSearchTableField();

    List<SysSearch> findSysSearchRecentlyList();

}
