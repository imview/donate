package com.donate.api.dto;

import com.wordnik.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class RedirectUrlDTO implements Serializable {
 
	private static final long serialVersionUID = -2951875363064291340L;
	@ApiModelProperty(value="访问公众号的地址")
	private String oauthUrl;
	public RedirectUrlDTO(String oauthUrl){
		this.setOauthUrl(oauthUrl);
	}
	public String getOauthUrl() {
		return oauthUrl;
	}

	public void setOauthUrl(String oauthUrl) {
		this.oauthUrl = oauthUrl;
	}
}
