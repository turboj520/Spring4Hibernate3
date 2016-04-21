package com.keung.spring4hibernate3.common.persistence.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource
{

	private static boolean beanInitOver = false;
	private List<String> slaveKeys;
	private List<String> otherKeys;
	private static Random random = new Random(System.currentTimeMillis());

	public static void setBeanInitOver()
	{
		beanInitOver = true;
	}

	public void afterPropertiesSet()
	{
		super.afterPropertiesSet();
//		Map targetDataSources = Tools.getObjectByKey(this, "targetDataSources", Map.class);
//		if (targetDataSources != null)
//		{
//			String k = null;
//			for (Iterator it = targetDataSources.keySet().iterator(); it.hasNext();)
//			{
//				k = (String) it.next();
//				if (k != null && !k.equals(DataSourceSwitcher.master))
//				{
//					if (k.startsWith(DataSourceSwitcher.slave))
//					{
//						if (slaveKeys == null)
//							slaveKeys = new ArrayList<String>();
//						slaveKeys.add(k);
//					} else
//					{
//						if (otherKeys == null)
//							otherKeys = new ArrayList<String>();
//						otherKeys.add(k);
//					}
//				}
//			}
//		}
	}

	@Override
	protected Object determineCurrentLookupKey()
	{
		// System.out.println("DynamicDataSource:  使用"+DataSourceSwitcher.getDataSource()+"库");
//		String k = DataSourceSwitcher.getDataSource();
//		if (DataSourceSwitcher.master.equals(k))
//			return k;
//		if (DataSourceSwitcher.slave.equals(k))
//		{
//			if (slaveKeys != null)
//			{
//				return slaveKeys.get(random.nextInt(slaveKeys.size()));
//			}
//		} else
//		{
//
//		}
		return "";
	}

	@Override
	public Connection getConnection() throws SQLException
	{
		// System.out.println("DynamicDataSource("+this+"): getConnection() "+this.toString());
		if (!beanInitOver)
			return super.getConnection();
//		MultiConnection conn = new MultiConnection();
		// conn.setConnection(super.getConnection());
//		conn.setDynamicDataSource(this);
		// System.out.println("DynamicDataSource("+this+"): new
		// MultiConnection("+conn+")");
//		return conn;
		return null;
	}

	public Connection getNewConnection() throws SQLException
	{
		DataSource ds = super.determineTargetDataSource();
		if (ds != null)
			return ds.getConnection();
		return null;
	}
}
