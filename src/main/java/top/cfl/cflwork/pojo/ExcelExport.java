package top.cfl.cflwork.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author cfl
 * @date 2020-04-08 11:54
 * @email 275300091@qq.com
 */
@Data
public class ExcelExport {

    @ApiModelProperty("导出名称")
    private String key;
    @ApiModelProperty("导出的名称")
    private String name;
    @ApiModelProperty("导出的新名称")
    private String newName;


}
