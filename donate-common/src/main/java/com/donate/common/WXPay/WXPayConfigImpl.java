package com.donate.common.WXPay;

//import com.github.wxpay.sdk.*;
import com.donate.common.properties.WxMpConfig;
import com.donate.common.utils.StringUtils;
//import org.apache.commons.lang.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * 微信支付配置类
 * @author Administrator
 *
 */
public class WXPayConfigImpl extends WXPayConfig  {
    
	private byte[] certData; 
	private WxMpConfig  wxMpConfig; 
    public WXPayConfigImpl(WxMpConfig wxMpConfig) throws Exception {
    	  
    	this.wxMpConfig = wxMpConfig;
    	
    	//从微信商户平台下载的安全证书存放的目录 
        String certPath = ""; //暂时没用 
        if(!StringUtils.isBlank(certPath)){
	        File file = new File(certPath);
	        InputStream certStream = new FileInputStream(file);
	        this.certData = new byte[(int) file.length()];
	        certStream.read(this.certData);
	        certStream.close();
        }
    }
    
    @Override
    public String getAppID() {
        return wxMpConfig.getAppid();//如初appid
    }
    @Override
    public String getMchID() {
        return wxMpConfig.getMchid();//商户号
    }
    @Override
    public String getKey() {
        return wxMpConfig.getAesKey();//密钥
    }
    @Override
    public InputStream getCertStream() {
        ByteArrayInputStream certBis = new ByteArrayInputStream(this.certData);
        return certBis;
    }
    public int getHttpConnectTimeoutMs() {
        return 8000;
    }
    public int getHttpReadTimeoutMs() {
        return 10000;
    }

	@Override
	public IWXPayDomain getWXPayDomain() {
		// TODO Auto-generated method stub
		IWXPayDomain sDomain = new IWXPayDomain(){

			@Override
			public void report(String domain, long elapsedTimeMillis, Exception ex) {
				// TODO Auto-generated method stub
				
			}

			
			@Override
			public DomainInfo getDomain(WXPayConfig config) {
				 
				// TODO Auto-generated method stub
				DomainInfo domainInfo =	 new DomainInfo(WXPayConstants.DOMAIN_API,true); 
				return domainInfo;
			}
			  
		};
		 
		return sDomain;
	}
}
