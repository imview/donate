package com.donate.common.utils;

import com.ucs.fw.utils.SysConfigUtil;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import ucsmy.usp.api.ApiBaseService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.HashMap;

public class GlobalServletContextListener implements ServletContextListener {

	private static Logger logger = Logger.getLogger(GlobalServletContextListener.class);
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		HashMap<String, String> apiConfig = new HashMap<String, String>();
		apiConfig.put("ServiceHost", SysConfigUtil.getConfigMsg("usp_system_url"));
		apiConfig.put("SystemId", SysConfigUtil.getConfigMsg("usp_system_id"));
		try {
			ApiBaseService.initialize(apiConfig);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		PropertyConfigurator.configure(GlobalServletContextListener.class.getResource(SysConfigUtil.getConfigMsg("log4jpath")));   
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		
	}

}
