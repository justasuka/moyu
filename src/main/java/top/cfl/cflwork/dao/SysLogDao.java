package top.cfl.cflwork.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.cfl.cflwork.pojo.SysLog;

import java.util.List;

@Mapper
public interface SysLogDao {
    List<SysLog> findSysLogListByCondition(SysLog sysLog);

    long findSysLogCountByCondition(SysLog sysLog);

    SysLog findOneSysLogByCondition(SysLog sysLog);

    SysLog findSysLogById(@Param("id") String id);

    void saveSysLog(SysLog sysLog);

    void updateSysLog(SysLog sysLog);

    void deleteSysLog(@Param("id") String id);

    void deleteSysLogByCondition(SysLog sysLog);

    void batchSaveSysLog(List<SysLog> sysLogs);

    void deleteManySysLog();

}
