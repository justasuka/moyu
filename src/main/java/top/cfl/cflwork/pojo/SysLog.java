package top.cfl.cflwork.pojo;

import top.cfl.cflwork.pojo.Pager;
import lombok.Data;
import org.springframework.data.annotation.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.Valid;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModel;


@Data
@ApiModel("系统日志")
public class SysLog{

    @ApiModelProperty("编号")
    private String id;
    @ApiModelProperty("用户id")
    private String userId;
    @ApiModelProperty("用户名")
    private String username;
    @ApiModelProperty("用户操作")
    private String operation;
    @ApiModelProperty("响应时间")
    private Integer time;
    @ApiModelProperty("请求方法")
    private String method;
    @ApiModelProperty("请求参数")
    private String params;
    @ApiModelProperty("IP地址")
    private String ip;
    @ApiModelProperty("创建时间")
    private String createTime;
    //分页排序等
    @Transient
    @NotNull(message = "pager不能为空")
    @Valid
    private Pager pager;
}
