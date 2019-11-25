package com.donate.common.utils;

public enum EarlyWarnLevelEnum {
    心跳预警(1),
    默认(2),
    普通(3),
    警告(4),
    紧急(5),
    严重错误(6),
    致命错误(7),
    灾难事故(8);

    private final int value;

    public int getValue() {
        return this.value;
    }

    private EarlyWarnLevelEnum(int value) {
        this.value = value;
    }
}
