package top.cfl.cflwork.pojo;

import lombok.Data;

import java.util.List;

/**
 * @author cfl
 * @date 2020-04-08 15:12
 * @email 275300091@qq.com
 */
@Data
public class ExcelParam<T> {

    private T data;
    private List<ExcelExport> excelExportList;
}
