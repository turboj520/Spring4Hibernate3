// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   MuxInputStream.java

package com.autohome.hessian.mux;

import java.io.IOException;
import java.io.InputStream;

// Referenced classes of package com.autohome.hessian.mux:
//			MuxServer

public class MuxInputStream extends InputStream
{

	private MuxServer server;
	protected InputStream is;
	private int channel;
	private String url;
	private int chunkLength;

	public MuxInputStream()
	{
	}

	protected void init(MuxServer server, int channel)
		throws IOException
	{
		this.server = server;
		this.channel = channel;
		url = null;
		chunkLength = 0;
	}

	protected InputStream getInputStream()
		throws IOException
	{
		if (is == null && server != null)
			is = server.readChannel(channel);
		return is;
	}

	void setInputStream(InputStream is)
	{
		this.is = is;
	}

	public int getChannel()
	{
		return channel;
	}

	public String getURL()
	{
		return url;
	}

	public int read()
		throws IOException
	{
		if (chunkLength <= 0)
		{
			readToData(false);
			if (chunkLength <= 0)
				return -1;
		}
		chunkLength--;
		return is.read();
	}

	public void close()
		throws IOException
	{
		skipToEnd();
	}

	private void skipToEnd()
		throws IOException
	{
		InputStream is = getInputStream();
		if (is == null)
			return;
		if (chunkLength > 0)
			is.skip(chunkLength);
		for (int tag = is.read(); tag >= 0; tag = is.read())
			switch (tag)
			{
			case 89: // 'Y'
				server.freeReadLock();
				this.is = is = server.readChannel(channel);
				if (is == null)
				{
					server = null;
					return;
				}
				break;

			case 81: // 'Q'
				server.freeReadLock();
				this.is = null;
				server = null;
				return;

			case -1: 
				server.freeReadLock();
				this.is = null;
				server = null;
				return;

			default:
				int length = (is.read() << 8) + is.read();
				is.skip(length);
				break;
			}

	}

	void readToData(boolean returnOnYield)
		throws IOException
	{
		InputStream is = getInputStream();
		if (is == null)
			return;
		for (int tag = is.read(); tag >= 0; tag = is.read())
			switch (tag)
			{
			case 89: // 'Y'
				server.freeReadLock();
				if (returnOnYield)
					return;
				server.readChannel(channel);
				break;

			case 81: // 'Q'
				server.freeReadLock();
				this.is = null;
				server = null;
				return;

			case 85: // 'U'
				url = readUTF();
				break;

			case 68: // 'D'
				chunkLength = (is.read() << 8) + is.read();
				return;

			default:
				readTag(tag);
				break;
			}

	}

	protected void readTag(int tag)
		throws IOException
	{
		int length = (is.read() << 8) + is.read();
		is.skip(length);
	}

	protected String readUTF()
		throws IOException
	{
		int len = (is.read() << 8) + is.read();
		StringBuffer sb = new StringBuffer();
		while (len > 0) 
		{
			int d1 = is.read();
			if (d1 < 0)
				return sb.toString();
			if (d1 < 128)
			{
				len--;
				sb.append((char)d1);
			} else
			if ((d1 & 0xe0) == 192)
			{
				len -= 2;
				sb.append(((d1 & 0x1f) << 6) + (is.read() & 0x3f));
			} else
			if ((d1 & 0xf0) == 224)
			{
				len -= 3;
				sb.append(((d1 & 0xf) << 12) + ((is.read() & 0x3f) << 6) + (is.read() & 0x3f));
			} else
			{
				throw new IOException("utf-8 encoding error");
			}
		}
		return sb.toString();
	}
}
