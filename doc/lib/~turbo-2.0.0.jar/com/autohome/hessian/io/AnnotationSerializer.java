// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AnnotationSerializer.java

package com.autohome.hessian.io;

import com.autohome.hessian.HessianException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

// Referenced classes of package com.autohome.hessian.io:
//			AbstractSerializer, HessianMethodSerializationException, AbstractHessianOutput

public class AnnotationSerializer extends AbstractSerializer
{
	static class DateMethodSerializer extends MethodSerializer
	{

		static final MethodSerializer SER = new DateMethodSerializer();

		void serialize(AbstractHessianOutput out, Object obj, Method method)
			throws IOException
		{
			Date value = null;
			try
			{
				value = (Date)method.invoke(obj, new Object[0]);
			}
			catch (InvocationTargetException e)
			{
				throw AnnotationSerializer.error(method, e.getCause());
			}
			catch (IllegalAccessException e)
			{
				AnnotationSerializer.log.log(Level.FINE, e.toString(), e);
			}
			if (value == null)
				out.writeNull();
			else
				out.writeUTCDate(value.getTime());
		}


		DateMethodSerializer()
		{
		}
	}

	static class StringMethodSerializer extends MethodSerializer
	{

		static final MethodSerializer SER = new StringMethodSerializer();

		void serialize(AbstractHessianOutput out, Object obj, Method method)
			throws IOException
		{
			String value = null;
			try
			{
				value = (String)method.invoke(obj, new Object[0]);
			}
			catch (InvocationTargetException e)
			{
				throw AnnotationSerializer.error(method, e.getCause());
			}
			catch (IllegalAccessException e)
			{
				AnnotationSerializer.log.log(Level.FINE, e.toString(), e);
			}
			out.writeString(value);
		}


		StringMethodSerializer()
		{
		}
	}

	static class DoubleMethodSerializer extends MethodSerializer
	{

		static final MethodSerializer SER = new DoubleMethodSerializer();

		void serialize(AbstractHessianOutput out, Object obj, Method method)
			throws IOException
		{
			double value = 0.0D;
			try
			{
				value = ((Double)method.invoke(obj, new Object[0])).doubleValue();
			}
			catch (InvocationTargetException e)
			{
				throw AnnotationSerializer.error(method, e.getCause());
			}
			catch (IllegalAccessException e)
			{
				AnnotationSerializer.log.log(Level.FINE, e.toString(), e);
			}
			out.writeDouble(value);
		}


		DoubleMethodSerializer()
		{
		}
	}

	static class LongMethodSerializer extends MethodSerializer
	{

		static final MethodSerializer SER = new LongMethodSerializer();

		void serialize(AbstractHessianOutput out, Object obj, Method method)
			throws IOException
		{
			long value = 0L;
			try
			{
				value = ((Long)method.invoke(obj, new Object[0])).longValue();
			}
			catch (InvocationTargetException e)
			{
				throw AnnotationSerializer.error(method, e.getCause());
			}
			catch (IllegalAccessException e)
			{
				AnnotationSerializer.log.log(Level.FINE, e.toString(), e);
			}
			out.writeLong(value);
		}


		LongMethodSerializer()
		{
		}
	}

	static class IntMethodSerializer extends MethodSerializer
	{

		static final MethodSerializer SER = new IntMethodSerializer();

		void serialize(AbstractHessianOutput out, Object obj, Method method)
			throws IOException
		{
			int value = 0;
			try
			{
				value = ((Integer)method.invoke(obj, new Object[0])).intValue();
			}
			catch (InvocationTargetException e)
			{
				throw AnnotationSerializer.error(method, e.getCause());
			}
			catch (IllegalAccessException e)
			{
				AnnotationSerializer.log.log(Level.FINE, e.toString(), e);
			}
			out.writeInt(value);
		}


		IntMethodSerializer()
		{
		}
	}

	static class BooleanMethodSerializer extends MethodSerializer
	{

		static final MethodSerializer SER = new BooleanMethodSerializer();

		void serialize(AbstractHessianOutput out, Object obj, Method method)
			throws IOException
		{
			boolean value = false;
			try
			{
				value = ((Boolean)method.invoke(obj, new Object[0])).booleanValue();
			}
			catch (InvocationTargetException e)
			{
				throw AnnotationSerializer.error(method, e.getCause());
			}
			catch (IllegalAccessException e)
			{
				AnnotationSerializer.log.log(Level.FINE, e.toString(), e);
			}
			out.writeBoolean(value);
		}


		BooleanMethodSerializer()
		{
		}
	}

	static class MethodSerializer
	{

		static final MethodSerializer SER = new MethodSerializer();

		void serialize(AbstractHessianOutput out, Object obj, Method method)
			throws IOException
		{
			Object value = null;
			try
			{
				value = method.invoke(obj, new Object[0]);
			}
			catch (InvocationTargetException e)
			{
				throw AnnotationSerializer.error(method, e.getCause());
			}
			catch (IllegalAccessException e)
			{
				AnnotationSerializer.log.log(Level.FINE, e.toString(), e);
			}
			try
			{
				out.writeObject(value);
			}
			catch (Exception e)
			{
				throw AnnotationSerializer.error(method, e);
			}
		}


		MethodSerializer()
		{
		}
	}


	private static final Logger log = Logger.getLogger(com/autohome/hessian/io/AnnotationSerializer.getName());
	private static Object NULL_ARGS[] = new Object[0];
	private Class _annType;
	private Method _methods[];
	private MethodSerializer _methodSerializers[];

	public AnnotationSerializer(Class annType)
	{
		if (!java/lang/annotation/Annotation.isAssignableFrom(annType))
			throw new IllegalStateException((new StringBuilder()).append(annType.getName()).append(" is invalid because it is not a java.lang.annotation.Annotation").toString());
		else
			return;
	}

	public void writeObject(Object obj, AbstractHessianOutput out)
		throws IOException
	{
		if (out.addRef(obj))
			return;
		init(((Annotation)obj).annotationType());
		int ref = out.writeObjectBegin(_annType.getName());
		if (ref < -1)
		{
			writeObject10(obj, out);
		} else
		{
			if (ref == -1)
			{
				writeDefinition20(out);
				out.writeObjectBegin(_annType.getName());
			}
			writeInstance(obj, out);
		}
	}

	protected void writeObject10(Object obj, AbstractHessianOutput out)
		throws IOException
	{
		for (int i = 0; i < _methods.length; i++)
		{
			Method method = _methods[i];
			out.writeString(method.getName());
			_methodSerializers[i].serialize(out, obj, method);
		}

		out.writeMapEnd();
	}

	private void writeDefinition20(AbstractHessianOutput out)
		throws IOException
	{
		out.writeClassFieldLength(_methods.length);
		for (int i = 0; i < _methods.length; i++)
		{
			Method method = _methods[i];
			out.writeString(method.getName());
		}

	}

	public void writeInstance(Object obj, AbstractHessianOutput out)
		throws IOException
	{
		for (int i = 0; i < _methods.length; i++)
		{
			Method method = _methods[i];
			_methodSerializers[i].serialize(out, obj, method);
		}

	}

	private void init(Class cl)
	{
label0:
		{
			synchronized (this)
			{
				if (_annType == null)
					break label0;
			}
			return;
		}
		_annType = cl;
		ArrayList methods = new ArrayList();
		Method arr$[] = _annType.getDeclaredMethods();
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			Method method = arr$[i$];
			if (!method.getName().equals("hashCode") && !method.getName().equals("toString") && !method.getName().equals("annotationType") && method.getParameterTypes().length == 0)
			{
				methods.add(method);
				method.setAccessible(true);
			}
		}

		if (_annType == null)
			throw new IllegalStateException((new StringBuilder()).append(cl.getName()).append(" is invalid because it does not have a valid annotationType()").toString());
		_methods = new Method[methods.size()];
		methods.toArray(_methods);
		_methodSerializers = new MethodSerializer[_methods.length];
		for (int i = 0; i < _methods.length; i++)
			_methodSerializers[i] = getMethodSerializer(_methods[i].getReturnType());

		annotationserializer;
		JVM INSTR monitorexit ;
		  goto _L1
		exception;
		throw exception;
_L1:
	}

	private Class getAnnotationType(Class cl)
	{
		if (cl == null)
			return null;
		if (java/lang/annotation/Annotation.equals(cl.getSuperclass()))
			return cl;
		Class ifaces[] = cl.getInterfaces();
		if (ifaces != null)
		{
			Class arr$[] = ifaces;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				Class iface = arr$[i$];
				if (iface.equals(java/lang/annotation/Annotation))
					return cl;
				Class annType = getAnnotationType(iface);
				if (annType != null)
					return annType;
			}

		}
		return getAnnotationType(cl.getSuperclass());
	}

	private static MethodSerializer getMethodSerializer(Class type)
	{
		if (Integer.TYPE.equals(type) || Byte.TYPE.equals(type) || Short.TYPE.equals(type) || Integer.TYPE.equals(type))
			return IntMethodSerializer.SER;
		if (Long.TYPE.equals(type))
			return LongMethodSerializer.SER;
		if (Double.TYPE.equals(type) || Float.TYPE.equals(type))
			return DoubleMethodSerializer.SER;
		if (Boolean.TYPE.equals(type))
			return BooleanMethodSerializer.SER;
		if (java/lang/String.equals(type))
			return StringMethodSerializer.SER;
		if (java/util/Date.equals(type) || java/sql/Date.equals(type) || java/sql/Timestamp.equals(type) || java/sql/Time.equals(type))
			return DateMethodSerializer.SER;
		else
			return MethodSerializer.SER;
	}

	static HessianException error(Method method, Throwable cause)
	{
		String msg = (new StringBuilder()).append(method.getDeclaringClass().getSimpleName()).append(".").append(method.getName()).append("(): ").append(cause).toString();
		throw new HessianMethodSerializationException(msg, cause);
	}


}
