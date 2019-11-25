package com.donate.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.donate.dao.entity.User;

public interface UserMapper extends BaseMapper<User> {

    Integer addUser(User userInfo)throws Exception;


}
