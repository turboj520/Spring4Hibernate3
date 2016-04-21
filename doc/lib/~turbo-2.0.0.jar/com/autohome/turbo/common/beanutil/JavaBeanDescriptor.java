// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JavaBeanDescriptor.java

package com.autohome.turbo.common.beanutil;

import java.io.Serializable;
import java.util.*;

public final class JavaBeanDescriptor
	implements Serializable, Iterable
{

	private static final long serialVersionUID = 0x89f61628606a57f3L;
	public static final int TYPE_CLASS = 1;
	public static final int TYPE_ENUM = 2;
	public static final int TYPE_COLLECTION = 3;
	public static final int TYPE_MAP = 4;
	public static final int TYPE_ARRAY = 5;
	public static final int TYPE_PRIMITIVE = 6;
	public static final int TYPE_BEAN = 7;
	private static final String ENUM_PROPERTY_NAME = "name";
	private static final String CLASS_PROPERTY_NAME = "name";
	private static final String PRIMITIVE_PROPERTY_VALUE = "value";
	private static final int TYPE_MAX = 7;
	private static final int TYPE_MIN = 1;
	private String className;
	private int type;
	private Map properties;

	public JavaBeanDescriptor()
	{
		properties = new LinkedHashMap();
	}

	public JavaBeanDescriptor(String className, int type)
	{
		properties = new LinkedHashMap();
		notEmpty(className, "class name is empty");
		if (!isValidType(type))
		{
			throw new IllegalArgumentException((new StringBuilder(16)).append("type [ ").append(type).append(" ] is unsupported").toString());
		} else
		{
			this.className = className;
			this.type = type;
			return;
		}
	}

	public void setClassName(String className)
	{
		this.className = className;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public boolean isClassType()
	{
		return 1 == type;
	}

	public boolean isEnumType()
	{
		return 2 == type;
	}

	public boolean isCollectionType()
	{
		return 3 == type;
	}

	public boolean isMapType()
	{
		return 4 == type;
	}

	public boolean isArrayType()
	{
		return 5 == type;
	}

	public boolean isPrimitiveType()
	{
		return 6 == type;
	}

	public boolean isBeanType()
	{
		return 7 == type;
	}

	public int getType()
	{
		return type;
	}

	public String getClassName()
	{
		return className;
	}

	public Object setProperty(Object propertyName, Object propertyValue)
	{
		notNull(propertyName, "Property name is null");
		Object oldValue = properties.put(propertyName, propertyValue);
		return oldValue;
	}

	public String setEnumNameProperty(String name)
	{
		if (isEnumType())
		{
			Object result = setProperty("name", name);
			return result != null ? result.toString() : null;
		} else
		{
			throw new IllegalStateException("The instance is not a enum wrapper");
		}
	}

	public String getEnumPropertyName()
	{
		if (isEnumType())
		{
			Object result = getProperty("name").toString();
			return result != null ? result.toString() : null;
		} else
		{
			throw new IllegalStateException("The instance is not a enum wrapper");
		}
	}

	public String setClassNameProperty(String name)
	{
		if (isClassType())
		{
			Object result = setProperty("name", name);
			return result != null ? result.toString() : null;
		} else
		{
			throw new IllegalStateException("The instance is not a class wrapper");
		}
	}

	public String getClassNameProperty()
	{
		if (isClassType())
		{
			Object result = getProperty("name");
			return result != null ? result.toString() : null;
		} else
		{
			throw new IllegalStateException("The instance is not a class wrapper");
		}
	}

	public Object setPrimitiveProperty(Object primitiveValue)
	{
		if (isPrimitiveType())
			return setProperty("value", primitiveValue);
		else
			throw new IllegalStateException("The instance is not a primitive type wrapper");
	}

	public Object getPrimitiveProperty()
	{
		if (isPrimitiveType())
			return getProperty("value");
		else
			throw new IllegalStateException("The instance is not a primitive type wrapper");
	}

	public Object getProperty(Object propertyName)
	{
		notNull(propertyName, "Property name is null");
		Object propertyValue = properties.get(propertyName);
		return propertyValue;
	}

	public boolean containsProperty(Object propertyName)
	{
		notNull(propertyName, "Property name is null");
		return properties.containsKey(propertyName);
	}

	public Iterator iterator()
	{
		return properties.entrySet().iterator();
	}

	public int propertySize()
	{
		return properties.size();
	}

	private boolean isValidType(int type)
	{
		return 1 <= type && type <= 7;
	}

	private void notNull(Object obj, String message)
	{
		if (obj == null)
			throw new IllegalArgumentException(message);
		else
			return;
	}

	private void notEmpty(String string, String message)
	{
		if (isEmpty(string))
			throw new IllegalArgumentException(message);
		else
			return;
	}

	private boolean isEmpty(String string)
	{
		return string == null || "".equals(string.trim());
	}
}
