package com.donate.api.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

@ApiModel(value="2005-2008列表接口返回数据内容")
public class RecordDTO {
	
	@ApiModelProperty(value="用户名")
	private String  name;
	
	@ApiModelProperty(value="头像url")
	private String headImgUrl;
	
	@ApiModelProperty(value="金额",notes="")
	private BigDecimal amount;
	
	@ApiModelProperty(value="金额（千分符）",notes="")
	private String amountStr;
	
	@ApiModelProperty(value="捐献时间")
	private Date createTime;
	
	@ApiModelProperty(value="捐献时间，字符串")
	private String createTimeStr;
	
	@ApiModelProperty(value="项目名称")
	private String projectName;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHeadImgUrl() {
		return headImgUrl;
	}

	public void setHeadImgUrl(String headImgUrl) {
		this.headImgUrl = headImgUrl;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.setCreateTimeStr(formatter.format(createTime));
	}

//	public void setCreateTime(Date createTime) {
//		this.createTime = createTime;
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
//		String today = formatter2.format(new Date());
//		if(today.equals(formatter2.format(createTime))){
//			this.setCreateTimeStr(formatter2.format(createTime));
//		}else{
//			this.setCreateTimeStr(formatter.format(createTime));
//		}
//		
//	}
	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getCreateTimeStr() {
		return createTimeStr;
	}

	public void setCreateTimeStr(String createTimeStr) {
		this.createTimeStr = createTimeStr;
	}

	public String getAmountStr() {
		return amountStr;
	}

	public void setAmountStr(String amountStr) {
		this.amountStr = amountStr;
	}
}
