// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JSONConverter.java

package com.autohome.turbo.common.json;

import java.io.IOException;

// Referenced classes of package com.autohome.turbo.common.json:
//			JSONWriter

public interface JSONConverter
{

	public abstract void writeValue(Object obj, JSONWriter jsonwriter, boolean flag)
		throws IOException;

	public abstract Object readValue(Class class1, Object obj)
		throws IOException;
}
