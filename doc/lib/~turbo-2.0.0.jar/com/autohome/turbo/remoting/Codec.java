// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Codec.java

package com.autohome.turbo.remoting;

import java.io.*;

// Referenced classes of package com.autohome.turbo.remoting:
//			Channel

/**
 * @deprecated Interface Codec is deprecated
 */

public interface Codec
{

	public static final Object NEED_MORE_INPUT = new Object();

	public abstract void encode(Channel channel, OutputStream outputstream, Object obj)
		throws IOException;

	public abstract Object decode(Channel channel, InputStream inputstream)
		throws IOException;

}
