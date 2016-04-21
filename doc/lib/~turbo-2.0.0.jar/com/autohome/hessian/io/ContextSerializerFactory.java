// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ContextSerializerFactory.java

package com.autohome.hessian.io;

import com.autohome.hessian.HessianException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

// Referenced classes of package com.autohome.hessian.io:
//			Serializer, Deserializer, BasicSerializer, BasicDeserializer, 
//			JavaDeserializer, ClassSerializer, SqlDateSerializer, SqlDateDeserializer, 
//			StackTraceElementDeserializer, AbstractSerializer, AbstractDeserializer

public class ContextSerializerFactory
{

	private static final Logger log = Logger.getLogger(com/autohome/hessian/io/ContextSerializerFactory.getName());
	private static Deserializer OBJECT_DESERIALIZER = new BasicDeserializer(13);
	private static final WeakHashMap _contextRefMap = new WeakHashMap();
	private static final ClassLoader _systemClassLoader;
	private static HashMap _staticSerializerMap;
	private static HashMap _staticDeserializerMap;
	private static HashMap _staticClassNameMap;
	private ContextSerializerFactory _parent;
	private ClassLoader _loader;
	private final HashSet _serializerFiles = new HashSet();
	private final HashSet _deserializerFiles = new HashSet();
	private final HashMap _serializerClassMap = new HashMap();
	private final ConcurrentHashMap _customSerializerMap = new ConcurrentHashMap();
	private final HashMap _serializerInterfaceMap = new HashMap();
	private final HashMap _deserializerClassMap = new HashMap();
	private final HashMap _deserializerClassNameMap = new HashMap();
	private final ConcurrentHashMap _customDeserializerMap = new ConcurrentHashMap();
	private final HashMap _deserializerInterfaceMap = new HashMap();

	public ContextSerializerFactory(ContextSerializerFactory parent, ClassLoader loader)
	{
		if (loader == null)
			loader = _systemClassLoader;
		_loader = loader;
		init();
	}

	public static ContextSerializerFactory create()
	{
		return create(Thread.currentThread().getContextClassLoader());
	}

	public static ContextSerializerFactory create(ClassLoader loader)
	{
		WeakHashMap weakhashmap = _contextRefMap;
		JVM INSTR monitorenter ;
		ContextSerializerFactory factory;
		SoftReference factoryRef = (SoftReference)_contextRefMap.get(loader);
		factory = null;
		if (factoryRef != null)
			factory = (ContextSerializerFactory)factoryRef.get();
		if (factory == null)
		{
			ContextSerializerFactory parent = null;
			if (loader != null)
				parent = create(loader.getParent());
			factory = new ContextSerializerFactory(parent, loader);
			factoryRef = new SoftReference(factory);
			_contextRefMap.put(loader, factoryRef);
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

	public Serializer getSerializer(String className)
	{
		Serializer serializer = (Serializer)_serializerClassMap.get(className);
		if (serializer == AbstractSerializer.NULL)
			return null;
		else
			return serializer;
	}

	public Serializer getCustomSerializer(Class cl)
	{
		Serializer serializer = (Serializer)_customSerializerMap.get(cl.getName());
		if (serializer == AbstractSerializer.NULL)
			return null;
		if (serializer != null)
			return serializer;
		Serializer ser;
		Class serClass = Class.forName((new StringBuilder()).append(cl.getName()).append("HessianSerializer").toString(), false, cl.getClassLoader());
		ser = (Serializer)serClass.newInstance();
		_customSerializerMap.put(cl.getName(), ser);
		return ser;
		ClassNotFoundException e;
		e;
		log.log(Level.ALL, e.toString(), e);
		break MISSING_BLOCK_LABEL_115;
		e;
		throw new HessianException(e);
		_customSerializerMap.put(cl.getName(), AbstractSerializer.NULL);
		return null;
	}

	public Deserializer getDeserializer(String className)
	{
		Deserializer deserializer = (Deserializer)_deserializerClassMap.get(className);
		if (deserializer == AbstractDeserializer.NULL)
			return null;
		else
			return deserializer;
	}

	public Deserializer getCustomDeserializer(Class cl)
	{
		Deserializer deserializer = (Deserializer)_customDeserializerMap.get(cl.getName());
		if (deserializer == AbstractDeserializer.NULL)
			return null;
		if (deserializer != null)
			return deserializer;
		Deserializer ser;
		Class serClass = Class.forName((new StringBuilder()).append(cl.getName()).append("HessianDeserializer").toString(), false, cl.getClassLoader());
		ser = (Deserializer)serClass.newInstance();
		_customDeserializerMap.put(cl.getName(), ser);
		return ser;
		ClassNotFoundException e;
		e;
		log.log(Level.ALL, e.toString(), e);
		break MISSING_BLOCK_LABEL_115;
		e;
		throw new HessianException(e);
		_customDeserializerMap.put(cl.getName(), AbstractDeserializer.NULL);
		return null;
	}

	private void init()
	{
		if (_parent != null)
		{
			_serializerFiles.addAll(_parent._serializerFiles);
			_deserializerFiles.addAll(_parent._deserializerFiles);
			_serializerClassMap.putAll(_parent._serializerClassMap);
			_deserializerClassMap.putAll(_parent._deserializerClassMap);
		}
		if (_parent == null)
		{
			_serializerClassMap.putAll(_staticSerializerMap);
			_deserializerClassMap.putAll(_staticDeserializerMap);
			_deserializerClassNameMap.putAll(_staticClassNameMap);
		}
		HashMap classMap = new HashMap();
		initSerializerFiles("META-INF/hessian/serializers", _serializerFiles, classMap, com/autohome/hessian/io/Serializer);
		for (Iterator i$ = classMap.entrySet().iterator(); i$.hasNext();)
		{
			java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
			try
			{
				Serializer ser = (Serializer)((Class)entry.getValue()).newInstance();
				if (((Class)entry.getKey()).isInterface())
					_serializerInterfaceMap.put(entry.getKey(), ser);
				else
					_serializerClassMap.put(((Class)entry.getKey()).getName(), ser);
			}
			catch (Exception e)
			{
				throw new HessianException(e);
			}
		}

		classMap = new HashMap();
		initSerializerFiles("META-INF/hessian/deserializers", _deserializerFiles, classMap, com/autohome/hessian/io/Deserializer);
		for (Iterator i$ = classMap.entrySet().iterator(); i$.hasNext();)
		{
			java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
			try
			{
				Deserializer ser = (Deserializer)((Class)entry.getValue()).newInstance();
				if (((Class)entry.getKey()).isInterface())
					_deserializerInterfaceMap.put(entry.getKey(), ser);
				else
					_deserializerClassMap.put(((Class)entry.getKey()).getName(), ser);
			}
			catch (Exception e)
			{
				throw new HessianException(e);
			}
		}

	}

	private void initSerializerFiles(String fileName, HashSet fileList, HashMap classMap, Class type)
	{
		ClassLoader classLoader;
		Enumeration iter;
		URL url;
		InputStream is;
		Exception exception;
		try
		{
			classLoader = getClassLoader();
			if (classLoader == null)
				return;
		}
		catch (RuntimeException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new HessianException(e);
		}
		iter = classLoader.getResources(fileName);
_L2:
		if (!iter.hasMoreElements())
			break; /* Loop/switch isn't completed */
		url = (URL)iter.nextElement();
		if (fileList.contains(url.toString()))
			continue; /* Loop/switch isn't completed */
		fileList.add(url.toString());
		is = null;
		is = url.openStream();
		Properties props = new Properties();
		props.load(is);
		Iterator i$ = props.entrySet().iterator();
		do
		{
			if (!i$.hasNext())
				break;
			java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
			String apiName = (String)entry.getKey();
			String serializerName = (String)entry.getValue();
			Class apiClass = null;
			Class serializerClass = null;
			try
			{
				apiClass = Class.forName(apiName, false, classLoader);
			}
			catch (ClassNotFoundException e)
			{
				log.fine((new StringBuilder()).append(url).append(": ").append(apiName).append(" is not available in this context: ").append(getClassLoader()).toString());
				continue;
			}
			try
			{
				serializerClass = Class.forName(serializerName, false, classLoader);
			}
			catch (ClassNotFoundException e)
			{
				log.fine((new StringBuilder()).append(url).append(": ").append(serializerName).append(" is not available in this context: ").append(getClassLoader()).toString());
				continue;
			}
			if (!type.isAssignableFrom(serializerClass))
				throw new HessianException((new StringBuilder()).append(url).append(": ").append(serializerClass.getName()).append(" is invalid because it does not implement ").append(type.getName()).toString());
			classMap.put(apiClass, serializerClass);
		} while (true);
		if (is != null)
			is.close();
		if (true) goto _L2; else goto _L1
		exception;
		if (is != null)
			is.close();
		throw exception;
_L1:
	}

	private static void addBasic(Class cl, String typeName, int type)
	{
		_staticSerializerMap.put(cl.getName(), new BasicSerializer(type));
		Deserializer deserializer = new BasicDeserializer(type);
		_staticDeserializerMap.put(cl.getName(), deserializer);
		_staticClassNameMap.put(typeName, deserializer);
	}

	static 
	{
		_staticSerializerMap = new HashMap();
		_staticDeserializerMap = new HashMap();
		_staticClassNameMap = new HashMap();
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
		_staticDeserializerMap.put("object", objectDeserializer);
		_staticClassNameMap.put("object", objectDeserializer);
		_staticSerializerMap.put(java/lang/Class.getName(), new ClassSerializer());
		_staticDeserializerMap.put(java/lang/Number.getName(), new BasicDeserializer(12));
		_staticSerializerMap.put(java/sql/Date.getName(), new SqlDateSerializer());
		_staticSerializerMap.put(java/sql/Time.getName(), new SqlDateSerializer());
		_staticSerializerMap.put(java/sql/Timestamp.getName(), new SqlDateSerializer());
		_staticDeserializerMap.put(java/sql/Date.getName(), new SqlDateDeserializer(java/sql/Date));
		_staticDeserializerMap.put(java/sql/Time.getName(), new SqlDateDeserializer(java/sql/Time));
		_staticDeserializerMap.put(java/sql/Timestamp.getName(), new SqlDateDeserializer(java/sql/Timestamp));
		_staticDeserializerMap.put(java/lang/StackTraceElement.getName(), new StackTraceElementDeserializer());
		ClassLoader systemClassLoader = null;
		try
		{
			systemClassLoader = ClassLoader.getSystemClassLoader();
		}
		catch (Exception e) { }
		_systemClassLoader = systemClassLoader;
	}
}
