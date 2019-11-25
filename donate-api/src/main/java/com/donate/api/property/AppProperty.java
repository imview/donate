package com.donate.api.property;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppProperty {

    @Value("${url}")
    private String url;

    @Value("${version}")
    private String version;

    @Value("${manager_title}")
    private String managerTitle;

    @Value("${session.timeout}")
    private int sessionTimeOut;

    @Value("${web.jdbc.driverClassName}")
    private String webJdbcDriverClassName;

    @Value("${web.jdbc.url}")
    private String webJdbcUrl;

    @Value("${web.jdbc.username}")
    private String webJdbcUsername;

    @Value("${web.jdbc.password}")
    private String webJdbcPassword;

//    @Value("${um.jdbc.driverClassName}")
//    private String umJdbcDriverClassName;

//    @Value("${um.jdbc.url}")
//    private String umJdbcUrl;
//
//    @Value("${um.jdbc.username}")
//    private String umJdbcUsername;
//
//    @Value("${um.jdbc.password}")
//    private String umJdbcPassword;

    @Value("${max_pwd_error_count}")
    private Integer maxPwdErrorCount;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getManagerTitle() {
        return managerTitle;
    }

    public void setManagerTitle(String managerTitle) {
        this.managerTitle = managerTitle;
    }

    public int getSessionTimeOut() {
        return sessionTimeOut;
    }

    public void setSessionTimeOut(int sessionTimeOut) {
        this.sessionTimeOut = sessionTimeOut;
    }

    public String getWebJdbcDriverClassName() {
        return webJdbcDriverClassName;
    }

    public void setWebJdbcDriverClassName(String webJdbcDriverClassName) {
        this.webJdbcDriverClassName = webJdbcDriverClassName;
    }

    public String getWebJdbcUrl() {
        return webJdbcUrl;
    }

    public void setWebJdbcUrl(String webJdbcUrl) {
        this.webJdbcUrl = webJdbcUrl;
    }

    public String getWebJdbcUsername() {
        return webJdbcUsername;
    }

    public void setWebJdbcUsername(String webJdbcUsername) {
        this.webJdbcUsername = webJdbcUsername;
    }

    public String getWebJdbcPassword() {
        return webJdbcPassword;
    }

    public void setWebJdbcPassword(String webJdbcPassword) {
        this.webJdbcPassword = webJdbcPassword;
    }


    public Integer getMaxPwdErrorCount() {
        return maxPwdErrorCount;
    }

    public void setMaxPwdErrorCount(Integer maxPwdErrorCount) {
        this.maxPwdErrorCount = maxPwdErrorCount;
    }
}
