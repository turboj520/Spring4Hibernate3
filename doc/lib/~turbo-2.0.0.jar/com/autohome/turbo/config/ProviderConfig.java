// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ProviderConfig.java

package com.autohome.turbo.config;

import com.autohome.turbo.common.status.StatusChecker;
import com.autohome.turbo.common.threadpool.ThreadPool;
import com.autohome.turbo.remoting.Dispatcher;
import com.autohome.turbo.remoting.Transporter;
import com.autohome.turbo.remoting.exchange.Exchanger;
import com.autohome.turbo.remoting.telnet.TelnetHandler;
import java.util.Arrays;

// Referenced classes of package com.autohome.turbo.config:
//			AbstractServiceConfig, ProtocolConfig

public class ProviderConfig extends AbstractServiceConfig
{

	private static final long serialVersionUID = 0x5ff16a6469e9ef7dL;
	private String host;
	private Integer port;
	private String contextpath;
	private String threadpool;
	private Integer threads;
	private Integer iothreads;
	private Integer queues;
	private Integer accepts;
	private String codec;
	private String serialization;
	private String charset;
	private Integer payload;
	private Integer buffer;
	private String transporter;
	private String exchanger;
	private String dispatcher;
	private String networker;
	private String server;
	private String client;
	private String telnet;
	private String prompt;
	private String status;
	private Integer wait;
	private Boolean isDefault;

	public ProviderConfig()
	{
	}

	/**
	 * @deprecated Method setProtocol is deprecated
	 */

	public void setProtocol(String protocol)
	{
		protocols = Arrays.asList(new ProtocolConfig[] {
			new ProtocolConfig(protocol)
		});
	}

	public Boolean isDefault()
	{
		return isDefault;
	}

	/**
	 * @deprecated Method setDefault is deprecated
	 */

	public void setDefault(Boolean isDefault)
	{
		this.isDefault = isDefault;
	}

	public String getHost()
	{
		return host;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public Integer getPort()
	{
		return port;
	}

	/**
	 * @deprecated Method setPort is deprecated
	 */

	public void setPort(Integer port)
	{
		this.port = port;
	}

	/**
	 * @deprecated Method getPath is deprecated
	 */

	public String getPath()
	{
		return getContextpath();
	}

	/**
	 * @deprecated Method setPath is deprecated
	 */

	public void setPath(String path)
	{
		setContextpath(path);
	}

	public String getContextpath()
	{
		return contextpath;
	}

	public void setContextpath(String contextpath)
	{
		checkPathName("contextpath", contextpath);
		this.contextpath = contextpath;
	}

	public String getThreadpool()
	{
		return threadpool;
	}

	public void setThreadpool(String threadpool)
	{
		checkExtension(com/autohome/turbo/common/threadpool/ThreadPool, "threadpool", threadpool);
		this.threadpool = threadpool;
	}

	public Integer getThreads()
	{
		return threads;
	}

	public void setThreads(Integer threads)
	{
		this.threads = threads;
	}

	public Integer getIothreads()
	{
		return iothreads;
	}

	public void setIothreads(Integer iothreads)
	{
		this.iothreads = iothreads;
	}

	public Integer getQueues()
	{
		return queues;
	}

	public void setQueues(Integer queues)
	{
		this.queues = queues;
	}

	public Integer getAccepts()
	{
		return accepts;
	}

	public void setAccepts(Integer accepts)
	{
		this.accepts = accepts;
	}

	public String getCodec()
	{
		return codec;
	}

	public void setCodec(String codec)
	{
		this.codec = codec;
	}

	public String getSerialization()
	{
		return serialization;
	}

	public void setSerialization(String serialization)
	{
		this.serialization = serialization;
	}

	public String getCharset()
	{
		return charset;
	}

	public void setCharset(String charset)
	{
		this.charset = charset;
	}

	public Integer getPayload()
	{
		return payload;
	}

	public void setPayload(Integer payload)
	{
		this.payload = payload;
	}

	public Integer getBuffer()
	{
		return buffer;
	}

	public void setBuffer(Integer buffer)
	{
		this.buffer = buffer;
	}

	public String getServer()
	{
		return server;
	}

	public void setServer(String server)
	{
		this.server = server;
	}

	public String getClient()
	{
		return client;
	}

	public void setClient(String client)
	{
		this.client = client;
	}

	public String getTelnet()
	{
		return telnet;
	}

	public void setTelnet(String telnet)
	{
		checkMultiExtension(com/autohome/turbo/remoting/telnet/TelnetHandler, "telnet", telnet);
		this.telnet = telnet;
	}

	public String getPrompt()
	{
		return prompt;
	}

	public void setPrompt(String prompt)
	{
		this.prompt = prompt;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		checkMultiExtension(com/autohome/turbo/common/status/StatusChecker, "status", status);
		this.status = status;
	}

	public String getCluster()
	{
		return super.getCluster();
	}

	public Integer getConnections()
	{
		return super.getConnections();
	}

	public Integer getTimeout()
	{
		return super.getTimeout();
	}

	public Integer getRetries()
	{
		return super.getRetries();
	}

	public String getLoadbalance()
	{
		return super.getLoadbalance();
	}

	public Boolean isAsync()
	{
		return super.isAsync();
	}

	public Integer getActives()
	{
		return super.getActives();
	}

	public String getTransporter()
	{
		return transporter;
	}

	public void setTransporter(String transporter)
	{
		checkExtension(com/autohome/turbo/remoting/Transporter, "transporter", transporter);
		this.transporter = transporter;
	}

	public String getExchanger()
	{
		return exchanger;
	}

	public void setExchanger(String exchanger)
	{
		checkExtension(com/autohome/turbo/remoting/exchange/Exchanger, "exchanger", exchanger);
		this.exchanger = exchanger;
	}

	/**
	 * @deprecated Method getDispather is deprecated
	 */

	public String getDispather()
	{
		return getDispatcher();
	}

	/**
	 * @deprecated Method setDispather is deprecated
	 */

	public void setDispather(String dispather)
	{
		setDispatcher(dispather);
	}

	public String getDispatcher()
	{
		return dispatcher;
	}

	public void setDispatcher(String dispatcher)
	{
		checkExtension(com/autohome/turbo/remoting/Dispatcher, "dispatcher", exchanger);
		checkExtension(com/autohome/turbo/remoting/Dispatcher, "dispather", exchanger);
		this.dispatcher = dispatcher;
	}

	public String getNetworker()
	{
		return networker;
	}

	public void setNetworker(String networker)
	{
		this.networker = networker;
	}

	public Integer getWait()
	{
		return wait;
	}

	public void setWait(Integer wait)
	{
		this.wait = wait;
	}
}
