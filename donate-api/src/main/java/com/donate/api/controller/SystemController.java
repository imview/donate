package com.donate.api.controller;
 
//import com.ucs.fw.utils.SysConfigUtil;

import com.donate.api.dto.RedirectUrlDTO;
import com.donate.common.model.ServiceResultT;
import com.donate.common.properties.WxMpConfig;
import com.donate.common.utils.WeixinServiceUtil;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="system")
@Api(value = "system", description = "公众号基础接口", produces = MediaType.APPLICATION_JSON_VALUE)
public class SystemController {

	@Autowired
	WeixinServiceUtil wxMpService;
	@Autowired
	private WxMpConfig wxConfig;
	  
	private final Logger logger = LoggerFactory.getLogger(getClass().getName());
	  
	@ResponseBody
	@RequestMapping(value="getOauthUrl",method = RequestMethod.POST)
	@ApiOperation(value="getOauthUrl",notes="获取进入公众号的地址", produces = MediaType.APPLICATION_JSON_VALUE)
	public ServiceResultT<RedirectUrlDTO> getOauthUrl(
			@ApiParam(value = "redirectUrl-跳转至公众号的地址（相对路径）", required = true,name="redirectUrl") String redirectUrl){ 
		 
		ServiceResultT<RedirectUrlDTO> result = new ServiceResultT<RedirectUrlDTO>(); 
		 if(StringUtils.isBlank(redirectUrl)){
			 return result.failed("请求跳转地址不能为空");
		 }
		 
		 
		 if(!StringUtils.startsWith(redirectUrl, "/")){
			redirectUrl="/"+redirectUrl;
		 }
		 
		 redirectUrl=wxConfig.getWxCpDomain()+redirectUrl;
		
		 String oauthUrl = wxMpService.oauth2buildAuthorizationUrl(redirectUrl,
			      WxConsts.OAUTH2_SCOPE_USER_INFO, null);
		 RedirectUrlDTO url =new RedirectUrlDTO(oauthUrl); 
	     logger.info("请求地址："+redirectUrl+"; 相应地址："+ oauthUrl);
	   
		result.setDicData(url); 
		return result;
	}
	
	
	@ResponseBody
	@RequestMapping(value="getJsapiSignature",method= RequestMethod.GET)
	@ApiOperation(value="getJsapiSignature",notes="获取公众号分享的签名", produces = MediaType.APPLICATION_JSON_VALUE)
	public ServiceResultT<WxJsapiSignature> getJsapiSignature(
			@ApiParam(value = "需要进行分享的地址url", required = true,name="url")String url)throws Exception {

		ServiceResultT<WxJsapiSignature> result = new ServiceResultT<WxJsapiSignature>();
		WxJsapiSignature signature = new WxJsapiSignature();
		try {
			//logger.info("获取JsapiSignature签名的url:"+url);
			//logger.info("请求的APPId："+wxConfig.getAppid());
			//logger.info("请求的appSecret："+wxConfig.getAppsecret());
			
			signature = wxMpService.createJsapiSignature(url);
			
			//logger.info("请求的accesstoken："+ redisUtil.getAccessToken());
			//logger.info("请求的jsapiticket："+ redisUtil.getJsapiTicket());
			
			if(signature==null){
				result.failed("获取签名失败");
			}else {
				logger.info("获取签名成功！！返回来的signature对象是：");
				//logger.info("APPId："+signature.getAppId());
				logger.info("nonce："+signature.getNonceStr());
				logger.info("signature："+signature.getSignature());
				logger.info("timestamp："+signature.getTimestamp());
				logger.info("url："+signature.getUrl());
				result.setDicData(signature);
				result.succeed("获取签名成功！");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			result.failed("获取签名失败");
			//logger.info("请求的accesstoken："+ wxMpService.getAccessToken());
			//logger.info("请求的jsapiticket："+ wxMpService.getJsapiTicket());
			
			logger.info("获取签名失败！！返回来的signature对象是：");
			logger.info("APPId："+signature.getAppId());
			logger.info("nonce："+signature.getNonceStr());
			logger.info("signature："+signature.getSignature());
			logger.info("timestamp："+signature.getTimestamp());
			logger.info("url："+signature.getUrl());
			
		}
		return result ;
	
	}
	
	

	
}
