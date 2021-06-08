package top.cfl.cflwork.pojo.admin;

import top.cfl.cflwork.pojo.Pager;
import lombok.Data;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
*
*系统权限
*
*/
@Data
public class SysPerm implements Serializable {

    private static final long serialVersionUID = -5145979842498662704L;
    private String id;
    private String title;//权限名称
    private String identify;//唯一标识符
    private String icon;//图标
    private String parentId;//父id
    private String type;//0菜单,1按钮
    private String routeName;//路由名称,可为空
    private String urlPath;//除掉host外的绝对路径
    //分页排序等
    @Transient
    @NotNull
    private Pager pager;
    //额外字段
    @Transient
    private List<SysPerm> children;
    @Transient
    private Boolean autoBtn;
    @Transient
    private Integer sortNum;
    private String createTime;

}
