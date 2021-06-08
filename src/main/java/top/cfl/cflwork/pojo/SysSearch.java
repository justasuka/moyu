package top.cfl.cflwork.pojo;

import top.cfl.cflwork.pojo.Pager;
import lombok.Data;
import org.springframework.data.annotation.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.Valid;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModel;
import cn.afterturn.easypoi.excel.annotation.Excel;

@Data
@ApiModel("")
public class SysSearch{

    @ApiModelProperty("id")
    @Excel(name = "id")
    private String id;
    @ApiModelProperty("搜索")
    @Excel(name = "搜索")
    private String searchContent;
    @ApiModelProperty("次数")
    @Excel(name = "次数")
    private String count;
    @ApiModelProperty("ID")
    @Excel(name = "ID")
    private String adminId;
    @ApiModelProperty("name")
    @Excel(name = "name")
    private String adminName;
    @ApiModelProperty("搜索时间")
    @Excel(name = "搜索时间")
    private String createTime;
    //分页排序等
    @Transient
    @NotNull(message = "pager不能为空")
    @Valid
    private Pager pager;
    //多选数据
    @Transient
    private String[] rowData;
}
