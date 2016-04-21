// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   MicroHessianInput.java

package com.autohome.hessian.micro;

import java.io.*;
import java.util.Date;

public class MicroHessianInput
{

	protected InputStream is;

	public MicroHessianInput(InputStream is)
	{
		init(is);
	}

	public MicroHessianInput()
	{
	}

	public void init(InputStream is)
	{
		this.is = is;
	}

	public void startReply()
		throws IOException
	{
		int tag = is.read();
		if (tag != 114)
			protocolException("expected hessian reply");
		int major = is.read();
		int minor = is.read();
	}

	public void completeReply()
		throws IOException
	{
		int tag = is.read();
		if (tag != 122)
			protocolException("expected end of reply");
	}

	public boolean readBoolean()
		throws IOException
	{
		int tag = is.read();
		switch (tag)
		{
		case 84: // 'T'
			return true;

		case 70: // 'F'
			return false;
		}
		throw expect("boolean", tag);
	}

	public int readInt()
		throws IOException
	{
		int tag = is.read();
		if (tag != 73)
		{
			throw expect("integer", tag);
		} else
		{
			int b32 = is.read();
			int b24 = is.read();
			int b16 = is.read();
			int b8 = is.read();
			return (b32 << 24) + (b24 << 16) + (b16 << 8) + b8;
		}
	}

	public long readLong()
		throws IOException
	{
		int tag = is.read();
		if (tag != 76)
		{
			throw protocolException("expected long");
		} else
		{
			long b64 = is.read();
			long b56 = is.read();
			long b48 = is.read();
			long b40 = is.read();
			long b32 = is.read();
			long b24 = is.read();
			long b16 = is.read();
			long b8 = is.read();
			return (b64 << 56) + (b56 << 48) + (b48 << 40) + (b40 << 32) + (b32 << 24) + (b24 << 16) + (b16 << 8) + b8;
		}
	}

	public long readUTCDate()
		throws IOException
	{
		int tag = is.read();
		if (tag != 100)
		{
			throw protocolException("expected date");
		} else
		{
			long b64 = is.read();
			long b56 = is.read();
			long b48 = is.read();
			long b40 = is.read();
			long b32 = is.read();
			long b24 = is.read();
			long b16 = is.read();
			long b8 = is.read();
			return (b64 << 56) + (b56 << 48) + (b48 << 40) + (b40 << 32) + (b32 << 24) + (b24 << 16) + (b16 << 8) + b8;
		}
	}

	public String readString()
		throws IOException
	{
		int tag = is.read();
		if (tag == 78)
			return null;
		if (tag != 83)
		{
			throw expect("string", tag);
		} else
		{
			int b16 = is.read();
			int b8 = is.read();
			int len = (b16 << 8) + b8;
			return readStringImpl(len);
		}
	}

	public byte[] readBytes()
		throws IOException
	{
		int tag = is.read();
		if (tag == 78)
			return null;
		if (tag != 66)
			throw expect("bytes", tag);
		int b16 = is.read();
		int b8 = is.read();
		int len = (b16 << 8) + b8;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		for (int i = 0; i < len; i++)
			bos.write(is.read());

		return bos.toByteArray();
	}

	public Object readObject(Class expectedClass)
		throws IOException
	{
		int tag = is.read();
		switch (tag)
		{
		case 78: // 'N'
		{
			return null;
		}

		case 84: // 'T'
		{
			return new Boolean(true);
		}

		case 70: // 'F'
		{
			return new Boolean(false);
		}

		case 73: // 'I'
		{
			int b32 = is.read();
			int b24 = is.read();
			int b16 = is.read();
			int b8 = is.read();
			return new Integer((b32 << 24) + (b24 << 16) + (b16 << 8) + b8);
		}

		case 76: // 'L'
		{
			long b64 = is.read();
			long b56 = is.read();
			long b48 = is.read();
			long b40 = is.read();
			long b32 = is.read();
			long b24 = is.read();
			long b16 = is.read();
			long b8 = is.read();
			return new Long((b64 << 56) + (b56 << 48) + (b48 << 40) + (b40 << 32) + (b32 << 24) + (b24 << 16) + (b16 << 8) + b8);
		}

		case 100: // 'd'
		{
			long b64 = is.read();
			long b56 = is.read();
			long b48 = is.read();
			long b40 = is.read();
			long b32 = is.read();
			long b24 = is.read();
			long b16 = is.read();
			long b8 = is.read();
			return new Date((b64 << 56) + (b56 << 48) + (b48 << 40) + (b40 << 32) + (b32 << 24) + (b24 << 16) + (b16 << 8) + b8);
		}

		case 83: // 'S'
		case 88: // 'X'
		{
			int b16 = is.read();
			int b8 = is.read();
			int len = (b16 << 8) + b8;
			return readStringImpl(len);
		}

		case 66: // 'B'
		{
			if (tag != 66)
				throw expect("bytes", tag);
			int b16 = is.read();
			int b8 = is.read();
			int len = (b16 << 8) + b8;
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			for (int i = 0; i < len; i++)
				bos.write(is.read());

			return bos.toByteArray();
		}

		case 67: // 'C'
		case 68: // 'D'
		case 69: // 'E'
		case 71: // 'G'
		case 72: // 'H'
		case 74: // 'J'
		case 75: // 'K'
		case 77: // 'M'
		case 79: // 'O'
		case 80: // 'P'
		case 81: // 'Q'
		case 82: // 'R'
		case 85: // 'U'
		case 86: // 'V'
		case 87: // 'W'
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
		default:
		{
			throw new IOException((new StringBuilder()).append("unknown code:").append((char)tag).toString());
		}
		}
	}

	protected String readStringImpl(int length)
		throws IOException
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++)
		{
			int ch = is.read();
			if (ch < 128)
			{
				sb.append((char)ch);
				continue;
			}
			if ((ch & 0xe0) == 192)
			{
				int ch1 = is.read();
				int v = ((ch & 0x1f) << 6) + (ch1 & 0x3f);
				sb.append((char)v);
				continue;
			}
			if ((ch & 0xf0) == 224)
			{
				int ch1 = is.read();
				int ch2 = is.read();
				int v = ((ch & 0xf) << 12) + ((ch1 & 0x3f) << 6) + (ch2 & 0x3f);
				sb.append((char)v);
			} else
			{
				throw new IOException("bad utf-8 encoding");
			}
		}

		return sb.toString();
	}

	protected IOException expect(String expect, int ch)
	{
		if (ch < 0)
			return protocolException((new StringBuilder()).append("expected ").append(expect).append(" at end of file").toString());
		else
			return protocolException((new StringBuilder()).append("expected ").append(expect).append(" at ").append((char)ch).toString());
	}

	protected IOException protocolException(String message)
	{
		return new IOException(message);
	}
}
