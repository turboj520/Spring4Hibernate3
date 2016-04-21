// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Hessian2StreamingInput.java

package com.autohome.hessian.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

// Referenced classes of package com.autohome.hessian.io:
//			Hessian2Input, SerializerFactory

public class Hessian2StreamingInput
{
	static class StreamingInputStream extends InputStream
	{

		private InputStream _is;
		private int _length;
		private boolean _isPacketEnd;

		public boolean isDataAvailable()
		{
			return _is != null && _is.available() > 0;
			IOException e;
			e;
			Hessian2StreamingInput.log.log(Level.FINER, e.toString(), e);
			return true;
		}

		public boolean startPacket()
			throws IOException
		{
			do
				_isPacketEnd = false;
			while ((_length = readChunkLength(_is)) == 0);
			return _length > 0;
		}

		public void endPacket()
			throws IOException
		{
			do
			{
				if (_isPacketEnd)
					break;
				if (_length <= 0)
					_length = readChunkLength(_is);
				if (_length > 0)
					_is.skip(_length);
			} while (true);
		}

		public int read()
			throws IOException
		{
			if (_isPacketEnd)
				throw new IllegalStateException();
			InputStream is = _is;
			if (_length == 0)
			{
				_length = readChunkLength(is);
				if (_length <= 0)
					return -1;
			}
			_length--;
			return is.read();
		}

		public int read(byte buffer[], int offset, int length)
			throws IOException
		{
			if (_isPacketEnd)
				throw new IllegalStateException();
			InputStream is = _is;
			if (_length <= 0)
			{
				_length = readChunkLength(is);
				if (_length <= 0)
					return -1;
			}
			int sublen = _length;
			if (length < sublen)
				sublen = length;
			sublen = is.read(buffer, offset, sublen);
			if (sublen < 0)
			{
				return -1;
			} else
			{
				_length -= sublen;
				return sublen;
			}
		}

		private int readChunkLength(InputStream is)
			throws IOException
		{
			if (_isPacketEnd)
				return -1;
			int length = 0;
			int code = is.read();
			if (code < 0)
			{
				_isPacketEnd = true;
				return -1;
			}
			if ((code & 0x80) != 128)
			{
				int len = 256;
				StringBuilder sb = new StringBuilder();
				int ch;
				for (; len-- > 0 && is.available() > 0 && (ch = is.read()) >= 0; sb.append((char)ch));
				throw new IllegalStateException((new StringBuilder()).append("WebSocket binary must begin with a 0x80 packet at 0x").append(Integer.toHexString(code)).append(" (").append((char)code).append(")").append(" context[").append(sb).append("]").toString());
			}
			while ((code = is.read()) >= 0) 
			{
				length = (length << 7) + (code & 0x7f);
				if ((code & 0x80) == 0)
				{
					if (length == 0)
						_isPacketEnd = true;
					return length;
				}
			}
			_isPacketEnd = true;
			return -1;
		}

		StreamingInputStream(InputStream is)
		{
			_is = is;
		}
	}


	private static final Logger log = Logger.getLogger(com/autohome/hessian/io/Hessian2StreamingInput.getName());
	private StreamingInputStream _is;
	private Hessian2Input _in;

	public Hessian2StreamingInput(InputStream is)
	{
		_is = new StreamingInputStream(is);
		_in = new Hessian2Input(_is);
	}

	public void setSerializerFactory(SerializerFactory factory)
	{
		_in.setSerializerFactory(factory);
	}

	public boolean isDataAvailable()
	{
		StreamingInputStream is = _is;
		return is != null && is.isDataAvailable();
	}

	public Hessian2Input startPacket()
		throws IOException
	{
		if (_is.startPacket())
		{
			_in.resetReferences();
			_in.resetBuffer();
			return _in;
		} else
		{
			return null;
		}
	}

	public void endPacket()
		throws IOException
	{
		_is.endPacket();
		_in.resetBuffer();
	}

	public Hessian2Input getHessianInput()
	{
		return _in;
	}

	public Object readObject()
		throws IOException
	{
		_is.startPacket();
		Object obj = _in.readStreamingObject();
		_is.endPacket();
		return obj;
	}

	public void close()
		throws IOException
	{
		_in.close();
	}


}
