/**
 * 
 */
package com.keung.spring4hibernate3.modules.system.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

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
 * @Title: HomeController.java 
 * @description TODO
 * @author mac keung_java@126.com
 * @date 2016年4月16日 下午9:04:10 
 */

@Controller
public class HomeController
{
	@RequestMapping("/home")
	public ModelAndView home()
	{
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("result", "home");

		System.err.println("=============a.home===========================");
		return new ModelAndView("modules/system/sys/index", m);
	}

	@RequestMapping("/a/home")
	public ModelAndView home(HttpServletRequest request)
	{
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("result", "/a/home");

		System.err.println("=============a.home===========================");
		return new ModelAndView("modules/system/sys/index", m);
	}

	@RequestMapping("/a/mainContainer")
	public String mainContainer(HttpServletRequest request)
	{
		
		request.setAttribute("result", Math.random());
		return "modules/system/sys/mainContainer";
	}
}
