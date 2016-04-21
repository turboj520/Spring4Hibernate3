// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   RegistryConfig.java

package com.autohome.turbo.config;

import com.autohome.turbo.registry.support.AbstractRegistryFactory;
import java.util.Map;

// Referenced classes of package com.autohome.turbo.config:
//			AbstractConfig

public class RegistryConfig extends AbstractConfig
{

	private static final long serialVersionUID = 0x4c722b594bf1c3f1L;
	public static final String NO_AVAILABLE = "N/A";
	private String address;
	private String username;
	private String password;
	private Integer port;
	private String protocol;
	private String transporter;
	private String server;
	private String client;
	private String cluster;
	private String group;
	private String version;
	private Integer timeout;
	private Integer session;
	private String file;
	private Integer wait;
	private Boolean check;
	private Boolean dynamic;
	private Boolean register;
	private Boolean subscribe;
	private Map parameters;
	private Boolean isDefault;

	public RegistryConfig()
	{
	}

	public RegistryConfig(String address)
	{
		setAddress(address);
	}

	public String getProtocol()
	{
		return protocol;
	}

	public void setProtocol(String protocol)
	{
		checkName("protocol", protocol);
		this.protocol = protocol;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public Integer getPort()
	{
		return port;
	}

	public void setPort(Integer port)
	{
		this.port = port;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		checkName("username", username);
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		checkLength("password", password);
		this.password = password;
	}

	/**
	 * @deprecated Method getWait is deprecated
	 */

	public Integer getWait()
	{
		return wait;
	}

	/**
	 * @deprecated Method setWait is deprecated
	 */

	public void setWait(Integer wait)
	{
		this.wait = wait;
		if (wait != null && wait.intValue() > 0)
			System.setProperty("dubbo.service.shutdown.wait", String.valueOf(wait));
	}

	public Boolean isCheck()
	{
		return check;
	}

	public void setCheck(Boolean check)
	{
		this.check = check;
	}

	public String getFile()
	{
		return file;
	}

	public void setFile(String file)
	{
		checkPathLength("file", file);
		this.file = file;
	}

	/**
	 * @deprecated Method getTransport is deprecated
	 */

	public String getTransport()
	{
		return getTransporter();
	}

	/**
	 * @deprecated Method setTransport is deprecated
	 */

	public void setTransport(String transport)
	{
		setTransporter(transport);
	}

	public String getTransporter()
	{
		return transporter;
	}

	public void setTransporter(String transporter)
	{
		checkName("transporter", transporter);
		this.transporter = transporter;
	}

	public String getServer()
	{
		return server;
	}

	public void setServer(String server)
	{
		checkName("server", server);
		this.server = server;
	}

	public String getClient()
	{
		return client;
	}

	public void setClient(String client)
	{
		checkName("client", client);
		this.client = client;
	}

	public Integer getTimeout()
	{
		return timeout;
	}

	public void setTimeout(Integer timeout)
	{
		this.timeout = timeout;
	}

	public Integer getSession()
	{
		return session;
	}

	public void setSession(Integer session)
	{
		this.session = session;
	}

	public Boolean isDynamic()
	{
		return dynamic;
	}

	public void setDynamic(Boolean dynamic)
	{
		this.dynamic = dynamic;
	}

	public Boolean isRegister()
	{
		return register;
	}

	public void setRegister(Boolean register)
	{
		this.register = register;
	}

	public Boolean isSubscribe()
	{
		return subscribe;
	}

	public void setSubscribe(Boolean subscribe)
	{
		this.subscribe = subscribe;
	}

	public String getCluster()
	{
		return cluster;
	}

	public void setCluster(String cluster)
	{
		this.cluster = cluster;
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

	public static void destroyAll()
	{
		AbstractRegistryFactory.destroyAll();
	}

	/**
	 * @deprecated Method closeAll is deprecated
	 */

	public static void closeAll()
	{
		destroyAll();
	}
}
