package com.donate.common.utils;

import com.rabbitmq.tools.json.JSONReader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

public class ConfigFetchManager {
    public static ConfigFetchManager Instance = null;
    final String GetInsideConfigResource = "%s/ConfigApi/GetInsideConfig?clientID=%s&systemId=%s&version=%s&names=%s&clientName=%s";
    final String GetExternalConfigResource = "%s/ConfigApi/GetExternalConfig?clientID=%s&systemId=%s&externalSystemId=%s&version=%s&names=%s&isAll=%s&clientName=%s";
    final String GetRemoteConfigsResource = "%s/ConfigApi/GetAllConfig?clientID=%s&systemId=%s&version=%s&isAll=%s&clientName=%s";
    public String ClientID = "";
    public String Route = "";
    private Map<String, String> ItemDicCache = new HashMap();
    private Map<String, String> FileDicCache = new HashMap();
    private Map<String, String> ExternalItemDicCache = new HashMap();
    private Map<String, String> ExternalFileDicCache = new HashMap();
    private final Object lock = new Object();

    private ConfigFetchManager() {
        String clientIdFilePath = ApiSetting.getConfigFactRootDirectory() + File.separator + "clientid.txt";
        File clientFile = new File(clientIdFilePath);
        InputStreamReader read;
        if (clientFile.exists()) {
            try {
                read = null;
                read = new InputStreamReader(new FileInputStream(clientFile), "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(read);
                this.ClientID = bufferedReader.readLine();
                bufferedReader.close();
            } catch (Exception var7) {
                var7.printStackTrace();
            }
        } else {
            read = null;

            try {
                OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(clientFile), "UTF-8");
                BufferedWriter bufferedWriter = new BufferedWriter(writer);
                String s = UUID.randomUUID().toString();
                bufferedWriter.write(s);
                bufferedWriter.flush();
                bufferedWriter.close();
                this.ClientID = s;
            } catch (Exception var6) {
                var6.printStackTrace();
            }
        }

        if (!this.Route.isEmpty()) {
            this.ClientID = this.ClientID + "_" + (this.Route.length() > 10 ? this.Route.substring(0, 9) : this.Route);
        }

    }

    public String GetValueByName(String name) throws Exception {
        return this.ItemDicCache.containsKey(name) ? (String)this.ItemDicCache.get(name) : null;
    }

    public String GetValueByName(String systemId, String name) throws Exception {
        String key = systemId + "_" + name;
        return this.ExternalItemDicCache.containsKey(key) ? (String)this.ExternalItemDicCache.get(key) : null;
    }

    public String GetFileByName(String name) throws Exception {
        return this.FileDicCache.containsKey(name) ? (String)this.FileDicCache.get(name) : null;
    }

    public String GetFileByName(String systemId, String name) throws Exception {
        String key = systemId + "_" + name;
        return this.ExternalFileDicCache.containsKey(key) ? (String)this.ExternalFileDicCache.get(key) : null;
    }

    public Map<String, String> GetAllValues() throws Exception {
        Map<String, String> map = new HashMap();
        map.putAll(this.ItemDicCache);
        return map;
    }

    public Map<String, String> GetAllFiles() throws Exception {
        Map<String, String> map = new HashMap();
        map.putAll(this.FileDicCache);
        return map;
    }

    public Map<String, String> GetAllValues(String systemId) throws Exception {
        Map<String, String> map = new HashMap();
        Iterator var3 = this.ExternalItemDicCache.keySet().iterator();

        while(var3.hasNext()) {
            String key = (String)var3.next();
            String[] arr = key.split("_");
            if (arr[0].equals(systemId)) {
                map.put(arr[1], this.ExternalItemDicCache.get(key));
            }
        }

        return map;
    }

    public Map<String, String> GetAllFiles(String systemId) throws Exception {
        Map<String, String> map = new HashMap();
        Iterator var3 = this.ExternalFileDicCache.keySet().iterator();

        while(var3.hasNext()) {
            String key = (String)var3.next();
            String[] arr = key.split("_");
            if (arr[0].equals(systemId)) {
                map.put(arr[1], this.ExternalFileDicCache.get(key));
            }
        }

        return map;
    }

    public void GetRemoteConfigs(Map<String, String> files, Map<String, String> items, boolean isAll) throws Exception {
        String url = String.format("%s/ConfigApi/GetAllConfig?clientID=%s&systemId=%s&version=%s&isAll=%s&clientName=%s", ApiSetting.getConfigWebApiHost(), this.ClientID, ApiSetting.getSystemId(), ApiSetting.getConfigVersion(), isAll, URLEncoder.encode(ApiSetting.getConfigClientName(), "UTF-8"));
        this.RequestAnalysis(url, files, items, ApiSetting.getSystemId(), ApiSetting.getConfigVersion());
    }

    public void GetExternalConfig(String externalSystemId, String version, String names, Map<String, String> files, Map<String, String> items, boolean isAll) throws Exception {
        String url = String.format("%s/ConfigApi/GetExternalConfig?clientID=%s&systemId=%s&externalSystemId=%s&version=%s&names=%s&isAll=%s&clientName=%s", ApiSetting.getConfigWebApiHost(), this.ClientID, ApiSetting.getSystemId(), externalSystemId, version, names, isAll, URLEncoder.encode(ApiSetting.getConfigClientName(), "UTF-8"));
        this.RequestAnalysis(url, files, items, externalSystemId, version);
    }

    private void RequestAnalysis(String url, Map<String, String> files, Map<String, String> items, String systemId, String ver) throws Exception {
        boolean isExternal = !systemId.equals(ApiSetting.getSystemId());
        String jsonStr = "";

        Object json;
        try {
            jsonStr = HttpPost(url, (HashMap)null, "UTF-8");
            JSONReader reader = new JSONReader();
            json = (Map)reader.read(jsonStr);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            System.out.println(df.format(new Date()) + " url:" + url + " json: " + json);
        } catch (Exception var17) {
            Log.LogText("客户端从远程更新配置时出现错误", String.format("客户端ID:$s;请求地址:$s;错误信息:$s", this.ClientID, url, var17), this.ClientID);
            EarlyWarn.AlarmLog(EarlyWarnLevelEnum.严重错误, "客户端从远程更新配置时出现错误", String.format("客户端ID:$s;请求地址:$s;错误信息:$s", this.ClientID, url, var17));
            json = new HashMap();
            ((Map)json).put("IsError", true);
            ((Map)json).put("IsOk", false);
        }

        if (((Map)json).get("IsOk") != null && Boolean.valueOf(((Map)json).get("IsOk").toString()) && ((Map)json).get("ReturnData") != null && ((List)((List)((Map)json).get("ReturnData"))).size() > 0) {
            List<Object> returnData = (List)((List)((Map)json).get("ReturnData"));
            Iterator var19 = returnData.iterator();

            while(true) {
                String name;
                String content;
                do {
                    while(true) {
                        int type;
                        String fullname;
                        do {
                            if (!var19.hasNext()) {
                                if (items != null && items.size() > 0) {
                                    if (!isExternal) {
                                        this.SaveItem(items);
                                    } else {
                                        this.SaveExternalItem(systemId, ver, items);
                                    }

                                    return;
                                }

                                return;
                            }

                            Object item = var19.next();
                            Map<String, Object> jsonObj = (Map)item;
                            type = Integer.valueOf(jsonObj.get("Type").toString());
                            name = (String)((String)jsonObj.get("Name"));
                            content = (String)((String)jsonObj.get("Content"));
                            if (type == 2) {
                                if (!isExternal) {
                                    if (!this.FileDicCache.containsKey(name) || !((String)this.FileDicCache.get(name)).equals(content)) {
                                        files.put(name, content);
                                        this.SaveFile(name, content);
                                    }
                                } else {
                                    fullname = systemId + "_" + name;
                                    if (!this.ExternalFileDicCache.containsKey(fullname) || !((String)this.ExternalFileDicCache.get(fullname)).equals(content)) {
                                        files.put(name, content);
                                        this.SaveExternalFile(systemId, ver, name, content);
                                    }
                                }
                            }
                        } while(type != 1);

                        if (!isExternal) {
                            break;
                        }

                        fullname = systemId + "_" + name;
                        if (!this.ExternalItemDicCache.containsKey(fullname) || !((String)this.ExternalItemDicCache.get(fullname)).equals(content)) {
                            items.put(name, content);
                        }
                    }
                } while(this.ItemDicCache.containsKey(name) && ((String)this.ItemDicCache.get(name)).equals(content));

                items.put(name, content);
            }
        } else if (((Map)json).get("IsError") != null && Boolean.valueOf(((Map)json).get("IsError").toString())) {
            ;
        }

    }

    public Map<String, String> GetLocalItem() throws Exception {
        Hashtable itemDic = new Hashtable();

        try {
            String factPath = ApiSetting.getConfigFactRootDirectory() + File.separator + ApiSetting.getConfigFactItemsFileName();
            if (isFileExist(factPath)) {
                Properties pps = new Properties();
                pps.load(new FileInputStream(factPath));
                Enumeration enum1 = pps.propertyNames();

                while(enum1.hasMoreElements()) {
                    String strKey = (String)enum1.nextElement();
                    String strValue = pps.getProperty(strKey);
                    itemDic.put(strKey, strValue);
                    this.ItemDicCache.put(strKey, strValue);
                }
            }

            return itemDic;
        } catch (Exception var7) {
            throw new Exception(String.format("GetLocalItem Exception:%s", var7.getMessage()));
        }
    }

    public Map<String, String> GetLocalExternalItem(String systemId, String ver) throws Exception {
        Hashtable itemDic = new Hashtable();

        try {
            String dir = ApiSetting.getConfigFactRootDirectory() + File.separator + systemId + "_" + ver + File.separator;
            CreateDir(dir);
            String factPath = dir + ApiSetting.getConfigFactItemsFileName();
            if (isFileExist(factPath)) {
                Properties pps = new Properties();
                pps.load(new FileInputStream(factPath));
                Enumeration enum1 = pps.propertyNames();

                while(enum1.hasMoreElements()) {
                    String strKey = (String)enum1.nextElement();
                    String strValue = pps.getProperty(strKey);
                    itemDic.put(strKey, strValue);
                    this.ExternalItemDicCache.put(systemId + "_" + strKey, strValue);
                }
            }

            return itemDic;
        } catch (Exception var10) {
            throw new Exception(String.format("GetLocalItem Exception:%s", var10.getMessage()));
        }
    }

    public Map<String, String> GetLocalFile() throws Exception {
        Hashtable fileDic = new Hashtable();

        try {
            String factPath = ApiSetting.getConfigFactRootDirectory() + File.separator;
            File dir = new File(factPath);
            File[] lists = dir.listFiles();
            if (lists != null) {
                for(int i = 0; i < lists.length; ++i) {
                    if (!lists[i].isDirectory() && !lists[i].getName().equals(ApiSetting.getConfigFactItemsFileName()) && !lists[i].getName().equals("clientid.txt")) {
                        InputStreamReader read = new InputStreamReader(new FileInputStream(lists[i]), "UTF-8");
                        BufferedReader bufferedReader = new BufferedReader(read);
                        StringBuilder sb = new StringBuilder();

                        String line;
                        while((line = bufferedReader.readLine()) != null) {
                            sb.append(line);
                            sb.append(System.getProperty("line.separator"));
                        }

                        read.close();
                        fileDic.put(lists[i].getName(), sb.toString());
                        this.FileDicCache.put(lists[i].getName(), sb.toString());
                    }
                }
            }

            return fileDic;
        } catch (Exception var10) {
            throw new Exception(String.format("GetLocalFile Exception:%s", var10.getMessage()));
        }
    }

    public Map<String, String> GetLocalExternalFile(String systemId, String ver) throws Exception {
        Hashtable fileDic = new Hashtable();

        try {
            String factPath = ApiSetting.getConfigFactRootDirectory() + File.separator + systemId + "_" + ver + File.separator;
            CreateDir(factPath);
            File dir = new File(factPath);
            File[] lists = dir.listFiles();
            if (lists != null) {
                for(int i = 0; i < lists.length; ++i) {
                    if (!lists[i].getName().equals(ApiSetting.getConfigFactItemsFileName())) {
                        InputStreamReader read = new InputStreamReader(new FileInputStream(lists[i]), "UTF-8");
                        BufferedReader bufferedReader = new BufferedReader(read);
                        StringBuilder sb = new StringBuilder();

                        String line;
                        while((line = bufferedReader.readLine()) != null) {
                            sb.append(line);
                            sb.append(System.getProperty("line.separator"));
                        }

                        read.close();
                        fileDic.put(lists[i].getName(), sb.toString());
                        this.ExternalFileDicCache.put(systemId + "_" + lists[i].getName(), sb.toString());
                    }
                }
            }

            return fileDic;
        } catch (Exception var12) {
            throw new Exception(String.format("GetLocalFile Exception:%s", var12.getMessage()));
        }
    }

    public Map<String, String> GetLocalExternalSystemIdList() throws Exception {
        Map<String, String> re = new HashMap();
        String factPath = ApiSetting.getConfigFactRootDirectory() + File.separator;
        CreateDir(factPath);
        File dir = new File(factPath);
        File[] lists = dir.listFiles();
        if (lists != null) {
            for(int i = 0; i < lists.length; ++i) {
                if (lists[i].isDirectory()) {
                    String[] arr = lists[i].getName().split("_");
                    if (arr.length >= 2) {
                        if (re.containsKey(arr[0])) {
                            String ver = (String)re.get(arr[0]);
                            if (Config.compareVersion(arr[0], ver) > 0) {
                                re.put(arr[0], arr[1]);
                                String oldVersionDir = ApiSetting.getConfigFactRootDirectory() + File.separator + arr[0] + "_" + ver;
                                (new File(oldVersionDir)).delete();
                            }
                        } else {
                            re.put(arr[0], arr[1]);
                        }
                    }
                }
            }
        }

        return re;
    }

    private void SaveFile(String configName, String content) throws Exception {
        try {
            this.FileDicCache.put(configName, content);
            String tmpPath = ApiSetting.getConfigTmpRootDirectory() + File.separator + configName;
            String factPath = ApiSetting.getConfigFactRootDirectory() + File.separator + configName;
            OutputStream os1 = new FileOutputStream(tmpPath);
            PrintStream pPRINT = new PrintStream(os1, true, "UTF-8");
            pPRINT.println(content);
            pPRINT.close();
            Copy(tmpPath, factPath);
        } catch (Exception var7) {
            Log.LogText("FetchManager->SaveFile 保存从远程下载的文件，并复制到配置目录中出错", String.format("客户端ID:%s;错误信息:%s", this.ClientID, var7), this.ClientID);
            EarlyWarn.AlarmLog(EarlyWarnLevelEnum.严重错误, "FetchManager->SaveFile 保存从远程下载的文件，并复制到配置目录中出错", String.format("客户端ID:%s;错误信息:%s", this.ClientID, var7));
        }

    }

    private void SaveExternalFile(String systemId, String ver, String configName, String content) throws Exception {
        try {
            this.ExternalFileDicCache.put(systemId + "_" + configName, content);
            String tmpdir = ApiSetting.getConfigTmpRootDirectory() + File.separator + systemId + "_" + ver + File.separator;
            CreateDir(tmpdir);
            String facdir = ApiSetting.getConfigFactRootDirectory() + File.separator + systemId + "_" + ver + File.separator;
            CreateDir(facdir);
            String tmpPath = tmpdir + configName;
            String factPath = facdir + configName;
            OutputStream os1 = new FileOutputStream(tmpPath);
            PrintStream pPRINT = new PrintStream(os1, true, "UTF-8");
            pPRINT.println(content);
            pPRINT.close();
            Copy(tmpPath, factPath);
        } catch (Exception var11) {
            Log.LogText("FetchManager->SaveExternalFile 保存从远程下载的文件，并复制到配置目录中出错", String.format("客户端ID:%s;错误信息:%s", this.ClientID, var11), this.ClientID);
            EarlyWarn.AlarmLog(EarlyWarnLevelEnum.严重错误, "FetchManager->SaveExternalFile 保存从远程下载的文件，并复制到配置目录中出错", String.format("客户端ID:%s;错误信息:%s", this.ClientID, var11));
        }

    }

    private void SaveItem(Map<String, String> dic) throws Exception {
        try {
            if (dic != null && dic.size() > 0) {
                Map<String, String> itemDic = this.GetLocalItem();
                Set<String> keys = dic.keySet();
                Iterator var4 = keys.iterator();

                String tmpPath;
                String factPath;
                while(var4.hasNext()) {
                    tmpPath = (String)var4.next();
                    factPath = (String)dic.get(tmpPath);
                    itemDic.put(tmpPath, factPath);
                    this.ItemDicCache.put(tmpPath, factPath);
                }

                keys = itemDic.keySet();
                Properties pps = new Properties();
                Iterator var10 = keys.iterator();

                while(var10.hasNext()) {
                    factPath = (String)var10.next();
                    String strValue = (String)itemDic.get(factPath);
                    pps.setProperty(factPath, strValue);
                }

                tmpPath = ApiSetting.getConfigTmpRootDirectory() + File.separator + ApiSetting.getConfigTmpItemsLocalName();
                factPath = ApiSetting.getConfigFactRootDirectory() + File.separator + ApiSetting.getConfigFactItemsFileName();
                OutputStream os1 = new FileOutputStream(tmpPath);
                pps.store(os1, "");
                os1.close();
                Copy(tmpPath, factPath);
            }
        } catch (Exception var8) {
            throw new Exception(String.format("SaveItem Exception:%s", var8.getMessage()));
        }
    }

    private void SaveExternalItem(String systemId, String ver, Map<String, String> dic) throws Exception {
        try {
            if (dic != null && dic.size() > 0) {
                Map<String, String> itemDic = this.GetLocalExternalItem(systemId, ver);
                Set<String> keys = dic.keySet();
                Iterator var6 = keys.iterator();

                String tmpDir;
                String factDir;
                while(var6.hasNext()) {
                    tmpDir = (String)var6.next();
                    factDir = (String)dic.get(tmpDir);
                    itemDic.put(tmpDir, factDir);
                    this.ExternalItemDicCache.put(systemId + "_" + tmpDir, factDir);
                }

                keys = itemDic.keySet();
                Properties pps = new Properties();
                Iterator var14 = keys.iterator();

                String tmpPath;
                while(var14.hasNext()) {
                    factDir = (String)var14.next();
                    tmpPath = (String)itemDic.get(factDir);
                    pps.setProperty(factDir, tmpPath);
                }

                tmpDir = ApiSetting.getConfigTmpRootDirectory() + File.separator + systemId + "_" + ver + File.separator;
                CreateDir(tmpDir);
                factDir = ApiSetting.getConfigFactRootDirectory() + File.separator + systemId + "_" + ver + File.separator;
                CreateDir(factDir);
                tmpPath = tmpDir + ApiSetting.getConfigTmpItemsLocalName();
                String factPath = factDir + ApiSetting.getConfigFactItemsFileName();
                OutputStream os1 = new FileOutputStream(tmpPath);
                pps.store(os1, "");
                os1.close();
                Copy(tmpPath, factPath);
            }
        } catch (Exception var12) {
            throw new Exception(String.format("SaveItem Exception:%s", var12.getMessage()));
        }
    }

    public static boolean isFileExist(String filePathString) {
        File f = new File(filePathString);
        return f.exists();
    }

    public static void Copy(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];

//                int byteread;
                while((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }

                inStream.close();
            }
        } catch (Exception var9) {
            System.out.println("error  ");
            var9.printStackTrace();
        }

    }

    public static void Copy(File oldfile, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            if (oldfile.exists()) {
                InputStream inStream = new FileInputStream(oldfile);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];

//                int byteread;
                while((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }

                inStream.close();
            }
        } catch (Exception var7) {
            System.out.println("error  ");
            var7.printStackTrace();
        }

    }

    public static boolean CreateDir(String destDirName) {
        File dir = new File(destDirName);
        if (dir.exists()) {
            return false;
        } else {
            if (!destDirName.endsWith(File.separator)) {
                destDirName = destDirName + File.separator;
            }

            if (dir.mkdirs()) {
                System.out.println("创建目录" + destDirName + "成功！");
                return true;
            } else {
                System.out.println("创建目录" + destDirName + "失败！");
                return false;
            }
        }
    }

    protected static String HttpPost(String url, HashMap<String, String> param, String encoding) throws Exception {
        StringBuffer result = new StringBuffer();
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;

        try {
            URL httpUrl = new URL(url);
            connection = (HttpURLConnection)httpUrl.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Close");
            connection.setRequestProperty("CacheControl", "no-cache");
            connection.setRequestProperty("Charset", encoding);
            connection.setRequestProperty("Content-Type", "application/json; charset=" + encoding + "; ");
            outputStream = new DataOutputStream(connection.getOutputStream());
            StringBuilder sb = new StringBuilder();
            Iterator var11;
            if (param != null && param.size() > 0) {
                var11 = param.entrySet().iterator();

                while(var11.hasNext()) {
                    Entry<String, String> entry = (Entry)var11.next();
                    sb.append(String.format("%s=%s&", entry.getKey(), URLEncoder.encode((String)entry.getValue(), encoding)));
                }
            }

            outputStream.writeBytes(sb.toString());
            outputStream.flush();
            inputStream = connection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream, encoding);
            bufferedReader = new BufferedReader(inputStreamReader);
            var11 = null;

            String line;
            while((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception var32) {
            throw var32;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (Exception var31) {
                    ;
                }
            }

            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (Exception var30) {
                    ;
                }
            }

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception var29) {
                    ;
                }
            }

            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception var28) {
                    ;
                }
            }

            if (connection != null) {
                try {
                    connection.disconnect();
                } catch (Exception var27) {
                    ;
                }
            }

        }

        return result.toString().trim();
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

    static {
        Instance = new ConfigFetchManager();
    }
}
