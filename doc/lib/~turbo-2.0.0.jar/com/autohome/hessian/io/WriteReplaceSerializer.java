// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   WriteReplaceSerializer.java

package com.autohome.hessian.io;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

// Referenced classes of package com.autohome.hessian.io:
//			AbstractSerializer, AbstractHessianOutput, Serializer

public class WriteReplaceSerializer extends AbstractSerializer
{

	private static final Logger log = Logger.getLogger(com/autohome/hessian/io/WriteReplaceSerializer.getName());
	private static Object NULL_ARGS[] = new Object[0];
	private Object _writeReplaceFactory;
	private Method _writeReplace;
	private Serializer _baseSerializer;

	public WriteReplaceSerializer(Class cl, ClassLoader loader, Serializer baseSerializer)
	{
		introspectWriteReplace(cl, loader);
		_baseSerializer = baseSerializer;
	}

	private void introspectWriteReplace(Class cl, ClassLoader loader)
	{
		try
		{
			String className = (new StringBuilder()).append(cl.getName()).append("HessianSerializer").toString();
			Class serializerClass = Class.forName(className, false, loader);
			Object serializerObject = serializerClass.newInstance();
			Method writeReplace = getWriteReplace(serializerClass, cl);
			if (writeReplace != null)
			{
				_writeReplaceFactory = serializerObject;
				_writeReplace = writeReplace;
			}
		}
		catch (ClassNotFoundException e) { }
		catch (Exception e)
		{
			log.log(Level.FINER, e.toString(), e);
		}
		_writeReplace = getWriteReplace(cl);
		if (_writeReplace != null)
			_writeReplace.setAccessible(true);
	}

	protected static Method getWriteReplace(Class cl, Class param)
	{
		for (; cl != null; cl = cl.getSuperclass())
		{
			Method arr$[] = cl.getDeclaredMethods();
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				Method method = arr$[i$];
				if (method.getName().equals("writeReplace") && method.getParameterTypes().length == 1 && param.equals(method.getParameterTypes()[0]))
					return method;
			}

		}

		return null;
	}

	protected static Method getWriteReplace(Class cl)
	{
		for (; cl != null; cl = cl.getSuperclass())
		{
			Method methods[] = cl.getDeclaredMethods();
			for (int i = 0; i < methods.length; i++)
			{
				Method method = methods[i];
				if (method.getName().equals("writeReplace") && method.getParameterTypes().length == 0)
					return method;
			}

		}

		return null;
	}

	public void writeObject(Object obj, AbstractHessianOutput out)
		throws IOException
	{
		int ref = out.getRef(obj);
		if (ref >= 0)
		{
			out.writeRef(ref);
			return;
		}
		Object repl;
		try
		{
			repl = writeReplace(obj);
			if (obj == repl)
			{
				if (log.isLoggable(Level.FINE))
					log.fine((new StringBuilder()).append(this).append(": Hessian writeReplace error.  The writeReplace method (").append(_writeReplace).append(") must not return the same object: ").append(obj).toString());
				_baseSerializer.writeObject(obj, out);
				return;
			}
		}
		catch (RuntimeException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		out.writeObject(repl);
		out.replaceRef(repl, obj);
	}

	protected Object writeReplace(Object obj)
	{
		if (_writeReplaceFactory != null)
			return _writeReplace.invoke(_writeReplaceFactory, new Object[] {
				obj
			});
		return _writeReplace.invoke(obj, new Object[0]);
		RuntimeException e;
		e;
		throw e;
		e;
		throw new RuntimeException(e.getCause());
		e;
		throw new RuntimeException(e);
	}

}
