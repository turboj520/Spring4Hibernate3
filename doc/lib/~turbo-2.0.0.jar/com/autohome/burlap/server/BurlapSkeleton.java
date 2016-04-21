// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   BurlapSkeleton.java

package com.autohome.burlap.server;

import com.autohome.burlap.io.BurlapInput;
import com.autohome.burlap.io.BurlapOutput;
import com.autohome.services.server.AbstractSkeleton;
import com.autohome.services.server.ServiceContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BurlapSkeleton extends AbstractSkeleton
{

	private static final Logger log = Logger.getLogger(com/autohome/burlap/server/BurlapSkeleton.getName());
	private Object _service;

	public BurlapSkeleton(Object service, Class apiClass)
	{
		super(apiClass);
		_service = service;
	}

	public BurlapSkeleton(Class apiClass)
	{
		super(apiClass);
	}

	public void invoke(BurlapInput in, BurlapOutput out)
		throws Exception
	{
		invoke(_service, in, out);
	}

	public void invoke(Object service, BurlapInput in, BurlapOutput out)
		throws Exception
	{
		in.readCall();
		ServiceContext context = ServiceContext.getContext();
		String header;
		while ((header = in.readHeader()) != null) 
		{
			Object value = in.readObject();
			context.addHeader(header, value);
		}
		String methodName = in.readMethod();
		Method method = getMethod(methodName);
		if (log.isLoggable(Level.FINE))
			log.fine((new StringBuilder()).append(this).append(" invoking ").append(methodName).append(" (").append(method).append(")").toString());
		if (method == null)
		{
			if ("_burlap_getAttribute".equals(in.getMethod()))
			{
				String attrName = in.readString();
				in.completeCall();
				String value = null;
				if ("java.api.class".equals(attrName))
					value = getAPIClassName();
				else
				if ("java.home.class".equals(attrName))
					value = getHomeClassName();
				else
				if ("java.object.class".equals(attrName))
					value = getObjectClassName();
				out.startReply();
				out.writeObject(value);
				out.completeReply();
				return;
			}
			if (method == null)
			{
				out.startReply();
				out.writeFault("NoSuchMethodException", (new StringBuilder()).append("The service has no method named: ").append(in.getMethod()).toString(), null);
				out.completeReply();
				return;
			}
		}
		Class args[] = method.getParameterTypes();
		Object values[] = new Object[args.length];
		for (int i = 0; i < args.length; i++)
			values[i] = in.readObject(args[i]);

		in.completeCall();
		Object result = null;
		try
		{
			result = method.invoke(service, values);
		}
		catch (Throwable e)
		{
			log.log(Level.FINE, (new StringBuilder()).append(service).append(".").append(method.getName()).append("() failed with exception:\n").append(e.toString()).toString(), e);
			if ((e instanceof InvocationTargetException) && (e.getCause() instanceof Exception))
				e = ((InvocationTargetException)e).getTargetException();
			out.startReply();
			out.writeFault("ServiceException", e.getMessage(), e);
			out.completeReply();
			return;
		}
		out.startReply();
		out.writeObject(result);
		out.completeReply();
	}

}
