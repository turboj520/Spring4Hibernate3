// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   CollectionDeserializer.java

package com.autohome.com.caucho.hessian.io;

import java.io.IOException;
import java.util.*;

// Referenced classes of package com.autohome.com.caucho.hessian.io:
//			AbstractListDeserializer, IOExceptionWrapper, AbstractHessianInput

public class CollectionDeserializer extends AbstractListDeserializer
{

	private Class _type;

	public CollectionDeserializer(Class type)
	{
		_type = type;
	}

	public Class getType()
	{
		return _type;
	}

	public Object readList(AbstractHessianInput in, int length)
		throws IOException
	{
		Collection list = createList();
		in.addRef(list);
		for (; !in.isEnd(); list.add(in.readObject()));
		in.readEnd();
		return list;
	}

	public Object readLengthList(AbstractHessianInput in, int length)
		throws IOException
	{
		Collection list = createList();
		in.addRef(list);
		for (; length > 0; length--)
			list.add(in.readObject());

		return list;
	}

	private Collection createList()
		throws IOException
	{
		Collection list = null;
		if (_type == null)
			list = new ArrayList();
		else
		if (!_type.isInterface())
			try
			{
				list = (Collection)_type.newInstance();
			}
			catch (Exception e) { }
		if (list == null)
			if (java/util/SortedSet.isAssignableFrom(_type))
				list = new TreeSet();
			else
			if (java/util/Set.isAssignableFrom(_type))
				list = new HashSet();
			else
			if (java/util/List.isAssignableFrom(_type))
				list = new ArrayList();
			else
			if (java/util/Collection.isAssignableFrom(_type))
				list = new ArrayList();
			else
				try
				{
					list = (Collection)_type.newInstance();
				}
				catch (Exception e)
				{
					throw new IOExceptionWrapper(e);
				}
		return list;
	}
}
