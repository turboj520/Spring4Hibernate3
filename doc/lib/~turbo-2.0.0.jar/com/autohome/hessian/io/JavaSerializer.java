// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JavaSerializer.java

package com.autohome.hessian.io;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

// Referenced classes of package com.autohome.hessian.io:
//			AbstractSerializer, IOExceptionWrapper, AbstractHessianOutput, Serializer

public class JavaSerializer extends AbstractSerializer
{
	static class DateFieldSerializer extends FieldSerializer
	{

		static final FieldSerializer SER = new DateFieldSerializer();

		void serialize(AbstractHessianOutput out, Object obj, Field field)
			throws IOException
		{
			Date value = null;
			try
			{
				value = (Date)field.get(obj);
			}
			catch (IllegalAccessException e)
			{
				JavaSerializer.log.log(Level.FINE, e.toString(), e);
			}
			if (value == null)
				out.writeNull();
			else
				out.writeUTCDate(value.getTime());
		}


		DateFieldSerializer()
		{
		}
	}

	static class StringFieldSerializer extends FieldSerializer
	{

		static final FieldSerializer SER = new StringFieldSerializer();

		void serialize(AbstractHessianOutput out, Object obj, Field field)
			throws IOException
		{
			String value = null;
			try
			{
				value = (String)field.get(obj);
			}
			catch (IllegalAccessException e)
			{
				JavaSerializer.log.log(Level.FINE, e.toString(), e);
			}
			out.writeString(value);
		}


		StringFieldSerializer()
		{
		}
	}

	static class DoubleFieldSerializer extends FieldSerializer
	{

		static final FieldSerializer SER = new DoubleFieldSerializer();

		void serialize(AbstractHessianOutput out, Object obj, Field field)
			throws IOException
		{
			double value = 0.0D;
			try
			{
				value = field.getDouble(obj);
			}
			catch (IllegalAccessException e)
			{
				JavaSerializer.log.log(Level.FINE, e.toString(), e);
			}
			out.writeDouble(value);
		}


		DoubleFieldSerializer()
		{
		}
	}

	static class LongFieldSerializer extends FieldSerializer
	{

		static final FieldSerializer SER = new LongFieldSerializer();

		void serialize(AbstractHessianOutput out, Object obj, Field field)
			throws IOException
		{
			long value = 0L;
			try
			{
				value = field.getLong(obj);
			}
			catch (IllegalAccessException e)
			{
				JavaSerializer.log.log(Level.FINE, e.toString(), e);
			}
			out.writeLong(value);
		}


		LongFieldSerializer()
		{
		}
	}

	static class IntFieldSerializer extends FieldSerializer
	{

		static final FieldSerializer SER = new IntFieldSerializer();

		void serialize(AbstractHessianOutput out, Object obj, Field field)
			throws IOException
		{
			int value = 0;
			try
			{
				value = field.getInt(obj);
			}
			catch (IllegalAccessException e)
			{
				JavaSerializer.log.log(Level.FINE, e.toString(), e);
			}
			out.writeInt(value);
		}


		IntFieldSerializer()
		{
		}
	}

	static class BooleanFieldSerializer extends FieldSerializer
	{

		static final FieldSerializer SER = new BooleanFieldSerializer();

		void serialize(AbstractHessianOutput out, Object obj, Field field)
			throws IOException
		{
			boolean value = false;
			try
			{
				value = field.getBoolean(obj);
			}
			catch (IllegalAccessException e)
			{
				JavaSerializer.log.log(Level.FINE, e.toString(), e);
			}
			out.writeBoolean(value);
		}


		BooleanFieldSerializer()
		{
		}
	}

	static class FieldSerializer
	{

		static final FieldSerializer SER = new FieldSerializer();

		void serialize(AbstractHessianOutput out, Object obj, Field field)
			throws IOException
		{
			Object value = null;
			try
			{
				value = field.get(obj);
			}
			catch (IllegalAccessException e)
			{
				JavaSerializer.log.log(Level.FINE, e.toString(), e);
			}
			try
			{
				out.writeObject(value);
			}
			catch (RuntimeException e)
			{
				throw new RuntimeException((new StringBuilder()).append(e.getMessage()).append("\n field: ").append(field.getDeclaringClass().getName()).append('.').append(field.getName()).toString(), e);
			}
			catch (IOException e)
			{
				throw new IOExceptionWrapper((new StringBuilder()).append(e.getMessage()).append("\n field: ").append(field.getDeclaringClass().getName()).append('.').append(field.getName()).toString(), e);
			}
		}


		FieldSerializer()
		{
		}
	}


	private static final Logger log = Logger.getLogger(com/autohome/hessian/io/JavaSerializer.getName());
	private static final WeakHashMap _serializerMap = new WeakHashMap();
	private Field _fields[];
	private FieldSerializer _fieldSerializers[];
	private Object _writeReplaceFactory;
	private Method _writeReplace;

	public JavaSerializer(Class cl)
	{
		introspect(cl);
		_writeReplace = getWriteReplace(cl);
	}

	public static Serializer create(Class cl)
	{
		WeakHashMap weakhashmap = _serializerMap;
		JVM INSTR monitorenter ;
		JavaSerializer base;
		SoftReference baseRef = (SoftReference)_serializerMap.get(cl);
		base = baseRef == null ? null : (JavaSerializer)baseRef.get();
		if (base == null)
		{
			base = new JavaSerializer(cl);
			baseRef = new SoftReference(base);
			_serializerMap.put(cl, baseRef);
		}
		return base;
		Exception exception;
		exception;
		throw exception;
	}

	protected void introspect(Class cl)
	{
		if (_writeReplace != null)
			_writeReplace.setAccessible(true);
		ArrayList primitiveFields = new ArrayList();
		ArrayList compoundFields = new ArrayList();
		for (; cl != null; cl = cl.getSuperclass())
		{
			Field fields[] = cl.getDeclaredFields();
			for (int i = 0; i < fields.length; i++)
			{
				Field field = fields[i];
				if (Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers()))
					continue;
				field.setAccessible(true);
				if (field.getType().isPrimitive() || field.getType().getName().startsWith("java.lang.") && !field.getType().equals(java/lang/Object))
					primitiveFields.add(field);
				else
					compoundFields.add(field);
			}

		}

		ArrayList fields = new ArrayList();
		fields.addAll(primitiveFields);
		fields.addAll(compoundFields);
		_fields = new Field[fields.size()];
		fields.toArray(_fields);
		_fieldSerializers = new FieldSerializer[_fields.length];
		for (int i = 0; i < _fields.length; i++)
			_fieldSerializers[i] = getFieldSerializer(_fields[i].getType());

	}

	protected static Method getWriteReplace(Class cl)
	{
		for (; cl != null; cl = cl.getSuperclass())
		{
			Method methods[] = cl.getDeclaredMethods();
			for (int i = 0; i < methods.length; i++)
			{
				Method method = methods[i];
				if (method.getName().equals("writeReplace") && method.getParameterTypes().length == 0)
					return method;
			}

		}

		return null;
	}

	protected Method getWriteReplace(Class cl, Class param)
	{
		for (; cl != null; cl = cl.getSuperclass())
		{
			Method arr$[] = cl.getDeclaredMethods();
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				Method method = arr$[i$];
				if (method.getName().equals("writeReplace") && method.getParameterTypes().length == 1 && param.equals(method.getParameterTypes()[0]))
					return method;
			}

		}

		return null;
	}

	public void writeObject(Object obj, AbstractHessianOutput out)
		throws IOException
	{
		if (out.addRef(obj))
			return;
		Class cl = obj.getClass();
		try
		{
			if (_writeReplace != null)
			{
				Object repl;
				if (_writeReplaceFactory != null)
					repl = _writeReplace.invoke(_writeReplaceFactory, new Object[] {
						obj
					});
				else
					repl = _writeReplace.invoke(obj, new Object[0]);
				out.writeObject(repl);
				out.replaceRef(repl, obj);
				return;
			}
		}
		catch (RuntimeException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		int ref = out.writeObjectBegin(cl.getName());
		if (ref < -1)
		{
			writeObject10(obj, out);
		} else
		{
			if (ref == -1)
			{
				writeDefinition20(out);
				out.writeObjectBegin(cl.getName());
			}
			writeInstance(obj, out);
		}
	}

	protected void writeObject10(Object obj, AbstractHessianOutput out)
		throws IOException
	{
		for (int i = 0; i < _fields.length; i++)
		{
			Field field = _fields[i];
			out.writeString(field.getName());
			_fieldSerializers[i].serialize(out, obj, field);
		}

		out.writeMapEnd();
	}

	private void writeDefinition20(AbstractHessianOutput out)
		throws IOException
	{
		out.writeClassFieldLength(_fields.length);
		for (int i = 0; i < _fields.length; i++)
		{
			Field field = _fields[i];
			out.writeString(field.getName());
		}

	}

	public void writeInstance(Object obj, AbstractHessianOutput out)
		throws IOException
	{
		try
		{
			for (int i = 0; i < _fields.length; i++)
			{
				Field field = _fields[i];
				_fieldSerializers[i].serialize(out, obj, field);
			}

		}
		catch (RuntimeException e)
		{
			throw new RuntimeException((new StringBuilder()).append(e.getMessage()).append("\n class: ").append(obj.getClass().getName()).append(" (object=").append(obj).append(")").toString(), e);
		}
		catch (IOException e)
		{
			throw new IOExceptionWrapper((new StringBuilder()).append(e.getMessage()).append("\n class: ").append(obj.getClass().getName()).append(" (object=").append(obj).append(")").toString(), e);
		}
	}

	private static FieldSerializer getFieldSerializer(Class type)
	{
		if (Integer.TYPE.equals(type) || Byte.TYPE.equals(type) || Short.TYPE.equals(type) || Integer.TYPE.equals(type))
			return IntFieldSerializer.SER;
		if (Long.TYPE.equals(type))
			return LongFieldSerializer.SER;
		if (Double.TYPE.equals(type) || Float.TYPE.equals(type))
			return DoubleFieldSerializer.SER;
		if (Boolean.TYPE.equals(type))
			return BooleanFieldSerializer.SER;
		if (java/lang/String.equals(type))
			return StringFieldSerializer.SER;
		if (java/util/Date.equals(type) || java/sql/Date.equals(type) || java/sql/Timestamp.equals(type) || java/sql/Time.equals(type))
			return DateFieldSerializer.SER;
		else
			return FieldSerializer.SER;
	}


}
