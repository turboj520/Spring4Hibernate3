// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HessianSkeleton.java

package com.autohome.hessian.server;

import com.autohome.hessian.io.*;
import com.autohome.services.server.AbstractSkeleton;
import com.autohome.services.server.ServiceContext;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HessianSkeleton extends AbstractSkeleton
{
	static class LogWriter extends Writer
	{

		private Logger _log;
		private StringBuilder _sb;

		public void write(char ch)
		{
			if (ch == '\n' && _sb.length() > 0)
			{
				_log.fine(_sb.toString());
				_sb.setLength(0);
			} else
			{
				_sb.append(ch);
			}
		}

		public void write(char buffer[], int offset, int length)
		{
			for (int i = 0; i < length; i++)
			{
				char ch = buffer[offset + i];
				if (ch == '\n' && _sb.length() > 0)
				{
					_log.fine(_sb.toString());
					_sb.setLength(0);
				} else
				{
					_sb.append(ch);
				}
			}

		}

		public void flush()
		{
		}

		public void close()
		{
		}

		LogWriter(Logger log)
		{
			_sb = new StringBuilder();
			_log = log;
		}
	}


	private static final Logger log = Logger.getLogger(com/autohome/hessian/server/HessianSkeleton.getName());
	private boolean _isDebug;
	private HessianInputFactory _inputFactory;
	private HessianFactory _hessianFactory;
	private Object _service;

	public HessianSkeleton(Object service, Class apiClass)
	{
		super(apiClass);
		_inputFactory = new HessianInputFactory();
		_hessianFactory = new HessianFactory();
		if (service == null)
			service = this;
		_service = service;
		if (!apiClass.isAssignableFrom(service.getClass()))
			throw new IllegalArgumentException((new StringBuilder()).append("Service ").append(service).append(" must be an instance of ").append(apiClass.getName()).toString());
		else
			return;
	}

	public HessianSkeleton(Class apiClass)
	{
		super(apiClass);
		_inputFactory = new HessianInputFactory();
		_hessianFactory = new HessianFactory();
	}

	public void setDebug(boolean isDebug)
	{
		_isDebug = isDebug;
	}

	public boolean isDebug()
	{
		return _isDebug;
	}

	public void setHessianFactory(HessianFactory factory)
	{
		_hessianFactory = factory;
	}

	public void invoke(InputStream is, OutputStream os)
		throws Exception
	{
		invoke(is, os, ((SerializerFactory) (null)));
	}

	public void invoke(InputStream is, OutputStream os, SerializerFactory serializerFactory)
		throws Exception
	{
		boolean isDebug;
		AbstractHessianInput in;
		AbstractHessianOutput out;
		isDebug = false;
		if (isDebugInvoke())
		{
			isDebug = true;
			PrintWriter dbg = createDebugPrintWriter();
			HessianDebugInputStream dIs = new HessianDebugInputStream(is, dbg);
			dIs.startTop2();
			is = dIs;
			HessianDebugOutputStream dOs = new HessianDebugOutputStream(os, dbg);
			dOs.startTop2();
			os = dOs;
		}
		com.autohome.hessian.io.HessianInputFactory.HeaderType header = _inputFactory.readHeader(is);
		static class 1
		{

			static final int $SwitchMap$com$autohome$hessian$io$HessianInputFactory$HeaderType[];

			static 
			{
				$SwitchMap$com$autohome$hessian$io$HessianInputFactory$HeaderType = new int[com.autohome.hessian.io.HessianInputFactory.HeaderType.values().length];
				try
				{
					$SwitchMap$com$autohome$hessian$io$HessianInputFactory$HeaderType[com.autohome.hessian.io.HessianInputFactory.HeaderType.CALL_1_REPLY_1.ordinal()] = 1;
				}
				catch (NoSuchFieldError ex) { }
				try
				{
					$SwitchMap$com$autohome$hessian$io$HessianInputFactory$HeaderType[com.autohome.hessian.io.HessianInputFactory.HeaderType.CALL_1_REPLY_2.ordinal()] = 2;
				}
				catch (NoSuchFieldError ex) { }
				try
				{
					$SwitchMap$com$autohome$hessian$io$HessianInputFactory$HeaderType[com.autohome.hessian.io.HessianInputFactory.HeaderType.HESSIAN_2.ordinal()] = 3;
				}
				catch (NoSuchFieldError ex) { }
			}
		}

		switch (1..SwitchMap.com.autohome.hessian.io.HessianInputFactory.HeaderType[header.ordinal()])
		{
		case 1: // '\001'
			in = _hessianFactory.createHessianInput(is);
			out = _hessianFactory.createHessianOutput(os);
			break;

		case 2: // '\002'
			in = _hessianFactory.createHessianInput(is);
			out = _hessianFactory.createHessian2Output(os);
			break;

		case 3: // '\003'
			in = _hessianFactory.createHessian2Input(is);
			in.readCall();
			out = _hessianFactory.createHessian2Output(os);
			break;

		default:
			throw new IllegalStateException((new StringBuilder()).append(header).append(" is an unknown Hessian call").toString());
		}
		if (serializerFactory != null)
		{
			in.setSerializerFactory(serializerFactory);
			out.setSerializerFactory(serializerFactory);
		}
		invoke(_service, in, out);
		in.close();
		out.close();
		if (isDebug)
			os.close();
		break MISSING_BLOCK_LABEL_281;
		Exception exception;
		exception;
		in.close();
		out.close();
		if (isDebug)
			os.close();
		throw exception;
	}

	public void invoke(AbstractHessianInput in, AbstractHessianOutput out)
		throws Exception
	{
		invoke(_service, in, out);
	}

	public void invoke(Object service, AbstractHessianInput in, AbstractHessianOutput out)
		throws Exception
	{
		ServiceContext context = ServiceContext.getContext();
		in.skipOptionalCall();
		String header;
		while ((header = in.readHeader()) != null) 
		{
			Object value = in.readObject();
			context.addHeader(header, value);
		}
		String methodName = in.readMethod();
		int argLength = in.readMethodArgLength();
		Method method = getMethod((new StringBuilder()).append(methodName).append("__").append(argLength).toString());
		if (method == null)
			method = getMethod(methodName);
		if (method == null)
		{
			if ("_hessian_getAttribute".equals(methodName))
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
				out.writeReply(value);
				out.close();
				return;
			}
			if (method == null)
			{
				out.writeFault("NoSuchMethodException", (new StringBuilder()).append("The service has no method named: ").append(in.getMethod()).toString(), null);
				out.close();
				return;
			}
		}
		Class args[] = method.getParameterTypes();
		if (argLength != args.length && argLength >= 0)
		{
			out.writeFault("NoSuchMethod", (new StringBuilder()).append("method ").append(method).append(" argument length mismatch, received length=").append(argLength).toString(), null);
			out.close();
			return;
		}
		Object values[] = new Object[args.length];
		for (int i = 0; i < args.length; i++)
			values[i] = in.readObject(args[i]);

		Object result = null;
		try
		{
			result = method.invoke(service, values);
		}
		catch (Exception e)
		{
			Throwable e1 = e;
			if (e1 instanceof InvocationTargetException)
				e1 = ((InvocationTargetException)e).getTargetException();
			log.log(Level.FINE, (new StringBuilder()).append(this).append(" ").append(e1.toString()).toString(), e1);
			out.writeFault("ServiceException", e1.getMessage(), e1);
			out.close();
			return;
		}
		in.completeCall();
		out.writeReply(result);
		out.close();
	}

	protected boolean isDebugInvoke()
	{
		return log.isLoggable(Level.FINEST) || isDebug() && log.isLoggable(Level.FINE);
	}

	protected PrintWriter createDebugPrintWriter()
		throws IOException
	{
		return new PrintWriter(new LogWriter(log));
	}

}
