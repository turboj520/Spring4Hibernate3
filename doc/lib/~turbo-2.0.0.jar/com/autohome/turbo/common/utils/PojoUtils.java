// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   PojoUtils.java

package com.autohome.turbo.common.utils;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;

// Referenced classes of package com.autohome.turbo.common.utils:
//			ReflectUtils, CompatibleTypeUtils, ClassHelper

public class PojoUtils
{
	private static class PojoInvocationHandler
		implements InvocationHandler
	{

		private Map map;

		public Object invoke(Object proxy, Method method, Object args[])
			throws Throwable
		{
			if (method.getDeclaringClass() == java/lang/Object)
				return method.invoke(map, args);
			String methodName = method.getName();
			Object value = null;
			if (methodName.length() > 3 && methodName.startsWith("get"))
				value = map.get((new StringBuilder()).append(methodName.substring(3, 4).toLowerCase()).append(methodName.substring(4)).toString());
			else
			if (methodName.length() > 2 && methodName.startsWith("is"))
				value = map.get((new StringBuilder()).append(methodName.substring(2, 3).toLowerCase()).append(methodName.substring(3)).toString());
			else
				value = map.get((new StringBuilder()).append(methodName.substring(0, 1).toLowerCase()).append(methodName.substring(1)).toString());
			if ((value instanceof Map) && !java/util/Map.isAssignableFrom(method.getReturnType()))
				value = PojoUtils.realize0((Map)value, method.getReturnType(), null, new IdentityHashMap());
			return value;
		}

		public PojoInvocationHandler(Map map)
		{
			this.map = map;
		}
	}


	private static final ConcurrentMap NAME_METHODS_CACHE = new ConcurrentHashMap();
	private static final ConcurrentMap CLASS_FIELD_CACHE = new ConcurrentHashMap();

	public PojoUtils()
	{
	}

	public static Object[] generalize(Object objs[])
	{
		Object dests[] = new Object[objs.length];
		for (int i = 0; i < objs.length; i++)
			dests[i] = generalize(objs[i]);

		return dests;
	}

	public static Object[] realize(Object objs[], Class types[])
	{
		if (objs.length != types.length)
			throw new IllegalArgumentException("args.length != types.length");
		Object dests[] = new Object[objs.length];
		for (int i = 0; i < objs.length; i++)
			dests[i] = realize(objs[i], types[i]);

		return dests;
	}

	public static Object[] realize(Object objs[], Class types[], Type gtypes[])
	{
		if (objs.length != types.length || objs.length != gtypes.length)
			throw new IllegalArgumentException("args.length != types.length");
		Object dests[] = new Object[objs.length];
		for (int i = 0; i < objs.length; i++)
			dests[i] = realize(objs[i], types[i], gtypes[i]);

		return dests;
	}

	public static Object generalize(Object pojo)
	{
		return generalize(pojo, ((Map) (new IdentityHashMap())));
	}

	private static Object generalize(Object pojo, Map history)
	{
		Map map;
		Method arr$[];
		int len$;
		int i$;
		if (pojo == null)
			return null;
		if (pojo instanceof Enum)
			return ((Enum)pojo).name();
		if (pojo.getClass().isArray() && java/lang/Enum.isAssignableFrom(pojo.getClass().getComponentType()))
		{
			int len = Array.getLength(pojo);
			String values[] = new String[len];
			for (int i = 0; i < len; i++)
				values[i] = ((Enum)Array.get(pojo, i)).name();

			return values;
		}
		if (ReflectUtils.isPrimitives(pojo.getClass()))
			return pojo;
		if (pojo instanceof Class)
			return ((Class)pojo).getName();
		Object o = history.get(pojo);
		if (o != null)
			return o;
		history.put(pojo, pojo);
		if (pojo.getClass().isArray())
		{
			int len = Array.getLength(pojo);
			Object dest[] = new Object[len];
			history.put(pojo, ((Object) (dest)));
			for (int i = 0; i < len; i++)
			{
				Object obj = Array.get(pojo, i);
				dest[i] = generalize(obj, history);
			}

			return ((Object) (dest));
		}
		if (pojo instanceof Collection)
		{
			Collection src = (Collection)pojo;
			int len = src.size();
			Collection dest = ((Collection) ((pojo instanceof List) ? ((Collection) (new ArrayList(len))) : ((Collection) (new HashSet(len)))));
			history.put(pojo, dest);
			Object obj;
			for (Iterator i$ = src.iterator(); i$.hasNext(); dest.add(generalize(obj, history)))
				obj = i$.next();

			return dest;
		}
		if (pojo instanceof Map)
		{
			Map src = (Map)pojo;
			Map dest = createMap(src);
			history.put(pojo, dest);
			java.util.Map.Entry obj;
			for (Iterator i$ = src.entrySet().iterator(); i$.hasNext(); dest.put(generalize(obj.getKey(), history), generalize(obj.getValue(), history)))
				obj = (java.util.Map.Entry)i$.next();

			return dest;
		}
		map = new HashMap();
		history.put(pojo, map);
		map.put("class", pojo.getClass().getName());
		arr$ = pojo.getClass().getMethods();
		len$ = arr$.length;
		for (i$ = 0; i$ < len$; i$++)
		{
			Method method = arr$[i$];
			if (!ReflectUtils.isBeanPropertyReadMethod(method))
				continue;
			try
			{
				map.put(ReflectUtils.getPropertyNameFromBeanReadMethod(method), generalize(method.invoke(pojo, new Object[0]), history));
			}
			catch (Exception e)
			{
				throw new RuntimeException(e.getMessage(), e);
			}
		}

		arr$ = pojo.getClass().getFields();
		len$ = arr$.length;
		i$ = 0;
_L3:
		if (i$ >= len$) goto _L2; else goto _L1
_L1:
		Field field = arr$[i$];
		if (!ReflectUtils.isPublicInstanceField(field))
			continue; /* Loop/switch isn't completed */
		Object fieldValue;
		try
		{
			fieldValue = field.get(pojo);
			if (history.containsKey(pojo))
			{
				Object pojoGenerilizedValue = history.get(pojo);
				if ((pojoGenerilizedValue instanceof Map) && ((Map)pojoGenerilizedValue).containsKey(field.getName()))
					continue; /* Loop/switch isn't completed */
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}
		if (fieldValue != null)
			map.put(field.getName(), generalize(fieldValue, history));
		i$++;
		  goto _L3
_L2:
		return map;
	}

	public static Object realize(Object pojo, Class type)
	{
		return realize0(pojo, type, null, new IdentityHashMap());
	}

	public static Object realize(Object pojo, Class type, Type genericType)
	{
		return realize0(pojo, type, genericType, new IdentityHashMap());
	}

	private static Collection createCollection(Class type, int len)
	{
		if (type.isAssignableFrom(java/util/ArrayList))
			return new ArrayList(len);
		if (type.isAssignableFrom(java/util/HashSet))
			return new HashSet(len);
		if (type.isInterface() || Modifier.isAbstract(type.getModifiers()))
			break MISSING_BLOCK_LABEL_64;
		return (Collection)type.newInstance();
		Exception e;
		e;
		return new ArrayList();
	}

	private static Map createMap(Map src)
	{
		Class cl = src.getClass();
		Map result = null;
		if (java/util/HashMap == cl)
			result = new HashMap();
		else
		if (java/util/Hashtable == cl)
			result = new Hashtable();
		else
		if (java/util/IdentityHashMap == cl)
			result = new IdentityHashMap();
		else
		if (java/util/LinkedHashMap == cl)
			result = new LinkedHashMap();
		else
		if (java/util/Properties == cl)
			result = new Properties();
		else
		if (java/util/TreeMap == cl)
		{
			result = new TreeMap();
		} else
		{
			if (java/util/WeakHashMap == cl)
				return new WeakHashMap();
			if (java/util/concurrent/ConcurrentHashMap == cl)
				result = new ConcurrentHashMap();
			else
			if (java/util/concurrent/ConcurrentSkipListMap == cl)
			{
				result = new ConcurrentSkipListMap();
			} else
			{
				try
				{
					result = (Map)cl.newInstance();
				}
				catch (Exception e) { }
				if (result == null)
					try
					{
						Constructor constructor = cl.getConstructor(new Class[] {
							java/util/Map
						});
						result = (Map)constructor.newInstance(new Object[] {
							Collections.EMPTY_MAP
						});
					}
					catch (Exception e) { }
			}
		}
		if (result == null)
			result = new HashMap();
		return result;
	}

	private static Object realize0(Object pojo, Class type, Type genericType, Map history)
	{
		if (pojo == null)
			return null;
		if (type != null && type.isEnum() && pojo.getClass() == java/lang/String)
			return Enum.valueOf(type, (String)pojo);
		if (ReflectUtils.isPrimitives(pojo.getClass()) && (type == null || !type.isArray() || !type.getComponentType().isEnum() || pojo.getClass() != [Ljava/lang/String;))
			return CompatibleTypeUtils.compatibleTypeConvert(pojo, type);
		Object o = history.get(pojo);
		if (o != null)
			return o;
		history.put(pojo, pojo);
		if (pojo.getClass().isArray())
		{
			Class ctype;
			int len;
			Object dest;
			if (java/util/Collection.isAssignableFrom(type))
			{
				ctype = pojo.getClass().getComponentType();
				len = Array.getLength(pojo);
				dest = createCollection(type, len);
				history.put(pojo, dest);
				for (int i = 0; i < len; i++)
				{
					Object obj = Array.get(pojo, i);
					Object value = realize0(obj, ctype, null, history);
					((Collection) (dest)).add(value);
				}

				return dest;
			}
			ctype = type == null || !type.isArray() ? pojo.getClass().getComponentType() : type.getComponentType();
			len = Array.getLength(pojo);
			dest = Array.newInstance(ctype, len);
			history.put(pojo, dest);
			for (int i = 0; i < len; i++)
			{
				Object obj = Array.get(pojo, i);
				Object value = realize0(obj, ctype, null, history);
				Array.set(dest, i, value);
			}

			return dest;
		}
		if (pojo instanceof Collection)
		{
			if (type.isArray())
			{
				Class ctype = type.getComponentType();
				Collection src = (Collection)pojo;
				int len = src.size();
				Object dest = Array.newInstance(ctype, len);
				history.put(pojo, dest);
				int i = 0;
				for (Iterator i$ = src.iterator(); i$.hasNext();)
				{
					Object obj = i$.next();
					Object value = realize0(obj, ctype, null, history);
					Array.set(dest, i, value);
					i++;
				}

				return dest;
			}
			Collection src = (Collection)pojo;
			int len = src.size();
			Collection dest = createCollection(type, len);
			history.put(pojo, dest);
			Object value;
			for (Iterator i$ = src.iterator(); i$.hasNext(); dest.add(value))
			{
				Object obj = i$.next();
				Type keyType = getGenericClassByIndex(genericType, 0);
				Class keyClazz = obj.getClass();
				if (keyType instanceof Class)
					keyClazz = (Class)keyType;
				value = realize0(obj, keyClazz, keyType, history);
			}

			return dest;
		}
		if ((pojo instanceof Map) && type != null)
		{
			Object className = ((Map)pojo).get("class");
			if (className instanceof String)
				try
				{
					type = ClassHelper.forName((String)className);
				}
				catch (ClassNotFoundException e) { }
			Map map;
			if (!type.isInterface() && !type.isAssignableFrom(pojo.getClass()))
				try
				{
					map = (Map)type.newInstance();
				}
				catch (Exception e)
				{
					map = (Map)pojo;
				}
			else
				map = (Map)pojo;
			Iterator i$;
			if (java/util/Map.isAssignableFrom(type) || type == java/lang/Object)
			{
				Map result = createMap(map);
				history.put(pojo, result);
				Object key;
				Object value;
				for (i$ = map.entrySet().iterator(); i$.hasNext(); result.put(key, value))
				{
					java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
					Type keyType = getGenericClassByIndex(genericType, 0);
					Type valueType = getGenericClassByIndex(genericType, 1);
					Class keyClazz;
					if (keyType instanceof Class)
						keyClazz = (Class)keyType;
					else
						keyClazz = entry.getKey() != null ? entry.getKey().getClass() : null;
					Class valueClazz;
					if (valueType instanceof Class)
						valueClazz = (Class)valueType;
					else
						valueClazz = entry.getValue() != null ? entry.getValue().getClass() : null;
					key = keyClazz != null ? realize0(entry.getKey(), keyClazz, keyType, history) : entry.getKey();
					value = valueClazz != null ? realize0(entry.getValue(), valueClazz, valueType, history) : entry.getValue();
				}

				return result;
			}
			Object dest;
			if (type.isInterface())
			{
				dest = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] {
					type
				}, new PojoInvocationHandler(map));
				history.put(pojo, dest);
				return dest;
			}
			dest = newInstance(type);
			history.put(pojo, dest);
			i$ = map.entrySet().iterator();
			do
			{
				if (!i$.hasNext())
					break;
				java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
				Object key = entry.getKey();
				if (key instanceof String)
				{
					String name = (String)key;
					Object value = entry.getValue();
					if (value != null)
					{
						Method method = getSetterMethod(dest.getClass(), name, value.getClass());
						Field field = getField(dest.getClass(), name);
						if (method != null)
						{
							if (!method.isAccessible())
								method.setAccessible(true);
							Type ptype = method.getGenericParameterTypes()[0];
							value = realize0(value, method.getParameterTypes()[0], ptype, history);
							try
							{
								method.invoke(dest, new Object[] {
									value
								});
							}
							catch (Exception e)
							{
								e.printStackTrace();
								throw new RuntimeException((new StringBuilder()).append("Failed to set pojo ").append(dest.getClass().getSimpleName()).append(" property ").append(name).append(" value ").append(value).append("(").append(value.getClass()).append("), cause: ").append(e.getMessage()).toString(), e);
							}
						} else
						if (field != null)
						{
							value = realize0(value, field.getType(), field.getGenericType(), history);
							try
							{
								field.set(dest, value);
							}
							catch (IllegalAccessException e)
							{
								throw new RuntimeException((new StringBuilder(32)).append("Failed to set filed ").append(name).append(" of pojo ").append(dest.getClass().getName()).append(" : ").append(e.getMessage()).toString(), e);
							}
						}
					}
				}
			} while (true);
			if (dest instanceof Throwable)
			{
				Object message = map.get("message");
				if (message instanceof String)
					try
					{
						Field filed = java/lang/Throwable.getDeclaredField("detailMessage");
						if (!filed.isAccessible())
							filed.setAccessible(true);
						filed.set(dest, (String)message);
					}
					catch (Exception e) { }
			}
			return dest;
		} else
		{
			return pojo;
		}
	}

	private static Type getGenericClassByIndex(Type genericType, int index)
	{
		Type clazz = null;
		if (genericType instanceof ParameterizedType)
		{
			ParameterizedType t = (ParameterizedType)genericType;
			Type types[] = t.getActualTypeArguments();
			clazz = types[index];
		}
		return clazz;
	}

	private static Object newInstance(Class cls)
	{
		return cls.newInstance();
		Throwable t;
		t;
		Constructor constructor;
		Constructor constructors[] = cls.getConstructors();
		if (constructors != null && constructors.length == 0)
			throw new RuntimeException((new StringBuilder()).append("Illegal constructor: ").append(cls.getName()).toString());
		constructor = constructors[0];
		if (constructor.getParameterTypes().length > 0)
		{
			Constructor arr$[] = constructors;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				Constructor c = arr$[i$];
				if (c.getParameterTypes().length >= constructor.getParameterTypes().length)
					continue;
				constructor = c;
				if (constructor.getParameterTypes().length == 0)
					break;
			}

		}
		return constructor.newInstance(new Object[constructor.getParameterTypes().length]);
		InstantiationException e;
		e;
		throw new RuntimeException(e.getMessage(), e);
		e;
		throw new RuntimeException(e.getMessage(), e);
		e;
		throw new RuntimeException(e.getMessage(), e);
	}

	private static Method getSetterMethod(Class cls, String property, Class valueCls)
	{
		String name = (new StringBuilder()).append("set").append(property.substring(0, 1).toUpperCase()).append(property.substring(1)).toString();
		Method method = (Method)NAME_METHODS_CACHE.get((new StringBuilder()).append(cls.getName()).append(".").append(name).append("(").append(valueCls.getName()).append(")").toString());
		if (method == null)
		{
			try
			{
				method = cls.getMethod(name, new Class[] {
					valueCls
				});
			}
			catch (NoSuchMethodException e)
			{
				Method arr$[] = cls.getMethods();
				int len$ = arr$.length;
				for (int i$ = 0; i$ < len$; i$++)
				{
					Method m = arr$[i$];
					if (ReflectUtils.isBeanPropertyWriteMethod(m) && m.getName().equals(name))
						method = m;
				}

			}
			if (method != null)
				NAME_METHODS_CACHE.put((new StringBuilder()).append(cls.getName()).append(".").append(name).append("(").append(valueCls.getName()).append(")").toString(), method);
		}
		return method;
	}

	private static Field getField(Class cls, String fieldName)
	{
		Field result = null;
		if (CLASS_FIELD_CACHE.containsKey(cls) && ((ConcurrentMap)CLASS_FIELD_CACHE.get(cls)).containsKey(fieldName))
			return (Field)((ConcurrentMap)CLASS_FIELD_CACHE.get(cls)).get(fieldName);
		try
		{
			result = cls.getField(fieldName);
		}
		catch (NoSuchFieldException e)
		{
			Field arr$[] = cls.getFields();
			int len$ = arr$.length;
			int i$ = 0;
			do
			{
				if (i$ >= len$)
					break;
				Field field = arr$[i$];
				if (fieldName.equals(field.getName()) && ReflectUtils.isPublicInstanceField(field))
				{
					result = field;
					break;
				}
				i$++;
			} while (true);
		}
		if (result != null)
		{
			ConcurrentMap fields = (ConcurrentMap)CLASS_FIELD_CACHE.get(cls);
			if (fields == null)
			{
				fields = new ConcurrentHashMap();
				CLASS_FIELD_CACHE.putIfAbsent(cls, fields);
			}
			fields = (ConcurrentMap)CLASS_FIELD_CACHE.get(cls);
			fields.putIfAbsent(fieldName, result);
		}
		return result;
	}

	public static boolean isPojo(Class cls)
	{
		return !ReflectUtils.isPrimitives(cls) && !java/util/Collection.isAssignableFrom(cls) && !java/util/Map.isAssignableFrom(cls);
	}


}
