// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   SerializerFactory.java

package com.autohome.com.caucho.hessian.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.ObjectName;

// Referenced classes of package com.autohome.com.caucho.hessian.io:
//			AbstractSerializerFactory, CollectionSerializer, MapSerializer, Serializer, 
//			JavaSerializer, HessianRemoteObject, RemoteSerializer, ArraySerializer, 
//			ThrowableSerializer, InputStreamSerializer, EnumSerializer, Deserializer, 
//			CollectionDeserializer, MapDeserializer, ObjectDeserializer, ArrayDeserializer, 
//			EnumDeserializer, ClassDeserializer, JavaDeserializer, HessianHandle, 
//			BasicSerializer, BasicDeserializer, ClassSerializer, StringValueSerializer, 
//			StringValueDeserializer, BigIntegerDeserializer, SqlDateSerializer, InputStreamDeserializer, 
//			SqlDateDeserializer, StackTraceElementDeserializer, HessianProtocolException, IteratorSerializer, 
//			EnumerationSerializer, CalendarSerializer, LocaleSerializer, EnumerationDeserializer, 
//			AbstractHessianInput

public class SerializerFactory extends AbstractSerializerFactory
{

	private static final Logger log = Logger.getLogger(com/autohome/com/caucho/hessian/io/SerializerFactory.getName());
	private static Deserializer OBJECT_DESERIALIZER = new BasicDeserializer(13);
	private static HashMap _staticSerializerMap;
	private static HashMap _staticDeserializerMap;
	private static HashMap _staticTypeMap = new HashMap();
	private ClassLoader _loader;
	protected Serializer _defaultSerializer;
	protected ArrayList _factories;
	protected CollectionSerializer _collectionSerializer;
	protected MapSerializer _mapSerializer;
	private Deserializer _hashMapDeserializer;
	private Deserializer _arrayListDeserializer;
	private HashMap _cachedSerializerMap;
	private HashMap _cachedDeserializerMap;
	private HashMap _cachedTypeDeserializerMap;
	private boolean _isAllowNonSerializable;

	public SerializerFactory()
	{
		this(Thread.currentThread().getContextClassLoader());
	}

	public SerializerFactory(ClassLoader loader)
	{
		_factories = new ArrayList();
		_loader = loader;
	}

	public ClassLoader getClassLoader()
	{
		return _loader;
	}

	public void setSendCollectionType(boolean isSendType)
	{
		if (_collectionSerializer == null)
			_collectionSerializer = new CollectionSerializer();
		_collectionSerializer.setSendJavaType(isSendType);
		if (_mapSerializer == null)
			_mapSerializer = new MapSerializer();
		_mapSerializer.setSendJavaType(isSendType);
	}

	public void addFactory(AbstractSerializerFactory factory)
	{
		_factories.add(factory);
	}

	public void setAllowNonSerializable(boolean allow)
	{
		_isAllowNonSerializable = allow;
	}

	public boolean isAllowNonSerializable()
	{
		return _isAllowNonSerializable;
	}

	public Serializer getSerializer(Class cl)
		throws HessianProtocolException
	{
		Serializer serializer = (Serializer)_staticSerializerMap.get(cl);
		if (serializer != null)
			return serializer;
		if (_cachedSerializerMap != null)
		{
			synchronized (_cachedSerializerMap)
			{
				serializer = (Serializer)_cachedSerializerMap.get(cl);
			}
			if (serializer != null)
				return serializer;
		}
		for (int i = 0; serializer == null && _factories != null && i < _factories.size(); i++)
		{
			AbstractSerializerFactory factory = (AbstractSerializerFactory)_factories.get(i);
			serializer = factory.getSerializer(cl);
		}

		if (serializer == null)
			if (JavaSerializer.getWriteReplace(cl) != null)
				serializer = new JavaSerializer(cl, _loader);
			else
			if (com/autohome/com/caucho/hessian/io/HessianRemoteObject.isAssignableFrom(cl))
				serializer = new RemoteSerializer();
			else
			if (java/util/Map.isAssignableFrom(cl))
			{
				if (_mapSerializer == null)
					_mapSerializer = new MapSerializer();
				serializer = _mapSerializer;
			} else
			if (java/util/Collection.isAssignableFrom(cl))
			{
				if (_collectionSerializer == null)
					_collectionSerializer = new CollectionSerializer();
				serializer = _collectionSerializer;
			} else
			if (cl.isArray())
				serializer = new ArraySerializer();
			else
			if (java/lang/Throwable.isAssignableFrom(cl))
				serializer = new ThrowableSerializer(cl, getClassLoader());
			else
			if (java/io/InputStream.isAssignableFrom(cl))
				serializer = new InputStreamSerializer();
			else
			if (java/util/Iterator.isAssignableFrom(cl))
				serializer = IteratorSerializer.create();
			else
			if (java/util/Enumeration.isAssignableFrom(cl))
				serializer = EnumerationSerializer.create();
			else
			if (java/util/Calendar.isAssignableFrom(cl))
				serializer = CalendarSerializer.create();
			else
			if (java/util/Locale.isAssignableFrom(cl))
				serializer = LocaleSerializer.create();
			else
			if (java/lang/Enum.isAssignableFrom(cl))
				serializer = new EnumSerializer(cl);
		if (serializer == null)
			serializer = getDefaultSerializer(cl);
		if (_cachedSerializerMap == null)
			_cachedSerializerMap = new HashMap(8);
		synchronized (_cachedSerializerMap)
		{
			_cachedSerializerMap.put(cl, serializer);
		}
		return serializer;
	}

	protected Serializer getDefaultSerializer(Class cl)
	{
		if (_defaultSerializer != null)
			return _defaultSerializer;
		if (!java/io/Serializable.isAssignableFrom(cl) && !_isAllowNonSerializable)
			throw new IllegalStateException((new StringBuilder()).append("Serialized class ").append(cl.getName()).append(" must implement java.io.Serializable").toString());
		else
			return new JavaSerializer(cl, _loader);
	}

	public Deserializer getDeserializer(Class cl)
		throws HessianProtocolException
	{
		Deserializer deserializer = (Deserializer)_staticDeserializerMap.get(cl);
		if (deserializer != null)
			return deserializer;
		if (_cachedDeserializerMap != null)
		{
			synchronized (_cachedDeserializerMap)
			{
				deserializer = (Deserializer)_cachedDeserializerMap.get(cl);
			}
			if (deserializer != null)
				return deserializer;
		}
		for (int i = 0; deserializer == null && _factories != null && i < _factories.size(); i++)
		{
			AbstractSerializerFactory factory = (AbstractSerializerFactory)_factories.get(i);
			deserializer = factory.getDeserializer(cl);
		}

		if (deserializer == null)
			if (java/util/Collection.isAssignableFrom(cl))
				deserializer = new CollectionDeserializer(cl);
			else
			if (java/util/Map.isAssignableFrom(cl))
				deserializer = new MapDeserializer(cl);
			else
			if (cl.isInterface())
				deserializer = new ObjectDeserializer(cl);
			else
			if (cl.isArray())
				deserializer = new ArrayDeserializer(cl.getComponentType());
			else
			if (java/util/Enumeration.isAssignableFrom(cl))
				deserializer = EnumerationDeserializer.create();
			else
			if (java/lang/Enum.isAssignableFrom(cl))
				deserializer = new EnumDeserializer(cl);
			else
			if (java/lang/Class.equals(cl))
				deserializer = new ClassDeserializer(_loader);
			else
				deserializer = getDefaultDeserializer(cl);
		if (_cachedDeserializerMap == null)
			_cachedDeserializerMap = new HashMap(8);
		synchronized (_cachedDeserializerMap)
		{
			_cachedDeserializerMap.put(cl, deserializer);
		}
		return deserializer;
	}

	protected Deserializer getDefaultDeserializer(Class cl)
	{
		return new JavaDeserializer(cl);
	}

	public Object readList(AbstractHessianInput in, int length, String type)
		throws HessianProtocolException, IOException
	{
		Deserializer deserializer = getDeserializer(type);
		if (deserializer != null)
			return deserializer.readList(in, length);
		else
			return (new CollectionDeserializer(java/util/ArrayList)).readList(in, length);
	}

	public Object readMap(AbstractHessianInput in, String type)
		throws HessianProtocolException, IOException
	{
		Deserializer deserializer = getDeserializer(type);
		if (deserializer != null)
			return deserializer.readMap(in);
		if (_hashMapDeserializer != null)
		{
			return _hashMapDeserializer.readMap(in);
		} else
		{
			_hashMapDeserializer = new MapDeserializer(java/util/HashMap);
			return _hashMapDeserializer.readMap(in);
		}
	}

	public Object readObject(AbstractHessianInput in, String type, String fieldNames[])
		throws HessianProtocolException, IOException
	{
		Deserializer deserializer = getDeserializer(type);
		if (deserializer != null)
			return deserializer.readObject(in, fieldNames);
		if (_hashMapDeserializer != null)
		{
			return _hashMapDeserializer.readObject(in, fieldNames);
		} else
		{
			_hashMapDeserializer = new MapDeserializer(java/util/HashMap);
			return _hashMapDeserializer.readObject(in, fieldNames);
		}
	}

	public Deserializer getObjectDeserializer(String type, Class cl)
		throws HessianProtocolException
	{
		Deserializer reader = getObjectDeserializer(type);
		if (cl == null || cl.equals(reader.getType()) || cl.isAssignableFrom(reader.getType()) || com/autohome/com/caucho/hessian/io/HessianHandle.isAssignableFrom(reader.getType()))
			return reader;
		if (log.isLoggable(Level.FINE))
			log.fine((new StringBuilder()).append("hessian: expected '").append(cl.getName()).append("' at '").append(type).append("' (").append(reader.getType().getName()).append(")").toString());
		return getDeserializer(cl);
	}

	public Deserializer getObjectDeserializer(String type)
		throws HessianProtocolException
	{
		Deserializer deserializer = getDeserializer(type);
		if (deserializer != null)
			return deserializer;
		if (_hashMapDeserializer != null)
		{
			return _hashMapDeserializer;
		} else
		{
			_hashMapDeserializer = new MapDeserializer(java/util/HashMap);
			return _hashMapDeserializer;
		}
	}

	public Deserializer getListDeserializer(String type, Class cl)
		throws HessianProtocolException
	{
		Deserializer reader = getListDeserializer(type);
		if (cl == null || cl.equals(reader.getType()) || cl.isAssignableFrom(reader.getType()))
			return reader;
		if (log.isLoggable(Level.FINE))
			log.fine((new StringBuilder()).append("hessian: expected '").append(cl.getName()).append("' at '").append(type).append("' (").append(reader.getType().getName()).append(")").toString());
		return getDeserializer(cl);
	}

	public Deserializer getListDeserializer(String type)
		throws HessianProtocolException
	{
		Deserializer deserializer = getDeserializer(type);
		if (deserializer != null)
			return deserializer;
		if (_arrayListDeserializer != null)
		{
			return _arrayListDeserializer;
		} else
		{
			_arrayListDeserializer = new CollectionDeserializer(java/util/ArrayList);
			return _arrayListDeserializer;
		}
	}

	public Deserializer getDeserializer(String type)
		throws HessianProtocolException
	{
		if (type == null || type.equals(""))
			return null;
		Deserializer deserializer;
		if (_cachedTypeDeserializerMap != null)
		{
			synchronized (_cachedTypeDeserializerMap)
			{
				deserializer = (Deserializer)_cachedTypeDeserializerMap.get(type);
			}
			if (deserializer != null)
				return deserializer;
		}
		deserializer = (Deserializer)_staticTypeMap.get(type);
		if (deserializer != null)
			return deserializer;
		if (type.startsWith("["))
		{
			Deserializer subDeserializer = getDeserializer(type.substring(1));
			if (subDeserializer != null)
				deserializer = new ArrayDeserializer(subDeserializer.getType());
			else
				deserializer = new ArrayDeserializer(java/lang/Object);
		} else
		{
			try
			{
				Class cl = Class.forName(type, false, _loader);
				deserializer = getDeserializer(cl);
			}
			catch (Exception e)
			{
				log.warning((new StringBuilder()).append("Hessian/Burlap: '").append(type).append("' is an unknown class in ").append(_loader).append(":\n").append(e).toString());
				log.log(Level.FINER, e.toString(), e);
			}
		}
		if (deserializer != null)
		{
			if (_cachedTypeDeserializerMap == null)
				_cachedTypeDeserializerMap = new HashMap(8);
			synchronized (_cachedTypeDeserializerMap)
			{
				_cachedTypeDeserializerMap.put(type, deserializer);
			}
		}
		return deserializer;
	}

	private static void addBasic(Class cl, String typeName, int type)
	{
		_staticSerializerMap.put(cl, new BasicSerializer(type));
		Deserializer deserializer = new BasicDeserializer(type);
		_staticDeserializerMap.put(cl, deserializer);
		_staticTypeMap.put(typeName, deserializer);
	}

	static 
	{
		_staticSerializerMap = new HashMap();
		_staticDeserializerMap = new HashMap();
		addBasic(Void.TYPE, "void", 0);
		addBasic(java/lang/Boolean, "boolean", 1);
		addBasic(java/lang/Byte, "byte", 2);
		addBasic(java/lang/Short, "short", 3);
		addBasic(java/lang/Integer, "int", 4);
		addBasic(java/lang/Long, "long", 5);
		addBasic(java/lang/Float, "float", 6);
		addBasic(java/lang/Double, "double", 7);
		addBasic(java/lang/Character, "char", 9);
		addBasic(java/lang/String, "string", 10);
		addBasic(java/lang/Object, "object", 13);
		addBasic(java/util/Date, "date", 11);
		addBasic(Boolean.TYPE, "boolean", 1);
		addBasic(Byte.TYPE, "byte", 2);
		addBasic(Short.TYPE, "short", 3);
		addBasic(Integer.TYPE, "int", 4);
		addBasic(Long.TYPE, "long", 5);
		addBasic(Float.TYPE, "float", 6);
		addBasic(Double.TYPE, "double", 7);
		addBasic(Character.TYPE, "char", 8);
		addBasic([Z, "[boolean", 14);
		addBasic([B, "[byte", 15);
		addBasic([S, "[short", 16);
		addBasic([I, "[int", 17);
		addBasic([J, "[long", 18);
		addBasic([F, "[float", 19);
		addBasic([D, "[double", 20);
		addBasic([C, "[char", 21);
		addBasic([Ljava/lang/String;, "[string", 22);
		addBasic([Ljava/lang/Object;, "[object", 23);
		_staticSerializerMap.put(java/lang/Class, new ClassSerializer());
		_staticDeserializerMap.put(java/lang/Number, new BasicDeserializer(12));
		_staticSerializerMap.put(java/math/BigDecimal, new StringValueSerializer());
		try
		{
			_staticDeserializerMap.put(java/math/BigDecimal, new StringValueDeserializer(java/math/BigDecimal));
			_staticDeserializerMap.put(java/math/BigInteger, new BigIntegerDeserializer());
		}
		catch (Throwable e) { }
		_staticSerializerMap.put(java/io/File, new StringValueSerializer());
		try
		{
			_staticDeserializerMap.put(java/io/File, new StringValueDeserializer(java/io/File));
		}
		catch (Throwable e) { }
		_staticSerializerMap.put(javax/management/ObjectName, new StringValueSerializer());
		try
		{
			_staticDeserializerMap.put(javax/management/ObjectName, new StringValueDeserializer(javax/management/ObjectName));
		}
		catch (Throwable e) { }
		_staticSerializerMap.put(java/sql/Date, new SqlDateSerializer());
		_staticSerializerMap.put(java/sql/Time, new SqlDateSerializer());
		_staticSerializerMap.put(java/sql/Timestamp, new SqlDateSerializer());
		_staticSerializerMap.put(java/io/InputStream, new InputStreamSerializer());
		_staticDeserializerMap.put(java/io/InputStream, new InputStreamDeserializer());
		try
		{
			_staticDeserializerMap.put(java/sql/Date, new SqlDateDeserializer(java/sql/Date));
			_staticDeserializerMap.put(java/sql/Time, new SqlDateDeserializer(java/sql/Time));
			_staticDeserializerMap.put(java/sql/Timestamp, new SqlDateDeserializer(java/sql/Timestamp));
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
		try
		{
			Class stackTrace = java/lang/StackTraceElement;
			_staticDeserializerMap.put(stackTrace, new StackTraceElementDeserializer());
		}
		catch (Throwable e) { }
	}
}
