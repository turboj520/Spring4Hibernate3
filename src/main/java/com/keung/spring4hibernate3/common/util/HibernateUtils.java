package com.keung.spring4hibernate3.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.transform.ResultTransformer;
import org.springframework.util.Assert;

/**
 * hibernate工具类
 * @author yangzhibin
 *
 */
public class HibernateUtils
{
	/**
	 * 根据查询HQL与参数列表创建Query对象.
	 */
	public static Query createQuery(Session session, String hql, Object... values)
	{
		Assert.hasText(hql, "hql不能为空");
		Query query = session.createQuery(hql);

		for (int i = 0; i < values.length; i++)
		{
			query.setParameter(i, values[i]);
		}

		return query;
	}

	/**
	 * 根据查询HQL与参数列表创建Query对象.
	 */
	public static Query createQuery(Session session, String hql, Map<String, ?> values)
	{
		Assert.hasText(hql, "hql不能为空");
		Query query = session.createQuery(hql);
		if (values != null)
		{
			query.setProperties(values);
		}
		return query;
	}

	/**
	 * 创建Criterion
	 */
//	private static Criterion createCriterion(String fieldName, Object fieldValue, MatchType matchType)
//	{
//		Criterion criterion = null;
//		Assert.hasText(fieldName, "fieldName不能为空");
//		switch (matchType)
//		{
//		case EQ: // =
//			criterion = Restrictions.eq(fieldName, fieldValue);
//			break;
//		case LIKE: // like
//			criterion = Restrictions.like(fieldName, (String) fieldValue, MatchMode.ANYWHERE);
//			break;
//		case LT: // <
//			criterion = Restrictions.lt(fieldName, fieldValue);
//			break;
//		case LE: // <=
//			criterion = Restrictions.le(fieldName, fieldValue);
//			break;
//		case GT: // >
//			criterion = Restrictions.gt(fieldName, fieldValue);
//			break;
//		case GE: // >=
//			criterion = Restrictions.ge(fieldName, fieldValue);
//			break;
//		}
//		return criterion;
//	}

	/**
	 * 执行count查询获得本次Criteria查询所能获得的对象总数.
	 */
	@SuppressWarnings("unchecked")
	public static long countCriteriaResult(Criteria criteria)
	{
		CriteriaImpl impl = (CriteriaImpl) criteria;

		// 先把Projection、ResultTransformer、OrderBy取出来,清空三者后再执行Count操作
		Projection projection = impl.getProjection();
		ResultTransformer resultTransformer = impl.getResultTransformer();

		List<CriteriaImpl.OrderEntry> orderEntries = null;
		orderEntries = (List) ReflectionUtils.getFieldValue(impl, "orderEntries");
		ReflectionUtils.setFieldValue(impl, "orderEntries", new ArrayList());

		// 执行Count查询
		long totalCount = (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();

		// 将之前的Projection,ResultTransformer和OrderBy条件重新设回去
		criteria.setProjection(projection);

		if (projection == null)
		{
			criteria.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
		}
		if (resultTransformer != null)
		{
			criteria.setResultTransformer(resultTransformer);
		}

		ReflectionUtils.setFieldValue(impl, "orderEntries", orderEntries);

		return totalCount;
	}

	/**
	 * 设置排序参数到Criteria对象
	 */
//	public static Criteria setCompositorParameter(Criteria criteria, Compositor compositor)
//	{
//		if (compositor != null)
//		{
//			String fieldName = compositor.getFieldName();
//			CompositorType compositorType = compositor.getCompositorType();
//			switch (compositorType)
//			{
//			case ASC:
//				criteria.addOrder(Order.asc(fieldName));
//				break;
//			case DESC:
//				criteria.addOrder(Order.desc(fieldName));
//				break;
//			}
//		}
//
//		return criteria;
//	}

	/**
	 * 设置过滤条件到Criteria对象
	 */
//	public static Criteria setFiltrationParameter(Criteria criteria, Filtration... filtrations)
//	{
//		if (filtrations.length > 0)
//		{
//			List<Criterion> criterions = new ArrayList<Criterion>();
//			for (Filtration filtration : filtrations)
//			{
//				Criterion criterion = null;
//				if (!filtration.isMultiFilter())
//				{
//					criterion = createCriterion(filtration.getFieldName(), filtration.getFieldValue(), filtration.getMatchType());
//					criterions.add(criterion);
//
//				} else
//				{
//					//包含多个属性需要比较的情况,进行or处理.
//					Disjunction disjunction = Restrictions.disjunction();
//					for (String filedName : filtration.getFieldNames())
//					{
//						criterion = createCriterion(filedName, filtration.getFieldValue(), filtration.getMatchType());
//						disjunction.add(criterion);
//					}
//					criterions.add(disjunction);
//				}
//			}
//			for (Criterion criterion : criterions)
//			{
//				criteria.add(criterion);
//			}
//		}
//
//		return criteria;
//	}

	/**
	 * 设置过滤条件到Criteria对象
	 */
//	public static Criteria setFiltrationParameter(Criteria criteria, List<Filtration> filtrationList)
//	{
//		if (filtrationList != null)
//		{
//			//Filtration[] filtrations = (Filtration[]) filtrationList.toArray();
//			Filtration[] filtrations = new Filtration[filtrationList.size()];
//			for(int i =0;i<filtrationList.size();i++)
//			{
//				filtrations[i] = filtrationList.get(i);
//			}
//			return setFiltrationParameter(criteria, filtrations);
//		} else
//		{
//			return criteria;
//		}
//	}

	/**
	 * 设置分页参数到Criteria对象
	 */
//	public static Criteria setParameter(Criteria criteria, PageData<?> pageData)
//	{
//		//第一步：设置查询条件
//		setFiltrationParameter(criteria, pageData.getFiltrations());
//		//第二步：读取记录总数
//		if (pageData.getPagination().isReadTotalCount())
//		{
//			long totalCount = countCriteriaResult(criteria);
//			pageData.getPagination().setTotalCount(totalCount);
//		}
//
//		//第三步：设置查询范围
//		criteria.setFirstResult(pageData.getPagination().getCurrentlyPageFirstResoultIndex());
//		criteria.setMaxResults(pageData.getPagination().getPageSize());
//
//		//排序条件
//		setCompositorParameter(criteria, pageData.getCompositor());
//
//		return criteria;
//	}
}
