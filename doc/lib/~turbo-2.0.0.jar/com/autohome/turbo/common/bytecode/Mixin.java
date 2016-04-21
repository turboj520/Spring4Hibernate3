// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Mixin.java

package com.autohome.turbo.common.bytecode;

import com.autohome.turbo.common.utils.ClassHelper;
import com.autohome.turbo.common.utils.ReflectUtils;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

// Referenced classes of package com.autohome.turbo.common.bytecode:
//			ClassGenerator

public abstract class Mixin
{
	public static interface MixinAware
	{

		public abstract void setMixinInstance(Object obj);
	}


	private static AtomicLong MIXIN_CLASS_COUNTER = new AtomicLong(0L);
	private static final String PACKAGE_NAME = com/autohome/turbo/common/bytecode/Mixin.getPackage().getName();

	public static Mixin mixin(Class ics[], Class dc)
	{
		return mixin(ics, new Class[] {
			dc
		});
	}

	public static Mixin mixin(Class ics[], Class dc, ClassLoader cl)
	{
		return mixin(ics, new Class[] {
			dc
		}, cl);
	}

	public static Mixin mixin(Class ics[], Class dcs[])
	{
		return mixin(ics, dcs, ClassHelper.getCallerClassLoader(com/autohome/turbo/common/bytecode/Mixin));
	}

	public static Mixin mixin(Class ics[], Class dcs[], ClassLoader cl)
	{
		ClassGenerator ccp;
		ClassGenerator ccm;
		Exception exception;
		assertInterfaceArray(ics);
		long id = MIXIN_CLASS_COUNTER.getAndIncrement();
		String pkg = null;
		ccp = null;
		ccm = null;
		Mixin mixin1;
		try
		{
			ccp = ClassGenerator.newInstance(cl);
			StringBuilder code = new StringBuilder();
			for (int i = 0; i < dcs.length; i++)
			{
				if (!Modifier.isPublic(dcs[i].getModifiers()))
				{
					String npkg = dcs[i].getPackage().getName();
					if (pkg == null)
						pkg = npkg;
					else
					if (!pkg.equals(npkg))
						throw new IllegalArgumentException("non-public interfaces class from different packages");
				}
				ccp.addField((new StringBuilder()).append("private ").append(dcs[i].getName()).append(" d").append(i).append(";").toString());
				code.append("d").append(i).append(" = (").append(dcs[i].getName()).append(")$1[").append(i).append("];\n");
				if (com/autohome/turbo/common/bytecode/Mixin$MixinAware.isAssignableFrom(dcs[i]))
					code.append("d").append(i).append(".setMixinInstance(this);\n");
			}

			ccp.addConstructor(1, new Class[] {
				[Ljava/lang/Object;
			}, code.toString());
			Set worked = new HashSet();
			for (int i = 0; i < ics.length; i++)
			{
				if (!Modifier.isPublic(ics[i].getModifiers()))
				{
					String npkg = ics[i].getPackage().getName();
					if (pkg == null)
						pkg = npkg;
					else
					if (!pkg.equals(npkg))
						throw new IllegalArgumentException("non-public delegate class from different packages");
				}
				ccp.addInterface(ics[i]);
				Method arr$[] = ics[i].getMethods();
				int len$ = arr$.length;
				for (int i$ = 0; i$ < len$; i$++)
				{
					Method method = arr$[i$];
					if ("java.lang.Object".equals(method.getDeclaringClass().getName()))
						continue;
					String desc = ReflectUtils.getDesc(method);
					if (worked.contains(desc))
						continue;
					worked.add(desc);
					int ix = findMethod(dcs, desc);
					if (ix < 0)
						throw new RuntimeException((new StringBuilder()).append("Missing method [").append(desc).append("] implement.").toString());
					Class rt = method.getReturnType();
					String mn = method.getName();
					if (Void.TYPE.equals(rt))
						ccp.addMethod(mn, method.getModifiers(), rt, method.getParameterTypes(), method.getExceptionTypes(), (new StringBuilder()).append("d").append(ix).append(".").append(mn).append("($$);").toString());
					else
						ccp.addMethod(mn, method.getModifiers(), rt, method.getParameterTypes(), method.getExceptionTypes(), (new StringBuilder()).append("return ($r)d").append(ix).append(".").append(mn).append("($$);").toString());
				}

			}

			if (pkg == null)
				pkg = PACKAGE_NAME;
			String micn = (new StringBuilder()).append(pkg).append(".mixin").append(id).toString();
			ccp.setClassName(micn);
			ccp.toClass();
			String fcn = (new StringBuilder()).append(com/autohome/turbo/common/bytecode/Mixin.getName()).append(id).toString();
			ccm = ClassGenerator.newInstance(cl);
			ccm.setClassName(fcn);
			ccm.addDefaultConstructor();
			ccm.setSuperClass(com/autohome/turbo/common/bytecode/Mixin.getName());
			ccm.addMethod((new StringBuilder()).append("public Object newInstance(Object[] delegates){ return new ").append(micn).append("($1); }").toString());
			Class mixin = ccm.toClass();
			mixin1 = (Mixin)mixin.newInstance();
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
		return mixin1;
		ccp.release();
		if (ccm != null)
			ccm.release();
		throw exception;
	}

	public abstract Object newInstance(Object aobj[]);

	protected Mixin()
	{
	}

	private static int findMethod(Class dcs[], String desc)
	{
		for (int i = 0; i < dcs.length; i++)
		{
			Class cl = dcs[i];
			Method methods[] = cl.getMethods();
			Method arr$[] = methods;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				Method method = arr$[i$];
				if (desc.equals(ReflectUtils.getDesc(method)))
					return i;
			}

		}

		return -1;
	}

	private static void assertInterfaceArray(Class ics[])
	{
		for (int i = 0; i < ics.length; i++)
			if (!ics[i].isInterface())
				throw new RuntimeException((new StringBuilder()).append("Class ").append(ics[i].getName()).append(" is not a interface.").toString());

	}

}
