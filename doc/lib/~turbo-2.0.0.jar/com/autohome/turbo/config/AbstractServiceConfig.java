// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractServiceConfig.java

package com.autohome.turbo.config;

import com.autohome.turbo.rpc.ExporterListener;
import java.util.Arrays;
import java.util.List;

// Referenced classes of package com.autohome.turbo.config:
//			AbstractInterfaceConfig, ProtocolConfig, RegistryConfig

public abstract class AbstractServiceConfig extends AbstractInterfaceConfig
{

	private static final long serialVersionUID = 1L;
	protected String version;
	protected String group;
	protected Boolean deprecated;
	protected Integer delay;
	protected Boolean export;
	protected Integer weight;
	protected String document;
	protected Boolean dynamic;
	protected String token;
	protected String accesslog;
	private Integer executes;
	protected List protocols;
	private Boolean register;

	public AbstractServiceConfig()
	{
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		checkKey("version", version);
		this.version = version;
	}

	public String getGroup()
	{
		return group;
	}

	public void setGroup(String group)
	{
		checkKey("group", group);
		this.group = group;
	}

	public Integer getDelay()
	{
		return delay;
	}

	public void setDelay(Integer delay)
	{
		this.delay = delay;
	}

	public Boolean getExport()
	{
		return export;
	}

	public void setExport(Boolean export)
	{
		this.export = export;
	}

	public Integer getWeight()
	{
		return weight;
	}

	public void setWeight(Integer weight)
	{
		this.weight = weight;
	}

	public String getDocument()
	{
		return document;
	}

	public void setDocument(String document)
	{
		this.document = document;
	}

	public String getToken()
	{
		return token;
	}

	public void setToken(String token)
	{
		checkName("token", token);
		this.token = token;
	}

	public void setToken(Boolean token)
	{
		if (token == null)
			setToken((String)null);
		else
			setToken(String.valueOf(token));
	}

	public Boolean isDeprecated()
	{
		return deprecated;
	}

	public void setDeprecated(Boolean deprecated)
	{
		this.deprecated = deprecated;
	}

	public Boolean isDynamic()
	{
		return dynamic;
	}

	public void setDynamic(Boolean dynamic)
	{
		this.dynamic = dynamic;
	}

	public List getProtocols()
	{
		return protocols;
	}

	public void setProtocols(List protocols)
	{
		this.protocols = protocols;
	}

	public ProtocolConfig getProtocol()
	{
		return protocols != null && protocols.size() != 0 ? (ProtocolConfig)protocols.get(0) : null;
	}

	public void setProtocol(ProtocolConfig protocol)
	{
		protocols = Arrays.asList(new ProtocolConfig[] {
			protocol
		});
	}

	public String getAccesslog()
	{
		return accesslog;
	}

	public void setAccesslog(String accesslog)
	{
		this.accesslog = accesslog;
	}

	public void setAccesslog(Boolean accesslog)
	{
		if (accesslog == null)
			setAccesslog((String)null);
		else
			setAccesslog(String.valueOf(accesslog));
	}

	public Integer getExecutes()
	{
		return executes;
	}

	public void setExecutes(Integer executes)
	{
		this.executes = executes;
	}

	public String getFilter()
	{
		return super.getFilter();
	}

	public String getListener()
	{
		return super.getListener();
	}

	public void setListener(String listener)
	{
		checkMultiExtension(com/autohome/turbo/rpc/ExporterListener, "listener", listener);
		super.setListener(listener);
	}

	public Boolean isRegister()
	{
		return register;
	}

	public void setRegister(Boolean register)
	{
		this.register = register;
		if (Boolean.FALSE.equals(register))
			setRegistry(new RegistryConfig("N/A"));
	}
}
