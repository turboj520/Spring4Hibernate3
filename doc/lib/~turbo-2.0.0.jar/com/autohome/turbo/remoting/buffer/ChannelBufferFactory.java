// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ChannelBufferFactory.java

package com.autohome.turbo.remoting.buffer;

import java.nio.ByteBuffer;

// Referenced classes of package com.autohome.turbo.remoting.buffer:
//			ChannelBuffer

public interface ChannelBufferFactory
{

	public abstract ChannelBuffer getBuffer(int i);

	public abstract ChannelBuffer getBuffer(byte abyte0[], int i, int j);

	public abstract ChannelBuffer getBuffer(ByteBuffer bytebuffer);
}
