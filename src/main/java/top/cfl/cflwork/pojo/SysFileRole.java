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
@ApiModel("角色与文件（夹）关系表")
public class SysFileRole{

    @ApiModelProperty("id")
    @Excel(name = "id")
    private String id;
    @ApiModelProperty("角色id")
    @Excel(name = "角色id")
    private String sysRoleId;
    @ApiModelProperty("文件id")
    @Excel(name = "文件id")
    private String sysFileId;
    @ApiModelProperty("sysFileParentId")
    @Excel(name = "sysFileParentId")
    private String sysFileParentId;
    //分页排序等
    @Transient
    @NotNull(message = "pager不能为空")
    @Valid
    private Pager pager;
    //多选数据
    @Transient
    private String[] rowData;
}
