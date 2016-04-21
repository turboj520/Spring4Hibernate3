// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HessianDebugOutputStream.java

package com.autohome.hessian.io;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

// Referenced classes of package com.autohome.hessian.io:
//			HessianDebugState

public class HessianDebugOutputStream extends OutputStream
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


	private OutputStream _os;
	private HessianDebugState _state;

	public HessianDebugOutputStream(OutputStream os, PrintWriter dbg)
	{
		_os = os;
		_state = new HessianDebugState(dbg);
	}

	public HessianDebugOutputStream(OutputStream os, Logger log, Level level)
	{
		this(os, new PrintWriter(new LogWriter(log, level)));
	}

	public void startTop2()
	{
		_state.startTop2();
	}

	public void startStreaming()
	{
		_state.startStreaming();
	}

	public void write(int ch)
		throws IOException
	{
		ch &= 0xff;
		_os.write(ch);
		_state.next(ch);
	}

	public void flush()
		throws IOException
	{
		_os.flush();
	}

	public void close()
		throws IOException
	{
		OutputStream os = _os;
		_os = null;
		if (os != null)
		{
			_state.next(-1);
			os.close();
		}
		_state.println();
	}
}
