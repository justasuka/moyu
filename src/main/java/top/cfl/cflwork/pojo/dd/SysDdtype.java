package top.cfl.cflwork.pojo.dd;

import lombok.Data;
import org.springframework.data.annotation.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.Valid;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModel;
import top.cfl.cflwork.pojo.Pager;


@Data
@ApiModel("字典类型表")
public class SysDdtype{

    @ApiModelProperty("字典类型编号")
    private String id;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("code编码")
    private String code;
    @ApiModelProperty("创建时间")
    private String createTime;
    @ApiModelProperty("修改时间")
    private String updateTime;
    //分页排序等
    @Transient
    @NotNull(message = "pager不能为空")
    @Valid
    private Pager pager;
}
