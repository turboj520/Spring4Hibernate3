// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Hessian2Input.java

package com.autohome.hessian.io;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Node;

// Referenced classes of package com.autohome.hessian.io:
//			AbstractHessianInput, SerializerFactory, HessianServiceException, HessianProtocolException, 
//			HessianRemote, Hessian2Constants, Deserializer, HessianRemoteResolver

public class Hessian2Input extends AbstractHessianInput
	implements Hessian2Constants
{
	static final class ObjectDefinition
	{

		private final String _type;
		private final Deserializer _reader;
		private final Object _fields[];
		private final String _fieldNames[];

		String getType()
		{
			return _type;
		}

		Deserializer getReader()
		{
			return _reader;
		}

		Object[] getFields()
		{
			return _fields;
		}

		String[] getFieldNames()
		{
			return _fieldNames;
		}

		ObjectDefinition(String type, Deserializer reader, Object fields[], String fieldNames[])
		{
			_type = type;
			_reader = reader;
			_fields = fields;
			_fieldNames = fieldNames;
		}
	}

	class ReadInputStream extends InputStream
	{

		boolean _isClosed;
		final Hessian2Input this$0;

		public int read()
			throws IOException
		{
			if (_isClosed)
				return -1;
			int ch = parseByte();
			if (ch < 0)
				_isClosed = true;
			return ch;
		}

		public int read(byte buffer[], int offset, int length)
			throws IOException
		{
			if (_isClosed)
				return -1;
			int len = Hessian2Input.this.read(buffer, offset, length);
			if (len < 0)
				_isClosed = true;
			return len;
		}

		public void close()
			throws IOException
		{
			while (read() >= 0) ;
		}

		ReadInputStream()
		{
			this$0 = Hessian2Input.this;
			super();
			_isClosed = false;
		}
	}


	private static final Logger log = Logger.getLogger(com/autohome/hessian/io/Hessian2Input.getName());
	private static final int END_OF_DATA = -2;
	private static Field _detailMessageField;
	private static final int SIZE = 256;
	private static final int GAP = 16;
	protected SerializerFactory _defaultSerializerFactory;
	protected SerializerFactory _serializerFactory;
	private static boolean _isCloseStreamOnClose;
	protected ArrayList _refs;
	protected ArrayList _classDefs;
	protected ArrayList _types;
	private InputStream _is;
	private final byte _buffer[] = new byte[256];
	private int _offset;
	private int _length;
	private String _method;
	private Throwable _replyFault;
	private StringBuffer _sbuf;
	private boolean _isLastChunk;
	private int _chunkLength;

	public Hessian2Input(InputStream is)
	{
		_refs = new ArrayList();
		_classDefs = new ArrayList();
		_types = new ArrayList();
		_sbuf = new StringBuffer();
		_is = is;
	}

	public void setSerializerFactory(SerializerFactory factory)
	{
		_serializerFactory = factory;
	}

	public SerializerFactory getSerializerFactory()
	{
		if (_serializerFactory == _defaultSerializerFactory)
			_serializerFactory = new SerializerFactory();
		return _serializerFactory;
	}

	protected final SerializerFactory findSerializerFactory()
	{
		SerializerFactory factory = _serializerFactory;
		if (factory == null)
		{
			factory = SerializerFactory.createDefault();
			_defaultSerializerFactory = factory;
			_serializerFactory = factory;
		}
		return factory;
	}

	public void setCloseStreamOnClose(boolean isClose)
	{
		_isCloseStreamOnClose = isClose;
	}

	public boolean isCloseStreamOnClose()
	{
		return _isCloseStreamOnClose;
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
		if (tag != 67)
			throw error((new StringBuilder()).append("expected hessian call ('C') at ").append(codeName(tag)).toString());
		else
			return 0;
	}

	public int readEnvelope()
		throws IOException
	{
		int tag = read();
		int version = 0;
		if (tag == 72)
		{
			int major = read();
			int minor = read();
			version = (major << 16) + minor;
			tag = read();
		}
		if (tag != 69)
			throw error((new StringBuilder()).append("expected hessian Envelope ('E') at ").append(codeName(tag)).toString());
		else
			return version;
	}

	public void completeEnvelope()
		throws IOException
	{
		int tag = read();
		if (tag != 90)
			error((new StringBuilder()).append("expected end of envelope at ").append(codeName(tag)).toString());
	}

	public String readMethod()
		throws IOException
	{
		_method = readString();
		return _method;
	}

	public int readMethodArgLength()
		throws IOException
	{
		return readInt();
	}

	public void startCall()
		throws IOException
	{
		readCall();
		readMethod();
	}

	public Object[] readArguments()
		throws IOException
	{
		int len = readInt();
		Object args[] = new Object[len];
		for (int i = 0; i < len; i++)
			args[i] = readObject();

		return args;
	}

	public void completeCall()
		throws IOException
	{
	}

	public Object readReply(Class expectedClass)
		throws Throwable
	{
		int tag = read();
		if (tag == 82)
			return readObject(expectedClass);
		if (tag == 70)
		{
			HashMap map = (HashMap)readObject(java/util/HashMap);
			throw prepareFault(map);
		}
		StringBuilder sb = new StringBuilder();
		sb.append((char)tag);
		int ch;
		try
		{
			while ((ch = read()) >= 0) 
				sb.append((char)ch);
		}
		catch (IOException e)
		{
			log.log(Level.FINE, e.toString(), e);
		}
		throw error((new StringBuilder()).append("expected hessian reply at ").append(codeName(tag)).append("\n").append(sb).toString());
	}

	public void startReply()
		throws Throwable
	{
		readReply(java/lang/Object);
	}

	private Throwable prepareFault(HashMap fault)
		throws IOException
	{
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
	}

	public void completeValueReply()
		throws IOException
	{
		int tag = read();
		if (tag != 90)
			error((new StringBuilder()).append("expected end of reply at ").append(codeName(tag)).toString());
	}

	public String readHeader()
		throws IOException
	{
		return null;
	}

	public int startMessage()
		throws IOException
	{
		int tag = read();
		if (tag != 112 && tag != 80)
		{
			throw error((new StringBuilder()).append("expected Hessian message ('p') at ").append(codeName(tag)).toString());
		} else
		{
			int major = read();
			int minor = read();
			return (major << 16) + minor;
		}
	}

	public void completeMessage()
		throws IOException
	{
		int tag = read();
		if (tag != 90)
			error((new StringBuilder()).append("expected end of message at ").append(codeName(tag)).toString());
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
		int tag = _offset >= _length ? read() : _buffer[_offset++] & 0xff;
		switch (tag)
		{
		case 84: // 'T'
			return true;

		case 70: // 'F'
			return false;

		case 128: 
		case 129: 
		case 130: 
		case 131: 
		case 132: 
		case 133: 
		case 134: 
		case 135: 
		case 136: 
		case 137: 
		case 138: 
		case 139: 
		case 140: 
		case 141: 
		case 142: 
		case 143: 
		case 144: 
		case 145: 
		case 146: 
		case 147: 
		case 148: 
		case 149: 
		case 150: 
		case 151: 
		case 152: 
		case 153: 
		case 154: 
		case 155: 
		case 156: 
		case 157: 
		case 158: 
		case 159: 
		case 160: 
		case 161: 
		case 162: 
		case 163: 
		case 164: 
		case 165: 
		case 166: 
		case 167: 
		case 168: 
		case 169: 
		case 170: 
		case 171: 
		case 172: 
		case 173: 
		case 174: 
		case 175: 
		case 176: 
		case 177: 
		case 178: 
		case 179: 
		case 180: 
		case 181: 
		case 182: 
		case 183: 
		case 184: 
		case 185: 
		case 186: 
		case 187: 
		case 188: 
		case 189: 
		case 190: 
		case 191: 
			return tag != 144;

		case 200: 
			return read() != 0;

		case 192: 
		case 193: 
		case 194: 
		case 195: 
		case 196: 
		case 197: 
		case 198: 
		case 199: 
		case 201: 
		case 202: 
		case 203: 
		case 204: 
		case 205: 
		case 206: 
		case 207: 
			read();
			return true;

		case 212: 
			return 256 * read() + read() != 0;

		case 208: 
		case 209: 
		case 210: 
		case 211: 
		case 213: 
		case 214: 
		case 215: 
			read();
			read();
			return true;

		case 73: // 'I'
			return parseInt() != 0;

		case 216: 
		case 217: 
		case 218: 
		case 219: 
		case 220: 
		case 221: 
		case 222: 
		case 223: 
		case 224: 
		case 225: 
		case 226: 
		case 227: 
		case 228: 
		case 229: 
		case 230: 
		case 231: 
		case 232: 
		case 233: 
		case 234: 
		case 235: 
		case 236: 
		case 237: 
		case 238: 
		case 239: 
			return tag != 224;

		case 248: 
			return read() != 0;

		case 240: 
		case 241: 
		case 242: 
		case 243: 
		case 244: 
		case 245: 
		case 246: 
		case 247: 
		case 249: 
		case 250: 
		case 251: 
		case 252: 
		case 253: 
		case 254: 
		case 255: 
			read();
			return true;

		case 60: // '<'
			return 256 * read() + read() != 0;

		case 56: // '8'
		case 57: // '9'
		case 58: // ':'
		case 59: // ';'
		case 61: // '='
		case 62: // '>'
		case 63: // '?'
			read();
			read();
			return true;

		case 89: // 'Y'
			return 0x1000000L * (long)read() + 0x10000L * (long)read() + (long)(256 * read()) + (long)read() != 0L;

		case 76: // 'L'
			return parseLong() != 0L;

		case 91: // '['
			return false;

		case 92: // '\\'
			return true;

		case 93: // ']'
			return read() != 0;

		case 94: // '^'
			return 256 * read() + read() != 0;

		case 95: // '_'
			int mills = parseInt();
			return mills != 0;

		case 68: // 'D'
			return parseDouble() != 0.0D;

		case 78: // 'N'
			return false;

		case 64: // '@'
		case 65: // 'A'
		case 66: // 'B'
		case 67: // 'C'
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
		case 85: // 'U'
		case 86: // 'V'
		case 87: // 'W'
		case 88: // 'X'
		case 90: // 'Z'
		case 96: // '`'
		case 97: // 'a'
		case 98: // 'b'
		case 99: // 'c'
		case 100: // 'd'
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
		case 114: // 'r'
		case 115: // 's'
		case 116: // 't'
		case 117: // 'u'
		case 118: // 'v'
		case 119: // 'w'
		case 120: // 'x'
		case 121: // 'y'
		case 122: // 'z'
		case 123: // '{'
		case 124: // '|'
		case 125: // '}'
		case 126: // '~'
		case 127: // '\177'
		default:
			throw expect("boolean", tag);
		}
	}

	public short readShort()
		throws IOException
	{
		return (short)readInt();
	}

	public final int readInt()
		throws IOException
	{
		int tag = read();
		switch (tag)
		{
		case 78: // 'N'
			return 0;

		case 70: // 'F'
			return 0;

		case 84: // 'T'
			return 1;

		case 128: 
		case 129: 
		case 130: 
		case 131: 
		case 132: 
		case 133: 
		case 134: 
		case 135: 
		case 136: 
		case 137: 
		case 138: 
		case 139: 
		case 140: 
		case 141: 
		case 142: 
		case 143: 
		case 144: 
		case 145: 
		case 146: 
		case 147: 
		case 148: 
		case 149: 
		case 150: 
		case 151: 
		case 152: 
		case 153: 
		case 154: 
		case 155: 
		case 156: 
		case 157: 
		case 158: 
		case 159: 
		case 160: 
		case 161: 
		case 162: 
		case 163: 
		case 164: 
		case 165: 
		case 166: 
		case 167: 
		case 168: 
		case 169: 
		case 170: 
		case 171: 
		case 172: 
		case 173: 
		case 174: 
		case 175: 
		case 176: 
		case 177: 
		case 178: 
		case 179: 
		case 180: 
		case 181: 
		case 182: 
		case 183: 
		case 184: 
		case 185: 
		case 186: 
		case 187: 
		case 188: 
		case 189: 
		case 190: 
		case 191: 
			return tag - 144;

		case 192: 
		case 193: 
		case 194: 
		case 195: 
		case 196: 
		case 197: 
		case 198: 
		case 199: 
		case 200: 
		case 201: 
		case 202: 
		case 203: 
		case 204: 
		case 205: 
		case 206: 
		case 207: 
			return (tag - 200 << 8) + read();

		case 208: 
		case 209: 
		case 210: 
		case 211: 
		case 212: 
		case 213: 
		case 214: 
		case 215: 
			return (tag - 212 << 16) + 256 * read() + read();

		case 73: // 'I'
		case 89: // 'Y'
			return (read() << 24) + (read() << 16) + (read() << 8) + read();

		case 216: 
		case 217: 
		case 218: 
		case 219: 
		case 220: 
		case 221: 
		case 222: 
		case 223: 
		case 224: 
		case 225: 
		case 226: 
		case 227: 
		case 228: 
		case 229: 
		case 230: 
		case 231: 
		case 232: 
		case 233: 
		case 234: 
		case 235: 
		case 236: 
		case 237: 
		case 238: 
		case 239: 
			return tag - 224;

		case 240: 
		case 241: 
		case 242: 
		case 243: 
		case 244: 
		case 245: 
		case 246: 
		case 247: 
		case 248: 
		case 249: 
		case 250: 
		case 251: 
		case 252: 
		case 253: 
		case 254: 
		case 255: 
			return (tag - 248 << 8) + read();

		case 56: // '8'
		case 57: // '9'
		case 58: // ':'
		case 59: // ';'
		case 60: // '<'
		case 61: // '='
		case 62: // '>'
		case 63: // '?'
			return (tag - 60 << 16) + 256 * read() + read();

		case 76: // 'L'
			return (int)parseLong();

		case 91: // '['
			return 0;

		case 92: // '\\'
			return 1;

		case 93: // ']'
			return (byte)(_offset >= _length ? read() : _buffer[_offset++]);

		case 94: // '^'
			return (short)(256 * read() + read());

		case 95: // '_'
			int mills = parseInt();
			return (int)(0.001D * (double)mills);

		case 68: // 'D'
			return (int)parseDouble();

		case 64: // '@'
		case 65: // 'A'
		case 66: // 'B'
		case 67: // 'C'
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
		case 85: // 'U'
		case 86: // 'V'
		case 87: // 'W'
		case 88: // 'X'
		case 90: // 'Z'
		case 96: // '`'
		case 97: // 'a'
		case 98: // 'b'
		case 99: // 'c'
		case 100: // 'd'
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
		case 114: // 'r'
		case 115: // 's'
		case 116: // 't'
		case 117: // 'u'
		case 118: // 'v'
		case 119: // 'w'
		case 120: // 'x'
		case 121: // 'y'
		case 122: // 'z'
		case 123: // '{'
		case 124: // '|'
		case 125: // '}'
		case 126: // '~'
		case 127: // '\177'
		default:
			throw expect("integer", tag);
		}
	}

	public long readLong()
		throws IOException
	{
		int tag = read();
		switch (tag)
		{
		case 78: // 'N'
			return 0L;

		case 70: // 'F'
			return 0L;

		case 84: // 'T'
			return 1L;

		case 128: 
		case 129: 
		case 130: 
		case 131: 
		case 132: 
		case 133: 
		case 134: 
		case 135: 
		case 136: 
		case 137: 
		case 138: 
		case 139: 
		case 140: 
		case 141: 
		case 142: 
		case 143: 
		case 144: 
		case 145: 
		case 146: 
		case 147: 
		case 148: 
		case 149: 
		case 150: 
		case 151: 
		case 152: 
		case 153: 
		case 154: 
		case 155: 
		case 156: 
		case 157: 
		case 158: 
		case 159: 
		case 160: 
		case 161: 
		case 162: 
		case 163: 
		case 164: 
		case 165: 
		case 166: 
		case 167: 
		case 168: 
		case 169: 
		case 170: 
		case 171: 
		case 172: 
		case 173: 
		case 174: 
		case 175: 
		case 176: 
		case 177: 
		case 178: 
		case 179: 
		case 180: 
		case 181: 
		case 182: 
		case 183: 
		case 184: 
		case 185: 
		case 186: 
		case 187: 
		case 188: 
		case 189: 
		case 190: 
		case 191: 
			return (long)(tag - 144);

		case 192: 
		case 193: 
		case 194: 
		case 195: 
		case 196: 
		case 197: 
		case 198: 
		case 199: 
		case 200: 
		case 201: 
		case 202: 
		case 203: 
		case 204: 
		case 205: 
		case 206: 
		case 207: 
			return (long)((tag - 200 << 8) + read());

		case 208: 
		case 209: 
		case 210: 
		case 211: 
		case 212: 
		case 213: 
		case 214: 
		case 215: 
			return (long)((tag - 212 << 16) + 256 * read() + read());

		case 93: // ']'
			return (long)(byte)(_offset >= _length ? read() : _buffer[_offset++]);

		case 94: // '^'
			return (long)(short)(256 * read() + read());

		case 73: // 'I'
		case 89: // 'Y'
			return (long)parseInt();

		case 216: 
		case 217: 
		case 218: 
		case 219: 
		case 220: 
		case 221: 
		case 222: 
		case 223: 
		case 224: 
		case 225: 
		case 226: 
		case 227: 
		case 228: 
		case 229: 
		case 230: 
		case 231: 
		case 232: 
		case 233: 
		case 234: 
		case 235: 
		case 236: 
		case 237: 
		case 238: 
		case 239: 
			return (long)(tag - 224);

		case 240: 
		case 241: 
		case 242: 
		case 243: 
		case 244: 
		case 245: 
		case 246: 
		case 247: 
		case 248: 
		case 249: 
		case 250: 
		case 251: 
		case 252: 
		case 253: 
		case 254: 
		case 255: 
			return (long)((tag - 248 << 8) + read());

		case 56: // '8'
		case 57: // '9'
		case 58: // ':'
		case 59: // ';'
		case 60: // '<'
		case 61: // '='
		case 62: // '>'
		case 63: // '?'
			return (long)((tag - 60 << 16) + 256 * read() + read());

		case 76: // 'L'
			return parseLong();

		case 91: // '['
			return 0L;

		case 92: // '\\'
			return 1L;

		case 95: // '_'
			int mills = parseInt();
			return (long)(0.001D * (double)mills);

		case 68: // 'D'
			return (long)parseDouble();

		case 64: // '@'
		case 65: // 'A'
		case 66: // 'B'
		case 67: // 'C'
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
		case 85: // 'U'
		case 86: // 'V'
		case 87: // 'W'
		case 88: // 'X'
		case 90: // 'Z'
		case 96: // '`'
		case 97: // 'a'
		case 98: // 'b'
		case 99: // 'c'
		case 100: // 'd'
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
		case 114: // 'r'
		case 115: // 's'
		case 116: // 't'
		case 117: // 'u'
		case 118: // 'v'
		case 119: // 'w'
		case 120: // 'x'
		case 121: // 'y'
		case 122: // 'z'
		case 123: // '{'
		case 124: // '|'
		case 125: // '}'
		case 126: // '~'
		case 127: // '\177'
		default:
			throw expect("long", tag);
		}
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
		case 78: // 'N'
			return 0.0D;

		case 70: // 'F'
			return 0.0D;

		case 84: // 'T'
			return 1.0D;

		case 128: 
		case 129: 
		case 130: 
		case 131: 
		case 132: 
		case 133: 
		case 134: 
		case 135: 
		case 136: 
		case 137: 
		case 138: 
		case 139: 
		case 140: 
		case 141: 
		case 142: 
		case 143: 
		case 144: 
		case 145: 
		case 146: 
		case 147: 
		case 148: 
		case 149: 
		case 150: 
		case 151: 
		case 152: 
		case 153: 
		case 154: 
		case 155: 
		case 156: 
		case 157: 
		case 158: 
		case 159: 
		case 160: 
		case 161: 
		case 162: 
		case 163: 
		case 164: 
		case 165: 
		case 166: 
		case 167: 
		case 168: 
		case 169: 
		case 170: 
		case 171: 
		case 172: 
		case 173: 
		case 174: 
		case 175: 
		case 176: 
		case 177: 
		case 178: 
		case 179: 
		case 180: 
		case 181: 
		case 182: 
		case 183: 
		case 184: 
		case 185: 
		case 186: 
		case 187: 
		case 188: 
		case 189: 
		case 190: 
		case 191: 
			return (double)(tag - 144);

		case 192: 
		case 193: 
		case 194: 
		case 195: 
		case 196: 
		case 197: 
		case 198: 
		case 199: 
		case 200: 
		case 201: 
		case 202: 
		case 203: 
		case 204: 
		case 205: 
		case 206: 
		case 207: 
			return (double)((tag - 200 << 8) + read());

		case 208: 
		case 209: 
		case 210: 
		case 211: 
		case 212: 
		case 213: 
		case 214: 
		case 215: 
			return (double)((tag - 212 << 16) + 256 * read() + read());

		case 73: // 'I'
		case 89: // 'Y'
			return (double)parseInt();

		case 216: 
		case 217: 
		case 218: 
		case 219: 
		case 220: 
		case 221: 
		case 222: 
		case 223: 
		case 224: 
		case 225: 
		case 226: 
		case 227: 
		case 228: 
		case 229: 
		case 230: 
		case 231: 
		case 232: 
		case 233: 
		case 234: 
		case 235: 
		case 236: 
		case 237: 
		case 238: 
		case 239: 
			return (double)(tag - 224);

		case 240: 
		case 241: 
		case 242: 
		case 243: 
		case 244: 
		case 245: 
		case 246: 
		case 247: 
		case 248: 
		case 249: 
		case 250: 
		case 251: 
		case 252: 
		case 253: 
		case 254: 
		case 255: 
			return (double)((tag - 248 << 8) + read());

		case 56: // '8'
		case 57: // '9'
		case 58: // ':'
		case 59: // ';'
		case 60: // '<'
		case 61: // '='
		case 62: // '>'
		case 63: // '?'
			return (double)((tag - 60 << 16) + 256 * read() + read());

		case 76: // 'L'
			return (double)parseLong();

		case 91: // '['
			return 0.0D;

		case 92: // '\\'
			return 1.0D;

		case 93: // ']'
			return (double)(byte)(_offset >= _length ? read() : _buffer[_offset++]);

		case 94: // '^'
			return (double)(short)(256 * read() + read());

		case 95: // '_'
			int mills = parseInt();
			return 0.001D * (double)mills;

		case 68: // 'D'
			return parseDouble();

		case 64: // '@'
		case 65: // 'A'
		case 66: // 'B'
		case 67: // 'C'
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
		case 85: // 'U'
		case 86: // 'V'
		case 87: // 'W'
		case 88: // 'X'
		case 90: // 'Z'
		case 96: // '`'
		case 97: // 'a'
		case 98: // 'b'
		case 99: // 'c'
		case 100: // 'd'
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
		case 114: // 'r'
		case 115: // 's'
		case 116: // 't'
		case 117: // 'u'
		case 118: // 'v'
		case 119: // 'w'
		case 120: // 'x'
		case 121: // 'y'
		case 122: // 'z'
		case 123: // '{'
		case 124: // '|'
		case 125: // '}'
		case 126: // '~'
		case 127: // '\177'
		default:
			throw expect("double", tag);
		}
	}

	public long readUTCDate()
		throws IOException
	{
		int tag = read();
		if (tag == 74)
			return parseLong();
		if (tag == 75)
			return (long)parseInt() * 60000L;
		else
			throw expect("date", tag);
	}

	public int readChar()
		throws IOException
	{
		if (_chunkLength > 0)
		{
			_chunkLength--;
			if (_chunkLength == 0 && _isLastChunk)
				_chunkLength = -2;
			int ch = parseUTF8Char();
			return ch;
		}
		if (_chunkLength == -2)
		{
			_chunkLength = 0;
			return -1;
		}
		int tag = read();
		switch (tag)
		{
		case 78: // 'N'
			return -1;

		case 82: // 'R'
		case 83: // 'S'
			_isLastChunk = tag == 83;
			_chunkLength = (read() << 8) + read();
			_chunkLength--;
			int value = parseUTF8Char();
			if (_chunkLength == 0 && _isLastChunk)
				_chunkLength = -2;
			return value;
		}
		throw expect("char", tag);
	}

	public int readString(char buffer[], int offset, int length)
		throws IOException
	{
		int readLength = 0;
		if (_chunkLength == -2)
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

			case 82: // 'R'
			case 83: // 'S'
				_isLastChunk = tag == 83;
				_chunkLength = (read() << 8) + read();
				break;

			case 0: // '\0'
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
			case 25: // '\031'
			case 26: // '\032'
			case 27: // '\033'
			case 28: // '\034'
			case 29: // '\035'
			case 30: // '\036'
			case 31: // '\037'
				_isLastChunk = true;
				_chunkLength = tag - 0;
				break;

			case 48: // '0'
			case 49: // '1'
			case 50: // '2'
			case 51: // '3'
				_isLastChunk = true;
				_chunkLength = (tag - 48) * 256 + read();
				break;

			case 32: // ' '
			case 33: // '!'
			case 34: // '"'
			case 35: // '#'
			case 36: // '$'
			case 37: // '%'
			case 38: // '&'
			case 39: // '\''
			case 40: // '('
			case 41: // ')'
			case 42: // '*'
			case 43: // '+'
			case 44: // ','
			case 45: // '-'
			case 46: // '.'
			case 47: // '/'
			case 52: // '4'
			case 53: // '5'
			case 54: // '6'
			case 55: // '7'
			case 56: // '8'
			case 57: // '9'
			case 58: // ':'
			case 59: // ';'
			case 60: // '<'
			case 61: // '='
			case 62: // '>'
			case 63: // '?'
			case 64: // '@'
			case 65: // 'A'
			case 66: // 'B'
			case 67: // 'C'
			case 68: // 'D'
			case 69: // 'E'
			case 70: // 'F'
			case 71: // 'G'
			case 72: // 'H'
			case 73: // 'I'
			case 74: // 'J'
			case 75: // 'K'
			case 76: // 'L'
			case 77: // 'M'
			case 79: // 'O'
			case 80: // 'P'
			case 81: // 'Q'
			default:
				throw expect("string", tag);
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
					_chunkLength = -2;
					return readLength;
				}
			int tag = read();
			switch (tag)
			{
			case 82: // 'R'
			case 83: // 'S'
				_isLastChunk = tag == 83;
				_chunkLength = (read() << 8) + read();
				break;

			case 0: // '\0'
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
			case 25: // '\031'
			case 26: // '\032'
			case 27: // '\033'
			case 28: // '\034'
			case 29: // '\035'
			case 30: // '\036'
			case 31: // '\037'
				_isLastChunk = true;
				_chunkLength = tag - 0;
				break;

			case 48: // '0'
			case 49: // '1'
			case 50: // '2'
			case 51: // '3'
				_isLastChunk = true;
				_chunkLength = (tag - 48) * 256 + read();
				break;

			case 32: // ' '
			case 33: // '!'
			case 34: // '"'
			case 35: // '#'
			case 36: // '$'
			case 37: // '%'
			case 38: // '&'
			case 39: // '\''
			case 40: // '('
			case 41: // ')'
			case 42: // '*'
			case 43: // '+'
			case 44: // ','
			case 45: // '-'
			case 46: // '.'
			case 47: // '/'
			case 52: // '4'
			case 53: // '5'
			case 54: // '6'
			case 55: // '7'
			case 56: // '8'
			case 57: // '9'
			case 58: // ':'
			case 59: // ';'
			case 60: // '<'
			case 61: // '='
			case 62: // '>'
			case 63: // '?'
			case 64: // '@'
			case 65: // 'A'
			case 66: // 'B'
			case 67: // 'C'
			case 68: // 'D'
			case 69: // 'E'
			case 70: // 'F'
			case 71: // 'G'
			case 72: // 'H'
			case 73: // 'I'
			case 74: // 'J'
			case 75: // 'K'
			case 76: // 'L'
			case 77: // 'M'
			case 78: // 'N'
			case 79: // 'O'
			case 80: // 'P'
			case 81: // 'Q'
			default:
				throw expect("string", tag);
			}
		} while (true);
		if (readLength == 0)
			return -1;
		if (_chunkLength > 0 || !_isLastChunk)
		{
			return readLength;
		} else
		{
			_chunkLength = -2;
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
		{
			return null;
		}

		case 84: // 'T'
		{
			return "true";
		}

		case 70: // 'F'
		{
			return "false";
		}

		case 128: 
		case 129: 
		case 130: 
		case 131: 
		case 132: 
		case 133: 
		case 134: 
		case 135: 
		case 136: 
		case 137: 
		case 138: 
		case 139: 
		case 140: 
		case 141: 
		case 142: 
		case 143: 
		case 144: 
		case 145: 
		case 146: 
		case 147: 
		case 148: 
		case 149: 
		case 150: 
		case 151: 
		case 152: 
		case 153: 
		case 154: 
		case 155: 
		case 156: 
		case 157: 
		case 158: 
		case 159: 
		case 160: 
		case 161: 
		case 162: 
		case 163: 
		case 164: 
		case 165: 
		case 166: 
		case 167: 
		case 168: 
		case 169: 
		case 170: 
		case 171: 
		case 172: 
		case 173: 
		case 174: 
		case 175: 
		case 176: 
		case 177: 
		case 178: 
		case 179: 
		case 180: 
		case 181: 
		case 182: 
		case 183: 
		case 184: 
		case 185: 
		case 186: 
		case 187: 
		case 188: 
		case 189: 
		case 190: 
		case 191: 
		{
			return String.valueOf(tag - 144);
		}

		case 192: 
		case 193: 
		case 194: 
		case 195: 
		case 196: 
		case 197: 
		case 198: 
		case 199: 
		case 200: 
		case 201: 
		case 202: 
		case 203: 
		case 204: 
		case 205: 
		case 206: 
		case 207: 
		{
			return String.valueOf((tag - 200 << 8) + read());
		}

		case 208: 
		case 209: 
		case 210: 
		case 211: 
		case 212: 
		case 213: 
		case 214: 
		case 215: 
		{
			return String.valueOf((tag - 212 << 16) + 256 * read() + read());
		}

		case 73: // 'I'
		case 89: // 'Y'
		{
			return String.valueOf(parseInt());
		}

		case 216: 
		case 217: 
		case 218: 
		case 219: 
		case 220: 
		case 221: 
		case 222: 
		case 223: 
		case 224: 
		case 225: 
		case 226: 
		case 227: 
		case 228: 
		case 229: 
		case 230: 
		case 231: 
		case 232: 
		case 233: 
		case 234: 
		case 235: 
		case 236: 
		case 237: 
		case 238: 
		case 239: 
		{
			return String.valueOf(tag - 224);
		}

		case 240: 
		case 241: 
		case 242: 
		case 243: 
		case 244: 
		case 245: 
		case 246: 
		case 247: 
		case 248: 
		case 249: 
		case 250: 
		case 251: 
		case 252: 
		case 253: 
		case 254: 
		case 255: 
		{
			return String.valueOf((tag - 248 << 8) + read());
		}

		case 56: // '8'
		case 57: // '9'
		case 58: // ':'
		case 59: // ';'
		case 60: // '<'
		case 61: // '='
		case 62: // '>'
		case 63: // '?'
		{
			return String.valueOf((tag - 60 << 16) + 256 * read() + read());
		}

		case 76: // 'L'
		{
			return String.valueOf(parseLong());
		}

		case 91: // '['
		{
			return "0.0";
		}

		case 92: // '\\'
		{
			return "1.0";
		}

		case 93: // ']'
		{
			return String.valueOf((byte)(_offset >= _length ? read() : _buffer[_offset++]));
		}

		case 94: // '^'
		{
			return String.valueOf((short)(256 * read() + read()));
		}

		case 95: // '_'
		{
			int mills = parseInt();
			return String.valueOf(0.001D * (double)mills);
		}

		case 68: // 'D'
		{
			return String.valueOf(parseDouble());
		}

		case 82: // 'R'
		case 83: // 'S'
		{
			_isLastChunk = tag == 83;
			_chunkLength = (read() << 8) + read();
			_sbuf.setLength(0);
			int ch;
			while ((ch = parseChar()) >= 0) 
				_sbuf.append((char)ch);
			return _sbuf.toString();
		}

		case 0: // '\0'
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
		case 25: // '\031'
		case 26: // '\032'
		case 27: // '\033'
		case 28: // '\034'
		case 29: // '\035'
		case 30: // '\036'
		case 31: // '\037'
		{
			_isLastChunk = true;
			_chunkLength = tag - 0;
			_sbuf.setLength(0);
			int ch;
			while ((ch = parseChar()) >= 0) 
				_sbuf.append((char)ch);
			return _sbuf.toString();
		}

		case 48: // '0'
		case 49: // '1'
		case 50: // '2'
		case 51: // '3'
		{
			_isLastChunk = true;
			_chunkLength = (tag - 48) * 256 + read();
			_sbuf.setLength(0);
			int ch;
			while ((ch = parseChar()) >= 0) 
				_sbuf.append((char)ch);
			return _sbuf.toString();
		}

		case 32: // ' '
		case 33: // '!'
		case 34: // '"'
		case 35: // '#'
		case 36: // '$'
		case 37: // '%'
		case 38: // '&'
		case 39: // '\''
		case 40: // '('
		case 41: // ')'
		case 42: // '*'
		case 43: // '+'
		case 44: // ','
		case 45: // '-'
		case 46: // '.'
		case 47: // '/'
		case 52: // '4'
		case 53: // '5'
		case 54: // '6'
		case 55: // '7'
		case 64: // '@'
		case 65: // 'A'
		case 66: // 'B'
		case 67: // 'C'
		case 69: // 'E'
		case 71: // 'G'
		case 72: // 'H'
		case 74: // 'J'
		case 75: // 'K'
		case 77: // 'M'
		case 79: // 'O'
		case 80: // 'P'
		case 81: // 'Q'
		case 85: // 'U'
		case 86: // 'V'
		case 87: // 'W'
		case 88: // 'X'
		case 90: // 'Z'
		case 96: // '`'
		case 97: // 'a'
		case 98: // 'b'
		case 99: // 'c'
		case 100: // 'd'
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
		case 114: // 'r'
		case 115: // 's'
		case 116: // 't'
		case 117: // 'u'
		case 118: // 'v'
		case 119: // 'w'
		case 120: // 'x'
		case 121: // 'y'
		case 122: // 'z'
		case 123: // '{'
		case 124: // '|'
		case 125: // '}'
		case 126: // '~'
		case 127: // '\177'
		default:
		{
			throw expect("string", tag);
		}
		}
	}

	public byte[] readBytes()
		throws IOException
	{
		int tag = read();
		switch (tag)
		{
		case 78: // 'N'
		{
			return null;
		}

		case 65: // 'A'
		case 66: // 'B'
		{
			_isLastChunk = tag == 66;
			_chunkLength = (read() << 8) + read();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int data;
			while ((data = parseByte()) >= 0) 
				bos.write(data);
			return bos.toByteArray();
		}

		case 32: // ' '
		case 33: // '!'
		case 34: // '"'
		case 35: // '#'
		case 36: // '$'
		case 37: // '%'
		case 38: // '&'
		case 39: // '\''
		case 40: // '('
		case 41: // ')'
		case 42: // '*'
		case 43: // '+'
		case 44: // ','
		case 45: // '-'
		case 46: // '.'
		case 47: // '/'
		{
			_isLastChunk = true;
			_chunkLength = tag - 32;
			byte buffer[] = new byte[_chunkLength];
			int offset = 0;
			do
			{
				if (offset >= _chunkLength)
					break;
				int sublen = read(buffer, 0, _chunkLength - offset);
				if (sublen <= 0)
					break;
				offset += sublen;
			} while (true);
			return buffer;
		}

		case 52: // '4'
		case 53: // '5'
		case 54: // '6'
		case 55: // '7'
		{
			_isLastChunk = true;
			_chunkLength = (tag - 52) * 256 + read();
			byte buffer[] = new byte[_chunkLength];
			int offset = 0;
			do
			{
				if (offset >= _chunkLength)
					break;
				int sublen = read(buffer, 0, _chunkLength - offset);
				if (sublen <= 0)
					break;
				offset += sublen;
			} while (true);
			return buffer;
		}

		case 48: // '0'
		case 49: // '1'
		case 50: // '2'
		case 51: // '3'
		case 56: // '8'
		case 57: // '9'
		case 58: // ':'
		case 59: // ';'
		case 60: // '<'
		case 61: // '='
		case 62: // '>'
		case 63: // '?'
		case 64: // '@'
		case 67: // 'C'
		case 68: // 'D'
		case 69: // 'E'
		case 70: // 'F'
		case 71: // 'G'
		case 72: // 'H'
		case 73: // 'I'
		case 74: // 'J'
		case 75: // 'K'
		case 76: // 'L'
		case 77: // 'M'
		default:
		{
			throw expect("bytes", tag);
		}
		}
	}

	public int readByte()
		throws IOException
	{
		if (_chunkLength > 0)
		{
			_chunkLength--;
			if (_chunkLength == 0 && _isLastChunk)
				_chunkLength = -2;
			return read();
		}
		if (_chunkLength == -2)
		{
			_chunkLength = 0;
			return -1;
		}
		int tag = read();
		switch (tag)
		{
		case 78: // 'N'
		{
			return -1;
		}

		case 65: // 'A'
		case 66: // 'B'
		{
			_isLastChunk = tag == 66;
			_chunkLength = (read() << 8) + read();
			int value = parseByte();
			if (_chunkLength == 0 && _isLastChunk)
				_chunkLength = -2;
			return value;
		}

		case 32: // ' '
		case 33: // '!'
		case 34: // '"'
		case 35: // '#'
		case 36: // '$'
		case 37: // '%'
		case 38: // '&'
		case 39: // '\''
		case 40: // '('
		case 41: // ')'
		case 42: // '*'
		case 43: // '+'
		case 44: // ','
		case 45: // '-'
		case 46: // '.'
		case 47: // '/'
		{
			_isLastChunk = true;
			_chunkLength = tag - 32;
			int value = parseByte();
			if (_chunkLength == 0)
				_chunkLength = -2;
			return value;
		}

		case 52: // '4'
		case 53: // '5'
		case 54: // '6'
		case 55: // '7'
		{
			_isLastChunk = true;
			_chunkLength = (tag - 52) * 256 + read();
			int value = parseByte();
			if (_chunkLength == 0)
				_chunkLength = -2;
			return value;
		}

		case 48: // '0'
		case 49: // '1'
		case 50: // '2'
		case 51: // '3'
		case 56: // '8'
		case 57: // '9'
		case 58: // ':'
		case 59: // ';'
		case 60: // '<'
		case 61: // '='
		case 62: // '>'
		case 63: // '?'
		case 64: // '@'
		case 67: // 'C'
		case 68: // 'D'
		case 69: // 'E'
		case 70: // 'F'
		case 71: // 'G'
		case 72: // 'H'
		case 73: // 'I'
		case 74: // 'J'
		case 75: // 'K'
		case 76: // 'L'
		case 77: // 'M'
		default:
		{
			throw expect("binary", tag);
		}
		}
	}

	public int readBytes(byte buffer[], int offset, int length)
		throws IOException
	{
		int readLength = 0;
		if (_chunkLength == -2)
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

			case 65: // 'A'
			case 66: // 'B'
				_isLastChunk = tag == 66;
				_chunkLength = (read() << 8) + read();
				break;

			case 32: // ' '
			case 33: // '!'
			case 34: // '"'
			case 35: // '#'
			case 36: // '$'
			case 37: // '%'
			case 38: // '&'
			case 39: // '\''
			case 40: // '('
			case 41: // ')'
			case 42: // '*'
			case 43: // '+'
			case 44: // ','
			case 45: // '-'
			case 46: // '.'
			case 47: // '/'
				_isLastChunk = true;
				_chunkLength = tag - 32;
				break;

			case 52: // '4'
			case 53: // '5'
			case 54: // '6'
			case 55: // '7'
				_isLastChunk = true;
				_chunkLength = (tag - 52) * 256 + read();
				break;

			case 48: // '0'
			case 49: // '1'
			case 50: // '2'
			case 51: // '3'
			case 56: // '8'
			case 57: // '9'
			case 58: // ':'
			case 59: // ';'
			case 60: // '<'
			case 61: // '='
			case 62: // '>'
			case 63: // '?'
			case 64: // '@'
			case 67: // 'C'
			case 68: // 'D'
			case 69: // 'E'
			case 70: // 'F'
			case 71: // 'G'
			case 72: // 'H'
			case 73: // 'I'
			case 74: // 'J'
			case 75: // 'K'
			case 76: // 'L'
			case 77: // 'M'
			default:
				throw expect("binary", tag);
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
					_chunkLength = -2;
					return readLength;
				}
			int tag = read();
			switch (tag)
			{
			case 65: // 'A'
			case 66: // 'B'
				_isLastChunk = tag == 66;
				_chunkLength = (read() << 8) + read();
				break;

			default:
				throw expect("binary", tag);
			}
		} while (true);
		if (readLength == 0)
			return -1;
		if (_chunkLength > 0 || !_isLastChunk)
		{
			return readLength;
		} else
		{
			_chunkLength = -2;
			return readLength;
		}
	}

	private HashMap readFault()
		throws IOException
	{
		HashMap map = new HashMap();
		int code;
		for (code = read(); code > 0 && code != 90; code = read())
		{
			_offset--;
			Object key = readObject();
			Object value = readObject();
			if (key != null && value != null)
				map.put(key, value);
		}

		if (code != 90)
			throw expect("fault", code);
		else
			return map;
	}

	public Object readObject(Class cl)
		throws IOException
	{
		if (cl == null || cl == java/lang/Object)
			return readObject();
		int tag = _offset >= _length ? read() : _buffer[_offset++] & 0xff;
		switch (tag)
		{
		case 78: // 'N'
		{
			return null;
		}

		case 72: // 'H'
		{
			Deserializer reader = findSerializerFactory().getDeserializer(cl);
			return reader.readMap(this);
		}

		case 77: // 'M'
		{
			String type = readType();
			if ("".equals(type))
			{
				Deserializer reader = findSerializerFactory().getDeserializer(cl);
				return reader.readMap(this);
			} else
			{
				Deserializer reader = findSerializerFactory().getObjectDeserializer(type, cl);
				return reader.readMap(this);
			}
		}

		case 67: // 'C'
		{
			readObjectDefinition(cl);
			return readObject(cl);
		}

		case 96: // '`'
		case 97: // 'a'
		case 98: // 'b'
		case 99: // 'c'
		case 100: // 'd'
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
		{
			int ref = tag - 96;
			int size = _classDefs.size();
			if (ref < 0 || size <= ref)
			{
				throw new HessianProtocolException((new StringBuilder()).append("'").append(ref).append("' is an unknown class definition").toString());
			} else
			{
				ObjectDefinition def = (ObjectDefinition)_classDefs.get(ref);
				return readObjectInstance(cl, def);
			}
		}

		case 79: // 'O'
		{
			int ref = readInt();
			int size = _classDefs.size();
			if (ref < 0 || size <= ref)
			{
				throw new HessianProtocolException((new StringBuilder()).append("'").append(ref).append("' is an unknown class definition").toString());
			} else
			{
				ObjectDefinition def = (ObjectDefinition)_classDefs.get(ref);
				return readObjectInstance(cl, def);
			}
		}

		case 85: // 'U'
		{
			String type = readType();
			Deserializer reader = findSerializerFactory().getListDeserializer(type, cl);
			Object v = reader.readList(this, -1);
			return v;
		}

		case 86: // 'V'
		{
			String type = readType();
			int length = readInt();
			Deserializer reader = findSerializerFactory().getListDeserializer(type, cl);
			Object v = reader.readLengthList(this, length);
			return v;
		}

		case 112: // 'p'
		case 113: // 'q'
		case 114: // 'r'
		case 115: // 's'
		case 116: // 't'
		case 117: // 'u'
		case 118: // 'v'
		case 119: // 'w'
		{
			int length = tag - 112;
			String type = readType();
			Deserializer reader = findSerializerFactory().getListDeserializer(type, cl);
			Object v = reader.readLengthList(this, length);
			return v;
		}

		case 87: // 'W'
		{
			Deserializer reader = findSerializerFactory().getListDeserializer(null, cl);
			Object v = reader.readList(this, -1);
			return v;
		}

		case 88: // 'X'
		{
			int length = readInt();
			Deserializer reader = findSerializerFactory().getListDeserializer(null, cl);
			Object v = reader.readLengthList(this, length);
			return v;
		}

		case 120: // 'x'
		case 121: // 'y'
		case 122: // 'z'
		case 123: // '{'
		case 124: // '|'
		case 125: // '}'
		case 126: // '~'
		case 127: // '\177'
		{
			int length = tag - 120;
			Deserializer reader = findSerializerFactory().getListDeserializer(null, cl);
			Object v = reader.readLengthList(this, length);
			return v;
		}

		case 81: // 'Q'
		{
			int ref = readInt();
			return _refs.get(ref);
		}
		}
		if (tag >= 0)
			_offset--;
		Object value = findSerializerFactory().getDeserializer(cl).readObject(this);
		return value;
	}

	public Object readObject()
		throws IOException
	{
		int tag = _offset >= _length ? read() : _buffer[_offset++] & 0xff;
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

		case 128: 
		case 129: 
		case 130: 
		case 131: 
		case 132: 
		case 133: 
		case 134: 
		case 135: 
		case 136: 
		case 137: 
		case 138: 
		case 139: 
		case 140: 
		case 141: 
		case 142: 
		case 143: 
		case 144: 
		case 145: 
		case 146: 
		case 147: 
		case 148: 
		case 149: 
		case 150: 
		case 151: 
		case 152: 
		case 153: 
		case 154: 
		case 155: 
		case 156: 
		case 157: 
		case 158: 
		case 159: 
		case 160: 
		case 161: 
		case 162: 
		case 163: 
		case 164: 
		case 165: 
		case 166: 
		case 167: 
		case 168: 
		case 169: 
		case 170: 
		case 171: 
		case 172: 
		case 173: 
		case 174: 
		case 175: 
		case 176: 
		case 177: 
		case 178: 
		case 179: 
		case 180: 
		case 181: 
		case 182: 
		case 183: 
		case 184: 
		case 185: 
		case 186: 
		case 187: 
		case 188: 
		case 189: 
		case 190: 
		case 191: 
		{
			return Integer.valueOf(tag - 144);
		}

		case 192: 
		case 193: 
		case 194: 
		case 195: 
		case 196: 
		case 197: 
		case 198: 
		case 199: 
		case 200: 
		case 201: 
		case 202: 
		case 203: 
		case 204: 
		case 205: 
		case 206: 
		case 207: 
		{
			return Integer.valueOf((tag - 200 << 8) + read());
		}

		case 208: 
		case 209: 
		case 210: 
		case 211: 
		case 212: 
		case 213: 
		case 214: 
		case 215: 
		{
			return Integer.valueOf((tag - 212 << 16) + 256 * read() + read());
		}

		case 73: // 'I'
		{
			return Integer.valueOf(parseInt());
		}

		case 216: 
		case 217: 
		case 218: 
		case 219: 
		case 220: 
		case 221: 
		case 222: 
		case 223: 
		case 224: 
		case 225: 
		case 226: 
		case 227: 
		case 228: 
		case 229: 
		case 230: 
		case 231: 
		case 232: 
		case 233: 
		case 234: 
		case 235: 
		case 236: 
		case 237: 
		case 238: 
		case 239: 
		{
			return Long.valueOf(tag - 224);
		}

		case 240: 
		case 241: 
		case 242: 
		case 243: 
		case 244: 
		case 245: 
		case 246: 
		case 247: 
		case 248: 
		case 249: 
		case 250: 
		case 251: 
		case 252: 
		case 253: 
		case 254: 
		case 255: 
		{
			return Long.valueOf((tag - 248 << 8) + read());
		}

		case 56: // '8'
		case 57: // '9'
		case 58: // ':'
		case 59: // ';'
		case 60: // '<'
		case 61: // '='
		case 62: // '>'
		case 63: // '?'
		{
			return Long.valueOf((tag - 60 << 16) + 256 * read() + read());
		}

		case 89: // 'Y'
		{
			return Long.valueOf(parseInt());
		}

		case 76: // 'L'
		{
			return Long.valueOf(parseLong());
		}

		case 91: // '['
		{
			return Double.valueOf(0.0D);
		}

		case 92: // '\\'
		{
			return Double.valueOf(1.0D);
		}

		case 93: // ']'
		{
			return Double.valueOf((byte)read());
		}

		case 94: // '^'
		{
			return Double.valueOf((short)(256 * read() + read()));
		}

		case 95: // '_'
		{
			int mills = parseInt();
			return Double.valueOf(0.001D * (double)mills);
		}

		case 68: // 'D'
		{
			return Double.valueOf(parseDouble());
		}

		case 74: // 'J'
		{
			return new Date(parseLong());
		}

		case 75: // 'K'
		{
			return new Date((long)parseInt() * 60000L);
		}

		case 82: // 'R'
		case 83: // 'S'
		{
			_isLastChunk = tag == 83;
			_chunkLength = (read() << 8) + read();
			_sbuf.setLength(0);
			int data;
			while ((data = parseChar()) >= 0) 
				_sbuf.append((char)data);
			return _sbuf.toString();
		}

		case 0: // '\0'
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
		case 25: // '\031'
		case 26: // '\032'
		case 27: // '\033'
		case 28: // '\034'
		case 29: // '\035'
		case 30: // '\036'
		case 31: // '\037'
		{
			_isLastChunk = true;
			_chunkLength = tag - 0;
			_sbuf.setLength(0);
			int data;
			while ((data = parseChar()) >= 0) 
				_sbuf.append((char)data);
			return _sbuf.toString();
		}

		case 48: // '0'
		case 49: // '1'
		case 50: // '2'
		case 51: // '3'
		{
			_isLastChunk = true;
			_chunkLength = (tag - 48) * 256 + read();
			_sbuf.setLength(0);
			int ch;
			while ((ch = parseChar()) >= 0) 
				_sbuf.append((char)ch);
			return _sbuf.toString();
		}

		case 65: // 'A'
		case 66: // 'B'
		{
			_isLastChunk = tag == 66;
			_chunkLength = (read() << 8) + read();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int data;
			while ((data = parseByte()) >= 0) 
				bos.write(data);
			return bos.toByteArray();
		}

		case 32: // ' '
		case 33: // '!'
		case 34: // '"'
		case 35: // '#'
		case 36: // '$'
		case 37: // '%'
		case 38: // '&'
		case 39: // '\''
		case 40: // '('
		case 41: // ')'
		case 42: // '*'
		case 43: // '+'
		case 44: // ','
		case 45: // '-'
		case 46: // '.'
		case 47: // '/'
		{
			_isLastChunk = true;
			int len = tag - 32;
			_chunkLength = 0;
			byte data[] = new byte[len];
			for (int i = 0; i < len; i++)
				data[i] = (byte)read();

			return data;
		}

		case 52: // '4'
		case 53: // '5'
		case 54: // '6'
		case 55: // '7'
		{
			_isLastChunk = true;
			int len = (tag - 52) * 256 + read();
			_chunkLength = 0;
			byte buffer[] = new byte[len];
			for (int i = 0; i < len; i++)
				buffer[i] = (byte)read();

			return buffer;
		}

		case 85: // 'U'
		{
			String type = readType();
			return findSerializerFactory().readList(this, -1, type);
		}

		case 87: // 'W'
		{
			return findSerializerFactory().readList(this, -1, null);
		}

		case 86: // 'V'
		{
			String type = readType();
			int length = readInt();
			Deserializer reader = findSerializerFactory().getListDeserializer(type, null);
			return reader.readLengthList(this, length);
		}

		case 88: // 'X'
		{
			int length = readInt();
			Deserializer reader = findSerializerFactory().getListDeserializer(null, null);
			return reader.readLengthList(this, length);
		}

		case 112: // 'p'
		case 113: // 'q'
		case 114: // 'r'
		case 115: // 's'
		case 116: // 't'
		case 117: // 'u'
		case 118: // 'v'
		case 119: // 'w'
		{
			String type = readType();
			int length = tag - 112;
			Deserializer reader = findSerializerFactory().getListDeserializer(type, null);
			return reader.readLengthList(this, length);
		}

		case 120: // 'x'
		case 121: // 'y'
		case 122: // 'z'
		case 123: // '{'
		case 124: // '|'
		case 125: // '}'
		case 126: // '~'
		case 127: // '\177'
		{
			int length = tag - 120;
			Deserializer reader = findSerializerFactory().getListDeserializer(null, null);
			return reader.readLengthList(this, length);
		}

		case 72: // 'H'
		{
			return findSerializerFactory().readMap(this, null);
		}

		case 77: // 'M'
		{
			String type = readType();
			return findSerializerFactory().readMap(this, type);
		}

		case 67: // 'C'
		{
			readObjectDefinition(null);
			return readObject();
		}

		case 96: // '`'
		case 97: // 'a'
		case 98: // 'b'
		case 99: // 'c'
		case 100: // 'd'
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
		{
			int ref = tag - 96;
			if (_classDefs.size() <= ref)
			{
				throw error((new StringBuilder()).append("No classes defined at reference '").append(Integer.toHexString(tag)).append("'").toString());
			} else
			{
				ObjectDefinition def = (ObjectDefinition)_classDefs.get(ref);
				return readObjectInstance(null, def);
			}
		}

		case 79: // 'O'
		{
			int ref = readInt();
			if (_classDefs.size() <= ref)
			{
				throw error((new StringBuilder()).append("Illegal object reference #").append(ref).toString());
			} else
			{
				ObjectDefinition def = (ObjectDefinition)_classDefs.get(ref);
				return readObjectInstance(null, def);
			}
		}

		case 81: // 'Q'
		{
			int ref = readInt();
			return _refs.get(ref);
		}
		}
		if (tag < 0)
			throw new EOFException("readObject: unexpected end of file");
		else
			throw error((new StringBuilder()).append("readObject: unknown code ").append(codeName(tag)).toString());
	}

	private void readObjectDefinition(Class cl)
		throws IOException
	{
		String type = readString();
		int len = readInt();
		SerializerFactory factory = findSerializerFactory();
		Deserializer reader = factory.getObjectDeserializer(type, null);
		Object fields[] = reader.createFields(len);
		String fieldNames[] = new String[len];
		for (int i = 0; i < len; i++)
		{
			String name = readString();
			fields[i] = reader.createField(name);
			fieldNames[i] = name;
		}

		ObjectDefinition def = new ObjectDefinition(type, reader, fields, fieldNames);
		_classDefs.add(def);
	}

	private Object readObjectInstance(Class cl, ObjectDefinition def)
		throws IOException
	{
		String type = def.getType();
		Deserializer reader = def.getReader();
		Object fields[] = def.getFields();
		SerializerFactory factory = findSerializerFactory();
		if (cl != reader.getType() && cl != null)
		{
			reader = factory.getObjectDeserializer(type, cl);
			return reader.readObject(this, def.getFieldNames());
		} else
		{
			return reader.readObject(this, fields);
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
		int code;
		if (_offset < _length)
		{
			code = _buffer[_offset] & 0xff;
		} else
		{
			code = read();
			if (code >= 0)
				_offset--;
		}
		return code < 0 || code == 90;
	}

	public void readEnd()
		throws IOException
	{
		int code = _offset >= _length ? read() : _buffer[_offset++] & 0xff;
		if (code == 90)
			return;
		if (code < 0)
			throw error("unexpected end of file");
		else
			throw error((new StringBuilder()).append("unknown code:").append(codeName(code)).toString());
	}

	public void readMapEnd()
		throws IOException
	{
		int code = _offset >= _length ? read() : _buffer[_offset++] & 0xff;
		if (code != 90)
			throw error((new StringBuilder()).append("expected end of map ('Z') at '").append(codeName(code)).append("'").toString());
		else
			return;
	}

	public void readListEnd()
		throws IOException
	{
		int code = _offset >= _length ? read() : _buffer[_offset++] & 0xff;
		if (code != 90)
			throw error((new StringBuilder()).append("expected end of list ('Z') at '").append(codeName(code)).append("'").toString());
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
		_refs.clear();
	}

	public void reset()
	{
		resetReferences();
		_classDefs.clear();
		_types.clear();
	}

	public void resetBuffer()
	{
		int offset = _offset;
		_offset = 0;
		int length = _length;
		_length = 0;
		if (length > 0 && offset != length)
			throw new IllegalStateException((new StringBuilder()).append("offset=").append(offset).append(" length=").append(length).toString());
		else
			return;
	}

	public Object readStreamingObject()
		throws IOException
	{
		if (_refs != null)
			_refs.clear();
		return readObject();
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
		int code = _offset >= _length ? read() : _buffer[_offset++] & 0xff;
		_offset--;
		int ref;
		switch (code)
		{
		case 0: // '\0'
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
		case 25: // '\031'
		case 26: // '\032'
		case 27: // '\033'
		case 28: // '\034'
		case 29: // '\035'
		case 30: // '\036'
		case 31: // '\037'
		case 48: // '0'
		case 49: // '1'
		case 50: // '2'
		case 51: // '3'
		case 82: // 'R'
		case 83: // 'S'
			String type = readString();
			if (_types == null)
				_types = new ArrayList();
			_types.add(type);
			return type;

		case 32: // ' '
		case 33: // '!'
		case 34: // '"'
		case 35: // '#'
		case 36: // '$'
		case 37: // '%'
		case 38: // '&'
		case 39: // '\''
		case 40: // '('
		case 41: // ')'
		case 42: // '*'
		case 43: // '+'
		case 44: // ','
		case 45: // '-'
		case 46: // '.'
		case 47: // '/'
		case 52: // '4'
		case 53: // '5'
		case 54: // '6'
		case 55: // '7'
		case 56: // '8'
		case 57: // '9'
		case 58: // ':'
		case 59: // ';'
		case 60: // '<'
		case 61: // '='
		case 62: // '>'
		case 63: // '?'
		case 64: // '@'
		case 65: // 'A'
		case 66: // 'B'
		case 67: // 'C'
		case 68: // 'D'
		case 69: // 'E'
		case 70: // 'F'
		case 71: // 'G'
		case 72: // 'H'
		case 73: // 'I'
		case 74: // 'J'
		case 75: // 'K'
		case 76: // 'L'
		case 77: // 'M'
		case 78: // 'N'
		case 79: // 'O'
		case 80: // 'P'
		case 81: // 'Q'
		default:
			ref = readInt();
			break;
		}
		if (_types.size() <= ref)
			throw new IndexOutOfBoundsException((new StringBuilder()).append("type ref #").append(ref).append(" is greater than the number of valid types (").append(_types.size()).append(")").toString());
		else
			return (String)_types.get(ref);
	}

	public int readLength()
		throws IOException
	{
		throw new UnsupportedOperationException();
	}

	private int parseInt()
		throws IOException
	{
		int offset = _offset;
		if (offset + 3 < _length)
		{
			byte buffer[] = _buffer;
			int b32 = buffer[offset + 0] & 0xff;
			int b24 = buffer[offset + 1] & 0xff;
			int b16 = buffer[offset + 2] & 0xff;
			int b8 = buffer[offset + 3] & 0xff;
			_offset = offset + 4;
			return (b32 << 24) + (b24 << 16) + (b16 << 8) + b8;
		} else
		{
			int b32 = read();
			int b24 = read();
			int b16 = read();
			int b8 = read();
			return (b32 << 24) + (b24 << 16) + (b16 << 8) + b8;
		}
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
		long bits = parseLong();
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
			int code = _offset >= _length ? read() : _buffer[_offset++] & 0xff;
			switch (code)
			{
			case 82: // 'R'
				_isLastChunk = false;
				_chunkLength = (read() << 8) + read();
				break;

			case 83: // 'S'
				_isLastChunk = true;
				_chunkLength = (read() << 8) + read();
				break;

			case 0: // '\0'
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
			case 25: // '\031'
			case 26: // '\032'
			case 27: // '\033'
			case 28: // '\034'
			case 29: // '\035'
			case 30: // '\036'
			case 31: // '\037'
				_isLastChunk = true;
				_chunkLength = code - 0;
				break;

			case 48: // '0'
			case 49: // '1'
			case 50: // '2'
			case 51: // '3'
				_isLastChunk = true;
				_chunkLength = (code - 48) * 256 + read();
				break;

			case 32: // ' '
			case 33: // '!'
			case 34: // '"'
			case 35: // '#'
			case 36: // '$'
			case 37: // '%'
			case 38: // '&'
			case 39: // '\''
			case 40: // '('
			case 41: // ')'
			case 42: // '*'
			case 43: // '+'
			case 44: // ','
			case 45: // '-'
			case 46: // '.'
			case 47: // '/'
			case 52: // '4'
			case 53: // '5'
			case 54: // '6'
			case 55: // '7'
			case 56: // '8'
			case 57: // '9'
			case 58: // ':'
			case 59: // ';'
			case 60: // '<'
			case 61: // '='
			case 62: // '>'
			case 63: // '?'
			case 64: // '@'
			case 65: // 'A'
			case 66: // 'B'
			case 67: // 'C'
			case 68: // 'D'
			case 69: // 'E'
			case 70: // 'F'
			case 71: // 'G'
			case 72: // 'H'
			case 73: // 'I'
			case 74: // 'J'
			case 75: // 'K'
			case 76: // 'L'
			case 77: // 'M'
			case 78: // 'N'
			case 79: // 'O'
			case 80: // 'P'
			case 81: // 'Q'
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
		int ch = _offset >= _length ? read() : _buffer[_offset++] & 0xff;
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
			case 65: // 'A'
				_isLastChunk = false;
				_chunkLength = (read() << 8) + read();
				break;

			case 66: // 'B'
				_isLastChunk = true;
				_chunkLength = (read() << 8) + read();
				break;

			case 32: // ' '
			case 33: // '!'
			case 34: // '"'
			case 35: // '#'
			case 36: // '$'
			case 37: // '%'
			case 38: // '&'
			case 39: // '\''
			case 40: // '('
			case 41: // ')'
			case 42: // '*'
			case 43: // '+'
			case 44: // ','
			case 45: // '-'
			case 46: // '.'
			case 47: // '/'
				_isLastChunk = true;
				_chunkLength = code - 32;
				break;

			case 52: // '4'
			case 53: // '5'
			case 54: // '6'
			case 55: // '7'
				_isLastChunk = true;
				_chunkLength = (code - 52) * 256 + read();
				break;

			case 48: // '0'
			case 49: // '1'
			case 50: // '2'
			case 51: // '3'
			case 56: // '8'
			case 57: // '9'
			case 58: // ':'
			case 59: // ';'
			case 60: // '<'
			case 61: // '='
			case 62: // '>'
			case 63: // '?'
			case 64: // '@'
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

		case 65: // 'A'
		case 66: // 'B'
			_isLastChunk = tag == 66;
			_chunkLength = (read() << 8) + read();
			break;

		case 32: // ' '
		case 33: // '!'
		case 34: // '"'
		case 35: // '#'
		case 36: // '$'
		case 37: // '%'
		case 38: // '&'
		case 39: // '\''
		case 40: // '('
		case 41: // ')'
		case 42: // '*'
		case 43: // '+'
		case 44: // ','
		case 45: // '-'
		case 46: // '.'
		case 47: // '/'
			_isLastChunk = true;
			_chunkLength = tag - 32;
			break;

		case 52: // '4'
		case 53: // '5'
		case 54: // '6'
		case 55: // '7'
			_isLastChunk = true;
			_chunkLength = (tag - 52) * 256 + read();
			break;

		case 48: // '0'
		case 49: // '1'
		case 50: // '2'
		case 51: // '3'
		case 56: // '8'
		case 57: // '9'
		case 58: // ':'
		case 59: // ';'
		case 60: // '<'
		case 61: // '='
		case 62: // '>'
		case 63: // '?'
		case 64: // '@'
		case 67: // 'C'
		case 68: // 'D'
		case 69: // 'E'
		case 70: // 'F'
		case 71: // 'G'
		case 72: // 'H'
		case 73: // 'I'
		case 74: // 'J'
		case 75: // 'K'
		case 76: // 'L'
		case 77: // 'M'
		default:
			throw expect("binary", tag);
		}
		return new ReadInputStream();
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
				case 65: // 'A'
					_isLastChunk = false;
					_chunkLength = (read() << 8) + read();
					break;

				case 66: // 'B'
					_isLastChunk = true;
					_chunkLength = (read() << 8) + read();
					break;

				case 32: // ' '
				case 33: // '!'
				case 34: // '"'
				case 35: // '#'
				case 36: // '$'
				case 37: // '%'
				case 38: // '&'
				case 39: // '\''
				case 40: // '('
				case 41: // ')'
				case 42: // '*'
				case 43: // '+'
				case 44: // ','
				case 45: // '-'
				case 46: // '.'
				case 47: // '/'
					_isLastChunk = true;
					_chunkLength = code - 32;
					break;

				case 52: // '4'
				case 53: // '5'
				case 54: // '6'
				case 55: // '7'
					_isLastChunk = true;
					_chunkLength = (code - 52) * 256 + read();
					break;

				case 48: // '0'
				case 49: // '1'
				case 50: // '2'
				case 51: // '3'
				case 56: // '8'
				case 57: // '9'
				case 58: // ':'
				case 59: // ';'
				case 60: // '<'
				case 61: // '='
				case 62: // '>'
				case 63: // '?'
				case 64: // '@'
				default:
					throw expect("byte[]", code);
				}
			} while (true);
			int sublen = _chunkLength;
			if (length < sublen)
				sublen = length;
			if (_length <= _offset && !readBuffer())
				return -1;
			if (_length - _offset < sublen)
				sublen = _length - _offset;
			System.arraycopy(_buffer, _offset, buffer, offset, sublen);
			_offset += sublen;
			offset += sublen;
			readLength += sublen;
			length -= sublen;
			_chunkLength -= sublen;
		}
		return readLength;
	}

	public final int read()
		throws IOException
	{
		if (_length <= _offset && !readBuffer())
			return -1;
		else
			return _buffer[_offset++] & 0xff;
	}

	protected void unread()
	{
		if (_offset <= 0)
		{
			throw new IllegalStateException();
		} else
		{
			_offset--;
			return;
		}
	}

	private final boolean readBuffer()
		throws IOException
	{
		byte buffer[] = _buffer;
		int offset = _offset;
		int length = _length;
		if (offset < length)
		{
			System.arraycopy(buffer, offset, buffer, 0, length - offset);
			offset = length - offset;
		} else
		{
			offset = 0;
		}
		int len = _is.read(buffer, offset, 256 - offset);
		if (len <= 0)
		{
			_length = offset;
			_offset = 0;
			return offset > 0;
		} else
		{
			_length = offset + len;
			_offset = 0;
			return true;
		}
	}

	public Reader getReader()
	{
		return null;
	}

	protected IOException expect(String expect, int ch)
		throws IOException
	{
		if (ch < 0)
			return error((new StringBuilder()).append("expected ").append(expect).append(" at end of file").toString());
		_offset--;
		String context;
		Object obj;
		int offset = _offset;
		context = buildDebugContext(_buffer, 0, _length, offset);
		obj = readObject();
		if (obj != null)
			return error((new StringBuilder()).append("expected ").append(expect).append(" at 0x").append(Integer.toHexString(ch & 0xff)).append(" ").append(obj.getClass().getName()).append(" (").append(obj).append(")").append("\n  ").append(context).append("").toString());
		return error((new StringBuilder()).append("expected ").append(expect).append(" at 0x").append(Integer.toHexString(ch & 0xff)).append(" null").toString());
		Exception e;
		e;
		log.log(Level.FINE, e.toString(), e);
		return error((new StringBuilder()).append("expected ").append(expect).append(" at 0x").append(Integer.toHexString(ch & 0xff)).toString());
	}

	private String buildDebugContext(byte buffer[], int offset, int length, int errorOffset)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i = 0; i < errorOffset; i++)
		{
			int ch = buffer[offset + i];
			addDebugChar(sb, ch);
		}

		sb.append("] ");
		addDebugChar(sb, buffer[offset + errorOffset]);
		sb.append(" [");
		for (int i = errorOffset + 1; i < length; i++)
		{
			int ch = buffer[offset + i];
			addDebugChar(sb, ch);
		}

		sb.append("]");
		return sb.toString();
	}

	private void addDebugChar(StringBuilder sb, int ch)
	{
		if (ch >= 32 && ch < 127)
			sb.append((char)ch);
		else
		if (ch == 10)
			sb.append((char)ch);
		else
			sb.append(String.format("\\x%02x", new Object[] {
				Integer.valueOf(ch & 0xff)
			}));
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

	public void close()
		throws IOException
	{
		InputStream is = _is;
		_is = null;
		if (_isCloseStreamOnClose && is != null)
			is.close();
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
