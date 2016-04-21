// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ArrayDeserializer.java

package com.autohome.hessian.io;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

// Referenced classes of package com.autohome.hessian.io:
//			AbstractListDeserializer, AbstractHessianInput

public class ArrayDeserializer extends AbstractListDeserializer
{

	private Class _componentType;
	private Class _type;

	public ArrayDeserializer(Class componentType)
	{
		_componentType = componentType;
		if (_componentType != null)
			try
			{
				_type = Array.newInstance(_componentType, 0).getClass();
			}
			catch (Exception e) { }
		if (_type == null)
			_type = [Ljava/lang/Object;;
	}

	public Class getType()
	{
		return _type;
	}

	public Object readList(AbstractHessianInput in, int length)
		throws IOException
	{
		if (length >= 0)
		{
			Object data[] = createArray(length);
			in.addRef(((Object) (data)));
			if (_componentType != null)
			{
				for (int i = 0; i < data.length; i++)
					data[i] = in.readObject(_componentType);

			} else
			{
				for (int i = 0; i < data.length; i++)
					data[i] = in.readObject();

			}
			in.readListEnd();
			return ((Object) (data));
		}
		ArrayList list = new ArrayList();
		in.addRef(list);
		if (_componentType != null)
			for (; !in.isEnd(); list.add(in.readObject(_componentType)));
		else
			for (; !in.isEnd(); list.add(in.readObject()));
		in.readListEnd();
		Object data[] = createArray(list.size());
		for (int i = 0; i < data.length; i++)
			data[i] = list.get(i);

		return ((Object) (data));
	}

	public Object readLengthList(AbstractHessianInput in, int length)
		throws IOException
	{
		Object data[] = createArray(length);
		in.addRef(((Object) (data)));
		if (_componentType != null)
		{
			for (int i = 0; i < data.length; i++)
				data[i] = in.readObject(_componentType);

		} else
		{
			for (int i = 0; i < data.length; i++)
				data[i] = in.readObject();

		}
		return ((Object) (data));
	}

	protected Object[] createArray(int length)
	{
		if (_componentType != null)
			return (Object[])(Object[])Array.newInstance(_componentType, length);
		else
			return new Object[length];
	}

	public String toString()
	{
		return (new StringBuilder()).append("ArrayDeserializer[").append(_componentType).append("]").toString();
	}
}
