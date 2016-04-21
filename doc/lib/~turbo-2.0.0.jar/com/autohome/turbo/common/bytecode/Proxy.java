// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Proxy.java

package com.autohome.turbo.common.bytecode;

import com.autohome.turbo.common.utils.ClassHelper;
import com.autohome.turbo.common.utils.ReflectUtils;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

// Referenced classes of package com.autohome.turbo.common.bytecode:
//			ClassGenerator

public abstract class Proxy
{

	private static final AtomicLong PROXY_CLASS_COUNTER = new AtomicLong(0L);
	private static final String PACKAGE_NAME = com/autohome/turbo/common/bytecode/Proxy.getPackage().getName();
	public static final InvocationHandler RETURN_NULL_INVOKER = new InvocationHandler() {

		public Object invoke(Object proxy, Method method, Object args[])
		{
			return null;
		}

	};
	public static final InvocationHandler THROW_UNSUPPORTED_INVOKER = new InvocationHandler() {

		public Object invoke(Object proxy, Method method, Object args[])
		{
			throw new UnsupportedOperationException((new StringBuilder()).append("Method [").append(ReflectUtils.getName(method)).append("] unimplemented.").toString());
		}

	};
	private static final Map ProxyCacheMap = new WeakHashMap();
	private static final Object PendingGenerationMarker = new Object();

	public static transient Proxy getProxy(Class ics[])
	{
		return getProxy(ClassHelper.getCallerClassLoader(com/autohome/turbo/common/bytecode/Proxy), ics);
	}

	public static transient Proxy getProxy(ClassLoader cl, Class ics[])
	{
		String key;
		Map cache;
		if (ics.length > 65535)
			throw new IllegalArgumentException("interface limit exceeded");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < ics.length; i++)
		{
			String itf = ics[i].getName();
			if (!ics[i].isInterface())
				throw new RuntimeException((new StringBuilder()).append(itf).append(" is not a interface.").toString());
			Class tmp = null;
			try
			{
				tmp = Class.forName(itf, false, cl);
			}
			catch (ClassNotFoundException e) { }
			if (tmp != ics[i])
				throw new IllegalArgumentException((new StringBuilder()).append(ics[i]).append(" is not visible from class loader").toString());
			sb.append(itf).append(';');
		}

		key = sb.toString();
		synchronized (ProxyCacheMap)
		{
			cache = (Map)ProxyCacheMap.get(cl);
			if (cache == null)
			{
				cache = new HashMap();
				ProxyCacheMap.put(cl, cache);
			}
		}
		Proxy proxy = null;
		Map map1 = cache;
		JVM INSTR monitorenter ;
_L2:
		Object value;
		value = cache.get(key);
		if (!(value instanceof Reference))
			break MISSING_BLOCK_LABEL_267;
		proxy = (Proxy)((Reference)value).get();
		if (proxy != null)
			return proxy;
label0:
		{
			if (value != PendingGenerationMarker)
				break label0;
			try
			{
				cache.wait();
			}
			catch (InterruptedException e) { }
		}
		if (true) goto _L2; else goto _L1
_L1:
		cache.put(key, PendingGenerationMarker);
		map1;
		JVM INSTR monitorexit ;
		  goto _L3
		Exception exception1;
		exception1;
		throw exception1;
_L3:
		long id = PROXY_CLASS_COUNTER.getAndIncrement();
		String pkg = null;
		ClassGenerator ccp = null;
		ClassGenerator ccm = null;
		Exception exception3;
		try
		{
			ccp = ClassGenerator.newInstance(cl);
			Set worked = new HashSet();
			List methods = new ArrayList();
			for (int i = 0; i < ics.length; i++)
			{
				if (!Modifier.isPublic(ics[i].getModifiers()))
				{
					String npkg = ics[i].getPackage().getName();
					if (pkg == null)
						pkg = npkg;
					else
					if (!pkg.equals(npkg))
						throw new IllegalArgumentException("non-public interfaces from different packages");
				}
				ccp.addInterface(ics[i]);
				Method arr$[] = ics[i].getMethods();
				int len$ = arr$.length;
				for (int i$ = 0; i$ < len$; i$++)
				{
					Method method = arr$[i$];
					String desc = ReflectUtils.getDesc(method);
					if (worked.contains(desc))
						continue;
					worked.add(desc);
					int ix = methods.size();
					Class rt = method.getReturnType();
					Class pts[] = method.getParameterTypes();
					StringBuilder code = (new StringBuilder("Object[] args = new Object[")).append(pts.length).append("];");
					for (int j = 0; j < pts.length; j++)
						code.append(" args[").append(j).append("] = ($w)$").append(j + 1).append(";");

					code.append((new StringBuilder()).append(" Object ret = handler.invoke(this, methods[").append(ix).append("], args);").toString());
					if (!Void.TYPE.equals(rt))
						code.append(" return ").append(asArgument(rt, "ret")).append(";");
					methods.add(method);
					ccp.addMethod(method.getName(), method.getModifiers(), rt, pts, method.getExceptionTypes(), code.toString());
				}

			}

			if (pkg == null)
				pkg = PACKAGE_NAME;
			String pcn = (new StringBuilder()).append(pkg).append(".proxy").append(id).toString();
			ccp.setClassName(pcn);
			ccp.addField("public static java.lang.reflect.Method[] methods;");
			ccp.addField((new StringBuilder()).append("private ").append(java/lang/reflect/InvocationHandler.getName()).append(" handler;").toString());
			ccp.addConstructor(1, new Class[] {
				java/lang/reflect/InvocationHandler
			}, new Class[0], "handler=$1;");
			ccp.addDefaultConstructor();
			Class clazz = ccp.toClass();
			clazz.getField("methods").set(null, ((Object) (methods.toArray(new Method[0]))));
			String fcn = (new StringBuilder()).append(com/autohome/turbo/common/bytecode/Proxy.getName()).append(id).toString();
			ccm = ClassGenerator.newInstance(cl);
			ccm.setClassName(fcn);
			ccm.addDefaultConstructor();
			ccm.setSuperClass(com/autohome/turbo/common/bytecode/Proxy);
			ccm.addMethod((new StringBuilder()).append("public Object newInstance(").append(java/lang/reflect/InvocationHandler.getName()).append(" h){ return new ").append(pcn).append("($1); }").toString());
			Class pc = ccm.toClass();
			proxy = (Proxy)pc.newInstance();
		}
		catch (RuntimeException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}
		finally
		{
			if (ccp == null) goto _L0; else goto _L0
		}
		if (ccp != null)
			ccp.release();
		if (ccm != null)
			ccm.release();
		synchronized (cache)
		{
			if (proxy == null)
				cache.remove(key);
			else
				cache.put(key, new WeakReference(proxy));
			cache.notifyAll();
		}
		if (false)
		{
			ccp.release();
			if (ccm != null)
				ccm.release();
			synchronized (cache)
			{
				if (proxy == null)
					cache.remove(key);
				else
					cache.put(key, new WeakReference(proxy));
				cache.notifyAll();
			}
			throw exception3;
		} else
		{
			return proxy;
		}
	}

	public Object newInstance()
	{
		return newInstance(THROW_UNSUPPORTED_INVOKER);
	}

	public abstract Object newInstance(InvocationHandler invocationhandler);

	protected Proxy()
	{
	}

	private static String asArgument(Class cl, String name)
	{
		if (cl.isPrimitive())
		{
			if (Boolean.TYPE == cl)
				return (new StringBuilder()).append(name).append("==null?false:((Boolean)").append(name).append(").booleanValue()").toString();
			if (Byte.TYPE == cl)
				return (new StringBuilder()).append(name).append("==null?(byte)0:((Byte)").append(name).append(").byteValue()").toString();
			if (Character.TYPE == cl)
				return (new StringBuilder()).append(name).append("==null?(char)0:((Character)").append(name).append(").charValue()").toString();
			if (Double.TYPE == cl)
				return (new StringBuilder()).append(name).append("==null?(double)0:((Double)").append(name).append(").doubleValue()").toString();
			if (Float.TYPE == cl)
				return (new StringBuilder()).append(name).append("==null?(float)0:((Float)").append(name).append(").floatValue()").toString();
			if (Integer.TYPE == cl)
				return (new StringBuilder()).append(name).append("==null?(int)0:((Integer)").append(name).append(").intValue()").toString();
			if (Long.TYPE == cl)
				return (new StringBuilder()).append(name).append("==null?(long)0:((Long)").append(name).append(").longValue()").toString();
			if (Short.TYPE == cl)
				return (new StringBuilder()).append(name).append("==null?(short)0:((Short)").append(name).append(").shortValue()").toString();
			else
				throw new RuntimeException((new StringBuilder()).append(name).append(" is unknown primitive type.").toString());
		} else
		{
			return (new StringBuilder()).append("(").append(ReflectUtils.getName(cl)).append(")").append(name).toString();
		}
	}

}
