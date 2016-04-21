// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Serialization.java

package com.autohome.turbo.common.serialize;

import com.autohome.turbo.common.URL;
import java.io.*;

// Referenced classes of package com.autohome.turbo.common.serialize:
//			ObjectOutput, ObjectInput

public interface Serialization
{

	public abstract byte getContentTypeId();

	public abstract String getContentType();

	public abstract ObjectOutput serialize(URL url, OutputStream outputstream)
		throws IOException;

	public abstract ObjectInput deserialize(URL url, InputStream inputstream)
		throws IOException;
}
