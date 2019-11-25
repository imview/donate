package com.donate.common.utils;

import java.util.HashMap;

public class ApiBaseService {
    public ApiBaseService() {
    }

    public static void initialize() throws Exception {
        initialize("");
    }

    public static void initialize(String apiConfigPath) throws Exception {
        if (!ApiSetting.getIsInitialized()) {
            ApiSetting.InitBase();
            ApiSetting.InitSetting(apiConfigPath);
            ApiSetting.setIsInitialized(true);
            if (ApiSetting.getConfigEnable()) {
                Config.Init();
            }

            run();
        }

    }

    public static void initialize(String serviceHost, String systemId) throws Exception {
        if (!ApiSetting.getIsInitialized()) {
            HashMap<String, String> apiConfig = new HashMap();
            apiConfig.put("ServiceHost", serviceHost);
            apiConfig.put("SystemId", systemId);
            initialize(apiConfig);
        }

    }

    public static void initialize(HashMap<String, String> apiConfig) throws Exception {
        if (!ApiSetting.getIsInitialized()) {
            ApiSetting.InitBase();
            ApiSetting.InitSetting(apiConfig);
            ApiSetting.setIsInitialized(true);
            if (ApiSetting.getConfigEnable()) {
                Config.Init();
            }

            run();
        }

    }

    public static void run() throws Exception {
        if (ApiSetting.getHeartbeatEnable()) {
            Thread monitorThread = new Thread(new EarlyWarnHeartbeat());
            monitorThread.setName("UspMonitorThread");
            monitorThread.setDaemon(true);
            monitorThread.start();
        }

    }
}
