// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ApplicationConfig.java

package com.autohome.turbo.config;

import com.autohome.turbo.common.compiler.support.AdaptiveCompiler;
import com.autohome.turbo.common.logger.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

// Referenced classes of package com.autohome.turbo.config:
//			AbstractConfig, RegistryConfig, MonitorConfig

public class ApplicationConfig extends AbstractConfig
{

	private static final long serialVersionUID = 0x4c722b594bf1c3f1L;
	private String name;
	private String version;
	private String owner;
	private String organization;
	private String architecture;
	private String environment;
	private String compiler;
	private String logger;
	private List registries;
	private MonitorConfig monitor;
	private Boolean isDefault;

	public ApplicationConfig()
	{
	}

	public ApplicationConfig(String name)
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
		checkMultiName("owner", owner);
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

	public String getArchitecture()
	{
		return architecture;
	}

	public void setArchitecture(String architecture)
	{
		checkName("architecture", architecture);
		this.architecture = architecture;
	}

	public String getEnvironment()
	{
		return environment;
	}

	public void setEnvironment(String environment)
	{
		checkName("environment", environment);
		if (environment != null && !"develop".equals(environment) && !"test".equals(environment) && !"product".equals(environment))
		{
			throw new IllegalStateException((new StringBuilder()).append("Unsupported environment: ").append(environment).append(", only support develop/test/product, default is product.").toString());
		} else
		{
			this.environment = environment;
			return;
		}
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

	public String getCompiler()
	{
		return compiler;
	}

	public void setCompiler(String compiler)
	{
		this.compiler = compiler;
		AdaptiveCompiler.setDefaultCompiler(compiler);
	}

	public String getLogger()
	{
		return logger;
	}

	public void setLogger(String logger)
	{
		this.logger = logger;
		LoggerFactory.setLoggerAdapter(logger);
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
