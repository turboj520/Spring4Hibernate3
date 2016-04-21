/**
 * 
 */
package com.keung.spring4hibernate3.modules.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 *                   _ooOoo_
 *                  o8888888o
 *                  88" . "88
 *                  (| -_- |)
 *                  O\  =  /O
 *               ____/`---'\____
 *             .'  \\|     |//  `.
 *            /  \\|||  :  |||//  \
 *           /  _||||| -:- |||||-  \
 *           |   | \\\  -  /// |   |
 *           | \_|  ''\---/''  |   |
 *           \  .-\__  `-`  ___/-. /
 *         ___`. .'  /--.--\  `. . __
 *      ."" '<  `.___\_<|>_/___.'  >'"".
 *     | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 *     \  \ `-.   \_ __\ /__ _/   .-` /  /
 * ======`-.____`-.___\_____/___.-`____.-'======
 *                   `=---='
 * ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 *         佛祖保佑       永无BUG
 *  佛曰:
 *       写字楼里写字间，写字间里程序员；  
 *       程序人员写程序，又拿程序换酒钱。  
 *       酒醒只在网上坐，酒醉还来网下眠；  
 *       酒醉酒醒日复日，网上网下年复年。  
 *       但愿老死电脑间，不愿鞠躬老板前；  
 *       奔驰宝马贵者趣，公交自行程序员。  
 *       别人笑我忒疯癫，我笑自己命太贱；  
 *       不见满街漂亮妹，哪个归得程序员？
 *
 * @Title: SpringRefresh.java 
 * @description TODO
 * @author mac keung_java@126.com
 * @date 2016年4月13日 下午3:39:41 
 */
public class SpringRefresh
{
	private Integer startupShutdownMonitor;
	public void refresh() throws BeansException, IllegalStateException
	{
		synchronized (this.startupShutdownMonitor)
		{
			//刷新之前的准备工作，包括设置启动时间，是否激活标识位，初始化属性源(property source)配置
			prepareRefresh1();
			//由子类去刷新BeanFactory(如果还没创建则创建)，并将BeanFactory返回
			ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();
			//准备BeanFactory以供ApplicationContext使用
			prepareBeanFactory(beanFactory);
			try
			{
				//子类可通过格式此方法来对BeanFactory进行修改
				postProcessBeanFactory(beanFactory);
				//实例化并调用所有注册的BeanFactoryPostProcessor对象
				invokeBeanFactoryPostProcessors(beanFactory);
				//实例化并调用所有注册的BeanPostProcessor对象
				registerBeanPostProcessors(beanFactory);
				//初始化MessageSource
				initMessageSource();
				//初始化事件广播器
				initApplicationEventMulticaster();
				//子类覆盖此方法在刷新过程做额外工作
				onRefresh();
				//注册应用监听器ApplicationListener
				registerListeners();
				//实例化所有non-lazy-init bean
				finishBeanFactoryInitialization(beanFactory);
				//刷新完成工作，包括初始化LifecycleProcessor，发布刷新完成事件等
				finishRefresh();
			} catch (BeansException ex)
			{
				// Destroy already created singletons to avoid dangling resources.
				destroyBeans();
				// Reset 'active' flag.
				cancelRefresh(ex);
				// Propagate exception to caller.
				throw ex;
			}
		}
	}

	/**
	 * 
	 */
	private void prepareRefresh1()
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * @param ex
	 */
	private void cancelRefresh(BeansException ex)
	{
		// TODO Auto-generated method stub
		
	}
	/**
	 * 
	 */
	private void destroyBeans()
	{
		// TODO Auto-generated method stub
		
	}
	/**
	 * 
	 */
	private void finishRefresh()
	{
		// TODO Auto-generated method stub
		
	}
	/**
	 * @param beanFactory
	 */
	private void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory)
	{
		// TODO Auto-generated method stub
		
	}
	/**
	 * 
	 */
	private void registerListeners()
	{
		// TODO Auto-generated method stub
		
	}
	/**
	 * 
	 */
	private void onRefresh()
	{
		// TODO Auto-generated method stub
		
	}
	/**
	 * 
	 */
	private void initApplicationEventMulticaster()
	{
		// TODO Auto-generated method stub
		
	}
	/**
	 * 
	 */
	private void initMessageSource()
	{
		// TODO Auto-generated method stub
		
	}
	/**
	 * @param beanFactory
	 */
	private void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory)
	{
		// TODO Auto-generated method stub
		
	}
	/**
	 * @param beanFactory
	 */
	private void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory)
	{
		// TODO Auto-generated method stub
		
	}
	/**
	 * @param beanFactory
	 */
	private void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
	{
		// TODO Auto-generated method stub
		
	}
	/**
	 * @param beanFactory
	 */
	private void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory)
	{
		// TODO Auto-generated method stub
		
	}
	/**
	 * @return
	 */
	private ConfigurableListableBeanFactory obtainFreshBeanFactory()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
