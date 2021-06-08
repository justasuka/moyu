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

import java.util.List;

@Data
@ApiModel("文件管理")
public class SysFile{

    @ApiModelProperty("id")
    @Excel(name = "id")
    private String id;
    @ApiModelProperty("类型；0=文件夹；1=文档；2=图像；3=音频；4=视频")
    @Excel(name = "类型；0=文件夹；1=文档；2=图像；3=音频；4=视频")
    private String type;
    @ApiModelProperty("名称")
    @Excel(name = "名称")
    private String title;
    @ApiModelProperty("父id")
    @Excel(name = "父id")
    private String parentId;
    @ApiModelProperty("路径")
    @Excel(name = "路径")
    private String path;
    @ApiModelProperty("标签")
    @Excel(name = "标签")
    private String tag;
    @ApiModelProperty("形状；0=方图；1=横图；2=竖图")
    @Excel(name = "形状；0=方图；1=横图；2=竖图")
    private String shape;
    @ApiModelProperty("宽度")
    @Excel(name = "宽度")
    private String width;
    @ApiModelProperty("高度")
    @Excel(name = "高度")
    private String height;
    @ApiModelProperty("文件大小")
    @Excel(name = "文件大小")
    private String size;
    @ApiModelProperty("后缀")
    @Excel(name = "后缀")
    private String suffix;
    @ApiModelProperty("描述")
    @Excel(name = "描述")
    private String description;
    @ApiModelProperty("位置")
    @Excel(name = "位置")
    private String address;
    @ApiModelProperty("创建人id")
    @Excel(name = "创建人id")
    private String adminId;
    @ApiModelProperty("创建人名称")
    @Excel(name = "创建人名称")
    private String adminName;
    @ApiModelProperty("应用级别，0：表格，1：新增，2：修改，3：查看，4：删除")
    @Excel(name = "应用级别，0：表格，1：新增，2：修改，3：查看，4：删除")
    private String level;
    @ApiModelProperty("评分")
    @Excel(name = "评分")
    private String score;
    @ApiModelProperty("创建时间")
    @Excel(name = "创建时间")
    private String createTime;
    @ApiModelProperty("回收站；0=不是；1=是")
    @Excel(name = "回收站；0=不是；1=是")
    private String isDelete;
    @ApiModelProperty("上次修改时间")
    @Excel(name = "上次修改时间")
    private String updateTime;
    @ApiModelProperty("上次打开时间")
    @Excel(name = "上次打开时间")
    private String openTime;
    @ApiModelProperty("说明")
    @Excel(name = "说明")
    private String remark;
    @ApiModelProperty("拍摄时间")
    @Excel(name = "拍摄时间")
    private String shootingTime;
    //分页排序等
    @Transient
    @NotNull(message = "pager不能为空")
    @Valid
    private Pager pager;
    //多选数据
    @Transient
    private String[] rowData;
    @Transient
    private List<SysFile> children;
    @Transient
    private String refilePath;
    @Transient
    private String createTimeEnd;
    @Transient
    private Integer nameType;
    @Transient
    private String[] rolePath;

    public String[] getTags() {
        if (ObjectUtil.isNotNull(this.tag)&&!this.tag.equals("")){
            String[] split = this.tag.split(",");
            return split;
        }else {
            return new String[0];
        }
    }

    @Transient
    private String relativePath;
}
