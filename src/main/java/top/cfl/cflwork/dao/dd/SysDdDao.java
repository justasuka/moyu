package top.cfl.cflwork.dao.dd;

import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.cfl.cflwork.pojo.dd.SysDd;

@Mapper
public interface SysDdDao {
    List<SysDd> findSysDdListByCondition(SysDd sysDd);

    List<SysDd> findSysDdAllList(SysDd sysDd);

    long findSysDdCountByCondition(SysDd sysDd);

    SysDd findOneSysDdByCondition(SysDd sysDd);

    SysDd findSysDdById(@Param("id") String id);

    void saveSysDd(SysDd sysDd);

    void updateSysDd(SysDd sysDd);

    void deleteSysDd(@Param("id") String id);

    void deleteSysDdByCondition(SysDd sysDd);

    void batchSaveSysDd(List<SysDd> sysDds);

    void batchDeleteSysDd(@Param("rowData") String rowData[]);

    void batchUpdateSysDd(SysDd sysDd);

    List<HashMap<String,String>> censysList(@Param("code")String code);
}
