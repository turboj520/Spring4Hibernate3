// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ClassGenerator.java

package com.autohome.turbo.common.bytecode;

import com.autohome.turbo.common.utils.ReflectUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.LoaderClassPath;
import javassist.Modifier;
import javassist.NotFoundException;

public final class ClassGenerator
{
	public static interface DC
	{
	}


	private static final AtomicLong CLASS_NAME_COUNTER = new AtomicLong(0L);
	private static final String SIMPLE_NAME_TAG = "<init>";
	private static final Map POOL_MAP = new ConcurrentHashMap();
	private ClassPool mPool;
	private CtClass mCtc;
	private String mClassName;
	private String mSuperClass;
	private Set mInterfaces;
	private List mFields;
	private List mConstructors;
	private List mMethods;
	private Map mCopyMethods;
	private Map mCopyConstructors;
	private boolean mDefaultConstructor;

	public static ClassGenerator newInstance()
	{
		return new ClassGenerator(getClassPool(Thread.currentThread().getContextClassLoader()));
	}

	public static ClassGenerator newInstance(ClassLoader loader)
	{
		return new ClassGenerator(getClassPool(loader));
	}

	public static boolean isDynamicClass(Class cl)
	{
		return com/autohome/turbo/common/bytecode/ClassGenerator$DC.isAssignableFrom(cl);
	}

	public static ClassPool getClassPool(ClassLoader loader)
	{
		if (loader == null)
			return ClassPool.getDefault();
		ClassPool pool = (ClassPool)POOL_MAP.get(loader);
		if (pool == null)
		{
			pool = new ClassPool(true);
			pool.appendClassPath(new LoaderClassPath(loader));
			POOL_MAP.put(loader, pool);
		}
		return pool;
	}

	private ClassGenerator()
	{
		mDefaultConstructor = false;
	}

	private ClassGenerator(ClassPool pool)
	{
		mDefaultConstructor = false;
		mPool = pool;
	}

	public String getClassName()
	{
		return mClassName;
	}

	public ClassGenerator setClassName(String name)
	{
		mClassName = name;
		return this;
	}

	public ClassGenerator addInterface(String cn)
	{
		if (mInterfaces == null)
			mInterfaces = new HashSet();
		mInterfaces.add(cn);
		return this;
	}

	public ClassGenerator addInterface(Class cl)
	{
		return addInterface(cl.getName());
	}

	public ClassGenerator setSuperClass(String cn)
	{
		mSuperClass = cn;
		return this;
	}

	public ClassGenerator setSuperClass(Class cl)
	{
		mSuperClass = cl.getName();
		return this;
	}

	public ClassGenerator addField(String code)
	{
		if (mFields == null)
			mFields = new ArrayList();
		mFields.add(code);
		return this;
	}

	public ClassGenerator addField(String name, int mod, Class type)
	{
		return addField(name, mod, type, null);
	}

	public ClassGenerator addField(String name, int mod, Class type, String def)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(modifier(mod)).append(' ').append(ReflectUtils.getName(type)).append(' ');
		sb.append(name);
		if (def != null && def.length() > 0)
		{
			sb.append('=');
			sb.append(def);
		}
		sb.append(';');
		return addField(sb.toString());
	}

	public ClassGenerator addMethod(String code)
	{
		if (mMethods == null)
			mMethods = new ArrayList();
		mMethods.add(code);
		return this;
	}

	public ClassGenerator addMethod(String name, int mod, Class rt, Class pts[], String body)
	{
		return addMethod(name, mod, rt, pts, null, body);
	}

	public ClassGenerator addMethod(String name, int mod, Class rt, Class pts[], Class ets[], String body)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(modifier(mod)).append(' ').append(ReflectUtils.getName(rt)).append(' ').append(name);
		sb.append('(');
		for (int i = 0; i < pts.length; i++)
		{
			if (i > 0)
				sb.append(',');
			sb.append(ReflectUtils.getName(pts[i]));
			sb.append(" arg").append(i);
		}

		sb.append(')');
		if (ets != null && ets.length > 0)
		{
			sb.append(" throws ");
			for (int i = 0; i < ets.length; i++)
			{
				if (i > 0)
					sb.append(',');
				sb.append(ReflectUtils.getName(ets[i]));
			}

		}
		sb.append('{').append(body).append('}');
		return addMethod(sb.toString());
	}

	public ClassGenerator addMethod(Method m)
	{
		addMethod(m.getName(), m);
		return this;
	}

	public ClassGenerator addMethod(String name, Method m)
	{
		String desc = (new StringBuilder()).append(name).append(ReflectUtils.getDescWithoutMethodName(m)).toString();
		addMethod((new StringBuilder()).append(':').append(desc).toString());
		if (mCopyMethods == null)
			mCopyMethods = new ConcurrentHashMap(8);
		mCopyMethods.put(desc, m);
		return this;
	}

	public ClassGenerator addConstructor(String code)
	{
		if (mConstructors == null)
			mConstructors = new LinkedList();
		mConstructors.add(code);
		return this;
	}

	public ClassGenerator addConstructor(int mod, Class pts[], String body)
	{
		return addConstructor(mod, pts, null, body);
	}

	public ClassGenerator addConstructor(int mod, Class pts[], Class ets[], String body)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(modifier(mod)).append(' ').append("<init>");
		sb.append('(');
		for (int i = 0; i < pts.length; i++)
		{
			if (i > 0)
				sb.append(',');
			sb.append(ReflectUtils.getName(pts[i]));
			sb.append(" arg").append(i);
		}

		sb.append(')');
		if (ets != null && ets.length > 0)
		{
			sb.append(" throws ");
			for (int i = 0; i < ets.length; i++)
			{
				if (i > 0)
					sb.append(',');
				sb.append(ReflectUtils.getName(ets[i]));
			}

		}
		sb.append('{').append(body).append('}');
		return addConstructor(sb.toString());
	}

	public ClassGenerator addConstructor(Constructor c)
	{
		String desc = ReflectUtils.getDesc(c);
		addConstructor((new StringBuilder()).append(":").append(desc).toString());
		if (mCopyConstructors == null)
			mCopyConstructors = new ConcurrentHashMap(4);
		mCopyConstructors.put(desc, c);
		return this;
	}

	public ClassGenerator addDefaultConstructor()
	{
		mDefaultConstructor = true;
		return this;
	}

	public ClassPool getClassPool()
	{
		return mPool;
	}

	public Class toClass()
	{
		return toClass(getClass().getClassLoader(), getClass().getProtectionDomain());
	}

	public Class toClass(ClassLoader loader, ProtectionDomain pd)
	{
		long id;
		if (mCtc != null)
			mCtc.detach();
		id = CLASS_NAME_COUNTER.getAndIncrement();
		CtClass ctcs = mSuperClass != null ? mPool.get(mSuperClass) : null;
		if (mClassName == null)
			mClassName = (new StringBuilder()).append(mSuperClass != null && !Modifier.isPublic(ctcs.getModifiers()) ? (new StringBuilder()).append(mSuperClass).append("$sc").toString() : com/autohome/turbo/common/bytecode/ClassGenerator.getName()).append(id).toString();
		mCtc = mPool.makeClass(mClassName);
		if (mSuperClass != null)
			mCtc.setSuperclass(ctcs);
		mCtc.addInterface(mPool.get(com/autohome/turbo/common/bytecode/ClassGenerator$DC.getName()));
		if (mInterfaces != null)
		{
			String cl;
			for (Iterator i$ = mInterfaces.iterator(); i$.hasNext(); mCtc.addInterface(mPool.get(cl)))
				cl = (String)i$.next();

		}
		if (mFields != null)
		{
			String code;
			for (Iterator i$ = mFields.iterator(); i$.hasNext(); mCtc.addField(CtField.make(code, mCtc)))
				code = (String)i$.next();

		}
		if (mMethods != null)
		{
			for (Iterator i$ = mMethods.iterator(); i$.hasNext();)
			{
				String code = (String)i$.next();
				if (code.charAt(0) == ':')
					mCtc.addMethod(CtNewMethod.copy(getCtMethod((Method)mCopyMethods.get(code.substring(1))), code.substring(1, code.indexOf('(')), mCtc, null));
				else
					mCtc.addMethod(CtNewMethod.make(code, mCtc));
			}

		}
		if (mDefaultConstructor)
			mCtc.addConstructor(CtNewConstructor.defaultConstructor(mCtc));
		if (mConstructors != null)
		{
			for (Iterator i$ = mConstructors.iterator(); i$.hasNext();)
			{
				String code = (String)i$.next();
				if (code.charAt(0) == ':')
				{
					mCtc.addConstructor(CtNewConstructor.copy(getCtConstructor((Constructor)mCopyConstructors.get(code.substring(1))), mCtc, null));
				} else
				{
					String sn[] = mCtc.getSimpleName().split("\\$+");
					mCtc.addConstructor(CtNewConstructor.make(code.replaceFirst("<init>", sn[sn.length - 1]), mCtc));
				}
			}

		}
		return mCtc.toClass(loader, pd);
		RuntimeException e;
		e;
		throw e;
		e;
		throw new RuntimeException(e.getMessage(), e);
		e;
		throw new RuntimeException(e.getMessage(), e);
	}

	public void release()
	{
		if (mCtc != null)
			mCtc.detach();
		if (mInterfaces != null)
			mInterfaces.clear();
		if (mFields != null)
			mFields.clear();
		if (mMethods != null)
			mMethods.clear();
		if (mConstructors != null)
			mConstructors.clear();
		if (mCopyMethods != null)
			mCopyMethods.clear();
		if (mCopyConstructors != null)
			mCopyConstructors.clear();
	}

	private CtClass getCtClass(Class c)
		throws NotFoundException
	{
		return mPool.get(c.getName());
	}

	private CtMethod getCtMethod(Method m)
		throws NotFoundException
	{
		return getCtClass(m.getDeclaringClass()).getMethod(m.getName(), ReflectUtils.getDescWithoutMethodName(m));
	}

	private CtConstructor getCtConstructor(Constructor c)
		throws NotFoundException
	{
		return getCtClass(c.getDeclaringClass()).getConstructor(ReflectUtils.getDesc(c));
	}

	private static String modifier(int mod)
	{
		if (java.lang.reflect.Modifier.isPublic(mod))
			return "public";
		if (java.lang.reflect.Modifier.isProtected(mod))
			return "protected";
		if (java.lang.reflect.Modifier.isPrivate(mod))
			return "private";
		else
			return "";
	}

}
