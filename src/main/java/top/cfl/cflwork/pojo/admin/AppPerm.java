package top.cfl.cflwork.pojo.admin;

import top.cfl.cflwork.pojo.Pager;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Transient;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
@ApiModel("app端菜单权限")
public class AppPerm{

    @ApiModelProperty("id")
    private String id;
    @ApiModelProperty("学校权限id")
    private String schoolPermId;
    @ApiModelProperty("排序，从小到大")
    private Integer sort;
    @ApiModelProperty("app端显示的名称")
    private String appName;
    @ApiModelProperty("父id")
    private String parentId;
    @ApiModelProperty("分组名称")
    private String groupName;
    @ApiModelProperty("属于哪个app,0:原生教师端,1:小程序教师端,2:原生家长端,3:小程序家长端")
    private Integer whatApp;
    @ApiModelProperty("app图标")
    private String appIcon;
    @ApiModelProperty("唯一标识符")
    private String identify;
    //分页排序等
    @Transient
    @NotNull(message = "pager不能为空")
    @Valid
    private Pager pager;
    private List<AppPerm> children;
    private String schoolId;
    private String teacherId;
}
