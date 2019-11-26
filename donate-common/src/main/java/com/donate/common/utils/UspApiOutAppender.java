package com.donate.common.utils;

import com.donate.common.model.ConstKeys;

import org.slf4j.event.LoggingEvent;

public class UspApiOutAppender {

//public class UspApiOutAppender extends AppenderSkeleton {

//	@Override
//	public void close() {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public boolean requiresLayout() {
//		// TODO Auto-generated method stub
//		return true;
//	}
//
//	@Override
//	protected void append(LoggingEvent event) {
//		// TODO Auto-generated method stub
//		try {
//			String title="["+event.getLevel()+"]"+event.getLocationInformation().getClassName()+"."+event.getLocationInformation().getMethodName()+"["+event.getLocationInformation().getLineNumber()+"]";
//			String content=this.layout.format(event);
//			String traceId=(MDC.get(ConstKeys.TRACE_ID)!=null)?MDC.get(ConstKeys.TRACE_ID).toString():"";
//			Log.LogText(traceId,title, content,traceId);
//			if(event.getLevel()==Level.WARN||event.getLevel()==Level.ERROR){
//				EarlyWarn.AlarmLog(title,content);
//			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

}
