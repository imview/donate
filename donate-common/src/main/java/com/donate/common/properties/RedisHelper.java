package com.donate.common.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.exceptions.JedisException;

@Component
public class RedisHelper { 
	
	private final Logger logger = LoggerFactory.getLogger(getClass().getName());
	 
	@Autowired
	@Qualifier("jedisSentinelPool")
	private JedisSentinelPool jedisSentinelPool; 
	
/*	@SuppressWarnings("unused")
	private Map<String, String> slaveList;*/
	
	@SuppressWarnings("unused")
	private Jedis currentMaster; 
  
	public  Jedis getConnect() { 
		Jedis jedis = null;
		boolean broken = false;
		try{ 
			jedis = jedisSentinelPool.getResource();  
		} 
		catch (JedisException e) {
	        broken = handleJedisException(e);
	        throw e;
	    }
		/* finally {
	        closeResource(jedis, broken);
	    } */
		return jedis;  
	}
	 
	public void returnResource(Jedis resource){ 
		jedisSentinelPool.returnResource(resource);
	}
	
	
	public  void handleException(Jedis jedis,JedisException e) {
		 boolean broken = handleJedisException(e);
		 closeResource(jedis, broken);
	}
	/*public Jedis getMasterConnect(){
		return null;
	}
	*/
	
	protected  void  release(Jedis jedis) {
		try{
		jedisSentinelPool.destroy();
		}catch (Exception e) {
			logger.error("释放jedis哨兵连接错误" ,e);
		}
	}
 
	
	/**
	 * Handle jedisException, write log and return whether the connection is broken.
	 */
	protected boolean handleJedisException(JedisException jedisException) {

		String hostIp = jedisSentinelPool.getCurrentHostMaster() ==null ?"":
			(
				jedisSentinelPool.getCurrentHostMaster().getHost() == null?"":
					jedisSentinelPool.getCurrentHostMaster().getHost()
			 );
				
	    if (jedisException instanceof JedisConnectionException) {
	    	
	        logger.error("Redis connection " + hostIp 	+ " lost.", jedisException);
	        
	    } else if (jedisException instanceof JedisDataException) {
	        if ((jedisException.getMessage() != null) && (jedisException.getMessage().indexOf("READONLY") != -1)) {
	            logger.error("Redis connection " + hostIp + " are read-only slave.", jedisException);
	        } else {
	            // dataException, isBroken=false
	            return false;
	        }
	    } else {
	        logger.error("Jedis exception happen.", jedisException);
	    }
	    return true;
	}
	/**
	 * Return jedis connection to the pool, call different return methods depends on the conectionBroken status.
	 */
	protected void closeResource(Jedis jedis, boolean conectionBroken) {
	    try {
	        if (conectionBroken) {
	        	jedisSentinelPool.returnBrokenResource(jedis);
	        } else {
	        	jedisSentinelPool.returnResource(jedis);
	        }
	    } catch (Exception e) {
	        logger.error("return back jedis failed, will fore close the jedis.", e);
	        jedisSentinelPool.destroy();
	    }
	}
	
}
