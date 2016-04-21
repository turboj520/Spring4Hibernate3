package com.keung.spring4hibernate3.modules.system.service;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.keung.spring4hibernate3.common.persistence.dao.BaseDao;
import com.keung.spring4hibernate3.common.persistence.service.BaseService;
import com.keung.spring4hibernate3.modules.system.entity.MtDisId;
import com.keung.spring4hibernate3.modules.system.entity.User;

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
 * @Title: UserService.java 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author mac keung_java@126.com
 * @email keung_java@126.com
 * @date 2016年3月20日 上午10:31:52 
 */

@Service
@Transactional
public class UserService extends BaseService<User>
{
	@Autowired
	private MtDisIdService mtDisIdService;

	/* (non-Javadoc)
	 * @see com.keung.spring4hibernate3.common.persistence.service.BaseService#setBaseDao()
	 */
	@Override
	@Resource(name = "userDao")
	public void setBaseDao(BaseDao<User> baseDao)
	{
		this.baseDao = baseDao;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(User user) 
	{
		getBaseDao().save(user);
		
		MtDisId m = new MtDisId();
		m.setId(111L);
		mtDisIdService.insert(m);
		
//		User u = new  User();
//		u.setName("guomin");
//		this.insert2(user);
		
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void insert2(User user) 
	{
		getBaseDao().save(user);

		System.out.print(1/0);
		
	}

	
}
