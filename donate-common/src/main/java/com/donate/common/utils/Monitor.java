package com.donate.common.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Monitor {
    private static double LogSendCount = 0.0D;
    private static double LogThreadCount = 0.0D;
    private static double AllCpuRatio = 0.0D;
    private static double MemoryAvailable = 0.0D;
    private static double MemoryCommitted = 0.0D;
    private static String DiskInfo = "";
    private static double DiskReads = 0.0D;
    private static double DiskReadKB = 0.0D;
    private static double DiskWrites = 0.0D;
    private static double DiskWriteKB = 0.0D;
    private static double AllNetworkSend = 0.0D;
    private static double AllNetworkReceive = 0.0D;

    public Monitor() {
    }

    public static void resetMonitorData() {
        AllCpuRatio = 0.0D;
        MemoryAvailable = 0.0D;
        MemoryCommitted = 0.0D;
        DiskInfo = "";
        DiskReads = 0.0D;
        DiskReadKB = 0.0D;
        DiskWrites = 0.0D;
        DiskWriteKB = 0.0D;
        AllNetworkSend = 0.0D;
        AllNetworkReceive = 0.0D;
    }

    public static void setMonitorData() {
        Runtime runtime = null;
        String line = null;
        Process process = null;
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        InputStream input = null;

        try {
            runtime = Runtime.getRuntime();
            process = runtime.exec("vmstat");
            input = process.getInputStream();
            inputStreamReader = new InputStreamReader(input);
            bufferedReader = new BufferedReader(inputStreamReader);
            int count = 0;

            while((line = bufferedReader.readLine()) != null) {
                ++count;
                if (count >= 3) {
                    String[] infoArr = line.split("\\s+");
                    if (infoArr.length > 16) {
                        AllCpuRatio = (double)(Integer.parseInt(infoArr[12]) + Integer.parseInt(infoArr[13]));
                        DiskReadKB = (double)Integer.parseInt(infoArr[9]);
                        DiskWriteKB = (double)Integer.parseInt(infoArr[8]);
                    }
                    break;
                }
            }
        } catch (Exception var65) {
            ;
        } finally {
            try {
                if (input != null) {
                    input.close();
                }

                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }

                if (bufferedReader != null) {
                    bufferedReader.close();
                }

                if (process != null) {
                    process.destroy();
                }
            } catch (Exception var59) {
                ;
            }

        }

        try {
            runtime = Runtime.getRuntime();
            process = runtime.exec("cat /proc/meminfo");
            input = process.getInputStream();
            inputStreamReader = new InputStreamReader(input);
            bufferedReader = new BufferedReader(inputStreamReader);
            long totalMem = 0L;
            long freeMem = 0L;

            while((line = bufferedReader.readLine()) != null) {
                String[] memInfo = line.split("\\s+");
                if (memInfo[0].startsWith("MemTotal")) {
                    totalMem = Long.parseLong(memInfo[1]);
                }

                if (memInfo[0].startsWith("MemFree")) {
                    freeMem = Long.parseLong(memInfo[1]);
                }

                if (totalMem > 0L && freeMem > 0L) {
                    break;
                }
            }

            if (totalMem > 0L && freeMem > 0L) {
                MemoryAvailable = (double)freeMem / 1024.0D / 1024.0D;
                MemoryAvailable = (double)Math.round(MemoryAvailable * 100.0D) / 100.0D;
                MemoryCommitted = (double)(totalMem - freeMem) / 1024.0D / 1024.0D;
                MemoryCommitted = (double)Math.round(MemoryCommitted * 100.0D) / 100.0D;
            }
        } catch (Exception var63) {
            ;
        } finally {
            try {
                if (input != null) {
                    input.close();
                }

                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }

                if (bufferedReader != null) {
                    bufferedReader.close();
                }

                if (process != null) {
                    process.destroy();
                }
            } catch (Exception var58) {
                ;
            }

        }

        try {
            runtime = Runtime.getRuntime();
            process = runtime.exec("df -hl -m");
            input = process.getInputStream();
            inputStreamReader = new InputStreamReader(input);
            bufferedReader = new BufferedReader(inputStreamReader);

            while((line = bufferedReader.readLine()) != null) {
                if (!line.startsWith("Filesystem")) {
                    String driveName = "";
                    double totleSize = 0.0D;
                    double freeSize = 0.0D;
                    String[] lineArr = line.split("\\s+");
                    if (lineArr.length > 5) {
                        try {
                            driveName = lineArr[0];
                            totleSize = (double)Math.round((double)Long.parseLong(lineArr[1]) / 1024.0D * 100.0D) / 100.0D;
                            freeSize = (double)Math.round((double)Long.parseLong(lineArr[3]) / 1024.0D * 100.0D) / 100.0D;
                            if (totleSize > 20.0D) {
                                DiskInfo = DiskInfo + driveName + "," + totleSize + "," + freeSize + ";";
                            }
                        } catch (Exception var60) {
                            ;
                        }
                    }
                }
            }
        } catch (Exception var61) {
            ;
        } finally {
            try {
                if (input != null) {
                    input.close();
                }

                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }

                if (bufferedReader != null) {
                    bufferedReader.close();
                }

                if (process != null) {
                    process.destroy();
                }
            } catch (Exception var57) {
                ;
            }

        }

    }

    public static void setLogSendCount(int count) {
        LogSendCount += (double)count;
    }

    public static double getLogSendCount() {
        double value = 0.0D;
        value = LogSendCount / (double)ApiSetting.getEarlyWarnHeartbeatTime();
        value = (double)Math.round(value * 100.0D) / 100.0D;
        LogSendCount = 0.0D;
        return value;
    }

    public static void setLogThreadCount(int count) {
        LogThreadCount += (double)count;
    }

    public static double getLogThreadCount() {
        double value = 0.0D;
        value = LogThreadCount / (double)ApiSetting.getEarlyWarnHeartbeatTime();
        value = (double)Math.round(value * 100.0D) / 100.0D;
        LogThreadCount = 0.0D;
        return value;
    }

    public static double getAllCpuRatio() {
        return AllCpuRatio;
    }

    public static double getMemoryAvailable() {
        return MemoryAvailable;
    }

    public static double getMemoryCommitted() {
        return MemoryCommitted;
    }

    public static String getDiskInfo() {
        return DiskInfo;
    }

    public static double getDiskReads() {
        return DiskReads;
    }

    public static double getDiskReadKB() {
        return DiskReadKB;
    }

    public static double getDiskWrites() {
        return DiskWrites;
    }

    public static double getDiskWriteKB() {
        return DiskWriteKB;
    }

    public static double getAllNetworkSend() {
        return AllNetworkSend;
    }

    public static double getAllNetworkReceive() {
        return AllNetworkReceive;
    }
}

