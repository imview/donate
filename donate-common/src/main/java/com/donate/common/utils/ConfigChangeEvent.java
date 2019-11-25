package com.donate.common.utils;


import java.util.Iterator;
import java.util.Map;

public class ConfigChangeEvent {
    private final Map<String, String> m_files;
    private final Map<String, String> m_items;
    private final Map<String, Map<String, String>> m_externalFiles;
    private final Map<String, Map<String, String>> m_externalItems;

    public ConfigChangeEvent(Map<String, String> files, Map<String, String> items, Map<String, Map<String, String>> externalFiles, Map<String, Map<String, String>> externalItems) {
        this.m_files = files;
        this.m_items = items;
        this.m_externalFiles = externalFiles;
        this.m_externalItems = externalItems;
    }

    public Map<String, String> getFiles() {
        return this.m_files;
    }

    public Map<String, String> getItems() {
        return this.m_items;
    }

    public Map<String, Map<String, String>> getExternalFiles() {
        return this.m_externalFiles;
    }

    public Map<String, Map<String, String>> getExternalItems() {
        return this.m_externalItems;
    }

    public String toString() {
        String lineSeparator = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder();
        Iterator var3;
        String systemId;
        if (this.m_items.size() > 0) {
            sb.append("键值对更新个数：" + this.m_items.size() + lineSeparator);
            var3 = this.m_items.keySet().iterator();

            while(var3.hasNext()) {
                systemId = (String)var3.next();
                sb.append(" " + systemId + "=" + (String)this.m_items.get(systemId) + lineSeparator);
            }
        }

        if (this.m_files.size() > 0) {
            sb.append("文件更新个数：" + this.m_files.size() + lineSeparator);
            var3 = this.m_files.keySet().iterator();

            while(var3.hasNext()) {
                systemId = (String)var3.next();
                sb.append(" " + systemId + "=" + (String)this.m_files.get(systemId) + lineSeparator);
            }
        }

        Map tmp;
        Iterator var6;
        String key;
        if (this.m_externalItems.size() > 0) {
            sb.append("外部系统键值对更新：" + lineSeparator);
            var3 = this.m_externalItems.keySet().iterator();

            while(var3.hasNext()) {
                systemId = (String)var3.next();
                tmp = (Map)this.m_externalItems.get(systemId);
                var6 = tmp.keySet().iterator();

                while(var6.hasNext()) {
                    key = (String)var6.next();
                    sb.append(" " + systemId + "_" + key + "=" + (String)tmp.get(key) + lineSeparator);
                }
            }
        }

        if (this.m_externalFiles.size() > 0) {
            sb.append("外部系统文件更新更新：" + lineSeparator);
            var3 = this.m_externalFiles.keySet().iterator();

            while(var3.hasNext()) {
                systemId = (String)var3.next();
                tmp = (Map)this.m_externalFiles.get(systemId);
                var6 = tmp.keySet().iterator();

                while(var6.hasNext()) {
                    key = (String)var6.next();
                    sb.append(" " + systemId + "_" + key + "=" + (String)tmp.get(key) + lineSeparator);
                }
            }
        }

        return sb.toString();
    }
}
