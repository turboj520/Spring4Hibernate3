// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   DubboProtocol.java

package com.autohome.turbo.rpc.protocol.dubbo;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.Version;
import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.serialize.support.SerializableClassRegistry;
import com.autohome.turbo.common.serialize.support.SerializationOptimizer;
import com.autohome.turbo.common.utils.*;
import com.autohome.turbo.remoting.*;
import com.autohome.turbo.remoting.exchange.*;
import com.autohome.turbo.remoting.exchange.support.ExchangeHandlerAdapter;
import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.protocol.AbstractProtocol;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

// Referenced classes of package com.autohome.turbo.rpc.protocol.dubbo:
//			DubboExporter, DubboInvoker, ReferenceCountExchangeClient, LazyConnectExchangeClient

public class DubboProtocol extends AbstractProtocol
{

	public static final String NAME = "dubbo";
	public static final String COMPATIBLE_CODEC_NAME = "dubbo1compatible";
	public static final int DEFAULT_PORT = 20880;
	public final ReentrantLock lock = new ReentrantLock();
	private final Map serverMap = new ConcurrentHashMap();
	private final Map referenceClientMap = new ConcurrentHashMap();
	private final ConcurrentMap ghostClientMap = new ConcurrentHashMap();
	private final Set optimizers = new ConcurrentHashSet();
	private final ConcurrentMap stubServiceMethodsMap = new ConcurrentHashMap();
	private static final String IS_CALLBACK_SERVICE_INVOKE = "_isCallBackServiceInvoke";
	private ExchangeHandler requestHandler;
	private static DubboProtocol INSTANCE;

	public DubboProtocol()
	{
		requestHandler = new ExchangeHandlerAdapter() {

			final DubboProtocol this$0;

			public Object reply(ExchangeChannel channel, Object message)
				throws RemotingException
			{
				if (message instanceof Invocation)
				{
					Invocation inv = (Invocation)message;
					Invoker invoker = getInvoker(channel, inv);
					if (Boolean.TRUE.toString().equals(inv.getAttachments().get("_isCallBackServiceInvoke")))
					{
						String methodsStr = (String)invoker.getUrl().getParameters().get("methods");
						boolean hasMethod = false;
						if (methodsStr == null || methodsStr.indexOf(",") == -1)
						{
							hasMethod = inv.getMethodName().equals(methodsStr);
						} else
						{
							String methods[] = methodsStr.split(",");
							String arr$[] = methods;
							int len$ = arr$.length;
							int i$ = 0;
							do
							{
								if (i$ >= len$)
									break;
								String method = arr$[i$];
								if (inv.getMethodName().equals(method))
								{
									hasMethod = true;
									break;
								}
								i$++;
							} while (true);
						}
						if (!hasMethod)
						{
							DubboProtocol.this.this$0.warn((new StringBuilder()).append(new IllegalStateException((new StringBuilder()).append("The methodName ").append(inv.getMethodName()).append(" not found in callback service interface ,invoke will be ignored. please update the api interface. url is:").append(invoker.getUrl()).toString())).append(" ,invocation is :").append(inv).toString());
							return null;
						}
					}
					RpcContext.getContext().setRemoteAddress(channel.getRemoteAddress());
					return invoker.invoke(inv);
				} else
				{
					throw new RemotingException(channel, (new StringBuilder()).append("Unsupported request: ").append(message).toString() != null ? (new StringBuilder()).append(message.getClass().getName()).append(": ").append(message).append(", channel: consumer: ").append(channel.getRemoteAddress()).append(" --> provider: ").append(channel.getLocalAddress()).toString() : null);
				}
			}

			public void received(Channel channel, Object message)
				throws RemotingException
			{
				if (message instanceof Invocation)
					reply((ExchangeChannel)channel, message);
				else
					super.received(channel, message);
			}

			public void connected(Channel channel)
				throws RemotingException
			{
				invoke(channel, "onconnect");
			}

			public void disconnected(Channel channel)
				throws RemotingException
			{
				if (DubboProtocol.this.this$0.isInfoEnabled())
					DubboProtocol.this.this$0.info((new StringBuilder()).append("disconected from ").append(channel.getRemoteAddress()).append(",url:").append(channel.getUrl()).toString());
				invoke(channel, "ondisconnect");
			}

			private void invoke(Channel channel, String methodKey)
			{
				Invocation invocation = createInvocation(channel, channel.getUrl(), methodKey);
				if (invocation != null)
					try
					{
						received(channel, invocation);
					}
					catch (Throwable t)
					{
						DubboProtocol.this.this$0.warn((new StringBuilder()).append("Failed to invoke event method ").append(invocation.getMethodName()).append("(), cause: ").append(t.getMessage()).toString(), t);
					}
			}

			private Invocation createInvocation(Channel channel, URL url, String methodKey)
			{
				String method = url.getParameter(methodKey);
				if (method == null || method.length() == 0)
					return null;
				RpcInvocation invocation = new RpcInvocation(method, new Class[0], new Object[0]);
				invocation.setAttachment("path", url.getPath());
				invocation.setAttachment("group", url.getParameter("group"));
				invocation.setAttachment("interface", url.getParameter("interface"));
				invocation.setAttachment("version", url.getParameter("version"));
				if (url.getParameter("dubbo.stub.event", false))
					invocation.setAttachment("dubbo.stub.event", Boolean.TRUE.toString());
				return invocation;
			}

			
			{
				this$0 = DubboProtocol.this;
				super();
			}
		};
		INSTANCE = this;
	}

	public static DubboProtocol getDubboProtocol()
	{
		if (INSTANCE == null)
			ExtensionLoader.getExtensionLoader(com/autohome/turbo/rpc/Protocol).getExtension("dubbo");
		return INSTANCE;
	}

	public Collection getServers()
	{
		return Collections.unmodifiableCollection(serverMap.values());
	}

	public Collection getExporters()
	{
		return Collections.unmodifiableCollection(exporterMap.values());
	}

	Map getExporterMap()
	{
		return exporterMap;
	}

	private boolean isClientSide(Channel channel)
	{
		InetSocketAddress address = channel.getRemoteAddress();
		URL url = channel.getUrl();
		return url.getPort() == address.getPort() && NetUtils.filterLocalHost(channel.getUrl().getIp()).equals(NetUtils.filterLocalHost(address.getAddress().getHostAddress()));
	}

	Invoker getInvoker(Channel channel, Invocation inv)
		throws RemotingException
	{
		boolean isCallBackServiceInvoke = false;
		boolean isStubServiceInvoke = false;
		int port = channel.getLocalAddress().getPort();
		String path = (String)inv.getAttachments().get("path");
		isStubServiceInvoke = Boolean.TRUE.toString().equals(inv.getAttachments().get("dubbo.stub.event"));
		if (isStubServiceInvoke)
			port = channel.getRemoteAddress().getPort();
		isCallBackServiceInvoke = isClientSide(channel) && !isStubServiceInvoke;
		if (isCallBackServiceInvoke)
		{
			path = (new StringBuilder()).append((String)inv.getAttachments().get("path")).append(".").append((String)inv.getAttachments().get("callback.service.instid")).toString();
			inv.getAttachments().put("_isCallBackServiceInvoke", Boolean.TRUE.toString());
		}
		String serviceKey = serviceKey(port, path, (String)inv.getAttachments().get("version"), (String)inv.getAttachments().get("group"));
		DubboExporter exporter = (DubboExporter)exporterMap.get(serviceKey);
		if (exporter == null)
			throw new RemotingException(channel, (new StringBuilder()).append("Not found exported service: ").append(serviceKey).append(" in ").append(exporterMap.keySet()).append(", may be version or group mismatch ").append(", channel: consumer: ").append(channel.getRemoteAddress()).append(" --> provider: ").append(channel.getLocalAddress()).append(", message:").append(inv).toString());
		else
			return exporter.getInvoker();
	}

	public Collection getInvokers()
	{
		return Collections.unmodifiableCollection(invokers);
	}

	public int getDefaultPort()
	{
		return 20880;
	}

	public Exporter export(Invoker invoker)
		throws RpcException
	{
		URL url = invoker.getUrl();
		String key = serviceKey(url);
		DubboExporter exporter = new DubboExporter(invoker, key, exporterMap);
		exporterMap.put(key, exporter);
		Boolean isStubSupportEvent = Boolean.valueOf(url.getParameter("dubbo.stub.event", false));
		Boolean isCallbackservice = Boolean.valueOf(url.getParameter("is_callback_service", false));
		if (isStubSupportEvent.booleanValue() && !isCallbackservice.booleanValue())
		{
			String stubServiceMethods = url.getParameter("dubbo.stub.event.methods");
			if (stubServiceMethods == null || stubServiceMethods.length() == 0)
			{
				if (logger.isWarnEnabled())
					logger.warn(new IllegalStateException((new StringBuilder()).append("consumer [").append(url.getParameter("interface")).append("], has set stubproxy support event ,but no stub methods founded.").toString()));
			} else
			{
				stubServiceMethodsMap.put(url.getServiceKey(), stubServiceMethods);
			}
		}
		openServer(url);
		optimizeSerialization(url);
		return exporter;
	}

	private void optimizeSerialization(URL url)
		throws RpcException
	{
		String className = url.getParameter("optimizer", "");
		if (StringUtils.isEmpty(className) || optimizers.contains(className))
			return;
		logger.info("Optimizing the serialization process for Kryo, FST, etc...");
		SerializationOptimizer optimizer;
		Iterator i$;
		Class c;
		try
		{
			Class clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
			if (!com/autohome/turbo/common/serialize/support/SerializationOptimizer.isAssignableFrom(clazz))
				throw new RpcException((new StringBuilder()).append("The serialization optimizer ").append(className).append(" isn't an instance of ").append(com/autohome/turbo/common/serialize/support/SerializationOptimizer.getName()).toString());
			optimizer = (SerializationOptimizer)clazz.newInstance();
			if (optimizer.getSerializableClasses() == null)
				return;
		}
		catch (ClassNotFoundException e)
		{
			throw new RpcException((new StringBuilder()).append("Cannot find the serialization optimizer class: ").append(className).toString(), e);
		}
		catch (InstantiationException e)
		{
			throw new RpcException((new StringBuilder()).append("Cannot instantiate the serialization optimizer class: ").append(className).toString(), e);
		}
		catch (IllegalAccessException e)
		{
			throw new RpcException((new StringBuilder()).append("Cannot instantiate the serialization optimizer class: ").append(className).toString(), e);
		}
		for (i$ = optimizer.getSerializableClasses().iterator(); i$.hasNext(); SerializableClassRegistry.registerClass(c))
			c = (Class)i$.next();

		optimizers.add(className);
	}

	private void openServer(URL url)
	{
		String key = url.getAddress();
		boolean isServer = url.getParameter("isserver", true);
		if (isServer)
		{
			ExchangeServer server = (ExchangeServer)serverMap.get(key);
			if (server == null)
				serverMap.put(key, createServer(url));
			else
				server.reset(url);
		}
	}

	private ExchangeServer createServer(URL url)
	{
		url = url.addParameterIfAbsent("channel.readonly.sent", Boolean.TRUE.toString());
		url = url.addParameterIfAbsent("heartbeat", String.valueOf(60000));
		String str = url.getParameter("server", "netty");
		if (str != null && str.length() > 0 && !ExtensionLoader.getExtensionLoader(com/autohome/turbo/remoting/Transporter).hasExtension(str))
			throw new RpcException((new StringBuilder()).append("Unsupported server type: ").append(str).append(", url: ").append(url).toString());
		url = url.addParameter("codec", Version.isCompatibleVersion() ? "dubbo1compatible" : "dubbo");
		ExchangeServer server;
		try
		{
			server = Exchangers.bind(url, requestHandler);
		}
		catch (RemotingException e)
		{
			throw new RpcException((new StringBuilder()).append("Fail to start server(url: ").append(url).append(") ").append(e.getMessage()).toString(), e);
		}
		str = url.getParameter("client");
		if (str != null && str.length() > 0)
		{
			Set supportedTypes = ExtensionLoader.getExtensionLoader(com/autohome/turbo/remoting/Transporter).getSupportedExtensions();
			if (!supportedTypes.contains(str))
				throw new RpcException((new StringBuilder()).append("Unsupported client type: ").append(str).toString());
		}
		return server;
	}

	public Invoker refer(Class serviceType, URL url)
		throws RpcException
	{
		optimizeSerialization(url);
		DubboInvoker invoker = new DubboInvoker(serviceType, url, getClients(url), invokers);
		invokers.add(invoker);
		return invoker;
	}

	private ExchangeClient[] getClients(URL url)
	{
		boolean service_share_connect = false;
		int connections = url.getParameter("connections", 0);
		if (connections == 0)
		{
			service_share_connect = true;
			connections = 1;
		}
		ExchangeClient clients[] = new ExchangeClient[connections];
		for (int i = 0; i < clients.length; i++)
			if (service_share_connect)
				clients[i] = getSharedClient(url);
			else
				clients[i] = initClient(url);

		return clients;
	}

	private ExchangeClient getSharedClient(URL url)
	{
		String key = url.getAddress();
		ReferenceCountExchangeClient client = (ReferenceCountExchangeClient)referenceClientMap.get(key);
		if (client != null)
		{
			if (!client.isClosed())
			{
				client.incrementAndGetCount();
				return client;
			}
			referenceClientMap.remove(key);
		}
		ExchangeClient exchagneclient = initClient(url);
		client = new ReferenceCountExchangeClient(exchagneclient, ghostClientMap);
		referenceClientMap.put(key, client);
		ghostClientMap.remove(key);
		return client;
	}

	private ExchangeClient initClient(URL url)
	{
		String str = url.getParameter("client", url.getParameter("server", "netty"));
		String version = url.getParameter("dubbo");
		boolean compatible = version != null && version.startsWith("1.0.");
		url = url.addParameter("codec", !Version.isCompatibleVersion() || !compatible ? "dubbo" : "dubbo1compatible");
		url = url.addParameterIfAbsent("heartbeat", String.valueOf(60000));
		if (str != null && str.length() > 0 && !ExtensionLoader.getExtensionLoader(com/autohome/turbo/remoting/Transporter).hasExtension(str))
			throw new RpcException((new StringBuilder()).append("Unsupported client type: ").append(str).append(",").append(" supported client type is ").append(StringUtils.join(ExtensionLoader.getExtensionLoader(com/autohome/turbo/remoting/Transporter).getSupportedExtensions(), " ")).toString());
		ExchangeClient client;
		try
		{
			if (url.getParameter("lazy", false))
				client = new LazyConnectExchangeClient(url, requestHandler);
			else
				client = Exchangers.connect(url, requestHandler);
		}
		catch (RemotingException e)
		{
			throw new RpcException((new StringBuilder()).append("Fail to create remoting client for service(").append(url).append("): ").append(e.getMessage()).toString(), e);
		}
		return client;
	}

	public void destroy()
	{
		Iterator i$ = (new ArrayList(serverMap.keySet())).iterator();
		do
		{
			if (!i$.hasNext())
				break;
			String key = (String)i$.next();
			ExchangeServer server = (ExchangeServer)serverMap.remove(key);
			if (server != null)
				try
				{
					if (logger.isInfoEnabled())
						logger.info((new StringBuilder()).append("Close dubbo server: ").append(server.getLocalAddress()).toString());
					server.close(getServerShutdownTimeout());
				}
				catch (Throwable t)
				{
					logger.warn(t.getMessage(), t);
				}
		} while (true);
		i$ = (new ArrayList(referenceClientMap.keySet())).iterator();
		do
		{
			if (!i$.hasNext())
				break;
			String key = (String)i$.next();
			ExchangeClient client = (ExchangeClient)referenceClientMap.remove(key);
			if (client != null)
				try
				{
					if (logger.isInfoEnabled())
						logger.info((new StringBuilder()).append("Close dubbo connect: ").append(client.getLocalAddress()).append("-->").append(client.getRemoteAddress()).toString());
					client.close();
				}
				catch (Throwable t)
				{
					logger.warn(t.getMessage(), t);
				}
		} while (true);
		i$ = (new ArrayList(ghostClientMap.keySet())).iterator();
		do
		{
			if (!i$.hasNext())
				break;
			String key = (String)i$.next();
			ExchangeClient client = (ExchangeClient)ghostClientMap.remove(key);
			if (client != null)
				try
				{
					if (logger.isInfoEnabled())
						logger.info((new StringBuilder()).append("Close dubbo connect: ").append(client.getLocalAddress()).append("-->").append(client.getRemoteAddress()).toString());
					client.close();
				}
				catch (Throwable t)
				{
					logger.warn(t.getMessage(), t);
				}
		} while (true);
		stubServiceMethodsMap.clear();
		super.destroy();
	}




}
