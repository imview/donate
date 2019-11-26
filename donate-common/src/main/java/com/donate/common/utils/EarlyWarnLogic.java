package com.donate.common.utils;


public class EarlyWarnLogic {
    public EarlyWarnLogic() {
    }

    protected void AlarmLog(String systemId, String hostIP, String traceId, int level, String title, String message) throws Exception {
        try {
            if (traceId == null || traceId.isEmpty()) {
                Object obj = LogLogic.threadLocalStack.get();
                ThreadLocalVar logLocalVar = obj == null ? null : (ThreadLocalVar)obj;
                if (logLocalVar != null) {
                    traceId = logLocalVar.getLogTraceId() == null ? traceId : logLocalVar.getLogTraceId();
                }
            }
        } catch (Exception var9) {
        }

        String report = String.format("%d|%s|%s|%s|%s|%s|%s", level, systemId != null && !systemId.isEmpty() ? systemId : ApiSetting.getSystemId(), hostIP != null && !hostIP.isEmpty() ? hostIP : ApiSetting.getSystemIP(), ApiSetting.getCurrentHostIP(), title.replace("|", "｜"), message.replace("|", "｜"), traceId.replace("|", "｜"));
        ApiCommon.SendMQ(ApiSetting.getEarlyWarnHost(), ApiSetting.getEarlyWarnUserName(), ApiSetting.getEarlyWarnPassword(), ApiSetting.getEarlyWarnMQ(), false, report);
    }
}
