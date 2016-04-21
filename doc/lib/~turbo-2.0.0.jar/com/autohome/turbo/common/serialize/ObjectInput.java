// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ObjectInput.java

package com.autohome.turbo.common.serialize;

import java.io.IOException;
import java.lang.reflect.Type;

// Referenced classes of package com.autohome.turbo.common.serialize:
//			DataInput

public interface ObjectInput
	extends DataInput
{

	public abstract Object readObject()
		throws IOException, ClassNotFoundException;

	public abstract Object readObject(Class class1)
		throws IOException, ClassNotFoundException;

	public abstract Object readObject(Class class1, Type type)
		throws IOException, ClassNotFoundException;
}
