// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   CodecAdapter.java

package com.autohome.turbo.remoting.transport.codec;

import com.autohome.turbo.common.io.UnsafeByteArrayInputStream;
import com.autohome.turbo.common.io.UnsafeByteArrayOutputStream;
import com.autohome.turbo.common.utils.Assert;
import com.autohome.turbo.remoting.*;
import com.autohome.turbo.remoting.buffer.ChannelBuffer;
import java.io.IOException;

public class CodecAdapter
	implements Codec2
{

	private Codec codec;

	public CodecAdapter(Codec codec)
	{
		Assert.notNull(codec, "codec == null");
		this.codec = codec;
	}

	public void encode(Channel channel, ChannelBuffer buffer, Object message)
		throws IOException
	{
		UnsafeByteArrayOutputStream os = new UnsafeByteArrayOutputStream(1024);
		codec.encode(channel, os, message);
		buffer.writeBytes(os.toByteArray());
	}

	public Object decode(Channel channel, ChannelBuffer buffer)
		throws IOException
	{
		byte bytes[] = new byte[buffer.readableBytes()];
		int savedReaderIndex = buffer.readerIndex();
		buffer.readBytes(bytes);
		UnsafeByteArrayInputStream is = new UnsafeByteArrayInputStream(bytes);
		Object result = codec.decode(channel, is);
		buffer.readerIndex(savedReaderIndex + is.position());
		return result != Codec.NEED_MORE_INPUT ? result : com.autohome.turbo.remoting.Codec2.DecodeResult.NEED_MORE_INPUT;
	}

	public Codec getCodec()
	{
		return codec;
	}
}
