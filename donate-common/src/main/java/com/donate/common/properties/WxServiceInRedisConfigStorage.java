package com.donate.common.properties;

import me.chanjar.weixin.common.bean.WxAccessToken;
import me.chanjar.weixin.common.util.ToStringUtils;
import me.chanjar.weixin.common.util.http.apache.ApacheHttpClientBuilder;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import java.io.File;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WxServiceInRedisConfigStorage implements WxMpConfigStorage{
    
	private static Logger logger = LoggerFactory.getLogger(WxServiceInRedisConfigStorage.class);

	protected volatile String appId;
	protected volatile String secret;
	protected volatile String token;
	protected volatile String accessToken;
	protected volatile String aesKey;
	protected volatile long expiresTime;

	protected volatile String oauth2redirectUri;

	protected volatile String httpProxyHost;
	protected volatile int httpProxyPort;
	protected volatile String httpProxyUsername;
	protected volatile String httpProxyPassword;

	protected volatile String jsapiTicket;
	protected volatile long jsapiTicketExpiresTime;

	protected volatile String cardApiTicket;
	protected volatile long cardApiTicketExpiresTime;

	protected Lock accessTokenLock = new ReentrantLock();
	protected Lock jsapiTicketLock = new ReentrantLock();
	protected Lock cardApiTicketLock = new ReentrantLock();

	/**
	 * 涓存椂鏂囦欢鐩綍
	 */
	protected volatile File tmpDirFile;

	protected volatile ApacheHttpClientBuilder apacheHttpClientBuilder;
	
	/**
	 * redis 注入
	 */
	//@Autowired
	protected RedisHelper redis;
	
	private final static String ACCESS_TOKEN_KEY = "wechat_access_token_";
	private final static String JSAPI_TICKET_KEY = "wechat_jsapi_ticket_";
	private final static String CARDAPI_TICKET_KEY = "wechat_cardapi_ticket_";
	
	@Override
	public String getAccessToken() {
		String result= null;
		  Jedis jedis = null;
		  try {  
	      	jedis = redis.getConnect(); 
	      	result = jedis.get(ACCESS_TOKEN_KEY.concat(appId)); 
	      	logger.info("Redis获取AccessToken：" + result);
	      	redis.returnResource(jedis);
	      } 
	      catch (JedisException e) {   
	      	redis.handleException(jedis,e); 
	      	logger.error("Redis获取AccessToken异常-JedisException" + e.getMessage(), e.getCause());  
	      }
	      catch (Exception e1) {
	      	logger.error("Redis获取AccessToken异常" + e1.getMessage(), e1.getCause());
	      } 
		  return result;
	}

	@Override
	public Lock getAccessTokenLock() {
		return this.accessTokenLock;
	}

	@Override
	public boolean isAccessTokenExpired() {
		logger.info("进入isAccessTokenExpired方法");
		 boolean result = false;
		  Jedis jedis = null;
		  try {  
	      	jedis = redis.getConnect(); 
	      	result = jedis.ttl(ACCESS_TOKEN_KEY.concat(appId)) < 2; 
	    	redis.returnResource(jedis);
	      } 
	      catch (JedisException e) {   
	      	redis.handleException(jedis,e); 
	      	logger.error("Redis获取AccessToken异常-JedisException" + e.getMessage(), e.getCause());  
	      }
	      catch (Exception e1) {
	      	logger.error("Redis获取AccessToken异常" + e1.getMessage(), e1.getCause());
	      } 
		  logger.info("isAccessTokenExpired方法的返回结果为："+result);
		  return result;
	}

	@Override
	public void expireAccessToken() {
		logger.info("进入expireAccessToken方法");
		Jedis jedis = null;
		try {  
	      jedis = redis.getConnect(); 
	      jedis.expire(ACCESS_TOKEN_KEY.concat(appId), 0);
	      redis.returnResource(jedis);
	    } 
	    catch (JedisException e) {   
	      redis.handleException(jedis,e); 
	      logger.error("Redis获取AccessToken异常-JedisException" + e.getMessage(), e.getCause());  
	    }
	    catch (Exception e1) {
	      logger.error("Redis获取AccessToken异常" + e1.getMessage(), e1.getCause());
	    } 
		
	}

	@Override
	public synchronized void updateAccessToken(WxAccessToken accessToken) {
		updateAccessToken(accessToken.getAccessToken(), accessToken.getExpiresIn()); 
	}

	@Override
	public synchronized void updateAccessToken(String accessToken, int expiresInSeconds) {
		Jedis jedis = null;
		try {  
	      jedis = redis.getConnect(); 
	      jedis.set(ACCESS_TOKEN_KEY.concat(appId), accessToken);
	      jedis.expire(ACCESS_TOKEN_KEY.concat(appId), expiresInSeconds - 200);
	      logger.info("Redis更新AccessToken：" + accessToken);
	      redis.returnResource(jedis);
	    } 
	    catch (JedisException e) {   
	      redis.handleException(jedis,e); 
	      logger.error("Redis获取AccessToken异常-JedisException" + e.getMessage(), e.getCause());  
	    }
	    catch (Exception e1) {
	      logger.error("Redis获取AccessToken异常" + e1.getMessage(), e1.getCause());
	    }
		
	}

	@Override
	public String getJsapiTicket() {
		String result= null;
		Jedis jedis = null;
		logger.info("进入redis读取jsapiTicket方法：");
		try {  
	      jedis = redis.getConnect(); 
	      result = jedis.get(JSAPI_TICKET_KEY.concat(appId)); 
	      logger.info("Redis获取jsapiTicket：" + result);
	      redis.returnResource(jedis);
	    } 
	    catch (JedisException e) {   
	      redis.handleException(jedis,e); 
	      logger.error("Redis获取JsapiTicket异常-JedisException" + e.getMessage(), e.getCause());  
	    }
	    catch (Exception e1) {
	      logger.error("Redis获取JsapiTicket异常" + e1.getMessage(), e1.getCause());
	    } 
		logger.info("进入redis读取jsapiTicket的结果为："+result);
		return result;
		
	}

	@Override
	public Lock getJsapiTicketLock() {
		return this.jsapiTicketLock;
	}

	@Override
	public boolean isJsapiTicketExpired() {
		logger.info("进入isAccessTokenExpired方法");
		boolean result = true;
		Jedis jedis = null;
		try {  
	      jedis = redis.getConnect(); 
	      result = jedis.ttl(JSAPI_TICKET_KEY.concat(appId)) < 2; 
	      redis.returnResource(jedis);
	    } 
	    catch (JedisException e) {   
	      redis.handleException(jedis,e); 
	      logger.error("Redis获取JsapiTicket异常-JedisException" + e.getMessage(), e.getCause());  
	    }
	    catch (Exception e1) {
	      logger.error("Redis获取JsapiTicket异常" + e1.getMessage(), e1.getCause());
	    } 
		logger.info("isJsapiTicketExpired方法的返回结果为："+result);
		return result;
	}

	@Override
	public void expireJsapiTicket() {
	    Jedis jedis = null;
		try {  
	      jedis = redis.getConnect(); 
	      jedis.expire(JSAPI_TICKET_KEY.concat(appId), 0);
	      redis.returnResource(jedis);
	    } 
	    catch (JedisException e) {   
	      redis.handleException(jedis,e); 
	      logger.error("Redis进行更新JsapiTicket异常-JedisException" + e.getMessage(), e.getCause());  
	    }
	    catch (Exception e1) {
	      logger.error("Redis进行更新JsapiTicket异常" + e1.getMessage(), e1.getCause());
	    }
		
	}

	@Override
	public synchronized void updateJsapiTicket(String jsapiTicket, int expiresInSeconds) {
		logger.info("更新的jsapiTicket是：" +jsapiTicket );
		Jedis jedis = null;
		try {  
	      jedis = redis.getConnect(); 
	      jedis.set(JSAPI_TICKET_KEY.concat(appId), jsapiTicket);
	      logger.info("JSAPI_TICKET_KEY的APPId是：appID：{}，jsapiTicket是：{}",appId,jsapiTicket);
	      jedis.expire(JSAPI_TICKET_KEY.concat(appId), expiresInSeconds - 200);
	      logger.info("Redis更新jsapiTicket：" + jsapiTicket);
	      redis.returnResource(jedis);
	    } 
	    catch (JedisException e) {   
	      redis.handleException(jedis,e); 
	      logger.error("Redis进行更新JsapiTicket异常-JedisException" + e.getMessage(), e.getCause());  
	    }
	    catch (Exception e1) {
	      logger.error("Redis进行更新JsapiTicket异常" + e1.getMessage(), e1.getCause());
	    }
		
	}

	@Override
	public String getCardApiTicket() {
		String result= null;
		Jedis jedis = null;
		try {  
	      jedis = redis.getConnect(); 
	      result = jedis.get(CARDAPI_TICKET_KEY.concat(appId));
	    } 
	    catch (JedisException e) {   
	      redis.handleException(jedis,e); 
	      logger.error("Redis获取cardApiTicket异常-JedisException" + e.getMessage(), e.getCause());  
	    }
	    catch (Exception e1) {
	      logger.error("Redis获取cardApiTicket异常" + e1.getMessage(), e1.getCause());
	    } 
		return result;
	}

	@Override
	public Lock getCardApiTicketLock() {
		return this.cardApiTicketLock;
	}

	@Override
	public boolean isCardApiTicketExpired() {
		boolean result= false;
		Jedis jedis = null;
		try {  
	      jedis = redis.getConnect(); 
	      result = jedis.ttl(CARDAPI_TICKET_KEY.concat(appId)) < 2;
	      redis.returnResource(jedis);
	    } 
	    catch (JedisException e) {   
	      redis.handleException(jedis,e); 
	      logger.error("Redis获取cardApiTicket异常-JedisException" + e.getMessage(), e.getCause());  
	    }
	    catch (Exception e1) {
	      logger.error("Redis获取cardApiTicket异常" + e1.getMessage(), e1.getCause());
	    } 
		return result;
	}

	@Override
	public void expireCardApiTicket() {
		 Jedis jedis = null;
	     try {  
		     jedis = redis.getConnect(); 
		     jedis.expire(CARDAPI_TICKET_KEY.concat(appId), 0);
		     redis.returnResource(jedis);
		 } 
		 catch (JedisException e) {   
		     redis.handleException(jedis,e); 
		     logger.error("Redis进行更新cardApiTicket异常-JedisException" + e.getMessage(), e.getCause());  
		 }
		 catch (Exception e1) {
		     logger.error("Redis进行更新cardApiTicket异常" + e1.getMessage(), e1.getCause());
		 }
		
	}

	@Override
	public void updateCardApiTicket(String cardApiTicket, int expiresInSeconds) {
		Jedis jedis = null;
		try {  
	      jedis = redis.getConnect(); 
	      jedis.set(CARDAPI_TICKET_KEY.concat(appId), cardApiTicket);
	      jedis.expire(CARDAPI_TICKET_KEY.concat(appId), expiresInSeconds - 200);
	      logger.info("JSAPI_TICKET_KEY的APPId是：appID{}，cardApiTicket是：{}",appId,cardApiTicket);
	      redis.returnResource(jedis);
	    } 
	    catch (JedisException e) {   
	      redis.handleException(jedis,e); 
	      logger.error("Redis进行更新cardApiTicket异常-JedisException" + e.getMessage(), e.getCause());  
	    }
	    catch (Exception e1) {
	      logger.error("Redis进行更新cardApiTicket异常" + e1.getMessage(), e1.getCause());
	    }
		
	}

	

	/**
	 * set config的redis
	 * @return
	 */
	public RedisHelper getRedis() {
	  return this.redis;
	}

	public void setRedis(RedisHelper redis) {
	  this.redis = redis;
	}
	
	
	/**
	 * 重写的appId那些
	 */
	
    @Override
	public String getAppId() {
	  return this.appId;
	}

	public void setAppId(String appId) {
	  this.appId = appId;
	}

	@Override
	public String getSecret() {
	  return this.secret;
	}

	public void setSecret(String secret) {
	  this.secret = secret;
	}

	@Override
	public String getToken() {
	  return this.token;
	}

	public void setToken(String token) {
	  this.token = token;
	}

	@Override
	public long getExpiresTime() {
	  return this.expiresTime;
	}

	public void setExpiresTime(long expiresTime) {
	  this.expiresTime = expiresTime;
	}

	@Override
	public String getAesKey() {
	  return this.aesKey;
	}

	public void setAesKey(String aesKey) {
	  this.aesKey = aesKey;
	}

	@Override
	public String getOauth2redirectUri() {
	  return this.oauth2redirectUri;
	}

	public void setOauth2redirectUri(String oauth2redirectUri) {
	  this.oauth2redirectUri = oauth2redirectUri;
	}

	@Override
	public String getHttpProxyHost() {
	  return this.httpProxyHost;
	}

	public void setHttpProxyHost(String httpProxyHost) {
	  this.httpProxyHost = httpProxyHost;
	}

    @Override
	public int getHttpProxyPort() {
	  return this.httpProxyPort;
	}

    public void setHttpProxyPort(int httpProxyPort) {
	  this.httpProxyPort = httpProxyPort;
	}

	@Override
	public String getHttpProxyUsername() {
	  return this.httpProxyUsername;
	}

	public void setHttpProxyUsername(String httpProxyUsername) {
	  this.httpProxyUsername = httpProxyUsername;
	}

	@Override
	public String getHttpProxyPassword() {
	  return this.httpProxyPassword;
	}

	public void setHttpProxyPassword(String httpProxyPassword) {
	  this.httpProxyPassword = httpProxyPassword;
	}

	@Override
	public String toString() {
	  return ToStringUtils.toSimpleString(this);
	}

	@Override
	public File getTmpDirFile() {
	  return this.tmpDirFile;
	}

	public void setTmpDirFile(File tmpDirFile) {
	  this.tmpDirFile = tmpDirFile;
	}

	@Override
	public ApacheHttpClientBuilder getApacheHttpClientBuilder() {
	  return this.apacheHttpClientBuilder;
	}

	public void setApacheHttpClientBuilder(ApacheHttpClientBuilder apacheHttpClientBuilder) {
	  this.apacheHttpClientBuilder = apacheHttpClientBuilder;
	}

	public long getJsapiTicketExpiresTime() {
	  return this.jsapiTicketExpiresTime;
	}

	public void setJsapiTicketExpiresTime(long jsapiTicketExpiresTime) {
	  this.jsapiTicketExpiresTime = jsapiTicketExpiresTime;
	}

	public long getCardApiTicketExpiresTime() {
	  return this.cardApiTicketExpiresTime;
	}

	public void setCardApiTicketExpiresTime(long cardApiTicketExpiresTime) {
	  this.cardApiTicketExpiresTime = cardApiTicketExpiresTime;
	}

	@Override
	public boolean autoRefreshToken() {
	  return true;
	}

}
