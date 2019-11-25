package com.donate.common.utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang.StringUtils;
//import org.jboss.spring.vfs.VFSResourceLoader;
import org.springframework.util.ResourceUtils;

public class CommonUtils {
    public static final String DATE_FORMAT_yyyyMMdd = "yyyyMMdd";
    public static final String DATE_FORMAT_HHmmss = "HHmmss";
    public static final String DATETIME_FORMAT_yyyyMMddHHmmss = "yyyyMMddHHmmss";
    public static final String DATETIME_FORMAT_GENERAL = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_GENERAL = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT_yyyyMMddHHmmssSSS = "yyyyMMddHHmmssSSS";

    public CommonUtils() {
    }

    public static Date toDate(String dateStr, String format) {
        Date date = null;
        if (StringUtils.isNotBlank(dateStr) && StringUtils.isNotBlank(format)) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                date = sdf.parse(dateStr);
            } catch (Exception var4) {
                date = null;
            }
        }

        return date;
    }

    public static Date toDate(String str) {
        return toDate(str, "yyyyMMdd");
    }

    public static Date toDateGeneral(String str) {
        return toDate(str, "yyyy-MM-dd");
    }

    public static Date toTime(String str) {
        return toDate(str, "HHmmss");
    }

    public static Date toDatetime(String str) {
        return toDate(str, "yyyyMMddHHmmss");
    }

    public static Date toDatetimeGeneral(String str) {
        return toDate(str, "yyyy-MM-dd HH:mm:ss");
    }

    public static Date toDateFromMillis(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.valueOf(millis));
        return calendar.getTime();
    }

    public static String toStr(Date date, String format) {
        String ret = "";
        if (null != date && StringUtils.isNotBlank(format)) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                ret = sdf.format(date);
            } catch (Exception var4) {
                ret = "";
            }
        }

        return ret;
    }

    public static String toStrDate(Date date) {
        return toStr(date, "yyyyMMdd");
    }

    public static String toStrTime(Date date) {
        return toStr(date, "HHmmss");
    }

    public static String toStrDatetime(Date date) {
        return toStr(date, "yyyyMMddHHmmss");
    }

    public static String toStrDateGeneral(Date date) {
        return toStr(date, "yyyy-MM-dd");
    }

    public static String toStrDatetimeGeneral(Date date) {
        return toStr(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String toStryyyyMMddHHmmssSSS(Date date) {
        return toStr(date, "yyyyMMddHHmmssSSS");
    }

    public static InputStream getInputStream(String filePath) {
        InputStream is = null;
        File file = getFile(filePath);
        if (null != file) {
            try {
                is = new FileInputStream(file);
            } catch (Exception var4) {
                is = null;
            }
        }

        return is;
    }

    public static File getFile(String filePath) {
        File file = null;
        if (StringUtils.isNotEmpty(filePath)) {
            try {
                file = ResourceUtils.getFile(filePath);
            } catch (FileNotFoundException var5) {
                try {
                    VFSResourceLoader loader = new VFSResourceLoader(VFSResourceLoader.class.getClassLoader());
                    file = loader.getResource(filePath).getFile();
                } catch (Exception var4) {
                    file = null;
                }
            }
        }

        return file;
    }
}
