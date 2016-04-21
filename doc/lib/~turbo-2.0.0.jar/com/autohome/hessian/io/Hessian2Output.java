// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Hessian2Output.java

package com.autohome.hessian.io;

import com.autohome.hessian.util.IdentityIntMap;
import java.io.*;
import java.util.HashMap;

// Referenced classes of package com.autohome.hessian.io:
//			AbstractHessianOutput, Hessian2Constants, SerializerFactory, Serializer

public class Hessian2Output extends AbstractHessianOutput
	implements Hessian2Constants
{
	class BytesOutputStream extends OutputStream
	{

		private int _startOffset;
		final Hessian2Output this$0;

		public void write(int ch)
			throws IOException
		{
			if (4096 <= _offset)
			{
				int length = _offset - _startOffset - 3;
				_buffer[_startOffset] = 65;
				_buffer[_startOffset + 1] = (byte)(length >> 8);
				_buffer[_startOffset + 2] = (byte)length;
				flushBuffer();
				_startOffset = _offset;
				_offset+= = 3;
			}
			_buffer[_offset++] = (byte)ch;
		}

		public void write(byte buffer[], int offset, int length)
			throws IOException
		{
			do
			{
				if (length <= 0)
					break;
				int sublen = 4096 - _offset;
				if (length < sublen)
					sublen = length;
				if (sublen > 0)
				{
					System.arraycopy(buffer, offset, _buffer, _offset, sublen);
					_offset+= = sublen;
				}
				length -= sublen;
				offset += sublen;
				if (4096 <= _offset)
				{
					int chunkLength = _offset - _startOffset - 3;
					_buffer[_startOffset] = 65;
					_buffer[_startOffset + 1] = (byte)(chunkLength >> 8);
					_buffer[_startOffset + 2] = (byte)chunkLength;
					flushBuffer();
					_startOffset = _offset;
					_offset+= = 3;
				}
			} while (true);
		}

		public void close()
			throws IOException
		{
			int startOffset = _startOffset;
			_startOffset = -1;
			if (startOffset < 0)
			{
				return;
			} else
			{
				int length = _offset - startOffset - 3;
				_buffer[startOffset] = 66;
				_buffer[startOffset + 1] = (byte)(length >> 8);
				_buffer[startOffset + 2] = (byte)length;
				flushBuffer();
				return;
			}
		}

		BytesOutputStream()
			throws IOException
		{
			this$0 = Hessian2Output.this;
			super();
			if (4096 < _offset + 16)
				flushBuffer();
			_startOffset = _offset;
			_offset+= = 3;
		}
	}


	public static final int SIZE = 4096;
	protected OutputStream _os;
	private final IdentityIntMap _refs = new IdentityIntMap(256);
	private boolean _isCloseStreamOnClose;
	private final IdentityIntMap _classRefs = new IdentityIntMap(256);
	private HashMap _typeRefs;
	private final byte _buffer[] = new byte[4096];
	private int _offset;
	private boolean _isPacket;

	public Hessian2Output(OutputStream os)
	{
		init(os);
	}

	public void init(OutputStream os)
	{
		reset();
		_os = os;
	}

	public void setCloseStreamOnClose(boolean isClose)
	{
		_isCloseStreamOnClose = isClose;
	}

	public boolean isCloseStreamOnClose()
	{
		return _isCloseStreamOnClose;
	}

	public void call(String method, Object args[])
		throws IOException
	{
		writeVersion();
		int length = args == null ? 0 : args.length;
		startCall(method, length);
		for (int i = 0; i < length; i++)
			writeObject(args[i]);

		completeCall();
		flush();
	}

	public void startCall(String method, int length)
		throws IOException
	{
		int offset = _offset;
		if (4096 < offset + 32)
		{
			flushBuffer();
			offset = _offset;
		}
		byte buffer[] = _buffer;
		buffer[_offset++] = 67;
		writeString(method);
		writeInt(length);
	}

	public void startCall()
		throws IOException
	{
		flushIfFull();
		_buffer[_offset++] = 67;
	}

	public void startEnvelope(String method)
		throws IOException
	{
		int offset = _offset;
		if (4096 < offset + 32)
		{
			flushBuffer();
			offset = _offset;
		}
		_buffer[_offset++] = 69;
		writeString(method);
	}

	public void completeEnvelope()
		throws IOException
	{
		flushIfFull();
		_buffer[_offset++] = 90;
	}

	public void writeMethod(String method)
		throws IOException
	{
		writeString(method);
	}

	public void completeCall()
		throws IOException
	{
	}

	public void startReply()
		throws IOException
	{
		writeVersion();
		flushIfFull();
		_buffer[_offset++] = 82;
	}

	public void writeVersion()
		throws IOException
	{
		flushIfFull();
		_buffer[_offset++] = 72;
		_buffer[_offset++] = 2;
		_buffer[_offset++] = 0;
	}

	public void completeReply()
		throws IOException
	{
	}

	public void startMessage()
		throws IOException
	{
		flushIfFull();
		_buffer[_offset++] = 112;
		_buffer[_offset++] = 2;
		_buffer[_offset++] = 0;
	}

	public void completeMessage()
		throws IOException
	{
		flushIfFull();
		_buffer[_offset++] = 122;
	}

	public void writeFault(String code, String message, Object detail)
		throws IOException
	{
		flushIfFull();
		writeVersion();
		_buffer[_offset++] = 70;
		_buffer[_offset++] = 72;
		_refs.put(new Object(), _refs.size(), false);
		writeString("code");
		writeString(code);
		writeString("message");
		writeString(message);
		if (detail != null)
		{
			writeString("detail");
			writeObject(detail);
		}
		flushIfFull();
		_buffer[_offset++] = 90;
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
			Serializer serializer = findSerializerFactory().getObjectSerializer(object.getClass());
			serializer.writeObject(object, this);
			return;
		}
	}

	public boolean writeListBegin(int length, String type)
		throws IOException
	{
		flushIfFull();
		if (length < 0)
		{
			if (type != null)
			{
				_buffer[_offset++] = 85;
				writeType(type);
			} else
			{
				_buffer[_offset++] = 87;
			}
			return true;
		}
		if (length <= 7)
		{
			if (type != null)
			{
				_buffer[_offset++] = (byte)(112 + length);
				writeType(type);
			} else
			{
				_buffer[_offset++] = (byte)(120 + length);
			}
			return false;
		}
		if (type != null)
		{
			_buffer[_offset++] = 86;
			writeType(type);
		} else
		{
			_buffer[_offset++] = 88;
		}
		writeInt(length);
		return false;
	}

	public void writeListEnd()
		throws IOException
	{
		flushIfFull();
		_buffer[_offset++] = 90;
	}

	public void writeMapBegin(String type)
		throws IOException
	{
		if (4096 < _offset + 32)
			flushBuffer();
		if (type != null)
		{
			_buffer[_offset++] = 77;
			writeType(type);
		} else
		{
			_buffer[_offset++] = 72;
		}
	}

	public void writeMapEnd()
		throws IOException
	{
		if (4096 < _offset + 32)
			flushBuffer();
		_buffer[_offset++] = 90;
	}

	public int writeObjectBegin(String type)
		throws IOException
	{
		int newRef = _classRefs.size();
		int ref = _classRefs.put(type, newRef, false);
		if (newRef != ref)
		{
			if (4096 < _offset + 32)
				flushBuffer();
			if (ref <= 15)
			{
				_buffer[_offset++] = (byte)(96 + ref);
			} else
			{
				_buffer[_offset++] = 79;
				writeInt(ref);
			}
			return ref;
		}
		if (4096 < _offset + 32)
			flushBuffer();
		_buffer[_offset++] = 67;
		writeString(type);
		return -1;
	}

	public void writeClassFieldLength(int len)
		throws IOException
	{
		writeInt(len);
	}

	public void writeObjectEnd()
		throws IOException
	{
	}

	private void writeType(String type)
		throws IOException
	{
		flushIfFull();
		int len = type.length();
		if (len == 0)
			throw new IllegalArgumentException("empty type is not allowed");
		if (_typeRefs == null)
			_typeRefs = new HashMap();
		Integer typeRefV = (Integer)_typeRefs.get(type);
		if (typeRefV != null)
		{
			int typeRef = typeRefV.intValue();
			writeInt(typeRef);
		} else
		{
			_typeRefs.put(type, Integer.valueOf(_typeRefs.size()));
			writeString(type);
		}
	}

	public void writeBoolean(boolean value)
		throws IOException
	{
		if (4096 < _offset + 16)
			flushBuffer();
		if (value)
			_buffer[_offset++] = 84;
		else
			_buffer[_offset++] = 70;
	}

	public void writeInt(int value)
		throws IOException
	{
		int offset = _offset;
		byte buffer[] = _buffer;
		if (4096 <= offset + 16)
		{
			flushBuffer();
			offset = _offset;
		}
		if (-16 <= value && value <= 47)
			buffer[offset++] = (byte)(value + 144);
		else
		if (-2048 <= value && value <= 2047)
		{
			buffer[offset++] = (byte)(200 + (value >> 8));
			buffer[offset++] = (byte)value;
		} else
		if (0xfffc0000 <= value && value <= 0x3ffff)
		{
			buffer[offset++] = (byte)(212 + (value >> 16));
			buffer[offset++] = (byte)(value >> 8);
			buffer[offset++] = (byte)value;
		} else
		{
			buffer[offset++] = 73;
			buffer[offset++] = (byte)(value >> 24);
			buffer[offset++] = (byte)(value >> 16);
			buffer[offset++] = (byte)(value >> 8);
			buffer[offset++] = (byte)value;
		}
		_offset = offset;
	}

	public void writeLong(long value)
		throws IOException
	{
		int offset = _offset;
		byte buffer[] = _buffer;
		if (4096 <= offset + 16)
		{
			flushBuffer();
			offset = _offset;
		}
		if (-8L <= value && value <= 15L)
			buffer[offset++] = (byte)(int)(value + 224L);
		else
		if (-2048L <= value && value <= 2047L)
		{
			buffer[offset++] = (byte)(int)(248L + (value >> 8));
			buffer[offset++] = (byte)(int)value;
		} else
		if (0xfffffffffffc0000L <= value && value <= 0x3ffffL)
		{
			buffer[offset++] = (byte)(int)(60L + (value >> 16));
			buffer[offset++] = (byte)(int)(value >> 8);
			buffer[offset++] = (byte)(int)value;
		} else
		if (0xffffffff80000000L <= value && value <= 0x7fffffffL)
		{
			buffer[offset + 0] = 89;
			buffer[offset + 1] = (byte)(int)(value >> 24);
			buffer[offset + 2] = (byte)(int)(value >> 16);
			buffer[offset + 3] = (byte)(int)(value >> 8);
			buffer[offset + 4] = (byte)(int)value;
			offset += 5;
		} else
		{
			buffer[offset + 0] = 76;
			buffer[offset + 1] = (byte)(int)(value >> 56);
			buffer[offset + 2] = (byte)(int)(value >> 48);
			buffer[offset + 3] = (byte)(int)(value >> 40);
			buffer[offset + 4] = (byte)(int)(value >> 32);
			buffer[offset + 5] = (byte)(int)(value >> 24);
			buffer[offset + 6] = (byte)(int)(value >> 16);
			buffer[offset + 7] = (byte)(int)(value >> 8);
			buffer[offset + 8] = (byte)(int)value;
			offset += 9;
		}
		_offset = offset;
	}

	public void writeDouble(double value)
		throws IOException
	{
		int offset = _offset;
		byte buffer[] = _buffer;
		if (4096 <= offset + 16)
		{
			flushBuffer();
			offset = _offset;
		}
		int intValue = (int)value;
		if ((double)intValue == value)
		{
			if (intValue == 0)
			{
				buffer[offset++] = 91;
				_offset = offset;
				return;
			}
			if (intValue == 1)
			{
				buffer[offset++] = 92;
				_offset = offset;
				return;
			}
			if (-128 <= intValue && intValue < 128)
			{
				buffer[offset++] = 93;
				buffer[offset++] = (byte)intValue;
				_offset = offset;
				return;
			}
			if (-32768 <= intValue && intValue < 32768)
			{
				buffer[offset + 0] = 94;
				buffer[offset + 1] = (byte)(intValue >> 8);
				buffer[offset + 2] = (byte)intValue;
				_offset = offset + 3;
				return;
			}
		}
		int mills = (int)(value * 1000D);
		if (0.001D * (double)mills == value)
		{
			buffer[offset + 0] = 95;
			buffer[offset + 1] = (byte)(mills >> 24);
			buffer[offset + 2] = (byte)(mills >> 16);
			buffer[offset + 3] = (byte)(mills >> 8);
			buffer[offset + 4] = (byte)mills;
			_offset = offset + 5;
			return;
		} else
		{
			long bits = Double.doubleToLongBits(value);
			buffer[offset + 0] = 68;
			buffer[offset + 1] = (byte)(int)(bits >> 56);
			buffer[offset + 2] = (byte)(int)(bits >> 48);
			buffer[offset + 3] = (byte)(int)(bits >> 40);
			buffer[offset + 4] = (byte)(int)(bits >> 32);
			buffer[offset + 5] = (byte)(int)(bits >> 24);
			buffer[offset + 6] = (byte)(int)(bits >> 16);
			buffer[offset + 7] = (byte)(int)(bits >> 8);
			buffer[offset + 8] = (byte)(int)bits;
			_offset = offset + 9;
			return;
		}
	}

	public void writeUTCDate(long time)
		throws IOException
	{
		if (4096 < _offset + 32)
			flushBuffer();
		int offset = _offset;
		byte buffer[] = _buffer;
		if (time % 60000L == 0L)
		{
			long minutes = time / 60000L;
			if (minutes >> 31 == 0L || minutes >> 31 == -1L)
			{
				buffer[offset++] = 75;
				buffer[offset++] = (byte)(int)(minutes >> 24);
				buffer[offset++] = (byte)(int)(minutes >> 16);
				buffer[offset++] = (byte)(int)(minutes >> 8);
				buffer[offset++] = (byte)(int)(minutes >> 0);
				_offset = offset;
				return;
			}
		}
		buffer[offset++] = 74;
		buffer[offset++] = (byte)(int)(time >> 56);
		buffer[offset++] = (byte)(int)(time >> 48);
		buffer[offset++] = (byte)(int)(time >> 40);
		buffer[offset++] = (byte)(int)(time >> 32);
		buffer[offset++] = (byte)(int)(time >> 24);
		buffer[offset++] = (byte)(int)(time >> 16);
		buffer[offset++] = (byte)(int)(time >> 8);
		buffer[offset++] = (byte)(int)time;
		_offset = offset;
	}

	public void writeNull()
		throws IOException
	{
		int offset = _offset;
		byte buffer[] = _buffer;
		if (4096 <= offset + 16)
		{
			flushBuffer();
			offset = _offset;
		}
		buffer[offset++] = 78;
		_offset = offset;
	}

	public void writeString(String value)
		throws IOException
	{
		int offset = _offset;
		byte buffer[] = _buffer;
		if (4096 <= offset + 16)
		{
			flushBuffer();
			offset = _offset;
		}
		if (value == null)
		{
			buffer[offset++] = 78;
			_offset = offset;
		} else
		{
			int length = value.length();
			int strOffset;
			int sublen;
			for (strOffset = 0; length > 32768; strOffset += sublen)
			{
				sublen = 32768;
				offset = _offset;
				if (4096 <= offset + 16)
				{
					flushBuffer();
					offset = _offset;
				}
				char tail = value.charAt((strOffset + sublen) - 1);
				if ('\uD800' <= tail && tail <= '\uDBFF')
					sublen--;
				buffer[offset + 0] = 82;
				buffer[offset + 1] = (byte)(sublen >> 8);
				buffer[offset + 2] = (byte)sublen;
				_offset = offset + 3;
				printString(value, strOffset, sublen);
				length -= sublen;
			}

			offset = _offset;
			if (4096 <= offset + 16)
			{
				flushBuffer();
				offset = _offset;
			}
			if (length <= 31)
				buffer[offset++] = (byte)(0 + length);
			else
			if (length <= 1023)
			{
				buffer[offset++] = (byte)(48 + (length >> 8));
				buffer[offset++] = (byte)length;
			} else
			{
				buffer[offset++] = 83;
				buffer[offset++] = (byte)(length >> 8);
				buffer[offset++] = (byte)length;
			}
			_offset = offset;
			printString(value, strOffset, length);
		}
	}

	public void writeString(char buffer[], int offset, int length)
		throws IOException
	{
		if (buffer == null)
		{
			if (4096 < _offset + 16)
				flushBuffer();
			_buffer[_offset++] = 78;
		} else
		{
			while (length > 32768) 
			{
				int sublen = 32768;
				if (4096 < _offset + 16)
					flushBuffer();
				char tail = buffer[(offset + sublen) - 1];
				if ('\uD800' <= tail && tail <= '\uDBFF')
					sublen--;
				_buffer[_offset++] = 82;
				_buffer[_offset++] = (byte)(sublen >> 8);
				_buffer[_offset++] = (byte)sublen;
				printString(buffer, offset, sublen);
				length -= sublen;
				offset += sublen;
			}
			if (4096 < _offset + 16)
				flushBuffer();
			if (length <= 31)
				_buffer[_offset++] = (byte)(0 + length);
			else
			if (length <= 1023)
			{
				_buffer[_offset++] = (byte)(48 + (length >> 8));
				_buffer[_offset++] = (byte)length;
			} else
			{
				_buffer[_offset++] = 83;
				_buffer[_offset++] = (byte)(length >> 8);
				_buffer[_offset++] = (byte)length;
			}
			printString(buffer, offset, length);
		}
	}

	public void writeBytes(byte buffer[])
		throws IOException
	{
		if (buffer == null)
		{
			if (4096 < _offset + 16)
				flushBuffer();
			_buffer[_offset++] = 78;
		} else
		{
			writeBytes(buffer, 0, buffer.length);
		}
	}

	public void writeBytes(byte buffer[], int offset, int length)
		throws IOException
	{
		if (buffer == null)
		{
			if (4096 < _offset + 16)
				flushBuffer();
			_buffer[_offset++] = 78;
		} else
		{
			while (4096 - _offset - 3 < length) 
			{
				int sublen = 4096 - _offset - 3;
				if (sublen < 16)
				{
					flushBuffer();
					sublen = 4096 - _offset - 3;
					if (length < sublen)
						sublen = length;
				}
				_buffer[_offset++] = 65;
				_buffer[_offset++] = (byte)(sublen >> 8);
				_buffer[_offset++] = (byte)sublen;
				System.arraycopy(buffer, offset, _buffer, _offset, sublen);
				_offset += sublen;
				length -= sublen;
				offset += sublen;
				flushBuffer();
			}
			if (4096 < _offset + 16)
				flushBuffer();
			if (length <= 15)
				_buffer[_offset++] = (byte)(32 + length);
			else
			if (length <= 1023)
			{
				_buffer[_offset++] = (byte)(52 + (length >> 8));
				_buffer[_offset++] = (byte)length;
			} else
			{
				_buffer[_offset++] = 66;
				_buffer[_offset++] = (byte)(length >> 8);
				_buffer[_offset++] = (byte)length;
			}
			System.arraycopy(buffer, offset, _buffer, _offset, length);
			_offset += length;
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
			flushIfFull();
			int sublen = _buffer.length - _offset;
			if (length < sublen)
				sublen = length;
			_buffer[_offset++] = 65;
			_buffer[_offset++] = (byte)(sublen >> 8);
			_buffer[_offset++] = (byte)sublen;
			System.arraycopy(buffer, offset, _buffer, _offset, sublen);
			_offset += sublen;
			length -= sublen;
			offset += sublen;
		}
	}

	public void writeByteBufferEnd(byte buffer[], int offset, int length)
		throws IOException
	{
		writeBytes(buffer, offset, length);
	}

	public OutputStream getBytesOutputStream()
		throws IOException
	{
		return new BytesOutputStream();
	}

	public void writeByteStream(InputStream is)
		throws IOException
	{
		do
		{
			int len = 4096 - _offset - 3;
			if (len < 16)
			{
				flushBuffer();
				len = 4096 - _offset - 3;
			}
			len = is.read(_buffer, _offset + 3, len);
			if (len <= 0)
			{
				_buffer[_offset++] = 32;
				return;
			}
			_buffer[_offset + 0] = 65;
			_buffer[_offset + 1] = (byte)(len >> 8);
			_buffer[_offset + 2] = (byte)len;
			_offset += len + 3;
		} while (true);
	}

	protected void writeRef(int value)
		throws IOException
	{
		if (4096 < _offset + 16)
			flushBuffer();
		_buffer[_offset++] = 81;
		writeInt(value);
	}

	public boolean addRef(Object object)
		throws IOException
	{
		int newRef = _refs.size();
		int ref = _refs.put(object, newRef, false);
		if (ref != newRef)
		{
			writeRef(ref);
			return true;
		} else
		{
			return false;
		}
	}

	public int getRef(Object obj)
	{
		return _refs.get(obj);
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
		int value = _refs.get(oldRef);
		if (value >= 0)
		{
			_refs.put(newRef, value, true);
			_refs.remove(oldRef);
			return true;
		} else
		{
			return false;
		}
	}

	public void writeStreamingObject(Object obj)
		throws IOException
	{
		startPacket();
		writeObject(obj);
		endPacket();
	}

	public void startPacket()
		throws IOException
	{
		if (_refs != null)
			_refs.clear();
		flushBuffer();
		_isPacket = true;
		_offset = 3;
		_buffer[0] = 85;
		_buffer[1] = 85;
		_buffer[2] = 85;
	}

	public void endPacket()
		throws IOException
	{
		int offset = _offset;
		OutputStream os = _os;
		if (os == null)
		{
			_offset = 0;
			return;
		}
		int len = offset - 3;
		_buffer[0] = -128;
		_buffer[1] = (byte)(128 + (len >> 7 & 0x7f));
		_buffer[2] = (byte)(len & 0x7f);
		_buffer[offset++] = -128;
		_buffer[offset++] = 0;
		_isPacket = false;
		_offset = 0;
		if (os != null)
			if (len == 0)
				os.write(_buffer, 1, 2);
			else
			if (len < 128)
				os.write(_buffer, 1, offset - 1);
			else
				os.write(_buffer, 0, offset);
	}

	public void printLenString(String v)
		throws IOException
	{
		if (4096 < _offset + 16)
			flushBuffer();
		if (v == null)
		{
			_buffer[_offset++] = 0;
			_buffer[_offset++] = 0;
		} else
		{
			int len = v.length();
			_buffer[_offset++] = (byte)(len >> 8);
			_buffer[_offset++] = (byte)len;
			printString(v, 0, len);
		}
	}

	public void printString(String v)
		throws IOException
	{
		printString(v, 0, v.length());
	}

	public void printString(String v, int strOffset, int length)
		throws IOException
	{
		int offset = _offset;
		byte buffer[] = _buffer;
		for (int i = 0; i < length; i++)
		{
			if (4096 <= offset + 16)
			{
				_offset = offset;
				flushBuffer();
				offset = _offset;
			}
			char ch = v.charAt(i + strOffset);
			if (ch < '\200')
			{
				buffer[offset++] = (byte)ch;
				continue;
			}
			if (ch < '\u0800')
			{
				buffer[offset++] = (byte)(192 + (ch >> 6 & 0x1f));
				buffer[offset++] = (byte)(128 + (ch & 0x3f));
			} else
			{
				buffer[offset++] = (byte)(224 + (ch >> 12 & 0xf));
				buffer[offset++] = (byte)(128 + (ch >> 6 & 0x3f));
				buffer[offset++] = (byte)(128 + (ch & 0x3f));
			}
		}

		_offset = offset;
	}

	public void printString(char v[], int strOffset, int length)
		throws IOException
	{
		int offset = _offset;
		byte buffer[] = _buffer;
		for (int i = 0; i < length; i++)
		{
			if (4096 <= offset + 16)
			{
				_offset = offset;
				flushBuffer();
				offset = _offset;
			}
			char ch = v[i + strOffset];
			if (ch < '\200')
			{
				buffer[offset++] = (byte)ch;
				continue;
			}
			if (ch < '\u0800')
			{
				buffer[offset++] = (byte)(192 + (ch >> 6 & 0x1f));
				buffer[offset++] = (byte)(128 + (ch & 0x3f));
			} else
			{
				buffer[offset++] = (byte)(224 + (ch >> 12 & 0xf));
				buffer[offset++] = (byte)(128 + (ch >> 6 & 0x3f));
				buffer[offset++] = (byte)(128 + (ch & 0x3f));
			}
		}

		_offset = offset;
	}

	private final void flushIfFull()
		throws IOException
	{
		int offset = _offset;
		if (4096 < offset + 32)
			flushBuffer();
	}

	public final void flush()
		throws IOException
	{
		flushBuffer();
		if (_os != null)
			_os.flush();
	}

	public final void flushBuffer()
		throws IOException
	{
		int offset = _offset;
		OutputStream os = _os;
		if (!_isPacket && offset > 0)
		{
			_offset = 0;
			if (os != null)
				os.write(_buffer, 0, offset);
		} else
		if (_isPacket && offset > 3)
		{
			int len = offset - 3;
			_buffer[0] = -128;
			_buffer[1] = (byte)(128 + (len >> 7 & 0x7f));
			_buffer[2] = (byte)(len & 0x7f);
			_offset = 3;
			if (os != null)
				os.write(_buffer, 0, offset);
			_buffer[0] = 86;
			_buffer[1] = 86;
			_buffer[2] = 86;
		}
	}

	public void close()
		throws IOException
	{
		flush();
		OutputStream os = _os;
		_os = null;
		if (os != null && _isCloseStreamOnClose)
			os.close();
	}

	public void free()
	{
		reset();
		_os = null;
		_isCloseStreamOnClose = false;
	}

	public void resetReferences()
	{
		if (_refs != null)
			_refs.clear();
	}

	public void reset()
	{
		if (_refs != null)
			_refs.clear();
		_classRefs.clear();
		_typeRefs = null;
		_offset = 0;
		_isPacket = false;
	}




}
