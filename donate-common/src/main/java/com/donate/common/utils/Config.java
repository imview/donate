package com.donate.common.utils;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class Config {
    private static List<ConfigChangeListener> m_listeners = new ArrayList();
    public static boolean IsStart = false;
    private static Object lockObj = new Object();
    public static Map<String, String> m_externalSystemIdList = new HashMap();
    public static List<String> m_externalSystemInitialled = new ArrayList();

    public Config() {
    }

    public static void Init() throws Exception {
        Init("");
    }

    public static void Init(String route) throws Exception {
        if (!ApiSetting.getIsInitialized()) {
            throw new Exception("Api尚未初始化!需要在程序【主线程】的【启动位置】增加初始化代码 ApiBaseService.Initialize(); ");
        } else if (ApiSetting.getConfigEnable()) {
            Object var1 = lockObj;
            synchronized(lockObj) {
                if (!IsStart) {
                    Log.LogText("配置中心开始初始化", "route = " + route, "");
                    if (!route.isEmpty()) {
                        ConfigFetchManager.Instance.Route = route;
                    }

                    if (CheckDir(ApiSetting.getConfigFactRootDirectory()) && CheckDir(ApiSetting.getConfigTmpRootDirectory())) {
                        if (ApiSetting.getConfigEnableRemote() && ApiSetting.getInitConfigSync()) {
                            Map<String, String> files = new HashMap();
                            Map<String, String> items = new HashMap();
                            Log.LogText("配置中心开始同步获取配置项", "route = " + route, "");
                            ConfigFetchManager.Instance.GetRemoteConfigs(files, items, true);
                            Log.LogText("配置中心同步获取配置项完成", "route = " + route + " items = " + items.size() + " files = " + files.size(), "");
                            Map<String, String> externalList = ConfigFetchManager.Instance.GetLocalExternalSystemIdList();
                            if (externalList.size() > 0) {
                                Map<String, String> externalFiles = new HashMap();
                                Map<String, String> externalItems = new HashMap();
                                Iterator var7 = externalList.keySet().iterator();

                                while(var7.hasNext()) {
                                    String systemId = (String)var7.next();
                                    ConfigFetchManager.Instance.GetExternalConfig(systemId, (String)externalList.get(systemId), "", externalFiles, externalItems, true);
                                    Log.LogText("配置中心同步获取外部系统[" + systemId + "]配置项完成", "route = " + route + " items = " + externalItems.size() + " files = " + externalFiles.size(), "");
                                    externalFiles.clear();
                                    externalItems.clear();
                                    m_externalSystemInitialled.add(systemId);
                                    m_externalSystemIdList.put(systemId, externalList.get(systemId));
                                }
                            }
                        }

                        GetLocalConfig();
                        if (ApiSetting.getConfigEnableRemote()) {
                            Log.LogText("配置中心启动守护线程", "route = " + route, "");
                            Thread configThread = new Thread(new ConfigLogicThread());
                            configThread.setName("UspConfigThread");
                            configThread.setDaemon(true);
                            configThread.start();
                        }

                        IsStart = true;
                        Log.LogText("配置中心初始化完成", "route = " + route, "");
                    } else {
                        throw new Exception("配置中心客户端初始化失败：无法创建目录");
                    }
                }
            }
        }
    }

    private static void GetLocalConfig() {
        try {
            Log.LogText("配置中心获取本地配置项开始,", "", "");
            Map<String, String> items = ConfigFetchManager.Instance.GetLocalItem();
            Map<String, String> files = ConfigFetchManager.Instance.GetLocalFile();
            Log.LogText("配置中心获取本地配置项完成", " items = " + items.size() + " files = " + files.size(), "");
            Map<String, String> externalList = ConfigFetchManager.Instance.GetLocalExternalSystemIdList();
            Iterator var3 = externalList.keySet().iterator();

            while(var3.hasNext()) {
                String systemId = (String)var3.next();
                items.clear();
                files.clear();
                Log.LogText("配置中心获取本地外部系统[" + systemId + "]配置项开始,", "", "");
                items = ConfigFetchManager.Instance.GetLocalExternalItem(systemId, (String)externalList.get(systemId));
                files = ConfigFetchManager.Instance.GetLocalExternalFile(systemId, (String)externalList.get(systemId));
                Log.LogText("配置中心获取本地外部系统[" + systemId + "]配置项完成,", " items = " + items.size() + " files = " + files.size(), "");
            }
        } catch (Exception var6) {
            Exception ex = var6;

            try {
                EarlyWarn.AlarmLog(EarlyWarnLevelEnum.严重错误, "配置中心从本地获取配置时出现错误", String.format("客户端ID:%s;错误信息:%s", ConfigFetchManager.Instance.ClientID, ex.getMessage()));
            } catch (Exception var5) {
                var5.printStackTrace();
            }
        }

    }

    public static void addChangeListener(ConfigChangeListener listener) {
        if (!m_listeners.contains(listener)) {
            m_listeners.add(listener);
        }

    }

    public static void removeChangeListener(ConfigChangeListener listener) {
        m_listeners.remove(listener);
    }

    protected static void fireChange(Map<String, String> files, Map<String, String> items, Map<String, Map<String, String>> externalFiles, Map<String, Map<String, String>> externalItems) throws Exception {
        Iterator var4 = m_listeners.iterator();

        while(var4.hasNext()) {
            ConfigChangeListener listener = (ConfigChangeListener)var4.next();

            try {
                listener.onChange(new ConfigChangeEvent(files, items, externalFiles, externalItems));
            } catch (Throwable var7) {
                EarlyWarn.AlarmLog(EarlyWarnLevelEnum.警告, "配置中心更新回调异常：" + var7.getMessage(), exceptionToString(var7));
                throw new Exception("Failed to invoke change listener : " + var7.getMessage());
            }
        }

    }

    public static String GetFileByName(String name) throws Exception {
        CheckState();
        return ConfigFetchManager.Instance.GetFileByName(name);
    }

    public static String GetValueByName(String name) throws Exception {
        CheckState();
        return ConfigFetchManager.Instance.GetValueByName(name);
    }

    public static String GetFileByName(String systemId, String version, String name) throws Exception {
        CheckState();
        if (ApiSetting.getConfigEnableRemote() && (!m_externalSystemIdList.containsKey(systemId) || compareVersion(version, (String)m_externalSystemIdList.get(systemId)) > 0)) {
            m_externalSystemInitialled.remove(systemId);
            synchronized(systemId) {
                if (!m_externalSystemInitialled.contains(systemId)) {
                    InitExternalConfig(systemId, (String)m_externalSystemIdList.get(systemId));
                    m_externalSystemInitialled.add(systemId);
                    m_externalSystemIdList.put(systemId, version);
                }
            }
        }

        return ConfigFetchManager.Instance.GetFileByName(systemId, name);
    }

    public static String GetValueByName(String systemId, String version, String name) throws Exception {
        CheckState();
        if (ApiSetting.getConfigEnableRemote() && (!m_externalSystemIdList.containsKey(systemId) || compareVersion(version, (String)m_externalSystemIdList.get(systemId)) > 0)) {
            m_externalSystemInitialled.remove(systemId);
            synchronized(systemId) {
                if (!m_externalSystemInitialled.contains(systemId)) {
                    InitExternalConfig(systemId, version);
                    m_externalSystemInitialled.add(systemId);
                    m_externalSystemIdList.put(systemId, version);
                }
            }
        }

        return ConfigFetchManager.Instance.GetValueByName(systemId, name);
    }

    private static void InitExternalConfig(String systemId, String version) throws Exception {
        Map<String, String> externalFiles = new HashMap();
        Map<String, String> externalItems = new HashMap();
        Log.LogText("初始化外部系统[" + systemId + "_" + version + "]配置项开始,", "", "");
        ConfigFetchManager.Instance.GetExternalConfig(systemId, version, "", externalFiles, externalItems, true);
        Log.LogText("初始化外部系统[" + systemId + "_" + version + "]配置项完成,", " items = " + externalFiles.size() + " files = " + externalItems.size(), "");
    }

    public static Map<String, String> GetAllValues() throws Exception {
        CheckState();
        return ConfigFetchManager.Instance.GetAllValues();
    }

    public static Map<String, String> GetAllFiles() throws Exception {
        CheckState();
        return ConfigFetchManager.Instance.GetAllFiles();
    }

    public static Map<String, String> GetAllValues(String systemId) throws Exception {
        CheckState();
        return ConfigFetchManager.Instance.GetAllValues(systemId);
    }

    public static Map<String, String> GetAllFiles(String systemId) throws Exception {
        CheckState();
        return ConfigFetchManager.Instance.GetAllFiles(systemId);
    }

    static void CheckState() throws Exception {
        if (!IsStart) {
            throw new Exception("配置中心客户端尚未初始化! ");
        }
    }

    public static String stackTraceToString(Throwable e) {
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] var2 = e.getStackTrace();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            StackTraceElement element = var2[var4];
            sb.append(element.toString());
            sb.append("\n");
        }

        return sb.toString();
    }

    public static String exceptionToString(Throwable e) {
        StringBuilder sb = new StringBuilder();
        sb.append(e.getMessage());
        sb.append("\n");
        StackTraceElement[] var2 = e.getStackTrace();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            StackTraceElement element = var2[var4];
            sb.append(element.toString());
            sb.append("\n");
        }

        return sb.toString();
    }

    public static boolean CheckDir(String destDirName) {
        File dir = new File(destDirName);
        if (dir.exists()) {
            return true;
        } else {
            if (!destDirName.endsWith(File.separator)) {
                destDirName = destDirName + File.separator;
            }

            return dir.mkdirs();
        }
    }

    public static int compareVersion(String version1, String version2) throws Exception {
        if (version1 != null && version2 != null) {
            String[] versionArray1 = version1.split("\\.");
            String[] versionArray2 = version2.split("\\.");
            int idx = 0;
            int minLength = Math.min(versionArray1.length, versionArray2.length);

            int diff;
            for(diff = 0; idx < minLength && (diff = versionArray1[idx].length() - versionArray2[idx].length()) == 0 && (diff = versionArray1[idx].compareTo(versionArray2[idx])) == 0; ++idx) {
                ;
            }

            diff = diff != 0 ? diff : versionArray1.length - versionArray2.length;
            return diff;
        } else {
            throw new Exception("compareVersion error:illegal params.");
        }
    }
}
