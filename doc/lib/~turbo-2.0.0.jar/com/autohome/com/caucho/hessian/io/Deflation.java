// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Deflation.java

package com.autohome.com.caucho.hessian.io;

import java.io.*;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

// Referenced classes of package com.autohome.com.caucho.hessian.io:
//			HessianEnvelope, Hessian2Output, Hessian2Input

public class Deflation extends HessianEnvelope
{
	static class DeflateInputStream extends InputStream
	{

		private Hessian2Input _in;
		private InputStream _bodyIn;
		private InflaterInputStream _inflateIn;

		public int read()
			throws IOException
		{
			return _inflateIn.read();
		}

		public int read(byte buffer[], int offset, int length)
			throws IOException
		{
			return _inflateIn.read(buffer, offset, length);
		}

		public void close()
			throws IOException
		{
			Hessian2Input in = _in;
			_in = null;
			if (in != null)
			{
				_inflateIn.close();
				_bodyIn.close();
				int len = in.readInt();
				if (len != 0)
					throw new IOException("Unexpected footer");
				in.completeEnvelope();
				in.close();
			}
		}

		DeflateInputStream(Hessian2Input in)
			throws IOException
		{
			_in = in;
			int len = in.readInt();
			if (len != 0)
			{
				throw new IOException("expected no headers");
			} else
			{
				_bodyIn = _in.readInputStream();
				_inflateIn = new InflaterInputStream(_bodyIn);
				return;
			}
		}
	}

	static class DeflateOutputStream extends OutputStream
	{

		private Hessian2Output _out;
		private OutputStream _bodyOut;
		private DeflaterOutputStream _deflateOut;

		public void write(int ch)
			throws IOException
		{
			_deflateOut.write(ch);
		}

		public void write(byte buffer[], int offset, int length)
			throws IOException
		{
			_deflateOut.write(buffer, offset, length);
		}

		public void close()
			throws IOException
		{
			Hessian2Output out = _out;
			_out = null;
			if (out != null)
			{
				_deflateOut.close();
				_bodyOut.close();
				out.writeInt(0);
				out.completeEnvelope();
				out.close();
			}
		}

		DeflateOutputStream(Hessian2Output out)
			throws IOException
		{
			_out = out;
			_out.startEnvelope(com/autohome/com/caucho/hessian/io/Deflation.getName());
			_out.writeInt(0);
			_bodyOut = _out.getBytesOutputStream();
			_deflateOut = new DeflaterOutputStream(_bodyOut);
		}
	}


	public Deflation()
	{
	}

	public Hessian2Output wrap(Hessian2Output out)
		throws IOException
	{
		OutputStream os = new DeflateOutputStream(out);
		Hessian2Output filterOut = new Hessian2Output(os);
		filterOut.setCloseStreamOnClose(true);
		return filterOut;
	}

	public Hessian2Input unwrap(Hessian2Input in)
		throws IOException
	{
		int version = in.readEnvelope();
		String method = in.readMethod();
		if (!method.equals(getClass().getName()))
			throw new IOException((new StringBuilder()).append("expected hessian Envelope method '").append(getClass().getName()).append("' at '").append(method).append("'").toString());
		else
			return unwrapHeaders(in);
	}

	public Hessian2Input unwrapHeaders(Hessian2Input in)
		throws IOException
	{
		InputStream is = new DeflateInputStream(in);
		Hessian2Input filter = new Hessian2Input(is);
		filter.setCloseStreamOnClose(true);
		return filter;
	}
}
