// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HessianDebugInputStream.java

package com.autohome.hessian.io;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

// Referenced classes of package com.autohome.hessian.io:
//			HessianDebugState

public class HessianDebugInputStream extends InputStream
{
	static class LogWriter extends Writer
	{

		private Logger _log;
		private Level _level;
		private StringBuilder _sb;

		public void write(char ch)
		{
			if (ch == '\n' && _sb.length() > 0)
			{
				_log.log(_level, _sb.toString());
				_sb.setLength(0);
			} else
			{
				_sb.append(ch);
			}
		}

		public void write(char buffer[], int offset, int length)
		{
			for (int i = 0; i < length; i++)
			{
				char ch = buffer[offset + i];
				if (ch == '\n' && _sb.length() > 0)
				{
					_log.log(_level, _sb.toString());
					_sb.setLength(0);
				} else
				{
					_sb.append(ch);
				}
			}

		}

		public void flush()
		{
		}

		public void close()
		{
		}

		LogWriter(Logger log, Level level)
		{
			_sb = new StringBuilder();
			_log = log;
			_level = level;
		}
	}


	private InputStream _is;
	private HessianDebugState _state;

	public HessianDebugInputStream(InputStream is, OutputStream os)
	{
		this(is, new PrintWriter(os));
	}

	public HessianDebugInputStream(InputStream is, PrintWriter dbg)
	{
		_is = is;
		if (dbg == null)
			dbg = new PrintWriter(System.out);
		_state = new HessianDebugState(dbg);
	}

	public HessianDebugInputStream(InputStream is, Logger log, Level level)
	{
		this(is, new PrintWriter(new LogWriter(log, level)));
	}

	public void startTop2()
	{
		_state.startTop2();
	}

	public void startData1()
	{
		_state.startData1();
	}

	public void startStreaming()
	{
		_state.startStreaming();
	}

	public void setDepth(int depth)
	{
		_state.setDepth(depth);
	}

	public int read()
		throws IOException
	{
		InputStream is = _is;
		if (is == null)
		{
			return -1;
		} else
		{
			int ch = is.read();
			_state.next(ch);
			return ch;
		}
	}

	public void close()
		throws IOException
	{
		InputStream is = _is;
		_is = null;
		if (is != null)
			is.close();
		_state.println();
	}
}
