//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.donate.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ApiSetting {
    private static String ApiConfigPath = "Api.properties";
    private static Boolean IsInitialized = false;
    private static String Version = "v1.2.4,Java";
    private static Date SubtractDateTime = null;
    private static String SystemIP = "127.0.0.1";
    private static String CurrentHostIP = "127.0.0.1";
    private static String SystemId = "JavaDemo";
    private static String UspApiUrl = "";
    private static Boolean HeartbeatEnable = true;
    private static Boolean ServerMonitorEnable = false;
    private static String EarlyWarnHost = "";
    private static String EarlyWarnUserName = "earlywarn";
    private static String EarlyWarnPassword = "ucs2015";
    private static String EarlyWarnMQ = "EarlyWarnMQ";
    private static String EarlyWarnHeartbeatMQ = "EarlyWarnHeartbeatMQ";
    private static int EarlyWarnHeartbeatTime = 30;
    private static String LogHost = "";
    private static String LogUserName = "logtext";
    private static String LogPassword = "ucs2015";
    private static String LogMQ = "LogMQ";
    private static String DurableLogHost = "";
    private static String DurableLogUserName = "durablelogtext";
    private static String DurableLogPassword = "ucs2015";
    private static String DurableLogMQ = "DurableLogMQ";
    private static String MsgHost = "";
    private static String MsgUserName = "msgadmin";
    private static String MsgPassword = "ucs2015";
    private static String MsgMQ = "MsgMQ";
    private static String ConfigWebApiHost = "";
    private static Boolean ConfigEnableRemote = true;
    private static String ConfigVersion = "1.0.0.0";
    private static String ConfigClientName = "SDK测试";
    private static Boolean ConfigAbsolutePath = false;
    private static String ConfigTmpRootDirectory;
    private static String ConfigFactRootDirectory;
    private static String ConfigFactItemsFileName;
    private static String ConfigTmpItemsLocalName;
    private static String ConfigInsideConfig;
    private static String ConfigExternalConfig;
    private static Boolean ConfigEnableAll;
    private static Boolean ConfigEnable;
    private static Boolean InitConfigSync;
    private static int ConfigUpdateInterval;
    private static int ConfigGetAllInterval;

    public ApiSetting() {
    }

    protected static void InitBase() throws ParseException {
        SubtractDateTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")).parse("2016-01-01 00:00:00.000");

        try {
            CurrentHostIP = InetAddress.getLocalHost().getHostAddress();
            if (CurrentHostIP != "") {
                SystemIP = CurrentHostIP;
            }
        } catch (UnknownHostException var1) {
            CurrentHostIP = "null";
        }

    }

    protected static void InitSetting(HashMap<String, String> apiConfig) throws Exception {
        if (apiConfig != null && !apiConfig.isEmpty()) {
            if (apiConfig.containsKey("ServiceHost") && apiConfig.get("ServiceHost") != null) {
                UspApiUrl = "http://" + ((String)apiConfig.get("ServiceHost")).trim() + "/UspApi";
                EarlyWarnHost = ((String)apiConfig.get("ServiceHost")).trim();
                LogHost = ((String)apiConfig.get("ServiceHost")).trim();
                MsgHost = ((String)apiConfig.get("ServiceHost")).trim();
                DurableLogHost = ((String)apiConfig.get("ServiceHost")).trim();
            }

            if (apiConfig.containsKey("SystemIP") && apiConfig.get("SystemIP") != null) {
                SystemIP = ((String)apiConfig.get("SystemIP")).trim();
            }

            if (apiConfig.containsKey("SystemId") && apiConfig.get("SystemId") != null) {
                SystemId = ((String)apiConfig.get("SystemId")).trim();
            }

            if (apiConfig.containsKey("UspApiUrl") && apiConfig.get("UspApiUrl") != null) {
                UspApiUrl = ((String)apiConfig.get("UspApiUrl")).trim();
                if (UspApiUrl.toLowerCase().indexOf("http") < 0) {
                    UspApiUrl = "http://" + UspApiUrl;
                }
            }

            if (apiConfig.containsKey("HeartbeatEnable") && apiConfig.get("HeartbeatEnable") != null) {
                HeartbeatEnable = Boolean.valueOf(((String)apiConfig.get("HeartbeatEnable")).trim());
            }

            if (apiConfig.containsKey("ServerMonitorEnable") && apiConfig.get("ServerMonitorEnable") != null) {
                ServerMonitorEnable = Boolean.valueOf(((String)apiConfig.get("ServerMonitorEnable")).trim());
            }

            if (apiConfig.containsKey("EarlyWarnHost") && apiConfig.get("EarlyWarnHost") != null) {
                EarlyWarnHost = ((String)apiConfig.get("EarlyWarnHost")).trim();
            }

            if (apiConfig.containsKey("EarlyWarnUserName") && apiConfig.get("EarlyWarnUserName") != null) {
                EarlyWarnUserName = ((String)apiConfig.get("EarlyWarnUserName")).trim();
            }

            if (apiConfig.containsKey("EarlyWarnPassword") && apiConfig.get("EarlyWarnPassword") != null) {
                EarlyWarnPassword = ((String)apiConfig.get("EarlyWarnPassword")).trim();
            }

            if (apiConfig.containsKey("EarlyWarnMQ") && apiConfig.get("EarlyWarnMQ") != null) {
                EarlyWarnMQ = ((String)apiConfig.get("EarlyWarnMQ")).trim();
            }

            if (apiConfig.containsKey("EarlyWarnHeartbeatMQ") && apiConfig.get("EarlyWarnHeartbeatMQ") != null) {
                EarlyWarnHeartbeatMQ = ((String)apiConfig.get("EarlyWarnHeartbeatMQ")).trim();
            }

            if (apiConfig.containsKey("EarlyWarnHeartbeatTime") && apiConfig.get("EarlyWarnHeartbeatTime") != null) {
                EarlyWarnHeartbeatTime = Integer.valueOf(((String)apiConfig.get("EarlyWarnHeartbeatTime")).trim());
            }

            if (apiConfig.containsKey("LogHost") && apiConfig.get("LogHost") != null) {
                LogHost = ((String)apiConfig.get("LogHost")).trim();
            }

            if (apiConfig.containsKey("LogUserName") && apiConfig.get("LogUserName") != null) {
                LogUserName = ((String)apiConfig.get("LogUserName")).trim();
            }

            if (apiConfig.containsKey("LogPassword") && apiConfig.get("LogPassword") != null) {
                LogPassword = ((String)apiConfig.get("LogPassword")).trim();
            }

            if (apiConfig.containsKey("LogMQ") && apiConfig.get("LogMQ") != null) {
                LogMQ = ((String)apiConfig.get("LogMQ")).trim();
            }

            if (apiConfig.containsKey("DurableLogHost") && apiConfig.get("DurableLogHost") != null) {
                DurableLogHost = ((String)apiConfig.get("DurableLogHost")).trim();
            }

            if (apiConfig.containsKey("DurableLogUserName") && apiConfig.get("DurableLogUserName") != null) {
                DurableLogUserName = ((String)apiConfig.get("DurableLogUserName")).trim();
            }

            if (apiConfig.containsKey("DurableLogPassword") && apiConfig.get("DurableLogPassword") != null) {
                DurableLogPassword = ((String)apiConfig.get("DurableLogPassword")).trim();
            }

            if (apiConfig.containsKey("DurableLogMQ") && apiConfig.get("DurableLogMQ") != null) {
                DurableLogMQ = ((String)apiConfig.get("DurableLogMQ")).trim();
            }

            if (apiConfig.containsKey("MsgHost") && apiConfig.get("MsgHost") != null) {
                MsgHost = ((String)apiConfig.get("MsgHost")).trim();
            }

            if (apiConfig.containsKey("MsgUserName") && apiConfig.get("MsgUserName") != null) {
                MsgUserName = ((String)apiConfig.get("MsgUserName")).trim();
            }

            if (apiConfig.containsKey("MsgPassword") && apiConfig.get("MsgPassword") != null) {
                MsgPassword = ((String)apiConfig.get("MsgPassword")).trim();
            }

            if (apiConfig.containsKey("MsgMQ") && apiConfig.get("MsgMQ") != null) {
                MsgMQ = ((String)apiConfig.get("MsgMQ")).trim();
            }

            if (apiConfig.containsKey("ConfigWebApiHost") && apiConfig.get("ConfigWebApiHost") != null) {
                ConfigWebApiHost = ((String)apiConfig.get("ConfigWebApiHost")).trim();
            }

            if (apiConfig.containsKey("ConfigEnableRemote") && apiConfig.get("ConfigEnableRemote") != null) {
                ConfigEnableRemote = Boolean.valueOf(((String)apiConfig.get("ConfigEnableRemote")).trim());
            }

            if (apiConfig.containsKey("ConfigVersion") && apiConfig.get("ConfigVersion") != null) {
                ConfigVersion = ((String)apiConfig.get("ConfigVersion")).trim();
            }

            if (apiConfig.containsKey("ConfigClientName") && apiConfig.get("ConfigClientName") != null) {
                ConfigClientName = ((String)apiConfig.get("ConfigClientName")).trim();
            }

            if (apiConfig.containsKey("ConfigAbsolutePath") && apiConfig.get("ConfigAbsolutePath") != null) {
                ConfigAbsolutePath = Boolean.valueOf(((String)apiConfig.get("ConfigAbsolutePath")).trim());
            }

            if (apiConfig.containsKey("ConfigTmpRootDirectory") && apiConfig.get("ConfigTmpRootDirectory") != null) {
                ConfigTmpRootDirectory = ((String)apiConfig.get("ConfigTmpRootDirectory")).trim();
            }

            if (apiConfig.containsKey("ConfigFactRootDirectory") && apiConfig.get("ConfigFactRootDirectory") != null) {
                ConfigFactRootDirectory = ((String)apiConfig.get("ConfigFactRootDirectory")).trim();
            }

            if (apiConfig.containsKey("ConfigFactItemsFileName") && apiConfig.get("ConfigFactItemsFileName") != null) {
                ConfigFactItemsFileName = ((String)apiConfig.get("ConfigFactItemsFileName")).trim();
            }

            if (apiConfig.containsKey("ConfigTmpItemsLocalName") && apiConfig.get("ConfigTmpItemsLocalName") != null) {
                ConfigTmpItemsLocalName = ((String)apiConfig.get("ConfigTmpItemsLocalName")).trim();
            }

            if (apiConfig.containsKey("ConfigInsideConfig") && apiConfig.get("ConfigInsideConfig") != null) {
                ConfigInsideConfig = ((String)apiConfig.get("ConfigInsideConfig")).trim();
            }

            if (apiConfig.containsKey("ConfigExternalConfig") && apiConfig.get("ConfigExternalConfig") != null) {
                ConfigExternalConfig = ((String)apiConfig.get("ConfigExternalConfig")).trim();
            }

            if (apiConfig.containsKey("ConfigEnableAll") && apiConfig.get("ConfigEnableAll") != null) {
                ConfigEnableAll = Boolean.valueOf(((String)apiConfig.get("ConfigEnableAll")).trim());
            }

            if (apiConfig.containsKey("ConfigUpdateInterval") && apiConfig.get("ConfigUpdateInterval") != null) {
                ConfigUpdateInterval = Integer.valueOf(((String)apiConfig.get("ConfigUpdateInterval")).trim());
            }

            if (apiConfig.containsKey("ConfigGetAllInterval") && apiConfig.get("ConfigGetAllInterval") != null) {
                ConfigGetAllInterval = Integer.valueOf(((String)apiConfig.get("ConfigGetAllInterval")).trim());
            }

            if (apiConfig.containsKey("ConfigEnable") && apiConfig.get("ConfigEnable") != null) {
                ConfigEnable = Boolean.valueOf(((String)apiConfig.get("ConfigEnable")).trim());
            }

            if (apiConfig.containsKey("InitConfigSync") && apiConfig.get("InitConfigSync") != null) {
                InitConfigSync = Boolean.valueOf(((String)apiConfig.get("InitConfigSync")).trim());
            }

        } else {
            throw new Exception("找不到配置参数!");
        }
    }

    protected static void InitSetting(String customApiConfigPath) throws Exception {
        if (!IsInitialized) {
            if (customApiConfigPath != null && !customApiConfigPath.isEmpty()) {
                ApiConfigPath = customApiConfigPath;
            } else {
                ApiConfigPath = (new File("")).getCanonicalPath() + "\\Api.properties";
            }

            File file = null;
            FileReader fReader = null;
            BufferedReader buffer = null;

            try {
                if (ApiConfigPath != null && !ApiConfigPath.isEmpty()) {
                    file = new File(ApiConfigPath);
                    if (file.isFile() && file.exists()) {
                        fReader = new FileReader(file);
                        buffer = new BufferedReader(fReader);
                        String line = null;

                        while((line = buffer.readLine()) != null) {
                            line = line.trim();
                            if (!line.isEmpty()) {
                                if (line.startsWith("ServiceHost=")) {
                                    UspApiUrl = "http://" + line.replaceFirst("ServiceHost=", "") + "/UspApi";
                                    EarlyWarnHost = line.replaceFirst("ServiceHost=", "");
                                    LogHost = line.replaceFirst("ServiceHost=", "");
                                    MsgHost = line.replaceFirst("ServiceHost=", "");
                                    DurableLogHost = line.replaceFirst("ServiceHost=", "");
                                } else if (line.startsWith("SystemId=")) {
                                    SystemId = line.replaceFirst("SystemId=", "");
                                } else if (line.startsWith("SystemIP=")) {
                                    SystemIP = line.replaceFirst("SystemIP=", "");
                                } else if (line.startsWith("UspApiUrl=")) {
                                    UspApiUrl = line.replaceFirst("UspApiUrl=", "");
                                    if (UspApiUrl.toLowerCase().indexOf("http") < 0) {
                                        UspApiUrl = "http://" + UspApiUrl;
                                    }
                                } else if (line.startsWith("HeartbeatEnable=")) {
                                    HeartbeatEnable = Boolean.valueOf(line.replaceFirst("HeartbeatEnable=", ""));
                                } else if (line.startsWith("ServerMonitorEnable=")) {
                                    ServerMonitorEnable = Boolean.valueOf(line.replaceFirst("ServerMonitorEnable=", ""));
                                } else if (line.startsWith("EarlyWarnHost=")) {
                                    EarlyWarnHost = line.replaceFirst("EarlyWarnHost=", "");
                                } else if (line.startsWith("EarlyWarnUserName=")) {
                                    EarlyWarnUserName = line.replaceFirst("EarlyWarnUserName=", "");
                                } else if (line.startsWith("EarlyWarnPassword=")) {
                                    EarlyWarnPassword = line.replaceFirst("EarlyWarnPassword=", "");
                                } else if (line.startsWith("EarlyWarnMQ=")) {
                                    EarlyWarnMQ = line.replaceFirst("EarlyWarnMQ=", "");
                                } else if (line.startsWith("EarlyWarnHeartbeatMQ=")) {
                                    EarlyWarnHeartbeatMQ = line.replaceFirst("EarlyWarnHeartbeatMQ=", "");
                                } else if (line.startsWith("EarlyWarnHeartbeatTime=")) {
                                    EarlyWarnHeartbeatTime = Integer.valueOf(line.replaceFirst("EarlyWarnHeartbeatTime=", ""));
                                } else if (line.startsWith("LogHost=")) {
                                    LogHost = line.replaceFirst("LogHost=", "");
                                } else if (line.startsWith("LogUserName=")) {
                                    LogUserName = line.replaceFirst("LogUserName=", "");
                                } else if (line.startsWith("LogPassword=")) {
                                    LogPassword = line.replaceFirst("LogPassword=", "");
                                } else if (line.startsWith("LogMQ=")) {
                                    LogMQ = line.replaceFirst("LogMQ=", "");
                                } else if (line.startsWith("DurableLogHost=")) {
                                    DurableLogHost = line.replaceFirst("DurableLogHost=", "");
                                } else if (line.startsWith("DurableLogUserName=")) {
                                    DurableLogUserName = line.replaceFirst("DurableLogUserName=", "");
                                } else if (line.startsWith("DurableLogPassword=")) {
                                    DurableLogPassword = line.replaceFirst("DurableLogPassword=", "");
                                } else if (line.startsWith("DurableLogMQ=")) {
                                    DurableLogMQ = line.replaceFirst("DurableLogMQ=", "");
                                } else if (line.startsWith("MsgHost=")) {
                                    MsgHost = line.replaceFirst("MsgHost=", "");
                                } else if (line.startsWith("MsgUserName=")) {
                                    MsgUserName = line.replaceFirst("MsgUserName=", "");
                                } else if (line.startsWith("MsgPassword=")) {
                                    MsgPassword = line.replaceFirst("MsgPassword=", "");
                                } else if (line.startsWith("MsgMQ=")) {
                                    MsgMQ = line.replaceFirst("MsgMQ=", "");
                                } else if (line.startsWith("ConfigWebApiHost=")) {
                                    ConfigWebApiHost = line.replaceFirst("ConfigWebApiHost=", "");
                                } else if (line.startsWith("ConfigEnableRemote=")) {
                                    ConfigEnableRemote = Boolean.valueOf(line.replaceFirst("ConfigEnableRemote=", ""));
                                } else if (line.startsWith("ConfigVersion=")) {
                                    ConfigVersion = line.replaceFirst("ConfigVersion=", "");
                                } else if (line.startsWith("ConfigClientName=")) {
                                    ConfigClientName = line.replaceFirst("ConfigClientName=", "");
                                } else if (line.startsWith("ConfigAbsolutePath=")) {
                                    ConfigAbsolutePath = Boolean.valueOf(line.replaceFirst("ConfigAbsolutePath=", ""));
                                } else if (line.startsWith("ConfigTmpRootDirectory=")) {
                                    ConfigTmpRootDirectory = line.replaceFirst("ConfigTmpRootDirectory=", "");
                                } else if (line.startsWith("ConfigFactRootDirectory=")) {
                                    ConfigFactRootDirectory = line.replaceFirst("ConfigFactRootDirectory=", "");
                                } else if (line.startsWith("ConfigFactItemsFileName=")) {
                                    ConfigFactItemsFileName = line.replaceFirst("ConfigFactItemsFileName=", "");
                                } else if (line.startsWith("ConfigTmpItemsLocalName=")) {
                                    ConfigTmpItemsLocalName = line.replaceFirst("ConfigTmpItemsLocalName=", "");
                                } else if (line.startsWith("ConfigInsideConfig=")) {
                                    ConfigInsideConfig = line.replaceFirst("ConfigInsideConfig=", "");
                                } else if (line.startsWith("ConfigExternalConfig=")) {
                                    ConfigExternalConfig = line.replaceFirst("ConfigExternalConfig=", "");
                                } else if (line.startsWith("ConfigEnableAll=")) {
                                    ConfigEnableAll = Boolean.valueOf(line.replaceFirst("ConfigEnableAll=", ""));
                                } else if (line.startsWith("ConfigUpdateInterval=")) {
                                    ConfigUpdateInterval = Integer.valueOf(line.replaceFirst("ConfigUpdateInterval=", ""));
                                } else if (line.startsWith("ConfigGetAllInterval=")) {
                                    ConfigGetAllInterval = Integer.valueOf(line.replaceFirst("ConfigGetAllInterval=", ""));
                                } else if (line.startsWith("ConfigEnable=")) {
                                    ConfigEnable = Boolean.valueOf(line.replaceFirst("ConfigEnable=", ""));
                                } else if (line.startsWith("InitConfigSync=")) {
                                    InitConfigSync = Boolean.valueOf(line.replaceFirst("InitConfigSync=", ""));
                                }
                            }
                        }

                    } else {
                        throw new Exception("找不到配置文件![" + ApiConfigPath + "]");
                    }
                } else {
                    throw new Exception("找不到配置文件!文件路径为空!");
                }
            } catch (Exception var15) {
                throw var15;
            } finally {
                if (buffer != null) {
                    try {
                        buffer.close();
                    } catch (Exception var14) {
                        ;
                    }
                }

                if (fReader != null) {
                    try {
                        fReader.close();
                    } catch (Exception var13) {
                        ;
                    }
                }

                if (file != null) {
                    file = null;
                }

            }
        }
    }

    protected static Boolean getIsInitialized() {
        return IsInitialized;
    }

    protected static void setIsInitialized(Boolean isInitialized) {
        IsInitialized = isInitialized;
    }

    protected static String getVersion() {
        return Version;
    }

    public static Date getSubtractDateTime() {
        return SubtractDateTime;
    }

    protected static String getSystemIP() {
        return SystemIP;
    }

    protected static String getCurrentHostIP() {
        return CurrentHostIP;
    }

    protected static String getSystemId() {
        return SystemId;
    }

    protected static String getUspApiUrl() {
        return UspApiUrl;
    }

    public static Boolean getHeartbeatEnable() {
        return HeartbeatEnable;
    }

    public static Boolean getServerMonitorEnable() {
        return ServerMonitorEnable;
    }

    protected static String getEarlyWarnHost() {
        return EarlyWarnHost;
    }

    protected static String getEarlyWarnUserName() {
        return EarlyWarnUserName;
    }

    protected static String getEarlyWarnPassword() {
        return EarlyWarnPassword;
    }

    protected static String getEarlyWarnMQ() {
        return EarlyWarnMQ;
    }

    protected static String getEarlyWarnHeartbeatMQ() {
        return EarlyWarnHeartbeatMQ;
    }

    protected static int getEarlyWarnHeartbeatTime() {
        return EarlyWarnHeartbeatTime;
    }

    protected static String getLogHost() {
        return LogHost;
    }

    protected static String getLogUserName() {
        return LogUserName;
    }

    protected static String getLogPassword() {
        return LogPassword;
    }

    protected static String getLogMQ() {
        return LogMQ;
    }

    protected static String getDurableLogHost() {
        return DurableLogHost;
    }

    protected static String getDurableLogUserName() {
        return DurableLogUserName;
    }

    protected static String getDurableLogPassword() {
        return DurableLogPassword;
    }

    protected static String getDurableLogMQ() {
        return DurableLogMQ;
    }

    protected static String getMsgHost() {
        return MsgHost;
    }

    protected static String getMsgUserName() {
        return MsgUserName;
    }

    protected static String getMsgPassword() {
        return MsgPassword;
    }

    protected static String getMsgMQ() {
        return MsgMQ;
    }

    public static String getConfigWebApiHost() {
        return ConfigWebApiHost;
    }

    public static boolean getConfigEnableRemote() {
        return ConfigEnableRemote;
    }

    public static boolean getConfigEnableAll() {
        return ConfigEnableAll;
    }

    public static String getConfigVersion() {
        return ConfigVersion;
    }

    public static String getConfigClientName() {
        return ConfigClientName;
    }

    public static boolean getConfigAbsolutePath() {
        return ConfigAbsolutePath;
    }

    public static String getConfigTmpRootDirectory() {
        return ConfigTmpRootDirectory;
    }

    public static String getConfigFactRootDirectory() {
        return ConfigFactRootDirectory;
    }

    public static String getConfigFactItemsFileName() {
        return ConfigFactItemsFileName;
    }

    public static String getConfigTmpItemsLocalName() {
        return ConfigTmpItemsLocalName;
    }

    public static String getConfigInsideConfig() {
        return ConfigInsideConfig;
    }

    public static String getConfigExternalConfig() {
        return ConfigExternalConfig;
    }

    public static int getConfigUpdateInterval() {
        return ConfigUpdateInterval;
    }

    public static int getConfigGetAllInterval() {
        return ConfigGetAllInterval;
    }

    public static boolean getConfigEnable() {
        return ConfigEnable;
    }

    public static boolean getInitConfigSync() {
        return InitConfigSync;
    }

    static {
        ConfigTmpRootDirectory = "Tmp" + File.separator + "Download" + File.separator + "Configs";
        ConfigFactRootDirectory = "Configs";
        ConfigFactItemsFileName = "items.properties";
        ConfigTmpItemsLocalName = "~items.properties";
        ConfigInsideConfig = "";
        ConfigExternalConfig = "";
        ConfigEnableAll = true;
        ConfigEnable = false;
        InitConfigSync = true;
        ConfigUpdateInterval = 10;
        ConfigGetAllInterval = 30;
    }
}
