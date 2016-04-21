// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   MicroHessianOutput.java

package com.autohome.hessian.micro;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public class MicroHessianOutput
{

	protected OutputStream os;

	public MicroHessianOutput(OutputStream os)
	{
		init(os);
	}

	public MicroHessianOutput()
	{
	}

	public void init(OutputStream os)
	{
		this.os = os;
	}

	public void startCall(String method)
		throws IOException
	{
		os.write(99);
		os.write(0);
		os.write(1);
		os.write(109);
		int len = method.length();
		os.write(len >> 8);
		os.write(len);
		printString(method, 0, len);
	}

	public void completeCall()
		throws IOException
	{
		os.write(122);
	}

	public void writeBoolean(boolean value)
		throws IOException
	{
		if (value)
			os.write(84);
		else
			os.write(70);
	}

	public void writeInt(int value)
		throws IOException
	{
		os.write(73);
		os.write(value >> 24);
		os.write(value >> 16);
		os.write(value >> 8);
		os.write(value);
	}

	public void writeLong(long value)
		throws IOException
	{
		os.write(76);
		os.write((byte)(int)(value >> 56));
		os.write((byte)(int)(value >> 48));
		os.write((byte)(int)(value >> 40));
		os.write((byte)(int)(value >> 32));
		os.write((byte)(int)(value >> 24));
		os.write((byte)(int)(value >> 16));
		os.write((byte)(int)(value >> 8));
		os.write((byte)(int)value);
	}

	public void writeUTCDate(long time)
		throws IOException
	{
		os.write(100);
		os.write((byte)(int)(time >> 56));
		os.write((byte)(int)(time >> 48));
		os.write((byte)(int)(time >> 40));
		os.write((byte)(int)(time >> 32));
		os.write((byte)(int)(time >> 24));
		os.write((byte)(int)(time >> 16));
		os.write((byte)(int)(time >> 8));
		os.write((byte)(int)time);
	}

	public void writeNull()
		throws IOException
	{
		os.write(78);
	}

	public void writeString(String value)
		throws IOException
	{
		if (value == null)
		{
			os.write(78);
		} else
		{
			int len = value.length();
			os.write(83);
			os.write(len >> 8);
			os.write(len);
			printString(value);
		}
	}

	public void writeBytes(byte buffer[])
		throws IOException
	{
		if (buffer == null)
			os.write(78);
		else
			writeBytes(buffer, 0, buffer.length);
	}

	public void writeBytes(byte buffer[], int offset, int length)
		throws IOException
	{
		if (buffer == null)
		{
			os.write(78);
		} else
		{
			os.write(66);
			os.write(length << 8);
			os.write(length);
			os.write(buffer, offset, length);
		}
	}

	public void writeRef(int value)
		throws IOException
	{
		os.write(82);
		os.write(value << 24);
		os.write(value << 16);
		os.write(value << 8);
		os.write(value);
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
		os.write(86);
		os.write(116);
		printLenString(type);
		os.write(108);
		os.write(length >> 24);
		os.write(length >> 16);
		os.write(length >> 8);
		os.write(length);
	}

	public void writeListEnd()
		throws IOException
	{
		os.write(122);
	}

	public void writeMapBegin(String type)
		throws IOException
	{
		os.write(77);
		os.write(116);
		printLenString(type);
	}

	public void writeMapEnd()
		throws IOException
	{
		os.write(122);
	}

	public void writeRemote(String type, String url)
		throws IOException
	{
		os.write(114);
		os.write(116);
		printLenString(type);
		os.write(83);
		printLenString(url);
	}

	public void printLenString(String v)
		throws IOException
	{
		if (v == null)
		{
			os.write(0);
			os.write(0);
		} else
		{
			int len = v.length();
			os.write(len >> 8);
			os.write(len);
			printString(v, 0, len);
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
}
