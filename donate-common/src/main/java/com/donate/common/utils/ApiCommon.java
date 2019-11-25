package com.donate.common.utils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.AMQP.BasicProperties;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

public class ApiCommon {
    public ApiCommon() {
    }

    protected static void SendMQ(String host, String userName, String password, String mqName, Boolean durable, String message) {
        message = ApiSetting.getVersion() + ":" + message;
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(userName);
        factory.setPassword(password);
        factory.setAutomaticRecoveryEnabled(true);
        factory.setHost(host);
        Connection conn = null;
        Channel channel = null;

        try {
            conn = factory.newConnection();
            channel = conn.createChannel();
            channel.queueDeclare(mqName, durable, false, false, (Map)null);
            channel.basicPublish("", mqName, (BasicProperties)null, message.getBytes("utf-8"));
        } catch (Exception var18) {
            ;
        } finally {
            try {
                if (channel != null) {
                    if (channel.isOpen()) {
                        channel.close();
                    }

                    channel.abort();
                }

                if (conn != null) {
                    if (conn.isOpen()) {
                        conn.close();
                    }

                    conn.abort();
                }
            } catch (Exception var17) {
                ;
            }

        }

    }

    protected static String HttpUploadFile(String url, HashMap<String, String> param, String[] fileNameArr, InputStream[] fileArr) throws Exception {
        return HttpUploadFile(url, param, fileNameArr, fileArr, "utf-8");
    }

    protected static String HttpUploadFile(String url, HashMap<String, String> param, String[] fileNameArr, InputStream[] fileArr, String encoding) throws Exception {
        StringBuffer result = new StringBuffer();
        String newLine = "\r\n";
        String boundary = "***" + UUID.randomUUID().toString().replaceAll("\\-", "") + "***";
        String formDataTemplate = "Content-Disposition: form-data; name=\"%s\"" + newLine + newLine + "%s";
        String fileDataTemplate = "Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"" + newLine + "Content-Type: application/octet-stream" + newLine + newLine;
        String boundaryLine = newLine + "--" + boundary + newLine;
        String endLine = newLine + "--" + boundary + "--" + newLine;
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        boolean var38 = false;

        try {
            var38 = true;
            URL httpUrl = new URL(url);
            connection = (HttpURLConnection)httpUrl.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Close");
            connection.setRequestProperty("CacheControl", "no-cache");
            connection.setRequestProperty("Charset", encoding);
            connection.setRequestProperty("Accept-Charset", encoding);
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            outputStream = new DataOutputStream(connection.getOutputStream());
            Iterator var18;
            if (param != null && param.size() > 0) {
                var18 = param.entrySet().iterator();

                while(var18.hasNext()) {
                    Entry<String, String> entry = (Entry)var18.next();
                    outputStream.writeBytes(boundaryLine);
                    outputStream.write(String.format(formDataTemplate, entry.getKey(), entry.getValue()).getBytes(encoding));
                }
            }

            if (fileArr != null && fileArr.length > 0) {
                for(int i = 0; i < fileArr.length; ++i) {
                    byte[] buffer = new byte[2048];
                    int bytesRead = 0;
                    outputStream.writeBytes(boundaryLine);
                    outputStream.writeBytes(String.format(fileDataTemplate, "file" + i, fileNameArr[i]));

//                    int bytesRead;
                    while((bytesRead = fileArr[i].read(buffer, 0, buffer.length)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
            }

            outputStream.writeBytes(endLine);
            outputStream.flush();
            inputStream = connection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream, encoding);
            bufferedReader = new BufferedReader(inputStreamReader);
            var18 = null;

            while(true) {
                String line;
                if ((line = bufferedReader.readLine()) == null) {
                    var38 = false;
                    break;
                }

                result.append(line);
            }
        } catch (Exception var51) {
            throw var51;
        } finally {
            if (var38) {
                if (fileArr != null && fileArr.length > 0) {
                    for(int i = 0; i < fileArr.length; ++i) {
                        try {
                            fileArr[i].close();
                        } catch (Exception var44) {
                            ;
                        }
                    }
                }

                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (Exception var43) {
                        ;
                    }
                }

                if (inputStreamReader != null) {
                    try {
                        inputStreamReader.close();
                    } catch (Exception var42) {
                        ;
                    }
                }

                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception var41) {
                        ;
                    }
                }

                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (Exception var40) {
                        ;
                    }
                }

                if (connection != null) {
                    try {
                        connection.disconnect();
                    } catch (Exception var39) {
                        ;
                    }
                }

            }
        }

        if (fileArr != null && fileArr.length > 0) {
            for(int i = 0; i < fileArr.length; ++i) {
                try {
                    fileArr[i].close();
                } catch (Exception var50) {
                    ;
                }
            }
        }

        if (bufferedReader != null) {
            try {
                bufferedReader.close();
            } catch (Exception var49) {
                ;
            }
        }

        if (inputStreamReader != null) {
            try {
                inputStreamReader.close();
            } catch (Exception var48) {
                ;
            }
        }

        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (Exception var47) {
                ;
            }
        }

        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (Exception var46) {
                ;
            }
        }

        if (connection != null) {
            try {
                connection.disconnect();
            } catch (Exception var45) {
                ;
            }
        }

        return result.toString().trim();
    }
}
