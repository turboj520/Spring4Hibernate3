// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   DecodeableRpcResult.java

package com.autohome.turbo.rpc.protocol.dubbo;

import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.serialize.*;
import com.autohome.turbo.common.utils.Assert;
import com.autohome.turbo.common.utils.StringUtils;
import com.autohome.turbo.remoting.*;
import com.autohome.turbo.remoting.exchange.Response;
import com.autohome.turbo.remoting.transport.CodecSupport;
import com.autohome.turbo.rpc.Invocation;
import com.autohome.turbo.rpc.RpcResult;
import com.autohome.turbo.rpc.support.RpcUtils;
import java.io.*;

public class DecodeableRpcResult extends RpcResult
	implements Codec, Decodeable
{

	private static final Logger log = LoggerFactory.getLogger(com/autohome/turbo/rpc/protocol/dubbo/DecodeableRpcResult);
	private Channel channel;
	private byte serializationType;
	private InputStream inputStream;
	private Response response;
	private Invocation invocation;
	private volatile boolean hasDecoded;

	public DecodeableRpcResult(Channel channel, Response response, InputStream is, Invocation invocation, byte id)
	{
		Assert.notNull(channel, "channel == null");
		Assert.notNull(response, "response == null");
		Assert.notNull(is, "inputStream == null");
		this.channel = channel;
		this.response = response;
		inputStream = is;
		this.invocation = invocation;
		serializationType = id;
	}

	public void encode(Channel channel, OutputStream output, Object message)
		throws IOException
	{
		throw new UnsupportedOperationException();
	}

	public Object decode(Channel channel, InputStream input)
		throws IOException
	{
		ObjectInput in = CodecSupport.getSerialization(channel.getUrl(), Byte.valueOf(serializationType)).deserialize(channel.getUrl(), input);
		DecodeableRpcResult decodeablerpcresult;
		byte flag = in.readByte();
		switch (flag)
		{
		case 2: // '\002'
			break;

		case 1: // '\001'
			try
			{
				java.lang.reflect.Type returnType[] = RpcUtils.getReturnTypes(invocation);
				setValue(returnType != null && returnType.length != 0 ? returnType.length != 1 ? in.readObject((Class)returnType[0], returnType[1]) : in.readObject((Class)returnType[0]) : in.readObject());
			}
			catch (ClassNotFoundException e)
			{
				throw new IOException(StringUtils.toString("Read response data failed.", e));
			}
			break;

		case 0: // '\0'
			try
			{
				Object obj = in.readObject();
				if (!(obj instanceof Throwable))
					throw new IOException((new StringBuilder()).append("Response data error, expect Throwable, but get ").append(obj).toString());
				setException((Throwable)obj);
			}
			catch (ClassNotFoundException e)
			{
				throw new IOException(StringUtils.toString("Read response data failed.", e));
			}
			break;

		default:
			throw new IOException((new StringBuilder()).append("Unknown result flag, expect '0' '1' '2', get ").append(flag).toString());
		}
		decodeablerpcresult = this;
		if (in instanceof Cleanable)
			((Cleanable)in).cleanup();
		return decodeablerpcresult;
		Exception exception;
		exception;
		if (in instanceof Cleanable)
			((Cleanable)in).cleanup();
		throw exception;
	}

	public void decode()
		throws Exception
	{
		if (hasDecoded || channel == null || inputStream == null)
			break MISSING_BLOCK_LABEL_121;
		decode(channel, inputStream);
		hasDecoded = true;
		break MISSING_BLOCK_LABEL_121;
		Throwable e;
		e;
		if (log.isWarnEnabled())
			log.warn((new StringBuilder()).append("Decode rpc result failed: ").append(e.getMessage()).toString(), e);
		response.setStatus((byte)90);
		response.setErrorMessage(StringUtils.toString(e));
		hasDecoded = true;
		break MISSING_BLOCK_LABEL_121;
		Exception exception;
		exception;
		hasDecoded = true;
		throw exception;
	}

}
