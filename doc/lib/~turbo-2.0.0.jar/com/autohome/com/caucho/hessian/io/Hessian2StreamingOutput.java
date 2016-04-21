// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Hessian2StreamingOutput.java

package com.autohome.com.caucho.hessian.io;

import java.io.IOException;
import java.io.OutputStream;

// Referenced classes of package com.autohome.com.caucho.hessian.io:
//			Hessian2Output

public class Hessian2StreamingOutput
{

	private Hessian2Output _out;

	public Hessian2StreamingOutput(OutputStream os)
	{
		_out = new Hessian2Output(os);
	}

	public void setCloseStreamOnClose(boolean isClose)
	{
		_out.setCloseStreamOnClose(isClose);
	}

	public boolean isCloseStreamOnClose()
	{
		return _out.isCloseStreamOnClose();
	}

	public void writeObject(Object object)
		throws IOException
	{
		_out.writeStreamingObject(object);
	}

	public void flush()
		throws IOException
	{
		_out.flush();
	}

	public void close()
		throws IOException
	{
		_out.close();
	}
}
