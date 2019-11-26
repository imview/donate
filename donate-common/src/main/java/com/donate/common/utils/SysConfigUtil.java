package com.donate.common.utils;


import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Properties;
import org.apache.commons.io.IOUtils;
//import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class SysConfigUtil {
    private static final Logger logger = Logger.getLogger(SysConfigUtil.class);
    private static Properties properties = null;

    public SysConfigUtil() {
    }

    public static void init(String configPath) {
        try {
            StringBuilder config = new StringBuilder();
            if (StringUtils.isNotBlank(configPath)) {
                configPath = configPath.trim();
                String[] configPaths = configPath.split(";");
                String[] var3 = configPaths;
                int var4 = configPaths.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    String path = var3[var5];
                    if (StringUtils.isNotBlank(path)) {
                        path = path.trim();
                        InputStream is = null;

                        try {
                            is = CommonUtils.getInputStream(path);
                            config.append(IOUtils.toString(is, "UTF-8"));
                            config.append("\r\n");
                        } catch (Exception var18) {
                            logger.error("读取配置文件发生异常,path=" + path, var18);
                        } finally {
                            if (null != is) {
                                try {
                                    is.close();
                                } catch (IOException var17) {
                                    ;
                                }
                            }

                        }
                    }
                }
            }

            properties = new Properties();
            properties.load(new StringReader(config.toString()));
        } catch (Exception var20) {
            logger.error("初始化配置文件发生异常", var20);
        }

    }

    public static String getConfigMsg(String code) {
        if (code != null && !"".equals(code)) {
            if (properties == null) {
                return PropertiesUtils.getProperty(code);
            } else {
                return properties.getProperty(code) == null ? "" : properties.getProperty(code);
            }
        } else {
            return "";
        }
    }

    public static Properties getProperties() {
        return properties;
    }

    public static void setProperties(Properties properties) {
        properties = properties;
    }
}
