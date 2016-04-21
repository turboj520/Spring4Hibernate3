// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HessianFactory.java

package com.autohome.hessian.io;

import com.autohome.hessian.util.HessianFreeList;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

// Referenced classes of package com.autohome.hessian.io:
//			SerializerFactory, Hessian2Input, Hessian2StreamingInput, HessianInput, 
//			Hessian2Output, Hessian2StreamingOutput, HessianOutput, HessianDebugOutputStream

public class HessianFactory
{

	public static final Logger log = Logger.getLogger(com/autohome/hessian/io/HessianFactory.getName());
	private SerializerFactory _serializerFactory;
	private SerializerFactory _defaultSerializerFactory;
	private final HessianFreeList _freeHessian2Output = new HessianFreeList(32);
	private final HessianFreeList _freeHessianOutput = new HessianFreeList(32);
	private final HessianFreeList _freeHessian2Input = new HessianFreeList(32);
	private final HessianFreeList _freeHessianInput = new HessianFreeList(32);

	public HessianFactory()
	{
		_defaultSerializerFactory = SerializerFactory.createDefault();
		_serializerFactory = _defaultSerializerFactory;
	}

	public void setSerializerFactory(SerializerFactory factory)
	{
		_serializerFactory = factory;
	}

	public SerializerFactory getSerializerFactory()
	{
		if (_serializerFactory == _defaultSerializerFactory)
			_serializerFactory = new SerializerFactory();
		return _serializerFactory;
	}

	public Hessian2Input createHessian2Input(InputStream is)
	{
		Hessian2Input in = new Hessian2Input(is);
		in.setSerializerFactory(_serializerFactory);
		return in;
	}

	public void freeHessian2Input(Hessian2Input hessian2input)
	{
	}

	public Hessian2StreamingInput createHessian2StreamingInput(InputStream is)
	{
		Hessian2StreamingInput in = new Hessian2StreamingInput(is);
		in.setSerializerFactory(_serializerFactory);
		return in;
	}

	public void freeHessian2StreamingInput(Hessian2StreamingInput hessian2streaminginput)
	{
	}

	public HessianInput createHessianInput(InputStream is)
	{
		return new HessianInput(is);
	}

	public Hessian2Output createHessian2Output(OutputStream os)
	{
		Hessian2Output out = (Hessian2Output)_freeHessian2Output.allocate();
		if (out != null)
			out.init(os);
		else
			out = new Hessian2Output(os);
		out.setSerializerFactory(_serializerFactory);
		return out;
	}

	public void freeHessian2Output(Hessian2Output out)
	{
		if (out == null)
		{
			return;
		} else
		{
			out.free();
			_freeHessian2Output.free(out);
			return;
		}
	}

	public Hessian2StreamingOutput createHessian2StreamingOutput(OutputStream os)
	{
		Hessian2Output out = createHessian2Output(os);
		return new Hessian2StreamingOutput(out);
	}

	public void freeHessian2StreamingOutput(Hessian2StreamingOutput out)
	{
		if (out == null)
		{
			return;
		} else
		{
			freeHessian2Output(out.getHessian2Output());
			return;
		}
	}

	public HessianOutput createHessianOutput(OutputStream os)
	{
		return new HessianOutput(os);
	}

	public OutputStream createHessian2DebugOutput(OutputStream os, Logger log, Level level)
	{
		HessianDebugOutputStream out = new HessianDebugOutputStream(os, log, level);
		out.startTop2();
		return out;
	}

}
