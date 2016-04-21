// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JavaBeanSerializeUtil.java

package com.autohome.turbo.common.beanutil;

import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.LogHelper;
import com.autohome.turbo.common.utils.ReflectUtils;
import java.lang.reflect.*;
import java.util.*;

// Referenced classes of package com.autohome.turbo.common.beanutil:
//			JavaBeanDescriptor, JavaBeanAccessor

public final class JavaBeanSerializeUtil
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/common/beanutil/JavaBeanSerializeUtil);
	private static final Map TYPES;
	private static final String ARRAY_PREFIX = "[";
	private static final String REFERENCE_TYPE_PREFIX = "L";
	private static final String REFERENCE_TYPE_SUFFIX = ";";

	public static JavaBeanDescriptor serialize(Object obj)
	{
		JavaBeanDescriptor result = serialize(obj, JavaBeanAccessor.FIELD);
		return result;
	}

	public static JavaBeanDescriptor serialize(Object obj, JavaBeanAccessor accessor)
	{
		if (obj == null)
			return null;
		if (obj instanceof JavaBeanDescriptor)
		{
			return (JavaBeanDescriptor)obj;
		} else
		{
			IdentityHashMap cache = new IdentityHashMap();
			JavaBeanDescriptor result = createDescriptorIfAbsent(obj, accessor, cache);
			return result;
		}
	}

	private static JavaBeanDescriptor createDescriptorForSerialize(Class cl)
	{
		if (cl.isEnum())
			return new JavaBeanDescriptor(cl.getName(), 2);
		if (cl.isArray())
			return new JavaBeanDescriptor(cl.getComponentType().getName(), 5);
		if (ReflectUtils.isPrimitive(cl))
			return new JavaBeanDescriptor(cl.getName(), 6);
		if (java/lang/Class.equals(cl))
			return new JavaBeanDescriptor(java/lang/Class.getName(), 1);
		if (java/util/Collection.isAssignableFrom(cl))
			return new JavaBeanDescriptor(cl.getName(), 3);
		if (java/util/Map.isAssignableFrom(cl))
			return new JavaBeanDescriptor(cl.getName(), 4);
		else
			return new JavaBeanDescriptor(cl.getName(), 7);
	}

	private static JavaBeanDescriptor createDescriptorIfAbsent(Object obj, JavaBeanAccessor accessor, IdentityHashMap cache)
	{
		if (cache.containsKey(obj))
			return (JavaBeanDescriptor)cache.get(obj);
		if (obj instanceof JavaBeanDescriptor)
		{
			return (JavaBeanDescriptor)obj;
		} else
		{
			JavaBeanDescriptor result = createDescriptorForSerialize(obj.getClass());
			cache.put(obj, result);
			serializeInternal(result, obj, accessor, cache);
			return result;
		}
	}

	private static void serializeInternal(JavaBeanDescriptor descriptor, Object obj, JavaBeanAccessor accessor, IdentityHashMap cache)
	{
		if (obj == null || descriptor == null)
			return;
		if (!obj.getClass().isEnum()) goto _L2; else goto _L1
_L1:
		descriptor.setEnumNameProperty(((Enum)obj).name());
		  goto _L3
_L2:
		if (!ReflectUtils.isPrimitive(obj.getClass())) goto _L5; else goto _L4
_L4:
		descriptor.setPrimitiveProperty(obj);
		  goto _L3
_L5:
		if (!java/lang/Class.equals(obj.getClass())) goto _L7; else goto _L6
_L6:
		descriptor.setClassNameProperty(((Class)obj).getName());
		  goto _L3
_L7:
		if (!obj.getClass().isArray()) goto _L9; else goto _L8
_L8:
		int len = Array.getLength(obj);
		for (int i = 0; i < len; i++)
		{
			Object item = Array.get(obj, i);
			if (item == null)
			{
				descriptor.setProperty(Integer.valueOf(i), null);
			} else
			{
				JavaBeanDescriptor itemDescriptor = createDescriptorIfAbsent(item, accessor, cache);
				descriptor.setProperty(Integer.valueOf(i), itemDescriptor);
			}
		}

		  goto _L3
_L9:
		if (!(obj instanceof Collection)) goto _L11; else goto _L10
_L10:
		Collection collection = (Collection)obj;
		int index = 0;
		for (Iterator i$ = collection.iterator(); i$.hasNext();)
		{
			Object item = i$.next();
			if (item == null)
			{
				descriptor.setProperty(Integer.valueOf(index++), null);
			} else
			{
				JavaBeanDescriptor itemDescriptor = createDescriptorIfAbsent(item, accessor, cache);
				descriptor.setProperty(Integer.valueOf(index++), itemDescriptor);
			}
		}

		  goto _L3
_L11:
		if (!(obj instanceof Map)) goto _L13; else goto _L12
_L12:
		Iterator i$;
		Object value;
		Map map = (Map)obj;
		Object keyDescriptor;
		Object valueDescriptor;
		for (i$ = map.keySet().iterator(); i$.hasNext(); descriptor.setProperty(keyDescriptor, valueDescriptor))
		{
			Object key = i$.next();
			value = map.get(key);
			keyDescriptor = key != null ? ((Object) (createDescriptorIfAbsent(key, accessor, cache))) : null;
			valueDescriptor = value != null ? ((Object) (createDescriptorIfAbsent(value, accessor, cache))) : null;
		}

		  goto _L3
_L13:
		if (!JavaBeanAccessor.isAccessByMethod(accessor)) goto _L15; else goto _L14
_L14:
		Map methods = ReflectUtils.getBeanPropertyReadMethods(obj.getClass());
		i$ = methods.entrySet().iterator();
_L16:
		java.util.Map.Entry entry;
		if (!i$.hasNext())
			break; /* Loop/switch isn't completed */
		entry = (java.util.Map.Entry)i$.next();
		value = ((Method)entry.getValue()).invoke(obj, new Object[0]);
		if (value != null)
			try
			{
				JavaBeanDescriptor valueDescriptor = createDescriptorIfAbsent(value, accessor, cache);
				descriptor.setProperty(entry.getKey(), valueDescriptor);
			}
			catch (Exception e)
			{
				throw new RuntimeException(e.getMessage(), e);
			}
		if (true) goto _L16; else goto _L15
_L15:
		if (!JavaBeanAccessor.isAccessByField(accessor)) goto _L3; else goto _L17
_L17:
		Map fields = ReflectUtils.getBeanPropertyFields(obj.getClass());
		i$ = fields.entrySet().iterator();
_L18:
		if (!i$.hasNext())
			break; /* Loop/switch isn't completed */
		entry = (java.util.Map.Entry)i$.next();
		if (descriptor.containsProperty(entry.getKey()))
			continue; /* Loop/switch isn't completed */
		e = ((Exception) (((Field)entry.getValue()).get(obj)));
		if (e != null)
			try
			{
				JavaBeanDescriptor valueDescriptor = createDescriptorIfAbsent(e, accessor, cache);
				descriptor.setProperty(entry.getKey(), valueDescriptor);
			}
			catch (Exception e)
			{
				throw new RuntimeException(e.getMessage(), e);
			}
		if (true) goto _L18; else goto _L3
_L3:
	}

	public static Object deserialize(JavaBeanDescriptor beanDescriptor)
	{
		Object result = deserialize(beanDescriptor, Thread.currentThread().getContextClassLoader());
		return result;
	}

	public static Object deserialize(JavaBeanDescriptor beanDescriptor, ClassLoader loader)
	{
		if (beanDescriptor == null)
		{
			return null;
		} else
		{
			IdentityHashMap cache = new IdentityHashMap();
			Object result = instantiateForDeserialize(beanDescriptor, loader, cache);
			deserializeInternal(result, beanDescriptor, loader, cache);
			return result;
		}
	}

	private static void deserializeInternal(Object result, JavaBeanDescriptor beanDescriptor, ClassLoader loader, IdentityHashMap cache)
	{
		if (beanDescriptor.isEnumType() || beanDescriptor.isClassType() || beanDescriptor.isPrimitiveType())
			return;
		if (beanDescriptor.isArrayType())
		{
			int index = 0;
			Object item;
			for (Iterator i$ = beanDescriptor.iterator(); i$.hasNext(); Array.set(result, index++, item))
			{
				java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
				item = entry.getValue();
				if (item instanceof JavaBeanDescriptor)
				{
					JavaBeanDescriptor itemDescriptor = (JavaBeanDescriptor)entry.getValue();
					item = instantiateForDeserialize(itemDescriptor, loader, cache);
					deserializeInternal(item, itemDescriptor, loader, cache);
				}
			}

		} else
		if (beanDescriptor.isCollectionType())
		{
			Collection collection = (Collection)result;
			Object item;
			for (Iterator i$ = beanDescriptor.iterator(); i$.hasNext(); collection.add(item))
			{
				java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
				item = entry.getValue();
				if (item instanceof JavaBeanDescriptor)
				{
					JavaBeanDescriptor itemDescriptor = (JavaBeanDescriptor)entry.getValue();
					item = instantiateForDeserialize(itemDescriptor, loader, cache);
					deserializeInternal(item, itemDescriptor, loader, cache);
				}
			}

		} else
		if (beanDescriptor.isMapType())
		{
			Map map = (Map)result;
			Object key;
			Object value;
			for (Iterator i$ = beanDescriptor.iterator(); i$.hasNext(); map.put(key, value))
			{
				java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
				key = entry.getKey();
				value = entry.getValue();
				if (key != null && (key instanceof JavaBeanDescriptor))
				{
					JavaBeanDescriptor keyDescriptor = (JavaBeanDescriptor)entry.getKey();
					key = instantiateForDeserialize(keyDescriptor, loader, cache);
					deserializeInternal(key, keyDescriptor, loader, cache);
				}
				if (value != null && (value instanceof JavaBeanDescriptor))
				{
					JavaBeanDescriptor valueDescriptor = (JavaBeanDescriptor)entry.getValue();
					value = instantiateForDeserialize(valueDescriptor, loader, cache);
					deserializeInternal(value, valueDescriptor, loader, cache);
				}
			}

		} else
		if (beanDescriptor.isBeanType())
		{
			Iterator i$ = beanDescriptor.iterator();
			do
			{
				if (!i$.hasNext())
					break;
				java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
				String property = entry.getKey().toString();
				Object value = entry.getValue();
				if (value != null)
				{
					if (value instanceof JavaBeanDescriptor)
					{
						JavaBeanDescriptor valueDescriptor = (JavaBeanDescriptor)entry.getValue();
						value = instantiateForDeserialize(valueDescriptor, loader, cache);
						deserializeInternal(value, valueDescriptor, loader, cache);
					}
					Method method = getSetterMethod(result.getClass(), property, value.getClass());
					boolean setByMethod = false;
					try
					{
						if (method != null)
						{
							method.invoke(result, new Object[] {
								value
							});
							setByMethod = true;
						}
					}
					catch (Exception e)
					{
						LogHelper.warn(logger, (new StringBuilder()).append("Failed to set property through method ").append(method).toString(), e);
					}
					if (!setByMethod)
						try
						{
							Field field = result.getClass().getField(property);
							if (field != null)
								field.set(result, value);
						}
						catch (NoSuchFieldException e1)
						{
							LogHelper.warn(logger, "Failed to set field value", e1);
						}
						catch (IllegalAccessException e1)
						{
							LogHelper.warn(logger, "Failed to set field value", e1);
						}
				}
			} while (true);
		} else
		{
			throw new IllegalArgumentException((new StringBuilder()).append("Unsupported type ").append(beanDescriptor.getClassName()).append(":").append(beanDescriptor.getType()).toString());
		}
	}

	private static Method getSetterMethod(Class cls, String property, Class valueCls)
	{
		String name = (new StringBuilder()).append("set").append(property.substring(0, 1).toUpperCase()).append(property.substring(1)).toString();
		Method method = null;
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
			method.setAccessible(true);
		return method;
	}

	private static Object instantiate(Class cl)
		throws Exception
	{
		Constructor constructor;
		Object constructorArgs[];
		Constructor constructors[] = cl.getDeclaredConstructors();
		constructor = null;
		int argc = 0x7fffffff;
		Constructor arr$[] = constructors;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			Constructor c = arr$[i$];
			if (c.getParameterTypes().length < argc)
			{
				argc = c.getParameterTypes().length;
				constructor = c;
			}
		}

		if (constructor == null)
			break MISSING_BLOCK_LABEL_172;
		Class paramTypes[] = constructor.getParameterTypes();
		constructorArgs = new Object[paramTypes.length];
		for (int i = 0; i < constructorArgs.length; i++)
			constructorArgs[i] = getConstructorArg(paramTypes[i]);

		constructor.setAccessible(true);
		return constructor.newInstance(constructorArgs);
		InstantiationException e;
		e;
		LogHelper.warn(logger, e.getMessage(), e);
		break MISSING_BLOCK_LABEL_172;
		e;
		LogHelper.warn(logger, e.getMessage(), e);
		break MISSING_BLOCK_LABEL_172;
		e;
		LogHelper.warn(logger, e.getMessage(), e);
		return cl.newInstance();
	}

	private static Object getConstructorArg(Class cl)
	{
		if (Boolean.TYPE.equals(cl) || java/lang/Boolean.equals(cl))
			return Boolean.FALSE;
		if (Byte.TYPE.equals(cl) || java/lang/Byte.equals(cl))
			return Byte.valueOf((byte)0);
		if (Short.TYPE.equals(cl) || java/lang/Short.equals(cl))
			return Short.valueOf((short)0);
		if (Integer.TYPE.equals(cl) || java/lang/Integer.equals(cl))
			return Integer.valueOf(0);
		if (Long.TYPE.equals(cl) || java/lang/Long.equals(cl))
			return Long.valueOf(0L);
		if (Float.TYPE.equals(cl) || java/lang/Float.equals(cl))
			return Float.valueOf(0.0F);
		if (Double.TYPE.equals(cl) || java/lang/Double.equals(cl))
			return Double.valueOf(0.0D);
		if (Character.TYPE.equals(cl) || java/lang/Character.equals(cl))
			return new Character('\0');
		else
			return null;
	}

	private static Object instantiateForDeserialize(JavaBeanDescriptor beanDescriptor, ClassLoader loader, IdentityHashMap cache)
	{
		Object result;
		if (cache.containsKey(beanDescriptor))
			return cache.get(beanDescriptor);
		result = null;
		if (!beanDescriptor.isClassType())
			break MISSING_BLOCK_LABEL_51;
		result = name2Class(loader, beanDescriptor.getClassNameProperty());
		return result;
		ClassNotFoundException e;
		e;
		throw new RuntimeException(e.getMessage(), e);
		if (!beanDescriptor.isEnumType())
			break MISSING_BLOCK_LABEL_117;
		Class enumType = name2Class(loader, beanDescriptor.getClassName());
		Method method = getEnumValueOfMethod(enumType);
		result = method.invoke(null, new Object[] {
			enumType, beanDescriptor.getEnumPropertyName()
		});
		return result;
		enumType;
		throw new RuntimeException(enumType.getMessage(), enumType);
		if (beanDescriptor.isPrimitiveType())
		{
			result = beanDescriptor.getPrimitiveProperty();
			return result;
		}
		if (beanDescriptor.isArrayType())
		{
			Class componentType;
			try
			{
				componentType = name2Class(loader, beanDescriptor.getClassName());
			}
			catch (ClassNotFoundException e)
			{
				throw new RuntimeException(e.getMessage(), e);
			}
			result = Array.newInstance(componentType, beanDescriptor.propertySize());
			cache.put(beanDescriptor, result);
		} else
		{
			try
			{
				Class cl = name2Class(loader, beanDescriptor.getClassName());
				result = instantiate(cl);
				cache.put(beanDescriptor, result);
			}
			// Misplaced declaration of an exception variable
			catch (Class cl)
			{
				throw new RuntimeException(cl.getMessage(), cl);
			}
			// Misplaced declaration of an exception variable
			catch (Class cl)
			{
				throw new RuntimeException(cl.getMessage(), cl);
			}
		}
		return result;
	}

	public static Class name2Class(ClassLoader loader, String name)
		throws ClassNotFoundException
	{
		if (TYPES.containsKey(name))
			return (Class)TYPES.get(name);
		if (isArray(name))
		{
			int dimension = 0;
			for (; isArray(name); name = name.substring(1))
				dimension++;

			Class type = name2Class(loader, name);
			int dimensions[] = new int[dimension];
			for (int i = 0; i < dimension; i++)
				dimensions[i] = 0;

			return Array.newInstance(type, dimensions).getClass();
		}
		if (isReferenceType(name))
			name = name.substring(1, name.length() - 1);
		return Class.forName(name, false, loader);
	}

	private static boolean isArray(String type)
	{
		return type != null && type.startsWith("[");
	}

	private static boolean isReferenceType(String type)
	{
		return type != null && type.startsWith("L") && type.endsWith(";");
	}

	private static Method getEnumValueOfMethod(Class cl)
		throws NoSuchMethodException
	{
		return cl.getMethod("valueOf", new Class[] {
			java/lang/Class, java/lang/String
		});
	}

	private JavaBeanSerializeUtil()
	{
	}

	static 
	{
		TYPES = new HashMap();
		TYPES.put(Boolean.TYPE.getName(), Boolean.TYPE);
		TYPES.put(Byte.TYPE.getName(), Byte.TYPE);
		TYPES.put(Short.TYPE.getName(), Short.TYPE);
		TYPES.put(Integer.TYPE.getName(), Integer.TYPE);
		TYPES.put(Long.TYPE.getName(), Long.TYPE);
		TYPES.put(Float.TYPE.getName(), Float.TYPE);
		TYPES.put(Double.TYPE.getName(), Double.TYPE);
		TYPES.put(Void.TYPE.getName(), Void.TYPE);
		TYPES.put("Z", Boolean.TYPE);
		TYPES.put("B", Byte.TYPE);
		TYPES.put("C", Character.TYPE);
		TYPES.put("D", Double.TYPE);
		TYPES.put("F", Float.TYPE);
		TYPES.put("I", Integer.TYPE);
		TYPES.put("J", Long.TYPE);
		TYPES.put("S", Short.TYPE);
	}
}
