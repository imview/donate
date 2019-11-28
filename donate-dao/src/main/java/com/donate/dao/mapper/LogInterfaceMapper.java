package com.donate.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.donate.common.model.Pagination;
import com.donate.dao.entity.LogInterface;
import org.springframework.stereotype.Repository;

@Repository
public interface LogInterfaceMapper extends BaseMapper<LogInterface> {

    Integer addLogInterface(LogInterface logInterface)throws Exception;


}
