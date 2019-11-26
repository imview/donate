package com.donate.service.donate;

import com.donate.dao.entity.LogInterface;
import com.donate.dao.mapper.LogInterfaceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogInterfaceService {

	@Autowired
	LogInterfaceMapper logInterfaceMapper;
	 
	public void addLog(LogInterface log)throws Exception{
		logInterfaceMapper.insert(log);
	}
	
 
}
