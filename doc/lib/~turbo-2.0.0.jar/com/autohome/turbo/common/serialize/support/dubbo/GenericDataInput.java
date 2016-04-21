// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   GenericDataInput.java

package com.autohome.turbo.common.serialize.support.dubbo;

import com.autohome.turbo.common.serialize.DataInput;
import java.io.*;

// Referenced classes of package com.autohome.turbo.common.serialize.support.dubbo:
//			GenericDataFlags

public class GenericDataInput
	implements DataInput, GenericDataFlags
{

	private static final String EMPTY_STRING = "";
	private static final byte EMPTY_BYTES[] = new byte[0];
	private final InputStream mInput;
	private final byte mBuffer[];
	private int mRead;
	private int mPosition;

	public GenericDataInput(InputStream is)
	{
		this(is, 1024);
	}

	public GenericDataInput(InputStream is, int buffSize)
	{
		mRead = 0;
		mPosition = 0;
		mInput = is;
		mBuffer = new byte[buffSize];
	}

	public boolean readBool()
		throws IOException
	{
		byte b = read0();
		switch (b)
		{
		case 25: // '\031'
			return false;

		case 26: // '\032'
			return true;
		}
		throw new IOException((new StringBuilder()).append("Tag error, expect BYTE_TRUE|BYTE_FALSE, but get ").append(b).toString());
	}

	public byte readByte()
		throws IOException
	{
		byte b = read0();
		switch (b)
		{
		case 0: // '\0'
			return read0();

		case 25: // '\031'
			return 0;

		case 26: // '\032'
			return 1;

		case 27: // '\033'
			return 2;

		case 28: // '\034'
			return 3;

		case 29: // '\035'
			return 4;

		case 30: // '\036'
			return 5;

		case 31: // '\037'
			return 6;

		case 32: // ' '
			return 7;

		case 33: // '!'
			return 8;

		case 34: // '"'
			return 9;

		case 35: // '#'
			return 10;

		case 36: // '$'
			return 11;

		case 37: // '%'
			return 12;

		case 38: // '&'
			return 13;

		case 39: // '\''
			return 14;

		case 40: // '('
			return 15;

		case 41: // ')'
			return 16;

		case 42: // '*'
			return 17;

		case 43: // '+'
			return 18;

		case 44: // ','
			return 19;

		case 45: // '-'
			return 20;

		case 46: // '.'
			return 21;

		case 47: // '/'
			return 22;

		case 48: // '0'
			return 23;

		case 49: // '1'
			return 24;

		case 50: // '2'
			return 25;

		case 51: // '3'
			return 26;

		case 52: // '4'
			return 27;

		case 53: // '5'
			return 28;

		case 54: // '6'
			return 29;

		case 55: // '7'
			return 30;

		case 56: // '8'
			return 31;

		case 1: // '\001'
		case 2: // '\002'
		case 3: // '\003'
		case 4: // '\004'
		case 5: // '\005'
		case 6: // '\006'
		case 7: // '\007'
		case 8: // '\b'
		case 9: // '\t'
		case 10: // '\n'
		case 11: // '\013'
		case 12: // '\f'
		case 13: // '\r'
		case 14: // '\016'
		case 15: // '\017'
		case 16: // '\020'
		case 17: // '\021'
		case 18: // '\022'
		case 19: // '\023'
		case 20: // '\024'
		case 21: // '\025'
		case 22: // '\026'
		case 23: // '\027'
		case 24: // '\030'
		default:
			throw new IOException((new StringBuilder()).append("Tag error, expect VARINT, but get ").append(b).toString());
		}
	}

	public short readShort()
		throws IOException
	{
		return (short)readVarint32();
	}

	public int readInt()
		throws IOException
	{
		return readVarint32();
	}

	public long readLong()
		throws IOException
	{
		return readVarint64();
	}

	public float readFloat()
		throws IOException
	{
		return Float.intBitsToFloat(readVarint32());
	}

	public double readDouble()
		throws IOException
	{
		return Double.longBitsToDouble(readVarint64());
	}

	public String readUTF()
		throws IOException
	{
		byte b = read0();
		switch (b)
		{
		case -125: 
			int len = readUInt();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < len; i++)
			{
				byte b1 = read0();
				if ((b1 & 0x80) == 0)
				{
					sb.append((char)b1);
					continue;
				}
				if ((b1 & 0xe0) == 192)
				{
					byte b2 = read0();
					sb.append((char)((b1 & 0x1f) << 6 | b2 & 0x3f));
					continue;
				}
				if ((b1 & 0xf0) == 224)
				{
					byte b2 = read0();
					byte b3 = read0();
					sb.append((char)((b1 & 0xf) << 12 | (b2 & 0x3f) << 6 | b3 & 0x3f));
				} else
				{
					throw new UTFDataFormatException((new StringBuilder()).append("Bad utf-8 encoding at ").append(b1).toString());
				}
			}

			return sb.toString();

		case -108: 
			return null;

		case -107: 
			return "";
		}
		throw new IOException((new StringBuilder()).append("Tag error, expect BYTES|BYTES_NULL|BYTES_EMPTY, but get ").append(b).toString());
	}

	public byte[] readBytes()
		throws IOException
	{
		byte b = read0();
		switch (b)
		{
		case -125: 
			return read0(readUInt());

		case -108: 
			return null;

		case -107: 
			return EMPTY_BYTES;
		}
		throw new IOException((new StringBuilder()).append("Tag error, expect BYTES|BYTES_NULL|BYTES_EMPTY, but get ").append(b).toString());
	}

	public int readUInt()
		throws IOException
	{
		byte tmp = read0();
		if (tmp < 0)
			return tmp & 0x7f;
		int ret = tmp & 0x7f;
		if ((tmp = read0()) < 0)
		{
			ret |= (tmp & 0x7f) << 7;
		} else
		{
			ret |= tmp << 7;
			if ((tmp = read0()) < 0)
			{
				ret |= (tmp & 0x7f) << 14;
			} else
			{
				ret |= tmp << 14;
				if ((tmp = read0()) < 0)
				{
					ret |= (tmp & 0x7f) << 21;
				} else
				{
					ret |= tmp << 21;
					ret |= (read0() & 0x7f) << 28;
				}
			}
		}
		return ret;
	}

	protected byte read0()
		throws IOException
	{
		if (mPosition == mRead)
			fillBuffer();
		return mBuffer[mPosition++];
	}

	protected byte[] read0(int len)
		throws IOException
	{
		int rem = mRead - mPosition;
		byte ret[] = new byte[len];
		if (len <= rem)
		{
			System.arraycopy(mBuffer, mPosition, ret, 0, len);
			mPosition += len;
		} else
		{
			System.arraycopy(mBuffer, mPosition, ret, 0, rem);
			mPosition = mRead;
			len -= rem;
			int pos = rem;
			int read;
			for (; len > 0; len -= read)
			{
				read = mInput.read(ret, pos, len);
				if (read == -1)
					throw new EOFException();
				pos += read;
			}

		}
		return ret;
	}

	private int readVarint32()
		throws IOException
	{
		byte b = read0();
		switch (b)
		{
		case 0: // '\0'
		{
			return read0();
		}

		case 1: // '\001'
		{
			byte b1 = read0();
			byte b2 = read0();
			return (short)(b1 & 0xff | (b2 & 0xff) << 8);
		}

		case 2: // '\002'
		{
			byte b1 = read0();
			byte b2 = read0();
			byte b3 = read0();
			int ret = b1 & 0xff | (b2 & 0xff) << 8 | (b3 & 0xff) << 16;
			if (b3 < 0)
				return ret | 0xff000000;
			else
				return ret;
		}

		case 3: // '\003'
		{
			byte b1 = read0();
			byte b2 = read0();
			byte b3 = read0();
			byte b4 = read0();
			return b1 & 0xff | (b2 & 0xff) << 8 | (b3 & 0xff) << 16 | (b4 & 0xff) << 24;
		}

		case 10: // '\n'
		{
			return -15;
		}

		case 11: // '\013'
		{
			return -14;
		}

		case 12: // '\f'
		{
			return -13;
		}

		case 13: // '\r'
		{
			return -12;
		}

		case 14: // '\016'
		{
			return -11;
		}

		case 15: // '\017'
		{
			return -10;
		}

		case 16: // '\020'
		{
			return -9;
		}

		case 17: // '\021'
		{
			return -8;
		}

		case 18: // '\022'
		{
			return -7;
		}

		case 19: // '\023'
		{
			return -6;
		}

		case 20: // '\024'
		{
			return -5;
		}

		case 21: // '\025'
		{
			return -4;
		}

		case 22: // '\026'
		{
			return -3;
		}

		case 23: // '\027'
		{
			return -2;
		}

		case 24: // '\030'
		{
			return -1;
		}

		case 25: // '\031'
		{
			return 0;
		}

		case 26: // '\032'
		{
			return 1;
		}

		case 27: // '\033'
		{
			return 2;
		}

		case 28: // '\034'
		{
			return 3;
		}

		case 29: // '\035'
		{
			return 4;
		}

		case 30: // '\036'
		{
			return 5;
		}

		case 31: // '\037'
		{
			return 6;
		}

		case 32: // ' '
		{
			return 7;
		}

		case 33: // '!'
		{
			return 8;
		}

		case 34: // '"'
		{
			return 9;
		}

		case 35: // '#'
		{
			return 10;
		}

		case 36: // '$'
		{
			return 11;
		}

		case 37: // '%'
		{
			return 12;
		}

		case 38: // '&'
		{
			return 13;
		}

		case 39: // '\''
		{
			return 14;
		}

		case 40: // '('
		{
			return 15;
		}

		case 41: // ')'
		{
			return 16;
		}

		case 42: // '*'
		{
			return 17;
		}

		case 43: // '+'
		{
			return 18;
		}

		case 44: // ','
		{
			return 19;
		}

		case 45: // '-'
		{
			return 20;
		}

		case 46: // '.'
		{
			return 21;
		}

		case 47: // '/'
		{
			return 22;
		}

		case 48: // '0'
		{
			return 23;
		}

		case 49: // '1'
		{
			return 24;
		}

		case 50: // '2'
		{
			return 25;
		}

		case 51: // '3'
		{
			return 26;
		}

		case 52: // '4'
		{
			return 27;
		}

		case 53: // '5'
		{
			return 28;
		}

		case 54: // '6'
		{
			return 29;
		}

		case 55: // '7'
		{
			return 30;
		}

		case 56: // '8'
		{
			return 31;
		}

		case 4: // '\004'
		case 5: // '\005'
		case 6: // '\006'
		case 7: // '\007'
		case 8: // '\b'
		case 9: // '\t'
		default:
		{
			throw new IOException((new StringBuilder()).append("Tag error, expect VARINT, but get ").append(b).toString());
		}
		}
	}

	private long readVarint64()
		throws IOException
	{
		byte b = read0();
		switch (b)
		{
		case 0: // '\0'
		{
			return (long)read0();
		}

		case 1: // '\001'
		{
			byte b1 = read0();
			byte b2 = read0();
			return (long)(short)(b1 & 0xff | (b2 & 0xff) << 8);
		}

		case 2: // '\002'
		{
			byte b1 = read0();
			byte b2 = read0();
			byte b3 = read0();
			int ret = b1 & 0xff | (b2 & 0xff) << 8 | (b3 & 0xff) << 16;
			if (b3 < 0)
				return (long)(ret | 0xff000000);
			else
				return (long)ret;
		}

		case 3: // '\003'
		{
			byte b1 = read0();
			byte b2 = read0();
			byte b3 = read0();
			byte b4 = read0();
			return (long)(b1 & 0xff | (b2 & 0xff) << 8 | (b3 & 0xff) << 16 | (b4 & 0xff) << 24);
		}

		case 4: // '\004'
		{
			byte b1 = read0();
			byte b2 = read0();
			byte b3 = read0();
			byte b4 = read0();
			byte b5 = read0();
			long ret = (long)b1 & 255L | ((long)b2 & 255L) << 8 | ((long)b3 & 255L) << 16 | ((long)b4 & 255L) << 24 | ((long)b5 & 255L) << 32;
			if (b5 < 0)
				return ret | 0xffffff0000000000L;
			else
				return ret;
		}

		case 5: // '\005'
		{
			byte b1 = read0();
			byte b2 = read0();
			byte b3 = read0();
			byte b4 = read0();
			byte b5 = read0();
			byte b6 = read0();
			long ret = (long)b1 & 255L | ((long)b2 & 255L) << 8 | ((long)b3 & 255L) << 16 | ((long)b4 & 255L) << 24 | ((long)b5 & 255L) << 32 | ((long)b6 & 255L) << 40;
			if (b6 < 0)
				return ret | 0xffff000000000000L;
			else
				return ret;
		}

		case 6: // '\006'
		{
			byte b1 = read0();
			byte b2 = read0();
			byte b3 = read0();
			byte b4 = read0();
			byte b5 = read0();
			byte b6 = read0();
			byte b7 = read0();
			long ret = (long)b1 & 255L | ((long)b2 & 255L) << 8 | ((long)b3 & 255L) << 16 | ((long)b4 & 255L) << 24 | ((long)b5 & 255L) << 32 | ((long)b6 & 255L) << 40 | ((long)b7 & 255L) << 48;
			if (b7 < 0)
				return ret | 0xff00000000000000L;
			else
				return ret;
		}

		case 7: // '\007'
		{
			byte b1 = read0();
			byte b2 = read0();
			byte b3 = read0();
			byte b4 = read0();
			byte b5 = read0();
			byte b6 = read0();
			byte b7 = read0();
			byte b8 = read0();
			return (long)b1 & 255L | ((long)b2 & 255L) << 8 | ((long)b3 & 255L) << 16 | ((long)b4 & 255L) << 24 | ((long)b5 & 255L) << 32 | ((long)b6 & 255L) << 40 | ((long)b7 & 255L) << 48 | ((long)b8 & 255L) << 56;
		}

		case 10: // '\n'
		{
			return -15L;
		}

		case 11: // '\013'
		{
			return -14L;
		}

		case 12: // '\f'
		{
			return -13L;
		}

		case 13: // '\r'
		{
			return -12L;
		}

		case 14: // '\016'
		{
			return -11L;
		}

		case 15: // '\017'
		{
			return -10L;
		}

		case 16: // '\020'
		{
			return -9L;
		}

		case 17: // '\021'
		{
			return -8L;
		}

		case 18: // '\022'
		{
			return -7L;
		}

		case 19: // '\023'
		{
			return -6L;
		}

		case 20: // '\024'
		{
			return -5L;
		}

		case 21: // '\025'
		{
			return -4L;
		}

		case 22: // '\026'
		{
			return -3L;
		}

		case 23: // '\027'
		{
			return -2L;
		}

		case 24: // '\030'
		{
			return -1L;
		}

		case 25: // '\031'
		{
			return 0L;
		}

		case 26: // '\032'
		{
			return 1L;
		}

		case 27: // '\033'
		{
			return 2L;
		}

		case 28: // '\034'
		{
			return 3L;
		}

		case 29: // '\035'
		{
			return 4L;
		}

		case 30: // '\036'
		{
			return 5L;
		}

		case 31: // '\037'
		{
			return 6L;
		}

		case 32: // ' '
		{
			return 7L;
		}

		case 33: // '!'
		{
			return 8L;
		}

		case 34: // '"'
		{
			return 9L;
		}

		case 35: // '#'
		{
			return 10L;
		}

		case 36: // '$'
		{
			return 11L;
		}

		case 37: // '%'
		{
			return 12L;
		}

		case 38: // '&'
		{
			return 13L;
		}

		case 39: // '\''
		{
			return 14L;
		}

		case 40: // '('
		{
			return 15L;
		}

		case 41: // ')'
		{
			return 16L;
		}

		case 42: // '*'
		{
			return 17L;
		}

		case 43: // '+'
		{
			return 18L;
		}

		case 44: // ','
		{
			return 19L;
		}

		case 45: // '-'
		{
			return 20L;
		}

		case 46: // '.'
		{
			return 21L;
		}

		case 47: // '/'
		{
			return 22L;
		}

		case 48: // '0'
		{
			return 23L;
		}

		case 49: // '1'
		{
			return 24L;
		}

		case 50: // '2'
		{
			return 25L;
		}

		case 51: // '3'
		{
			return 26L;
		}

		case 52: // '4'
		{
			return 27L;
		}

		case 53: // '5'
		{
			return 28L;
		}

		case 54: // '6'
		{
			return 29L;
		}

		case 55: // '7'
		{
			return 30L;
		}

		case 56: // '8'
		{
			return 31L;
		}

		case 8: // '\b'
		case 9: // '\t'
		default:
		{
			throw new IOException((new StringBuilder()).append("Tag error, expect VARINT, but get ").append(b).toString());
		}
		}
	}

	private void fillBuffer()
		throws IOException
	{
		mPosition = 0;
		mRead = mInput.read(mBuffer);
		if (mRead == -1)
		{
			mRead = 0;
			throw new EOFException();
		} else
		{
			return;
		}
	}

}
