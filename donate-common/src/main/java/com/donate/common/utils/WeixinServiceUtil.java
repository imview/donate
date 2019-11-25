package com.donate.common.utils;

import com.donate.common.properties.RedisHelper;
import com.donate.common.properties.WxMpConfig;
import com.donate.common.properties.WxServiceInRedisConfigStorage;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * 
 * @author Binary Wang
 *
 */
@Service
public class WeixinServiceUtil extends WxMpServiceImpl {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  protected LogHandler logHandler;

  @Autowired
  protected RedisHelper redis;
 
  @Autowired
  private WxMpConfig wxConfig;

  private WxMpMessageRouter router;

  @PostConstruct
  public void init() {
	  final WxServiceInRedisConfigStorage config = new WxServiceInRedisConfigStorage();
      config.setAppId(this.wxConfig.getAppid());// 设置微信公众号的appid
	  config.setSecret(this.wxConfig.getAppsecret());// 设置微信公众号的app corpSecret
	  config.setToken(this.wxConfig.getToken());// 设置微信公众号的token
	  config.setAesKey(this.wxConfig.getAesKey());// 设置消息加解密密钥
	  config.setRedis(redis);
	  super.setWxMpConfigStorage(config);
		
    this.refreshRouter();
  }

  private void refreshRouter() {
    final WxMpMessageRouter newRouter = new WxMpMessageRouter(this);

    // 记录所有事件的日志
    newRouter.rule().handler(this.logHandler).next(); 
    this.router = newRouter;
  } 
}
