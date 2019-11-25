package com.donate.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.donate.dao.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends BaseMapper<User> {

    Integer addUser(User userInfo)throws Exception;


    Integer updateByOpenId();

    User getUserByOpenId(String openId);

    User getUserByRawHeadImgUrl(String rawHeadImgUrl);
}
