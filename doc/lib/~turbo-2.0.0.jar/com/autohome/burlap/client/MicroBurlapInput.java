// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   MicroBurlapInput.java

package com.autohome.burlap.client;

import java.io.*;
import java.util.*;

// Referenced classes of package com.autohome.burlap.client:
//			BurlapServiceException, BurlapProtocolException, BurlapRemote

public class MicroBurlapInput
{

	private static int base64Decode[];
	private InputStream is;
	protected int peek;
	protected boolean peekTag;
	protected Date date;
	protected Calendar utcCalendar;
	private Calendar localCalendar;
	protected Vector refs;
	protected String method;
	protected StringBuffer sbuf;
	protected StringBuffer entity;

	public MicroBurlapInput(InputStream is)
	{
		sbuf = new StringBuffer();
		entity = new StringBuffer();
		init(is);
	}

	public MicroBurlapInput()
	{
		sbuf = new StringBuffer();
		entity = new StringBuffer();
	}

	public String getMethod()
	{
		return method;
	}

	public void init(InputStream is)
	{
		this.is = is;
		refs = null;
	}

	public void startCall()
		throws IOException
	{
		expectStartTag("burlap:call");
		expectStartTag("method");
		method = parseString();
		expectEndTag("method");
		refs = null;
	}

	public void completeCall()
		throws IOException
	{
		expectEndTag("burlap:call");
	}

	public Object readReply(Class expectedClass)
		throws Exception
	{
		if (startReply())
		{
			Object value = readObject(expectedClass);
			completeReply();
			return value;
		}
		Hashtable fault = readFault();
		Object detail = fault.get("detail");
		if (detail instanceof Exception)
		{
			throw (Exception)detail;
		} else
		{
			String code = (String)fault.get("code");
			String message = (String)fault.get("message");
			throw new BurlapServiceException(message, code, detail);
		}
	}

	public boolean startReply()
		throws IOException
	{
		refs = null;
		expectStartTag("burlap:reply");
		if (!parseTag())
			throw new BurlapProtocolException("expected <value>");
		String tag = sbuf.toString();
		if (tag.equals("fault"))
		{
			peekTag = true;
			return false;
		} else
		{
			peekTag = true;
			return true;
		}
	}

	public void completeReply()
		throws IOException
	{
		expectEndTag("burlap:reply");
	}

	public boolean readBoolean()
		throws IOException
	{
		expectStartTag("boolean");
		int value = parseInt();
		expectEndTag("boolean");
		return value != 0;
	}

	public int readInt()
		throws IOException
	{
		expectStartTag("int");
		int value = parseInt();
		expectEndTag("int");
		return value;
	}

	public long readLong()
		throws IOException
	{
		expectStartTag("long");
		long value = parseLong();
		expectEndTag("long");
		return value;
	}

	public long readUTCDate()
		throws IOException
	{
		expectStartTag("date");
		if (utcCalendar == null)
			utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		long value = parseDate(utcCalendar);
		expectEndTag("date");
		return value;
	}

	public long readLocalDate()
		throws IOException
	{
		expectStartTag("date");
		if (localCalendar == null)
			localCalendar = Calendar.getInstance();
		long value = parseDate(localCalendar);
		expectEndTag("date");
		return value;
	}

	public BurlapRemote readRemote()
		throws IOException
	{
		expectStartTag("remote");
		String type = readType();
		String url = readString();
		expectEndTag("remote");
		return new BurlapRemote(type, url);
	}

	public String readString()
		throws IOException
	{
		if (!parseTag())
			throw new BurlapProtocolException("expected <string>");
		String tag = sbuf.toString();
		if (tag.equals("null"))
		{
			expectEndTag("null");
			return null;
		}
		if (tag.equals("string"))
		{
			sbuf.setLength(0);
			parseString(sbuf);
			String value = sbuf.toString();
			expectEndTag("string");
			return value;
		} else
		{
			throw expectBeginTag("string", tag);
		}
	}

	public byte[] readBytes()
		throws IOException
	{
		if (!parseTag())
			throw new BurlapProtocolException("expected <base64>");
		String tag = sbuf.toString();
		if (tag.equals("null"))
		{
			expectEndTag("null");
			return null;
		}
		if (tag.equals("base64"))
		{
			sbuf.setLength(0);
			byte value[] = parseBytes();
			expectEndTag("base64");
			return value;
		} else
		{
			throw expectBeginTag("base64", tag);
		}
	}

	public Object readObject(Class expectedClass)
		throws IOException
	{
		if (!parseTag())
			throw new BurlapProtocolException("expected <tag>");
		String tag = sbuf.toString();
		if (tag.equals("null"))
		{
			expectEndTag("null");
			return null;
		}
		if (tag.equals("boolean"))
		{
			int value = parseInt();
			expectEndTag("boolean");
			return new Boolean(value != 0);
		}
		if (tag.equals("int"))
		{
			int value = parseInt();
			expectEndTag("int");
			return new Integer(value);
		}
		if (tag.equals("long"))
		{
			long value = parseLong();
			expectEndTag("long");
			return new Long(value);
		}
		if (tag.equals("string"))
		{
			sbuf.setLength(0);
			parseString(sbuf);
			String value = sbuf.toString();
			expectEndTag("string");
			return value;
		}
		if (tag.equals("xml"))
		{
			sbuf.setLength(0);
			parseString(sbuf);
			String value = sbuf.toString();
			expectEndTag("xml");
			return value;
		}
		if (tag.equals("date"))
		{
			if (utcCalendar == null)
				utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			long value = parseDate(utcCalendar);
			expectEndTag("date");
			return new Date(value);
		}
		if (tag.equals("map"))
		{
			String type = readType();
			return readMap(expectedClass, type);
		}
		if (tag.equals("list"))
		{
			String type = readType();
			int length = readLength();
			return readList(expectedClass, type, length);
		}
		if (tag.equals("ref"))
		{
			int value = parseInt();
			expectEndTag("ref");
			return refs.elementAt(value);
		}
		if (tag.equals("remote"))
		{
			String type = readType();
			String url = readString();
			expectEndTag("remote");
			return resolveRemote(type, url);
		} else
		{
			return readExtensionObject(expectedClass, tag);
		}
	}

	public String readType()
		throws IOException
	{
		if (!parseTag())
			throw new BurlapProtocolException("expected <type>");
		String tag = sbuf.toString();
		if (!tag.equals("type"))
		{
			throw new BurlapProtocolException("expected <type>");
		} else
		{
			sbuf.setLength(0);
			parseString(sbuf);
			String value = sbuf.toString();
			expectEndTag("type");
			return value;
		}
	}

	public int readLength()
		throws IOException
	{
		expectStartTag("length");
		int ch = skipWhitespace();
		peek = ch;
		if (ch == 60)
		{
			expectEndTag("length");
			return -1;
		} else
		{
			int value = parseInt();
			expectEndTag("length");
			return value;
		}
	}

	public Object resolveRemote(String type, String url)
		throws IOException
	{
		return new BurlapRemote(type, url);
	}

	public Hashtable readFault()
		throws IOException
	{
		expectStartTag("fault");
		Hashtable map = new Hashtable();
		do
		{
			if (!parseTag())
				break;
			peekTag = true;
			Object key = readObject(null);
			Object value = readObject(null);
			if (key != null && value != null)
				map.put(key, value);
		} while (true);
		if (!sbuf.toString().equals("fault"))
			throw new BurlapProtocolException("expected </fault>");
		else
			return map;
	}

	public Object readMap(Class expectedClass, String type)
		throws IOException
	{
		Hashtable map = new Hashtable();
		if (refs == null)
			refs = new Vector();
		refs.addElement(map);
		Object key;
		Object value;
		for (; parseTag(); map.put(key, value))
		{
			peekTag = true;
			key = readObject(null);
			value = readObject(null);
		}

		if (!sbuf.toString().equals("map"))
			throw new BurlapProtocolException("expected </map>");
		else
			return map;
	}

	protected Object readExtensionObject(Class expectedClass, String tag)
		throws IOException
	{
		throw new BurlapProtocolException((new StringBuilder()).append("unknown object tag <").append(tag).append(">").toString());
	}

	public Object readList(Class expectedClass, String type, int length)
		throws IOException
	{
		Vector list = new Vector();
		if (refs == null)
			refs = new Vector();
		refs.addElement(list);
		Object value;
		for (; parseTag(); list.addElement(value))
		{
			peekTag = true;
			value = readObject(null);
		}

		if (!sbuf.toString().equals("list"))
			throw new BurlapProtocolException("expected </list>");
		else
			return list;
	}

	protected int parseInt()
		throws IOException
	{
		int sign = 1;
		int value = 0;
		int ch = skipWhitespace();
		if (ch == 43)
			ch = read();
		else
		if (ch == 45)
		{
			sign = -1;
			ch = read();
		}
		for (; ch >= 48 && ch <= 57; ch = read())
			value = (10 * value + ch) - 48;

		peek = ch;
		return sign * value;
	}

	protected long parseLong()
		throws IOException
	{
		long sign = 1L;
		long value = 0L;
		int ch = skipWhitespace();
		if (ch == 43)
			ch = read();
		else
		if (ch == 45)
		{
			sign = -1L;
			ch = read();
		}
		for (; ch >= 48 && ch <= 57; ch = read())
			value = (10L * value + (long)ch) - 48L;

		peek = ch;
		return sign * value;
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

		for (; ch > 0 && ch != 60; ch = read());
		peek = ch;
		calendar.set(1, year);
		calendar.set(2, month - 1);
		calendar.set(5, day);
		calendar.set(11, hour);
		calendar.set(12, minute);
		calendar.set(13, second);
		calendar.set(14, 0);
		return calendar.getTime().getTime();
	}

	protected String parseString()
		throws IOException
	{
		StringBuffer sbuf = new StringBuffer();
		return parseString(sbuf).toString();
	}

	protected StringBuffer parseString(StringBuffer sbuf)
		throws IOException
	{
		int ch;
		for (ch = read(); ch >= 0 && ch != 60; ch = read())
		{
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

						sbuf.append((char)v);
					}
				} else
				{
					StringBuffer entityBuffer = new StringBuffer();
					for (; ch >= 97 && ch <= 122; ch = read())
						entityBuffer.append((char)ch);

					String entity = entityBuffer.toString();
					if (entity.equals("amp"))
						sbuf.append('&');
					else
					if (entity.equals("apos"))
						sbuf.append('\'');
					else
					if (entity.equals("quot"))
						sbuf.append('"');
					else
					if (entity.equals("lt"))
						sbuf.append('<');
					else
					if (entity.equals("gt"))
						sbuf.append('>');
					else
						throw new BurlapProtocolException((new StringBuilder()).append("unknown XML entity &").append(entity).append("; at `").append((char)ch).append("'").toString());
				}
				if (ch != 59)
					throw expectedChar("';'", ch);
				continue;
			}
			if (ch < 128)
			{
				sbuf.append((char)ch);
				continue;
			}
			if ((ch & 0xe0) == 192)
			{
				int ch1 = read();
				int v = ((ch & 0x1f) << 6) + (ch1 & 0x3f);
				sbuf.append((char)v);
				continue;
			}
			if ((ch & 0xf0) == 224)
			{
				int ch1 = read();
				int ch2 = read();
				int v = ((ch & 0xf) << 12) + ((ch1 & 0x3f) << 6) + (ch2 & 0x3f);
				sbuf.append((char)v);
			} else
			{
				throw new BurlapProtocolException("bad utf-8 encoding");
			}
		}

		peek = ch;
		return sbuf;
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
		for (ch = read(); ch >= 0 && ch != 60; ch = read())
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
				int chunk = (base64Decode[b1] << 12) + (base64Decode[b2] << 6) + base64Decode[b3];
				bos.write(chunk >> 8);
				bos.write(chunk);
			} else
			{
				int chunk = (base64Decode[b1] << 6) + base64Decode[b2];
				bos.write(chunk);
			}
		}

		if (ch == 60)
			peek = ch;
		return bos;
	}

	protected void expectStartTag(String tag)
		throws IOException
	{
		if (!parseTag())
			throw new BurlapProtocolException((new StringBuilder()).append("expected <").append(tag).append(">").toString());
		if (!sbuf.toString().equals(tag))
			throw new BurlapProtocolException((new StringBuilder()).append("expected <").append(tag).append("> at <").append(sbuf).append(">").toString());
		else
			return;
	}

	protected void expectEndTag(String tag)
		throws IOException
	{
		if (parseTag())
			throw new BurlapProtocolException((new StringBuilder()).append("expected </").append(tag).append(">").toString());
		if (!sbuf.toString().equals(tag))
			throw new BurlapProtocolException((new StringBuilder()).append("expected </").append(tag).append("> at </").append(sbuf).append(">").toString());
		else
			return;
	}

	protected boolean parseTag()
		throws IOException
	{
		if (peekTag)
		{
			peekTag = false;
			return true;
		}
		int ch = skipWhitespace();
		boolean isStartTag = true;
		if (ch != 60)
			throw expectedChar("'<'", ch);
		ch = read();
		if (ch == 47)
		{
			isStartTag = false;
			ch = is.read();
		}
		if (!isTagChar(ch))
			throw expectedChar("tag", ch);
		sbuf.setLength(0);
		for (; isTagChar(ch); ch = read())
			sbuf.append((char)ch);

		if (ch != 62)
			throw expectedChar("'>'", ch);
		else
			return isStartTag;
	}

	protected IOException expectedChar(String expect, int actualChar)
	{
		return new BurlapProtocolException((new StringBuilder()).append("expected ").append(expect).append(" at ").append((char)actualChar).append("'").toString());
	}

	protected IOException expectBeginTag(String expect, String tag)
	{
		return new BurlapProtocolException((new StringBuilder()).append("expected <").append(expect).append("> at <").append(tag).append(">").toString());
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

	protected int read()
		throws IOException
	{
		if (peek > 0)
		{
			int value = peek;
			peek = 0;
			return value;
		} else
		{
			return is.read();
		}
	}

	static 
	{
		base64Decode = new int[256];
		for (int i = 65; i <= 90; i++)
			base64Decode[i] = i - 65;

		for (int i = 97; i <= 122; i++)
			base64Decode[i] = (i - 97) + 26;

		for (int i = 48; i <= 57; i++)
			base64Decode[i] = (i - 48) + 52;

		base64Decode[43] = 62;
		base64Decode[47] = 63;
	}
}
