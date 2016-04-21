// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Wrapper.java

package com.autohome.turbo.common.bytecode;

import com.autohome.turbo.common.utils.ClassHelper;
import com.autohome.turbo.common.utils.ReflectUtils;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Referenced classes of package com.autohome.turbo.common.bytecode:
//			NoSuchMethodException, NoSuchPropertyException, ClassGenerator

public abstract class Wrapper
{

	private static AtomicLong WRAPPER_CLASS_COUNTER = new AtomicLong(0L);
	private static final Map WRAPPER_MAP = new ConcurrentHashMap();
	private static final String EMPTY_STRING_ARRAY[] = new String[0];
	private static final String OBJECT_METHODS[] = {
		"getClass", "hashCode", "toString", "equals"
	};
	private static final Wrapper OBJECT_WRAPPER = new Wrapper() {

		public String[] getMethodNames()
		{
			return Wrapper.OBJECT_METHODS;
		}

		public String[] getDeclaredMethodNames()
		{
			return Wrapper.OBJECT_METHODS;
		}

		public String[] getPropertyNames()
		{
			return Wrapper.EMPTY_STRING_ARRAY;
		}

		public Class getPropertyType(String pn)
		{
			return null;
		}

		public Object getPropertyValue(Object instance, String pn)
			throws NoSuchPropertyException
		{
			throw new NoSuchPropertyException((new StringBuilder()).append("Property [").append(pn).append("] not found.").toString());
		}

		public void setPropertyValue(Object instance, String pn, Object pv)
			throws NoSuchPropertyException
		{
			throw new NoSuchPropertyException((new StringBuilder()).append("Property [").append(pn).append("] not found.").toString());
		}

		public boolean hasProperty(String name)
		{
			return false;
		}

		public Object invokeMethod(Object instance, String mn, Class types[], Object args[])
			throws NoSuchMethodException
		{
			if ("getClass".equals(mn))
				return instance.getClass();
			if ("hashCode".equals(mn))
				return Integer.valueOf(instance.hashCode());
			if ("toString".equals(mn))
				return instance.toString();
			if ("equals".equals(mn))
			{
				if (args.length == 1)
					return Boolean.valueOf(instance.equals(args[0]));
				else
					throw new IllegalArgumentException((new StringBuilder()).append("Invoke method [").append(mn).append("] argument number error.").toString());
			} else
			{
				throw new NoSuchMethodException((new StringBuilder()).append("Method [").append(mn).append("] not found.").toString());
			}
		}

	};

	public Wrapper()
	{
	}

	public static Wrapper getWrapper(Class c)
	{
		for (; ClassGenerator.isDynamicClass(c); c = c.getSuperclass());
		if (c == java/lang/Object)
			return OBJECT_WRAPPER;
		Wrapper ret = (Wrapper)WRAPPER_MAP.get(c);
		if (ret == null)
		{
			ret = makeWrapper(c);
			WRAPPER_MAP.put(c, ret);
		}
		return ret;
	}

	public abstract String[] getPropertyNames();

	public abstract Class getPropertyType(String s);

	public abstract boolean hasProperty(String s);

	public abstract Object getPropertyValue(Object obj, String s)
		throws NoSuchPropertyException, IllegalArgumentException;

	public abstract void setPropertyValue(Object obj, String s, Object obj1)
		throws NoSuchPropertyException, IllegalArgumentException;

	public Object[] getPropertyValues(Object instance, String pns[])
		throws NoSuchPropertyException, IllegalArgumentException
	{
		Object ret[] = new Object[pns.length];
		for (int i = 0; i < ret.length; i++)
			ret[i] = getPropertyValue(instance, pns[i]);

		return ret;
	}

	public void setPropertyValues(Object instance, String pns[], Object pvs[])
		throws NoSuchPropertyException, IllegalArgumentException
	{
		if (pns.length != pvs.length)
			throw new IllegalArgumentException("pns.length != pvs.length");
		for (int i = 0; i < pns.length; i++)
			setPropertyValue(instance, pns[i], pvs[i]);

	}

	public abstract String[] getMethodNames();

	public abstract String[] getDeclaredMethodNames();

	public boolean hasMethod(String name)
	{
		String arr$[] = getMethodNames();
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			String mn = arr$[i$];
			if (mn.equals(name))
				return true;
		}

		return false;
	}

	public abstract Object invokeMethod(Object obj, String s, Class aclass[], Object aobj[])
		throws NoSuchMethodException, InvocationTargetException;

	private static Wrapper makeWrapper(Class c)
	{
		Map ms;
		List mns;
		List dmns;
		Exception exception;
		if (c.isPrimitive())
			throw new IllegalArgumentException((new StringBuilder()).append("Can not create wrapper for primitive type: ").append(c).toString());
		String name = c.getName();
		ClassLoader cl = ClassHelper.getClassLoader(c);
		StringBuilder c1 = new StringBuilder("public void setPropertyValue(Object o, String n, Object v){ ");
		StringBuilder c2 = new StringBuilder("public Object getPropertyValue(Object o, String n){ ");
		StringBuilder c3 = new StringBuilder((new StringBuilder()).append("public Object invokeMethod(Object o, String n, Class[] p, Object[] v) throws ").append(java/lang/reflect/InvocationTargetException.getName()).append("{ ").toString());
		c1.append(name).append(" w; try{ w = ((").append(name).append(")$1); }catch(Throwable e){ throw new IllegalArgumentException(e); }");
		c2.append(name).append(" w; try{ w = ((").append(name).append(")$1); }catch(Throwable e){ throw new IllegalArgumentException(e); }");
		c3.append(name).append(" w; try{ w = ((").append(name).append(")$1); }catch(Throwable e){ throw new IllegalArgumentException(e); }");
		Map pts = new HashMap();
		ms = new LinkedHashMap();
		mns = new ArrayList();
		dmns = new ArrayList();
		Field arr$[] = c.getFields();
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			Field f = arr$[i$];
			String fn = f.getName();
			Class ft = f.getType();
			if (!Modifier.isStatic(f.getModifiers()) && !Modifier.isTransient(f.getModifiers()))
			{
				c1.append(" if( $2.equals(\"").append(fn).append("\") ){ w.").append(fn).append("=").append(arg(ft, "$3")).append("; return; }");
				c2.append(" if( $2.equals(\"").append(fn).append("\") ){ return ($w)w.").append(fn).append("; }");
				pts.put(fn, ft);
			}
		}

		Method methods[] = c.getMethods();
		boolean hasMethod = hasMethods(methods);
		if (hasMethod)
			c3.append(" try{");
		Method arr$[] = methods;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			Method m = arr$[i$];
			if (m.getDeclaringClass() == java/lang/Object)
				continue;
			String mn = m.getName();
			c3.append(" if( \"").append(mn).append("\".equals( $2 ) ");
			int len = m.getParameterTypes().length;
			c3.append(" && ").append(" $3.length == ").append(len);
			boolean override = false;
			Method arr$[] = methods;
			int len$ = arr$.length;
			int i$ = 0;
			do
			{
				if (i$ >= len$)
					break;
				Method m2 = arr$[i$];
				if (m != m2 && m.getName().equals(m2.getName()))
				{
					override = true;
					break;
				}
				i$++;
			} while (true);
			if (override && len > 0)
			{
				for (int l = 0; l < len; l++)
					c3.append(" && ").append(" $3[").append(l).append("].getName().equals(\"").append(m.getParameterTypes()[l].getName()).append("\")");

			}
			c3.append(" ) { ");
			if (m.getReturnType() == Void.TYPE)
				c3.append(" w.").append(mn).append('(').append(args(m.getParameterTypes(), "$4")).append(");").append(" return null;");
			else
				c3.append(" return ($w)w.").append(mn).append('(').append(args(m.getParameterTypes(), "$4")).append(");");
			c3.append(" }");
			mns.add(mn);
			if (m.getDeclaringClass() == c)
				dmns.add(mn);
			ms.put(ReflectUtils.getDesc(m), m);
		}

		if (hasMethod)
		{
			c3.append(" } catch(Throwable e) { ");
			c3.append("     throw new java.lang.reflect.InvocationTargetException(e); ");
			c3.append(" }");
		}
		c3.append((new StringBuilder()).append(" throw new ").append(com/autohome/turbo/common/bytecode/NoSuchMethodException.getName()).append("(\"Not found method \\\"\"+$2+\"\\\" in class ").append(c.getName()).append(".\"); }").toString());
		Iterator i$ = ms.entrySet().iterator();
		do
		{
			if (!i$.hasNext())
				break;
			java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
			String md = (String)entry.getKey();
			Method method = (Method)entry.getValue();
			Matcher matcher;
			if ((matcher = ReflectUtils.GETTER_METHOD_DESC_PATTERN.matcher(md)).matches())
			{
				String pn = propertyName(matcher.group(1));
				c2.append(" if( $2.equals(\"").append(pn).append("\") ){ return ($w)w.").append(method.getName()).append("(); }");
				pts.put(pn, method.getReturnType());
			} else
			if ((matcher = ReflectUtils.IS_HAS_CAN_METHOD_DESC_PATTERN.matcher(md)).matches())
			{
				String pn = propertyName(matcher.group(1));
				c2.append(" if( $2.equals(\"").append(pn).append("\") ){ return ($w)w.").append(method.getName()).append("(); }");
				pts.put(pn, method.getReturnType());
			} else
			if ((matcher = ReflectUtils.SETTER_METHOD_DESC_PATTERN.matcher(md)).matches())
			{
				Class pt = method.getParameterTypes()[0];
				String pn = propertyName(matcher.group(1));
				c1.append(" if( $2.equals(\"").append(pn).append("\") ){ w.").append(method.getName()).append("(").append(arg(pt, "$3")).append("); return; }");
				pts.put(pn, pt);
			}
		} while (true);
		c1.append((new StringBuilder()).append(" throw new ").append(com/autohome/turbo/common/bytecode/NoSuchPropertyException.getName()).append("(\"Not found property \\\"\"+$2+\"\\\" filed or setter method in class ").append(c.getName()).append(".\"); }").toString());
		c2.append((new StringBuilder()).append(" throw new ").append(com/autohome/turbo/common/bytecode/NoSuchPropertyException.getName()).append("(\"Not found property \\\"\"+$2+\"\\\" filed or setter method in class ").append(c.getName()).append(".\"); }").toString());
		long id = WRAPPER_CLASS_COUNTER.getAndIncrement();
		ClassGenerator cc = ClassGenerator.newInstance(cl);
		cc.setClassName((new StringBuilder()).append(Modifier.isPublic(c.getModifiers()) ? com/autohome/turbo/common/bytecode/Wrapper.getName() : (new StringBuilder()).append(c.getName()).append("$sw").toString()).append(id).toString());
		cc.setSuperClass(com/autohome/turbo/common/bytecode/Wrapper);
		cc.addDefaultConstructor();
		cc.addField("public static String[] pns;");
		cc.addField((new StringBuilder()).append("public static ").append(java/util/Map.getName()).append(" pts;").toString());
		cc.addField("public static String[] mns;");
		cc.addField("public static String[] dmns;");
		int i = 0;
		for (int len = ms.size(); i < len; i++)
			cc.addField((new StringBuilder()).append("public static Class[] mts").append(i).append(";").toString());

		cc.addMethod("public String[] getPropertyNames(){ return pns; }");
		cc.addMethod("public boolean hasProperty(String n){ return pts.containsKey($1); }");
		cc.addMethod("public Class getPropertyType(String n){ return (Class)pts.get($1); }");
		cc.addMethod("public String[] getMethodNames(){ return mns; }");
		cc.addMethod("public String[] getDeclaredMethodNames(){ return dmns; }");
		cc.addMethod(c1.toString());
		cc.addMethod(c2.toString());
		cc.addMethod(c3.toString());
		Wrapper wrapper;
		try
		{
			Class wc = cc.toClass();
			wc.getField("pts").set(null, pts);
			wc.getField("pns").set(null, ((Object) (pts.keySet().toArray(new String[0]))));
			wc.getField("mns").set(null, ((Object) (mns.toArray(new String[0]))));
			wc.getField("dmns").set(null, ((Object) (dmns.toArray(new String[0]))));
			int ix = 0;
			Method m;
			for (Iterator i$ = ms.values().iterator(); i$.hasNext(); wc.getField((new StringBuilder()).append("mts").append(ix++).toString()).set(null, m.getParameterTypes()))
				m = (Method)i$.next();

			wrapper = (Wrapper)wc.newInstance();
		}
		catch (RuntimeException e)
		{
			throw e;
		}
		catch (Throwable e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}
		finally
		{
			cc.release();
		}
		cc.release();
		ms.clear();
		mns.clear();
		dmns.clear();
		return wrapper;
		ms.clear();
		mns.clear();
		dmns.clear();
		throw exception;
	}

	private static String arg(Class cl, String name)
	{
		if (cl.isPrimitive())
		{
			if (cl == Boolean.TYPE)
				return (new StringBuilder()).append("((Boolean)").append(name).append(").booleanValue()").toString();
			if (cl == Byte.TYPE)
				return (new StringBuilder()).append("((Byte)").append(name).append(").byteValue()").toString();
			if (cl == Character.TYPE)
				return (new StringBuilder()).append("((Character)").append(name).append(").charValue()").toString();
			if (cl == Double.TYPE)
				return (new StringBuilder()).append("((Number)").append(name).append(").doubleValue()").toString();
			if (cl == Float.TYPE)
				return (new StringBuilder()).append("((Number)").append(name).append(").floatValue()").toString();
			if (cl == Integer.TYPE)
				return (new StringBuilder()).append("((Number)").append(name).append(").intValue()").toString();
			if (cl == Long.TYPE)
				return (new StringBuilder()).append("((Number)").append(name).append(").longValue()").toString();
			if (cl == Short.TYPE)
				return (new StringBuilder()).append("((Number)").append(name).append(").shortValue()").toString();
			else
				throw new RuntimeException((new StringBuilder()).append("Unknown primitive type: ").append(cl.getName()).toString());
		} else
		{
			return (new StringBuilder()).append("(").append(ReflectUtils.getName(cl)).append(")").append(name).toString();
		}
	}

	private static String args(Class cs[], String name)
	{
		int len = cs.length;
		if (len == 0)
			return "";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++)
		{
			if (i > 0)
				sb.append(',');
			sb.append(arg(cs[i], (new StringBuilder()).append(name).append("[").append(i).append("]").toString()));
		}

		return sb.toString();
	}

	private static String propertyName(String pn)
	{
		return pn.length() != 1 && !Character.isLowerCase(pn.charAt(1)) ? pn : (new StringBuilder()).append(Character.toLowerCase(pn.charAt(0))).append(pn.substring(1)).toString();
	}

	private static boolean hasMethods(Method methods[])
	{
		if (methods == null || methods.length == 0)
			return false;
		Method arr$[] = methods;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			Method m = arr$[i$];
			if (m.getDeclaringClass() != java/lang/Object)
				return true;
		}

		return false;
	}



}
