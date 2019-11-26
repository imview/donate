package com.donate.service.donate;

import com.donate.dao.entity.User;
import com.donate.dao.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

	@Autowired
	UserMapper userMapper;
	 
	public void addUser(User user)throws Exception{
		userMapper.insert(user);
	}
	
	public Integer upateUser(User user)throws Exception{
		return userMapper.updateByOpenId();
	}
	
	public User getUserByOpenId(String openId)throws Exception{
		User user = userMapper.getUserByOpenId(openId);
		return user;
	}
	public User getUserByRawHeadImgUrl(String rawHeadImgUrl)throws Exception{
		User user = userMapper.getUserByRawHeadImgUrl(rawHeadImgUrl);
		return user;
	}
}
