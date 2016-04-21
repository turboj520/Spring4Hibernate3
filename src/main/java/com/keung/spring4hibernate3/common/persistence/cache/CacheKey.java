package com.keung.spring4hibernate3.common.persistence.cache;

import java.util.concurrent.ConcurrentHashMap;



/**
 *
 * @author wangzhen 
 * @ctime：2014-12-4 上午11:03:03
 *
 */
public class CacheKey {
	
	//对象缓存 1小时
	public static final int CACHE_TIME_OBJ = 3600 ;
	
	//列表缓存 5分钟
	public static final int CACHE_TIME_LIST = 300;
	
	//注册黑名单
	public static final int CACHE_TIME_REGISTER_BLACK_LISTS = 300;
	
	//用户详情
	public static final String userInfoHash ="userInfoHash";
	
	/**
	 * 短信验证码
	 */
	public static final ConcurrentHashMap<String, String> mobileCaptchaHash =  new ConcurrentHashMap<String, String>(100);
	
	/**
	 * 短信发送计数
	 */
	public static final ConcurrentHashMap<String, Integer> mobileCaptchaHashcount =  new ConcurrentHashMap<String, Integer>(100);
	
	//验证成功计数
	public static final ConcurrentHashMap<String, Integer> mobileCaptchaHashSuccess =  new ConcurrentHashMap<String, Integer>(100);

	//开机启动
	public static final String bootStartHash = "bootStartHash";
	
	//活动列表
	public static final String ativityHash = "ativityHash";
	
	//浏览过我的用户
	public static final String browseUserHash = "browseUserHash";

	//我浏览过的用户
	public static final String selfbrowseUserHash = "selfbrowseUserHash";

	//系统版本
	public static final String sysVersionHash = "sysVersionHash";
	
	//用户相册
	public static final String userPhotoHash = "userPhotoHash";
	
	public static final String activityCategoryHash = "activityCategoryHash";
	
	// 产品包 hash key
	public static final String pkgHash = "pkgHash";

	public static final String adList ="adList";

}
