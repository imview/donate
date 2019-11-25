package com.donate.common.utils;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.MDC;
import org.apache.log4j.spi.LoggingEvent;
import ucsmy.usp.api.EarlyWarn;
import ucsmy.usp.api.Log;

public class UspApiOutAppender extends AppenderSkeleton {

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean requiresLayout() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected void append(LoggingEvent event) {
		// TODO Auto-generated method stub
		try {
			String title="["+event.getLevel()+"]"+event.getLocationInformation().getClassName()+"."+event.getLocationInformation().getMethodName()+"["+event.getLocationInformation().getLineNumber()+"]";
			String content=this.layout.format(event);
			String traceId=(MDC.get(ConstKeys.ConstKeys.TRACE_ID)!=null)?MDC.get(ConstKeys.ConstKeys.TRACE_ID).toString():"";
			Log.LogText(traceId,title, content,traceId);
			if(event.getLevel()==Level.WARN||event.getLevel()==Level.ERROR){
				EarlyWarn.AlarmLog(title,content); 
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
