package com.donate.api.controller;

import com.donate.api.dto.PrepayDTO;
import com.donate.common.model.ServiceResult;
import com.donate.common.model.ServiceResultT;
import com.donate.common.properties.WxMpConfig;
import com.donate.common.utils.XMLUtil;
import com.donate.dao.entity.DonateOrder;
import com.donate.dao.entity.LogInterface;
import com.donate.dao.entity.WxOrder;
import com.donate.service.donate.LogInterfaceService;
import com.donate.service.donate.WXOrderService;
import com.donate.service.enums.PayStatusEnum;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.util.SignUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.Document;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;


@Controller
@RequestMapping(value="wxOrder")
@Api(value="wxOrder",description = "微信支付接口调用", produces = MediaType.APPLICATION_JSON_VALUE)
public class WXOrderController {
	 
	@Autowired
	WXOrderService wxOrderService;
	@Autowired
	private WxMpConfig wxConfig;
	@Autowired
	private LogInterfaceService logService;
	
	  
	private final Logger logger = LoggerFactory.getLogger(getClass().getName());
 
	/**
	 * 统一下单接口
	 * @param donateId
	 * @return
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping(value="pay",method = RequestMethod.POST)
	@ApiOperation(value = "2003支付订单", notes = "2003",  produces = MediaType.APPLICATION_JSON_VALUE)
	public ServiceResultT<PrepayDTO> getPrepayId(HttpServletRequest request,
												 @ApiParam(value = "donateId=捐赠表Id", required = true,name="donateId")
			String donateId) throws Exception{
		 
		ServiceResultT<PrepayDTO> resultT = new ServiceResultT<PrepayDTO>();
		String clientIp = this.getRemortIP(request);
		ServiceResultT<String> resultStr =  wxOrderService.prepayWX(donateId,clientIp); 
		
		if(resultStr.getIsSuccess())
		{
			String prepayId = resultStr.getDicData();
			Map<String, String> jsapiMap = wxOrderService.getJSAPIPayParam(prepayId);
			 
			//返回加密的
			PrepayDTO prepayDTO = new PrepayDTO();
			prepayDTO.setAppId(jsapiMap.get("appId"));
			prepayDTO.setNonceStr(jsapiMap.get("nonceStr"));
			prepayDTO.setPackageStr(jsapiMap.get("package"));
			prepayDTO.setTimeStamp(jsapiMap.get("timeStamp"));
			prepayDTO.setPaySign(jsapiMap.get("paySign"));
			prepayDTO.setSignType(jsapiMap.get("signType"));
			resultT.setDicData(prepayDTO);
		}
		else{
			
			resultT.failed(resultStr.getMessage());
		}
		return resultT;
		
	}
	@ResponseBody
	@RequestMapping(value="getJSSDKCallbackData",method = RequestMethod.POST)
	@ApiOperation(value = "支付回调", notes = "支付回调",  produces = MediaType.APPLICATION_JSON_VALUE)
	public void getJSSDKCallbackData(HttpServletRequest request,
                                     HttpServletResponse response)throws IOException,DocumentException,Exception {
	    LogInterface logBO=new LogInterface();
  	    logBO.setCreateTime(new Date());
  	    logBO.setId(UUID.randomUUID().toString());
  	    logBO.setUrl(request.getRequestURL().toString());
		try{
	      synchronized (this) {
	    	   
	    	  InputStream inputStream = request.getInputStream(); 
	      	  SAXReader reader = new SAXReader();
	          Document document = reader.read(inputStream);
	      	  String xmlData=   document.asXML();
		      Map<String, String> kvm = XMLUtil.parseXmlStringToMap(xmlData);
		      
      	     logBO.setRequest(xmlData);
      	 
      	    //验签
	        if (!SignUtils.checkSign(kvm, this.wxConfig.getAesKey())) { 
	        	this.responseFail(response, logBO, "check signature FAIL");
	        	return ;
	        }
	       //通知返回成功代码
	          if (!kvm.get("result_code").equals("SUCCESS")) {
	        	  
	        	    this.responseFail(response, logBO, "result_code is FAIL");
		        	return ;  
	          }
	            
	        WxPayOrderNotifyResult payNotifyResult =this.getWxPayOrderNotifyResult(kvm);
	        logBO.setWxOrderNo(payNotifyResult.getOutTradeNo());
	        logBO.setOpenId(payNotifyResult.getOpenid());
	        
	       
	        DonateOrder donateOrder = wxOrderService.getDonateOrder(payNotifyResult.getOutTradeNo());
	        //查询订单状态，已成功的，无需再进行处理
	        if(donateOrder != null && donateOrder.getStatus().equals(PayStatusEnum.SUCCESS.getValue().intValue())){
		    	   this.responseSuccess(response, logBO, "ok"); 
		    	   return;
	         }
	        //验证订单有效性 //判断回到通知中金额与实际金额是否一致（微信官方建议验证）
	        ServiceResult result =this.checkOrder(donateOrder, payNotifyResult);
	        if(!result.getIsSuccess()){
	       	  this.responseFail(response, logBO, result.getMessage());
	       	   return ;
	         }
	       
	        WxOrder wxOrderBO =new WxOrder();
	        wxOrderBO.setNo(payNotifyResult.getOutTradeNo());
	        wxOrderBO.setIsSubscribe(payNotifyResult.getIsSubscribe().equals("Y")?1:0);
	        wxOrderBO.setStatus(PayStatusEnum.SUCCESS.getValue()); 
	        wxOrderBO.setTransactionId(payNotifyResult.getTransactionId());
	        wxOrderBO.setUpdateTime(new Date());
	         SimpleDateFormat timeDf =   new SimpleDateFormat("yyyyMMddHHmmss");
	        wxOrderBO.setPayTime(timeDf.parse(payNotifyResult.getTimeEnd()));
	       
	       int updateResult= wxOrderService.updateOderPayStatus(wxOrderBO);
	       if(updateResult <= 0){ 
	       	 this.responseFail(response, logBO, "udpate order fail");
	       	 return;
	       }
	        
		    logger.info("out_trade_no: " + kvm.get("out_trade_no") + " pay ");
		    this.responseSuccess(response, logBO, "ok"); 
		    
	      } 
	  }
	  catch(Exception e){
		  logBO.setRequest(logBO.getRequest()==null?"":logBO.getRequest());
		  logger.error("支付回调接口调用异常，订单号："+logBO.getWxOrderNo()+e.getMessage());
		  this.responseFail(response, logBO, "call exception");
	  }
	  } 

	
	/**
	 * nginx转发的ip，否则直接取
	 * @param request
	 * @return
	 */
	private  String getRemortIP(HttpServletRequest request) {
		 
	    if (request.getHeader("x-real-ip" ) != null) { 
		    return request.getHeader("x-real-ip" ); 
	    }
	    else if(request.getHeader("x-forwarded-for" ) != null){ 
		    return request.getHeader("x-forwarded-for" ); 
		    
	    } else { 
	    	return request.getRemoteAddr();
		}  
	 }
	
	private WxPayOrderNotifyResult getWxPayOrderNotifyResult(Map<String, String> kvm){
		WxPayOrderNotifyResult result=new WxPayOrderNotifyResult();
		result.setAppid(kvm.get("appid"));
		result.setBankType(kvm.get("bank_type"));
		result.setCashFee(Integer.parseInt(kvm.get("cash_fee")));
		result.setFeeType(kvm.get("fee_type"));
		result.setIsSubscribe(kvm.get("is_subscribe"));
		result.setMchId(kvm.get("mch_id"));
		result.setNonceStr(kvm.get("nonce_str"));
		result.setOpenid(kvm.get("openid"));
		result.setOutTradeNo(kvm.get("out_trade_no"));
		result.setResultCode(kvm.get("result_code"));
		result.setReturnCode(kvm.get("return_code"));
		result.setSign(kvm.get("sign"));
		result.setTimeEnd(kvm.get("time_end"));
		result.setTotalFee(Integer.parseInt(kvm.get("total_fee")));
		result.setTradeType(kvm.get("trade_type"));
		result.setTransactionId(kvm.get("transaction_id"));
		
		return result;
	}
	
	private String globalReponseResult="<xml><return_code><![CDATA[%s]]></return_code><return_msg><![CDATA[%s]]></return_msg></xml>";
	private ServiceResult checkOrder(DonateOrder donateOrder,WxPayOrderNotifyResult orderNotifyResult){
		ServiceResult result =new ServiceResult();
		  if(donateOrder== null){ 
    		return  result.failed("out_trade_no not matching"); 
    	   }
    	 
         BigDecimal cardinal=new BigDecimal(100);
         if(donateOrder.getAmount().multiply(cardinal).intValue() != orderNotifyResult.getTotalFee().intValue()){
           
            return result.failed("payAmount no matching");   
         }
    	
		  return result.succeed();
	}
	
	private void responseSuccess(HttpServletResponse response, LogInterface logBO, String message)throws IOException,Exception{
		   logBO.setResponse(String.format(globalReponseResult, "SUCCESS",message));
		   this.logger.info("out_trade_no: "
	                   + logBO.getWxOrderNo() + message);
		    response.getWriter().write(logBO.getResponse()); 
	        logService.addLog(logBO);
	}
	
	private void responseFail(HttpServletResponse response, LogInterface logBO, String message)throws IOException,Exception{
		 logBO.setResponse(String.format(globalReponseResult, "FAIL",message));
		    response.getWriter().write(logBO.getResponse()); 
		    this.logger.error("out_trade_no: "
	                   + logBO.getWxOrderNo() + message);
	        logService.addLog(logBO);
	}
 
}
