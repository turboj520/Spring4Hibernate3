// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   MuxOutputStream.java

package com.autohome.hessian.mux;

import java.io.IOException;
import java.io.OutputStream;

// Referenced classes of package com.autohome.hessian.mux:
//			MuxServer

public class MuxOutputStream extends OutputStream
{

	private MuxServer server;
	private int channel;
	private OutputStream os;

	public MuxOutputStream()
	{
	}

	protected void init(MuxServer server, int channel)
		throws IOException
	{
		this.server = server;
		this.channel = channel;
		os = null;
	}

	protected OutputStream getOutputStream()
		throws IOException
	{
		if (os == null && server != null)
			os = server.writeChannel(channel);
		return os;
	}

	public int getChannel()
	{
		return channel;
	}

	public void writeURL(String url)
		throws IOException
	{
		writeUTF(85, url);
	}

	public void write(int ch)
		throws IOException
	{
		OutputStream os = getOutputStream();
		os.write(68);
		os.write(0);
		os.write(1);
		os.write(ch);
	}

	public void write(byte buffer[], int offset, int length)
		throws IOException
	{
		OutputStream os = getOutputStream();
		for (; length > 32768; length -= 32768)
		{
			os.write(68);
			os.write(128);
			os.write(0);
			os.write(buffer, offset, 32768);
			offset += 32768;
		}

		os.write(68);
		os.write(length >> 8);
		os.write(length);
		os.write(buffer, offset, length);
	}

	public void yield()
		throws IOException
	{
		OutputStream os = this.os;
		this.os = null;
		if (os != null)
			server.yield(channel);
	}

	public void flush()
		throws IOException
	{
		OutputStream os = this.os;
		this.os = null;
		if (os != null)
			server.flush(channel);
	}

	public void close()
		throws IOException
	{
		if (this.server != null)
		{
			OutputStream os = getOutputStream();
			this.os = null;
			MuxServer server = this.server;
			this.server = null;
			server.close(channel);
		}
	}

	protected void writeUTF(int code, String string)
		throws IOException
	{
		OutputStream os = getOutputStream();
		os.write(code);
		int charLength = string.length();
		int length = 0;
		for (int i = 0; i < charLength; i++)
		{
			char ch = string.charAt(i);
			if (ch < '\200')
			{
				length++;
				continue;
			}
			if (ch < '\u0800')
				length += 2;
			else
				length += 3;
		}

		os.write(length >> 8);
		os.write(length);
		for (int i = 0; i < length; i++)
		{
			char ch = string.charAt(i);
			if (ch < '\200')
			{
				os.write(ch);
				continue;
			}
			if (ch < '\u0800')
			{
				os.write(192 + (ch >> 6) & 0x1f);
				os.write(128 + (ch & 0x3f));
			} else
			{
				os.write(224 + (ch >> 12) & 0xf);
				os.write(128 + (ch >> 6 & 0x3f));
				os.write(128 + (ch & 0x3f));
			}
		}

	}
}
