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
@ApiModel("用户收藏管理")
public class SysFileCollect{

    @ApiModelProperty("id")
    @Excel(name = "id")
    private String id;
    @ApiModelProperty("用户id")
    @Excel(name = "用户id")
    private String adminId;
    @ApiModelProperty("用户名称")
    @Excel(name = "用户名称")
    private String adminName;
    @ApiModelProperty("文件编码")
    @Excel(name = "文件编码")
    private String fileId;
    @ApiModelProperty("是否收藏；0=否，1=是")
    @Excel(name = "是否收藏；0=否，1=是")
    private String isCollect;
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
}
