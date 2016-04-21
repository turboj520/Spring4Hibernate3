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
 * @Title: Main.java 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author mac keung_java@126.com
 * @email keung_java@126.com
 * @date 2016年3月17日 下午4:24:00 
 */
package com.keung.spring4hibernate3.modules.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.keung.spring4hibernate3.modules.system.entity.User;
import com.keung.spring4hibernate3.modules.system.service.UserService;

public class Main1
{
	
	private static Logger logger = LoggerFactory.getLogger(Main1.class);

	public static void main(String[] args)
	{
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/import-spring.xml");
		
		context.start();

		UserService service = context.getBean(UserService.class);

//		User user = new User();
//		user.setName("keung_java");
//		logger.info("==========");
//		service.insert(user);
//		context.destroy();
		
	}
}
