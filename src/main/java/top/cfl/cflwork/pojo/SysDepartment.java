package top.cfl.cflwork.pojo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Transient;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Data
@ApiModel("功能描述：部门表")
public class SysDepartment {

    @ApiModelProperty("部门id")
    @Excel(width=20,name = "部门id")
    private String id;
    @ApiModelProperty("组织机构名称****全局唯一键,不可修改")
    @Excel(width=20,name = "组织机构名称****全局唯一键,不可修改")
    private String name;
    @ApiModelProperty("上级部门ID")
    @Excel(width=20,name = "上级部门ID")
    private String parentId;
    @ApiModelProperty("组织排序")
    @Excel(width=20,name = "组织排序")
    private Integer sort;
    @ApiModelProperty("创建时间")
    @Excel(width=20,name = "创建时间")
    private String createTime;
    @ApiModelProperty("修改时间")
    @Excel(width=20,name = "修改时间")
    private String updateTime;
    @ApiModelProperty("用户id")
    @Excel(width=20,name = "用户id")
    private String adminId;
    @ApiModelProperty("用户名")
    @Excel(width=20,name = "用户名")
    private String adminName;
    //分页排序等
    @Transient
    @NotNull(message = "pager不能为空")
    @Valid
    private Pager pager;
    //多选数据
    @Transient
    private String[] rowData;
    private List<SysDepartment> children;


    private List<Map<String,String>> adminList;

    private String seladminnumber = "0";


}
