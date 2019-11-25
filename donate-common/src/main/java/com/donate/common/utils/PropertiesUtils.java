package com.donate.common.utils;

import java.util.Properties;

public class PropertiesUtils {
    private static Properties defaultInitProperties;

    public PropertiesUtils() {
    }

    public static void setInitProperty(Properties props) {
        defaultInitProperties = props;
    }

    public static String getProperty(String name) {
        return defaultInitProperties.getProperty(name);
    }

    public static String getProperty(String name, String defaultValue) {
        return defaultInitProperties.getProperty(name) != null && !defaultInitProperties.getProperty(name).equals("") ? defaultInitProperties.getProperty(name) : defaultValue;
    }
}