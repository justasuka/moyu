package top.cfl.cflwork.pojo.admin;

import cn.afterturn.easypoi.excel.annotation.Excel;
import top.cfl.cflwork.pojo.Pager;
import top.cfl.cflwork.pojo.validateClass.GroupOne;
import top.cfl.cflwork.pojo.validateClass.GroupTwo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin implements Serializable {
    private static final long serialVersionUID = 7762799280945548221L;
    private String id;
    @NotBlank(groups = GroupOne.class,message = "用户名不能为空")
    @Size(groups = GroupOne.class,max = 64,message = "最长{max}字符")
    @Excel(name = "用户名")
    private String username;//用户名
    @NotBlank(groups = {GroupOne.class, GroupTwo.class},message = "密码不能为空")
    @Size(groups = GroupOne.class,max = 64,message = "最长{max}字符")
    private String password;
    @Excel(name = "真实姓名")
    private String realName;
    @Excel(name = "手机号码")
    //@NotEmpty(message = "手机号不能为空")
    @ApiModelProperty("手机号码")
    private String phone;
    @Excel(name = "电子邮件")
    @Email
    @ApiModelProperty("电子邮件")
    private String email;
    @ApiModelProperty("头像地址")
    private String portrait;
    @ApiModelProperty("创建时间")
    private String createTime;
    @ApiModelProperty("修改时间")
    private String updateTime;
    //额外
    @NotNull
    private Pager pager;
    private String verifyCode;
    private String roleNames;
    private String newPassword;
    private String challenge;
    @Length(groups = GroupOne.class,max = 255,message = "最长{max}字符")
    private String token;
    @ApiModelProperty("忽略参数")
    private String ignore;
    @ApiModelProperty("部门编号")
    private String departmentId;
    @ApiModelProperty("部门名称")
    @Excel(name = "部门名称")
    private String departmentName;
    @ApiModelProperty("城市id")
    private String cityId;
    @ApiModelProperty("省份id")
    private String provinceId;
    @ApiModelProperty("职务编号")
    private String jobId;
    @ApiModelProperty("职务名称")
    @Excel(name = "职务名称")
    private String jobName;
    private String rowData[];
    private String errorMsg;
}
