// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Hessian2StreamingInput.java

package com.autohome.com.caucho.hessian.io;

import java.io.IOException;
import java.io.InputStream;

// Referenced classes of package com.autohome.com.caucho.hessian.io:
//			Hessian2Input, HessianProtocolException

public class Hessian2StreamingInput
{
	static class StreamingInputStream extends InputStream
	{

		private InputStream _is;
		private int _length;

		public int read()
			throws IOException
		{
			InputStream is = _is;
			int d1;
			int d2;
			for (; _length == 0; _length = (d1 << 8) + d2)
			{
				int code = is.read();
				if (code < 0)
					return -1;
				if (code != 112 && code != 80)
					throw new HessianProtocolException((new StringBuilder()).append("expected streaming packet at 0x").append(Integer.toHexString(code & 0xff)).toString());
				d1 = is.read();
				d2 = is.read();
				if (d2 < 0)
					return -1;
			}

			_length--;
			return is.read();
		}

		public int read(byte buffer[], int offset, int length)
			throws IOException
		{
			InputStream is = _is;
			int d1;
			int d2;
			for (; _length == 0; _length = (d1 << 8) + d2)
			{
				int code = is.read();
				if (code < 0)
					return -1;
				if (code != 112 && code != 80)
					throw new HessianProtocolException((new StringBuilder()).append("expected streaming packet at 0x").append(Integer.toHexString(code & 0xff)).append(" (").append((char)code).append(")").toString());
				d1 = is.read();
				d2 = is.read();
				if (d2 < 0)
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

		StreamingInputStream(InputStream is)
		{
			_is = is;
		}
	}


	private Hessian2Input _in;

	public Hessian2StreamingInput(InputStream is)
	{
		_in = new Hessian2Input(new StreamingInputStream(is));
	}

	public Object readObject()
		throws IOException
	{
		return _in.readStreamingObject();
	}

	public void close()
		throws IOException
	{
		_in.close();
	}
}
