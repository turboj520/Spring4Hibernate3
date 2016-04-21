package com.keung.spring4hibernate3.common.persistence.dao;

import ij.util.Tools;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.Id;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.keung.spring4hibernate3.common.persistence.annotation.QueryCacheKeysDefine;
import com.keung.spring4hibernate3.common.persistence.annotation.SessionFactoryDefine;
import com.keung.spring4hibernate3.common.persistence.dao.hibernate.HibernateDaoSupport;
import com.keung.spring4hibernate3.common.persistence.entity.BaseEntity;
import com.keung.spring4hibernate3.common.persistence.entity.DataEntity;
import com.keung.spring4hibernate3.common.util.ReflectionUtils;

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
 * @Title: BaseDao.java 
 * @Description: BaseDao
 * @author mac keung_java@126.com
 * @email keung_java@126.com
 * @date 2016年3月19日 下午10:37:20 
 */
public class BaseDao<T extends DataEntity> extends HibernateDaoSupport
{

	protected Logger logger = LoggerFactory.getLogger("com.keung.spring4hibernate3.dao.BaseDao");

	protected Class<T> entityClass;

	/*
	 * 实体自增主键 ID 名称
	 */
	private static final Map<String, String> entityClassIdName = new HashMap<String, String>();

	/*
	 * 查询缓存定义KEY
	 */
	private static final Map<String, List<String[]>> queryCacheKeys = new HashMap<String, List<String[]>>();

	/*
	 * 第三方缓存
	 */
	// private static final RedisService qCache = new RemoteCache(BaseDao.class.getName(), 5000);

	//	public static final boolean isUseQueryCache = WirelessProperties.S().getBoolean("db.query.needcache", false);

	//	private BaseDao<T> cacheMethodDao;
	
	//	protected SessionFactory sessionFactory;

	//	private static final Set<String> queryCacheExclude = initCacheClassSet(WirelessProperties.S().getStringArray("db.query.needcache.exclude"));

	//	private static final Set<String> queryCacheInclude = initCacheClassSet(WirelessProperties.S().getStringArray("db.query.needcache.include"));

	/**
	 * 构造方法
	 */
	public BaseDao()
	{
		this.entityClass = ReflectionUtils.getSuperClassGenricType(getClass());
	}

	@Resource(name = "sessionFactoryMap")
	public void setBaseDaoSessionFactory(Map<String, SessionFactory> sessionFactoryMap)
	{
		SessionFactoryDefine sfd = entityClass.getAnnotation(SessionFactoryDefine.class);
		if (sfd != null && sfd.key() != null)
		{
			System.out.println(entityClass.getName() + "=====setSessionFactory:" + sfd.key());
			super.setSessionFactory(sessionFactoryMap.get(sfd.key()));
		} else
		{
			
		}
	}

	/**
	 * 新增对象.
	 */
	public Long save(T entity)
	{
		Assert.notNull(entity, "entity不能为空");
		if (entity instanceof BaseEntity)
		{
			Date date = new Date();
			// 插入时间
			((DataEntity) entity).setCreatedTime(date);
			// 插入最后更新时间和插入时间一致
			((DataEntity) entity).setLastUpdatedTime(date);
		}
		Long id = (Long) getHibernateTemplate().save(entity);
		String idname = this.getEntityIdDefineName();
		if (idname != null)
		{
			//			if (Tools.getObjectByKey(entity, idname) != null)
			//			{
			//				this.cacheQueryCache(entity);
			//			}
		} else
		{
			//			this.cacheQueryCache(entity);
		}
		logger.info("save", entity);
		return id;
	}

	/**
	 * 新增或更改对象.
	 */
	public void saveOrUpdate(T entity)
	{
		Assert.notNull(entity, "entity不能为空");
		if (entity instanceof DataEntity && ((DataEntity) entity).getCreatedTime() == null)
			((DataEntity) entity).setCreatedTime(new Date());
		getHibernateTemplate().saveOrUpdate(entity);
		if (super.getSession().isDirty())
		{
			if (entity instanceof DataEntity)
			{
				((DataEntity) entity).setLastUpdatedTime(new Date());//最后一次修改时间
				getHibernateTemplate().update(entity);
			}
		}
		//		this.cacheQueryCache(entity);
		logger.info("saveOrUpdate entity: {}", entity);
	}

	/**
	 * 修改对象.
	 */
	public void update(T entity)
	{
		Assert.notNull(entity, "[entity]不能为空");
		getHibernateTemplate().update(entity);
		if (super.getSession().isDirty())
		{
			if (entity instanceof DataEntity)
			{
				((DataEntity) entity).setLastUpdatedTime(new Date());
				getHibernateTemplate().update(entity);
			}
		}
		//		this.cacheQueryCache(entity);
		logger.info("update entity: {}", entity);
	}

	/**
	 * 按id获取对象.
	 */
	public T find(Long id)
	{
		Assert.notNull(id, "[id]不能为空");
		T entity = null;
		boolean needCache = this.getQueryCacheKeys() != null;
		if (needCache)
		{
			Map<String, Object> m = new HashMap<String, Object>();
			m.put(this.getEntityIdDefineName(), id);
			entity = this.getEntityFromCache(m);
			if (entity != null)
				return entity;
		}
		try
		{
			entity = (T) getHibernateTemplate().load(entityClass, id);
			if (needCache && entity != null)
				this.cacheQueryCache(entity);
			logger.info("find entity id: {}", id);
			return entity;
		} catch (Exception e)
		{

		}
		return null;
	}

	//
	//	public static String getCountSql(String sql)
	//	{
	//		String szRtn = "";
	//		try
	//		{
	//			//sql = sql.trim();
	//			//if(sql.startsWith("select "))
	//			//{
	//			//	sql = "select count(*) as count, " + sql.substring("select ".length());
	//			//}else
	//			{
	//				int index = sql.indexOf(" from ");
	//				if (index >= 0)
	//				{
	//					String latterPart = sql.substring(index);
	//					szRtn = "select count(*) as count " + latterPart;
	//				}
	//			}
	//		} catch (Exception e)
	//		{
	//			e.printStackTrace();
	//		}
	//		return szRtn;
	//	}
	//
	//	private static Set<String> initCacheClassSet(String[] list)
	//	{
	//		if (list != null && list.length > 0)
	//		{
	//			Set<String> s = new HashSet<String>();
	//			for (String ss : list)
	//				s.add(ss);
	//			return s;
	//		}
	//		return null;
	//	}
	//
	//	/**
	//	 * 执行HQL进行批量修改/删除操作.
	//	 * @return 更新记录数.
	//	 */
	//	public int batchExecute(String hql, Map<String, ?> values)
	//	{
	//		int n = HibernateUtils.createQuery(getXSession(), hql, values).executeUpdate();
	//		log("batchExecute", hql, values);
	//		return n;
	//	}
	//
	//	/**
	//	 * 执行HQL进行批量修改/删除操作.
	//	 * @return 更新记录数.
	//	 */
	//	public int batchExecute(String hql, Object... values)
	//	{
	//		int n = HibernateUtils.createQuery(getXSession(), hql, values).executeUpdate();
	//		log("batchExecute", hql, values);
	//		return n;
	//	}
	//
	private void cacheQueryCache(T entity)
	{
		List<String[]> def = getQueryCacheKeys();
		if (def != null)
		{
			StringBuffer key = new StringBuffer();
			Object v = null;
			for (String[] ds : def)
			{
				key.setLength(0);
				key.append(entityClass.getName());
				for (String s : ds)
				{
					key.append('|');
					key.append(s);
					key.append(':');
					//					v = Tools.getObjectByKey(entity, s);
					//					key.append(v == null ? "" : v.toString());
				}
				//				QueryCacheKeysDefine qckd = entityClass.getAnnotation(QueryCacheKeysDefine.class);
				//				if (qckd != null && qckd.timeout() > 0)
				//					qCache.set(key.toString(), entity, qckd.timeout());
				//				else
				//					qCache.set(key.toString(), entity);
			}
		}
	}

	//
	//	public List<Map<String, Object>> changeListToSelectOptions(List<T> list, String idName, String textName)
	//	{
	//		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
	//		try
	//		{
	//			for (int i = 0; i < list.size(); i++)
	//			{
	//				HashMap<String, Object> item = new HashMap<String, Object>();
	//				T t = list.get(i);
	//				Map dest = PropertyUtils.describe(t);
	//				if (dest.containsKey(idName) && dest.containsKey(textName))
	//				{
	//					item.put("id", dest.get(idName));
	//					item.put("text", dest.get(textName));
	//					items.add(item);
	//				}
	//			}
	//		} catch (Exception e)
	//		{
	//			e.printStackTrace();
	//		}
	//		return items;
	//	}
	//
	//	/*
	//	public Session getXSession2()
	//	{
	//		SessionFactory sf = this.getSessionFactory();
	//		if(sf==null)
	//		{
	//			sf = (SessionFactory)ServiceAwareContextFactory.getApplicationContext().getBean("sessionFactory");
	//		}
	//		
	//	//		System.out.println(sf);
	//		Session session = sf.openSession();
	//		//Session session = super.getSession();
	//		if(null==session)
	//		{
	//			//session = SessionFactoryUtils.getSession(sessionFactory, true);
	//		}else
	//		{
	//			//System.out.println("session="+session+",session.isConnected()="+session.isConnected()+",session.isOpen()="+session.isOpen());
	//		}
	//		return session;
	//	}
	//	*/
	//
	//	public void closeSession(Session se)
	//	{
	//		super.releaseSession(se == null ? super.getSession(false) : se);
	//	}
	//
	//	//--------------------------------------------------------------------------------------------------
	//
	//	public Criteria createCriteria(Class clazz)
	//	{
	//		Criteria criteria = HibernateUtils.createCriteria(getXSession(), clazz).setCacheable(isUseQueryCache());
	//		return criteria;
	//	}
	//
	//	public SQLQuery createSQLQuery(String sql)
	//	{
	//		Assert.hasText(sql, "sql不能为空");
	//		return getXSession().createSQLQuery(sql);
	//	}
	//
	//	/**
	//	 * 删除对象.
	//	 */
	//	public void delete(Long id)
	//	{
	//		delete(find(id));
	//		//		logger.debug("delete entity {},id is {}", entityClass.getSimpleName(), id);
	//	}
	//
	//	/**
	//	 * 按照SQL语句删除
	//	 */
	//	public void delete(String hql)
	//	{
	//		Query query = getXSession().createQuery(hql);
	//		query.executeUpdate();
	//		log("delete", hql);
	//	}
	//
	//	/**
	//	 * 删除对象.
	//	 */
	//	public void delete(T entity)
	//	{
	//		Assert.notNull(entity, "entity不能为空");
	//		getHibernateTemplate().delete(entity);
	//		removeQueryCache(entity);
	//		//		logger.debug("delete entity: {}", entity);
	//		log("delete", entity);
	//	}
	//
	//	//从session里去除实体的缓存
	//	public void evictTheEntity(T t)
	//	{
	//		getHibernateTemplate().evict(t);
	//	}
	//
	//	public void evictTheEntitys(List<T> list)
	//	{
	//		for (int i = 0; i < list.size(); i++)
	//		{
	//			getHibernateTemplate().evict(list.get(i));
	//		}
	//	}
	//
	//	/**
	//	 * 通过限定一系列条件进行唯一查询，如果设置了缓存key，会从缓存优先查询
	//	 * @param query
	//	 * @return
	//	 */
	//	public T find(Map<String, Object> query)
	//	{
	//		T entity = null;
	//		boolean needCache = this.getQueryCacheKeys() != null;
	//		if (needCache)
	//		{
	//			entity = this.getEntityFromCache(query);
	//			if (entity != null)
	//				return entity;
	//		}
	//		try
	//		{
	//			StringBuffer hql = new StringBuffer("from ").append(entityClass.getSimpleName()).append(" a where");
	//			HashMap<String, Object> map = new HashMap<String, Object>();
	//			int i = 0;
	//			for (Map.Entry<String, Object> en : query.entrySet())
	//			{
	//				if (Tools.stringIsNotNull(en.getKey()))
	//				{
	//					map.put(en.getKey(), en.getValue());
	//					if (i > 0)
	//						hql.append(" and");
	//					hql.append(" a.").append(en.getKey()).append("=:").append(en.getKey());
	//					i++;
	//				}
	//			}
	//			String idname = this.getEntityIdDefineName();
	//			try
	//			{
	//				if (entityClass.getField(idname) != null)
	//					hql.append(" order by a." + idname + " desc");
	//			} catch (Exception e)
	//			{
	//
	//			}
	//			List<T> list = this.findList(hql.toString(), map, 0, 1);
	//			if (list != null && list.size() > 0)
	//				entity = list.get(0);
	//			if (needCache && entity != null)
	//				this.cacheQueryCache(entity);
	//
	//			log("findList", hql, map);
	//			return entity;
	//		} catch (Exception e)
	//		{
	//
	//		}
	//		return null;
	//	}
	//
	//	/**
	//	 * 分页查询.
	//	 */
	//	public PageData<T> find(PageData<T> pageData)
	//	{
	//		Assert.notNull(pageData, "pageData不能为空");
	//
	//		//System.out.println("xxx");
	//
	//		Criteria criteria = HibernateUtils.createCriteria(getXSession(), entityClass);
	//
	//		criteria.setCacheable(isUseQueryCache());
	//		HibernateUtils.setParameter(criteria, pageData);
	//		pageData.setResult(criteria.list());
	//		log("find", pageData);
	//		return pageData;
	//	}
	//
	//	/**
	//	 * 本地SQL进行修改/删除操作.
	//	 * @return 更新记录数.
	//	 */
	//	public List find(String sql)
	//	{
	//		Assert.hasText(sql, "sql不能为空");
	//		List l = getXSession().createSQLQuery(sql).setCacheable(isUseQueryListCache()).list();
	//		log("find", sql);
	//		return l;
	//	}
	//
	//	/**
	//	 * 按HQL查询唯一对象.
	//	 * @param hql "from Users where name=:name and password=:password"
	//	 * @param values 命名参数,按名称绑定.
	//	 * @return
	//	 */
	//	public <X> X find(String hql, Map<String, ?> values)
	//	{
	//		X x = (X) HibernateUtils.createQuery(getXSession(), hql, values).setCacheable(isUseQueryListCache()).uniqueResult();
	//		log("find", hql, values);
	//		return x;
	//	}
	//
	//	/**
	//	 * 按属性查找唯一对象,匹配方式为相等.
	//	 */
	//	public T find(String fieldName, Object fieldValue)
	//	{
	//		Assert.hasText(fieldName, "fieldName不能为空");
	//		//DetachedCriteria detachedCriteria = null;
	//		//getHibernateTemplate().findByCriteria(detachedCriteria);
	//		T entity = null;
	//		boolean needCache = this.getQueryCacheKeys() != null;
	//		if (needCache)
	//		{
	//			Map<String, Object> m = new HashMap<String, Object>();
	//			m.put(fieldName, fieldValue);
	//			entity = this.getEntityFromCache(m);
	//			if (entity != null)
	//				return entity;
	//		}
	//		Criterion criterion = Restrictions.eq(fieldName, fieldValue);
	//		T t = (T) HibernateUtils.createCriteria(getXSession(), entityClass, criterion).setCacheable(isUseQueryCache()).uniqueResult();
	//		log("find", fieldName, fieldValue);
	//		return t;
	//	}
	//
	//	/**
	//	 * 按HQL查询唯一对象.
	//	 * @param hql "from Users where name=? and password=?"
	//	 * @param values 数量可变的参数,按顺序绑定.
	//	 * @return
	//	 */
	//	public <X> X find(String hql, Object... values)
	//	{
	//		X x = (X) HibernateUtils.createQuery(getXSession(), hql, values).setCacheable(isUseQueryListCache()).uniqueResult();
	//
	//		log("find", hql, values);
	//		return x;
	//	}
	//
	//	/**
	//	 * 获取全部对象.
	//	 */
	//	public List<T> findAll()
	//	{
	//		return findList();
	//	}
	//
	//	/**
	//	 * 获取全部对象,支持排序.
	//	 */
	//	public List<T> findAll(Compositor compositor)
	//	{
	//		return findList(compositor);
	//	}
	//
	//	public List<Map<String, Object>> findByObjectArray(final String hql, Map<String, Object> values, final int pageSize, final int pageNow)
	//	{
	//		StringBuffer queryStringbuffer = new StringBuffer(hql);
	//
	//		Query query = this.getXSession().createQuery(queryStringbuffer.toString());
	//		query.setProperties(values);
	//		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
	//
	//		/* 二级缓存设置 */
	//		query.setCacheable(isUseQueryListCache());
	//		//if(iscache)
	//		//	query.setCacheRegion(cacheName);
	//
	//		if (pageNow >= 0 && pageSize != 0)
	//		{
	//			query.setFirstResult(pageNow);
	//			query.setMaxResults(pageSize);
	//		}
	//
	//		List<Map<String, Object>> list = query.list();
	//		log("findByObjectArray", hql, values, pageSize, pageNow);
	//		return list;
	//	}
	//
	//	public PageData<Map<String, Object>> findByObjectArray(String countHql, String hql, Map<String, Object> values, PageData<Map<String, Object>> pageData)
	//	{
	//		//		System.out.println(hql);
	//
	//		if (countHql == null || countHql.trim().length() <= 0)
	//		{
	//			countHql = getCountSql(hql);
	//		}
	//		Query query = this.getXSession().createQuery(countHql);
	//		query.setProperties(values);
	//		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
	//		List<Map<String, Object>> list = query.list();
	//		if (list != null && list.size() == 1)
	//		{
	//			Long totalCount = (Long) list.get(0).get("count");
	//
	//			pageData.getPagination().setTotalCount(totalCount);
	//
	//			if (totalCount > 0)
	//			{
	//				long start = (pageData.getPagination().getPageNo() - 1) * pageData.getPagination().getPageSize();
	//				long end = start + pageData.getPagination().getPageSize() > totalCount ? totalCount : start + pageData.getPagination().getPageSize();
	//
	//				query = this.getXSession().createQuery(hql);
	//				query.setProperties(values);
	//				query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
	//				query.setCacheable(isUseQueryListCache());
	//
	//				int pageNow = (int) start;
	//				int pageSize = (int) (end - start);
	//				if (pageNow >= 0 && pageSize != 0)
	//				{
	//					query.setFirstResult(pageNow);
	//					query.setMaxResults(pageSize);
	//				}
	//
	//				list = query.list();
	//				pageData.setResult(list);
	//			}
	//		}
	//		log("findByObjectArray", countHql, hql, values, pageData);
	//		return pageData;
	//	}
	//
	//	public List<Object> findByObjectArray(final String classname, final String[] frontName, final String[] propertyName, final Object[] value, final String[] group, final String[] order, final String ordertype[],
	//			final int pageSize, final int pageNow)
	//	{
	//		StringBuffer queryStringbuffer = new StringBuffer();
	//
	//		if (frontName != null)
	//		{
	//			queryStringbuffer.append("select ");
	//			for (String frontname : frontName)
	//			{
	//				if (frontname.indexOf("_distinct") != -1)
	//				{
	//					String[] str = frontname.split("_");
	//					queryStringbuffer.append(" distinct " + str[0] + ",");
	//				} else if (frontname.indexOf("_count") != -1)
	//				{
	//					String[] str = frontname.split("_");
	//					queryStringbuffer.append(" count(" + str[0] + "),");
	//				} else if (frontname.indexOf("_substring") != -1)
	//				{
	//					String[] str = frontname.split("_");
	//					queryStringbuffer.append(" substring(" + str[0] + "," + str[1] + "," + str[2] + "),");
	//				} else if (frontname.indexOf("_sum") != -1)
	//				{
	//					String[] str = frontname.split("_");
	//					queryStringbuffer.append(" sum(" + str[0] + "),");
	//				} else
	//					queryStringbuffer.append(" " + frontname + ",");
	//			}
	//			int n = queryStringbuffer.lastIndexOf(",");
	//			queryStringbuffer = queryStringbuffer.deleteCharAt(n);
	//		}
	//
	//		queryStringbuffer.append(" from " + classname + " as model where 1=1 and ");
	//
	//		if (propertyName != null && value != null)
	//		{
	//			for (int i = 0; i < propertyName.length; i++)
	//			{
	//				if (propertyName[i].indexOf("_start") != -1)
	//				{
	//					String[] str = propertyName[i].split("_");
	//					queryStringbuffer.append("model." + str[0] + ">= ? and ");
	//				} else if (propertyName[i].indexOf("_littlestart") != -1)
	//				{
	//					String[] str = propertyName[i].split("_");
	//					queryStringbuffer.append("model." + str[0] + "> ? and ");
	//				} else if (propertyName[i].indexOf("_end") != -1)
	//				{
	//					String[] str = propertyName[i].split("_");
	//					queryStringbuffer.append("model." + str[0] + "<= ? and ");
	//				} else if (propertyName[i].indexOf("_littleend") != -1)
	//				{
	//					String[] str = propertyName[i].split("_");
	//					queryStringbuffer.append("model." + str[0] + "< ? and ");
	//				} else if (propertyName[i].indexOf("_timetochar24") != -1)
	//				{
	//					String[] str = propertyName[i].split("_");
	//					queryStringbuffer.append("to_char(model." + str[0] + ",'HH24')=? and ");
	//				} else if (propertyName[i].indexOf("_no") != -1)
	//				{
	//					String[] str = propertyName[i].split("_");
	//					queryStringbuffer.append("model." + str[0] + "!=? and ");
	//				} else if (propertyName[i].indexOf("_like") != -1)
	//				{
	//					String[] str = propertyName[i].split("_");
	//					queryStringbuffer.append("model." + str[0] + " like ? and ");
	//				} else
	//				{
	//					queryStringbuffer.append("model." + propertyName[i] + "= ? and ");
	//				}
	//			}
	//		}
	//		int j = queryStringbuffer.lastIndexOf("and");
	//		queryStringbuffer = queryStringbuffer.delete(j, j + 3);
	//
	//		if (group != null)
	//		{
	//			queryStringbuffer.append(" group by ");
	//			for (String g : group)
	//			{
	//				if (g.indexOf("_substring") != -1)
	//				{
	//					String[] str = g.split("_");
	//					queryStringbuffer.append(" substring(" + str[0] + "," + str[1] + "," + str[2] + "),");
	//				} else
	//					queryStringbuffer.append(" " + g + ",");
	//			}
	//			int m = queryStringbuffer.lastIndexOf(",");
	//			queryStringbuffer = queryStringbuffer.deleteCharAt(m);
	//		}
	//
	//		if (order != null && ordertype != null)
	//		{
	//			queryStringbuffer.append(" order by ");
	//			for (int j1 = 0; j1 < order.length; j1++)
	//			{
	//				if (order[j1].indexOf("_substring") != -1)
	//				{
	//					String[] str = order[j1].split("_");
	//					queryStringbuffer.append(" substring(" + str[0] + "," + str[1] + "," + str[2] + ") " + ordertype[j1] + ", ");
	//				} else
	//					queryStringbuffer.append(order[j1] + " " + ordertype[j1] + ", ");
	//			}
	//			int q = queryStringbuffer.lastIndexOf(",");
	//			queryStringbuffer = queryStringbuffer.deleteCharAt(q);
	//		}
	//		//		System.out.println(queryStringbuffer.toString());
	//		Query query = this.getXSession().createQuery(queryStringbuffer.toString());
	//		/* 二级缓存设置 */
	//		query.setCacheable(isUseQueryListCache());
	//		//if(iscache)
	//		//	query.setCacheRegion(classname);
	//		if (propertyName != null && value != null)
	//		{
	//			for (int k = 0; k < propertyName.length; k++)
	//			{
	//				if (propertyName[k].indexOf("_like") != -1)
	//				{
	//					value[k] = "%" + value[k] + "%";
	//				}
	//				query.setParameter(k, value[k]);
	//			}
	//		}
	//		if (pageNow >= 0 && pageSize != 0)
	//		{
	//			query.setFirstResult(pageNow);
	//			query.setMaxResults(pageSize);
	//		}
	//
	//		List list = query.list();
	//
	//		log("findByObjectArray", classname, frontName, propertyName, value, group, order, ordertype, pageSize, pageNow);
	//		return list;
	//	}
	//
	//	/**
	//	 * 利用存储过程执行查询
	//	 * @param procedureName
	//	 * @param param
	//	 * @return
	//	 */
	//	public List<Map<String, Object>> findByProcedure(String procedureName, Map<Integer, Object> param)
	//	{
	//		JiayuanHibernateCallback myWork = new JiayuanHibernateCallback(procedureName, param.size(), param);
	//		this.getSession().doWork(myWork);
	//		log("findByProcedure", procedureName, param);
	//		return myWork.getResult();
	//	}
	//
	//	public <X> List<X> findEntityListBySql(String sql, Map<String, Object> values, Class<?> clazz, Map<String, AbstractSingleColumnStandardBasicType<?>> typeMap)
	//	{
	//		List<X> l = this.findEntityListBySql(sql, values, clazz, typeMap, isUseQueryListCache());
	//		log("findEntityListBySql", sql, values, clazz, typeMap);
	//		return l;
	//	}
	//
	//	public <X> List<X> findEntityListBySql(String sql, Map<String, Object> values, Class<?> clazz, Map<String, AbstractSingleColumnStandardBasicType<?>> typeMap, boolean useCache)
	//	{
	//		List<X> list = null;
	//		try
	//		{
	//			SQLQuery query = createSQLQuery(sql);
	//			Iterator<String> it = typeMap.keySet().iterator();
	//			while (it.hasNext())
	//			{
	//				String key = it.next();
	//				query.addScalar(key, typeMap.get(key));
	//			}
	//
	//			query.setProperties(values);
	//			query.setResultTransformer(Transformers.aliasToBean(clazz));
	//
	//			query.setCacheable(useCache);
	//			list = query.list();
	//		} catch (Exception e)
	//		{
	//			e.printStackTrace();
	//		}
	//		log("findEntityListBySql", sql, values, clazz, typeMap, useCache);
	//		return list;
	//	}
	//
	//	public <X> PageData<X> findEntityPageBySql(String countSql, String sql, Map<String, Object> values, PageData<X> page, Class<?> clazz, Map<String, AbstractSingleColumnStandardBasicType<?>> typeMap, boolean useCache)
	//	{
	//		SQLQuery countQuery = createSQLQuery(countSql);
	//		countQuery.setProperties(values);
	//		countQuery.addScalar("count", StandardBasicTypes.LONG);
	//		Long count = (Long) countQuery.list().get(0);
	//		long totalCount = 0;
	//		if (null != count)
	//		{
	//			totalCount = count.longValue();
	//		}
	//
	//		page.getPagination().setTotalCount(totalCount);
	//		long start = (page.getPagination().getPageNo() - 1) * page.getPagination().getPageSize();
	//		long end = start + page.getPagination().getPageSize() > totalCount ? totalCount : start + page.getPagination().getPageSize();
	//
	//		//sql += " LIMIT " + start + ", " + end;
	//
	//		SQLQuery query = createSQLQuery(sql);
	//		Iterator<String> it = typeMap.keySet().iterator();
	//		while (it.hasNext())
	//		{
	//			String key = it.next();
	//			query.addScalar(key, typeMap.get(key));
	//		}
	//		/*
	//		query.addScalar("user_id", Hibernate.LONG);
	//		query.addScalar("nickname", Hibernate.STRING);
	//		query.addScalar("photo", Hibernate.STRING);
	//		query.addScalar("notread", Hibernate.INTEGER);
	//		query.addScalar("total", Hibernate.INTEGER);
	//		query.addScalar("insert_time", Hibernate.TIMESTAMP);
	//		*/
	//
	//		query.setProperties(values);
	//		query.setResultTransformer(Transformers.aliasToBean(clazz));
	//
	//		query.setCacheable(useCache);
	//
	//		int pageNow = (int) start;
	//		int pageSize = (int) (end - start);
	//		if (pageNow >= 0 && pageSize != 0)
	//		{
	//			query.setFirstResult(pageNow);
	//			query.setMaxResults(pageSize);
	//		}
	//
	//		List<X> list = query.list();
	//		page.setResult(list);
	//
	//		log("findEntityPageBySql", countSql, sql, values, clazz, typeMap, useCache);
	//		return page;
	//	}
	//
	//	public T findInNewSession(Long id)
	//	{
	//		Session se = this.getNewSession();
	//		if (se != null)
	//		{
	//			T t = null;
	//			try
	//			{
	//				Criterion criterion = Restrictions.eq("id", id);
	//				List<T> list = HibernateUtils.createCriteria(se, entityClass, criterion).setCacheable(isUseQueryCache()).list();
	//				if (list != null && list.size() > 0)
	//					t = (T) list.get(0);
	//			} catch (Exception e)
	//			{
	//
	//			}
	//			log("findInNewSession", id);
	//			this.closeSession(se);
	//			return t;
	//		}
	//		return null;
	//	}
	//
	//	/**
	//	 * 按照过滤条件对象查找对象列表，支持排序.
	//	 */
	//	public List<T> findList(Compositor compositor, Filtration... filtrations)
	//	{
	//		Criteria criteria = HibernateUtils.createCriteria(getXSession(), entityClass);
	//		//设置过滤条件
	//		criteria = HibernateUtils.setFiltrationParameter(criteria, filtrations);
	//		//设置排序
	//		criteria = HibernateUtils.setCompositorParameter(criteria, compositor);
	//		List<T> l = criteria.setCacheable(isUseQueryCache()).list();
	//		log("findList", compositor, filtrations);
	//		return l;
	//	}
	//
	//	/**
	//	 * 按照过滤条件对象查找对象列表，支持排序.
	//	 */
	//	public List<T> findList(Compositor compositor, List<Filtration> filtrationList)
	//	{
	//		Criteria criteria = HibernateUtils.createCriteria(getXSession(), entityClass);
	//		//设置过滤条件
	//		criteria = HibernateUtils.setFiltrationParameter(criteria, filtrationList);
	//		//设置排序
	//		criteria = HibernateUtils.setCompositorParameter(criteria, compositor);
	//		List<T> l = criteria.setCacheable(isUseQueryCache()).list();
	//		log("findList", compositor, filtrationList);
	//		return l;
	//	}
	//
	//	public List<T> findList(Compositor compositor, List<Filtration> filtrationList, int startPos, int pageSize)
	//	{
	//		Criteria criteria = HibernateUtils.createCriteria(getXSession(), entityClass);
	//		//设置过滤条件
	//		criteria = HibernateUtils.setFiltrationParameter(criteria, filtrationList);
	//		//设置排序
	//		criteria = HibernateUtils.setCompositorParameter(criteria, compositor);
	//		List<T> l = criteria.setFirstResult(startPos).setMaxResults(pageSize).setCacheable(isUseQueryCache()).list();
	//		log("findList", compositor, filtrationList, startPos, pageSize);
	//		return l;
	//	}
	//
	//	/**
	//	 * 按照过滤条件对象查找对象列表.
	//	 */
	//	public List<T> findList(Filtration... filtrations)
	//	{
	//		Criteria criteria = HibernateUtils.createCriteria(getXSession(), entityClass);
	//		//设置过滤条件
	//		criteria = HibernateUtils.setFiltrationParameter(criteria, filtrations);
	//		List<T> l = criteria.setCacheable(isUseQueryCache()).list();
	//		log("findList", filtrations);
	//		return l;
	//	}
	//
	//	/**
	//	 * 按照过滤条件对象查找对象列表.
	//	 */
	//	public List<T> findList(List<Filtration> filtrationList)
	//	{
	//		Criteria criteria = HibernateUtils.createCriteria(getXSession(), entityClass);
	//		//设置过滤条件
	//		criteria = HibernateUtils.setFiltrationParameter(criteria, filtrationList);
	//		List<T> l = criteria.setCacheable(isUseQueryCache()).list();
	//		log("findList", filtrationList);
	//		return l;
	//	}
	//
	//	/**
	//	 * 按HQL查询对象列表.
	//	 * @param hql "from Users where name=:name and password=:password"
	//	 * @param values 命名参数,按名称绑定.
	//	 * @return 
	//	 */
	//	public <X> List<X> findList(String hql, Map<String, ?> values)
	//	{
	//		List<X> l = HibernateUtils.createQuery(getXSession(), hql, values).setCacheable(isUseQueryListCache()).list();
	//		log("findList", hql, values);
	//		return l;
	//	}
	//
	//	public <X> List<X> findList(String hql, Map<String, ?> values, boolean cacheable)
	//	{
	//		List<X> l = HibernateUtils.createQuery(getXSession(), hql, values).setCacheable(cacheable).list();
	//		log("findList", hql, values, cacheable);
	//		return l;
	//	}
	//
	//	public <X> List<X> findList(String hql, Map<String, ?> values, int startPos, int pageSize)
	//	{
	//		List<X> l = HibernateUtils.createQuery(getXSession(), hql, values).setFirstResult(startPos).setMaxResults(pageSize).setCacheable(isUseQueryListCache()).list();
	//		log("findList", hql, values, startPos, pageSize);
	//		return l;
	//	}
	//
	//	/**
	//	 * 按属性查找对象列表,匹配方式为相等.
	//	 */
	//	public List<T> findList(String fieldName, Object fieldValue)
	//	{
	//		Assert.hasText(fieldName, "fieldName不能为空");
	//		Criterion criterion = Restrictions.eq(fieldName, fieldValue);
	//		List<T> l = HibernateUtils.createCriteria(getXSession(), entityClass, criterion).setCacheable(isUseQueryCache()).list();
	//		log("findList", fieldName, fieldValue);
	//		return l;
	//	}
	//
	//	/**
	//	 * 按HQL查询对象列表.
	//	 * @param hql "from Users where name=? and password=?"
	//	 * @param values 数量可变的参数,按顺序绑定.
	//	 * @return
	//	 */
	//	public <X> List<X> findList(String hql, Object... values)
	//	{
	//		List<X> l = HibernateUtils.createQuery(getXSession(), hql, values).setCacheable(isUseQueryListCache()).list();
	//		log("findList", hql, values);
	//		return l;
	//	}
	//
	//	/**
	//	 * 按id列表获取对象.
	//	 */
	//	public List<T> findListByIds(List<Long> idList)
	//	{
	//		if (idList != null && idList.size() >= 1)
	//		{
	//			Criterion criterion = Restrictions.in("id", idList);
	//			List<T> l = HibernateUtils.createCriteria(getXSession(), entityClass, criterion).setCacheable(isUseQueryCache()).list();
	//
	//			log("findListByIds", idList);
	//			return l;
	//		} else
	//		{
	//			return null;
	//		}
	//	}
	//
	//	public <X> List<X> findListByLimit(String hql, int begin, int size, Object... values)
	//	{
	//		Query query = HibernateUtils.createQuery(getXSession(), hql, values);
	//		query.setFirstResult(0);
	//		query.setMaxResults(1);
	//		query.setCacheable(this.isUseQueryListCache());
	//		List<X> l = query.list();
	//		log("findListByLimit", hql, begin, size, values);
	//		return l;
	//	}
	//
	//	public List<Map<String, Object>> findMapListBySql(String sql, Map<String, Object> values, Map<String, AbstractSingleColumnStandardBasicType<?>> resolveType)
	//	{
	//		return findMapListBySql(sql, values, resolveType, isUseQueryListCache());
	//	}
	//
	//	//--------------------------------------------------------------------------------------------------
	//
	//	public List<Map<String, Object>> findMapListBySql(String sql, Map<String, Object> values, Map<String, AbstractSingleColumnStandardBasicType<?>> resolveType, boolean isCache)
	//	{
	//		try
	//		{
	//			SQLQuery query = createSQLQuery(sql);
	//
	//			for (Map.Entry<String, AbstractSingleColumnStandardBasicType<?>> entry : resolveType.entrySet())
	//			{
	//				query.addScalar(entry.getKey(), entry.getValue());
	//			}
	//
	//			query.setProperties(values);
	//
	//			query.setCacheable(isCache);
	//
	//			query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
	//
	//			List<Map<String, Object>> list = query.list();
	//			log("findMapListBySql", sql, values, resolveType, isCache);
	//			return list;
	//		} catch (Exception e)
	//		{
	//			e.printStackTrace();
	//		}
	//		return null;
	//
	//	}
	//
	//	public PageData<Map<String, Object>> findMapPageBySql(String countSql, String sql, Map<String, Object> values, Map<String, org.hibernate.type.Type> resolveType, PageData<Map<String, Object>> pageData)
	//	{
	//		if (countSql == null || countSql.trim().length() <= 0)
	//		{
	//			countSql = getCountSql(sql);
	//		}
	//
	//		SQLQuery countQuery = createSQLQuery(countSql);
	//		countQuery.setProperties(values);
	//		countQuery.setCacheable(isUseQueryListCache());
	//
	//		countQuery.addScalar("count", Hibernate.LONG);
	//		Long count = (Long) countQuery.list().get(0);
	//		long totalCount = 0;
	//		if (null != count)
	//		{
	//			totalCount = count.longValue();
	//		}
	//		pageData.getPagination().setTotalCount(totalCount);
	//		long start = (pageData.getPagination().getPageNo() - 1) * pageData.getPagination().getPageSize();
	//		long end = start + pageData.getPagination().getPageSize() > totalCount ? totalCount : start + pageData.getPagination().getPageSize();
	//
	//		//sql += " LIMIT " + start + ", " + end;
	//
	//		SQLQuery query = createSQLQuery(sql);
	//
	//		for (Map.Entry<String, org.hibernate.type.Type> entry : resolveType.entrySet())
	//		{
	//			query.addScalar(entry.getKey(), entry.getValue());
	//		}
	//
	//		query.setProperties(values);
	//
	//		query.setCacheable(isUseQueryListCache());
	//
	//		int pageNow = (int) start;
	//		int pageSize = (int) (end - start);
	//		if (pageNow >= 0 && pageSize != 0)
	//		{
	//			query.setFirstResult(pageNow);
	//			query.setMaxResults(pageSize);
	//		}
	//
	//		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
	//
	//		List<Map<String, Object>> list = query.list();
	//		pageData.setResult(list);
	//
	//		log("findMapPageBySql", countSql, sql, values, resolveType, pageData);
	//		return pageData;
	//	}
	//
	//	/*
	//	public Object find2(String hql, Map<String, ?> values)
	//	{
	//		Session s = getXSession2();
	//		Object o = HibernateUtils.createQuery(s, hql, values).setCacheable(isUseQueryCache()).uniqueResult();
	//		if(s.isConnected())
	//			s.close();
	//		return o;
	//	}
	//	
	//	public <X> X find3(String hql, Map<String, ?> values)
	//	{
	//		List<X> list = HibernateUtils.createQuery(getXSession(), hql, values).setCacheable(isUseQueryCache()).list();
	//		if(null==list || list.size()<=0)
	//			return null;
	//		else
	//			return (X)list.get(list.size()-1); 
	//		//.uniqueResult();
	//	}
	//	*/
	//
	//	public List<Object> findObjectList(String hql, Map<String, ?> values)
	//	{
	//		Query query1 = this.getSession().createQuery(hql);
	//		query1.setProperties(values);
	//		List<Object> list = query1.list();
	//
	//		log("findObjectList", hql, values);
	//		return list;
	//	}
	//
	//	public List<Object> findObjectList(String hql, Map<String, ?> values, int count)
	//	{
	//		Query query1 = this.getSession().createQuery(hql);
	//		query1.setFirstResult(0);
	//		query1.setMaxResults(count);
	//		query1.setProperties(values);
	//		List<Object> list = query1.list();
	//
	//		log("findObjectList", hql, values, count);
	//		return list;
	//	}
	//
	//	public PageData<Object> findObjectPage(String counthql, String hql, Map<String, ?> paraMap, PageData<Object> page)
	//	{
	//
	//		Query query = this.getSession().createQuery(counthql);
	//		query.setProperties(paraMap);
	//		Long count = Long.parseLong(query.list().get(0).toString());
	//		page.getPagination().setTotalCount(count);
	//
	//		Query query1 = this.getSession().createQuery(hql);
	//		query1.setProperties(paraMap);
	//		int firstrecord = (page.getPagination().getPageNo() - 1) * page.getPagination().getPageSize();
	//		query1.setFirstResult(firstrecord);
	//		query1.setMaxResults(page.getPagination().getPageSize());
	//
	//		List<Object> list = query1.list();
	//
	//		page.setResult(list);
	//		log("findObjectPage", counthql, hql, paraMap, page);
	//		return page;
	//
	//	}
	//
	//	public PageData<T> findPage(String hql, Map<String, ?> paraMap, PageData<T> page)
	//	{
	//		return findPage(null, hql, paraMap, page);
	//		/*
	//		String counthql = "select count(*) " + hql;
	//		
	//		Query query =  this.getSession().createQuery(counthql);
	//		query.setProperties(paraMap);
	//		Long count = Long.parseLong(query.list().get(0).toString()); 
	//		page.getPagination().setTotalCount(count);
	//		
	//		Query query1 = this.getSession().createQuery(hql);
	//		query1.setProperties(paraMap);
	//		int firstrecord = (page.getPagination().getPageNo() - 1) * page.getPagination().getPageSize();
	//		query1.setFirstResult(firstrecord);
	//		query1.setMaxResults(page.getPagination().getPageSize());
	//		
	//		List<T> list=query1.list();
	//
	//		page.setResult(list);
	//		return page;
	//		*/
	//	}
	//
	//	public PageData<T> findPage(String counthql, String hql, Map<String, ?> paraMap, PageData<T> page)
	//	{
	//		if (counthql == null || counthql.trim().length() <= 0)
	//		{
	//			counthql = "select count(*) " + hql;
	//		}
	//
	//		Query query = this.getSession().createQuery(counthql);
	//		query.setProperties(paraMap);
	//		Long count = Long.parseLong(query.list().get(0).toString());
	//		page.getPagination().setTotalCount(count);
	//
	//		Query query1 = this.getSession().createQuery(hql);
	//		query1.setProperties(paraMap);
	//		int firstrecord = (page.getPagination().getPageNo() - 1) * page.getPagination().getPageSize();
	//		query1.setFirstResult(firstrecord);
	//		query1.setMaxResults(page.getPagination().getPageSize());
	//
	//		List<T> list = query1.list();
	//
	//		page.setResult(list);
	//
	//		log("findPage", counthql, hql, paraMap, page);
	//		return page;
	//	}
	//
	//	/**
	//	 * 按id获取对象.
	//	 */
	//	public T get(Long id)
	//	{
	//		Assert.notNull(id, "id不能为空");
	//
	//		T entity = null;
	//		boolean needCache = this.getQueryCacheKeys() != null;
	//		if (needCache)
	//		{
	//			Map<String, Object> m = new HashMap<String, Object>();
	//			m.put(this.getEntityIdDefineName(), id);
	//			entity = this.getEntityFromCache(m);
	//			if (entity != null)
	//				return entity;
	//		}
	//		try
	//		{
	//			entity = (T) getHibernateTemplate().get(entityClass, id);
	//			if (needCache && entity != null)
	//				this.cacheQueryCache(entity);
	//			log("get", id);
	//			return entity;
	//		} catch (Exception e)
	//		{
	//
	//		}
	//		return null;
	//	}
	//
	//	public BaseDao<T> getCacheMethodDao()
	//	{
	//		if (cacheMethodDao == null)
	//		{
	//			synchronized (this)
	//			{
	//				if (cacheMethodDao == null)
	//					cacheMethodDao = (BaseDao<T>) CacheMethod.newObject(this);
	//			}
	//		}
	//		return cacheMethodDao;
	//	}
	//

	private String getEntityIdDefineName()
	{
		if (entityClass != null)
		{
			String className = entityClass.getName();
			String define = entityClassIdName.get(className);
			if (define == null)
			{
				synchronized (entityClassIdName)
				{
					define = entityClassIdName.get(className);
					if (define == null)
					{
						try
						{
							Class c = entityClass;
							while (c != null && define == null)
							{
								Field[] fs = c.getDeclaredFields();
								for (Field f : fs)
								{
									try
									{
										f.setAccessible(true);
										if (f.getAnnotation(Id.class) != null)
										{
											define = f.getName();
											break;
										}
									} catch (Exception e)
									{

									}
								}
								if (define == null)
									c = c.getSuperclass();
							}
						} catch (Exception e)
						{

						}
						if (define == null)
							define = "id";
						entityClassIdName.put(className, define);
					}
				}
			}
			return define;
		}
		return "id";
	}

	//--------------------------------------------------------------------------------------------------

	private T getEntityFromCache(Map<String, Object> query)
	{
		if (query != null && query.size() > 0)
		{
			StringBuffer key = new StringBuffer();
			key.append(entityClass.getName());
			Set<String> ss = query.keySet();
			String[] sl = new String[ss.size()];
			ss.toArray(sl);
			Arrays.sort(sl);
			Object v = null;
			for (String s : sl)
			{
				if (!StringUtils.isEmpty(s))
				{
					key.append('|');
					key.append(s);
					key.append(':');
					v = query.get(s);
					key.append(v == null ? "" : v.toString());
				}
			}
			return null;
//			return (T) qCache.get(key.toString());
		}
		return null;
	}

	//
	//	public FullTextSession getFullTextSession()
	//	{
	//		FullTextSession fs = null;
	//
	//		Session session = getXSession();
	//		if (session != null)
	//		{
	//			fs = Search.getFullTextSession(session);
	//		}
	//		return fs;
	//	}
	//
	//	public Session getNewSession()
	//	{
	//		return this.getSessionFactory().openSession();
	//	}

	private List<String[]> getQueryCacheKeys()
	{
		if (entityClass != null)
		{
			String className = entityClass.getName();
			List<String[]> define = queryCacheKeys.get(className);
			if (define == null)
			{
				synchronized (queryCacheKeys)
				{
					define = queryCacheKeys.get(className);
					if (define == null)
					{
						define = new ArrayList<String[]>();
						QueryCacheKeysDefine def = entityClass.getAnnotation(QueryCacheKeysDefine.class);
						if (def != null)
						{
							String[] d = def.def();
							if (d != null && d.length > 0)
							{
								List<String> ll = null;
								String[] sl = null;
								String[] sll = null;
								for (String s : d)
								{
									if (s != null)
									{
										sl = s.split("\\,");
										if (sl != null && sl.length > 0)
										{
											ll = new ArrayList<String>();
											for (String ss : sl)
											{
												if (ss != null && !"".equals(ss.trim()))
												{
													ll.add(ss.trim());
												}
											}
											if (ll.size() > 0)
											{
												sll = new String[ll.size()];
												ll.toArray(sll);
												Arrays.sort(sll);
												define.add(sll);
											}
										}
									}
								}
							}
						}
						queryCacheKeys.put(className, define);
					}
				}
			}
			if (define.size() > 0)
				return define;
		}
		return null;
	}

	//	public String getTransactionDebugInfo()
	//	{
	//		StringBuffer sb = new StringBuffer();
	//
	//		try
	//		{
	//			Session s = this.getSession();
	//			if (s != null)
	//			{
	//				boolean isDirty = s.isDirty();
	//				Transaction tran = s.getTransaction();
	//				boolean isCommit = s.getTransaction().wasCommitted();
	//				sb.append("[session=" + s + "]").append("[isDirty=" + isDirty + "]").append("[tran=" + tran + "]").append("[isCommit=" + isCommit + "]");
	//			} else
	//			{
	//				sb.append("[session=" + s + "]");
	//			}
	//		} catch (Exception e)
	//		{
	//			e.printStackTrace();
	//		}
	//
	//		return sb.toString();
	//	}
	//
	//	/**
	//	 * 采用@Resource(name="xxx")按名称注入SessionFactory, 当有多个SesionFactory的时候Override本函数.
	//	 */
	//	/*
	//	@Resource
	//	public void setSessionFactory(SessionFactory sessionFactory)
	//	{
	//		this.sessionFactory = sessionFactory;
	//	}
	//
	//	public SessionFactory getSessionFactory()
	//	{
	//		return sessionFactory;
	//	}
	//	*/
	//	public Session getXSession()
	//	{
	//		//Session session = this.getSessionFactory().openSession();
	//		Session session = super.getSession();
	//		if (null == session)
	//		{
	//			//session = SessionFactoryUtils.getSession(sessionFactory, true);
	//		} else
	//		{
	//			//System.out.println("session="+session+",session.isConnected()="+session.isConnected()+",session.isOpen()="+session.isOpen());
	//		}
	//		return session;
	//	}
	//
	//	private boolean isUseQueryCache()
	//	{
	//		if (isUseQueryCache)
	//		{
	//			if (queryCacheExclude != null)
	//			{
	//				String clazzName = entityClass.getName();
	//				return !queryCacheExclude.contains(clazzName);
	//			}
	//		} else
	//		{
	//			if (queryCacheInclude != null)
	//			{
	//				String clazzName = entityClass.getName();
	//				return queryCacheInclude.contains(clazzName);
	//			}
	//		}
	//		return isUseQueryCache;
	//	}
	//
	//	private boolean isUseQueryListCache()
	//	{
	//		return false;
	//	}
	//
	//	public void log(Object... msg)
	//	{
	//		//		ThreadSessionManager.writeLog(super.getSessionFactory(), SessionFactoryUtils.getSession(super.getSessionFactory(), false), entityClass.getName(), msg);
	//	}
	//
	//	public Long queryCount(String hql, Map<String, ?> paraMap)
	//	{
	//		Query query = this.getSession().createQuery(hql);
	//		query.setProperties(paraMap);
	//		List list = query.list();
	//		Long count = (long) 0;
	//		if (null != list && 0 < list.size() && null != list.get(0))
	//		{
	//			count = Long.parseLong(list.get(0).toString());
	//		}
	//		log("queryCount", hql, paraMap);
	//		return count;
	//	}
	//
	//	public Long queryCountBySql(String sql, Map<String, ?> paraMap)
	//	{
	//		Query query = this.getSession().createSQLQuery(sql).setCacheable(false);
	//		query.setProperties(paraMap);
	//		Long count = Long.parseLong(query.list().get(0).toString());
	//		log("queryCountBySql", sql, paraMap);
	//		return count;
	//	}
	//
	//	private void removeQueryCache(T entity)
	//	{
	//		List<String[]> def = getQueryCacheKeys();
	//		if (def != null)
	//		{
	//			StringBuffer key = new StringBuffer();
	//			Object v = null;
	//			for (String[] ds : def)
	//			{
	//				key.setLength(0);
	//				key.append(entityClass.getName());
	//				for (String s : ds)
	//				{
	//					key.append('|');
	//					key.append(s);
	//					key.append(':');
	//					v = Tools.getObjectByKey(entity, s);
	//					key.append(v == null ? "" : v.toString());
	//				}
	//				qCache.remove(key.toString());
	//			}
	//		}
	//	}
	//

	//	public Session updateXSession()
	//	{
	//		//Session session = this.getSessionFactory().openSession();
	//		Session session = super.getSession();
	//		if (null == session)
	//		{
	//			//session = SessionFactoryUtils.getSession(sessionFactory, true);
	//		} else
	//		{
	//			//System.out.println("session="+session+",session.isConnected()="+session.isConnected()+",session.isOpen()="+session.isOpen());
	//		}
	//		return session;
	//	}
	//
}
