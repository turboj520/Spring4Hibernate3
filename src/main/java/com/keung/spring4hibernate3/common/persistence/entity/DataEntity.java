package com.keung.spring4hibernate3.common.persistence.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.search.annotations.DateBridge;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Resolution;
import org.hibernate.search.annotations.Store;

import com.keung.spring4hibernate3.common.util.CustomDate2Serializer;

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
 * @Title: DataEntity.java 
 * @Description: 数据实体类
 * @author mac keung_java@126.com
 * @email keung_java@126.com
 * @date 2016年3月19日 下午7:18:08 
 */

@MappedSuperclass
public abstract class DataEntity extends BaseEntity
{
	private static final long serialVersionUID = 1500216943553599460L;

	/*
	 * 记录ID
	 */
	@Id
	@GeneratedValue
	@DocumentId
	protected Long id;
	/*
	 * 创建时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false, updatable = false)
	protected Date createdTime;
	/*
	 * 最后一次修改时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Field(index = Index.UN_TOKENIZED, store = Store.NO)
	@DateBridge(resolution = Resolution.DAY)
	protected Date lastUpdatedTime;
	/*
	 * 创建者
	 */
	@Column(updatable=false)
	protected Long createdBy = 0L;
	/*
	 * 最后一次修改时间
	 */
	@Column(updatable=false)
	protected Long lastUpdatedBy = 0L;
	/*
	 * 删除标志
	 */
	@Enumerated(EnumType.ORDINAL)
	private Long delFlag = 0L;

	/**
	 * @return the id
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * @return the createdTime
	 */
	@JsonSerialize(using = CustomDate2Serializer.class)
	@Field(index = Index.UN_TOKENIZED, store = Store.NO)
	@DateBridge(resolution = Resolution.DAY)
	public Date getCreatedTime()
	{
		return createdTime;
	}

	/**
	 * @param createdTime the createdTime to set
	 */
	public void setCreatedTime(Date createdTime)
	{
		this.createdTime = createdTime;
	}

	/**
	 * @return the lastUpdatedTime
	 */
	@JsonSerialize(using = CustomDate2Serializer.class)
	@Field(index = Index.UN_TOKENIZED, store = Store.NO)
	@DateBridge(resolution = Resolution.DAY)
	public Date getLastUpdatedTime()
	{
		return lastUpdatedTime;
	}

	/**
	 * @param lastUpdatedTime the lastUpdatedTime to set
	 */
	public void setLastUpdatedTime(Date lastUpdatedTime)
	{
		this.lastUpdatedTime = lastUpdatedTime;
	}

	/**
	 * @return the createdBy
	 */
	public Long getCreatedBy()
	{
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(Long createdBy)
	{
		this.createdBy = createdBy;
	}

	/**
	 * @return the lastUpdatedBy
	 */
	public Long getLastUpdatedBy()
	{
		return lastUpdatedBy;
	}

	/**
	 * @param lastUpdatedBy the lastUpdatedBy to set
	 */
	public void setLastUpdatedBy(Long lastUpdatedBy)
	{
		this.lastUpdatedBy = lastUpdatedBy;
	}

	/**
	 * @return the delFlag
	 */
	public Long getDelFlag()
	{
		return delFlag;
	}

	/**
	 * @param delFlag the delFlag to set
	 */
	public void setDelFlag(Long delFlag)
	{
		this.delFlag = delFlag;
	}

}
