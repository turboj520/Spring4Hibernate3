// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractMethodConfig.java

package com.autohome.turbo.config;

import com.autohome.turbo.rpc.cluster.LoadBalance;
import java.util.Map;

// Referenced classes of package com.autohome.turbo.config:
//			AbstractConfig

public abstract class AbstractMethodConfig extends AbstractConfig
{

	private static final long serialVersionUID = 1L;
	protected Integer timeout;
	protected Integer retries;
	protected Integer actives;
	protected String loadbalance;
	protected Boolean async;
	protected Boolean sent;
	protected String mock;
	protected String merger;
	protected String cache;
	protected String validation;
	protected Map parameters;

	public AbstractMethodConfig()
	{
	}

	public Integer getTimeout()
	{
		return timeout;
	}

	public void setTimeout(Integer timeout)
	{
		this.timeout = timeout;
	}

	public Integer getRetries()
	{
		return retries;
	}

	public void setRetries(Integer retries)
	{
		this.retries = retries;
	}

	public String getLoadbalance()
	{
		return loadbalance;
	}

	public void setLoadbalance(String loadbalance)
	{
		checkExtension(com/autohome/turbo/rpc/cluster/LoadBalance, "loadbalance", loadbalance);
		this.loadbalance = loadbalance;
	}

	public Boolean isAsync()
	{
		return async;
	}

	public void setAsync(Boolean async)
	{
		this.async = async;
	}

	public Integer getActives()
	{
		return actives;
	}

	public void setActives(Integer actives)
	{
		this.actives = actives;
	}

	public Boolean getSent()
	{
		return sent;
	}

	public void setSent(Boolean sent)
	{
		this.sent = sent;
	}

	public String getMock()
	{
		return mock;
	}

	public void setMock(String mock)
	{
		if (mock != null && mock.startsWith("return "))
			checkLength("mock", mock);
		else
			checkName("mock", mock);
		this.mock = mock;
	}

	public void setMock(Boolean mock)
	{
		if (mock == null)
			setMock((String)null);
		else
			setMock(String.valueOf(mock));
	}

	public String getMerger()
	{
		return merger;
	}

	public void setMerger(String merger)
	{
		this.merger = merger;
	}

	public String getCache()
	{
		return cache;
	}

	public void setCache(String cache)
	{
		this.cache = cache;
	}

	public String getValidation()
	{
		return validation;
	}

	public void setValidation(String validation)
	{
		this.validation = validation;
	}

	public Map getParameters()
	{
		return parameters;
	}

	public void setParameters(Map parameters)
	{
		checkParameterName(parameters);
		this.parameters = parameters;
	}
}
