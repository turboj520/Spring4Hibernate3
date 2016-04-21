// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ProtocolConfig.java

package com.autohome.turbo.config;

import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.serialize.Serialization;
import com.autohome.turbo.common.status.StatusChecker;
import com.autohome.turbo.common.threadpool.ThreadPool;
import com.autohome.turbo.registry.support.AbstractRegistryFactory;
import com.autohome.turbo.remoting.*;
import com.autohome.turbo.remoting.exchange.Exchanger;
import com.autohome.turbo.remoting.telnet.TelnetHandler;
import com.autohome.turbo.rpc.Protocol;
import java.util.*;

// Referenced classes of package com.autohome.turbo.config:
//			AbstractConfig

public class ProtocolConfig extends AbstractConfig
{

	private static final long serialVersionUID = 0x5ff16a6469e9ef7dL;
	private String name;
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
	private Integer heartbeat;
	private String accesslog;
	private String transporter;
	private String exchanger;
	private String dispatcher;
	private String networker;
	private String server;
	private String client;
	private String telnet;
	private String prompt;
	private String status;
	private Boolean register;
	private Boolean keepAlive;
	private String optimizer;
	private String extension;
	private Map parameters;
	private Boolean isDefault;

	public ProtocolConfig()
	{
	}

	public ProtocolConfig(String name)
	{
		setName(name);
	}

	public ProtocolConfig(String name, int port)
	{
		setName(name);
		setPort(Integer.valueOf(port));
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

	public String getHost()
	{
		return host;
	}

	public void setHost(String host)
	{
		checkName("host", host);
		this.host = host;
	}

	public Integer getPort()
	{
		return port;
	}

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
		if ("dubbo".equals(name))
			checkMultiExtension(com/autohome/turbo/remoting/Codec, "codec", codec);
		this.codec = codec;
	}

	public String getSerialization()
	{
		return serialization;
	}

	public void setSerialization(String serialization)
	{
		if ("dubbo".equals(name))
			checkMultiExtension(com/autohome/turbo/common/serialize/Serialization, "serialization", serialization);
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

	public Integer getHeartbeat()
	{
		return heartbeat;
	}

	public void setHeartbeat(Integer heartbeat)
	{
		this.heartbeat = heartbeat;
	}

	public String getServer()
	{
		return server;
	}

	public void setServer(String server)
	{
		if ("dubbo".equals(name))
			checkMultiExtension(com/autohome/turbo/remoting/Transporter, "server", server);
		this.server = server;
	}

	public String getClient()
	{
		return client;
	}

	public void setClient(String client)
	{
		if ("dubbo".equals(name))
			checkMultiExtension(com/autohome/turbo/remoting/Transporter, "client", client);
		this.client = client;
	}

	public String getAccesslog()
	{
		return accesslog;
	}

	public void setAccesslog(String accesslog)
	{
		this.accesslog = accesslog;
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

	public Boolean isRegister()
	{
		return register;
	}

	public void setRegister(Boolean register)
	{
		this.register = register;
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
		checkExtension(com/autohome/turbo/remoting/Dispatcher, "dispacther", dispatcher);
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

	public Map getParameters()
	{
		return parameters;
	}

	public void setParameters(Map parameters)
	{
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

	public Boolean getKeepAlive()
	{
		return keepAlive;
	}

	public void setKeepAlive(Boolean keepAlive)
	{
		this.keepAlive = keepAlive;
	}

	public String getOptimizer()
	{
		return optimizer;
	}

	public void setOptimizer(String optimizer)
	{
		this.optimizer = optimizer;
	}

	public String getExtension()
	{
		return extension;
	}

	public void setExtension(String extension)
	{
		this.extension = extension;
	}

	public void destory()
	{
		if (name != null)
			((Protocol)ExtensionLoader.getExtensionLoader(com/autohome/turbo/rpc/Protocol).getExtension(name)).destroy();
	}

	public static void destroyAll()
	{
		AbstractRegistryFactory.destroyAll();
		ExtensionLoader loader = ExtensionLoader.getExtensionLoader(com/autohome/turbo/rpc/Protocol);
		Iterator i$ = loader.getLoadedExtensions().iterator();
		do
		{
			if (!i$.hasNext())
				break;
			String protocolName = (String)i$.next();
			try
			{
				Protocol protocol = (Protocol)loader.getLoadedExtension(protocolName);
				if (protocol != null)
					protocol.destroy();
			}
			catch (Throwable t)
			{
				logger.warn(t.getMessage(), t);
			}
		} while (true);
	}
}
