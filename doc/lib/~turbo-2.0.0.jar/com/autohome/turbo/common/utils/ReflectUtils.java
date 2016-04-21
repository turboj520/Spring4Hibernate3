// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ReflectUtils.java

package com.autohome.turbo.common.utils;

import java.lang.reflect.*;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javassist.*;

// Referenced classes of package com.autohome.turbo.common.utils:
//			ClassHelper, StringUtils

public final class ReflectUtils
{

	public static final char JVM_VOID = 86;
	public static final char JVM_BOOLEAN = 90;
	public static final char JVM_BYTE = 66;
	public static final char JVM_CHAR = 67;
	public static final char JVM_DOUBLE = 68;
	public static final char JVM_FLOAT = 70;
	public static final char JVM_INT = 73;
	public static final char JVM_LONG = 74;
	public static final char JVM_SHORT = 83;
	public static final Class EMPTY_CLASS_ARRAY[] = new Class[0];
	public static final String JAVA_IDENT_REGEX = "(?:[_$a-zA-Z][_$a-zA-Z0-9]*)";
	public static final String JAVA_NAME_REGEX = "(?:(?:[_$a-zA-Z][_$a-zA-Z0-9]*)(?:\\.(?:[_$a-zA-Z][_$a-zA-Z0-9]*))*)";
	public static final String CLASS_DESC = "(?:L(?:[_$a-zA-Z][_$a-zA-Z0-9]*)(?:\\/(?:[_$a-zA-Z][_$a-zA-Z0-9]*))*;)";
	public static final String ARRAY_DESC = "(?:\\[+(?:(?:[VZBCDFIJS])|(?:L(?:[_$a-zA-Z][_$a-zA-Z0-9]*)(?:\\/(?:[_$a-zA-Z][_$a-zA-Z0-9]*))*;)))";
	public static final String DESC_REGEX = "(?:(?:[VZBCDFIJS])|(?:L(?:[_$a-zA-Z][_$a-zA-Z0-9]*)(?:\\/(?:[_$a-zA-Z][_$a-zA-Z0-9]*))*;)|(?:\\[+(?:(?:[VZBCDFIJS])|(?:L(?:[_$a-zA-Z][_$a-zA-Z0-9]*)(?:\\/(?:[_$a-zA-Z][_$a-zA-Z0-9]*))*;))))";
	public static final Pattern DESC_PATTERN = Pattern.compile("(?:(?:[VZBCDFIJS])|(?:L(?:[_$a-zA-Z][_$a-zA-Z0-9]*)(?:\\/(?:[_$a-zA-Z][_$a-zA-Z0-9]*))*;)|(?:\\[+(?:(?:[VZBCDFIJS])|(?:L(?:[_$a-zA-Z][_$a-zA-Z0-9]*)(?:\\/(?:[_$a-zA-Z][_$a-zA-Z0-9]*))*;))))");
	public static final String METHOD_DESC_REGEX = "(?:((?:[_$a-zA-Z][_$a-zA-Z0-9]*))?\\(((?:(?:[VZBCDFIJS])|(?:L(?:[_$a-zA-Z][_$a-zA-Z0-9]*)(?:\\/(?:[_$a-zA-Z][_$a-zA-Z0-9]*))*;)|(?:\\[+(?:(?:[VZBCDFIJS])|(?:L(?:[_$a-zA-Z][_$a-zA-Z0-9]*)(?:\\/(?:[_$a-zA-Z][_$a-zA-Z0-9]*))*;))))*)\\)((?:(?:[VZBCDFIJS])|(?:L(?:[_$a-zA-Z][_$a-zA-Z0-9]*)(?:\\/(?:[_$a-zA-Z][_$a-zA-Z0-9]*))*;)|(?:\\[+(?:(?:[VZBCDFIJS])|(?:L(?:[_$a-zA-Z][_$a-zA-Z0-9]*)(?:\\/(?:[_$a-zA-Z][_$a-zA-Z0-9]*))*;)))))?)";
	public static final Pattern METHOD_DESC_PATTERN = Pattern.compile("(?:((?:[_$a-zA-Z][_$a-zA-Z0-9]*))?\\(((?:(?:[VZBCDFIJS])|(?:L(?:[_$a-zA-Z][_$a-zA-Z0-9]*)(?:\\/(?:[_$a-zA-Z][_$a-zA-Z0-9]*))*;)|(?:\\[+(?:(?:[VZBCDFIJS])|(?:L(?:[_$a-zA-Z][_$a-zA-Z0-9]*)(?:\\/(?:[_$a-zA-Z][_$a-zA-Z0-9]*))*;))))*)\\)((?:(?:[VZBCDFIJS])|(?:L(?:[_$a-zA-Z][_$a-zA-Z0-9]*)(?:\\/(?:[_$a-zA-Z][_$a-zA-Z0-9]*))*;)|(?:\\[+(?:(?:[VZBCDFIJS])|(?:L(?:[_$a-zA-Z][_$a-zA-Z0-9]*)(?:\\/(?:[_$a-zA-Z][_$a-zA-Z0-9]*))*;)))))?)");
	public static final Pattern GETTER_METHOD_DESC_PATTERN = Pattern.compile("get([A-Z][_a-zA-Z0-9]*)\\(\\)((?:(?:[VZBCDFIJS])|(?:L(?:[_$a-zA-Z][_$a-zA-Z0-9]*)(?:\\/(?:[_$a-zA-Z][_$a-zA-Z0-9]*))*;)|(?:\\[+(?:(?:[VZBCDFIJS])|(?:L(?:[_$a-zA-Z][_$a-zA-Z0-9]*)(?:\\/(?:[_$a-zA-Z][_$a-zA-Z0-9]*))*;)))))");
	public static final Pattern SETTER_METHOD_DESC_PATTERN = Pattern.compile("set([A-Z][_a-zA-Z0-9]*)\\(((?:(?:[VZBCDFIJS])|(?:L(?:[_$a-zA-Z][_$a-zA-Z0-9]*)(?:\\/(?:[_$a-zA-Z][_$a-zA-Z0-9]*))*;)|(?:\\[+(?:(?:[VZBCDFIJS])|(?:L(?:[_$a-zA-Z][_$a-zA-Z0-9]*)(?:\\/(?:[_$a-zA-Z][_$a-zA-Z0-9]*))*;)))))\\)V");
	public static final Pattern IS_HAS_CAN_METHOD_DESC_PATTERN = Pattern.compile("(?:is|has|can)([A-Z][_a-zA-Z0-9]*)\\(\\)Z");
	private static final ConcurrentMap DESC_CLASS_CACHE = new ConcurrentHashMap();
	private static final ConcurrentMap NAME_CLASS_CACHE = new ConcurrentHashMap();
	private static final ConcurrentMap Signature_METHODS_CACHE = new ConcurrentHashMap();

	public static boolean isPrimitives(Class cls)
	{
		if (cls.isArray())
			return isPrimitive(cls.getComponentType());
		else
			return isPrimitive(cls);
	}

	public static boolean isPrimitive(Class cls)
	{
		return cls.isPrimitive() || cls == java/lang/String || cls == java/lang/Boolean || cls == java/lang/Character || java/lang/Number.isAssignableFrom(cls) || java/util/Date.isAssignableFrom(cls);
	}

	public static Class getBoxedClass(Class c)
	{
		if (c == Integer.TYPE)
			c = java/lang/Integer;
		else
		if (c == Boolean.TYPE)
			c = java/lang/Boolean;
		else
		if (c == Long.TYPE)
			c = java/lang/Long;
		else
		if (c == Float.TYPE)
			c = java/lang/Float;
		else
		if (c == Double.TYPE)
			c = java/lang/Double;
		else
		if (c == Character.TYPE)
			c = java/lang/Character;
		else
		if (c == Byte.TYPE)
			c = java/lang/Byte;
		else
		if (c == Short.TYPE)
			c = java/lang/Short;
		return c;
	}

	public static boolean isCompatible(Class c, Object o)
	{
		boolean pt = c.isPrimitive();
		if (o == null)
			return !pt;
		if (pt)
			if (c == Integer.TYPE)
				c = java/lang/Integer;
			else
			if (c == Boolean.TYPE)
				c = java/lang/Boolean;
			else
			if (c == Long.TYPE)
				c = java/lang/Long;
			else
			if (c == Float.TYPE)
				c = java/lang/Float;
			else
			if (c == Double.TYPE)
				c = java/lang/Double;
			else
			if (c == Character.TYPE)
				c = java/lang/Character;
			else
			if (c == Byte.TYPE)
				c = java/lang/Byte;
			else
			if (c == Short.TYPE)
				c = java/lang/Short;
		if (c == o.getClass())
			return true;
		else
			return c.isInstance(o);
	}

	public static boolean isCompatible(Class cs[], Object os[])
	{
		int len = cs.length;
		if (len != os.length)
			return false;
		if (len == 0)
			return true;
		for (int i = 0; i < len; i++)
			if (!isCompatible(cs[i], os[i]))
				return false;

		return true;
	}

	public static String getCodeBase(Class cls)
	{
		if (cls == null)
			return null;
		ProtectionDomain domain = cls.getProtectionDomain();
		if (domain == null)
			return null;
		CodeSource source = domain.getCodeSource();
		if (source == null)
			return null;
		URL location = source.getLocation();
		if (location == null)
			return null;
		else
			return location.getFile();
	}

	public static String getName(Class c)
	{
		if (c.isArray())
		{
			StringBuilder sb = new StringBuilder();
			do
			{
				sb.append("[]");
				c = c.getComponentType();
			} while (c.isArray());
			return (new StringBuilder()).append(c.getName()).append(sb.toString()).toString();
		} else
		{
			return c.getName();
		}
	}

	public static Class getGenericClass(Class cls)
	{
		return getGenericClass(cls, 0);
	}

	public static Class getGenericClass(Class cls, int i)
	{
		Object genericClass;
		ParameterizedType parameterizedType = (ParameterizedType)cls.getGenericInterfaces()[0];
		genericClass = parameterizedType.getActualTypeArguments()[i];
		if (genericClass instanceof ParameterizedType)
			return (Class)((ParameterizedType)genericClass).getRawType();
		if (genericClass instanceof GenericArrayType)
			return (Class)((GenericArrayType)genericClass).getGenericComponentType();
		return (Class)genericClass;
		Throwable e;
		e;
		throw new IllegalArgumentException((new StringBuilder()).append(cls.getName()).append(" generic type undefined!").toString(), e);
	}

	public static String getName(Method m)
	{
		StringBuilder ret = new StringBuilder();
		ret.append(getName(m.getReturnType())).append(' ');
		ret.append(m.getName()).append('(');
		Class parameterTypes[] = m.getParameterTypes();
		for (int i = 0; i < parameterTypes.length; i++)
		{
			if (i > 0)
				ret.append(',');
			ret.append(getName(parameterTypes[i]));
		}

		ret.append(')');
		return ret.toString();
	}

	public static String getSignature(String methodName, Class parameterTypes[])
	{
		StringBuilder sb = new StringBuilder(methodName);
		sb.append("(");
		if (parameterTypes != null && parameterTypes.length > 0)
		{
			boolean first = true;
			Class arr$[] = parameterTypes;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				Class type = arr$[i$];
				if (first)
					first = false;
				else
					sb.append(",");
				sb.append(type.getName());
			}

		}
		sb.append(")");
		return sb.toString();
	}

	public static String getName(Constructor c)
	{
		StringBuilder ret = new StringBuilder("(");
		Class parameterTypes[] = c.getParameterTypes();
		for (int i = 0; i < parameterTypes.length; i++)
		{
			if (i > 0)
				ret.append(',');
			ret.append(getName(parameterTypes[i]));
		}

		ret.append(')');
		return ret.toString();
	}

	public static String getDesc(Class c)
	{
		StringBuilder ret = new StringBuilder();
		for (; c.isArray(); c = c.getComponentType())
			ret.append('[');

		if (c.isPrimitive())
		{
			String t = c.getName();
			if ("void".equals(t))
				ret.append('V');
			else
			if ("boolean".equals(t))
				ret.append('Z');
			else
			if ("byte".equals(t))
				ret.append('B');
			else
			if ("char".equals(t))
				ret.append('C');
			else
			if ("double".equals(t))
				ret.append('D');
			else
			if ("float".equals(t))
				ret.append('F');
			else
			if ("int".equals(t))
				ret.append('I');
			else
			if ("long".equals(t))
				ret.append('J');
			else
			if ("short".equals(t))
				ret.append('S');
		} else
		{
			ret.append('L');
			ret.append(c.getName().replace('.', '/'));
			ret.append(';');
		}
		return ret.toString();
	}

	public static String getDesc(Class cs[])
	{
		if (cs.length == 0)
			return "";
		StringBuilder sb = new StringBuilder(64);
		Class arr$[] = cs;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			Class c = arr$[i$];
			sb.append(getDesc(c));
		}

		return sb.toString();
	}

	public static String getDesc(Method m)
	{
		StringBuilder ret = (new StringBuilder(m.getName())).append('(');
		Class parameterTypes[] = m.getParameterTypes();
		for (int i = 0; i < parameterTypes.length; i++)
			ret.append(getDesc(parameterTypes[i]));

		ret.append(')').append(getDesc(m.getReturnType()));
		return ret.toString();
	}

	public static String getDesc(Constructor c)
	{
		StringBuilder ret = new StringBuilder("(");
		Class parameterTypes[] = c.getParameterTypes();
		for (int i = 0; i < parameterTypes.length; i++)
			ret.append(getDesc(parameterTypes[i]));

		ret.append(')').append('V');
		return ret.toString();
	}

	public static String getDescWithoutMethodName(Method m)
	{
		StringBuilder ret = new StringBuilder();
		ret.append('(');
		Class parameterTypes[] = m.getParameterTypes();
		for (int i = 0; i < parameterTypes.length; i++)
			ret.append(getDesc(parameterTypes[i]));

		ret.append(')').append(getDesc(m.getReturnType()));
		return ret.toString();
	}

	public static String getDesc(CtClass c)
		throws NotFoundException
	{
		StringBuilder ret = new StringBuilder();
		if (c.isArray())
		{
			ret.append('[');
			ret.append(getDesc(c.getComponentType()));
		} else
		if (c.isPrimitive())
		{
			String t = c.getName();
			if ("void".equals(t))
				ret.append('V');
			else
			if ("boolean".equals(t))
				ret.append('Z');
			else
			if ("byte".equals(t))
				ret.append('B');
			else
			if ("char".equals(t))
				ret.append('C');
			else
			if ("double".equals(t))
				ret.append('D');
			else
			if ("float".equals(t))
				ret.append('F');
			else
			if ("int".equals(t))
				ret.append('I');
			else
			if ("long".equals(t))
				ret.append('J');
			else
			if ("short".equals(t))
				ret.append('S');
		} else
		{
			ret.append('L');
			ret.append(c.getName().replace('.', '/'));
			ret.append(';');
		}
		return ret.toString();
	}

	public static String getDesc(CtMethod m)
		throws NotFoundException
	{
		StringBuilder ret = (new StringBuilder(m.getName())).append('(');
		CtClass parameterTypes[] = m.getParameterTypes();
		for (int i = 0; i < parameterTypes.length; i++)
			ret.append(getDesc(parameterTypes[i]));

		ret.append(')').append(getDesc(m.getReturnType()));
		return ret.toString();
	}

	public static String getDesc(CtConstructor c)
		throws NotFoundException
	{
		StringBuilder ret = new StringBuilder("(");
		CtClass parameterTypes[] = c.getParameterTypes();
		for (int i = 0; i < parameterTypes.length; i++)
			ret.append(getDesc(parameterTypes[i]));

		ret.append(')').append('V');
		return ret.toString();
	}

	public static String getDescWithoutMethodName(CtMethod m)
		throws NotFoundException
	{
		StringBuilder ret = new StringBuilder();
		ret.append('(');
		CtClass parameterTypes[] = m.getParameterTypes();
		for (int i = 0; i < parameterTypes.length; i++)
			ret.append(getDesc(parameterTypes[i]));

		ret.append(')').append(getDesc(m.getReturnType()));
		return ret.toString();
	}

	public static String name2desc(String name)
	{
		StringBuilder sb = new StringBuilder();
		int c = 0;
		int index = name.indexOf('[');
		if (index > 0)
		{
			c = (name.length() - index) / 2;
			name = name.substring(0, index);
		}
		while (c-- > 0) 
			sb.append("[");
		if ("void".equals(name))
			sb.append('V');
		else
		if ("boolean".equals(name))
			sb.append('Z');
		else
		if ("byte".equals(name))
			sb.append('B');
		else
		if ("char".equals(name))
			sb.append('C');
		else
		if ("double".equals(name))
			sb.append('D');
		else
		if ("float".equals(name))
			sb.append('F');
		else
		if ("int".equals(name))
			sb.append('I');
		else
		if ("long".equals(name))
			sb.append('J');
		else
		if ("short".equals(name))
			sb.append('S');
		else
			sb.append('L').append(name.replace('.', '/')).append(';');
		return sb.toString();
	}

	public static String desc2name(String desc)
	{
		StringBuilder sb = new StringBuilder();
		int c = desc.lastIndexOf('[') + 1;
		if (desc.length() == c + 1)
			switch (desc.charAt(c))
			{
			case 86: // 'V'
				sb.append("void");
				break;

			case 90: // 'Z'
				sb.append("boolean");
				break;

			case 66: // 'B'
				sb.append("byte");
				break;

			case 67: // 'C'
				sb.append("char");
				break;

			case 68: // 'D'
				sb.append("double");
				break;

			case 70: // 'F'
				sb.append("float");
				break;

			case 73: // 'I'
				sb.append("int");
				break;

			case 74: // 'J'
				sb.append("long");
				break;

			case 83: // 'S'
				sb.append("short");
				break;

			case 69: // 'E'
			case 71: // 'G'
			case 72: // 'H'
			case 75: // 'K'
			case 76: // 'L'
			case 77: // 'M'
			case 78: // 'N'
			case 79: // 'O'
			case 80: // 'P'
			case 81: // 'Q'
			case 82: // 'R'
			case 84: // 'T'
			case 85: // 'U'
			case 87: // 'W'
			case 88: // 'X'
			case 89: // 'Y'
			default:
				throw new RuntimeException();
			}
		else
			sb.append(desc.substring(c + 1, desc.length() - 1).replace('/', '.'));
		while (c-- > 0) 
			sb.append("[]");
		return sb.toString();
	}

	public static Class forName(String name)
	{
		return name2class(name);
		ClassNotFoundException e;
		e;
		throw new IllegalStateException((new StringBuilder()).append("Not found class ").append(name).append(", cause: ").append(e.getMessage()).toString(), e);
	}

	public static Class name2class(String name)
		throws ClassNotFoundException
	{
		return name2class(ClassHelper.getClassLoader(), name);
	}

	private static Class name2class(ClassLoader cl, String name)
		throws ClassNotFoundException
	{
		int c = 0;
		int index = name.indexOf('[');
		if (index > 0)
		{
			c = (name.length() - index) / 2;
			name = name.substring(0, index);
		}
		if (c > 0)
		{
			StringBuilder sb = new StringBuilder();
			while (c-- > 0) 
				sb.append("[");
			if ("void".equals(name))
				sb.append('V');
			else
			if ("boolean".equals(name))
				sb.append('Z');
			else
			if ("byte".equals(name))
				sb.append('B');
			else
			if ("char".equals(name))
				sb.append('C');
			else
			if ("double".equals(name))
				sb.append('D');
			else
			if ("float".equals(name))
				sb.append('F');
			else
			if ("int".equals(name))
				sb.append('I');
			else
			if ("long".equals(name))
				sb.append('J');
			else
			if ("short".equals(name))
				sb.append('S');
			else
				sb.append('L').append(name).append(';');
			name = sb.toString();
		} else
		{
			if ("void".equals(name))
				return Void.TYPE;
			if ("boolean".equals(name))
				return Boolean.TYPE;
			if ("byte".equals(name))
				return Byte.TYPE;
			if ("char".equals(name))
				return Character.TYPE;
			if ("double".equals(name))
				return Double.TYPE;
			if ("float".equals(name))
				return Float.TYPE;
			if ("int".equals(name))
				return Integer.TYPE;
			if ("long".equals(name))
				return Long.TYPE;
			if ("short".equals(name))
				return Short.TYPE;
		}
		if (cl == null)
			cl = ClassHelper.getClassLoader();
		Class clazz = (Class)NAME_CLASS_CACHE.get(name);
		if (clazz == null)
		{
			clazz = Class.forName(name, true, cl);
			NAME_CLASS_CACHE.put(name, clazz);
		}
		return clazz;
	}

	public static Class desc2class(String desc)
		throws ClassNotFoundException
	{
		return desc2class(ClassHelper.getClassLoader(), desc);
	}

	private static Class desc2class(ClassLoader cl, String desc)
		throws ClassNotFoundException
	{
		switch (desc.charAt(0))
		{
		case 86: // 'V'
			return Void.TYPE;

		case 90: // 'Z'
			return Boolean.TYPE;

		case 66: // 'B'
			return Byte.TYPE;

		case 67: // 'C'
			return Character.TYPE;

		case 68: // 'D'
			return Double.TYPE;

		case 70: // 'F'
			return Float.TYPE;

		case 73: // 'I'
			return Integer.TYPE;

		case 74: // 'J'
			return Long.TYPE;

		case 83: // 'S'
			return Short.TYPE;

		case 76: // 'L'
			desc = desc.substring(1, desc.length() - 1).replace('/', '.');
			break;

		case 91: // '['
			desc = desc.replace('/', '.');
			break;

		case 69: // 'E'
		case 71: // 'G'
		case 72: // 'H'
		case 75: // 'K'
		case 77: // 'M'
		case 78: // 'N'
		case 79: // 'O'
		case 80: // 'P'
		case 81: // 'Q'
		case 82: // 'R'
		case 84: // 'T'
		case 85: // 'U'
		case 87: // 'W'
		case 88: // 'X'
		case 89: // 'Y'
		default:
			throw new ClassNotFoundException((new StringBuilder()).append("Class not found: ").append(desc).toString());
		}
		if (cl == null)
			cl = ClassHelper.getClassLoader();
		Class clazz = (Class)DESC_CLASS_CACHE.get(desc);
		if (clazz == null)
		{
			clazz = Class.forName(desc, true, cl);
			DESC_CLASS_CACHE.put(desc, clazz);
		}
		return clazz;
	}

	public static Class[] desc2classArray(String desc)
		throws ClassNotFoundException
	{
		Class ret[] = desc2classArray(ClassHelper.getClassLoader(), desc);
		return ret;
	}

	private static Class[] desc2classArray(ClassLoader cl, String desc)
		throws ClassNotFoundException
	{
		if (desc.length() == 0)
			return EMPTY_CLASS_ARRAY;
		List cs = new ArrayList();
		for (Matcher m = DESC_PATTERN.matcher(desc); m.find(); cs.add(desc2class(cl, m.group())));
		return (Class[])cs.toArray(EMPTY_CLASS_ARRAY);
	}

	public static Method findMethodByMethodSignature(Class clazz, String methodName, String parameterTypes[])
		throws NoSuchMethodException, ClassNotFoundException
	{
		String signature = methodName;
		if (parameterTypes != null && parameterTypes.length > 0)
			signature = (new StringBuilder()).append(methodName).append(StringUtils.join(parameterTypes)).toString();
		Method method = (Method)Signature_METHODS_CACHE.get(signature);
		if (method != null)
			return method;
		if (parameterTypes == null)
		{
			List finded = new ArrayList();
			Method arr$[] = clazz.getMethods();
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				Method m = arr$[i$];
				if (m.getName().equals(methodName))
					finded.add(m);
			}

			if (finded.isEmpty())
				throw new NoSuchMethodException((new StringBuilder()).append("No such method ").append(methodName).append(" in class ").append(clazz).toString());
			if (finded.size() > 1)
			{
				String msg = String.format("Not unique method for method name(%s) in class(%s), find %d methods.", new Object[] {
					methodName, clazz.getName(), Integer.valueOf(finded.size())
				});
				throw new IllegalStateException(msg);
			}
			method = (Method)finded.get(0);
		} else
		{
			Class types[] = new Class[parameterTypes.length];
			for (int i = 0; i < parameterTypes.length; i++)
				types[i] = name2class(parameterTypes[i]);

			method = clazz.getMethod(methodName, types);
		}
		Signature_METHODS_CACHE.put(signature, method);
		return method;
	}

	public static Method findMethodByMethodName(Class clazz, String methodName)
		throws NoSuchMethodException, ClassNotFoundException
	{
		return findMethodByMethodSignature(clazz, methodName, null);
	}

	public static Constructor findConstructor(Class clazz, Class paramType)
		throws NoSuchMethodException
	{
		Constructor targetConstructor;
		try
		{
			targetConstructor = clazz.getConstructor(new Class[] {
				paramType
			});
		}
		catch (NoSuchMethodException e)
		{
			targetConstructor = null;
			Constructor constructors[] = clazz.getConstructors();
			Constructor arr$[] = constructors;
			int len$ = arr$.length;
			int i$ = 0;
			do
			{
				if (i$ >= len$)
					break;
				Constructor constructor = arr$[i$];
				if (Modifier.isPublic(constructor.getModifiers()) && constructor.getParameterTypes().length == 1 && constructor.getParameterTypes()[0].isAssignableFrom(paramType))
				{
					targetConstructor = constructor;
					break;
				}
				i$++;
			} while (true);
			if (targetConstructor == null)
				throw e;
		}
		return targetConstructor;
	}

	public static boolean isInstance(Object obj, String interfaceClazzName)
	{
		for (Class clazz = obj.getClass(); clazz != null && !clazz.equals(java/lang/Object); clazz = clazz.getSuperclass())
		{
			Class interfaces[] = clazz.getInterfaces();
			Class arr$[] = interfaces;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				Class itf = arr$[i$];
				if (itf.getName().equals(interfaceClazzName))
					return true;
			}

		}

		return false;
	}

	public static Object getEmptyObject(Class returnType)
	{
		return getEmptyObject(returnType, ((Map) (new HashMap())), 0);
	}

	private static Object getEmptyObject(Class returnType, Map emptyInstances, int level)
	{
		if (level > 2)
			return null;
		if (returnType == null)
			return null;
		if (returnType == Boolean.TYPE || returnType == java/lang/Boolean)
			return Boolean.valueOf(false);
		if (returnType == Character.TYPE || returnType == java/lang/Character)
			return Character.valueOf('\0');
		if (returnType == Byte.TYPE || returnType == java/lang/Byte)
			return Byte.valueOf((byte)0);
		if (returnType == Short.TYPE || returnType == java/lang/Short)
			return Short.valueOf((short)0);
		if (returnType == Integer.TYPE || returnType == java/lang/Integer)
			return Integer.valueOf(0);
		if (returnType == Long.TYPE || returnType == java/lang/Long)
			return Long.valueOf(0L);
		if (returnType == Float.TYPE || returnType == java/lang/Float)
			return Float.valueOf(0.0F);
		if (returnType == Double.TYPE || returnType == java/lang/Double)
			return Double.valueOf(0.0D);
		if (returnType.isArray())
			return Array.newInstance(returnType.getComponentType(), 0);
		if (returnType.isAssignableFrom(java/util/ArrayList))
			return new ArrayList(0);
		if (returnType.isAssignableFrom(java/util/HashSet))
			return new HashSet(0);
		if (returnType.isAssignableFrom(java/util/HashMap))
			return new HashMap(0);
		if (java/lang/String.equals(returnType))
			return "";
		if (returnType.isInterface())
			break MISSING_BLOCK_LABEL_403;
		Object value;
		value = emptyInstances.get(returnType);
		if (value == null)
		{
			value = returnType.newInstance();
			emptyInstances.put(returnType, value);
		}
		for (Class cls = value.getClass(); cls != null && cls != java/lang/Object; cls = cls.getSuperclass())
		{
			Field fields[] = cls.getDeclaredFields();
			Field arr$[] = fields;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				Field field = arr$[i$];
				Object property = getEmptyObject(field.getType(), emptyInstances, level + 1);
				if (property == null)
					continue;
				try
				{
					if (!field.isAccessible())
						field.setAccessible(true);
					field.set(value, property);
				}
				catch (Throwable e) { }
			}

		}

		return value;
		Throwable e;
		e;
		return null;
		return null;
	}

	public static boolean isBeanPropertyReadMethod(Method method)
	{
		return method != null && Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers()) && method.getReturnType() != Void.TYPE && method.getDeclaringClass() != java/lang/Object && method.getParameterTypes().length == 0 && (method.getName().startsWith("get") && method.getName().length() > 3 || method.getName().startsWith("is") && method.getName().length() > 2);
	}

	public static String getPropertyNameFromBeanReadMethod(Method method)
	{
		if (isBeanPropertyReadMethod(method))
		{
			if (method.getName().startsWith("get"))
				return (new StringBuilder()).append(method.getName().substring(3, 4).toLowerCase()).append(method.getName().substring(4)).toString();
			if (method.getName().startsWith("is"))
				return (new StringBuilder()).append(method.getName().substring(2, 3).toLowerCase()).append(method.getName().substring(3)).toString();
		}
		return null;
	}

	public static boolean isBeanPropertyWriteMethod(Method method)
	{
		return method != null && Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers()) && method.getDeclaringClass() != java/lang/Object && method.getParameterTypes().length == 1 && method.getName().startsWith("set") && method.getName().length() > 3;
	}

	public static String getPropertyNameFromBeanWriteMethod(Method method)
	{
		if (isBeanPropertyWriteMethod(method))
			return (new StringBuilder()).append(method.getName().substring(3, 4).toLowerCase()).append(method.getName().substring(4)).toString();
		else
			return null;
	}

	public static boolean isPublicInstanceField(Field field)
	{
		return Modifier.isPublic(field.getModifiers()) && !Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers()) && !field.isSynthetic();
	}

	public static Map getBeanPropertyFields(Class cl)
	{
		Map properties = new HashMap();
		for (; cl != null; cl = cl.getSuperclass())
		{
			Field fields[] = cl.getDeclaredFields();
			Field arr$[] = fields;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				Field field = arr$[i$];
				if (!Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers()))
				{
					field.setAccessible(true);
					properties.put(field.getName(), field);
				}
			}

		}

		return properties;
	}

	public static Map getBeanPropertyReadMethods(Class cl)
	{
		Map properties = new HashMap();
		for (; cl != null; cl = cl.getSuperclass())
		{
			Method methods[] = cl.getDeclaredMethods();
			Method arr$[] = methods;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				Method method = arr$[i$];
				if (isBeanPropertyReadMethod(method))
				{
					method.setAccessible(true);
					String property = getPropertyNameFromBeanReadMethod(method);
					properties.put(property, method);
				}
			}

		}

		return properties;
	}

	private ReflectUtils()
	{
	}

}
