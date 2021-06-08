package top.cfl.cflwork.dao.dd;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.cfl.cflwork.pojo.dd.SysDdtype;

@Mapper
public interface SysDdtypeDao {
    List<SysDdtype> findSysDdtypeListByCondition(SysDdtype sysDdtype);

    long findSysDdtypeCountByCondition(SysDdtype sysDdtype);

    SysDdtype findOneSysDdtypeByCondition(SysDdtype sysDdtype);

    SysDdtype findSysDdtypeById(@Param("id") String id);

    void saveSysDdtype(SysDdtype sysDdtype);

    void updateSysDdtype(SysDdtype sysDdtype);

    void deleteSysDdtype(@Param("id") String id);

    void deleteSysDdtypeByCondition(SysDdtype sysDdtype);

    void batchSaveSysDdtype(List<SysDdtype> sysDdtypes);
}
