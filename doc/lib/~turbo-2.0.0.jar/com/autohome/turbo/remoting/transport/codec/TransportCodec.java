// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   TransportCodec.java

package com.autohome.turbo.remoting.transport.codec;

import com.autohome.turbo.common.serialize.*;
import com.autohome.turbo.common.utils.StringUtils;
import com.autohome.turbo.remoting.Channel;
import com.autohome.turbo.remoting.buffer.*;
import com.autohome.turbo.remoting.transport.AbstractCodec;
import java.io.*;

public class TransportCodec extends AbstractCodec
{

	public TransportCodec()
	{
	}

	public void encode(Channel channel, ChannelBuffer buffer, Object message)
		throws IOException
	{
		OutputStream output = new ChannelBufferOutputStream(buffer);
		ObjectOutput objectOutput = getSerialization(channel).serialize(channel.getUrl(), output);
		encodeData(channel, objectOutput, message);
		objectOutput.flushBuffer();
		if (objectOutput instanceof Cleanable)
			((Cleanable)objectOutput).cleanup();
	}

	public Object decode(Channel channel, ChannelBuffer buffer)
		throws IOException
	{
		InputStream input = new ChannelBufferInputStream(buffer);
		ObjectInput objectInput = getSerialization(channel).deserialize(channel.getUrl(), input);
		Object object = decodeData(channel, objectInput);
		if (objectInput instanceof Cleanable)
			((Cleanable)objectInput).cleanup();
		return object;
	}

	protected void encodeData(Channel channel, ObjectOutput output, Object message)
		throws IOException
	{
		encodeData(output, message);
	}

	protected Object decodeData(Channel channel, ObjectInput input)
		throws IOException
	{
		return decodeData(input);
	}

	protected void encodeData(ObjectOutput output, Object message)
		throws IOException
	{
		output.writeObject(message);
	}

	protected Object decodeData(ObjectInput input)
		throws IOException
	{
		return input.readObject();
		ClassNotFoundException e;
		e;
		throw new IOException((new StringBuilder()).append("ClassNotFoundException: ").append(StringUtils.toString(e)).toString());
	}
}
