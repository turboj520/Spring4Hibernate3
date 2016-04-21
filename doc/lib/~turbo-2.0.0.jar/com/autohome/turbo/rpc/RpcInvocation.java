// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   RpcInvocation.java

package com.autohome.turbo.rpc;

import com.autohome.turbo.common.URL;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;

// Referenced classes of package com.autohome.turbo.rpc:
//			Invocation, Invoker

public class RpcInvocation
	implements Invocation, Serializable
{

	private static final long serialVersionUID = 0xc38eeb4a42ba02abL;
	private String methodName;
	private Class parameterTypes[];
	private Object arguments[];
	private Map attachments;
	private transient Invoker invoker;

	public RpcInvocation()
	{
	}

	public RpcInvocation(Invocation invocation, Invoker invoker)
	{
		this(invocation.getMethodName(), invocation.getParameterTypes(), invocation.getArguments(), ((Map) (new HashMap(invocation.getAttachments()))), invocation.getInvoker());
		if (invoker != null)
		{
			URL url = invoker.getUrl();
			setAttachment("path", url.getPath());
			if (url.hasParameter("interface"))
				setAttachment("interface", url.getParameter("interface"));
			if (url.hasParameter("group"))
				setAttachment("group", url.getParameter("group"));
			if (url.hasParameter("version"))
				setAttachment("version", url.getParameter("version", "0.0.0"));
			if (url.hasParameter("timeout"))
				setAttachment("timeout", url.getParameter("timeout"));
			if (url.hasParameter("token"))
				setAttachment("token", url.getParameter("token"));
			if (url.hasParameter("application"))
				setAttachment("application", url.getParameter("application"));
		}
	}

	public RpcInvocation(Invocation invocation)
	{
		this(invocation.getMethodName(), invocation.getParameterTypes(), invocation.getArguments(), invocation.getAttachments(), invocation.getInvoker());
	}

	public RpcInvocation(Method method, Object arguments[])
	{
		this(method.getName(), method.getParameterTypes(), arguments, null, null);
	}

	public RpcInvocation(Method method, Object arguments[], Map attachment)
	{
		this(method.getName(), method.getParameterTypes(), arguments, attachment, null);
	}

	public RpcInvocation(String methodName, Class parameterTypes[], Object arguments[])
	{
		this(methodName, parameterTypes, arguments, null, null);
	}

	public RpcInvocation(String methodName, Class parameterTypes[], Object arguments[], Map attachments)
	{
		this(methodName, parameterTypes, arguments, attachments, null);
	}

	public RpcInvocation(String methodName, Class parameterTypes[], Object arguments[], Map attachments, Invoker invoker)
	{
		this.methodName = methodName;
		this.parameterTypes = parameterTypes != null ? parameterTypes : new Class[0];
		this.arguments = arguments != null ? arguments : new Object[0];
		this.attachments = ((Map) (attachments != null ? attachments : ((Map) (new HashMap()))));
		this.invoker = invoker;
	}

	public Invoker getInvoker()
	{
		return invoker;
	}

	public void setInvoker(Invoker invoker)
	{
		this.invoker = invoker;
	}

	public String getMethodName()
	{
		return methodName;
	}

	public Class[] getParameterTypes()
	{
		return parameterTypes;
	}

	public Object[] getArguments()
	{
		return arguments;
	}

	public Map getAttachments()
	{
		return attachments;
	}

	public void setMethodName(String methodName)
	{
		this.methodName = methodName;
	}

	public void setParameterTypes(Class parameterTypes[])
	{
		this.parameterTypes = parameterTypes != null ? parameterTypes : new Class[0];
	}

	public void setArguments(Object arguments[])
	{
		this.arguments = arguments != null ? arguments : new Object[0];
	}

	public void setAttachments(Map attachments)
	{
		this.attachments = ((Map) (attachments != null ? attachments : ((Map) (new HashMap()))));
	}

	public void setAttachment(String key, String value)
	{
		if (attachments == null)
			attachments = new HashMap();
		attachments.put(key, value);
	}

	public void setAttachmentIfAbsent(String key, String value)
	{
		if (attachments == null)
			attachments = new HashMap();
		if (!attachments.containsKey(key))
			attachments.put(key, value);
	}

	public void addAttachments(Map attachments)
	{
		if (attachments == null)
			return;
		if (this.attachments == null)
			this.attachments = new HashMap();
		this.attachments.putAll(attachments);
	}

	public void addAttachmentsIfAbsent(Map attachments)
	{
		if (attachments == null)
			return;
		java.util.Map.Entry entry;
		for (Iterator i$ = attachments.entrySet().iterator(); i$.hasNext(); setAttachmentIfAbsent((String)entry.getKey(), (String)entry.getValue()))
			entry = (java.util.Map.Entry)i$.next();

	}

	public String getAttachment(String key)
	{
		if (attachments == null)
			return null;
		else
			return (String)attachments.get(key);
	}

	public String getAttachment(String key, String defaultValue)
	{
		if (attachments == null)
			return defaultValue;
		String value = (String)attachments.get(key);
		if (value == null || value.length() == 0)
			return defaultValue;
		else
			return value;
	}

	public String toString()
	{
		return (new StringBuilder()).append("RpcInvocation [methodName=").append(methodName).append(", parameterTypes=").append(Arrays.toString(parameterTypes)).append(", arguments=").append(Arrays.toString(arguments)).append(", attachments=").append(attachments).append("]").toString();
	}
}
