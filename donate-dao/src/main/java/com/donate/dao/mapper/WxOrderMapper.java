package com.donate.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.donate.common.model.Pagination;
import com.donate.dao.entity.DonateOrder;
import com.donate.dao.entity.WxOrder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface WxOrderMapper extends BaseMapper<WxOrder> {


    List<WxOrder> selectByPagination(Pagination pagination);

    Integer updateByPrimaryKeySelective(WxOrder wxOrder);

    Map getDonateOrder(String orderNo);
}
