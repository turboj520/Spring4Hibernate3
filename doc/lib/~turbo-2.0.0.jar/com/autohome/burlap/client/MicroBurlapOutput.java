// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   MicroBurlapOutput.java

package com.autohome.burlap.client;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public class MicroBurlapOutput
{

	private OutputStream os;
	private Date date;
	private Calendar utcCalendar;
	private Calendar localCalendar;

	public MicroBurlapOutput(OutputStream os)
	{
		init(os);
	}

	public MicroBurlapOutput()
	{
	}

	public void init(OutputStream os)
	{
		this.os = os;
	}

	public void call(String method, Object args[])
		throws IOException
	{
		startCall(method);
		if (args != null)
		{
			for (int i = 0; i < args.length; i++)
				writeObject(args[i]);

		}
		completeCall();
	}

	public void startCall(String method)
		throws IOException
	{
		print("<burlap:call><method>");
		print(method);
		print("</method>");
	}

	public void completeCall()
		throws IOException
	{
		print("</burlap:call>");
	}

	public void writeBoolean(boolean value)
		throws IOException
	{
		print("<boolean>");
		printInt(value ? 1 : 0);
		print("</boolean>");
	}

	public void writeInt(int value)
		throws IOException
	{
		print("<int>");
		printInt(value);
		print("</int>");
	}

	public void writeLong(long value)
		throws IOException
	{
		print("<long>");
		printLong(value);
		print("</long>");
	}

	public void writeNull()
		throws IOException
	{
		print("<null></null>");
	}

	public void writeString(String value)
		throws IOException
	{
		if (value == null)
		{
			print("<null></null>");
		} else
		{
			print("<string>");
			printString(value);
			print("</string>");
		}
	}

	public void writeBytes(byte buffer[], int offset, int length)
		throws IOException
	{
		if (buffer == null)
		{
			print("<null></null>");
		} else
		{
			print("<base64>");
			printBytes(buffer, offset, length);
			print("</base64>");
		}
	}

	public void writeUTCDate(long time)
		throws IOException
	{
		print("<date>");
		if (utcCalendar == null)
		{
			utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			date = new Date();
		}
		date.setTime(time);
		utcCalendar.setTime(date);
		printDate(utcCalendar);
		print("</date>");
	}

	public void writeLocalDate(long time)
		throws IOException
	{
		print("<date>");
		if (localCalendar == null)
		{
			localCalendar = Calendar.getInstance();
			date = new Date();
		}
		date.setTime(time);
		localCalendar.setTime(date);
		printDate(localCalendar);
		print("</date>");
	}

	public void writeRef(int value)
		throws IOException
	{
		print("<ref>");
		printInt(value);
		print("</ref>");
	}

	public void writeObject(Object object)
		throws IOException
	{
		if (object == null)
			writeNull();
		else
		if (object instanceof String)
			writeString((String)object);
		else
		if (object instanceof Boolean)
			writeBoolean(((Boolean)object).booleanValue());
		else
		if (object instanceof Integer)
			writeInt(((Integer)object).intValue());
		else
		if (object instanceof Long)
			writeLong(((Long)object).longValue());
		else
		if (object instanceof Date)
			writeUTCDate(((Date)object).getTime());
		else
		if (object instanceof byte[])
		{
			byte data[] = (byte[])(byte[])object;
			writeBytes(data, 0, data.length);
		} else
		if (object instanceof Vector)
		{
			Vector vector = (Vector)object;
			int size = vector.size();
			writeListBegin(size, null);
			for (int i = 0; i < size; i++)
				writeObject(vector.elementAt(i));

			writeListEnd();
		} else
		if (object instanceof Hashtable)
		{
			Hashtable hashtable = (Hashtable)object;
			writeMapBegin(null);
			Object value;
			for (Enumeration e = hashtable.keys(); e.hasMoreElements(); writeObject(value))
			{
				Object key = e.nextElement();
				value = hashtable.get(key);
				writeObject(key);
			}

			writeMapEnd();
		} else
		{
			writeCustomObject(object);
		}
	}

	public void writeCustomObject(Object object)
		throws IOException
	{
		throw new IOException((new StringBuilder()).append("unexpected object: ").append(object).toString());
	}

	public void writeListBegin(int length, String type)
		throws IOException
	{
		print("<list><type>");
		if (type != null)
			print(type);
		print("</type><length>");
		printInt(length);
		print("</length>");
	}

	public void writeListEnd()
		throws IOException
	{
		print("</list>");
	}

	public void writeMapBegin(String type)
		throws IOException
	{
		print("<map><type>");
		if (type != null)
			print(type);
		print("</type>");
	}

	public void writeMapEnd()
		throws IOException
	{
		print("</map>");
	}

	public void writeRemote(String type, String url)
		throws IOException
	{
		print("<remote><type>");
		if (type != null)
			print(type);
		print("</type><string>");
		print(url);
		print("</string></remote>");
	}

	public void printInt(int v)
		throws IOException
	{
		print(String.valueOf(v));
	}

	public void printLong(long v)
		throws IOException
	{
		print(String.valueOf(v));
	}

	public void printString(String v)
		throws IOException
	{
		int len = v.length();
		for (int i = 0; i < len; i++)
		{
			char ch = v.charAt(i);
			switch (ch)
			{
			case 60: // '<'
				print("&lt;");
				break;

			case 38: // '&'
				print("&amp;");
				break;

			case 13: // '\r'
				print("&#13;");
				break;

			default:
				if (ch < '\200')
				{
					os.write(ch);
					break;
				}
				if (ch < '\u0800')
				{
					os.write(192 + (ch >> 6 & 0x1f));
					os.write(128 + (ch & 0x3f));
				} else
				{
					os.write(224 + (ch >> 12 & 0xf));
					os.write(128 + (ch >> 6 & 0x3f));
					os.write(128 + (ch & 0x3f));
				}
				break;
			}
		}

	}

	public void printBytes(byte data[], int offset, int length)
		throws IOException
	{
		for (; length >= 3; length -= 3)
		{
			int chunk = ((data[offset] & 0xff) << 16) + ((data[offset + 1] & 0xff) << 8) + (data[offset + 2] & 0xff);
			os.write(base64encode(chunk >> 18));
			os.write(base64encode(chunk >> 12));
			os.write(base64encode(chunk >> 6));
			os.write(base64encode(chunk));
			offset += 3;
		}

		if (length == 2)
		{
			int chunk = ((data[offset] & 0xff) << 8) + (data[offset + 1] & 0xff);
			os.write(base64encode(chunk >> 12));
			os.write(base64encode(chunk >> 6));
			os.write(base64encode(chunk));
			os.write(61);
		} else
		if (length == 1)
		{
			int chunk = data[offset] & 0xff;
			os.write(base64encode(chunk >> 6));
			os.write(base64encode(chunk));
			os.write(61);
			os.write(61);
		}
	}

	public static char base64encode(int d)
	{
		d &= 0x3f;
		if (d < 26)
			return (char)(d + 65);
		if (d < 52)
			return (char)((d + 97) - 26);
		if (d < 62)
			return (char)((d + 48) - 52);
		return d != 62 ? '/' : '+';
	}

	public void printDate(Calendar calendar)
		throws IOException
	{
		int year = calendar.get(1);
		os.write((char)(48 + (year / 1000) % 10));
		os.write((char)(48 + (year / 100) % 10));
		os.write((char)(48 + (year / 10) % 10));
		os.write((char)(48 + year % 10));
		int month = calendar.get(2) + 1;
		os.write((char)(48 + (month / 10) % 10));
		os.write((char)(48 + month % 10));
		int day = calendar.get(5);
		os.write((char)(48 + (day / 10) % 10));
		os.write((char)(48 + day % 10));
		os.write(84);
		int hour = calendar.get(11);
		os.write((char)(48 + (hour / 10) % 10));
		os.write((char)(48 + hour % 10));
		int minute = calendar.get(12);
		os.write((char)(48 + (minute / 10) % 10));
		os.write((char)(48 + minute % 10));
		int second = calendar.get(13);
		os.write((char)(48 + (second / 10) % 10));
		os.write((char)(48 + second % 10));
		os.write(90);
	}

	public void print(String s)
		throws IOException
	{
		int len = s.length();
		for (int i = 0; i < len; i++)
		{
			int ch = s.charAt(i);
			os.write(ch);
		}

	}
}
