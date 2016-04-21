// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   BurlapOutput.java

package com.autohome.burlap.io;

import com.autohome.hessian.io.Serializer;
import com.autohome.hessian.io.SerializerFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

// Referenced classes of package com.autohome.burlap.io:
//			AbstractBurlapOutput

public class BurlapOutput extends AbstractBurlapOutput
{

	protected OutputStream os;
	private IdentityHashMap _refs;
	private Date date;
	private Calendar utcCalendar;
	private Calendar localCalendar;

	public BurlapOutput(OutputStream os)
	{
		init(os);
	}

	public BurlapOutput()
	{
	}

	public void init(OutputStream os)
	{
		this.os = os;
		_refs = null;
		if (_serializerFactory == null)
			_serializerFactory = new SerializerFactory();
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

	public void startCall()
		throws IOException
	{
		print("<burlap:call>");
	}

	public void writeMethod(String method)
		throws IOException
	{
		print("<method>");
		print(method);
		print("</method>");
	}

	public void completeCall()
		throws IOException
	{
		print("</burlap:call>");
	}

	public void startReply()
		throws IOException
	{
		print("<burlap:reply>");
	}

	public void completeReply()
		throws IOException
	{
		print("</burlap:reply>");
	}

	public void writeHeader(String name)
		throws IOException
	{
		print("<header>");
		printString(name);
		print("</header>");
	}

	public void writeFault(String code, String message, Object detail)
		throws IOException
	{
		print("<fault>");
		writeString("code");
		writeString(code);
		writeString("message");
		writeString(message);
		if (detail != null)
		{
			writeString("detail");
			writeObject(detail);
		}
		print("</fault>");
	}

	public void writeObject(Object object)
		throws IOException
	{
		if (object == null)
		{
			writeNull();
			return;
		} else
		{
			Serializer serializer = _serializerFactory.getSerializer(object.getClass());
			serializer.writeObject(object, this);
			return;
		}
	}

	public boolean writeListBegin(int length, String type)
		throws IOException
	{
		print("<list><type>");
		if (type != null)
			print(type);
		print("</type><length>");
		print(length);
		print("</length>");
		return true;
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
		print(type);
		print("</type><string>");
		print(url);
		print("</string></remote>");
	}

	public void writeBoolean(boolean value)
		throws IOException
	{
		if (value)
			print("<boolean>1</boolean>");
		else
			print("<boolean>0</boolean>");
	}

	public void writeInt(int value)
		throws IOException
	{
		print("<int>");
		print(value);
		print("</int>");
	}

	public void writeLong(long value)
		throws IOException
	{
		print("<long>");
		print(value);
		print("</long>");
	}

	public void writeDouble(double value)
		throws IOException
	{
		print("<double>");
		print(value);
		print("</double>");
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

	public void writeString(char buffer[], int offset, int length)
		throws IOException
	{
		if (buffer == null)
		{
			print("<null></null>");
		} else
		{
			print("<string>");
			printString(buffer, offset, length);
			print("</string>");
		}
	}

	public void writeBytes(byte buffer[])
		throws IOException
	{
		if (buffer == null)
			print("<null></null>");
		else
			writeBytes(buffer, 0, buffer.length);
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
			int i;
			for (i = 0; i + 2 < length; i += 3)
			{
				if (i != 0 && (i & 0x3f) == 0)
					print('\n');
				int v = ((buffer[offset + i] & 0xff) << 16) + ((buffer[offset + i + 1] & 0xff) << 8) + (buffer[offset + i + 2] & 0xff);
				print(encode(v >> 18));
				print(encode(v >> 12));
				print(encode(v >> 6));
				print(encode(v));
			}

			if (i + 1 < length)
			{
				int v = ((buffer[offset + i] & 0xff) << 8) + (buffer[offset + i + 1] & 0xff);
				print(encode(v >> 10));
				print(encode(v >> 4));
				print(encode(v << 2));
				print('=');
			} else
			if (i < length)
			{
				int v = buffer[offset + i] & 0xff;
				print(encode(v >> 2));
				print(encode(v << 4));
				print('=');
				print('=');
			}
			print("</base64>");
		}
	}

	public void writeByteBufferStart()
		throws IOException
	{
		throw new UnsupportedOperationException();
	}

	public void writeByteBufferPart(byte buffer[], int offset, int length)
		throws IOException
	{
		throw new UnsupportedOperationException();
	}

	public void writeByteBufferEnd(byte buffer[], int offset, int length)
		throws IOException
	{
		throw new UnsupportedOperationException();
	}

	private char encode(int d)
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

	public void writeRef(int value)
		throws IOException
	{
		print("<ref>");
		print(value);
		print("</ref>");
	}

	public boolean addRef(Object object)
		throws IOException
	{
		if (_refs == null)
			_refs = new IdentityHashMap();
		Integer ref = (Integer)_refs.get(object);
		if (ref != null)
		{
			int value = ref.intValue();
			writeRef(value);
			return true;
		} else
		{
			_refs.put(object, new Integer(_refs.size()));
			return false;
		}
	}

	public int getRef(Object obj)
	{
		if (_refs == null)
			return -1;
		Integer ref = (Integer)_refs.get(obj);
		if (ref != null)
			return ref.intValue();
		else
			return -1;
	}

	public boolean removeRef(Object obj)
		throws IOException
	{
		if (_refs != null)
		{
			_refs.remove(obj);
			return true;
		} else
		{
			return false;
		}
	}

	public boolean replaceRef(Object oldRef, Object newRef)
		throws IOException
	{
		Integer value = (Integer)_refs.remove(oldRef);
		if (value != null)
		{
			_refs.put(newRef, value);
			return true;
		} else
		{
			return false;
		}
	}

	public void printString(String v)
		throws IOException
	{
		printString(v, 0, v.length());
	}

	public void printString(String v, int offset, int length)
		throws IOException
	{
		for (int i = 0; i < length; i++)
		{
			char ch = v.charAt(i + offset);
			if (ch == '<')
			{
				os.write(38);
				os.write(35);
				os.write(54);
				os.write(48);
				os.write(59);
				continue;
			}
			if (ch == '&')
			{
				os.write(38);
				os.write(35);
				os.write(51);
				os.write(56);
				os.write(59);
				continue;
			}
			if (ch < '\200')
			{
				os.write(ch);
				continue;
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
		}

	}

	public void printString(char v[], int offset, int length)
		throws IOException
	{
		for (int i = 0; i < length; i++)
		{
			char ch = v[i + offset];
			if (ch < '\200')
			{
				os.write(ch);
				continue;
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
		}

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
		int ms = calendar.get(14);
		os.write(46);
		os.write((char)(48 + (ms / 100) % 10));
		os.write((char)(48 + (ms / 10) % 10));
		os.write((char)(48 + ms % 10));
		os.write(90);
	}

	protected void print(char v)
		throws IOException
	{
		os.write(v);
	}

	protected void print(int v)
		throws IOException
	{
		print(String.valueOf(v));
	}

	protected void print(long v)
		throws IOException
	{
		print(String.valueOf(v));
	}

	protected void print(double v)
		throws IOException
	{
		print(String.valueOf(v));
	}

	protected void print(String s)
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
