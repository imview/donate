package com.donate.common.utils;

public class EarlyWarn {
    public EarlyWarn() {
    }

    public static void AlarmLog(String title, String message) throws Exception {
        AlarmLog(EarlyWarnLevelEnum.默认.getValue(), title, message);
    }

    public static void AlarmLog(EarlyWarnLevelEnum level, String title, String message) throws Exception {
        AlarmLog(level.getValue(), title, message);
    }

    public static void AlarmLog(int level, String title, String message) throws Exception {
        AlarmLog((String)null, (String)null, (String)null, level, title, message);
    }

    public static void AlarmLog(String systemId, String hostIP, String traceId, int level, String title, String message) throws Exception {
        if (!ApiSetting.getIsInitialized()) {
            throw new Exception("Api尚未初始化!");
        } else {
            systemId = systemId != null && !"".equals(systemId.trim()) ? systemId : "";
            hostIP = hostIP != null && !"".equals(hostIP.trim()) ? hostIP : "";
            traceId = traceId != null && !"".equals(traceId.trim()) ? traceId : "";
            title = title != null && !"".equals(title.trim()) ? title : "";
            message = message != null && !"".equals(message.trim()) ? message : "";
            (new EarlyWarnLogic()).AlarmLog(systemId, hostIP, traceId, level, title, message);
        }
    }
}