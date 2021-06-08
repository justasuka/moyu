package top.cfl.cflwork.pojo.admin;

import io.swagger.annotations.ApiModelProperty;
import top.cfl.cflwork.pojo.Pager;
import lombok.Data;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
*
*系统角色
*
*/
@Data
public class SysRole{

    private String id;
    private String title;//角色名称
    @ApiModelProperty("描述")
    private String remark;
    @ApiModelProperty("创建时间")
    private String createTime;
    @ApiModelProperty("修改时间")
    private String updateTime;
    //分页排序等
    @Transient
    @NotNull
    private Pager pager;
    @Transient
    private List<SysRole> children;
}
