// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Statistics.java

package com.autohome.turbo.monitor.dubbo;

import com.autohome.turbo.common.URL;
import java.io.Serializable;

public class Statistics
	implements Serializable
{

	private static final long serialVersionUID = 0x9ff304add5508b9fL;
	private URL url;
	private String application;
	private String service;
	private String method;
	private String group;
	private String version;
	private String client;
	private String server;

	public Statistics(URL url)
	{
		this.url = url;
		application = url.getParameter("application");
		service = url.getParameter("interface");
		method = url.getParameter("method");
		group = url.getParameter("group");
		version = url.getParameter("version");
		client = url.getParameter("consumer", url.getAddress());
		server = url.getParameter("provider", url.getAddress());
	}

	public URL getUrl()
	{
		return url;
	}

	public void setUrl(URL url)
	{
		this.url = url;
	}

	public String getApplication()
	{
		return application;
	}

	public Statistics setApplication(String application)
	{
		this.application = application;
		return this;
	}

	public String getService()
	{
		return service;
	}

	public Statistics setService(String service)
	{
		this.service = service;
		return this;
	}

	public String getGroup()
	{
		return group;
	}

	public void setGroup(String group)
	{
		this.group = group;
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	public String getMethod()
	{
		return method;
	}

	public Statistics setMethod(String method)
	{
		this.method = method;
		return this;
	}

	public String getClient()
	{
		return client;
	}

	public Statistics setClient(String client)
	{
		this.client = client;
		return this;
	}

	public String getServer()
	{
		return server;
	}

	public Statistics setServer(String server)
	{
		this.server = server;
		return this;
	}

	public int hashCode()
	{
		int prime = 31;
		int result = 1;
		result = 31 * result + (application != null ? application.hashCode() : 0);
		result = 31 * result + (client != null ? client.hashCode() : 0);
		result = 31 * result + (group != null ? group.hashCode() : 0);
		result = 31 * result + (method != null ? method.hashCode() : 0);
		result = 31 * result + (server != null ? server.hashCode() : 0);
		result = 31 * result + (service != null ? service.hashCode() : 0);
		result = 31 * result + (version != null ? version.hashCode() : 0);
		return result;
	}

	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Statistics other = (Statistics)obj;
		if (application == null)
		{
			if (other.application != null)
				return false;
		} else
		if (!application.equals(other.application))
			return false;
		if (client == null)
		{
			if (other.client != null)
				return false;
		} else
		if (!client.equals(other.client))
			return false;
		if (group == null)
		{
			if (other.group != null)
				return false;
		} else
		if (!group.equals(other.group))
			return false;
		if (method == null)
		{
			if (other.method != null)
				return false;
		} else
		if (!method.equals(other.method))
			return false;
		if (server == null)
		{
			if (other.server != null)
				return false;
		} else
		if (!server.equals(other.server))
			return false;
		if (service == null)
		{
			if (other.service != null)
				return false;
		} else
		if (!service.equals(other.service))
			return false;
		if (version == null)
		{
			if (other.version != null)
				return false;
		} else
		if (!version.equals(other.version))
			return false;
		return true;
	}

	public String toString()
	{
		return url.toString();
	}
}
