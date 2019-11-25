package com.donate.common.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ConfigLogicThread implements Runnable {
    public ConfigLogicThread() {
    }

    public void run() {
        Date lastUpdateTime = new Date();

        while(Config.IsStart) {
            try {
                Map<String, String> files = new HashMap();
                Map<String, String> items = new HashMap();
                Map<String, Map<String, String>> externalFilesMap = new HashMap();
                Map<String, Map<String, String>> externalItemsMap = new HashMap();
                if (ApiSetting.getConfigEnableAll()) {
                    long minutes = ((new Date()).getTime() - lastUpdateTime.getTime()) / 60000L;
                    HashMap externalFiles;
                    HashMap externalItems;
                    Iterator var10;
                    String systemId;
                    if (minutes >= (long)ApiSetting.getConfigGetAllInterval()) {
                        ConfigFetchManager.Instance.GetRemoteConfigs(files, items, true);
                        externalFiles = new HashMap();
                        externalItems = new HashMap();
                        var10 = Config.m_externalSystemIdList.keySet().iterator();

                        while(var10.hasNext()) {
                            systemId = (String)var10.next();
                            externalFiles.clear();
                            externalItems.clear();
                            ConfigFetchManager.Instance.GetExternalConfig(systemId, (String)Config.m_externalSystemIdList.get(systemId), "", externalFiles, externalItems, true);
                            if (externalFiles.size() > 0) {
                                externalFilesMap.put(systemId, externalFiles);
                            }

                            if (externalItemsMap.size() > 0) {
                                externalItemsMap.put(systemId, externalItems);
                            }
                        }

                        lastUpdateTime = new Date();
                    } else {
                        ConfigFetchManager.Instance.GetRemoteConfigs(files, items, false);
                        externalFiles = new HashMap();
                        externalItems = new HashMap();
                        var10 = Config.m_externalSystemIdList.keySet().iterator();

                        while(var10.hasNext()) {
                            systemId = (String)var10.next();
                            externalFiles.clear();
                            externalItems.clear();
                            ConfigFetchManager.Instance.GetExternalConfig(systemId, (String)Config.m_externalSystemIdList.get(systemId), "", externalFiles, externalItems, false);
                            if (externalFiles.size() > 0) {
                                externalFilesMap.put(systemId, externalFiles);
                            }

                            if (externalItems.size() > 0) {
                                externalItemsMap.put(systemId, externalItems);
                            }
                        }
                    }

                    if (files.size() > 0 || items.size() > 0 || externalFilesMap.size() > 0 || externalItemsMap.size() > 0) {
                        Config.fireChange(files, items, externalFilesMap, externalItemsMap);
                    }
                }
            } catch (Exception var22) {
                Exception ex = var22;

                try {
                    Log.LogText("配置中心从远程更新配置时出现错误", String.format("客户端ID:%s;错误信息:%s", ConfigFetchManager.Instance.ClientID, Config.exceptionToString(ex)), ConfigFetchManager.Instance.ClientID);
                    EarlyWarn.AlarmLog(EarlyWarnLevelEnum.严重错误, "配置中心从远程更新配置时出现错误", String.format("客户端ID:%s;错误信息:%s", ConfigFetchManager.Instance.ClientID, Config.exceptionToString(ex)));
                } catch (Exception var21) {
                    ;
                }
            } finally {
                try {
                    Thread.sleep((long)(ApiSetting.getConfigUpdateInterval() * 1000));
                } catch (InterruptedException var20) {
                    var20.printStackTrace();
                }

            }
        }

    }
}
