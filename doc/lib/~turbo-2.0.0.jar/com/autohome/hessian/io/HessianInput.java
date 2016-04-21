// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HessianInput.java

package com.autohome.hessian.io;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import org.w3c.dom.Node;

// Referenced classes of package com.autohome.hessian.io:
//			AbstractHessianInput, SerializerFactory, HessianServiceException, HessianRemote, 
//			HessianProtocolException, Deserializer, HessianRemoteResolver

public class HessianInput extends AbstractHessianInput
{

	private static int END_OF_DATA = -2;
	private static Field _detailMessageField;
	protected SerializerFactory _serializerFactory;
	protected ArrayList _refs;
	private InputStream _is;
	protected int _peek;
	private String _method;
	private Reader _chunkReader;
	private InputStream _chunkInputStream;
	private Throwable _replyFault;
	private StringBuffer _sbuf;
	private boolean _isLastChunk;
	private int _chunkLength;

	public HessianInput()
	{
		_peek = -1;
		_sbuf = new StringBuffer();
	}

	public HessianInput(InputStream is)
	{
		_peek = -1;
		_sbuf = new StringBuffer();
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
		_isLastChunk = true;
		_chunkLength = 0;
		_peek = -1;
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

	public int readCall()
		throws IOException
	{
		int tag = read();
		if (tag != 99)
		{
			throw error((new StringBuilder()).append("expected hessian call ('c') at ").append(codeName(tag)).toString());
		} else
		{
			int major = read();
			int minor = read();
			return (major << 16) + minor;
		}
	}

	public void skipOptionalCall()
		throws IOException
	{
		int tag = read();
		if (tag == 99)
		{
			read();
			read();
		} else
		{
			_peek = tag;
		}
	}

	public String readMethod()
		throws IOException
	{
		int tag = read();
		if (tag != 109)
			throw error((new StringBuilder()).append("expected hessian method ('m') at ").append(codeName(tag)).toString());
		int d1 = read();
		int d2 = read();
		_isLastChunk = true;
		_chunkLength = d1 * 256 + d2;
		_sbuf.setLength(0);
		int ch;
		while ((ch = parseChar()) >= 0) 
			_sbuf.append((char)ch);
		_method = _sbuf.toString();
		return _method;
	}

	public void startCall()
		throws IOException
	{
		readCall();
		for (; readHeader() != null; readObject());
		readMethod();
	}

	public void completeCall()
		throws IOException
	{
		int tag = read();
		if (tag != 122)
			throw error((new StringBuilder()).append("expected end of call ('z') at ").append(codeName(tag)).append(".  Check method arguments and ensure method overloading is enabled if necessary").toString());
		else
			return;
	}

	public Object readReply(Class expectedClass)
		throws Throwable
	{
		int tag = read();
		if (tag != 114)
			error((new StringBuilder()).append("expected hessian reply at ").append(codeName(tag)).toString());
		int major = read();
		int minor = read();
		tag = read();
		if (tag == 102)
		{
			throw prepareFault();
		} else
		{
			_peek = tag;
			Object value = readObject(expectedClass);
			completeValueReply();
			return value;
		}
	}

	public void startReply()
		throws Throwable
	{
		int tag = read();
		if (tag != 114)
			error((new StringBuilder()).append("expected hessian reply at ").append(codeName(tag)).toString());
		int major = read();
		int minor = read();
		startReplyBody();
	}

	public void startReplyBody()
		throws Throwable
	{
		int tag = read();
		if (tag == 102)
		{
			throw prepareFault();
		} else
		{
			_peek = tag;
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
			_replyFault = new HessianServiceException(message, code, detail);
			return _replyFault;
		}
	}

	public void completeReply()
		throws IOException
	{
		int tag = read();
		if (tag != 122)
			error((new StringBuilder()).append("expected end of reply at ").append(codeName(tag)).toString());
	}

	public void completeValueReply()
		throws IOException
	{
		int tag = read();
		if (tag != 122)
			error((new StringBuilder()).append("expected end of reply at ").append(codeName(tag)).toString());
	}

	public String readHeader()
		throws IOException
	{
		int tag = read();
		if (tag == 72)
		{
			_isLastChunk = true;
			_chunkLength = (read() << 8) + read();
			_sbuf.setLength(0);
			int ch;
			while ((ch = parseChar()) >= 0) 
				_sbuf.append((char)ch);
			return _sbuf.toString();
		} else
		{
			_peek = tag;
			return null;
		}
	}

	public void readNull()
		throws IOException
	{
		int tag = read();
		switch (tag)
		{
		case 78: // 'N'
			return;
		}
		throw expect("null", tag);
	}

	public boolean readBoolean()
		throws IOException
	{
		int tag = read();
		switch (tag)
		{
		case 84: // 'T'
			return true;

		case 70: // 'F'
			return false;

		case 73: // 'I'
			return parseInt() == 0;

		case 76: // 'L'
			return parseLong() == 0L;

		case 68: // 'D'
			return parseDouble() == 0.0D;

		case 78: // 'N'
			return false;

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
		case 83: // 'S'
		default:
			throw expect("boolean", tag);
		}
	}

	public short readShort()
		throws IOException
	{
		return (short)readInt();
	}

	public int readInt()
		throws IOException
	{
		int tag = read();
		switch (tag)
		{
		case 84: // 'T'
			return 1;

		case 70: // 'F'
			return 0;

		case 73: // 'I'
			return parseInt();

		case 76: // 'L'
			return (int)parseLong();

		case 68: // 'D'
			return (int)parseDouble();
		}
		throw expect("int", tag);
	}

	public long readLong()
		throws IOException
	{
		int tag = read();
		switch (tag)
		{
		case 84: // 'T'
			return 1L;

		case 70: // 'F'
			return 0L;

		case 73: // 'I'
			return (long)parseInt();

		case 76: // 'L'
			return parseLong();

		case 68: // 'D'
			return (long)parseDouble();
		}
		throw expect("long", tag);
	}

	public float readFloat()
		throws IOException
	{
		return (float)readDouble();
	}

	public double readDouble()
		throws IOException
	{
		int tag = read();
		switch (tag)
		{
		case 84: // 'T'
			return 1.0D;

		case 70: // 'F'
			return 0.0D;

		case 73: // 'I'
			return (double)parseInt();

		case 76: // 'L'
			return (double)parseLong();

		case 68: // 'D'
			return parseDouble();
		}
		throw expect("long", tag);
	}

	public long readUTCDate()
		throws IOException
	{
		int tag = read();
		if (tag != 100)
		{
			throw error((new StringBuilder()).append("expected date at ").append(codeName(tag)).toString());
		} else
		{
			long b64 = read();
			long b56 = read();
			long b48 = read();
			long b40 = read();
			long b32 = read();
			long b24 = read();
			long b16 = read();
			long b8 = read();
			return (b64 << 56) + (b56 << 48) + (b48 << 40) + (b40 << 32) + (b32 << 24) + (b24 << 16) + (b16 << 8) + b8;
		}
	}

	public int readChar()
		throws IOException
	{
		if (_chunkLength > 0)
		{
			_chunkLength--;
			if (_chunkLength == 0 && _isLastChunk)
				_chunkLength = END_OF_DATA;
			int ch = parseUTF8Char();
			return ch;
		}
		if (_chunkLength == END_OF_DATA)
		{
			_chunkLength = 0;
			return -1;
		}
		int tag = read();
		switch (tag)
		{
		case 78: // 'N'
			return -1;

		case 83: // 'S'
		case 88: // 'X'
		case 115: // 's'
		case 120: // 'x'
			_isLastChunk = tag == 83 || tag == 88;
			_chunkLength = (read() << 8) + read();
			_chunkLength--;
			int value = parseUTF8Char();
			if (_chunkLength == 0 && _isLastChunk)
				_chunkLength = END_OF_DATA;
			return value;
		}
		throw new IOException((new StringBuilder()).append("expected 'S' at ").append((char)tag).toString());
	}

	public int readString(char buffer[], int offset, int length)
		throws IOException
	{
		int readLength = 0;
		if (_chunkLength == END_OF_DATA)
		{
			_chunkLength = 0;
			return -1;
		}
		if (_chunkLength == 0)
		{
			int tag = read();
			switch (tag)
			{
			case 78: // 'N'
				return -1;

			case 83: // 'S'
			case 88: // 'X'
			case 115: // 's'
			case 120: // 'x'
				_isLastChunk = tag == 83 || tag == 88;
				_chunkLength = (read() << 8) + read();
				break;

			default:
				throw new IOException((new StringBuilder()).append("expected 'S' at ").append((char)tag).toString());
			}
		}
label0:
		do
		{
			do
			{
				if (length <= 0)
					break label0;
				if (_chunkLength <= 0)
					break;
				buffer[offset++] = (char)parseUTF8Char();
				_chunkLength--;
				length--;
				readLength++;
			} while (true);
			if (_isLastChunk)
				if (readLength == 0)
				{
					return -1;
				} else
				{
					_chunkLength = END_OF_DATA;
					return readLength;
				}
			int tag = read();
			switch (tag)
			{
			case 83: // 'S'
			case 88: // 'X'
			case 115: // 's'
			case 120: // 'x'
				_isLastChunk = tag == 83 || tag == 88;
				_chunkLength = (read() << 8) + read();
				break;

			default:
				throw new IOException((new StringBuilder()).append("expected 'S' at ").append((char)tag).toString());
			}
		} while (true);
		if (readLength == 0)
			return -1;
		if (_chunkLength > 0 || !_isLastChunk)
		{
			return readLength;
		} else
		{
			_chunkLength = END_OF_DATA;
			return readLength;
		}
	}

	public String readString()
		throws IOException
	{
		int tag = read();
		switch (tag)
		{
		case 78: // 'N'
			return null;

		case 73: // 'I'
			return String.valueOf(parseInt());

		case 76: // 'L'
			return String.valueOf(parseLong());

		case 68: // 'D'
			return String.valueOf(parseDouble());

		case 83: // 'S'
		case 88: // 'X'
		case 115: // 's'
		case 120: // 'x'
			_isLastChunk = tag == 83 || tag == 88;
			_chunkLength = (read() << 8) + read();
			_sbuf.setLength(0);
			int ch;
			while ((ch = parseChar()) >= 0) 
				_sbuf.append((char)ch);
			return _sbuf.toString();
		}
		throw expect("string", tag);
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
			_isLastChunk = tag == 83 || tag == 88;
			_chunkLength = (read() << 8) + read();
			throw error("Can't handle string in this context");
		}
		throw expect("string", tag);
	}

	public byte[] readBytes()
		throws IOException
	{
		int tag = read();
		switch (tag)
		{
		case 78: // 'N'
			return null;

		case 66: // 'B'
		case 98: // 'b'
			_isLastChunk = tag == 66;
			_chunkLength = (read() << 8) + read();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int data;
			while ((data = parseByte()) >= 0) 
				bos.write(data);
			return bos.toByteArray();
		}
		throw expect("bytes", tag);
	}

	public int readByte()
		throws IOException
	{
		if (_chunkLength > 0)
		{
			_chunkLength--;
			if (_chunkLength == 0 && _isLastChunk)
				_chunkLength = END_OF_DATA;
			return read();
		}
		if (_chunkLength == END_OF_DATA)
		{
			_chunkLength = 0;
			return -1;
		}
		int tag = read();
		switch (tag)
		{
		case 78: // 'N'
			return -1;

		case 66: // 'B'
		case 98: // 'b'
			_isLastChunk = tag == 66;
			_chunkLength = (read() << 8) + read();
			int value = parseByte();
			if (_chunkLength == 0 && _isLastChunk)
				_chunkLength = END_OF_DATA;
			return value;
		}
		throw new IOException((new StringBuilder()).append("expected 'B' at ").append((char)tag).toString());
	}

	public int readBytes(byte buffer[], int offset, int length)
		throws IOException
	{
		int readLength = 0;
		if (_chunkLength == END_OF_DATA)
		{
			_chunkLength = 0;
			return -1;
		}
		if (_chunkLength == 0)
		{
			int tag = read();
			switch (tag)
			{
			case 78: // 'N'
				return -1;

			case 66: // 'B'
			case 98: // 'b'
				_isLastChunk = tag == 66;
				_chunkLength = (read() << 8) + read();
				break;

			default:
				throw new IOException((new StringBuilder()).append("expected 'B' at ").append((char)tag).toString());
			}
		}
label0:
		do
		{
			do
			{
				if (length <= 0)
					break label0;
				if (_chunkLength <= 0)
					break;
				buffer[offset++] = (byte)read();
				_chunkLength--;
				length--;
				readLength++;
			} while (true);
			if (_isLastChunk)
				if (readLength == 0)
				{
					return -1;
				} else
				{
					_chunkLength = END_OF_DATA;
					return readLength;
				}
			int tag = read();
			switch (tag)
			{
			case 66: // 'B'
			case 98: // 'b'
				_isLastChunk = tag == 66;
				_chunkLength = (read() << 8) + read();
				break;

			default:
				throw new IOException((new StringBuilder()).append("expected 'B' at ").append((char)tag).toString());
			}
		} while (true);
		if (readLength == 0)
			return -1;
		if (_chunkLength > 0 || !_isLastChunk)
		{
			return readLength;
		} else
		{
			_chunkLength = END_OF_DATA;
			return readLength;
		}
	}

	private HashMap readFault()
		throws IOException
	{
		HashMap map = new HashMap();
		int code;
		for (code = read(); code > 0 && code != 122; code = read())
		{
			_peek = code;
			Object key = readObject();
			Object value = readObject();
			if (key != null && value != null)
				map.put(key, value);
		}

		if (code != 122)
			throw expect("fault", code);
		else
			return map;
	}

	public Object readObject(Class cl)
		throws IOException
	{
		if (cl == null || cl == java/lang/Object)
			return readObject();
		int tag = read();
		switch (tag)
		{
		case 78: // 'N'
		{
			return null;
		}

		case 77: // 'M'
		{
			String type = readType();
			if ("".equals(type))
			{
				Deserializer reader = _serializerFactory.getDeserializer(cl);
				return reader.readMap(this);
			} else
			{
				Deserializer reader = _serializerFactory.getObjectDeserializer(type);
				return reader.readMap(this);
			}
		}

		case 86: // 'V'
		{
			String type = readType();
			int length = readLength();
			Deserializer reader = _serializerFactory.getObjectDeserializer(type);
			if (cl != reader.getType() && cl.isAssignableFrom(reader.getType()))
			{
				return reader.readList(this, length);
			} else
			{
				reader = _serializerFactory.getDeserializer(cl);
				Object v = reader.readList(this, length);
				return v;
			}
		}

		case 82: // 'R'
		{
			int ref = parseInt();
			return _refs.get(ref);
		}

		case 114: // 'r'
		{
			String type = readType();
			String url = readString();
			return resolveRemote(type, url);
		}
		}
		_peek = tag;
		Object value = _serializerFactory.getDeserializer(cl).readObject(this);
		return value;
	}

	public Object readObject()
		throws IOException
	{
		int tag = read();
		switch (tag)
		{
		case 78: // 'N'
		{
			return null;
		}

		case 84: // 'T'
		{
			return Boolean.valueOf(true);
		}

		case 70: // 'F'
		{
			return Boolean.valueOf(false);
		}

		case 73: // 'I'
		{
			return Integer.valueOf(parseInt());
		}

		case 76: // 'L'
		{
			return Long.valueOf(parseLong());
		}

		case 68: // 'D'
		{
			return Double.valueOf(parseDouble());
		}

		case 100: // 'd'
		{
			return new Date(parseLong());
		}

		case 88: // 'X'
		case 120: // 'x'
		{
			_isLastChunk = tag == 88;
			_chunkLength = (read() << 8) + read();
			return parseXML();
		}

		case 83: // 'S'
		case 115: // 's'
		{
			_isLastChunk = tag == 83;
			_chunkLength = (read() << 8) + read();
			_sbuf.setLength(0);
			int data;
			while ((data = parseChar()) >= 0) 
				_sbuf.append((char)data);
			return _sbuf.toString();
		}

		case 66: // 'B'
		case 98: // 'b'
		{
			_isLastChunk = tag == 66;
			_chunkLength = (read() << 8) + read();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int data;
			while ((data = parseByte()) >= 0) 
				bos.write(data);
			return bos.toByteArray();
		}

		case 86: // 'V'
		{
			String type = readType();
			int length = readLength();
			return _serializerFactory.readList(this, length, type);
		}

		case 77: // 'M'
		{
			String type = readType();
			return _serializerFactory.readMap(this, type);
		}

		case 82: // 'R'
		{
			int ref = parseInt();
			return _refs.get(ref);
		}

		case 114: // 'r'
		{
			String type = readType();
			String url = readString();
			return resolveRemote(type, url);
		}

		case 67: // 'C'
		case 69: // 'E'
		case 71: // 'G'
		case 72: // 'H'
		case 74: // 'J'
		case 75: // 'K'
		case 79: // 'O'
		case 80: // 'P'
		case 81: // 'Q'
		case 85: // 'U'
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
		case 99: // 'c'
		case 101: // 'e'
		case 102: // 'f'
		case 103: // 'g'
		case 104: // 'h'
		case 105: // 'i'
		case 106: // 'j'
		case 107: // 'k'
		case 108: // 'l'
		case 109: // 'm'
		case 110: // 'n'
		case 111: // 'o'
		case 112: // 'p'
		case 113: // 'q'
		case 116: // 't'
		case 117: // 'u'
		case 118: // 'v'
		case 119: // 'w'
		default:
		{
			throw error((new StringBuilder()).append("unknown code for readObject at ").append(codeName(tag)).toString());
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
		return read();
	}

	public int readMapStart()
		throws IOException
	{
		return read();
	}

	public boolean isEnd()
		throws IOException
	{
		int code = read();
		_peek = code;
		return code < 0 || code == 122;
	}

	public void readEnd()
		throws IOException
	{
		int code = read();
		if (code != 122)
			throw error((new StringBuilder()).append("unknown code at ").append(codeName(code)).toString());
		else
			return;
	}

	public void readMapEnd()
		throws IOException
	{
		int code = read();
		if (code != 122)
			throw error((new StringBuilder()).append("expected end of map ('z') at ").append(codeName(code)).toString());
		else
			return;
	}

	public void readListEnd()
		throws IOException
	{
		int code = read();
		if (code != 122)
			throw error((new StringBuilder()).append("expected end of list ('z') at ").append(codeName(code)).toString());
		else
			return;
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

	public void resetReferences()
	{
		if (_refs != null)
			_refs.clear();
	}

	public Object resolveRemote(String type, String url)
		throws IOException
	{
		HessianRemoteResolver resolver = getRemoteResolver();
		if (resolver != null)
			return resolver.lookup(type, url);
		else
			return new HessianRemote(type, url);
	}

	public String readType()
		throws IOException
	{
		int code = read();
		if (code != 116)
		{
			_peek = code;
			return "";
		}
		_isLastChunk = true;
		_chunkLength = (read() << 8) + read();
		_sbuf.setLength(0);
		int ch;
		while ((ch = parseChar()) >= 0) 
			_sbuf.append((char)ch);
		return _sbuf.toString();
	}

	public int readLength()
		throws IOException
	{
		int code = read();
		if (code != 108)
		{
			_peek = code;
			return -1;
		} else
		{
			return parseInt();
		}
	}

	private int parseInt()
		throws IOException
	{
		int b32 = read();
		int b24 = read();
		int b16 = read();
		int b8 = read();
		return (b32 << 24) + (b24 << 16) + (b16 << 8) + b8;
	}

	private long parseLong()
		throws IOException
	{
		long b64 = read();
		long b56 = read();
		long b48 = read();
		long b40 = read();
		long b32 = read();
		long b24 = read();
		long b16 = read();
		long b8 = read();
		return (b64 << 56) + (b56 << 48) + (b48 << 40) + (b40 << 32) + (b32 << 24) + (b24 << 16) + (b16 << 8) + b8;
	}

	private double parseDouble()
		throws IOException
	{
		long b64 = read();
		long b56 = read();
		long b48 = read();
		long b40 = read();
		long b32 = read();
		long b24 = read();
		long b16 = read();
		long b8 = read();
		long bits = (b64 << 56) + (b56 << 48) + (b48 << 40) + (b40 << 32) + (b32 << 24) + (b24 << 16) + (b16 << 8) + b8;
		return Double.longBitsToDouble(bits);
	}

	Node parseXML()
		throws IOException
	{
		throw new UnsupportedOperationException();
	}

	private int parseChar()
		throws IOException
	{
		do
		{
			if (_chunkLength > 0)
				break;
			if (_isLastChunk)
				return -1;
			int code = read();
			switch (code)
			{
			case 115: // 's'
			case 120: // 'x'
				_isLastChunk = false;
				_chunkLength = (read() << 8) + read();
				break;

			case 83: // 'S'
			case 88: // 'X'
				_isLastChunk = true;
				_chunkLength = (read() << 8) + read();
				break;

			default:
				throw expect("string", code);
			}
		} while (true);
		_chunkLength--;
		return parseUTF8Char();
	}

	private int parseUTF8Char()
		throws IOException
	{
		int ch = read();
		if (ch < 128)
			return ch;
		if ((ch & 0xe0) == 192)
		{
			int ch1 = read();
			int v = ((ch & 0x1f) << 6) + (ch1 & 0x3f);
			return v;
		}
		if ((ch & 0xf0) == 224)
		{
			int ch1 = read();
			int ch2 = read();
			int v = ((ch & 0xf) << 12) + ((ch1 & 0x3f) << 6) + (ch2 & 0x3f);
			return v;
		} else
		{
			throw error((new StringBuilder()).append("bad utf-8 encoding at ").append(codeName(ch)).toString());
		}
	}

	private int parseByte()
		throws IOException
	{
		do
		{
			if (_chunkLength > 0)
				break;
			if (_isLastChunk)
				return -1;
			int code = read();
			switch (code)
			{
			case 98: // 'b'
				_isLastChunk = false;
				_chunkLength = (read() << 8) + read();
				break;

			case 66: // 'B'
				_isLastChunk = true;
				_chunkLength = (read() << 8) + read();
				break;

			default:
				throw expect("byte[]", code);
			}
		} while (true);
		_chunkLength--;
		return read();
	}

	public InputStream readInputStream()
		throws IOException
	{
		int tag = read();
		switch (tag)
		{
		case 78: // 'N'
			return null;

		case 66: // 'B'
		case 98: // 'b'
			_isLastChunk = tag == 66;
			_chunkLength = (read() << 8) + read();
			break;

		default:
			throw expect("inputStream", tag);
		}
		return new InputStream() {

			boolean _isClosed;
			final HessianInput this$0;

			public int read()
				throws IOException
			{
				if (_isClosed || _is == null)
					return -1;
				int ch = parseByte();
				if (ch < 0)
					_isClosed = true;
				return ch;
			}

			public int read(byte buffer[], int offset, int length)
				throws IOException
			{
				if (_isClosed || _is == null)
					return -1;
				int len = HessianInput.this.read(buffer, offset, length);
				if (len < 0)
					_isClosed = true;
				return len;
			}

			public void close()
				throws IOException
			{
				while (read() >= 0) ;
				_isClosed = true;
			}

			
			{
				this$0 = HessianInput.this;
				super();
				_isClosed = false;
			}
		};
	}

	int read(byte buffer[], int offset, int length)
		throws IOException
	{
		int readLength = 0;
		while (length > 0) 
		{
			do
			{
				if (_chunkLength > 0)
					break;
				if (_isLastChunk)
					return readLength != 0 ? readLength : -1;
				int code = read();
				switch (code)
				{
				case 98: // 'b'
					_isLastChunk = false;
					_chunkLength = (read() << 8) + read();
					break;

				case 66: // 'B'
					_isLastChunk = true;
					_chunkLength = (read() << 8) + read();
					break;

				default:
					throw expect("byte[]", code);
				}
			} while (true);
			int sublen = _chunkLength;
			if (length < sublen)
				sublen = length;
			sublen = _is.read(buffer, offset, sublen);
			offset += sublen;
			readLength += sublen;
			length -= sublen;
			_chunkLength -= sublen;
		}
		return readLength;
	}

	final int read()
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

	public void close()
	{
		_is = null;
	}

	public Reader getReader()
	{
		return null;
	}

	protected IOException expect(String expect, int ch)
	{
		return error((new StringBuilder()).append("expected ").append(expect).append(" at ").append(codeName(ch)).toString());
	}

	protected String codeName(int ch)
	{
		if (ch < 0)
			return "end of file";
		else
			return (new StringBuilder()).append("0x").append(Integer.toHexString(ch & 0xff)).append(" (").append((char)ch).append(")").toString();
	}

	protected IOException error(String message)
	{
		if (_method != null)
			return new HessianProtocolException((new StringBuilder()).append(_method).append(": ").append(message).toString());
		else
			return new HessianProtocolException(message);
	}

	static 
	{
		try
		{
			_detailMessageField = java/lang/Throwable.getDeclaredField("detailMessage");
			_detailMessageField.setAccessible(true);
		}
		catch (Throwable e) { }
	}


}
