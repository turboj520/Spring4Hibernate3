// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   GenericJSONConverter.java

package com.autohome.turbo.common.json;

import com.autohome.turbo.common.bytecode.Wrapper;
import com.autohome.turbo.common.io.Bytes;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

// Referenced classes of package com.autohome.turbo.common.json:
//			JSONNode, JSONConverter, JSONWriter

public class GenericJSONConverter
	implements JSONConverter
{
	protected static interface Decoder
	{

		public abstract Object decode(Object obj)
			throws IOException;
	}

	protected static interface Encoder
	{

		public abstract void encode(Object obj, JSONWriter jsonwriter)
			throws IOException;
	}


	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final Map GlobalEncoderMap;
	private static final Map GlobalDecoderMap;

	public GenericJSONConverter()
	{
	}

	public void writeValue(Object obj, JSONWriter jb, boolean writeClass)
		throws IOException
	{
		if (obj == null)
		{
			jb.valueNull();
			return;
		}
		Class c = obj.getClass();
		Encoder encoder = (Encoder)GlobalEncoderMap.get(c);
		if (encoder != null)
			encoder.encode(obj, jb);
		else
		if (obj instanceof JSONNode)
			((JSONNode)obj).writeJSON(this, jb, writeClass);
		else
		if (c.isEnum())
			jb.valueString(((Enum)obj).name());
		else
		if (c.isArray())
		{
			int len = Array.getLength(obj);
			jb.arrayBegin();
			for (int i = 0; i < len; i++)
				writeValue(Array.get(obj, i), jb, writeClass);

			jb.arrayEnd();
		} else
		if (java/util/Map.isAssignableFrom(c))
		{
			jb.objectBegin();
			Iterator i$ = ((Map)obj).entrySet().iterator();
			do
			{
				if (!i$.hasNext())
					break;
				java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
				Object key = entry.getKey();
				if (key != null)
				{
					jb.objectItem(key.toString());
					Object value = entry.getValue();
					if (value == null)
						jb.valueNull();
					else
						writeValue(value, jb, writeClass);
				}
			} while (true);
			jb.objectEnd();
		} else
		if (java/util/Collection.isAssignableFrom(c))
		{
			jb.arrayBegin();
			for (Iterator i$ = ((Collection)obj).iterator(); i$.hasNext();)
			{
				Object item = i$.next();
				if (item == null)
					jb.valueNull();
				else
					writeValue(item, jb, writeClass);
			}

			jb.arrayEnd();
		} else
		{
			jb.objectBegin();
			Wrapper w = Wrapper.getWrapper(c);
			String pns[] = w.getPropertyNames();
			String arr$[] = pns;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				String pn = arr$[i$];
				if ((obj instanceof Throwable) && ("localizedMessage".equals(pn) || "cause".equals(pn) || "stackTrace".equals(pn)))
					continue;
				jb.objectItem(pn);
				Object value = w.getPropertyValue(obj, pn);
				if (value == null || value == obj)
					jb.valueNull();
				else
					writeValue(value, jb, writeClass);
			}

			if (writeClass)
			{
				jb.objectItem("class");
				writeValue(obj.getClass().getName(), jb, writeClass);
			}
			jb.objectEnd();
		}
	}

	public Object readValue(Class c, Object jv)
		throws IOException
	{
		if (jv == null)
			return null;
		Decoder decoder = (Decoder)GlobalDecoderMap.get(c);
		if (decoder != null)
			return decoder.decode(jv);
		if (c.isEnum())
			return Enum.valueOf(c, String.valueOf(jv));
		else
			return jv;
	}

	static 
	{
		GlobalEncoderMap = new HashMap();
		GlobalDecoderMap = new HashMap();
		Encoder e = new Encoder() {

			public void encode(Object obj, JSONWriter jb)
				throws IOException
			{
				jb.valueBoolean(((Boolean)obj).booleanValue());
			}

		};
		GlobalEncoderMap.put(Boolean.TYPE, e);
		GlobalEncoderMap.put(java/lang/Boolean, e);
		e = new Encoder() {

			public void encode(Object obj, JSONWriter jb)
				throws IOException
			{
				jb.valueInt(((Number)obj).intValue());
			}

		};
		GlobalEncoderMap.put(Integer.TYPE, e);
		GlobalEncoderMap.put(java/lang/Integer, e);
		GlobalEncoderMap.put(Short.TYPE, e);
		GlobalEncoderMap.put(java/lang/Short, e);
		GlobalEncoderMap.put(Byte.TYPE, e);
		GlobalEncoderMap.put(java/lang/Byte, e);
		GlobalEncoderMap.put(java/util/concurrent/atomic/AtomicInteger, e);
		e = new Encoder() {

			public void encode(Object obj, JSONWriter jb)
				throws IOException
			{
				jb.valueString(Character.toString(((Character)obj).charValue()));
			}

		};
		GlobalEncoderMap.put(Character.TYPE, e);
		GlobalEncoderMap.put(java/lang/Character, e);
		e = new Encoder() {

			public void encode(Object obj, JSONWriter jb)
				throws IOException
			{
				jb.valueLong(((Number)obj).longValue());
			}

		};
		GlobalEncoderMap.put(Long.TYPE, e);
		GlobalEncoderMap.put(java/lang/Long, e);
		GlobalEncoderMap.put(java/util/concurrent/atomic/AtomicLong, e);
		GlobalEncoderMap.put(java/math/BigInteger, e);
		e = new Encoder() {

			public void encode(Object obj, JSONWriter jb)
				throws IOException
			{
				jb.valueFloat(((Number)obj).floatValue());
			}

		};
		GlobalEncoderMap.put(Float.TYPE, e);
		GlobalEncoderMap.put(java/lang/Float, e);
		e = new Encoder() {

			public void encode(Object obj, JSONWriter jb)
				throws IOException
			{
				jb.valueDouble(((Number)obj).doubleValue());
			}

		};
		GlobalEncoderMap.put(Double.TYPE, e);
		GlobalEncoderMap.put(java/lang/Double, e);
		GlobalEncoderMap.put(java/math/BigDecimal, e);
		e = new Encoder() {

			public void encode(Object obj, JSONWriter jb)
				throws IOException
			{
				jb.valueString(obj.toString());
			}

		};
		GlobalEncoderMap.put(java/lang/String, e);
		GlobalEncoderMap.put(java/lang/StringBuilder, e);
		GlobalEncoderMap.put(java/lang/StringBuffer, e);
		e = new Encoder() {

			public void encode(Object obj, JSONWriter jb)
				throws IOException
			{
				jb.valueString(Bytes.bytes2base64((byte[])(byte[])obj));
			}

		};
		GlobalEncoderMap.put([B, e);
		e = new Encoder() {

			public void encode(Object obj, JSONWriter jb)
				throws IOException
			{
				jb.valueString((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format((Date)obj));
			}

		};
		GlobalEncoderMap.put(java/util/Date, e);
		Decoder d = new Decoder() {

			public Object decode(Object jv)
			{
				return jv.toString();
			}

		};
		GlobalDecoderMap.put(java/lang/String, d);
		d = new Decoder() {

			public Object decode(Object jv)
			{
				if (jv instanceof Boolean)
					return Boolean.valueOf(((Boolean)jv).booleanValue());
				else
					return Boolean.valueOf(false);
			}

		};
		GlobalDecoderMap.put(Boolean.TYPE, d);
		d = new Decoder() {

			public Object decode(Object jv)
			{
				if (jv instanceof Boolean)
					return (Boolean)jv;
				else
					return (Boolean)null;
			}

		};
		GlobalDecoderMap.put(java/lang/Boolean, d);
		d = new Decoder() {

			public Object decode(Object jv)
			{
				if ((jv instanceof String) && ((String)jv).length() > 0)
					return Character.valueOf(((String)jv).charAt(0));
				else
					return Character.valueOf('\0');
			}

		};
		GlobalDecoderMap.put(Character.TYPE, d);
		d = new Decoder() {

			public Object decode(Object jv)
			{
				if ((jv instanceof String) && ((String)jv).length() > 0)
					return Character.valueOf(((String)jv).charAt(0));
				else
					return (Character)null;
			}

		};
		GlobalDecoderMap.put(java/lang/Character, d);
		d = new Decoder() {

			public Object decode(Object jv)
			{
				if (jv instanceof Number)
					return Integer.valueOf(((Number)jv).intValue());
				else
					return Integer.valueOf(0);
			}

		};
		GlobalDecoderMap.put(Integer.TYPE, d);
		d = new Decoder() {

			public Object decode(Object jv)
			{
				if (jv instanceof Number)
					return Integer.valueOf(((Number)jv).intValue());
				else
					return (Integer)null;
			}

		};
		GlobalDecoderMap.put(java/lang/Integer, d);
		d = new Decoder() {

			public Object decode(Object jv)
			{
				if (jv instanceof Number)
					return Short.valueOf(((Number)jv).shortValue());
				else
					return Short.valueOf((short)0);
			}

		};
		GlobalDecoderMap.put(Short.TYPE, d);
		d = new Decoder() {

			public Object decode(Object jv)
			{
				if (jv instanceof Number)
					return Short.valueOf(((Number)jv).shortValue());
				else
					return (Short)null;
			}

		};
		GlobalDecoderMap.put(java/lang/Short, d);
		d = new Decoder() {

			public Object decode(Object jv)
			{
				if (jv instanceof Number)
					return Long.valueOf(((Number)jv).longValue());
				else
					return Long.valueOf(0L);
			}

		};
		GlobalDecoderMap.put(Long.TYPE, d);
		d = new Decoder() {

			public Object decode(Object jv)
			{
				if (jv instanceof Number)
					return Long.valueOf(((Number)jv).longValue());
				else
					return (Long)null;
			}

		};
		GlobalDecoderMap.put(java/lang/Long, d);
		d = new Decoder() {

			public Object decode(Object jv)
			{
				if (jv instanceof Number)
					return Float.valueOf(((Number)jv).floatValue());
				else
					return Float.valueOf(0.0F);
			}

		};
		GlobalDecoderMap.put(Float.TYPE, d);
		d = new Decoder() {

			public Object decode(Object jv)
			{
				if (jv instanceof Number)
					return new Float(((Number)jv).floatValue());
				else
					return (Float)null;
			}

		};
		GlobalDecoderMap.put(java/lang/Float, d);
		d = new Decoder() {

			public Object decode(Object jv)
			{
				if (jv instanceof Number)
					return Double.valueOf(((Number)jv).doubleValue());
				else
					return Double.valueOf(0.0D);
			}

		};
		GlobalDecoderMap.put(Double.TYPE, d);
		d = new Decoder() {

			public Object decode(Object jv)
			{
				if (jv instanceof Number)
					return new Double(((Number)jv).doubleValue());
				else
					return (Double)null;
			}

		};
		GlobalDecoderMap.put(java/lang/Double, d);
		d = new Decoder() {

			public Object decode(Object jv)
			{
				if (jv instanceof Number)
					return Byte.valueOf(((Number)jv).byteValue());
				else
					return Byte.valueOf((byte)0);
			}

		};
		GlobalDecoderMap.put(Byte.TYPE, d);
		d = new Decoder() {

			public Object decode(Object jv)
			{
				if (jv instanceof Number)
					return Byte.valueOf(((Number)jv).byteValue());
				else
					return (Byte)null;
			}

		};
		GlobalDecoderMap.put(java/lang/Byte, d);
		d = new Decoder() {

			public Object decode(Object jv)
				throws IOException
			{
				if (jv instanceof String)
					return Bytes.base642bytes((String)jv);
				else
					return (byte[])null;
			}

		};
		GlobalDecoderMap.put([B, d);
		d = new Decoder() {

			public Object decode(Object jv)
				throws IOException
			{
				return new StringBuilder(jv.toString());
			}

		};
		GlobalDecoderMap.put(java/lang/StringBuilder, d);
		d = new Decoder() {

			public Object decode(Object jv)
				throws IOException
			{
				return new StringBuffer(jv.toString());
			}

		};
		GlobalDecoderMap.put(java/lang/StringBuffer, d);
		d = new Decoder() {

			public Object decode(Object jv)
				throws IOException
			{
				if (jv instanceof Number)
					return BigInteger.valueOf(((Number)jv).longValue());
				else
					return (BigInteger)null;
			}

		};
		GlobalDecoderMap.put(java/math/BigInteger, d);
		d = new Decoder() {

			public Object decode(Object jv)
				throws IOException
			{
				if (jv instanceof Number)
					return BigDecimal.valueOf(((Number)jv).doubleValue());
				else
					return (BigDecimal)null;
			}

		};
		GlobalDecoderMap.put(java/math/BigDecimal, d);
		d = new Decoder() {

			public Object decode(Object jv)
				throws IOException
			{
				if (jv instanceof Number)
					return new AtomicInteger(((Number)jv).intValue());
				else
					return (AtomicInteger)null;
			}

		};
		GlobalDecoderMap.put(java/util/concurrent/atomic/AtomicInteger, d);
		d = new Decoder() {

			public Object decode(Object jv)
				throws IOException
			{
				if (jv instanceof Number)
					return new AtomicLong(((Number)jv).longValue());
				else
					return (AtomicLong)null;
			}

		};
		GlobalDecoderMap.put(java/util/concurrent/atomic/AtomicLong, d);
		d = new Decoder() {

			public Object decode(Object jv)
				throws IOException
			{
				if (!(jv instanceof String))
					break MISSING_BLOCK_LABEL_38;
				return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse((String)jv);
				ParseException e;
				e;
				throw new IllegalArgumentException(e.getMessage(), e);
				if (jv instanceof Number)
					return new Date(((Number)jv).longValue());
				else
					return (Date)null;
			}

		};
		GlobalDecoderMap.put(java/util/Date, d);
	}
}
