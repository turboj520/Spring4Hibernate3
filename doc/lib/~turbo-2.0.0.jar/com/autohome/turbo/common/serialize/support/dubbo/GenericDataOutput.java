// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   GenericDataOutput.java

package com.autohome.turbo.common.serialize.support.dubbo;

import com.autohome.turbo.common.serialize.DataOutput;
import java.io.IOException;
import java.io.OutputStream;

// Referenced classes of package com.autohome.turbo.common.serialize.support.dubbo:
//			GenericDataFlags

public class GenericDataOutput
	implements DataOutput, GenericDataFlags
{

	private static final int CHAR_BUF_SIZE = 256;
	private final byte mBuffer[];
	private final byte mTemp[];
	private final char mCharBuf[];
	private final OutputStream mOutput;
	private final int mLimit;
	private int mPosition;

	public GenericDataOutput(OutputStream out)
	{
		this(out, 1024);
	}

	public GenericDataOutput(OutputStream out, int buffSize)
	{
		mTemp = new byte[9];
		mCharBuf = new char[256];
		mPosition = 0;
		mOutput = out;
		mLimit = buffSize;
		mBuffer = new byte[buffSize];
	}

	public void writeBool(boolean v)
		throws IOException
	{
		write0(((byte)(v ? 26 : 25)));
	}

	public void writeByte(byte v)
		throws IOException
	{
		switch (v)
		{
		case 0: // '\0'
			write0((byte)25);
			break;

		case 1: // '\001'
			write0((byte)26);
			break;

		case 2: // '\002'
			write0((byte)27);
			break;

		case 3: // '\003'
			write0((byte)28);
			break;

		case 4: // '\004'
			write0((byte)29);
			break;

		case 5: // '\005'
			write0((byte)30);
			break;

		case 6: // '\006'
			write0((byte)31);
			break;

		case 7: // '\007'
			write0((byte)32);
			break;

		case 8: // '\b'
			write0((byte)33);
			break;

		case 9: // '\t'
			write0((byte)34);
			break;

		case 10: // '\n'
			write0((byte)35);
			break;

		case 11: // '\013'
			write0((byte)36);
			break;

		case 12: // '\f'
			write0((byte)37);
			break;

		case 13: // '\r'
			write0((byte)38);
			break;

		case 14: // '\016'
			write0((byte)39);
			break;

		case 15: // '\017'
			write0((byte)40);
			break;

		case 16: // '\020'
			write0((byte)41);
			break;

		case 17: // '\021'
			write0((byte)42);
			break;

		case 18: // '\022'
			write0((byte)43);
			break;

		case 19: // '\023'
			write0((byte)44);
			break;

		case 20: // '\024'
			write0((byte)45);
			break;

		case 21: // '\025'
			write0((byte)46);
			break;

		case 22: // '\026'
			write0((byte)47);
			break;

		case 23: // '\027'
			write0((byte)48);
			break;

		case 24: // '\030'
			write0((byte)49);
			break;

		case 25: // '\031'
			write0((byte)50);
			break;

		case 26: // '\032'
			write0((byte)51);
			break;

		case 27: // '\033'
			write0((byte)52);
			break;

		case 28: // '\034'
			write0((byte)53);
			break;

		case 29: // '\035'
			write0((byte)54);
			break;

		case 30: // '\036'
			write0((byte)55);
			break;

		case 31: // '\037'
			write0((byte)56);
			break;

		default:
			write0((byte)0);
			write0(v);
			break;
		}
	}

	public void writeShort(short v)
		throws IOException
	{
		writeVarint32(v);
	}

	public void writeInt(int v)
		throws IOException
	{
		writeVarint32(v);
	}

	public void writeLong(long v)
		throws IOException
	{
		writeVarint64(v);
	}

	public void writeFloat(float v)
		throws IOException
	{
		writeVarint32(Float.floatToRawIntBits(v));
	}

	public void writeDouble(double v)
		throws IOException
	{
		writeVarint64(Double.doubleToRawLongBits(v));
	}

	public void writeUTF(String v)
		throws IOException
	{
		if (v == null)
		{
			write0((byte)-108);
		} else
		{
			int len = v.length();
			if (len == 0)
			{
				write0((byte)-107);
			} else
			{
				write0((byte)-125);
				writeUInt(len);
				int off = 0;
				int limit = mLimit - 3;
				char buf[] = mCharBuf;
				do
				{
					int size = Math.min(len - off, 256);
					v.getChars(off, off + size, buf, 0);
					for (int i = 0; i < size; i++)
					{
						char c = buf[i];
						if (mPosition > limit)
						{
							if (c < '\200')
							{
								write0((byte)c);
								continue;
							}
							if (c < '\u0800')
							{
								write0((byte)(0xc0 | c >> 6 & 0x1f));
								write0((byte)(0x80 | c & 0x3f));
							} else
							{
								write0((byte)(0xe0 | c >> 12 & 0xf));
								write0((byte)(0x80 | c >> 6 & 0x3f));
								write0((byte)(0x80 | c & 0x3f));
							}
							continue;
						}
						if (c < '\200')
						{
							mBuffer[mPosition++] = (byte)c;
							continue;
						}
						if (c < '\u0800')
						{
							mBuffer[mPosition++] = (byte)(0xc0 | c >> 6 & 0x1f);
							mBuffer[mPosition++] = (byte)(0x80 | c & 0x3f);
						} else
						{
							mBuffer[mPosition++] = (byte)(0xe0 | c >> 12 & 0xf);
							mBuffer[mPosition++] = (byte)(0x80 | c >> 6 & 0x3f);
							mBuffer[mPosition++] = (byte)(0x80 | c & 0x3f);
						}
					}

					off += size;
				} while (off < len);
			}
		}
	}

	public void writeBytes(byte b[])
		throws IOException
	{
		if (b == null)
			write0((byte)-108);
		else
			writeBytes(b, 0, b.length);
	}

	public void writeBytes(byte b[], int off, int len)
		throws IOException
	{
		if (len == 0)
		{
			write0((byte)-107);
		} else
		{
			write0((byte)-125);
			writeUInt(len);
			write0(b, off, len);
		}
	}

	public void flushBuffer()
		throws IOException
	{
		if (mPosition > 0)
		{
			mOutput.write(mBuffer, 0, mPosition);
			mPosition = 0;
		}
	}

	public void writeUInt(int v)
		throws IOException
	{
		do
		{
			byte tmp = (byte)(v & 0x7f);
			if ((v >>>= 7) == 0)
			{
				write0((byte)(tmp | 0x80));
				return;
			}
			write0(tmp);
		} while (true);
	}

	protected void write0(byte b)
		throws IOException
	{
		if (mPosition == mLimit)
			flushBuffer();
		mBuffer[mPosition++] = b;
	}

	protected void write0(byte b[], int off, int len)
		throws IOException
	{
		int rem = mLimit - mPosition;
		if (rem > len)
		{
			System.arraycopy(b, off, mBuffer, mPosition, len);
			mPosition += len;
		} else
		{
			System.arraycopy(b, off, mBuffer, mPosition, rem);
			mPosition = mLimit;
			flushBuffer();
			off += rem;
			len -= rem;
			if (mLimit > len)
			{
				System.arraycopy(b, off, mBuffer, 0, len);
				mPosition = len;
			} else
			{
				mOutput.write(b, off, len);
			}
		}
	}

	private void writeVarint32(int v)
		throws IOException
	{
		switch (v)
		{
		case -15: 
			write0((byte)10);
			break;

		case -14: 
			write0((byte)11);
			break;

		case -13: 
			write0((byte)12);
			break;

		case -12: 
			write0((byte)13);
			break;

		case -11: 
			write0((byte)14);
			break;

		case -10: 
			write0((byte)15);
			break;

		case -9: 
			write0((byte)16);
			break;

		case -8: 
			write0((byte)17);
			break;

		case -7: 
			write0((byte)18);
			break;

		case -6: 
			write0((byte)19);
			break;

		case -5: 
			write0((byte)20);
			break;

		case -4: 
			write0((byte)21);
			break;

		case -3: 
			write0((byte)22);
			break;

		case -2: 
			write0((byte)23);
			break;

		case -1: 
			write0((byte)24);
			break;

		case 0: // '\0'
			write0((byte)25);
			break;

		case 1: // '\001'
			write0((byte)26);
			break;

		case 2: // '\002'
			write0((byte)27);
			break;

		case 3: // '\003'
			write0((byte)28);
			break;

		case 4: // '\004'
			write0((byte)29);
			break;

		case 5: // '\005'
			write0((byte)30);
			break;

		case 6: // '\006'
			write0((byte)31);
			break;

		case 7: // '\007'
			write0((byte)32);
			break;

		case 8: // '\b'
			write0((byte)33);
			break;

		case 9: // '\t'
			write0((byte)34);
			break;

		case 10: // '\n'
			write0((byte)35);
			break;

		case 11: // '\013'
			write0((byte)36);
			break;

		case 12: // '\f'
			write0((byte)37);
			break;

		case 13: // '\r'
			write0((byte)38);
			break;

		case 14: // '\016'
			write0((byte)39);
			break;

		case 15: // '\017'
			write0((byte)40);
			break;

		case 16: // '\020'
			write0((byte)41);
			break;

		case 17: // '\021'
			write0((byte)42);
			break;

		case 18: // '\022'
			write0((byte)43);
			break;

		case 19: // '\023'
			write0((byte)44);
			break;

		case 20: // '\024'
			write0((byte)45);
			break;

		case 21: // '\025'
			write0((byte)46);
			break;

		case 22: // '\026'
			write0((byte)47);
			break;

		case 23: // '\027'
			write0((byte)48);
			break;

		case 24: // '\030'
			write0((byte)49);
			break;

		case 25: // '\031'
			write0((byte)50);
			break;

		case 26: // '\032'
			write0((byte)51);
			break;

		case 27: // '\033'
			write0((byte)52);
			break;

		case 28: // '\034'
			write0((byte)53);
			break;

		case 29: // '\035'
			write0((byte)54);
			break;

		case 30: // '\036'
			write0((byte)55);
			break;

		case 31: // '\037'
			write0((byte)56);
			break;

		default:
			int t = v;
			int ix = 0;
			byte b[] = mTemp;
			do
				b[++ix] = (byte)(v & 0xff);
			while ((v >>>= 8) != 0);
			if (t > 0)
			{
				if (b[ix] < 0)
					b[++ix] = 0;
			} else
			{
				for (; b[ix] == -1 && b[ix - 1] < 0; ix--);
			}
			b[0] = (byte)((0 + ix) - 1);
			write0(b, 0, ix + 1);
			break;
		}
	}

	private void writeVarint64(long v)
		throws IOException
	{
		int i = (int)v;
		if (v == (long)i)
		{
			writeVarint32(i);
		} else
		{
			long t = v;
			int ix = 0;
			byte b[] = mTemp;
			do
				b[++ix] = (byte)(int)(v & 255L);
			while ((v >>>= 8) != 0L);
			if (t > 0L)
			{
				if (b[ix] < 0)
					b[++ix] = 0;
			} else
			{
				for (; b[ix] == -1 && b[ix - 1] < 0; ix--);
			}
			b[0] = (byte)((0 + ix) - 1);
			write0(b, 0, ix + 1);
		}
	}
}
