// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JavaDeserializer.java

package com.autohome.hessian.io;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;

// Referenced classes of package com.autohome.hessian.io:
//			AbstractMapDeserializer, IOExceptionWrapper, HessianProtocolException, HessianFieldException, 
//			AbstractHessianInput

public class JavaDeserializer extends AbstractMapDeserializer
{
	static class SqlTimeFieldDeserializer extends FieldDeserializer
	{

		private final Field _field;

		void deserialize(AbstractHessianInput in, Object obj)
			throws IOException
		{
			Time value = null;
			try
			{
				java.util.Date date = (java.util.Date)in.readObject();
				value = new Time(date.getTime());
				_field.set(obj, value);
			}
			catch (Exception e)
			{
				JavaDeserializer.logDeserializeError(_field, obj, value, e);
			}
		}

		SqlTimeFieldDeserializer(Field field)
		{
			_field = field;
		}
	}

	static class SqlTimestampFieldDeserializer extends FieldDeserializer
	{

		private final Field _field;

		void deserialize(AbstractHessianInput in, Object obj)
			throws IOException
		{
			Timestamp value = null;
			try
			{
				java.util.Date date = (java.util.Date)in.readObject();
				value = new Timestamp(date.getTime());
				_field.set(obj, value);
			}
			catch (Exception e)
			{
				JavaDeserializer.logDeserializeError(_field, obj, value, e);
			}
		}

		SqlTimestampFieldDeserializer(Field field)
		{
			_field = field;
		}
	}

	static class SqlDateFieldDeserializer extends FieldDeserializer
	{

		private final Field _field;

		void deserialize(AbstractHessianInput in, Object obj)
			throws IOException
		{
			Date value = null;
			try
			{
				java.util.Date date = (java.util.Date)in.readObject();
				value = new Date(date.getTime());
				_field.set(obj, value);
			}
			catch (Exception e)
			{
				JavaDeserializer.logDeserializeError(_field, obj, value, e);
			}
		}

		SqlDateFieldDeserializer(Field field)
		{
			_field = field;
		}
	}

	static class StringFieldDeserializer extends FieldDeserializer
	{

		private final Field _field;

		void deserialize(AbstractHessianInput in, Object obj)
			throws IOException
		{
			String value = null;
			try
			{
				value = in.readString();
				_field.set(obj, value);
			}
			catch (Exception e)
			{
				JavaDeserializer.logDeserializeError(_field, obj, value, e);
			}
		}

		StringFieldDeserializer(Field field)
		{
			_field = field;
		}
	}

	static class DoubleFieldDeserializer extends FieldDeserializer
	{

		private final Field _field;

		void deserialize(AbstractHessianInput in, Object obj)
			throws IOException
		{
			double value = 0.0D;
			try
			{
				value = in.readDouble();
				_field.setDouble(obj, value);
			}
			catch (Exception e)
			{
				JavaDeserializer.logDeserializeError(_field, obj, Double.valueOf(value), e);
			}
		}

		DoubleFieldDeserializer(Field field)
		{
			_field = field;
		}
	}

	static class FloatFieldDeserializer extends FieldDeserializer
	{

		private final Field _field;

		void deserialize(AbstractHessianInput in, Object obj)
			throws IOException
		{
			double value = 0.0D;
			try
			{
				value = in.readDouble();
				_field.setFloat(obj, (float)value);
			}
			catch (Exception e)
			{
				JavaDeserializer.logDeserializeError(_field, obj, Double.valueOf(value), e);
			}
		}

		FloatFieldDeserializer(Field field)
		{
			_field = field;
		}
	}

	static class LongFieldDeserializer extends FieldDeserializer
	{

		private final Field _field;

		void deserialize(AbstractHessianInput in, Object obj)
			throws IOException
		{
			long value = 0L;
			try
			{
				value = in.readLong();
				_field.setLong(obj, value);
			}
			catch (Exception e)
			{
				JavaDeserializer.logDeserializeError(_field, obj, Long.valueOf(value), e);
			}
		}

		LongFieldDeserializer(Field field)
		{
			_field = field;
		}
	}

	static class IntFieldDeserializer extends FieldDeserializer
	{

		private final Field _field;

		void deserialize(AbstractHessianInput in, Object obj)
			throws IOException
		{
			int value = 0;
			try
			{
				value = in.readInt();
				_field.setInt(obj, value);
			}
			catch (Exception e)
			{
				JavaDeserializer.logDeserializeError(_field, obj, Integer.valueOf(value), e);
			}
		}

		IntFieldDeserializer(Field field)
		{
			_field = field;
		}
	}

	static class ShortFieldDeserializer extends FieldDeserializer
	{

		private final Field _field;

		void deserialize(AbstractHessianInput in, Object obj)
			throws IOException
		{
			int value = 0;
			try
			{
				value = in.readInt();
				_field.setShort(obj, (short)value);
			}
			catch (Exception e)
			{
				JavaDeserializer.logDeserializeError(_field, obj, Integer.valueOf(value), e);
			}
		}

		ShortFieldDeserializer(Field field)
		{
			_field = field;
		}
	}

	static class ByteFieldDeserializer extends FieldDeserializer
	{

		private final Field _field;

		void deserialize(AbstractHessianInput in, Object obj)
			throws IOException
		{
			int value = 0;
			try
			{
				value = in.readInt();
				_field.setByte(obj, (byte)value);
			}
			catch (Exception e)
			{
				JavaDeserializer.logDeserializeError(_field, obj, Integer.valueOf(value), e);
			}
		}

		ByteFieldDeserializer(Field field)
		{
			_field = field;
		}
	}

	static class BooleanFieldDeserializer extends FieldDeserializer
	{

		private final Field _field;

		void deserialize(AbstractHessianInput in, Object obj)
			throws IOException
		{
			boolean value = false;
			try
			{
				value = in.readBoolean();
				_field.setBoolean(obj, value);
			}
			catch (Exception e)
			{
				JavaDeserializer.logDeserializeError(_field, obj, Boolean.valueOf(value), e);
			}
		}

		BooleanFieldDeserializer(Field field)
		{
			_field = field;
		}
	}

	static class ObjectFieldDeserializer extends FieldDeserializer
	{

		private final Field _field;

		void deserialize(AbstractHessianInput in, Object obj)
			throws IOException
		{
			Object value = null;
			try
			{
				value = in.readObject(_field.getType());
				_field.set(obj, value);
			}
			catch (Exception e)
			{
				JavaDeserializer.logDeserializeError(_field, obj, value, e);
			}
		}

		ObjectFieldDeserializer(Field field)
		{
			_field = field;
		}
	}

	static class NullFieldDeserializer
	{

		static NullFieldDeserializer DESER = new NullFieldDeserializer();

		void deserialize(AbstractHessianInput in, Object obj)
			throws IOException
		{
			in.readObject();
		}


		NullFieldDeserializer()
		{
		}
	}

	static abstract class FieldDeserializer
	{

		abstract void deserialize(AbstractHessianInput abstracthessianinput, Object obj)
			throws IOException;

		FieldDeserializer()
		{
		}
	}


	private Class _type;
	private HashMap _fieldMap;
	private Method _readResolve;
	private Constructor _constructor;
	private Object _constructorArgs[];

	public JavaDeserializer(Class cl)
	{
		_type = cl;
		_fieldMap = getFieldMap(cl);
		_readResolve = getReadResolve(cl);
		if (_readResolve != null)
			_readResolve.setAccessible(true);
		Constructor constructors[] = cl.getDeclaredConstructors();
		long bestCost = 0x7fffffffffffffffL;
		for (int i = 0; i < constructors.length; i++)
		{
			Class param[] = constructors[i].getParameterTypes();
			long cost = 0L;
			for (int j = 0; j < param.length; j++)
			{
				cost = 4L * cost;
				if (java/lang/Object.equals(param[j]))
				{
					cost++;
					continue;
				}
				if (java/lang/String.equals(param[j]))
				{
					cost += 2L;
					continue;
				}
				if (Integer.TYPE.equals(param[j]))
				{
					cost += 3L;
					continue;
				}
				if (Long.TYPE.equals(param[j]))
				{
					cost += 4L;
					continue;
				}
				if (param[j].isPrimitive())
					cost += 5L;
				else
					cost += 6L;
			}

			if (cost < 0L || cost > 0x10000L)
				cost = 0x10000L;
			cost += (long)param.length << 48;
			if (cost < bestCost)
			{
				_constructor = constructors[i];
				bestCost = cost;
			}
		}

		if (_constructor != null)
		{
			_constructor.setAccessible(true);
			Class params[] = _constructor.getParameterTypes();
			_constructorArgs = new Object[params.length];
			for (int i = 0; i < params.length; i++)
				_constructorArgs[i] = getParamArg(params[i]);

		}
	}

	public Class getType()
	{
		return _type;
	}

	public boolean isReadResolve()
	{
		return _readResolve != null;
	}

	public Object readMap(AbstractHessianInput in)
		throws IOException
	{
		Object obj = instantiate();
		return readMap(in, obj);
		IOException e;
		e;
		throw e;
		e;
		throw e;
		e;
		throw new IOExceptionWrapper((new StringBuilder()).append(_type.getName()).append(":").append(e.getMessage()).toString(), e);
	}

	public Object[] createFields(int len)
	{
		return new FieldDeserializer[len];
	}

	public Object createField(String name)
	{
		Object reader = _fieldMap.get(name);
		if (reader == null)
			reader = NullFieldDeserializer.DESER;
		return reader;
	}

	public Object readObject(AbstractHessianInput in, Object fields[])
		throws IOException
	{
		Object obj = instantiate();
		return readObject(in, obj, (FieldDeserializer[])(FieldDeserializer[])fields);
		IOException e;
		e;
		throw e;
		e;
		throw e;
		e;
		throw new IOExceptionWrapper((new StringBuilder()).append(_type.getName()).append(":").append(e.getMessage()).toString(), e);
	}

	public Object readObject(AbstractHessianInput in, String fieldNames[])
		throws IOException
	{
		Object obj = instantiate();
		return readObject(in, obj, fieldNames);
		IOException e;
		e;
		throw e;
		e;
		throw e;
		e;
		throw new IOExceptionWrapper((new StringBuilder()).append(_type.getName()).append(":").append(e.getMessage()).toString(), e);
	}

	protected Method getReadResolve(Class cl)
	{
		for (; cl != null; cl = cl.getSuperclass())
		{
			Method methods[] = cl.getDeclaredMethods();
			for (int i = 0; i < methods.length; i++)
			{
				Method method = methods[i];
				if (method.getName().equals("readResolve") && method.getParameterTypes().length == 0)
					return method;
			}

		}

		return null;
	}

	public Object readMap(AbstractHessianInput in, Object obj)
		throws IOException
	{
		Object resolve;
		int ref = in.addRef(obj);
		while (!in.isEnd()) 
		{
			Object key = in.readObject();
			FieldDeserializer deser = (FieldDeserializer)_fieldMap.get(key);
			if (deser != null)
				deser.deserialize(in, obj);
			else
				in.readObject();
		}
		in.readMapEnd();
		resolve = resolve(in, obj);
		if (obj != resolve)
			in.setRef(ref, resolve);
		return resolve;
		IOException e;
		e;
		throw e;
		e;
		throw new IOExceptionWrapper(e);
	}

	private Object readObject(AbstractHessianInput in, Object obj, FieldDeserializer fields[])
		throws IOException
	{
		Object resolve;
		int ref = in.addRef(obj);
		FieldDeserializer arr$[] = fields;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			FieldDeserializer reader = arr$[i$];
			reader.deserialize(in, obj);
		}

		resolve = resolve(in, obj);
		if (obj != resolve)
			in.setRef(ref, resolve);
		return resolve;
		IOException e;
		e;
		throw e;
		e;
		throw new IOExceptionWrapper((new StringBuilder()).append(obj.getClass().getName()).append(":").append(e).toString(), e);
	}

	public Object readObject(AbstractHessianInput in, Object obj, String fieldNames[])
		throws IOException
	{
		Object resolve;
		int ref = in.addRef(obj);
		String arr$[] = fieldNames;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			String fieldName = arr$[i$];
			FieldDeserializer reader = (FieldDeserializer)_fieldMap.get(fieldName);
			if (reader != null)
				reader.deserialize(in, obj);
			else
				in.readObject();
		}

		resolve = resolve(in, obj);
		if (obj != resolve)
			in.setRef(ref, resolve);
		return resolve;
		IOException e;
		e;
		throw e;
		e;
		throw new IOExceptionWrapper((new StringBuilder()).append(obj.getClass().getName()).append(":").append(e).toString(), e);
	}

	protected Object resolve(AbstractHessianInput in, Object obj)
		throws Exception
	{
		InvocationTargetException e;
		if (_readResolve != null)
			return _readResolve.invoke(obj, new Object[0]);
		else
			return obj;
		e;
		if (e.getCause() instanceof Exception)
			throw (Exception)e.getCause();
		else
			throw e;
	}

	protected Object instantiate()
		throws Exception
	{
		if (_constructor != null)
			return _constructor.newInstance(_constructorArgs);
		return _type.newInstance();
		Exception e;
		e;
		throw new HessianProtocolException((new StringBuilder()).append("'").append(_type.getName()).append("' could not be instantiated").toString(), e);
	}

	protected HashMap getFieldMap(Class cl)
	{
		HashMap fieldMap = new HashMap();
		for (; cl != null; cl = cl.getSuperclass())
		{
			Field fields[] = cl.getDeclaredFields();
			for (int i = 0; i < fields.length; i++)
			{
				Field field = fields[i];
				if (Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers()) || fieldMap.get(field.getName()) != null)
					continue;
				try
				{
					field.setAccessible(true);
				}
				catch (Throwable e)
				{
					e.printStackTrace();
				}
				Class type = field.getType();
				FieldDeserializer deser;
				if (java/lang/String.equals(type))
					deser = new StringFieldDeserializer(field);
				else
				if (Byte.TYPE.equals(type))
					deser = new ByteFieldDeserializer(field);
				else
				if (Short.TYPE.equals(type))
					deser = new ShortFieldDeserializer(field);
				else
				if (Integer.TYPE.equals(type))
					deser = new IntFieldDeserializer(field);
				else
				if (Long.TYPE.equals(type))
					deser = new LongFieldDeserializer(field);
				else
				if (Float.TYPE.equals(type))
					deser = new FloatFieldDeserializer(field);
				else
				if (Double.TYPE.equals(type))
					deser = new DoubleFieldDeserializer(field);
				else
				if (Boolean.TYPE.equals(type))
					deser = new BooleanFieldDeserializer(field);
				else
				if (java/sql/Date.equals(type))
					deser = new SqlDateFieldDeserializer(field);
				else
				if (java/sql/Timestamp.equals(type))
					deser = new SqlTimestampFieldDeserializer(field);
				else
				if (java/sql/Time.equals(type))
					deser = new SqlTimeFieldDeserializer(field);
				else
					deser = new ObjectFieldDeserializer(field);
				fieldMap.put(field.getName(), deser);
			}

		}

		return fieldMap;
	}

	protected static Object getParamArg(Class cl)
	{
		if (!cl.isPrimitive())
			return null;
		if (Boolean.TYPE.equals(cl))
			return Boolean.FALSE;
		if (Byte.TYPE.equals(cl))
			return new Byte((byte)0);
		if (Short.TYPE.equals(cl))
			return new Short((short)0);
		if (Character.TYPE.equals(cl))
			return new Character('\0');
		if (Integer.TYPE.equals(cl))
			return Integer.valueOf(0);
		if (Long.TYPE.equals(cl))
			return Long.valueOf(0L);
		if (Float.TYPE.equals(cl))
			return Float.valueOf(0.0F);
		if (Double.TYPE.equals(cl))
			return Double.valueOf(0.0D);
		else
			throw new UnsupportedOperationException();
	}

	static void logDeserializeError(Field field, Object obj, Object value, Throwable e)
		throws IOException
	{
		String fieldName = (new StringBuilder()).append(field.getDeclaringClass().getName()).append(".").append(field.getName()).toString();
		if (e instanceof HessianFieldException)
			throw (HessianFieldException)e;
		if (e instanceof IOException)
			throw new HessianFieldException((new StringBuilder()).append(fieldName).append(": ").append(e.getMessage()).toString(), e);
		if (value != null)
			throw new HessianFieldException((new StringBuilder()).append(fieldName).append(": ").append(value.getClass().getName()).append(" (").append(value).append(")").append(" cannot be assigned to '").append(field.getType().getName()).append("'").toString(), e);
		else
			throw new HessianFieldException((new StringBuilder()).append(fieldName).append(": ").append(field.getType().getName()).append(" cannot be assigned from null").toString(), e);
	}
}
