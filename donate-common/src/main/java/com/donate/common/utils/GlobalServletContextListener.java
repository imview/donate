package com.donate.common.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.HashMap;

public class GlobalServletContextListener implements ServletContextListener {

	private static Logger logger = LoggerFactory.getLogger(GlobalServletContextListener.class.getName());

	@Override
	public void contextInitialized(ServletContextEvent sce) {
//		HashMap<String, String> apiConfig = new HashMap<String, String>();
//		apiConfig.put("ServiceHost", SysConfigUtil.getConfigMsg("usp_system_url"));
//		apiConfig.put("SystemId", SysConfigUtil.getConfigMsg("usp_system_id"));
//		try {
//			ApiBaseService.initialize(apiConfig);
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//		}
//		PropertyConfigurator.configure(GlobalServletContextListener.class.getResource(SysConfigUtil.getConfigMsg("log4jpath")));
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub

	}

}
