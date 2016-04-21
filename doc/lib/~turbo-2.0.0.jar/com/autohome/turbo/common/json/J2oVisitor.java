// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   J2oVisitor.java

package com.autohome.turbo.common.json;

import com.autohome.turbo.common.bytecode.Wrapper;
import com.autohome.turbo.common.utils.Stack;
import com.autohome.turbo.common.utils.StringUtils;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

// Referenced classes of package com.autohome.turbo.common.json:
//			ParseException, JSONVisitor, JSONConverter

class J2oVisitor
	implements JSONVisitor
{

	public static final boolean EMPTY_BOOL_ARRAY[] = new boolean[0];
	public static final byte EMPTY_BYTE_ARRAY[] = new byte[0];
	public static final char EMPTY_CHAR_ARRAY[] = new char[0];
	public static final short EMPTY_SHORT_ARRAY[] = new short[0];
	public static final int EMPTY_INT_ARRAY[] = new int[0];
	public static final long EMPTY_LONG_ARRAY[] = new long[0];
	public static final float EMPTY_FLOAT_ARRAY[] = new float[0];
	public static final double EMPTY_DOUBLE_ARRAY[] = new double[0];
	public static final String EMPTY_STRING_ARRAY[] = new String[0];
	private Class mTypes[];
	private Class mType;
	private Object mValue;
	private Wrapper mWrapper;
	private JSONConverter mConverter;
	private Stack mStack;

	J2oVisitor(Class type, JSONConverter jc)
	{
		mType = [Ljava/lang/Object;;
		mStack = new Stack();
		mType = type;
		mConverter = jc;
	}

	J2oVisitor(Class types[], JSONConverter jc)
	{
		mType = [Ljava/lang/Object;;
		mStack = new Stack();
		mTypes = types;
		mConverter = jc;
	}

	public void begin()
	{
	}

	public Object end(Object obj, boolean isValue)
		throws ParseException
	{
		mStack.clear();
		return mConverter.readValue(mType, obj);
		IOException e;
		e;
		throw new IllegalStateException(e.getMessage(), e);
	}

	public void objectBegin()
		throws ParseException
	{
		mStack.push(mValue);
		mStack.push(mType);
		mStack.push(mWrapper);
		if (mType == java/lang/Object || java/util/Map.isAssignableFrom(mType))
		{
			if (!mType.isInterface() && mType != java/lang/Object)
				try
				{
					mValue = mType.newInstance();
				}
				catch (Exception e)
				{
					throw new IllegalStateException(e.getMessage(), e);
				}
			else
			if (mType == java/util/concurrent/ConcurrentMap)
				mValue = new ConcurrentHashMap();
			else
				mValue = new HashMap();
			mWrapper = null;
		} else
		{
			try
			{
				mValue = mType.newInstance();
				mWrapper = Wrapper.getWrapper(mType);
			}
			catch (IllegalAccessException e)
			{
				throw new ParseException(StringUtils.toString(e));
			}
			catch (InstantiationException e)
			{
				throw new ParseException(StringUtils.toString(e));
			}
		}
	}

	public Object objectEnd(int count)
	{
		Object ret = mValue;
		mWrapper = (Wrapper)mStack.pop();
		mType = (Class)mStack.pop();
		mValue = mStack.pop();
		return ret;
	}

	public void objectItem(String name)
	{
		mStack.push(name);
		mType = ((Class) (mWrapper != null ? mWrapper.getPropertyType(name) : java/lang/Object));
	}

	public void objectItemValue(Object obj, boolean isValue)
		throws ParseException
	{
		String name = (String)mStack.pop();
		if (mWrapper == null)
			((Map)mValue).put(name, obj);
		else
		if (mType != null)
		{
			if (isValue && obj != null)
				try
				{
					obj = mConverter.readValue(mType, obj);
				}
				catch (IOException e)
				{
					throw new ParseException(StringUtils.toString(e));
				}
			if ((mValue instanceof Throwable) && "message".equals(name))
				try
				{
					Field field = java/lang/Throwable.getDeclaredField("detailMessage");
					if (!field.isAccessible())
						field.setAccessible(true);
					field.set(mValue, obj);
				}
				catch (NoSuchFieldException e)
				{
					throw new ParseException(StringUtils.toString(e));
				}
				catch (IllegalAccessException e)
				{
					throw new ParseException(StringUtils.toString(e));
				}
			else
			if ((!(mValue instanceof Throwable) || !"suppressed".equals(name)) && !"class".equals(name))
				mWrapper.setPropertyValue(mValue, name, obj);
		}
	}

	public void arrayBegin()
		throws ParseException
	{
		mStack.push(mType);
		if (mType.isArray())
			mType = mType.getComponentType();
		else
		if (mType == java/lang/Object || java/util/Collection.isAssignableFrom(mType))
			mType = java/lang/Object;
		else
			throw new ParseException((new StringBuilder()).append("Convert error, can not load json array data into class [").append(mType.getName()).append("].").toString());
	}

	public Object arrayEnd(int count)
		throws ParseException
	{
		mType = (Class)mStack.get(-1 - count);
		Object ret;
		if (mType.isArray())
		{
			ret = toArray(mType.getComponentType(), mStack, count);
		} else
		{
			Collection items;
			if (mType == java/lang/Object || java/util/Collection.isAssignableFrom(mType))
			{
				if (!mType.isInterface() && mType != java/lang/Object)
					try
					{
						items = (Collection)mType.newInstance();
					}
					catch (Exception e)
					{
						throw new IllegalStateException(e.getMessage(), e);
					}
				else
				if (mType.isAssignableFrom(java/util/ArrayList))
					items = new ArrayList(count);
				else
				if (mType.isAssignableFrom(java/util/HashSet))
					items = new HashSet(count);
				else
				if (mType.isAssignableFrom(java/util/LinkedList))
					items = new LinkedList();
				else
					items = new ArrayList(count);
			} else
			{
				throw new ParseException((new StringBuilder()).append("Convert error, can not load json array data into class [").append(mType.getName()).append("].").toString());
			}
			for (int i = 0; i < count; i++)
				items.add(mStack.remove(i - count));

			ret = items;
		}
		mStack.pop();
		return ret;
	}

	public void arrayItem(int index)
		throws ParseException
	{
		if (mTypes != null && mStack.size() == index + 1)
			if (index < mTypes.length)
				mType = mTypes[index];
			else
				throw new ParseException((new StringBuilder()).append("Can not load json array data into [").append(name(mTypes)).append("].").toString());
	}

	public void arrayItemValue(int index, Object obj, boolean isValue)
		throws ParseException
	{
		if (isValue && obj != null)
			try
			{
				obj = mConverter.readValue(mType, obj);
			}
			catch (IOException e)
			{
				throw new ParseException(e.getMessage());
			}
		mStack.push(obj);
	}

	private static Object toArray(Class c, Stack list, int len)
		throws ParseException
	{
		if (c == java/lang/String)
		{
			if (len == 0)
				return EMPTY_STRING_ARRAY;
			String ss[] = new String[len];
			for (int i = len - 1; i >= 0; i--)
			{
				Object o = list.pop();
				ss[i] = o != null ? o.toString() : null;
			}

			return ss;
		}
		if (c == Boolean.TYPE)
		{
			if (len == 0)
				return EMPTY_BOOL_ARRAY;
			boolean ret[] = new boolean[len];
			for (int i = len - 1; i >= 0; i--)
			{
				Object o = list.pop();
				if (o instanceof Boolean)
					ret[i] = ((Boolean)o).booleanValue();
			}

			return ret;
		}
		if (c == Integer.TYPE)
		{
			if (len == 0)
				return EMPTY_INT_ARRAY;
			int ret[] = new int[len];
			for (int i = len - 1; i >= 0; i--)
			{
				Object o = list.pop();
				if (o instanceof Number)
					ret[i] = ((Number)o).intValue();
			}

			return ret;
		}
		if (c == Long.TYPE)
		{
			if (len == 0)
				return EMPTY_LONG_ARRAY;
			long ret[] = new long[len];
			for (int i = len - 1; i >= 0; i--)
			{
				Object o = list.pop();
				if (o instanceof Number)
					ret[i] = ((Number)o).longValue();
			}

			return ret;
		}
		if (c == Float.TYPE)
		{
			if (len == 0)
				return EMPTY_FLOAT_ARRAY;
			float ret[] = new float[len];
			for (int i = len - 1; i >= 0; i--)
			{
				Object o = list.pop();
				if (o instanceof Number)
					ret[i] = ((Number)o).floatValue();
			}

			return ret;
		}
		if (c == Double.TYPE)
		{
			if (len == 0)
				return EMPTY_DOUBLE_ARRAY;
			double ret[] = new double[len];
			for (int i = len - 1; i >= 0; i--)
			{
				Object o = list.pop();
				if (o instanceof Number)
					ret[i] = ((Number)o).doubleValue();
			}

			return ret;
		}
		if (c == Byte.TYPE)
		{
			if (len == 0)
				return EMPTY_BYTE_ARRAY;
			byte ret[] = new byte[len];
			for (int i = len - 1; i >= 0; i--)
			{
				Object o = list.pop();
				if (o instanceof Number)
					ret[i] = ((Number)o).byteValue();
			}

			return ret;
		}
		if (c == Character.TYPE)
		{
			if (len == 0)
				return EMPTY_CHAR_ARRAY;
			char ret[] = new char[len];
			for (int i = len - 1; i >= 0; i--)
			{
				Object o = list.pop();
				if (o instanceof Character)
					ret[i] = ((Character)o).charValue();
			}

			return ret;
		}
		if (c == Short.TYPE)
		{
			if (len == 0)
				return EMPTY_SHORT_ARRAY;
			short ret[] = new short[len];
			for (int i = len - 1; i >= 0; i--)
			{
				Object o = list.pop();
				if (o instanceof Number)
					ret[i] = ((Number)o).shortValue();
			}

			return ret;
		}
		Object ret = Array.newInstance(c, len);
		for (int i = len - 1; i >= 0; i--)
			Array.set(ret, i, list.pop());

		return ret;
	}

	private static String name(Class types[])
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < types.length; i++)
		{
			if (i > 0)
				sb.append(", ");
			sb.append(types[i].getName());
		}

		return sb.toString();
	}

}
