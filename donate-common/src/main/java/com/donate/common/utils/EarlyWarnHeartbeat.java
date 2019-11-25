package com.donate.common.utils;

import java.util.Date;

public class EarlyWarnHeartbeat implements Runnable {
    private static Boolean isRunning = false;

    public EarlyWarnHeartbeat() {
    }

    public void run() {
        if (!isRunning) {
            isRunning = true;
            StringBuilder report = null;

            while(true) {
                report = new StringBuilder();

                try {
                    report.append("HB|");
                    report.append(ApiSetting.getSystemId() + "|");
                    report.append(ApiSetting.getSystemIP() + "|");
                    report.append(ApiSetting.getCurrentHostIP() + "|");
                    report.append(ApiSetting.getEarlyWarnHost() + "|");
                    double logSendCount = 0.0D;
                    double logTps = 0.0D;
                    double cpuRatio = 0.0D;
                    double memoryAvailable = 0.0D;
                    double memoryCommitted = 0.0D;
                    String diskSizeInfo = "";
                    double diskReads = 0.0D;
                    double diskReadKB = 0.0D;
                    double diskWrites = 0.0D;
                    double diskWriteKB = 0.0D;
                    double networkSend = 0.0D;
                    double networkReceive = 0.0D;
                    long subtractSecond = ((new Date()).getTime() - ApiSetting.getSubtractDateTime().getTime()) / 1000L;
                    if (ApiSetting.getServerMonitorEnable()) {
                        try {
                            Monitor.setMonitorData();
                            logSendCount = Monitor.getLogSendCount();
                            logTps = Monitor.getLogThreadCount();
                            cpuRatio = Monitor.getAllCpuRatio();
                            memoryAvailable = Monitor.getMemoryAvailable();
                            memoryCommitted = Monitor.getMemoryCommitted();
                            diskSizeInfo = Monitor.getDiskInfo();
                            diskReads = Monitor.getDiskReads();
                            diskReadKB = Monitor.getDiskReadKB();
                            diskWrites = Monitor.getDiskWrites();
                            diskWriteKB = Monitor.getDiskWriteKB();
                            networkSend = Monitor.getAllNetworkSend();
                            networkReceive = Monitor.getAllNetworkReceive();
                            Monitor.resetMonitorData();
                        } catch (Exception var28) {
                            EarlyWarn.AlarmLog("获取监控数据失败", var28.getMessage());
                        }
                    }

                    report.append(logSendCount + "|");
                    report.append(logTps + "|");
                    report.append(cpuRatio + "|");
                    report.append(memoryAvailable + "|");
                    report.append(memoryCommitted + "|");
                    report.append(diskSizeInfo + "|");
                    report.append(diskReads + "|");
                    report.append(diskReadKB + "|");
                    report.append(diskWrites + "|");
                    report.append(diskWriteKB + "|");
                    report.append(networkSend + "|");
                    report.append(networkReceive + "|");
                    report.append(subtractSecond + "|");
                    ApiCommon.SendMQ(ApiSetting.getEarlyWarnHost(), ApiSetting.getEarlyWarnUserName(), ApiSetting.getEarlyWarnPassword(), ApiSetting.getEarlyWarnHeartbeatMQ(), false, report.toString());
                } catch (Exception var30) {
                    ;
                }

                try {
                    Thread.sleep((long)(ApiSetting.getEarlyWarnHeartbeatTime() * 1000));
                } catch (Exception var29) {
                    ;
                }
            }
        }

    }
}

