package com.donate.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.donate.common.model.Pagination;
import com.donate.dao.entity.Donate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public interface DonateMapper extends BaseMapper<Donate> {

    Integer countByPagination(Pagination pagination)throws Exception;

    Integer deleteByPrimaryKey(String id)throws Exception;

    Integer addDonate(Donate logInterface)throws Exception;

    List<Donate> selectByPagination(Pagination pagination) throws Exception;

    Donate selectById(String id)throws Exception;

    List<Donate> selectAllDonationList(Pagination queryPgn);

    BigDecimal selectTotalAmount(Pagination queryPgn);

    BigDecimal selectMyTotalAmount(Pagination pagination);

    List<Donate> selectMyDonationList(Pagination pagination);

    Donate selectDonateDetail(String donateId);
}
