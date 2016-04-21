// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ModuleConfig.java

package com.autohome.turbo.config;

import java.util.ArrayList;
import java.util.List;

// Referenced classes of package com.autohome.turbo.config:
//			AbstractConfig, RegistryConfig, MonitorConfig

public class ModuleConfig extends AbstractConfig
{

	private static final long serialVersionUID = 0x4c722b594bf1c3f1L;
	private String name;
	private String version;
	private String owner;
	private String organization;
	private List registries;
	private MonitorConfig monitor;
	private Boolean isDefault;

	public ModuleConfig()
	{
	}

	public ModuleConfig(String name)
	{
		setName(name);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		checkName("name", name);
		this.name = name;
		if (id == null || id.length() == 0)
			id = name;
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	public String getOwner()
	{
		return owner;
	}

	public void setOwner(String owner)
	{
		checkName("owner", owner);
		this.owner = owner;
	}

	public String getOrganization()
	{
		return organization;
	}

	public void setOrganization(String organization)
	{
		checkName("organization", organization);
		this.organization = organization;
	}

	public RegistryConfig getRegistry()
	{
		return registries != null && registries.size() != 0 ? (RegistryConfig)registries.get(0) : null;
	}

	public void setRegistry(RegistryConfig registry)
	{
		List registries = new ArrayList(1);
		registries.add(registry);
		this.registries = registries;
	}

	public List getRegistries()
	{
		return registries;
	}

	public void setRegistries(List registries)
	{
		this.registries = registries;
	}

	public MonitorConfig getMonitor()
	{
		return monitor;
	}

	public void setMonitor(MonitorConfig monitor)
	{
		this.monitor = monitor;
	}

	public void setMonitor(String monitor)
	{
		this.monitor = new MonitorConfig(monitor);
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
