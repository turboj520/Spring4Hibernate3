package com.keung.spring4hibernate3.common.persistence.cache;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;

/**
 *
 * @author wangzhen 
 * @ctime：2014-12-18 下午8:46:39
 *
 */
//@Aspect
//@Component
public class CacheAspect extends BaseAspect{
	
	private final static Logger logger = LoggerFactory.getLogger(CacheAspect.class.getName());
	
//	@Autowired
	public RedisTemplate<String, String> redisTemplate;
	
	/**
	 * 缓存更新
	 * @param jp
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Around("@annotation(cacheHashUpdate)")
	public Object handler(ProceedingJoinPoint jp,CacheHashUpdate cacheHashUpdate) {
		String  k = null;
		try{
			k = cacheHashUpdate.k();
			List<List<String>> hk_array = parseKey(k, getMethod(jp), jp.getArgs(),List.class);
			int size = hk_array.size();
			for (int i = 0; i < size; i++) {
				String hash = String.valueOf(hk_array.get(i).get(0));
				String hash_key= String.valueOf(hk_array.get(i).get(1));
				redisTemplate.boundHashOps(hash).delete(hash_key);
			}
		} catch (Exception e) {
			logger.error("Del cacheHash [ k:{} ] Error !");
			logger.error("{}",e.getMessage());
		}
		return invokeMethod(jp);
	}

	
	/**
	 * 字符串 缓存
	 * @param jp
	 * @param cacheStr
	 * @return
	 */
	@Around("@annotation(cacheStr)")
	public Object handler(ProceedingJoinPoint jp,CacheStr cacheStr) {
		String  k =null;
		Class<?> clazz = super.getReturnType(jp);
		try {
			k = cacheStr.k();
			
			String result = redisTemplate.boundValueOps(k).get();
			
			if(!StringUtils.isEmpty(result) && !"null".equals(result)){

				return JSONObject.parseObject(result.trim(), clazz);
			}
		} catch (Exception e) {
			logger.error("Get cacheStr [ k:{} ] Error !",k);
			logger.error("{}",e.getMessage());
		}
			
		Object obj = invokeMethod(jp);
		
		try {
			if(!StringUtils.isEmpty(obj)){

				redisTemplate.boundValueOps(k).set(JSONObject.toJSONString(obj));
				/**
				 * 列表自动过期
				 */
				redisTemplate.boundValueOps(k).expire(CacheKey.CACHE_TIME_LIST, TimeUnit.SECONDS);
			}
		} catch (Exception e) {
			logger.error("Set cacheStr [ k:{} ] Error !",k);
			logger.error("{}",e.getMessage());

		}
		return obj;
	}
	
	/**
	 * hash 缓存
	 * @param jp
	 * @param cacheHash
	 * @return
	 */
	@Around("@annotation(cacheHash)")
	public Object handler(ProceedingJoinPoint jp,CacheHash cacheHash) {
		String  hash =null;
		String hk = null;
		Class<?> clazz = super.getReturnType(jp);
		try {
			hash = cacheHash.hash();

			hk = parseKey(cacheHash.hkey(), getMethod(jp), jp.getArgs(),String.class);
			
			String result = String.valueOf(redisTemplate.boundHashOps(hash).get(hk));
			
			if(!StringUtils.isEmpty(result) && !"null".equals(result)){
				
				return JSONObject.parseObject(result.trim(), clazz);
			}
		} catch (Exception e) {
			logger.error("Get Cache Hash [ hash:{} hk:{} ] Error !",hash,hk);
			logger.error("{}",e.getMessage());
		}
			
		Object obj = invokeMethod(jp);
		
		try {
			if(!StringUtils.isEmpty(obj)  && !"null".equals(obj)){
				redisTemplate.boundHashOps(hash).put(hk, JSONObject.toJSONString(obj));
			}
		} catch (Exception e) {
			logger.error("Set Cache Hash [ hash:{} hk:{} ] Error !",hash,hk);
			logger.error("{}",e.getMessage());

		}
		return obj;
	}
	
	public Object invokeMethod(ProceedingJoinPoint jp){
		Object obj =null;
		try {
			obj = jp.proceed(jp.getArgs());
		} catch (Throwable e) {
			logger.error("invoke Method Error: {}",e.getMessage());
		}
		return obj;
	}
}
