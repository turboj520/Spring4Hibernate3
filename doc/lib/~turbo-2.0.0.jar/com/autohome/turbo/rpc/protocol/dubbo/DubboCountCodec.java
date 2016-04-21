// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   DubboCountCodec.java

package com.autohome.turbo.rpc.protocol.dubbo;

import com.autohome.turbo.remoting.Channel;
import com.autohome.turbo.remoting.Codec2;
import com.autohome.turbo.remoting.buffer.ChannelBuffer;
import com.autohome.turbo.remoting.exchange.Request;
import com.autohome.turbo.remoting.exchange.Response;
import com.autohome.turbo.remoting.exchange.support.MultiMessage;
import com.autohome.turbo.rpc.RpcInvocation;
import com.autohome.turbo.rpc.RpcResult;
import java.io.IOException;

// Referenced classes of package com.autohome.turbo.rpc.protocol.dubbo:
//			DubboCodec

public final class DubboCountCodec
	implements Codec2
{

	private DubboCodec codec;

	public DubboCountCodec()
	{
		codec = new DubboCodec();
	}

	public void encode(Channel channel, ChannelBuffer buffer, Object msg)
		throws IOException
	{
		codec.encode(channel, buffer, msg);
	}

	public Object decode(Channel channel, ChannelBuffer buffer)
		throws IOException
	{
		int save = buffer.readerIndex();
		MultiMessage result = MultiMessage.create();
		do
		{
			Object obj = codec.decode(channel, buffer);
			if (com.autohome.turbo.remoting.Codec2.DecodeResult.NEED_MORE_INPUT == obj)
			{
				buffer.readerIndex(save);
				break;
			}
			result.addMessage(obj);
			logMessageLength(obj, buffer.readerIndex() - save);
			save = buffer.readerIndex();
		} while (true);
		if (result.isEmpty())
			return com.autohome.turbo.remoting.Codec2.DecodeResult.NEED_MORE_INPUT;
		if (result.size() == 1)
			return result.get(0);
		else
			return result;
	}

	private void logMessageLength(Object result, int bytes)
	{
		if (bytes <= 0)
			return;
		if (result instanceof Request)
			try
			{
				((RpcInvocation)((Request)result).getData()).setAttachment("input", String.valueOf(bytes));
			}
			catch (Throwable e) { }
		else
		if (result instanceof Response)
			try
			{
				((RpcResult)((Response)result).getResult()).setAttachment("output", String.valueOf(bytes));
			}
			catch (Throwable e) { }
	}
}
