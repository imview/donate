package com.donate.common.model;

import com.wordnik.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class ServiceResultT<T> implements Serializable {
 
	private static final long serialVersionUID = 6551679195837214120L;
  
	 
	@ApiModelProperty(value = "操作说明", required = true) 
	private String message="请求成功";
	
	@ApiModelProperty(value = "操作状态", required = true) 
	private Boolean isSuccess=true;
	

	@ApiModelProperty(value = "返回实体", required = false) 
	private T dicData;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Boolean getIsSuccess() {
		return isSuccess;
	}

	public void setIsSuccess(Boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	
	public ServiceResultT<T> succeed() {
		this.isSuccess = true;
		return this;
	}
	
	public ServiceResultT<T> succeed(String message) {
		this.isSuccess = true;
		this.message = message;
		return this;
	}
	
	public ServiceResultT<T> failed() {
		this.isSuccess = false;
		return this;
	}
	public ServiceResultT<T> failed(String message) {
		this.message = message;
		this.isSuccess = false;
		return this;
	}

	public T getDicData() {
		return dicData;
	}

	public void setDicData(T t) {
		this.dicData = t;
	}
	
}
