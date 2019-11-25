package com.donate.common.utils;


public class Log {
    public Log() {
    }

    public static String LogText(String traceId, String title, String message, String trace) throws Exception {
        return LogText(traceId, title, message, trace, false);
    }

    public static String LogText(String title, String message, String trace) throws Exception {
        return LogText((String) null, title, message, trace, false);
    }

    public static String LogText(String title, String message, String trace, Boolean isDurable) throws Exception {
        return LogText((String) null, title, message, trace, isDurable);
    }

    public static String LogText(String traceId, String title, String message, String trace, Boolean isDurable) throws Exception {
        return LogText((String) null, traceId, title, message, trace, isDurable);
    }

    public static String LogText(String systemId, String traceId, String title, String message, String trace, Boolean isDurable) throws Exception {
        if (!ApiSetting.getIsInitialized()) {
            throw new Exception("Api尚未初始化!");
        } else {
            systemId = systemId != null && !"".equals(systemId.trim()) ? systemId : "";
            traceId = traceId != null && !"".equals(traceId.trim()) ? traceId : "";
            title = title != null && !"".equals(title.trim()) ? title : "";
            message = message != null && !"".equals(message.trim()) ? message : "";
            trace = trace != null && !"".equals(trace.trim()) ? trace : "";
            if (isDurable) {
                (new LogLogicDurable()).LogText(systemId, title, message, trace);
            } else {
                traceId = (new LogLogic()).LogText(systemId, traceId, title, message, trace);
            }

            return traceId;
        }
    }
}