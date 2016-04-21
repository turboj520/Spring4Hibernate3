/**
 * 
 */
package com.keung.spring4hibernate3.modules.test;

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
 * @Title: BubbleSortTest.java 
 * @description 冒泡排序
 * @author mac keung_java@126.com
 * @date 2016年4月5日 下午7:30:22 
 */
public class BubbleSortTest
{
	public static void main(String[] args)
	{
		int arr[] = new int[] { 7, 10, 1, 3, 5 };
		int out, in;
		int nElement = 5;

		/*
		 * 外层循环体，out 循环一次 out > nElement - 1 就是排序好得。
		 */
		for (out = nElement - 1; out > 1; out --)
		{
			// 
			for(in = 0; in <  out; in ++)
			{
				if(arr[in] > arr[in + 1]){
					int tmp = arr[in];
					arr[in] = arr[in + 1];
					arr[in + 1] = tmp;
				}
			}
		}

		System.out.println("=====================================");
		for (int i : arr)
		{
			System.out.print(i + " ");
		}

	}
	

}
