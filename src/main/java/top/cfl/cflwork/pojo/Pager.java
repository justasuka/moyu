package top.cfl.cflwork.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import java.util.Arrays;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel("分页排序等支撑类,保存数据时请忽略,做条件查询时用")
public class Pager implements Pageable {
    public static final String ASC="asc";
    public static final String DESC="desc";
    @ApiModelProperty(value = "当前页码,当查询列表的时候用到,其他接口请忽略")
    @Min(value = 1,message = "page最小为{value}")
    private int page=1;
    @ApiModelProperty(value = "每页记录数,当查询列表的时候用到,其他接口请忽略")
    @Range(min = 1,max = 100,message = "pageSize最小为{min},最大为{max}")
    private int pageSize=10;
    @ApiModelProperty(hidden = true)
    private long beginRow;
    //返回字段的控制
    @ApiModelProperty(hidden = true)
    private String[] includes;
    @ApiModelProperty(hidden = true)
    private List<String> excludes;
    //排序字段
    @ApiModelProperty(value = "排序字段名称,当查询列表的时候用到,其他接口请忽略")
    private String sortField;
    @ApiModelProperty(value = "排序的顺序,值为asc或者desc,当查询列表的时候用到,其他接口请忽略")
    private String sortOrder;
    @JsonIgnore
    @Transient
    private Sort sort= Sort.unsorted();
    @ApiModelProperty(hidden = true)
    private boolean paging = true;//是否分页
    @ApiModelProperty(value = "指定哪个字段使用模糊查询,不需要模糊查询,请求参数里可删除该字段")
    private String like;

    @ApiModelProperty("范围查询字段")
    private String rangeField;
    @ApiModelProperty("范围查询数组,如最小时间和最大时间")
    private Object[] rangeArray;


    private static final int MAX_SIZE=10000;



    public Pager() {
    }

    public Pager(String sortField, String sortOrder) {
        this.sortField = sortField;
        this.sortOrder = sortOrder;
        createOrder();
    }

    public Pager(int page, int pageSize, Sort sort) {
        this.page = page;
        this.pageSize = pageSize;
        this.sort = sort;
    }

    public String getLike() {
        return like;
    }

    public Pager setLike(String like) {
        this.like = like;
        return this;
    }

    private void createOrder(){
        if(sortField==null||"".equals(sortField)||sortOrder==null||"".equals(sortOrder)){
            return;
        }
        this.sort=new Sort(Sort.Direction.fromString(sortOrder.split(",")[0]),sortField.split(",")[0]);
    }

    public String getSortField() {
        return sortField;
    }

    public Pager setSortField(String sortField) {
        this.sortField = sortField;
        if(sortField!=null&&!"".equals(sortField)&&sortOrder!=null&&!"".equals(sortOrder)){
            createOrder();
        }
        return this;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public Pager setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
        if(sortField!=null&&!"".equals(sortField)&&sortOrder!=null&&!"".equals(sortOrder)){
            createOrder();
        }
        return this;
    }

    public List<String> getExcludes() {
        return excludes;
    }

    public Pager setExcludes(List<String> excludes) {
        this.excludes =excludes;
        return this;
    }
    public Pager addExcludes(String... excludes){
        List<String> list=Arrays.asList(excludes);
        if(this.excludes==null){
            this.excludes=list;
        }else{
            this.excludes.addAll(list);
        }
        return this;
    }
    public String[] getIncludes() {
        return includes;
    }

    public Pager setIncludes(String... includes) {
        this.includes = includes;
        return this;
    }
    public Integer getPage() {
        return page;
    }

    public Pager setPage(int page) {
        this.page = page;
        this.beginRow = (long)(this.pageSize * (page - 1));
        return this;
    }

    public String getRangeField() {
        return rangeField;
    }

    public Pager setRangeField(String rangeField) {
        this.rangeField = rangeField;
        return this;
    }

    public Object[] getRangeArray() {
        return rangeArray;
    }

    public Pager setRangeArray(Object[] rangeArray) {
        this.rangeArray = rangeArray;
        return this;
    }

    /*下面需要实现pageable*/
    @Override
    @ApiModelProperty(hidden = true)
    public int getPageNumber() {
        return this.page;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    @Override
    @ApiModelProperty(hidden = true)
    public long getOffset() {
        return this.beginRow;
    }

    @Override
    public Sort getSort() {
        return this.sort;
    }

    @Override
    public Pageable next() {
        return new Pager(this.page+1,this.pageSize,this.sort);
    }

    @Override
    public Pageable previousOrFirst() {
        return this.page==1?this:new Pager(this.page-1,this.pageSize,this.sort);
    }

    @Override
    public Pageable first() {
        return new Pager(1,this.pageSize,this.sort);
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }

    public Pager setPageSize(int pageSize) {
        this.pageSize = Math.min(pageSize,MAX_SIZE);
        this.beginRow = (long)(this.pageSize * (page - 1));
        return this;
    }

    public Long getBeginRow() {
        return beginRow;
    }

    public void setBeginRow(long beginRow) {
        this.beginRow = beginRow;
    }


    public boolean getPaging() {
        return paging;
    }

    public Pager setPaging(boolean paging) {
        this.paging = paging;
        return this;
    }

    @Override
    @ApiModelProperty(hidden = true)
    public boolean isPaged() {
        return true;
    }

    @Override
    @ApiModelProperty(hidden = true)
    public boolean isUnpaged() {
        return !this.isPaged();
    }
}
