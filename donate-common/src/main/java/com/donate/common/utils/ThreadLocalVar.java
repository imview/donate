package com.donate.common.utils;

import java.util.Date;

public class ThreadLocalVar {
    private String LogTraceId = null;
    private Date LogDate = null;

    public ThreadLocalVar() {
    }

    public String getLogTraceId() {
        return this.LogTraceId;
    }

    public void setLogTraceId(String logTraceId) {
        this.LogTraceId = logTraceId;
    }

    public Date getLogDate() {
        return this.LogDate;
    }

    public void setLogDate(Date logDate) {
        this.LogDate = logDate;
    }
}
