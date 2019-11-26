package com.donate.api.controller;


import com.donate.common.model.ServiceResultT;
import com.donate.dao.entity.User;
import com.google.gson.Gson;

import com.donate.common.properties.WxMpConfig;
import com.donate.common.utils.WeixinServiceUtil;
import com.donate.service.donate.UserService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.UUID;

@Controller
@RequestMapping(value = "user")
@Api(value = "user", description = "用户相关接口", produces = MediaType.ALL_VALUE)
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(getClass().getName());

    @Autowired
    WeixinServiceUtil wxMpService;
    @Autowired
    UserService userService;
    @Autowired
    WxMpConfig wxMpConfig;
    Gson gson = new Gson();

    @ResponseBody
    @RequestMapping(value = "getUserInfo", method = RequestMethod.POST)
    @ApiOperation(value = "获取用户信息", notes = "返回用户信息（opendId）", produces = MediaType.ALL_VALUE)
    public ServiceResultT<User> getUserInfo(
            @ApiParam(value = "code-获取access_token的code", required = true, name = "code")
                    String code) throws WxErrorException, Exception {
        ServiceResultT<User> result = new ServiceResultT<User>();
        if (StringUtils.isBlank(code)) {
            return result.failed("code不能为空");
        }

        logger.info("开始获取token,code=" + code);
        WxMpOAuth2AccessToken accessToken = wxMpService.oauth2getAccessToken(code);
        logger.info("结束获取token:" + accessToken.getAccessToken() + "; openId:" + accessToken.getOpenId());
        WxMpUser wxMpUser = wxMpService.oauth2getUserInfo(accessToken, "zh_CN");
        logger.info("获取用户图片地址：" + wxMpUser.getHeadImgUrl());
        logger.info("获取用户信息：" + gson.toJson(wxMpUser));
        User oldUser = userService.getUserByOpenId(wxMpUser.getOpenId());
        User user = new User();
        user.setOpenId(wxMpUser.getOpenId());
        user.setCity(wxMpUser.getCity());
        user.setCountry(wxMpUser.getCountry());
        user.setHeadImgUrl(wxMpUser.getHeadImgUrl());
        user.setNickName(URLEncoder.encode(wxMpUser.getNickname(), "utf-8"));
        //user.setPrivilege();
        user.setSex(wxMpUser.getSexId());
        user.setRawHeadImgUrl(wxMpUser.getHeadImgUrl());

        String imgUrl = null;
        if (oldUser == null || !wxMpUser.getHeadImgUrl().equals(oldUser.getRawHeadImgUrl())) {
            logger.info("开始保存图片");
            imgUrl = this.downLoadPic(wxMpUser.getHeadImgUrl());
            logger.info("结束保存图片");
        }
        user.setHeadImgUrl(imgUrl);//保存本地的图片地址
        logger.info("开始保存数据");
        if (oldUser != null) {
            user.setUpdateTime(new Date());
            userService.upateUser(user);
        } else {
            user.setCreateTime(new Date());
            userService.addUser(user);
        }
        logger.info(" 结束保存数据");
        result.setDicData(user);
        return result;
    }

    private String downLoadPic(String pUrl) {
        try {
            URL url = new URL(pUrl);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());

            String imageName = UUID.randomUUID().toString() + ".jpg";
            logger.info("dir:" + wxMpConfig.getWxheadPicDir());

            File file = new File(wxMpConfig.getWxheadPicDir(), imageName);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            logger.info("dir:" + wxMpConfig.getWxheadPicDir() + "; filePath:" + file.getPath());
            byte[] buffer = new byte[1024];
            int length;

            while ((length = dataInputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, length);
            }

            dataInputStream.close();
            fileOutputStream.close();
            return imageName;
        } catch (Exception e) {
            logger.error("保存图片出现异常");
            return "";
        }
    }
}
