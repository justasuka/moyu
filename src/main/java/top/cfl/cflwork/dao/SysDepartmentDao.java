package top.cfl.cflwork.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.cfl.cflwork.pojo.ExcelExport;
import top.cfl.cflwork.pojo.SysDepartment;

import java.util.List;

@Mapper
public interface SysDepartmentDao {
    List<SysDepartment> findSysDepartmentListByCondition(SysDepartment sysDepartment);

    List<SysDepartment> findSysDepartmentAllList(SysDepartment sysDepartment);

    long findSysDepartmentCountByCondition(SysDepartment sysDepartment);

    SysDepartment findOneSysDepartmentByCondition(SysDepartment sysDepartment);

    SysDepartment findSysDepartmentById(@Param("id") String id);

    void saveSysDepartment(SysDepartment sysDepartment);

    void updateSysDepartment(SysDepartment sysDepartment);

    void deleteSysDepartment(@Param("id") String id);

    void deleteSysDepartmentByCondition(SysDepartment sysDepartment);

    void batchSaveSysDepartment(List<SysDepartment> sysDepartments);

    void batchDeleteSysDepartment(@Param("rowData") String rowData[]);

    void batchUpdateSysDepartment(SysDepartment sysDepartment);

    List<ExcelExport> getSysDepartmentTableField();

    List<SysDepartment> findSysDepartmentByPId(@Param("id") String id);

    List<SysDepartment> findSysDepartmentNoParentAllList();

    List<SysDepartment> findSysDepartmentAllListByDep(SysDepartment sysDepartment);

    List<SysDepartment> findSysDepartmentListByIds(SysDepartment sysDepartment);
}
