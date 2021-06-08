package top.cfl.cflwork.pojo;

import lombok.Data;

import java.util.List;

@Data
public class ExcelInfo {

	private List<List<Object>> data;
	private List<List<Object>> persistentData;
	private Boolean ok;
	private List<List<String>> errors;
	private int errorNumber = 0;
	private String schoolId;
	private List objectList;
	private Integer successCnt;
	private Integer sumCnt;
	private String msg;
	private String code;
}
