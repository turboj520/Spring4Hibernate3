// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   SerializerFactory.java

package com.autohome.hessian.io;

import com.autohome.burlap.io.BurlapRemoteObject;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.ref.SoftReference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

// Referenced classes of package com.autohome.hessian.io:
//			AbstractSerializerFactory, CollectionSerializer, MapSerializer, ObjectSerializer, 
//			Serializer, HessianRemoteObject, RemoteSerializer, WriteReplaceSerializer, 
//			ArraySerializer, ThrowableSerializer, InputStreamSerializer, EnumSerializer, 
//			AnnotationSerializer, Deserializer, CollectionDeserializer, MapDeserializer, 
//			AnnotationDeserializer, ObjectDeserializer, ArrayDeserializer, EnumDeserializer, 
//			ClassDeserializer, UnsafeDeserializer, JavaDeserializer, HessianHandle, 
//			BasicDeserializer, HessianRemote, HessianProtocolException, ContextSerializerFactory, 
//			UnsafeSerializer, JavaSerializer, IteratorSerializer, CalendarSerializer, 
//			EnumerationSerializer, EnumerationDeserializer, InputStreamDeserializer, RemoteDeserializer, 
//			AbstractHessianInput

public class SerializerFactory extends AbstractSerializerFactory
{

	private static final Logger log = Logger.getLogger(com/autohome/hessian/io/SerializerFactory.getName());
	private static final Deserializer OBJECT_DESERIALIZER = new BasicDeserializer(13);
	private static final ClassLoader _systemClassLoader;
	private static final HashMap _staticTypeMap;
	private static final WeakHashMap _defaultFactoryRefMap = new WeakHashMap();
	private ContextSerializerFactory _contextFactory;
	private ClassLoader _loader;
	protected Serializer _defaultSerializer;
	protected ArrayList _factories;
	protected CollectionSerializer _collectionSerializer;
	protected MapSerializer _mapSerializer;
	private Deserializer _hashMapDeserializer;
	private Deserializer _arrayListDeserializer;
	private ConcurrentHashMap _cachedSerializerMap;
	private ConcurrentHashMap _cachedDeserializerMap;
	private HashMap _cachedTypeDeserializerMap;
	private boolean _isAllowNonSerializable;
	private boolean _isEnableUnsafeSerializer;

	public SerializerFactory()
	{
		this(Thread.currentThread().getContextClassLoader());
	}

	public SerializerFactory(ClassLoader loader)
	{
		_factories = new ArrayList();
		_isEnableUnsafeSerializer = UnsafeSerializer.isEnabled() && UnsafeDeserializer.isEnabled();
		_loader = loader;
		_contextFactory = ContextSerializerFactory.create(loader);
	}

	public static SerializerFactory createDefault()
	{
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		WeakHashMap weakhashmap = _defaultFactoryRefMap;
		JVM INSTR monitorenter ;
		SerializerFactory factory;
		SoftReference factoryRef = (SoftReference)_defaultFactoryRefMap.get(loader);
		factory = null;
		if (factoryRef != null)
			factory = (SerializerFactory)factoryRef.get();
		if (factory == null)
		{
			factory = new SerializerFactory();
			factoryRef = new SoftReference(factory);
			_defaultFactoryRefMap.put(loader, factoryRef);
		}
		return factory;
		Exception exception;
		exception;
		throw exception;
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

	public Serializer getObjectSerializer(Class cl)
		throws HessianProtocolException
	{
		Serializer serializer = getSerializer(cl);
		if (serializer instanceof ObjectSerializer)
			return ((ObjectSerializer)serializer).getObjectSerializer();
		else
			return serializer;
	}

	public Serializer getSerializer(Class cl)
		throws HessianProtocolException
	{
		Serializer serializer;
		if (_cachedSerializerMap != null)
		{
			serializer = (Serializer)_cachedSerializerMap.get(cl);
			if (serializer != null)
				return serializer;
		}
		serializer = loadSerializer(cl);
		if (_cachedSerializerMap == null)
			_cachedSerializerMap = new ConcurrentHashMap(8);
		_cachedSerializerMap.put(cl, serializer);
		return serializer;
	}

	protected Serializer loadSerializer(Class cl)
		throws HessianProtocolException
	{
		Serializer serializer = null;
		ContextSerializerFactory factory;
		for (int i = 0; _factories != null && i < _factories.size(); i++)
		{
			factory = (AbstractSerializerFactory)_factories.get(i);
			serializer = factory.getSerializer(cl);
			if (serializer != null)
				return serializer;
		}

		serializer = _contextFactory.getSerializer(cl.getName());
		if (serializer != null)
			return serializer;
		ClassLoader loader = cl.getClassLoader();
		if (loader == null)
			loader = _systemClassLoader;
		factory = null;
		factory = ContextSerializerFactory.create(loader);
		serializer = factory.getCustomSerializer(cl);
		if (serializer != null)
			return serializer;
		if (com/autohome/hessian/io/HessianRemoteObject.isAssignableFrom(cl))
			return new RemoteSerializer();
		if (com/autohome/burlap/io/BurlapRemoteObject.isAssignableFrom(cl))
			return new RemoteSerializer();
		if (JavaSerializer.getWriteReplace(cl) != null)
		{
			Serializer baseSerializer = getDefaultSerializer(cl);
			return new WriteReplaceSerializer(cl, _loader, baseSerializer);
		}
		if (java/util/Map.isAssignableFrom(cl))
		{
			if (_mapSerializer == null)
				_mapSerializer = new MapSerializer();
			return _mapSerializer;
		}
		if (java/util/Collection.isAssignableFrom(cl))
		{
			if (_collectionSerializer == null)
				_collectionSerializer = new CollectionSerializer();
			return _collectionSerializer;
		}
		if (cl.isArray())
			return new ArraySerializer();
		if (java/lang/Throwable.isAssignableFrom(cl))
			return new ThrowableSerializer(cl, getClassLoader());
		if (java/io/InputStream.isAssignableFrom(cl))
			return new InputStreamSerializer();
		if (java/util/Iterator.isAssignableFrom(cl))
			return IteratorSerializer.create();
		if (java/util/Calendar.isAssignableFrom(cl))
			return CalendarSerializer.SER;
		if (java/util/Enumeration.isAssignableFrom(cl))
			return EnumerationSerializer.create();
		if (java/lang/Enum.isAssignableFrom(cl))
			return new EnumSerializer(cl);
		if (java/lang/annotation/Annotation.isAssignableFrom(cl))
			return new AnnotationSerializer(cl);
		else
			return getDefaultSerializer(cl);
	}

	protected Serializer getDefaultSerializer(Class cl)
	{
		if (_defaultSerializer != null)
			return _defaultSerializer;
		if (!java/io/Serializable.isAssignableFrom(cl) && !_isAllowNonSerializable)
			throw new IllegalStateException((new StringBuilder()).append("Serialized class ").append(cl.getName()).append(" must implement java.io.Serializable").toString());
		if (_isEnableUnsafeSerializer && JavaSerializer.getWriteReplace(cl) == null)
			return UnsafeSerializer.create(cl);
		else
			return JavaSerializer.create(cl);
	}

	public Deserializer getDeserializer(Class cl)
		throws HessianProtocolException
	{
		Deserializer deserializer;
		if (_cachedDeserializerMap != null)
		{
			deserializer = (Deserializer)_cachedDeserializerMap.get(cl);
			if (deserializer != null)
				return deserializer;
		}
		deserializer = loadDeserializer(cl);
		if (_cachedDeserializerMap == null)
			_cachedDeserializerMap = new ConcurrentHashMap(8);
		_cachedDeserializerMap.put(cl, deserializer);
		return deserializer;
	}

	protected Deserializer loadDeserializer(Class cl)
		throws HessianProtocolException
	{
		Deserializer deserializer = null;
		for (int i = 0; deserializer == null && _factories != null && i < _factories.size(); i++)
		{
			AbstractSerializerFactory factory = (AbstractSerializerFactory)_factories.get(i);
			deserializer = factory.getDeserializer(cl);
		}

		if (deserializer != null)
			return deserializer;
		deserializer = _contextFactory.getDeserializer(cl.getName());
		if (deserializer != null)
			return deserializer;
		ContextSerializerFactory factory = null;
		if (cl.getClassLoader() != null)
			factory = ContextSerializerFactory.create(cl.getClassLoader());
		else
			factory = ContextSerializerFactory.create(_systemClassLoader);
		deserializer = factory.getCustomDeserializer(cl);
		if (deserializer != null)
			return deserializer;
		if (java/util/Collection.isAssignableFrom(cl))
			deserializer = new CollectionDeserializer(cl);
		else
		if (java/util/Map.isAssignableFrom(cl))
			deserializer = new MapDeserializer(cl);
		else
		if (java/lang/annotation/Annotation.isAssignableFrom(cl))
			deserializer = new AnnotationDeserializer(cl);
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
		return deserializer;
	}

	protected Deserializer getCustomDeserializer(Class cl)
	{
		Deserializer ser;
		Class serClass = Class.forName((new StringBuilder()).append(cl.getName()).append("HessianDeserializer").toString(), false, cl.getClassLoader());
		ser = (Deserializer)serClass.newInstance();
		return ser;
		ClassNotFoundException e;
		e;
		log.log(Level.FINEST, e.toString(), e);
		return null;
		e;
		log.log(Level.FINE, e.toString(), e);
		return null;
	}

	protected Deserializer getDefaultDeserializer(Class cl)
	{
		if (java/io/InputStream.equals(cl))
			return InputStreamDeserializer.DESER;
		if (_isEnableUnsafeSerializer)
			return new UnsafeDeserializer(cl);
		else
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
		if (cl == null || cl.equals(reader.getType()) || cl.isAssignableFrom(reader.getType()) || reader.isReadResolve() || com/autohome/hessian/io/HessianHandle.isAssignableFrom(reader.getType()))
			return reader;
		if (log.isLoggable(Level.FINE))
			log.fine((new StringBuilder()).append("hessian: expected deserializer '").append(cl.getName()).append("' at '").append(type).append("' (").append(reader.getType().getName()).append(")").toString());
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
		if (type.contains("System.Collections.Generic.List") || type.contains("System.Collections.Generic.IList"))
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
		Deserializer deserializer = new BasicDeserializer(type);
		_staticTypeMap.put(typeName, deserializer);
	}

	static 
	{
		_staticTypeMap = new HashMap();
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
		Deserializer objectDeserializer = new JavaDeserializer(java/lang/Object);
		_staticTypeMap.put("object", objectDeserializer);
		_staticTypeMap.put(com/autohome/hessian/io/HessianRemote.getName(), RemoteDeserializer.DESER);
		ClassLoader systemClassLoader = null;
		try
		{
			systemClassLoader = ClassLoader.getSystemClassLoader();
		}
		catch (Exception e) { }
		_systemClassLoader = systemClassLoader;
	}
}
