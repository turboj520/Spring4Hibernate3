// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   BeanDeserializer.java

package com.autohome.com.caucho.hessian.io;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.HashMap;

// Referenced classes of package com.autohome.com.caucho.hessian.io:
//			AbstractMapDeserializer, IOExceptionWrapper, AbstractHessianInput

public class BeanDeserializer extends AbstractMapDeserializer
{

	private Class _type;
	private HashMap _methodMap;
	private Method _readResolve;
	private Constructor _constructor;
	private Object _constructorArgs[];

	public BeanDeserializer(Class cl)
	{
		_type = cl;
		_methodMap = getMethodMap(cl);
		_readResolve = getReadResolve(cl);
		Constructor constructors[] = cl.getConstructors();
		int bestLength = 0x7fffffff;
		for (int i = 0; i < constructors.length; i++)
			if (constructors[i].getParameterTypes().length < bestLength)
			{
				_constructor = constructors[i];
				bestLength = _constructor.getParameterTypes().length;
			}

		if (_constructor != null)
		{
			_constructor.setAccessible(true);
			Class params[] = _constructor.getParameterTypes();
			_constructorArgs = new Object[params.length];
			for (int i = 0; i < params.length; i++)
				_constructorArgs[i] = getParamArg(params[i]);

		}
	}

	public Class getType()
	{
		return _type;
	}

	public Object readMap(AbstractHessianInput in)
		throws IOException
	{
		Object obj = instantiate();
		return readMap(in, obj);
		IOException e;
		e;
		throw e;
		e;
		throw new IOExceptionWrapper(e);
	}

	public Object readMap(AbstractHessianInput in, Object obj)
		throws IOException
	{
		Object resolve;
		int ref = in.addRef(obj);
		while (!in.isEnd()) 
		{
			Object key = in.readObject();
			Method method = (Method)_methodMap.get(key);
			Object value;
			if (method != null)
			{
				value = in.readObject(method.getParameterTypes()[0]);
				method.invoke(obj, new Object[] {
					value
				});
			} else
			{
				value = in.readObject();
			}
		}
		in.readMapEnd();
		resolve = resolve(obj);
		if (obj != resolve)
			in.setRef(ref, resolve);
		return resolve;
		IOException e;
		e;
		throw e;
		e;
		throw new IOExceptionWrapper(e);
	}

	private Object resolve(Object obj)
	{
		if (_readResolve != null)
			return _readResolve.invoke(obj, new Object[0]);
		break MISSING_BLOCK_LABEL_24;
		Exception e;
		e;
		return obj;
	}

	protected Object instantiate()
		throws Exception
	{
		return _constructor.newInstance(_constructorArgs);
	}

	protected Method getReadResolve(Class cl)
	{
		for (; cl != null; cl = cl.getSuperclass())
		{
			Method methods[] = cl.getDeclaredMethods();
			for (int i = 0; i < methods.length; i++)
			{
				Method method = methods[i];
				if (method.getName().equals("readResolve") && method.getParameterTypes().length == 0)
					return method;
			}

		}

		return null;
	}

	protected HashMap getMethodMap(Class cl)
	{
		HashMap methodMap = new HashMap();
		for (; cl != null; cl = cl.getSuperclass())
		{
			Method methods[] = cl.getDeclaredMethods();
			for (int i = 0; i < methods.length; i++)
			{
				Method method = methods[i];
				if (Modifier.isStatic(method.getModifiers()))
					continue;
				String name = method.getName();
				if (!name.startsWith("set"))
					continue;
				Class paramTypes[] = method.getParameterTypes();
				if (paramTypes.length != 1 || !method.getReturnType().equals(Void.TYPE) || findGetter(methods, name, paramTypes[0]) == null)
					continue;
				try
				{
					method.setAccessible(true);
				}
				catch (Throwable e)
				{
					e.printStackTrace();
				}
				name = name.substring(3);
				int j;
				for (j = 0; j < name.length() && Character.isUpperCase(name.charAt(j)); j++);
				if (j == 1)
					name = (new StringBuilder()).append(name.substring(0, j).toLowerCase()).append(name.substring(j)).toString();
				else
				if (j > 1)
					name = (new StringBuilder()).append(name.substring(0, j - 1).toLowerCase()).append(name.substring(j - 1)).toString();
				methodMap.put(name, method);
			}

		}

		return methodMap;
	}

	private Method findGetter(Method methods[], String setterName, Class arg)
	{
		String getterName = (new StringBuilder()).append("get").append(setterName.substring(3)).toString();
		for (int i = 0; i < methods.length; i++)
		{
			Method method = methods[i];
			if (!method.getName().equals(getterName) || !method.getReturnType().equals(arg))
				continue;
			Class params[] = method.getParameterTypes();
			if (params.length == 0)
				return method;
		}

		return null;
	}

	protected static Object getParamArg(Class cl)
	{
		if (!cl.isPrimitive())
			return null;
		if (Boolean.TYPE.equals(cl))
			return Boolean.FALSE;
		if (Byte.TYPE.equals(cl))
			return Byte.valueOf((byte)0);
		if (Short.TYPE.equals(cl))
			return Short.valueOf((short)0);
		if (Character.TYPE.equals(cl))
			return Character.valueOf('\0');
		if (Integer.TYPE.equals(cl))
			return Integer.valueOf(0);
		if (Long.TYPE.equals(cl))
			return Long.valueOf(0L);
		if (Float.TYPE.equals(cl))
			return Double.valueOf(0.0D);
		if (Double.TYPE.equals(cl))
			return Double.valueOf(0.0D);
		else
			throw new UnsupportedOperationException();
	}
}
