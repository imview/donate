package com.donate.service.donate;

import com.donate.common.model.Pagination;
import com.donate.common.model.ServiceResult;
import com.donate.dao.entity.Donate;
import com.donate.dao.entity.WxOrder;
import com.donate.dao.mapper.DonateMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DonateService {
 
	@Autowired
	DonateMapper donateMapper;
	  
    @Autowired
    WXOrderService wxOrderService;
	 
	private final Logger logger = LoggerFactory.getLogger(getClass().getName());
	 
	/**
	 * 查看是否有效（包括是否存在和是否已支付）
	 * @param donateId
	 * @return
	 */
	ServiceResult checkDonateId(String donateId) throws Exception {
		
		ServiceResult result = new ServiceResult(); 
	 	Donate donateDO =  donateMapper.selectById(donateId);
	 	
	    List<WxOrder> wxOrderDOs =  wxOrderService.selectByDonateIdSucced(donateId);
	 	   
	 	if(donateDO == null || StringUtils.isBlank(donateDO.getId())){
	 		//无效
	 		result.failed("该捐赠无效");
	 	}
	 	else if(!wxOrderDOs.isEmpty()){ 
			//是否已经支付
			result.failed("该捐赠已经支付");
	 	}
	 	else{
	 	  
	 		result.succeed();
	 	}
		return result; 
	}
	
	
	/**
	 * 添加donate记录
	 * @param donate
	 * @return
	 */
    public ServiceResult addDonate(Donate donate){
    	 
    	ServiceResult result  = new ServiceResult();
    	try{
	    	donateMapper.insert(donate);
	    	result.succeed(); 
	    	
    	}catch (Exception e) {
    		
			logger.error("捐赠入库失败");
			result.failed("操作失败");
		}
    	
    	return result; 
    }
	
    /**
     * 根据donateId和openId返回
     * @param pagin
     * @return
     */
    public List<Donate> selectDonateByPagin(Pagination pagin) throws Exception {
    	
    	@SuppressWarnings("unchecked")
		List<Donate> donateList= donateMapper.selectByPagination(pagin);
		return donateList;
    	
    }
    
    /**
     * 根据donateId返回单个
     * @param donateId
     * @return
     */
    @SuppressWarnings("unchecked")
	public Donate selectSignleDonateByPagin(String donateId) throws Exception {
    	
		Donate donateDO = null;
		Pagination pagination = new Pagination();
 		Map<String, Object> donateMap = new HashMap<String,Object>();
 		donateMap.put("donateId", donateId);
 		pagination.setConditions(donateMap);
 		
 		Donate donateDOs = donateMapper.selectById(donateId);
 		if(donateDOs==null){
 			donateDO = new Donate();
 		} 
		return donateDO;
    }
    
    /**
     * 分页查询所有的捐款记录
     * @author ucs_zhangfengjin
     * @param queryPgn
     * @return
     * @throws Exception
     */
    public List<Donate> selectAllDonationList(Pagination queryPgn)throws Exception {
		List<Donate> donateList = donateMapper.selectAllDonationList(queryPgn);
		return donateList;
	}
    
    /**
     * 查询所有捐赠总额
     * @param pagination
     * @return
     * @throws Exception
     */
    public BigDecimal selectTotalAmount(Pagination pagination)throws Exception {
    	BigDecimal totalAmount = donateMapper.selectTotalAmount(pagination);
    	return totalAmount;
    }
    
    /**
     * 查询个人捐赠总额
     * @param pagination
     * @return
     * @throws Exception
     */
    public BigDecimal selectMyTotalAmount(Pagination pagination)throws Exception{
    	BigDecimal myTotalAmount = donateMapper.selectMyTotalAmount(pagination);
    	return myTotalAmount;
    }
    
    /**
     * 分页查询个人的捐款记录
     * @author gene
     * @param pagination
     * @return
     * @throws Exception
     */
    public List<Donate> selectMyDonationList(Pagination pagination)throws Exception {
		List<Donate> donateList = donateMapper.selectMyDonationList(pagination);
		return donateList;
	}
    
    /**
     * 根据donateId获取订单号，不包含openId，联合wx_order表，只取支付成功的值
     * @author
     * @param donateId
     * @return
     * @throws Exception
     */
    public Donate selectDonationDetail(String donateId)throws Exception{
    	Donate donate = donateMapper.selectDonateDetail(donateId);
    	return donate;
    }
    
    
}
