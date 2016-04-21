// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ObjectOutput.java

package com.autohome.turbo.common.serialize;

import java.io.IOException;

// Referenced classes of package com.autohome.turbo.common.serialize:
//			DataOutput

public interface ObjectOutput
	extends DataOutput
{

	public abstract void writeObject(Object obj)
		throws IOException;
}
