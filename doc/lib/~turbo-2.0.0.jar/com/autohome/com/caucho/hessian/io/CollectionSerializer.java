// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   CollectionSerializer.java

package com.autohome.com.caucho.hessian.io;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

// Referenced classes of package com.autohome.com.caucho.hessian.io:
//			AbstractSerializer, AbstractHessianOutput

public class CollectionSerializer extends AbstractSerializer
{

	private boolean _sendJavaType;

	public CollectionSerializer()
	{
		_sendJavaType = true;
	}

	public void setSendJavaType(boolean sendJavaType)
	{
		_sendJavaType = sendJavaType;
	}

	public boolean getSendJavaType()
	{
		return _sendJavaType;
	}

	public void writeObject(Object obj, AbstractHessianOutput out)
		throws IOException
	{
		if (out.addRef(obj))
			return;
		Collection list = (Collection)obj;
		Class cl = obj.getClass();
		boolean hasEnd;
		if (cl.equals(java/util/ArrayList) || !_sendJavaType || !java/io/Serializable.isAssignableFrom(cl))
			hasEnd = out.writeListBegin(list.size(), null);
		else
			hasEnd = out.writeListBegin(list.size(), obj.getClass().getName());
		Object value;
		for (Iterator iter = list.iterator(); iter.hasNext(); out.writeObject(value))
			value = iter.next();

		if (hasEnd)
			out.writeListEnd();
	}
}
