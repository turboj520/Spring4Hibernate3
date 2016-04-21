// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   MapSerializer.java

package com.autohome.com.caucho.hessian.io;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

// Referenced classes of package com.autohome.com.caucho.hessian.io:
//			AbstractSerializer, AbstractHessianOutput

public class MapSerializer extends AbstractSerializer
{

	private boolean _isSendJavaType;

	public MapSerializer()
	{
		_isSendJavaType = true;
	}

	public void setSendJavaType(boolean sendJavaType)
	{
		_isSendJavaType = sendJavaType;
	}

	public boolean getSendJavaType()
	{
		return _isSendJavaType;
	}

	public void writeObject(Object obj, AbstractHessianOutput out)
		throws IOException
	{
		if (out.addRef(obj))
			return;
		Map map = (Map)obj;
		Class cl = obj.getClass();
		if (cl.equals(java/util/HashMap) || !_isSendJavaType || !(obj instanceof Serializable))
			out.writeMapBegin(null);
		else
			out.writeMapBegin(obj.getClass().getName());
		java.util.Map.Entry entry;
		for (Iterator iter = map.entrySet().iterator(); iter.hasNext(); out.writeObject(entry.getValue()))
		{
			entry = (java.util.Map.Entry)iter.next();
			out.writeObject(entry.getKey());
		}

		out.writeMapEnd();
	}
}
