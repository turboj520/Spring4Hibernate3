// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   RpcContext.java

package com.autohome.turbo.rpc;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.utils.NetUtils;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.*;

// Referenced classes of package com.autohome.turbo.rpc:
//			Invoker, RpcException, Invocation

public class RpcContext
{

	private static final ThreadLocal LOCAL = new ThreadLocal() {

		protected RpcContext initialValue()
		{
			return new RpcContext();
		}

		protected volatile Object initialValue()
		{
			return initialValue();
		}

	};
	private Future future;
	private List urls;
	private URL url;
	private String methodName;
	private Class parameterTypes[];
	private Object arguments[];
	private InetSocketAddress localAddress;
	private InetSocketAddress remoteAddress;
	private final Map attachments = new HashMap();
	private final Map values = new HashMap();
	private Object request;
	private Object response;
	/**
	 * @deprecated Field invokers is deprecated
	 */
	private List invokers;
	/**
	 * @deprecated Field invoker is deprecated
	 */
	private Invoker invoker;
	/**
	 * @deprecated Field invocation is deprecated
	 */
	private Invocation invocation;

	public static RpcContext getContext()
	{
		return (RpcContext)LOCAL.get();
	}

	public static void removeContext()
	{
		LOCAL.remove();
	}

	protected RpcContext()
	{
	}

	public Object getRequest()
	{
		return request;
	}

	public Object getRequest(Class clazz)
	{
		return request == null || !clazz.isAssignableFrom(request.getClass()) ? null : request;
	}

	public void setRequest(Object request)
	{
		this.request = request;
	}

	public Object getResponse()
	{
		return response;
	}

	public Object getResponse(Class clazz)
	{
		return response == null || !clazz.isAssignableFrom(response.getClass()) ? null : response;
	}

	public void setResponse(Object response)
	{
		this.response = response;
	}

	public boolean isProviderSide()
	{
		URL url = getUrl();
		if (url == null)
			return false;
		InetSocketAddress address = getRemoteAddress();
		if (address == null)
			return false;
		String host;
		if (address.getAddress() == null)
			host = address.getHostName();
		else
			host = address.getAddress().getHostAddress();
		return url.getPort() != address.getPort() || !NetUtils.filterLocalHost(url.getIp()).equals(NetUtils.filterLocalHost(host));
	}

	public boolean isConsumerSide()
	{
		URL url = getUrl();
		if (url == null)
			return false;
		InetSocketAddress address = getRemoteAddress();
		if (address == null)
			return false;
		String host;
		if (address.getAddress() == null)
			host = address.getHostName();
		else
			host = address.getAddress().getHostAddress();
		return url.getPort() == address.getPort() && NetUtils.filterLocalHost(url.getIp()).equals(NetUtils.filterLocalHost(host));
	}

	public Future getFuture()
	{
		return future;
	}

	public void setFuture(Future future)
	{
		this.future = future;
	}

	public List getUrls()
	{
		return urls != null || url == null ? urls : Arrays.asList(new URL[] {
			url
		});
	}

	public void setUrls(List urls)
	{
		this.urls = urls;
	}

	public URL getUrl()
	{
		return url;
	}

	public void setUrl(URL url)
	{
		this.url = url;
	}

	public String getMethodName()
	{
		return methodName;
	}

	public void setMethodName(String methodName)
	{
		this.methodName = methodName;
	}

	public Class[] getParameterTypes()
	{
		return parameterTypes;
	}

	public void setParameterTypes(Class parameterTypes[])
	{
		this.parameterTypes = parameterTypes;
	}

	public Object[] getArguments()
	{
		return arguments;
	}

	public void setArguments(Object arguments[])
	{
		this.arguments = arguments;
	}

	public RpcContext setLocalAddress(InetSocketAddress address)
	{
		localAddress = address;
		return this;
	}

	public RpcContext setLocalAddress(String host, int port)
	{
		if (port < 0)
			port = 0;
		localAddress = InetSocketAddress.createUnresolved(host, port);
		return this;
	}

	public InetSocketAddress getLocalAddress()
	{
		return localAddress;
	}

	public String getLocalAddressString()
	{
		return (new StringBuilder()).append(getLocalHost()).append(":").append(getLocalPort()).toString();
	}

	public String getLocalHostName()
	{
		String host = localAddress != null ? localAddress.getHostName() : null;
		if (host == null || host.length() == 0)
			return getLocalHost();
		else
			return host;
	}

	public RpcContext setRemoteAddress(InetSocketAddress address)
	{
		remoteAddress = address;
		return this;
	}

	public RpcContext setRemoteAddress(String host, int port)
	{
		if (port < 0)
			port = 0;
		remoteAddress = InetSocketAddress.createUnresolved(host, port);
		return this;
	}

	public InetSocketAddress getRemoteAddress()
	{
		return remoteAddress;
	}

	public String getRemoteAddressString()
	{
		return (new StringBuilder()).append(getRemoteHost()).append(":").append(getRemotePort()).toString();
	}

	public String getRemoteHostName()
	{
		return remoteAddress != null ? remoteAddress.getHostName() : null;
	}

	public String getLocalHost()
	{
		String host = localAddress != null ? localAddress.getAddress() != null ? NetUtils.filterLocalHost(localAddress.getAddress().getHostAddress()) : localAddress.getHostName() : null;
		if (host == null || host.length() == 0)
			return NetUtils.getLocalHost();
		else
			return host;
	}

	public int getLocalPort()
	{
		return localAddress != null ? localAddress.getPort() : 0;
	}

	public String getRemoteHost()
	{
		return remoteAddress != null ? remoteAddress.getAddress() != null ? NetUtils.filterLocalHost(remoteAddress.getAddress().getHostAddress()) : remoteAddress.getHostName() : null;
	}

	public int getRemotePort()
	{
		return remoteAddress != null ? remoteAddress.getPort() : 0;
	}

	public String getAttachment(String key)
	{
		return (String)attachments.get(key);
	}

	public RpcContext setAttachment(String key, String value)
	{
		if (value == null)
			attachments.remove(key);
		else
			attachments.put(key, value);
		return this;
	}

	public RpcContext removeAttachment(String key)
	{
		attachments.remove(key);
		return this;
	}

	public Map getAttachments()
	{
		return attachments;
	}

	public RpcContext setAttachments(Map attachment)
	{
		attachments.clear();
		if (attachment != null && attachment.size() > 0)
			attachments.putAll(attachment);
		return this;
	}

	public void clearAttachments()
	{
		attachments.clear();
	}

	public Map get()
	{
		return values;
	}

	public RpcContext set(String key, Object value)
	{
		if (value == null)
			values.remove(key);
		else
			values.put(key, value);
		return this;
	}

	public RpcContext remove(String key)
	{
		values.remove(key);
		return this;
	}

	public Object get(String key)
	{
		return values.get(key);
	}

	public RpcContext setInvokers(List invokers)
	{
		this.invokers = invokers;
		if (invokers != null && invokers.size() > 0)
		{
			List urls = new ArrayList(invokers.size());
			Invoker invoker;
			for (Iterator i$ = invokers.iterator(); i$.hasNext(); urls.add(invoker.getUrl()))
				invoker = (Invoker)i$.next();

			setUrls(urls);
		}
		return this;
	}

	public RpcContext setInvoker(Invoker invoker)
	{
		this.invoker = invoker;
		if (invoker != null)
			setUrl(invoker.getUrl());
		return this;
	}

	public RpcContext setInvocation(Invocation invocation)
	{
		this.invocation = invocation;
		if (invocation != null)
		{
			setMethodName(invocation.getMethodName());
			setParameterTypes(invocation.getParameterTypes());
			setArguments(invocation.getArguments());
		}
		return this;
	}

	/**
	 * @deprecated Method isServerSide is deprecated
	 */

	public boolean isServerSide()
	{
		return isProviderSide();
	}

	/**
	 * @deprecated Method isClientSide is deprecated
	 */

	public boolean isClientSide()
	{
		return isConsumerSide();
	}

	/**
	 * @deprecated Method getInvokers is deprecated
	 */

	public List getInvokers()
	{
		return invokers != null || invoker == null ? invokers : Arrays.asList(new Invoker[] {
			invoker
		});
	}

	/**
	 * @deprecated Method getInvoker is deprecated
	 */

	public Invoker getInvoker()
	{
		return invoker;
	}

	/**
	 * @deprecated Method getInvocation is deprecated
	 */

	public Invocation getInvocation()
	{
		return invocation;
	}

	public Future asyncCall(Callable callable)
	{
		final RpcException e;
		FutureTask futuretask;
		try
		{
			setAttachment("async", Boolean.TRUE.toString());
			final Object o = callable.call();
			if (o == null)
				break MISSING_BLOCK_LABEL_58;
			FutureTask f = new FutureTask(new Callable() {

				final Object val$o;
				final RpcContext this$0;

				public Object call()
					throws Exception
				{
					return o;
				}

			
			{
				this$0 = RpcContext.this;
				o = obj;
				super();
			}
			});
			f.run();
			futuretask = f;
		}
		// Misplaced declaration of an exception variable
		catch (final RpcException e)
		{
			throw new RpcException(e);
		}
		finally
		{
			removeAttachment("async");
			throw exception;
		}
		removeAttachment("async");
		return futuretask;
		removeAttachment("async");
		break MISSING_BLOCK_LABEL_104;
		e;
		return new Future() {

			final RpcException val$e;
			final RpcContext this$0;

			public boolean cancel(boolean mayInterruptIfRunning)
			{
				return false;
			}

			public boolean isCancelled()
			{
				return false;
			}

			public boolean isDone()
			{
				return true;
			}

			public Object get()
				throws InterruptedException, ExecutionException
			{
				throw new ExecutionException(e.getCause());
			}

			public Object get(long timeout, TimeUnit unit)
				throws InterruptedException, ExecutionException, TimeoutException
			{
				return get();
			}

			
			{
				this$0 = RpcContext.this;
				e = rpcexception;
				super();
			}
		};
		return getContext().getFuture();
	}

	public void asyncCall(Runnable runable)
	{
		Exception exception;
		try
		{
			setAttachment("return", Boolean.FALSE.toString());
			runable.run();
		}
		catch (Throwable e)
		{
			throw new RpcException((new StringBuilder()).append("oneway call error .").append(e.getMessage()).toString(), e);
		}
		finally
		{
			removeAttachment("return");
		}
		removeAttachment("return");
		break MISSING_BLOCK_LABEL_71;
		throw exception;
	}

}
