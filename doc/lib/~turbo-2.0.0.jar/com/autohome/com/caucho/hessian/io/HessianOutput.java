// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HessianOutput.java

package com.autohome.com.caucho.hessian.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.IdentityHashMap;

// Referenced classes of package com.autohome.com.caucho.hessian.io:
//			AbstractHessianOutput, SerializerFactory, Serializer

public class HessianOutput extends AbstractHessianOutput
{

	protected OutputStream os;
	private IdentityHashMap _refs;
	private int _version;

	public HessianOutput(OutputStream os)
	{
		_version = 1;
		init(os);
	}

	public HessianOutput()
	{
		_version = 1;
	}

	public void init(OutputStream os)
	{
		this.os = os;
		_refs = null;
		if (_serializerFactory == null)
			_serializerFactory = new SerializerFactory();
	}

	public void setVersion(int version)
	{
		_version = version;
	}

	public void call(String method, Object args[])
		throws IOException
	{
		int length = args == null ? 0 : args.length;
		startCall(method, length);
		for (int i = 0; i < length; i++)
			writeObject(args[i]);

		completeCall();
	}

	public void startCall(String method, int length)
		throws IOException
	{
		os.write(99);
		os.write(_version);
		os.write(0);
		os.write(109);
		int len = method.length();
		os.write(len >> 8);
		os.write(len);
		printString(method, 0, len);
	}

	public void startCall()
		throws IOException
	{
		os.write(99);
		os.write(0);
		os.write(1);
	}

	public void writeMethod(String method)
		throws IOException
	{
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

	public void startReply()
		throws IOException
	{
		os.write(114);
		os.write(1);
		os.write(0);
	}

	public void completeReply()
		throws IOException
	{
		os.write(122);
	}

	public void writeHeader(String name)
		throws IOException
	{
		int len = name.length();
		os.write(72);
		os.write(len >> 8);
		os.write(len);
		printString(name);
	}

	public void writeFault(String code, String message, Object detail)
		throws IOException
	{
		os.write(102);
		writeString("code");
		writeString(code);
		writeString("message");
		writeString(message);
		if (detail != null)
		{
			writeString("detail");
			writeObject(detail);
		}
		os.write(122);
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
		os.write(86);
		if (type != null)
		{
			os.write(116);
			printLenString(type);
		}
		if (length >= 0)
		{
			os.write(108);
			os.write(length >> 24);
			os.write(length >> 16);
			os.write(length >> 8);
			os.write(length);
		}
		return true;
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

	public void writeDouble(double value)
		throws IOException
	{
		long bits = Double.doubleToLongBits(value);
		os.write(68);
		os.write((byte)(int)(bits >> 56));
		os.write((byte)(int)(bits >> 48));
		os.write((byte)(int)(bits >> 40));
		os.write((byte)(int)(bits >> 32));
		os.write((byte)(int)(bits >> 24));
		os.write((byte)(int)(bits >> 16));
		os.write((byte)(int)(bits >> 8));
		os.write((byte)(int)bits);
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
			int length = value.length();
			int offset;
			int sublen;
			for (offset = 0; length > 32768; offset += sublen)
			{
				sublen = 32768;
				char tail = value.charAt((offset + sublen) - 1);
				if ('\uD800' <= tail && tail <= '\uDBFF')
					sublen--;
				os.write(115);
				os.write(sublen >> 8);
				os.write(sublen);
				printString(value, offset, sublen);
				length -= sublen;
			}

			os.write(83);
			os.write(length >> 8);
			os.write(length);
			printString(value, offset, length);
		}
	}

	public void writeString(char buffer[], int offset, int length)
		throws IOException
	{
		if (buffer == null)
		{
			os.write(78);
		} else
		{
			while (length > 32768) 
			{
				int sublen = 32768;
				char tail = buffer[(offset + sublen) - 1];
				if ('\uD800' <= tail && tail <= '\uDBFF')
					sublen--;
				os.write(115);
				os.write(sublen >> 8);
				os.write(sublen);
				printString(buffer, offset, sublen);
				length -= sublen;
				offset += sublen;
			}
			os.write(83);
			os.write(length >> 8);
			os.write(length);
			printString(buffer, offset, length);
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
			while (length > 32768) 
			{
				int sublen = 32768;
				os.write(98);
				os.write(sublen >> 8);
				os.write(sublen);
				os.write(buffer, offset, sublen);
				length -= sublen;
				offset += sublen;
			}
			os.write(66);
			os.write(length >> 8);
			os.write(length);
			os.write(buffer, offset, length);
		}
	}

	public void writeByteBufferStart()
		throws IOException
	{
	}

	public void writeByteBufferPart(byte buffer[], int offset, int length)
		throws IOException
	{
		while (length > 0) 
		{
			int sublen = length;
			if (32768 < sublen)
				sublen = 32768;
			os.write(98);
			os.write(sublen >> 8);
			os.write(sublen);
			os.write(buffer, offset, sublen);
			length -= sublen;
			offset += sublen;
		}
	}

	public void writeByteBufferEnd(byte buffer[], int offset, int length)
		throws IOException
	{
		writeBytes(buffer, offset, length);
	}

	public void writeRef(int value)
		throws IOException
	{
		os.write(82);
		os.write(value >> 24);
		os.write(value >> 16);
		os.write(value >> 8);
		os.write(value);
	}

	public void writePlaceholder()
		throws IOException
	{
		os.write(80);
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

	public void resetReferences()
	{
		if (_refs != null)
			_refs.clear();
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

	public void flush()
		throws IOException
	{
		if (os != null)
			os.flush();
	}

	public void close()
		throws IOException
	{
		if (os != null)
			os.flush();
	}
}
