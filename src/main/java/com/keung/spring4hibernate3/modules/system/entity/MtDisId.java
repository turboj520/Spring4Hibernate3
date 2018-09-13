/**
 *
 */
package com.keung.spring4hibernate3.modules.system.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.keung.spring4hibernate3.common.persistence.annotation.SessionFactoryDefine;
import com.keung.spring4hibernate3.common.persistence.entity.DataEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Proxy;

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
 * @Title: MtDisId.java
 * @description TODO
 * @author mac keung_java@126.com
 * @date 2016年3月28日 上午10:19:43 
 */

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Proxy(lazy = false)
@Entity
@org.hibernate.annotations.Entity(dynamicUpdate = true)
@SessionFactoryDefine(key = "sessionFactory")
@Table(name = "mt_dis_dealer")
public class MtDisId extends DataEntity {

    private static final long serialVersionUID = 3901040767149328272L;

    private Integer dealerId;

    /**
     * @return the dealerId
     */
    public Integer getDealerId() {
        return dealerId;
    }

    /**
     * @param dealerId the dealerId to set
     */
    public void setDealerId(Integer dealerId) {
        this.dealerId = dealerId;
    }

}
