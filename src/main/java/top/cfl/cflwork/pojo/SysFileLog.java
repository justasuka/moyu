package top.cfl.cflwork.pojo;

import cn.hutool.core.util.ObjectUtil;
import top.cfl.cflwork.pojo.Pager;
import lombok.Data;
import org.springframework.data.annotation.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.Valid;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModel;
import cn.afterturn.easypoi.excel.annotation.Excel;

@Data
@ApiModel("文件操作日志")
public class SysFileLog{

    @ApiModelProperty("id")
    @Excel(name = "id")
    private String id;
    @ApiModelProperty("文件编码")
    @Excel(name = "文件编码")
    private String fileId;
    @ApiModelProperty("操作者id")
    @Excel(name = "操作者id")
    private String adminId;
    @ApiModelProperty("创建人名称")
    @Excel(name = "创建人名称")
    private String adminName;
    @ApiModelProperty("操作")
    @Excel(name = "操作")
    private String operation;
    @ApiModelProperty("对象（文件）")
    @Excel(name = "对象（文件）")
    private String target;
    @ApiModelProperty("内容（详情）")
    @Excel(name = "内容（详情）")
    private String comment;
    @ApiModelProperty("创建时间")
    @Excel(name = "创建时间")
    private String createTime;
    //分页排序等
    @Transient
    @NotNull(message = "pager不能为空")
    @Valid
    private Pager pager;
    //多选数据
    @Transient
    private String[] rowData;
    @Transient
    private String filePath;
    @Transient
    private String portrait;

    public String getPortrait() {
       if (ObjectUtil.isNull(this.portrait)){
           return "../upload/avatar/2021-06-03/blacksan.jpg";
       }
       return this.portrait;
    }
}
