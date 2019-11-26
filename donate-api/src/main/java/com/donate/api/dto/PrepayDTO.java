package com.donate.api.dto;

import com.wordnik.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class PrepayDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4154669126895085146L;

	
	@ApiModelProperty(value = "商户注册具有支付权限的公众号成功后即可获得", required = true) 
	private String appId;

	@ApiModelProperty(value = "当前的时间 ex:1414561699", required = true)
	private String timeStamp;

	@ApiModelProperty(value = "随机字符串，不长于32位。", required = true)
	private String nonceStr;
	
	@ApiModelProperty(value = "统一下单接口返回的prepay_id参数值，提交格式如：prepay_id=***", required = true)
	private String packageStr;
	
	@ApiModelProperty(value = "签名算法，暂支持MD5", required = true)
	private String signType;
	
	@ApiModelProperty(value = "签名", required = true)
	private  String paySign;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getNonceStr() {
		return nonceStr;
	}

	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}

	public String getPackageStr() {
		return packageStr;
	}

	public void setPackageStr(String packageStr) {
		this.packageStr = packageStr;
	}

	public String getSignType() {
		return signType;
	}

	public void setSignType(String signType) {
		this.signType = signType;
	}

	public String getPaySign() {
		return paySign;
	}

	public void setPaySign(String paySign) {
		this.paySign = paySign;
	}
	 
}
