// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   BeanSerializer.java

package com.autohome.com.caucho.hessian.io;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

// Referenced classes of package com.autohome.com.caucho.hessian.io:
//			AbstractSerializer, AbstractHessianOutput

public class BeanSerializer extends AbstractSerializer
{
	static class MethodNameCmp
		implements Comparator
	{

		public int compare(Method a, Method b)
		{
			return a.getName().compareTo(b.getName());
		}

		public volatile int compare(Object x0, Object x1)
		{
			return compare((Method)x0, (Method)x1);
		}

		MethodNameCmp()
		{
		}
	}


	private static final Logger log = Logger.getLogger(com/autohome/com/caucho/hessian/io/BeanSerializer.getName());
	private static final Object NULL_ARGS[] = new Object[0];
	private Method _methods[];
	private String _names[];
	private Object _writeReplaceFactory;
	private Method _writeReplace;

	public BeanSerializer(Class cl, ClassLoader loader)
	{
		introspectWriteReplace(cl, loader);
		ArrayList primitiveMethods = new ArrayList();
		ArrayList compoundMethods = new ArrayList();
		for (; cl != null; cl = cl.getSuperclass())
		{
			Method methods[] = cl.getDeclaredMethods();
			for (int i = 0; i < methods.length; i++)
			{
				Method method = methods[i];
				if (Modifier.isStatic(method.getModifiers()) || method.getParameterTypes().length != 0)
					continue;
				String name = method.getName();
				if (!name.startsWith("get"))
					continue;
				Class type = method.getReturnType();
				if (type.equals(Void.TYPE) || findSetter(methods, name, type) == null)
					continue;
				method.setAccessible(true);
				if (type.isPrimitive() || type.getName().startsWith("java.lang.") && !type.equals(java/lang/Object))
					primitiveMethods.add(method);
				else
					compoundMethods.add(method);
			}

		}

		ArrayList methodList = new ArrayList();
		methodList.addAll(primitiveMethods);
		methodList.addAll(compoundMethods);
		Collections.sort(methodList, new MethodNameCmp());
		_methods = new Method[methodList.size()];
		methodList.toArray(_methods);
		_names = new String[_methods.length];
		for (int i = 0; i < _methods.length; i++)
		{
			String name = _methods[i].getName();
			name = name.substring(3);
			int j;
			for (j = 0; j < name.length() && Character.isUpperCase(name.charAt(j)); j++);
			if (j == 1)
				name = (new StringBuilder()).append(name.substring(0, j).toLowerCase()).append(name.substring(j)).toString();
			else
			if (j > 1)
				name = (new StringBuilder()).append(name.substring(0, j - 1).toLowerCase()).append(name.substring(j - 1)).toString();
			_names[i] = name;
		}

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
				return;
			}
		}
		catch (ClassNotFoundException e) { }
		catch (Exception e)
		{
			log.log(Level.FINER, e.toString(), e);
		}
		_writeReplace = getWriteReplace(cl);
	}

	protected Method getWriteReplace(Class cl)
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

	protected Method getWriteReplace(Class cl, Class param)
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

	public void writeObject(Object obj, AbstractHessianOutput out)
		throws IOException
	{
		if (out.addRef(obj))
			return;
		Class cl = obj.getClass();
		try
		{
			if (_writeReplace != null)
			{
				Object repl;
				if (_writeReplaceFactory != null)
					repl = _writeReplace.invoke(_writeReplaceFactory, new Object[] {
						obj
					});
				else
					repl = _writeReplace.invoke(obj, new Object[0]);
				out.removeRef(obj);
				out.writeObject(repl);
				out.replaceRef(repl, obj);
				return;
			}
		}
		catch (Exception e)
		{
			log.log(Level.FINER, e.toString(), e);
		}
		int ref = out.writeObjectBegin(cl.getName());
		if (ref < -1)
		{
			for (int i = 0; i < _methods.length; i++)
			{
				Method method = _methods[i];
				Object value = null;
				try
				{
					value = _methods[i].invoke(obj, (Object[])null);
				}
				catch (Exception e)
				{
					log.log(Level.FINE, e.toString(), e);
				}
				out.writeString(_names[i]);
				out.writeObject(value);
			}

			out.writeMapEnd();
		} else
		{
			if (ref == -1)
			{
				out.writeInt(_names.length);
				for (int i = 0; i < _names.length; i++)
					out.writeString(_names[i]);

				out.writeObjectBegin(cl.getName());
			}
			for (int i = 0; i < _methods.length; i++)
			{
				Method method = _methods[i];
				Object value = null;
				try
				{
					value = _methods[i].invoke(obj, (Object[])null);
				}
				catch (Exception e)
				{
					log.log(Level.FINER, e.toString(), e);
				}
				out.writeObject(value);
			}

		}
	}

	private Method findSetter(Method methods[], String getterName, Class arg)
	{
		String setterName = (new StringBuilder()).append("set").append(getterName.substring(3)).toString();
		for (int i = 0; i < methods.length; i++)
		{
			Method method = methods[i];
			if (!method.getName().equals(setterName) || !method.getReturnType().equals(Void.TYPE))
				continue;
			Class params[] = method.getParameterTypes();
			if (params.length == 1 && params[0].equals(arg))
				return method;
		}

		return null;
	}

}
