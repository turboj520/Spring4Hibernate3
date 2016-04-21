/**
 * 
 */
package com.keung.spring4hibernate3;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
 * @Title: HashTest.java 
 * @description TODO
 * @author mac keung_java@126.com
 * @date 2016年4月2日 下午8:30:31 
 */
public class HashTest
{
	public static void main(String[] args)
	{
		String a = "0";
		System.out.println(a.hashCode());

		String str1 = new String("str");
		String str2 = new String("str");

		System.out.println(str1.hashCode() + " " + str2.hashCode());
		
		Map<Object, Object> map = new HashMap<Object, Object>();
		
		
		Integer i1 = 12;
		
		System.out.println(i1.hashCode() + " " + 12%16);
		map.put(i1, "i1");
		map.put(13, "13");
		Iterator<Object> i = map.keySet().iterator();
	}

}
