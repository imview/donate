package com.donate.service.donate;

import com.alibaba.fastjson.JSONObject;
import com.donate.common.WXPay.WXPay;
import com.donate.common.WXPay.WXPayConfigImpl;
import com.donate.common.WXPay.WXPayConstants;
import com.donate.common.WXPay.WXPayUtil;
import com.donate.common.model.Pagination;
import com.donate.common.model.ServiceResult;
import com.donate.common.model.ServiceResultT;
import com.donate.common.properties.WxMpConfig;
import com.donate.common.utils.IdWorker;
import com.donate.common.utils.StringUtils;
import com.donate.dao.entity.Donate;
import com.donate.dao.entity.DonateOrder;
import com.donate.dao.entity.LogInterface;
import com.donate.dao.entity.WxOrder;
import com.donate.dao.mapper.WxOrderMapper;
//import com.github.binarywang.wxpay.constant.WxPayConstants.SignType;
//import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class WXOrderService {

	 
    
	private final Logger logger = LoggerFactory.getLogger(getClass().getName());
	
	@Autowired
	WxOrderMapper wxOrderMapper;
	
	@Autowired
	DonateService donateService;
	
	@Autowired
	WxMpConfig wxMpConfig;

	@Autowired
	IdWorker idWorker;
	
	@Autowired
	private LogInterfaceService logService;

	
	public boolean addWXOrder(WxOrder wxOrderDO) {
		wxOrderMapper.insert(wxOrderDO); 
		return true;
		
	} 
	
	/**
	 * 统一下单接口
	 * @param donateId
	 * @return
	 * @throws Exception 
	 */
	public ServiceResultT<String> prepayWX(String donateId, String clientIp) throws Exception{
		ServiceResultT<String> resultT= new ServiceResultT<String>();
		   
		String orderNo = Long.toString(idWorker.nextId()); 
 		ServiceResult result = donateService.checkDonateId(donateId);
 		Donate donate = donateService.selectSignleDonateByPagin(donateId);
    	 
 	/*	logger.info("donateId:" + donateId 
 				+ " donateDO:"+ (donateDO == null ?"null" : donateDO.getId())
 				+ " orderNo:"+ orderNo  
 				+ " checkId:"+result.getIsSuccess() );*/
 	 
	    if(result.getIsSuccess() 
	    	&& donate != null
	        && StringUtils.isNotBlank(donate.getId())){
	    	  
	 		 try {
 
			    //统一下单实体
	 			LogInterface logInterfaceBO = new LogInterface();  //接口日志
			    WxOrder wxOrderDO = new WxOrder();
			    wxOrderDO.setDonateId(donateId);
			    wxOrderDO.setNo(orderNo);  
                Map<String, String> reqData = new HashMap<String, String>();  
                BigDecimal totalAmount = donate.getAmount().multiply(new BigDecimal("100"));
                //.getBytes("ISO-8859-1"),"utf-8")
                reqData.put("body",wxMpConfig.getDonateProductName());   //商品名称
                reqData.put("detail",wxMpConfig.getDonateProductDetail()); //商品详情
                reqData.put("out_trade_no", orderNo); //订单号
                reqData.put("total_fee",Integer.toString( totalAmount.intValue()));   //金额
                reqData.put("spbill_create_ip", clientIp);  //终端IP 
                reqData.put("trade_type", "JSAPI"); 
                reqData.put("notify_url", wxMpConfig.getWxCallbackUrl());  
                reqData.put("openid", donate.getOpenId());
                 
				WXPayConfigImpl wXPayConfigImpl = new WXPayConfigImpl(wxMpConfig);
				WXPay wxPay = new WXPay(wXPayConfigImpl);
				Map<String, String> reqPackageData = wxPay.fillRequestData(reqData);  
			    Map<String,String> resMap = wxPay.unifiedOrder(reqPackageData); 
			    
			    
			    //保存接口日志
			    try{  
				    String reqXML = WXPayUtil.mapToXml(reqPackageData);
				    String repXML = WXPayUtil.mapToXml(resMap); 
				    logInterfaceBO.setCreateTime(new Date());
				    logInterfaceBO.setId(UUID.randomUUID().toString());
				    logInterfaceBO.setRequest(reqXML);
				    logInterfaceBO.setResponse(repXML);
				    logInterfaceBO.setUrl("http://" + WXPayConstants.DOMAIN_API + WXPayConstants.UNIFIEDORDER_URL_SUFFIX );
				    logInterfaceBO.setWxOrderNo(orderNo); 
				    logService.addLog(logInterfaceBO);
				    
			    }catch (Exception e) {
			    	logger.error("统一下单保存接口日志出错" , e);
				}
			    
			    //判断是否有返回code
			    if(resMap.get("return_code") != null ){
			    	  
			    	 wxOrderDO.setReturnCode(resMap.get("return_code"));
			    	 wxOrderDO.setReturnMsg(resMap.get("return_msg"));
			    	 wxOrderDO.setStatus(0);
			    	 wxOrderDO.setCreateTime(new Date());
			    	  
			    	 if(resMap.get("return_code").equals("SUCCESS")
					      && resMap.get("result_code") != null
					      && resMap.get("result_code").equals("SUCCESS")){ 
			    		 
			    		  logger.debug("统一下单成功" + donateId); 
				    	  wxOrderDO.setPrepayId(resMap.get("prepay_id"));
						  resultT.setDicData(resMap.get("prepay_id"));
						  
						  resultT.succeed();
			    	 }
			    	 else{
 
			    		 String errCode =  resMap.get("err_code") == null 
			    				 ?"":resMap.get("err_code");
			    		 String errCodeDes =  resMap.get("err_code_des")==null 
			    				 ?"": resMap.get("err_code_des");
			    		 
			    		 String errInfo =  String.format("统一下单失败 donateId:%s err_code:$s err_code_des:%s" 
			    				 , donateId
			    				 ,errCode
			    				 ,errCodeDes);
			    		 
			    		 resultT.failed("统一下单失败return_code：fail"); 
			    		 
			    		 logger.error(errInfo );
			    	 }
			    	  
			    	 if(StringUtils.isBlank(wxOrderDO.getPrepayId())){
			    		 wxOrderDO.setPrepayId("");
			    	 }
			    	 
					 this.addWXOrder(wxOrderDO);
			    	   
			    }
			    else{
			    	
			    	String feedback = resMap.get("return_msg") == null 
			    			?"":resMap.get("return_msg").toString();
			    	
			    	logger.error("统一下单失败 donateId:" + donateId + "  "+ feedback); 
			    	resultT.failed("统一下单失败"); 
			    } 
			       
			      
			} catch (Exception e) {
				
				resultT.failed("统一下单出现异常");
				logger.error("统一下单异常 donateId:" + donateId, e);
			 
			}
	 		 
	    }else if(donate == null || StringUtils.isBlank(donate.getId())){
	    	
	    	result.failed(); 
	    	logger.error("统一下单异常,捐赠信息没找到 donateId:" + donateId);
	    }
	    else if (!result.getIsSuccess()){
	    	
	    	logger.error("统一下单异常 donateId:" + donateId 
	    			+" info:" + result.getMessage());
	    	resultT.failed(result.getMessage());
	    }
		
		return resultT; 
	}
	
	/**
	 * 根据prepayId生成jsapi支付参数
	 * @param prepayId
	 * @return
	 */
	public Map<String, String> getJSAPIPayParam(String prepayId){
		
		//wxMpConfig  
		Map<String, String> data = new HashMap<String,String>();
		data.put("appId", wxMpConfig.getAppid());  
		data.put("timeStamp",Long.toString(System.currentTimeMillis()).substring(0,10));
		data.put("package", "prepay_id="+prepayId);
		data.put("nonceStr", WXPayUtil.generateNonceStr());
		data.put("signType", String.valueOf(WXPayConstants.SignType.MD5));
		
		
		String sign = "";
		try {
			sign = WXPayUtil.generateSignature(data, wxMpConfig.getAesKey());
		} catch (Exception e) {
			 
			logger.error("jsapi签名出错了",e);
		} 
		
		data.put("paySign",sign);  
		logger.debug(String.format("生成签名%s,Object:", sign,JSONObject.toJSONString(data)) );
		 
		return data;
		
	}
	
	/**
	 * 查询已经成功的单个捐赠记录
	 * @param donateId
	 * @return
	 */
	public  List<WxOrder> selectByDonateIdSucced(String donateId){
	
		 	//1成功 2失败
		 	List<Integer> status =  new ArrayList<Integer>();
		 	status.add(1);
		 	status.add(2);
		 	
			Pagination pagination  = new Pagination();
		 	Map<String, Object>  map = new HashMap<String, Object>();
		 	map.put("donateId", donateId);
		 	map.put("status", status);
		 	
		 	pagination.setConditions(map); 
		    @SuppressWarnings("unchecked")
			List<WxOrder> wxOrderList = wxOrderMapper.selectByPagination(pagination);
		    
		    return wxOrderList;

	}
	
	//更新订单状态
	public Integer updateOderPayStatus(WxOrder wxOrder)throws Exception {
		return wxOrderMapper.updateByPrimaryKeySelective(wxOrder);
	}
	
	public Map getDonateOrder(String orderNo)throws Exception{
		Map donateOrder = wxOrderMapper.getDonateOrder(orderNo);
		
		return donateOrder;
	}
	
}
