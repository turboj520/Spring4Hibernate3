// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   MonitorConfig.java

package com.autohome.turbo.config;

import java.util.Map;

// Referenced classes of package com.autohome.turbo.config:
//			AbstractConfig

public class MonitorConfig extends AbstractConfig
{

	private static final long serialVersionUID = 0xef8f2a706ac44b05L;
	private String protocol;
	private String address;
	private String username;
	private String password;
	private String group;
	private String version;
	private Map parameters;
	private Boolean isDefault;

	public MonitorConfig()
	{
	}

	public MonitorConfig(String address)
	{
		this.address = address;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public String getProtocol()
	{
		return protocol;
	}

	public void setProtocol(String protocol)
	{
		this.protocol = protocol;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
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

	public Map getParameters()
	{
		return parameters;
	}

	public void setParameters(Map parameters)
	{
		checkParameterName(parameters);
		this.parameters = parameters;
	}

	public Boolean isDefault()
	{
		return isDefault;
	}

	public void setDefault(Boolean isDefault)
	{
		this.isDefault = isDefault;
	}
}
