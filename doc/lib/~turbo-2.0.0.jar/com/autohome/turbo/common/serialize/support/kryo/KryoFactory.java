// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   KryoFactory.java

package com.autohome.turbo.common.serialize.support.kryo;

import com.autohome.turbo.common.serialize.support.SerializableClassRegistry;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import de.javakaffee.kryoserializers.*;
import java.lang.reflect.InvocationHandler;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

// Referenced classes of package com.autohome.turbo.common.serialize.support.kryo:
//			CompatibleKryo, PooledKryoFactory

public abstract class KryoFactory
{

	private static final KryoFactory factory = new PooledKryoFactory();
	private final Set registrations = new LinkedHashSet();
	private boolean registrationRequired;
	private volatile boolean kryoCreated;

	protected KryoFactory()
	{
	}

	public static KryoFactory getDefaultFactory()
	{
		return factory;
	}

	public void registerClass(Class clazz)
	{
		if (kryoCreated)
		{
			throw new IllegalStateException("Can't register class after creating kryo instance");
		} else
		{
			registrations.add(clazz);
			return;
		}
	}

	protected Kryo createKryo()
	{
		if (!kryoCreated)
			kryoCreated = true;
		Kryo kryo = new CompatibleKryo();
		kryo.setRegistrationRequired(registrationRequired);
		kryo.register(Arrays.asList(new String[] {
			""
		}).getClass(), new ArraysAsListSerializer());
		kryo.register(java/util/GregorianCalendar, new GregorianCalendarSerializer());
		kryo.register(java/lang/reflect/InvocationHandler, new JdkProxySerializer());
		kryo.register(java/math/BigDecimal, new com.esotericsoftware.kryo.serializers.DefaultSerializers.BigDecimalSerializer());
		kryo.register(java/math/BigInteger, new com.esotericsoftware.kryo.serializers.DefaultSerializers.BigIntegerSerializer());
		kryo.register(java/util/regex/Pattern, new RegexSerializer());
		kryo.register(java/util/BitSet, new BitSetSerializer());
		kryo.register(java/net/URI, new URISerializer());
		kryo.register(java/util/UUID, new UUIDSerializer());
		UnmodifiableCollectionsSerializer.registerSerializers(kryo);
		SynchronizedCollectionsSerializer.registerSerializers(kryo);
		kryo.register(java/util/HashMap);
		kryo.register(java/util/ArrayList);
		kryo.register(java/util/LinkedList);
		kryo.register(java/util/HashSet);
		kryo.register(java/util/TreeSet);
		kryo.register(java/util/Hashtable);
		kryo.register(java/util/Date);
		kryo.register(java/util/Calendar);
		kryo.register(java/util/concurrent/ConcurrentHashMap);
		kryo.register(java/text/SimpleDateFormat);
		kryo.register(java/util/GregorianCalendar);
		kryo.register(java/util/Vector);
		kryo.register(java/util/BitSet);
		kryo.register(java/lang/StringBuffer);
		kryo.register(java/lang/StringBuilder);
		kryo.register(java/lang/Object);
		kryo.register([Ljava/lang/Object;);
		kryo.register([Ljava/lang/String;);
		kryo.register([B);
		kryo.register([C);
		kryo.register([I);
		kryo.register([F);
		kryo.register([D);
		Class clazz;
		for (Iterator i$ = registrations.iterator(); i$.hasNext(); kryo.register(clazz))
			clazz = (Class)i$.next();

		Class clazz;
		for (Iterator i$ = SerializableClassRegistry.getRegisteredClasses().iterator(); i$.hasNext(); kryo.register(clazz))
			clazz = (Class)i$.next();

		return kryo;
	}

	public void returnKryo(Kryo kryo1)
	{
	}

	public void setRegistrationRequired(boolean registrationRequired)
	{
		this.registrationRequired = registrationRequired;
	}

	public void close()
	{
	}

	public abstract Kryo getKryo();

}
