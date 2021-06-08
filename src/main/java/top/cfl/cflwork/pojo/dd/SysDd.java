package top.cfl.cflwork.pojo.dd;

import top.cfl.cflwork.pojo.Pager;
import lombok.Data;
import org.springframework.data.annotation.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.Valid;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModel;
import cn.afterturn.easypoi.excel.annotation.Excel;

@Data
@ApiModel("字典表")
public class SysDd{

    @ApiModelProperty("编号")
    @Excel(name = "编号")
    private String id;
    @ApiModelProperty("名称")
    @Excel(name = "名称")
    private String name;
    @ApiModelProperty("描述")
    @Excel(name = "描述")
    private String remark;
    @ApiModelProperty("类型编号")
    @Excel(name = "类型编号")
    private String typeId;
    @ApiModelProperty("类型编码")
    @Excel(name = "类型编码")
    private String typeCode;
    @ApiModelProperty("创建时间")
    @Excel(name = "创建时间")
    private String createTime;
    @ApiModelProperty("修改时间")
    @Excel(name = "修改时间")
    private String updateTime;
    //分页排序等
    @Transient
    @NotNull(message = "pager不能为空")
    @Valid
    private Pager pager;
    //多选数据
    @Transient
    private String[] rowData;
    @ApiModelProperty("类型名称")
    @Transient
    private String typeName;
}
