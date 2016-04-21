// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractSerializer.java

package com.autohome.hessian.io;

import com.autohome.hessian.HessianException;
import java.io.IOException;
import java.util.logging.Logger;

// Referenced classes of package com.autohome.hessian.io:
//			Serializer, AbstractHessianOutput

public abstract class AbstractSerializer
	implements Serializer
{
	static final class NullSerializer extends AbstractSerializer
	{

		public void writeObject(Object obj, AbstractHessianOutput out)
			throws IOException
		{
			throw new IllegalStateException(getClass().getName());
		}

		NullSerializer()
		{
		}
	}


	public static final NullSerializer NULL = new NullSerializer();
	protected static final Logger log = Logger.getLogger(com/autohome/hessian/io/AbstractSerializer.getName());

	public AbstractSerializer()
	{
	}

	public void writeObject(Object obj, AbstractHessianOutput out)
		throws IOException
	{
		if (out.addRef(obj))
			return;
		try
		{
			Object replace = writeReplace(obj);
			if (replace != null)
			{
				out.writeObject(replace);
				out.replaceRef(replace, obj);
				return;
			}
		}
		catch (RuntimeException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new HessianException(e);
		}
		Class cl = getClass(obj);
		int ref = out.writeObjectBegin(cl.getName());
		if (ref < -1)
		{
			writeObject10(obj, out);
		} else
		{
			if (ref == -1)
			{
				writeDefinition20(cl, out);
				out.writeObjectBegin(cl.getName());
			}
			writeInstance(obj, out);
		}
	}

	protected Object writeReplace(Object obj)
	{
		return null;
	}

	protected Class getClass(Object obj)
	{
		return obj.getClass();
	}

	protected void writeObject10(Object obj, AbstractHessianOutput out)
		throws IOException
	{
		throw new UnsupportedOperationException(getClass().getName());
	}

	protected void writeDefinition20(Class cl, AbstractHessianOutput out)
		throws IOException
	{
		throw new UnsupportedOperationException(getClass().getName());
	}

	protected void writeInstance(Object obj, AbstractHessianOutput out)
		throws IOException
	{
		throw new UnsupportedOperationException(getClass().getName());
	}

}
