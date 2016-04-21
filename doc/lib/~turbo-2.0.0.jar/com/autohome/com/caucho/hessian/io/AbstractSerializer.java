// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractSerializer.java

package com.autohome.com.caucho.hessian.io;

import java.io.IOException;
import java.util.logging.Logger;

// Referenced classes of package com.autohome.com.caucho.hessian.io:
//			Serializer, AbstractHessianOutput

public abstract class AbstractSerializer
	implements Serializer
{

	protected static final Logger log = Logger.getLogger(com/autohome/com/caucho/hessian/io/AbstractSerializer.getName());

	public AbstractSerializer()
	{
	}

	public abstract void writeObject(Object obj, AbstractHessianOutput abstracthessianoutput)
		throws IOException;

}
