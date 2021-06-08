package top.cfl.cflwork.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.cfl.cflwork.pojo.SysFile;
import top.cfl.cflwork.pojo.ExcelExport;
@Mapper
public interface SysFileDao {
    List<SysFile> findSysFileListByCondition(SysFile sysFile);

    List<SysFile> findSysFileAllList(SysFile sysFile);

    long findSysFileCountByCondition(SysFile sysFile);

    SysFile findOneSysFileByCondition(SysFile sysFile);

    SysFile findSysFileById(@Param("id") String id);

    void saveSysFile(SysFile sysFile);

    void updateSysFile(SysFile sysFile);

    void deleteSysFile(@Param("id") String id);

    void deleteSysFileByCondition(SysFile sysFile);

    void batchSaveSysFile(List<SysFile> sysFiles);

    void batchDeleteSysFile(@Param("rowData") String rowData[]);

    void batchUpdateSysFile(SysFile sysFile);

    List<ExcelExport> getSysFileTableField();

    String getSysFileChildrenList(@Param("id")String id);

    void updateSysFileWithOutUpdatetime(SysFile sysFile);

    List<SysFile> findSysFileAllbyPath(@Param("path")String path);

    List<SysFile> findSysFileAllWithoutStatus(@Param("path")String path);

    List<SysFile> findSysFileOpenTime();

    List<SysFile> findSysFileListByRole(SysFile sysFile);

    long findSysFileCountByRole(SysFile sysFile);

    long findSysFileCountByDate(Integer num);

    List<Map<String,Object>>  findSysFileCountByMonth();

    List<SysFile> findSysFileListByConditionRecycleBin(SysFile sysFile);

    long findCountByConditionRecycleBin(SysFile sysFile);

    List<SysFile> findSysFileListByINStatus(SysFile sysFile);

    long findSysFileCountByINStatus(SysFile sysFile);

    List<SysFile> findSysFileListByRecycleBinRole(SysFile sysFile);

    long findCountByRecycleBinRole(SysFile sysFile);
}
