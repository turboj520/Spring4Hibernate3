// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   BurlapInput.java

package com.autohome.burlap.io;

import com.autohome.hessian.io.*;
import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import org.w3c.dom.Node;

// Referenced classes of package com.autohome.burlap.io:
//			AbstractBurlapInput, BurlapServiceException, BurlapRemote, BurlapProtocolException

public class BurlapInput extends AbstractBurlapInput
{

	private static int base64Decode[];
	public static final int TAG_EOF = -1;
	public static final int TAG_NULL = 0;
	public static final int TAG_BOOLEAN = 1;
	public static final int TAG_INT = 2;
	public static final int TAG_LONG = 3;
	public static final int TAG_DOUBLE = 4;
	public static final int TAG_DATE = 5;
	public static final int TAG_STRING = 6;
	public static final int TAG_XML = 7;
	public static final int TAG_BASE64 = 8;
	public static final int TAG_MAP = 9;
	public static final int TAG_LIST = 10;
	public static final int TAG_TYPE = 11;
	public static final int TAG_LENGTH = 12;
	public static final int TAG_REF = 13;
	public static final int TAG_REMOTE = 14;
	public static final int TAG_CALL = 15;
	public static final int TAG_REPLY = 16;
	public static final int TAG_FAULT = 17;
	public static final int TAG_METHOD = 18;
	public static final int TAG_HEADER = 19;
	public static final int TAG_NULL_END = 100;
	public static final int TAG_BOOLEAN_END = 101;
	public static final int TAG_INT_END = 102;
	public static final int TAG_LONG_END = 103;
	public static final int TAG_DOUBLE_END = 104;
	public static final int TAG_DATE_END = 105;
	public static final int TAG_STRING_END = 106;
	public static final int TAG_XML_END = 107;
	public static final int TAG_BASE64_END = 108;
	public static final int TAG_MAP_END = 109;
	public static final int TAG_LIST_END = 110;
	public static final int TAG_TYPE_END = 111;
	public static final int TAG_LENGTH_END = 112;
	public static final int TAG_REF_END = 113;
	public static final int TAG_REMOTE_END = 114;
	public static final int TAG_CALL_END = 115;
	public static final int TAG_REPLY_END = 116;
	public static final int TAG_FAULT_END = 117;
	public static final int TAG_METHOD_END = 118;
	public static final int TAG_HEADER_END = 119;
	private static HashMap _tagMap;
	private static Field _detailMessageField;
	protected SerializerFactory _serializerFactory;
	protected ArrayList _refs;
	private InputStream _is;
	protected int _peek;
	private String _method;
	private int _peekTag;
	private Throwable _replyFault;
	protected StringBuffer _sbuf;
	protected StringBuffer _entityBuffer;
	protected Calendar _utcCalendar;
	protected Calendar _localCalendar;

	public BurlapInput()
	{
		_peek = -1;
		_sbuf = new StringBuffer();
		_entityBuffer = new StringBuffer();
	}

	public BurlapInput(InputStream is)
	{
		_peek = -1;
		_sbuf = new StringBuffer();
		_entityBuffer = new StringBuffer();
		init(is);
	}

	public void setSerializerFactory(SerializerFactory factory)
	{
		_serializerFactory = factory;
	}

	public SerializerFactory getSerializerFactory()
	{
		return _serializerFactory;
	}

	public void init(InputStream is)
	{
		_is = is;
		_method = null;
		_peek = -1;
		_peekTag = -1;
		_refs = null;
		_replyFault = null;
		if (_serializerFactory == null)
			_serializerFactory = new SerializerFactory();
	}

	public String getMethod()
	{
		return _method;
	}

	public Throwable getReplyFault()
	{
		return _replyFault;
	}

	public void startCall()
		throws IOException
	{
		readCall();
		for (; readHeader() != null; readObject());
		readMethod();
	}

	public int readCall()
		throws IOException
	{
		expectTag(15);
		int major = 1;
		int minor = 0;
		return (major << 16) + minor;
	}

	public String readMethod()
		throws IOException
	{
		expectTag(18);
		_method = parseString();
		expectTag(118);
		return _method;
	}

	public void completeCall()
		throws IOException
	{
		expectTag(115);
	}

	public Object readReply(Class expectedClass)
		throws Throwable
	{
		expectTag(16);
		int tag = parseTag();
		if (tag == 17)
		{
			throw prepareFault();
		} else
		{
			_peekTag = tag;
			Object value = readObject(expectedClass);
			expectTag(116);
			return value;
		}
	}

	public void startReply()
		throws Throwable
	{
		expectTag(16);
		int tag = parseTag();
		if (tag == 17)
		{
			throw prepareFault();
		} else
		{
			_peekTag = tag;
			return;
		}
	}

	private Throwable prepareFault()
		throws IOException
	{
		HashMap fault = readFault();
		Object detail = fault.get("detail");
		String message = (String)fault.get("message");
		if (detail instanceof Throwable)
		{
			_replyFault = (Throwable)detail;
			if (message != null && _detailMessageField != null)
				try
				{
					_detailMessageField.set(_replyFault, message);
				}
				catch (Throwable e) { }
			return _replyFault;
		} else
		{
			String code = (String)fault.get("code");
			_replyFault = new BurlapServiceException(message, code, detail);
			return _replyFault;
		}
	}

	public void completeReply()
		throws IOException
	{
		expectTag(116);
	}

	public String readHeader()
		throws IOException
	{
		int tag = parseTag();
		if (tag == 19)
		{
			_sbuf.setLength(0);
			String value = parseString(_sbuf).toString();
			expectTag(119);
			return value;
		} else
		{
			_peekTag = tag;
			return null;
		}
	}

	public void readNull()
		throws IOException
	{
		int tag = parseTag();
		switch (tag)
		{
		case 0: // '\0'
			expectTag(100);
			return;
		}
		throw expectedTag("null", tag);
	}

	public boolean readBoolean()
		throws IOException
	{
		int tag = parseTag();
		switch (tag)
		{
		case 0: // '\0'
		{
			boolean value = false;
			expectTag(100);
			return value;
		}

		case 1: // '\001'
		{
			boolean value = parseInt() != 0;
			expectTag(101);
			return value;
		}

		case 2: // '\002'
		{
			boolean value = parseInt() != 0;
			expectTag(102);
			return value;
		}

		case 3: // '\003'
		{
			boolean value = parseLong() != 0L;
			expectTag(103);
			return value;
		}

		case 4: // '\004'
		{
			boolean value = parseDouble() != 0.0D;
			expectTag(104);
			return value;
		}
		}
		throw expectedTag("boolean", tag);
	}

	public byte readByte()
		throws IOException
	{
		return (byte)readInt();
	}

	public short readShort()
		throws IOException
	{
		return (short)readInt();
	}

	public int readInt()
		throws IOException
	{
		int tag = parseTag();
		switch (tag)
		{
		case 0: // '\0'
		{
			int value = 0;
			expectTag(100);
			return value;
		}

		case 1: // '\001'
		{
			int value = parseInt();
			expectTag(101);
			return value;
		}

		case 2: // '\002'
		{
			int value = parseInt();
			expectTag(102);
			return value;
		}

		case 3: // '\003'
		{
			int value = (int)parseLong();
			expectTag(103);
			return value;
		}

		case 4: // '\004'
		{
			int value = (int)parseDouble();
			expectTag(104);
			return value;
		}
		}
		throw expectedTag("int", tag);
	}

	public long readLong()
		throws IOException
	{
		int tag = parseTag();
		switch (tag)
		{
		case 0: // '\0'
		{
			long value = 0L;
			expectTag(100);
			return value;
		}

		case 1: // '\001'
		{
			long value = parseInt();
			expectTag(101);
			return value;
		}

		case 2: // '\002'
		{
			long value = parseInt();
			expectTag(102);
			return value;
		}

		case 3: // '\003'
		{
			long value = parseLong();
			expectTag(103);
			return value;
		}

		case 4: // '\004'
		{
			long value = (long)parseDouble();
			expectTag(104);
			return value;
		}
		}
		throw expectedTag("long", tag);
	}

	public float readFloat()
		throws IOException
	{
		return (float)readDouble();
	}

	public double readDouble()
		throws IOException
	{
		int tag = parseTag();
		switch (tag)
		{
		case 0: // '\0'
		{
			double value = 0.0D;
			expectTag(100);
			return value;
		}

		case 1: // '\001'
		{
			double value = parseInt();
			expectTag(101);
			return value;
		}

		case 2: // '\002'
		{
			double value = parseInt();
			expectTag(102);
			return value;
		}

		case 3: // '\003'
		{
			double value = parseLong();
			expectTag(103);
			return value;
		}

		case 4: // '\004'
		{
			double value = parseDouble();
			expectTag(104);
			return value;
		}
		}
		throw expectedTag("double", tag);
	}

	public long readUTCDate()
		throws IOException
	{
		int tag = parseTag();
		if (tag != 5)
			throw error("expected date");
		if (_utcCalendar == null)
			_utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		long value = parseDate(_utcCalendar);
		expectTag(105);
		return value;
	}

	public long readLocalDate()
		throws IOException
	{
		int tag = parseTag();
		if (tag != 5)
			throw error("expected date");
		if (_localCalendar == null)
			_localCalendar = Calendar.getInstance();
		long value = parseDate(_localCalendar);
		expectTag(105);
		return value;
	}

	public String readString()
		throws IOException
	{
		int tag = parseTag();
		switch (tag)
		{
		case 0: // '\0'
		{
			expectTag(100);
			return null;
		}

		case 6: // '\006'
		{
			_sbuf.setLength(0);
			String value = parseString(_sbuf).toString();
			expectTag(106);
			return value;
		}

		case 7: // '\007'
		{
			_sbuf.setLength(0);
			String value = parseString(_sbuf).toString();
			expectTag(107);
			return value;
		}
		}
		throw expectedTag("string", tag);
	}

	public Node readNode()
		throws IOException
	{
		int tag = read();
		switch (tag)
		{
		case 78: // 'N'
			return null;

		case 83: // 'S'
		case 88: // 'X'
		case 115: // 's'
		case 120: // 'x'
			throw error("can't cope");
		}
		throw expectedTag("string", tag);
	}

	public byte[] readBytes()
		throws IOException
	{
		int tag = parseTag();
		switch (tag)
		{
		case 0: // '\0'
			expectTag(100);
			return null;

		case 8: // '\b'
			byte data[] = parseBytes();
			expectTag(108);
			return data;
		}
		throw expectedTag("bytes", tag);
	}

	public int readLength()
		throws IOException
	{
		int tag = parseTag();
		if (tag != 12)
		{
			_peekTag = tag;
			return -1;
		} else
		{
			int value = parseInt();
			expectTag(112);
			return value;
		}
	}

	private HashMap readFault()
		throws IOException
	{
		HashMap map = new HashMap();
		int code;
		for (code = parseTag(); code >= 0 && code != 117; code = parseTag())
		{
			_peekTag = code;
			Object key = readObject();
			Object value = readObject();
			if (key != null && value != null)
				map.put(key, value);
		}

		if (code != 117)
			throw expectedTag("fault", code);
		else
			return map;
	}

	public Object readObject(Class cl)
		throws IOException
	{
		if (cl == null || cl.equals(java/lang/Object))
			return readObject();
		int tag = parseTag();
		switch (tag)
		{
		case 0: // '\0'
		{
			expectTag(100);
			return null;
		}

		case 9: // '\t'
		{
			String type = readType();
			Deserializer reader = _serializerFactory.getObjectDeserializer(type, cl);
			return reader.readMap(this);
		}

		case 10: // '\n'
		{
			String type = readType();
			int length = readLength();
			Deserializer reader = _serializerFactory.getObjectDeserializer(type, cl);
			return reader.readList(this, length);
		}

		case 13: // '\r'
		{
			int ref = parseInt();
			expectTag(113);
			return _refs.get(ref);
		}

		case 14: // '\016'
		{
			String type = readType();
			String url = readString();
			expectTag(114);
			Object remote = resolveRemote(type, url);
			return remote;
		}

		case 1: // '\001'
		case 2: // '\002'
		case 3: // '\003'
		case 4: // '\004'
		case 5: // '\005'
		case 6: // '\006'
		case 7: // '\007'
		case 8: // '\b'
		case 11: // '\013'
		case 12: // '\f'
		default:
		{
			_peekTag = tag;
			Object value = _serializerFactory.getDeserializer(cl).readObject(this);
			return value;
		}
		}
	}

	public Object readObject()
		throws IOException
	{
		int tag = parseTag();
		switch (tag)
		{
		case 0: // '\0'
		{
			expectTag(100);
			return null;
		}

		case 1: // '\001'
		{
			int value = parseInt();
			expectTag(101);
			return new Boolean(value != 0);
		}

		case 2: // '\002'
		{
			int value = parseInt();
			expectTag(102);
			return new Integer(value);
		}

		case 3: // '\003'
		{
			long value = parseLong();
			expectTag(103);
			return new Long(value);
		}

		case 4: // '\004'
		{
			double value = parseDouble();
			expectTag(104);
			return new Double(value);
		}

		case 5: // '\005'
		{
			long value = parseDate();
			expectTag(105);
			return new Date(value);
		}

		case 7: // '\007'
		{
			return parseXML();
		}

		case 6: // '\006'
		{
			_sbuf.setLength(0);
			String value = parseString(_sbuf).toString();
			expectTag(106);
			return value;
		}

		case 8: // '\b'
		{
			byte data[] = parseBytes();
			expectTag(108);
			return data;
		}

		case 10: // '\n'
		{
			String type = readType();
			int length = readLength();
			return _serializerFactory.readList(this, length, type);
		}

		case 9: // '\t'
		{
			String type = readType();
			Deserializer deserializer = _serializerFactory.getObjectDeserializer(type);
			return deserializer.readMap(this);
		}

		case 13: // '\r'
		{
			int ref = parseInt();
			expectTag(113);
			return _refs.get(ref);
		}

		case 14: // '\016'
		{
			String type = readType();
			String url = readString();
			expectTag(114);
			return resolveRemote(type, url);
		}

		case 11: // '\013'
		case 12: // '\f'
		default:
		{
			throw error((new StringBuilder()).append("unknown code:").append(tagName(tag)).toString());
		}
		}
	}

	public Object readRemote()
		throws IOException
	{
		String type = readType();
		String url = readString();
		return resolveRemote(type, url);
	}

	public Object readRef()
		throws IOException
	{
		return _refs.get(parseInt());
	}

	public int readListStart()
		throws IOException
	{
		return parseTag();
	}

	public int readMapStart()
		throws IOException
	{
		return parseTag();
	}

	public boolean isEnd()
		throws IOException
	{
		int code = parseTag();
		_peekTag = code;
		return code < 0 || code >= 100;
	}

	public void readEnd()
		throws IOException
	{
		int code = parseTag();
		if (code < 100)
			throw error((new StringBuilder()).append("unknown code:").append((char)code).toString());
		else
			return;
	}

	public void readMapEnd()
		throws IOException
	{
		expectTag(109);
	}

	public void readListEnd()
		throws IOException
	{
		expectTag(110);
	}

	public int addRef(Object ref)
	{
		if (_refs == null)
			_refs = new ArrayList();
		_refs.add(ref);
		return _refs.size() - 1;
	}

	public void setRef(int i, Object ref)
	{
		_refs.set(i, ref);
	}

	public Object resolveRemote(String type, String url)
		throws IOException
	{
		HessianRemoteResolver resolver = getRemoteResolver();
		if (resolver != null)
			return resolver.lookup(type, url);
		else
			return new BurlapRemote(type, url);
	}

	public String readType()
		throws IOException
	{
		int code = parseTag();
		if (code != 11)
		{
			_peekTag = code;
			return "";
		}
		_sbuf.setLength(0);
		int ch;
		while ((ch = readChar()) >= 0) 
			_sbuf.append((char)ch);
		String type = _sbuf.toString();
		expectTag(111);
		return type;
	}

	private int parseInt()
		throws IOException
	{
		int sign = 1;
		int ch = read();
		if (ch == 45)
		{
			sign = -1;
			ch = read();
		}
		int value = 0;
		for (; ch >= 48 && ch <= 57; ch = read())
			value = (10 * value + ch) - 48;

		_peek = ch;
		return sign * value;
	}

	private long parseLong()
		throws IOException
	{
		int sign = 1;
		int ch = read();
		if (ch == 45)
		{
			sign = -1;
			ch = read();
		}
		long value = 0L;
		for (; ch >= 48 && ch <= 57; ch = read())
			value = (10L * value + (long)ch) - 48L;

		_peek = ch;
		return (long)sign * value;
	}

	private double parseDouble()
		throws IOException
	{
		int ch = skipWhitespace();
		_sbuf.setLength(0);
		for (; !isWhitespace(ch) && ch != 60; ch = read())
			_sbuf.append((char)ch);

		_peek = ch;
		return (new Double(_sbuf.toString())).doubleValue();
	}

	protected long parseDate()
		throws IOException
	{
		if (_utcCalendar == null)
			_utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		return parseDate(_utcCalendar);
	}

	protected long parseDate(Calendar calendar)
		throws IOException
	{
		int ch = skipWhitespace();
		int year = 0;
		for (int i = 0; i < 4; i++)
		{
			if (ch >= 48 && ch <= 57)
				year = (10 * year + ch) - 48;
			else
				throw expectedChar("year", ch);
			ch = read();
		}

		int month = 0;
		for (int i = 0; i < 2; i++)
		{
			if (ch >= 48 && ch <= 57)
				month = (10 * month + ch) - 48;
			else
				throw expectedChar("month", ch);
			ch = read();
		}

		int day = 0;
		for (int i = 0; i < 2; i++)
		{
			if (ch >= 48 && ch <= 57)
				day = (10 * day + ch) - 48;
			else
				throw expectedChar("day", ch);
			ch = read();
		}

		if (ch != 84)
			throw expectedChar("`T'", ch);
		ch = read();
		int hour = 0;
		for (int i = 0; i < 2; i++)
		{
			if (ch >= 48 && ch <= 57)
				hour = (10 * hour + ch) - 48;
			else
				throw expectedChar("hour", ch);
			ch = read();
		}

		int minute = 0;
		for (int i = 0; i < 2; i++)
		{
			if (ch >= 48 && ch <= 57)
				minute = (10 * minute + ch) - 48;
			else
				throw expectedChar("minute", ch);
			ch = read();
		}

		int second = 0;
		for (int i = 0; i < 2; i++)
		{
			if (ch >= 48 && ch <= 57)
				second = (10 * second + ch) - 48;
			else
				throw expectedChar("second", ch);
			ch = read();
		}

		int ms = 0;
		if (ch == 46)
			for (ch = read(); ch >= 48 && ch <= 57; ch = read())
				ms = (10 * ms + ch) - 48;

		for (; ch > 0 && ch != 60; ch = read());
		_peek = ch;
		calendar.set(1, year);
		calendar.set(2, month - 1);
		calendar.set(5, day);
		calendar.set(11, hour);
		calendar.set(12, minute);
		calendar.set(13, second);
		calendar.set(14, ms);
		return calendar.getTime().getTime();
	}

	protected String parseString()
		throws IOException
	{
		_sbuf.setLength(0);
		return parseString(_sbuf).toString();
	}

	protected StringBuffer parseString(StringBuffer sbuf)
		throws IOException
	{
		int ch;
		while ((ch = readChar()) >= 0) 
			sbuf.append((char)ch);
		return sbuf;
	}

	Node parseXML()
		throws IOException
	{
		throw error("help!");
	}

	int readChar()
		throws IOException
	{
		int ch = read();
		if (ch == 60 || ch < 0)
		{
			_peek = ch;
			return -1;
		}
		if (ch == 38)
		{
			ch = read();
			if (ch == 35)
			{
				ch = read();
				if (ch >= 48 && ch <= 57)
				{
					int v = 0;
					for (; ch >= 48 && ch <= 57; ch = read())
						v = (10 * v + ch) - 48;

					if (ch != 59)
						throw error((new StringBuilder()).append("expected ';' at ").append((char)ch).toString());
					else
						return (char)v;
				} else
				{
					throw error((new StringBuilder()).append("expected digit at ").append((char)ch).toString());
				}
			}
			_entityBuffer.setLength(0);
			for (; ch >= 97 && ch <= 122; ch = read())
				_entityBuffer.append((char)ch);

			String entity = _entityBuffer.toString();
			if (ch != 59)
				throw expectedChar("';'", ch);
			if (entity.equals("amp"))
				return 38;
			if (entity.equals("apos"))
				return 39;
			if (entity.equals("quot"))
				return 34;
			if (entity.equals("lt"))
				return 60;
			if (entity.equals("gt"))
				return 62;
			else
				throw new BurlapProtocolException((new StringBuilder()).append("unknown XML entity &").append(entity).append("; at `").append((char)ch).append("'").toString());
		}
		if (ch < 128)
			return (char)ch;
		if ((ch & 0xe0) == 192)
		{
			int ch1 = read();
			int v = ((ch & 0x1f) << 6) + (ch1 & 0x3f);
			return (char)v;
		}
		if ((ch & 0xf0) == 224)
		{
			int ch1 = read();
			int ch2 = read();
			int v = ((ch & 0xf) << 12) + ((ch1 & 0x3f) << 6) + (ch2 & 0x3f);
			return (char)v;
		} else
		{
			throw new BurlapProtocolException("bad utf-8 encoding");
		}
	}

	protected byte[] parseBytes()
		throws IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		parseBytes(bos);
		return bos.toByteArray();
	}

	protected ByteArrayOutputStream parseBytes(ByteArrayOutputStream bos)
		throws IOException
	{
		int ch;
		for (ch = skipWhitespace(); ch >= 0 && ch != 60; ch = skipWhitespace())
		{
			int b1 = ch;
			int b2 = read();
			int b3 = read();
			int b4 = read();
			if (b4 != 61)
			{
				int chunk = (base64Decode[b1] << 18) + (base64Decode[b2] << 12) + (base64Decode[b3] << 6) + base64Decode[b4];
				bos.write(chunk >> 16);
				bos.write(chunk >> 8);
				bos.write(chunk);
				continue;
			}
			if (b3 != 61)
			{
				int chunk = (base64Decode[b1] << 10) + (base64Decode[b2] << 4) + (base64Decode[b3] >> 2);
				bos.write(chunk >> 8);
				bos.write(chunk);
			} else
			{
				int chunk = (base64Decode[b1] << 2) + (base64Decode[b2] >> 4);
				bos.write(chunk);
			}
		}

		if (ch == 60)
			_peek = ch;
		return bos;
	}

	public void expectTag(int expectTag)
		throws IOException
	{
		int tag = parseTag();
		if (tag != expectTag)
			throw error((new StringBuilder()).append("expected ").append(tagName(expectTag)).append(" at ").append(tagName(tag)).toString());
		else
			return;
	}

	protected int parseTag()
		throws IOException
	{
		if (_peekTag >= 0)
		{
			int tag = _peekTag;
			_peekTag = -1;
			return tag;
		}
		int ch = skipWhitespace();
		int endTagDelta = 0;
		if (ch != 60)
			throw expectedChar("'<'", ch);
		ch = read();
		if (ch == 47)
		{
			endTagDelta = 100;
			ch = _is.read();
		}
		if (!isTagChar(ch))
			throw expectedChar("tag", ch);
		_sbuf.setLength(0);
		for (; isTagChar(ch); ch = read())
			_sbuf.append((char)ch);

		if (ch != 62)
			throw expectedChar("'>'", ch);
		Integer value = (Integer)_tagMap.get(_sbuf.toString());
		if (value == null)
			throw error((new StringBuilder()).append("Unknown tag <").append(_sbuf).append(">").toString());
		else
			return value.intValue() + endTagDelta;
	}

	private boolean isTagChar(int ch)
	{
		return ch >= 97 && ch <= 122 || ch >= 65 && ch <= 90 || ch >= 48 && ch <= 57 || ch == 58 || ch == 45;
	}

	protected int skipWhitespace()
		throws IOException
	{
		int ch;
		for (ch = read(); ch == 32 || ch == 9 || ch == 10 || ch == 13; ch = read());
		return ch;
	}

	protected boolean isWhitespace(int ch)
		throws IOException
	{
		return ch == 32 || ch == 9 || ch == 10 || ch == 13;
	}

	int read(byte buffer[], int offset, int length)
		throws IOException
	{
		throw new UnsupportedOperationException();
	}

	int read()
		throws IOException
	{
		if (_peek >= 0)
		{
			int value = _peek;
			_peek = -1;
			return value;
		} else
		{
			int ch = _is.read();
			return ch;
		}
	}

	public Reader getReader()
	{
		return null;
	}

	public InputStream readInputStream()
	{
		return null;
	}

	public InputStream getInputStream()
	{
		return null;
	}

	protected IOException expectBeginTag(String expect, String tag)
	{
		return new BurlapProtocolException((new StringBuilder()).append("expected <").append(expect).append("> at <").append(tag).append(">").toString());
	}

	protected IOException expectedChar(String expect, int ch)
	{
		if (ch < 0)
			return error((new StringBuilder()).append("expected ").append(expect).append(" at end of file").toString());
		else
			return error((new StringBuilder()).append("expected ").append(expect).append(" at ").append((char)ch).toString());
	}

	protected IOException expectedTag(String expect, int tag)
	{
		return error((new StringBuilder()).append("expected ").append(expect).append(" at ").append(tagName(tag)).toString());
	}

	protected IOException error(String message)
	{
		return new BurlapProtocolException(message);
	}

	protected static String tagName(int tag)
	{
		switch (tag)
		{
		case 0: // '\0'
			return "<null>";

		case 100: // 'd'
			return "</null>";

		case 1: // '\001'
			return "<boolean>";

		case 101: // 'e'
			return "</boolean>";

		case 2: // '\002'
			return "<int>";

		case 102: // 'f'
			return "</int>";

		case 3: // '\003'
			return "<long>";

		case 103: // 'g'
			return "</long>";

		case 4: // '\004'
			return "<double>";

		case 104: // 'h'
			return "</double>";

		case 6: // '\006'
			return "<string>";

		case 106: // 'j'
			return "</string>";

		case 7: // '\007'
			return "<xml>";

		case 107: // 'k'
			return "</xml>";

		case 8: // '\b'
			return "<base64>";

		case 108: // 'l'
			return "</base64>";

		case 9: // '\t'
			return "<map>";

		case 109: // 'm'
			return "</map>";

		case 10: // '\n'
			return "<list>";

		case 110: // 'n'
			return "</list>";

		case 11: // '\013'
			return "<type>";

		case 111: // 'o'
			return "</type>";

		case 12: // '\f'
			return "<length>";

		case 112: // 'p'
			return "</length>";

		case 13: // '\r'
			return "<ref>";

		case 113: // 'q'
			return "</ref>";

		case 14: // '\016'
			return "<remote>";

		case 114: // 'r'
			return "</remote>";

		case 15: // '\017'
			return "<burlap:call>";

		case 115: // 's'
			return "</burlap:call>";

		case 16: // '\020'
			return "<burlap:reply>";

		case 116: // 't'
			return "</burlap:reply>";

		case 19: // '\023'
			return "<header>";

		case 119: // 'w'
			return "</header>";

		case 17: // '\021'
			return "<fault>";

		case 117: // 'u'
			return "</fault>";

		case -1: 
			return "end of file";

		case 5: // '\005'
		case 18: // '\022'
		case 20: // '\024'
		case 21: // '\025'
		case 22: // '\026'
		case 23: // '\027'
		case 24: // '\030'
		case 25: // '\031'
		case 26: // '\032'
		case 27: // '\033'
		case 28: // '\034'
		case 29: // '\035'
		case 30: // '\036'
		case 31: // '\037'
		case 32: // ' '
		case 33: // '!'
		case 34: // '"'
		case 35: // '#'
		case 36: // '$'
		case 37: // '%'
		case 38: // '&'
		case 39: // '\''
		case 40: // '('
		case 41: // ')'
		case 42: // '*'
		case 43: // '+'
		case 44: // ','
		case 45: // '-'
		case 46: // '.'
		case 47: // '/'
		case 48: // '0'
		case 49: // '1'
		case 50: // '2'
		case 51: // '3'
		case 52: // '4'
		case 53: // '5'
		case 54: // '6'
		case 55: // '7'
		case 56: // '8'
		case 57: // '9'
		case 58: // ':'
		case 59: // ';'
		case 60: // '<'
		case 61: // '='
		case 62: // '>'
		case 63: // '?'
		case 64: // '@'
		case 65: // 'A'
		case 66: // 'B'
		case 67: // 'C'
		case 68: // 'D'
		case 69: // 'E'
		case 70: // 'F'
		case 71: // 'G'
		case 72: // 'H'
		case 73: // 'I'
		case 74: // 'J'
		case 75: // 'K'
		case 76: // 'L'
		case 77: // 'M'
		case 78: // 'N'
		case 79: // 'O'
		case 80: // 'P'
		case 81: // 'Q'
		case 82: // 'R'
		case 83: // 'S'
		case 84: // 'T'
		case 85: // 'U'
		case 86: // 'V'
		case 87: // 'W'
		case 88: // 'X'
		case 89: // 'Y'
		case 90: // 'Z'
		case 91: // '['
		case 92: // '\\'
		case 93: // ']'
		case 94: // '^'
		case 95: // '_'
		case 96: // '`'
		case 97: // 'a'
		case 98: // 'b'
		case 99: // 'c'
		case 105: // 'i'
		case 118: // 'v'
		default:
			return (new StringBuilder()).append("unknown ").append(tag).toString();
		}
	}

	static 
	{
		_tagMap = new HashMap();
		_tagMap.put("null", new Integer(0));
		_tagMap.put("boolean", new Integer(1));
		_tagMap.put("int", new Integer(2));
		_tagMap.put("long", new Integer(3));
		_tagMap.put("double", new Integer(4));
		_tagMap.put("date", new Integer(5));
		_tagMap.put("string", new Integer(6));
		_tagMap.put("xml", new Integer(7));
		_tagMap.put("base64", new Integer(8));
		_tagMap.put("map", new Integer(9));
		_tagMap.put("list", new Integer(10));
		_tagMap.put("type", new Integer(11));
		_tagMap.put("length", new Integer(12));
		_tagMap.put("ref", new Integer(13));
		_tagMap.put("remote", new Integer(14));
		_tagMap.put("burlap:call", new Integer(15));
		_tagMap.put("burlap:reply", new Integer(16));
		_tagMap.put("fault", new Integer(17));
		_tagMap.put("method", new Integer(18));
		_tagMap.put("header", new Integer(19));
		base64Decode = new int[256];
		for (int i = 65; i <= 90; i++)
			base64Decode[i] = i - 65;

		for (int i = 97; i <= 122; i++)
			base64Decode[i] = (i - 97) + 26;

		for (int i = 48; i <= 57; i++)
			base64Decode[i] = (i - 48) + 52;

		base64Decode[43] = 62;
		base64Decode[47] = 63;
		try
		{
			_detailMessageField = java/lang/Throwable.getDeclaredField("detailMessage");
			_detailMessageField.setAccessible(true);
		}
		catch (Throwable e) { }
	}
}
