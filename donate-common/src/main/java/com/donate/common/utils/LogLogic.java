package com.donate.common.utils;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LogLogic {
    public static Boolean IsRunning = false;
    private static Lock lock = new ReentrantLock();
    private static int TimeCount = 10;
    protected static ThreadLocal<ThreadLocalVar> threadLocalStack = new ThreadLocal();

    public LogLogic() {
    }

    protected String LogText(String systemId, String logTraceId, String title, String message, String trace) {
        try {
            if (LogLogicThread.logQueue == null) {
                LogLogicThread.logQueue = new ConcurrentLinkedQueue();
            }

            double subSeconds = (double)((new Date()).getTime() - ApiSetting.getSubtractDateTime().getTime()) / 1000.0D;
            String subTime = String.format("%.3f", subSeconds) + TimeCount;
            String[] arr = SetTraceId(logTraceId);
            logTraceId = arr[0];
            String logDuration = arr[1];
            String report = String.format(ApiSetting.getVersion() + ":Log|%s|%s|%s|%s|%s|%s|%s|%s|%s", systemId != null && !systemId.isEmpty() ? systemId : ApiSetting.getSystemId(), ApiSetting.getSystemIP(), ApiSetting.getCurrentHostIP(), title.replace("|", "｜"), message.replace("|", "｜"), trace.replace("|", "｜"), subTime, logTraceId.replace("|", "｜"), logDuration);
            LogLogicThread.logQueue.offer(report);
            TimeCount = TimeCount < 90 ? ++TimeCount : 10;
            lock.lock();
            if (!IsRunning) {
                IsRunning = true;
                Thread logThread = new Thread(new LogLogicThread());
                logThread.setName("UspLogThread");
                logThread.setDaemon(true);
                logThread.start();
            }

            lock.unlock();
        } catch (Exception var13) {
            ;
        }

        return logTraceId;
    }

    private static String[] SetTraceId(String logTraceId) {
        long logDuration = 0L;

        try {
            Object obj = threadLocalStack.get();
            ThreadLocalVar logLocalVar = obj == null ? null : (ThreadLocalVar)obj;
            if (logLocalVar == null) {
                logDuration = 0L;
                logLocalVar = new ThreadLocalVar();
                Monitor.setLogThreadCount(1);
            }

            if (logTraceId == null || logTraceId.trim().isEmpty()) {
                logTraceId = logLocalVar.getLogTraceId() == null ? UUID.randomUUID().toString().replaceAll("-", "") : logLocalVar.getLogTraceId();
            }

            logDuration = logLocalVar.getLogDate() == null ? 0L : (new Date()).getTime() - logLocalVar.getLogDate().getTime();
            logLocalVar.setLogTraceId(logTraceId);
            logLocalVar.setLogDate(new Date());
            threadLocalStack.set(logLocalVar);
            Monitor.setLogSendCount(1);
        } catch (Exception var5) {
            logTraceId = "";
            logDuration = 0L;
        }

        return new String[]{logTraceId, String.valueOf(logDuration)};
    }
}
