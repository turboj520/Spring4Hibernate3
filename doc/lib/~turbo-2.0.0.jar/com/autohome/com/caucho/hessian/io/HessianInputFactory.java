// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HessianInputFactory.java

package com.autohome.com.caucho.hessian.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

// Referenced classes of package com.autohome.com.caucho.hessian.io:
//			Hessian2Input, HessianInput, AbstractHessianInput, SerializerFactory

public class HessianInputFactory
{

	public static final Logger log = Logger.getLogger(com/autohome/com/caucho/hessian/io/HessianInputFactory.getName());
	private SerializerFactory _serializerFactory;

	public HessianInputFactory()
	{
	}

	public void setSerializerFactory(SerializerFactory factory)
	{
		_serializerFactory = factory;
	}

	public SerializerFactory getSerializerFactory()
	{
		return _serializerFactory;
	}

	public AbstractHessianInput open(InputStream is)
		throws IOException
	{
		int code = is.read();
		int major = is.read();
		int minor = is.read();
		switch (code)
		{
		case 67: // 'C'
		case 82: // 'R'
		case 99: // 'c'
		case 114: // 'r'
			if (major >= 2)
			{
				AbstractHessianInput in = new Hessian2Input(is);
				in.setSerializerFactory(_serializerFactory);
				return in;
			} else
			{
				AbstractHessianInput in = new HessianInput(is);
				in.setSerializerFactory(_serializerFactory);
				return in;
			}
		}
		throw new IOException((new StringBuilder()).append((char)code).append(" is an unknown Hessian message code.").toString());
	}

}
