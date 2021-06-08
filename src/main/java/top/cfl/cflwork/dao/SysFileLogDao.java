package top.cfl.cflwork.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.cfl.cflwork.pojo.SysFileLog;
import top.cfl.cflwork.pojo.ExcelExport;
@Mapper
public interface SysFileLogDao {
    List<SysFileLog> findSysFileLogListByCondition(SysFileLog sysFileLog);

    List<SysFileLog> findSysFileLogAllList(SysFileLog sysFileLog);

    long findSysFileLogCountByCondition(SysFileLog sysFileLog);

    SysFileLog findOneSysFileLogByCondition(SysFileLog sysFileLog);

    SysFileLog findSysFileLogById(@Param("id") String id);

    void saveSysFileLog(SysFileLog sysFileLog);

    void updateSysFileLog(SysFileLog sysFileLog);

    void deleteSysFileLog(@Param("id") String id);

    void deleteSysFileLogByCondition(SysFileLog sysFileLog);

    void batchSaveSysFileLog(List<SysFileLog> sysFileLogs);

    void batchDeleteSysFileLog(@Param("rowData") String rowData[]);

    void batchUpdateSysFileLog(SysFileLog sysFileLog);

    List<ExcelExport> getSysFileLogTableField();

    List<SysFileLog> findSysFileLogByFileId(SysFileLog sysFileLog);

    long findCountSysFileLogByFileId(SysFileLog sysFileLog);

    void delfileLogWhitoutID();

    List<SysFileLog> findSysFileLogByWithPath(SysFileLog sysFileLog);
}
