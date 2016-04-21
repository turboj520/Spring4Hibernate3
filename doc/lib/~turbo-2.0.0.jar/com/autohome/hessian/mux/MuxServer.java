// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   MuxServer.java

package com.autohome.hessian.mux;

import java.io.*;

// Referenced classes of package com.autohome.hessian.mux:
//			MuxInputStream, MuxOutputStream

public class MuxServer
{

	private Object READ_LOCK;
	private Object WRITE_LOCK;
	private InputStream is;
	private OutputStream os;
	private boolean isClient;
	private transient boolean isClosed;
	private boolean inputReady[];
	private boolean isReadLocked;
	private boolean isWriteLocked;

	public MuxServer()
	{
		READ_LOCK = new Object();
		WRITE_LOCK = new Object();
		inputReady = new boolean[4];
	}

	public MuxServer(InputStream is, OutputStream os, boolean isClient)
	{
		READ_LOCK = new Object();
		WRITE_LOCK = new Object();
		inputReady = new boolean[4];
		init(is, os, isClient);
	}

	public void init(InputStream is, OutputStream os, boolean isClient)
	{
		this.is = is;
		this.os = os;
		this.isClient = isClient;
	}

	public InputStream getInputStream()
	{
		return is;
	}

	public OutputStream getOutputStream()
	{
		return os;
	}

	public boolean startCall(MuxInputStream in, MuxOutputStream out)
		throws IOException
	{
		int channel = isClient ? 2 : 3;
		return startCall(channel, in, out);
	}

	public boolean startCall(int channel, MuxInputStream in, MuxOutputStream out)
		throws IOException
	{
		in.init(this, channel);
		out.init(this, channel);
		return true;
	}

	public boolean readRequest(MuxInputStream in, MuxOutputStream out)
		throws IOException
	{
		int channel = isClient ? 3 : 2;
		in.init(this, channel);
		out.init(this, channel);
		if (readChannel(channel) != null)
		{
			in.setInputStream(is);
			in.readToData(false);
			return true;
		} else
		{
			return false;
		}
	}

	OutputStream writeChannel(int channel)
		throws IOException
	{
		while (os != null) 
		{
			boolean canWrite = false;
			synchronized (WRITE_LOCK)
			{
				if (!isWriteLocked)
				{
					isWriteLocked = true;
					canWrite = true;
				} else
				{
					try
					{
						WRITE_LOCK.wait(5000L);
					}
					catch (Exception e) { }
				}
			}
			if (canWrite)
			{
				os.write(67);
				os.write(channel >> 8);
				os.write(channel);
				return os;
			}
		}
		return null;
	}

	void yield(int channel)
		throws IOException
	{
		os.write(89);
		freeWriteLock();
	}

	void flush(int channel)
		throws IOException
	{
		os.write(89);
		os.flush();
		freeWriteLock();
	}

	void close(int channel)
		throws IOException
	{
		if (os != null)
		{
			os.write(81);
			os.flush();
			freeWriteLock();
		}
	}

	void freeWriteLock()
	{
		synchronized (WRITE_LOCK)
		{
			isWriteLocked = false;
			WRITE_LOCK.notifyAll();
		}
	}

	InputStream readChannel(int channel)
		throws IOException
	{
		do
		{
			if (isClosed)
				break;
			if (inputReady[channel])
			{
				inputReady[channel] = false;
				return is;
			}
			boolean canRead = false;
			synchronized (READ_LOCK)
			{
				if (!isReadLocked)
				{
					isReadLocked = true;
					canRead = true;
				} else
				{
					try
					{
						READ_LOCK.wait(5000L);
					}
					catch (Exception e) { }
				}
			}
			if (canRead)
				try
				{
					readData();
				}
				catch (IOException e)
				{
					close();
				}
		} while (true);
		return null;
	}

	boolean getReadLock()
	{
		Object obj = READ_LOCK;
		JVM INSTR monitorenter ;
		if (isReadLocked)
			break MISSING_BLOCK_LABEL_23;
		isReadLocked = true;
		return true;
		try
		{
			READ_LOCK.wait(5000L);
		}
		catch (Exception e) { }
		obj;
		JVM INSTR monitorexit ;
		  goto _L1
		Exception exception;
		exception;
		throw exception;
_L1:
		return false;
	}

	void freeReadLock()
	{
		synchronized (READ_LOCK)
		{
			isReadLocked = false;
			READ_LOCK.notifyAll();
		}
	}

	private void readData()
		throws IOException
	{
		do
		{
			if (isClosed)
				break;
			int code = is.read();
			switch (code)
			{
			case 67: // 'C'
			{
				int channel = (is.read() << 8) + is.read();
				inputReady[channel] = true;
				return;
			}

			case 69: // 'E'
			{
				int channel = (is.read() << 8) + is.read();
				int status = (is.read() << 8) + is.read();
				inputReady[channel] = true;
				return;
			}

			case -1: 
			{
				close();
				return;
			}

			default:
			{
				close();
				return;
			}

			case 9: // '\t'
			case 10: // '\n'
			case 13: // '\r'
			case 32: // ' '
				break;
			}
		} while (true);
	}

	public void close()
		throws IOException
	{
		isClosed = true;
		OutputStream os = this.os;
		this.os = null;
		InputStream is = this.is;
		this.is = null;
		if (os != null)
			os.close();
		if (is != null)
			is.close();
	}
}
