// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   UnsafeSerializer.java

package com.autohome.hessian.io;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.Unsafe;

// Referenced classes of package com.autohome.hessian.io:
//			AbstractSerializer, IOExceptionWrapper, AbstractHessianOutput

public class UnsafeSerializer extends AbstractSerializer
{
	static final class DateFieldSerializer extends FieldSerializer
	{

		private final Field _field;
		private final long _offset;

		void serialize(AbstractHessianOutput out, Object obj)
			throws IOException
		{
			Date value = (Date)UnsafeSerializer._unsafe.getObject(obj, _offset);
			if (value == null)
				out.writeNull();
			else
				out.writeUTCDate(value.getTime());
		}

		DateFieldSerializer(Field field)
		{
			_field = field;
			_offset = UnsafeSerializer._unsafe.objectFieldOffset(field);
			if (_offset == -1L)
				throw new IllegalStateException();
			else
				return;
		}
	}

	static final class StringFieldSerializer extends FieldSerializer
	{

		private final Field _field;
		private final long _offset;

		final void serialize(AbstractHessianOutput out, Object obj)
			throws IOException
		{
			String value = (String)UnsafeSerializer._unsafe.getObject(obj, _offset);
			out.writeString(value);
		}

		StringFieldSerializer(Field field)
		{
			_field = field;
			_offset = UnsafeSerializer._unsafe.objectFieldOffset(field);
			if (_offset == -1L)
				throw new IllegalStateException();
			else
				return;
		}
	}

	static final class DoubleFieldSerializer extends FieldSerializer
	{

		private final Field _field;
		private final long _offset;

		final void serialize(AbstractHessianOutput out, Object obj)
			throws IOException
		{
			double value = UnsafeSerializer._unsafe.getDouble(obj, _offset);
			out.writeDouble(value);
		}

		DoubleFieldSerializer(Field field)
		{
			_field = field;
			_offset = UnsafeSerializer._unsafe.objectFieldOffset(field);
			if (_offset == -1L)
				throw new IllegalStateException();
			else
				return;
		}
	}

	static final class FloatFieldSerializer extends FieldSerializer
	{

		private final Field _field;
		private final long _offset;

		final void serialize(AbstractHessianOutput out, Object obj)
			throws IOException
		{
			double value = UnsafeSerializer._unsafe.getFloat(obj, _offset);
			out.writeDouble(value);
		}

		FloatFieldSerializer(Field field)
		{
			_field = field;
			_offset = UnsafeSerializer._unsafe.objectFieldOffset(field);
			if (_offset == -1L)
				throw new IllegalStateException();
			else
				return;
		}
	}

	static final class LongFieldSerializer extends FieldSerializer
	{

		private final Field _field;
		private final long _offset;

		final void serialize(AbstractHessianOutput out, Object obj)
			throws IOException
		{
			long value = UnsafeSerializer._unsafe.getLong(obj, _offset);
			out.writeLong(value);
		}

		LongFieldSerializer(Field field)
		{
			_field = field;
			_offset = UnsafeSerializer._unsafe.objectFieldOffset(field);
			if (_offset == -1L)
				throw new IllegalStateException();
			else
				return;
		}
	}

	static final class IntFieldSerializer extends FieldSerializer
	{

		private final Field _field;
		private final long _offset;

		final void serialize(AbstractHessianOutput out, Object obj)
			throws IOException
		{
			int value = UnsafeSerializer._unsafe.getInt(obj, _offset);
			out.writeInt(value);
		}

		IntFieldSerializer(Field field)
		{
			_field = field;
			_offset = UnsafeSerializer._unsafe.objectFieldOffset(field);
			if (_offset == -1L)
				throw new IllegalStateException();
			else
				return;
		}
	}

	static final class ShortFieldSerializer extends FieldSerializer
	{

		private final Field _field;
		private final long _offset;

		final void serialize(AbstractHessianOutput out, Object obj)
			throws IOException
		{
			int value = UnsafeSerializer._unsafe.getShort(obj, _offset);
			out.writeInt(value);
		}

		ShortFieldSerializer(Field field)
		{
			_field = field;
			_offset = UnsafeSerializer._unsafe.objectFieldOffset(field);
			if (_offset == -1L)
				throw new IllegalStateException();
			else
				return;
		}
	}

	static final class CharFieldSerializer extends FieldSerializer
	{

		private final Field _field;
		private final long _offset;

		final void serialize(AbstractHessianOutput out, Object obj)
			throws IOException
		{
			char value = UnsafeSerializer._unsafe.getChar(obj, _offset);
			out.writeString(String.valueOf(value));
		}

		CharFieldSerializer(Field field)
		{
			_field = field;
			_offset = UnsafeSerializer._unsafe.objectFieldOffset(field);
			if (_offset == -1L)
				throw new IllegalStateException();
			else
				return;
		}
	}

	static final class ByteFieldSerializer extends FieldSerializer
	{

		private final Field _field;
		private final long _offset;

		final void serialize(AbstractHessianOutput out, Object obj)
			throws IOException
		{
			int value = UnsafeSerializer._unsafe.getByte(obj, _offset);
			out.writeInt(value);
		}

		ByteFieldSerializer(Field field)
		{
			_field = field;
			_offset = UnsafeSerializer._unsafe.objectFieldOffset(field);
			if (_offset == -1L)
				throw new IllegalStateException();
			else
				return;
		}
	}

	static final class BooleanFieldSerializer extends FieldSerializer
	{

		private final Field _field;
		private final long _offset;

		void serialize(AbstractHessianOutput out, Object obj)
			throws IOException
		{
			boolean value = UnsafeSerializer._unsafe.getBoolean(obj, _offset);
			out.writeBoolean(value);
		}

		BooleanFieldSerializer(Field field)
		{
			_field = field;
			_offset = UnsafeSerializer._unsafe.objectFieldOffset(field);
			if (_offset == -1L)
				throw new IllegalStateException();
			else
				return;
		}
	}

	static final class ObjectFieldSerializer extends FieldSerializer
	{

		private final Field _field;
		private final long _offset;

		final void serialize(AbstractHessianOutput out, Object obj)
			throws IOException
		{
			try
			{
				Object value = UnsafeSerializer._unsafe.getObject(obj, _offset);
				out.writeObject(value);
			}
			catch (RuntimeException e)
			{
				throw new RuntimeException((new StringBuilder()).append(e.getMessage()).append("\n field: ").append(_field.getDeclaringClass().getName()).append('.').append(_field.getName()).toString(), e);
			}
			catch (IOException e)
			{
				throw new IOExceptionWrapper((new StringBuilder()).append(e.getMessage()).append("\n field: ").append(_field.getDeclaringClass().getName()).append('.').append(_field.getName()).toString(), e);
			}
		}

		ObjectFieldSerializer(Field field)
		{
			_field = field;
			_offset = UnsafeSerializer._unsafe.objectFieldOffset(field);
			if (_offset == -1L)
				throw new IllegalStateException();
			else
				return;
		}
	}

	static abstract class FieldSerializer
	{

		abstract void serialize(AbstractHessianOutput abstracthessianoutput, Object obj)
			throws IOException;

		FieldSerializer()
		{
		}
	}


	private static final Logger log;
	private static boolean _isEnabled;
	private static Unsafe _unsafe;
	private static final WeakHashMap _serializerMap = new WeakHashMap();
	private static Object NULL_ARGS[] = new Object[0];
	private Field _fields[];
	private FieldSerializer _fieldSerializers[];

	public static boolean isEnabled()
	{
		return _isEnabled;
	}

	public UnsafeSerializer(Class cl)
	{
		introspect(cl);
	}

	public static UnsafeSerializer create(Class cl)
	{
		ClassLoader loader = cl.getClassLoader();
		WeakHashMap weakhashmap = _serializerMap;
		JVM INSTR monitorenter ;
		UnsafeSerializer base;
		SoftReference baseRef = (SoftReference)_serializerMap.get(cl);
		base = baseRef == null ? null : (UnsafeSerializer)baseRef.get();
		if (base == null)
		{
			base = new UnsafeSerializer(cl);
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
			_fieldSerializers[i] = getFieldSerializer(_fields[i]);

	}

	public void writeObject(Object obj, AbstractHessianOutput out)
		throws IOException
	{
		if (out.addRef(obj))
			return;
		Class cl = obj.getClass();
		int ref = out.writeObjectBegin(cl.getName());
		if (ref >= 0)
			writeInstance(obj, out);
		else
		if (ref == -1)
		{
			writeDefinition20(out);
			out.writeObjectBegin(cl.getName());
			writeInstance(obj, out);
		} else
		{
			writeObject10(obj, out);
		}
	}

	protected void writeObject10(Object obj, AbstractHessianOutput out)
		throws IOException
	{
		for (int i = 0; i < _fields.length; i++)
		{
			Field field = _fields[i];
			out.writeString(field.getName());
			_fieldSerializers[i].serialize(out, obj);
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

	public final void writeInstance(Object obj, AbstractHessianOutput out)
		throws IOException
	{
		try
		{
			FieldSerializer fieldSerializers[] = _fieldSerializers;
			int length = fieldSerializers.length;
			for (int i = 0; i < length; i++)
				fieldSerializers[i].serialize(out, obj);

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

	private static FieldSerializer getFieldSerializer(Field field)
	{
		Class type = field.getType();
		if (Boolean.TYPE.equals(type))
			return new BooleanFieldSerializer(field);
		if (Byte.TYPE.equals(type))
			return new ByteFieldSerializer(field);
		if (Character.TYPE.equals(type))
			return new CharFieldSerializer(field);
		if (Short.TYPE.equals(type))
			return new ShortFieldSerializer(field);
		if (Integer.TYPE.equals(type))
			return new IntFieldSerializer(field);
		if (Long.TYPE.equals(type))
			return new LongFieldSerializer(field);
		if (Double.TYPE.equals(type))
			return new DoubleFieldSerializer(field);
		if (Float.TYPE.equals(type))
			return new FloatFieldSerializer(field);
		if (java/lang/String.equals(type))
			return new StringFieldSerializer(field);
		if (java/util/Date.equals(type) || java/sql/Date.equals(type) || java/sql/Timestamp.equals(type) || java/sql/Time.equals(type))
			return new DateFieldSerializer(field);
		else
			return new ObjectFieldSerializer(field);
	}

	static 
	{
		log = Logger.getLogger(com/autohome/hessian/io/UnsafeSerializer.getName());
		boolean isEnabled = false;
		try
		{
			Class unsafe = Class.forName("sun.misc.Unsafe");
			Field theUnsafe = null;
			Field arr$[] = unsafe.getDeclaredFields();
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				Field field = arr$[i$];
				if (field.getName().equals("theUnsafe"))
					theUnsafe = field;
			}

			if (theUnsafe != null)
			{
				theUnsafe.setAccessible(true);
				_unsafe = (Unsafe)theUnsafe.get(null);
			}
			isEnabled = _unsafe != null;
			String unsafeProp = System.getProperty("com.autohome.hessian.unsafe");
			if ("false".equals(unsafeProp))
				isEnabled = false;
		}
		catch (Throwable e)
		{
			log.log(Level.FINER, e.toString(), e);
		}
		_isEnabled = isEnabled;
	}

}
