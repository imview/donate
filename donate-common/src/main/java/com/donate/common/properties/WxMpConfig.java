package com.donate.common.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * @author Binary Wang
 *
 */
 
@Configuration
public class WxMpConfig {
  @Value("${wx_token}")
  private String token;

  @Value("${wx_appid}")
  private String appid;

  @Value("${wx_appsecret}")
  private String appsecret;
  
  /**
   * 商户ID
   */
  @Value("${wx_mchid}")
  private String mchid;
  

  @Value("${wx_donate_product_name}")
  private String donateProductName;

  @Value("${wx_donate_product_detila}")
  private String donateProductDetail;
    
  @Value("${wx_aeskey}")
  private String aesKey;
  @Value("${wx_ucs_gdckpf_domain}")
  private String wxCpDomain;
  
  @Value("${wx_ucs_callbak_url}")
  private String wxCallbackUrl;
  
  @Value("${wx_ucs_headPic_dir}")
  private String wxHeadPicDir;
  
  public String getWxheadPicDir(){
	  return this.wxHeadPicDir;
  }
  public String getWxCallbackUrl(){
	  return this.wxCallbackUrl;
  }
  
  public String getWxCpDomain(){
	  return this.wxCpDomain;
  }
  
  public String getToken() {
    return this.token;
  }

  public String getAppid() {
    return this.appid;
  }

  public String getAppsecret() {
    return this.appsecret;
  }

  public String getAesKey() {
    return this.aesKey;
  }

public String getMchid() {
	return mchid;
}

public void setMchid(String mchid) {
	this.mchid = mchid;
}

public String getDonateProductName() {
	return donateProductName;
}

public void setDonateProductName(String donateProductName) {
	this.donateProductName = donateProductName;
}

public String getDonateProductDetail() {
	return donateProductDetail;
}

public void setDonateProductDetail(String donateProductDetail) {
	this.donateProductDetail = donateProductDetail;
}

}
