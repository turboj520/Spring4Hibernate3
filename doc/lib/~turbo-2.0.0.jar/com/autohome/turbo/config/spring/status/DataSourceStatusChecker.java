// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   DataSourceStatusChecker.java

package com.autohome.turbo.config.spring.status;

import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.status.Status;
import com.autohome.turbo.common.status.StatusChecker;
import com.autohome.turbo.config.spring.ServiceBean;
import java.sql.*;
import java.util.*;
import javax.sql.DataSource;
import org.springframework.context.ApplicationContext;

public class DataSourceStatusChecker
	implements StatusChecker
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/config/spring/status/DataSourceStatusChecker);

	public DataSourceStatusChecker()
	{
	}

	public Status check()
	{
		com.autohome.turbo.common.status.Status.Level level;
		StringBuilder buf;
		Iterator i$;
		ApplicationContext context = ServiceBean.getSpringContext();
		if (context == null)
			return new Status(com.autohome.turbo.common.status.Status.Level.UNKNOWN);
		Map dataSources = context.getBeansOfType(javax/sql/DataSource, false, false);
		if (dataSources == null || dataSources.size() == 0)
			return new Status(com.autohome.turbo.common.status.Status.Level.UNKNOWN);
		level = com.autohome.turbo.common.status.Status.Level.OK;
		buf = new StringBuilder();
		i$ = dataSources.entrySet().iterator();
_L2:
		DataSource dataSource;
		if (!i$.hasNext())
			break; /* Loop/switch isn't completed */
		java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
		dataSource = (DataSource)entry.getValue();
		if (buf.length() > 0)
			buf.append(", ");
		buf.append((String)entry.getKey());
		Connection connection = dataSource.getConnection();
		DatabaseMetaData metaData;
		ResultSet resultSet;
		metaData = connection.getMetaData();
		resultSet = metaData.getTypeInfo();
		if (!resultSet.next())
			level = com.autohome.turbo.common.status.Status.Level.ERROR;
		resultSet.close();
		break MISSING_BLOCK_LABEL_210;
		Exception exception;
		exception;
		resultSet.close();
		throw exception;
		buf.append(metaData.getURL());
		buf.append("(");
		buf.append(metaData.getDatabaseProductName());
		buf.append("-");
		buf.append(metaData.getDatabaseProductVersion());
		buf.append(")");
		connection.close();
		if (true) goto _L2; else goto _L1
		Exception exception1;
		exception1;
		connection.close();
		throw exception1;
		Throwable e;
		e;
		logger.warn(e.getMessage(), e);
		return new Status(level, e.getMessage());
_L1:
		return new Status(level, buf.toString());
	}

}
