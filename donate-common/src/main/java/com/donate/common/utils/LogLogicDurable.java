package com.donate.common.utils;
import java.util.Date;

public class LogLogicDurable {
    private static int TimeCount = 10;

    public LogLogicDurable() {
    }

    protected void LogText(String systemId, String title, String message, String trace) throws Exception {
        double subSeconds = (double)((new Date()).getTime() - ApiSetting.getSubtractDateTime().getTime()) / 1000.0D;
        String subTime = String.format("%.3f", subSeconds) + TimeCount;
        String report = String.format("DurableLog|%s|%s|%s|%s|%s|%s|%s", systemId != null && !systemId.isEmpty() ? systemId : ApiSetting.getSystemId(), ApiSetting.getSystemIP(), ApiSetting.getCurrentHostIP(), title.replace("|", "｜"), message.replace("|", "｜"), trace.replace("|", "｜"), subTime);
        TimeCount = TimeCount < 90 ? ++TimeCount : 10;
        ApiCommon.SendMQ(ApiSetting.getDurableLogHost(), ApiSetting.getDurableLogUserName(), ApiSetting.getDurableLogPassword(), ApiSetting.getDurableLogMQ(), true, report);
    }
}