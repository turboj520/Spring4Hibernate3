// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   DecodeableRpcInvocation.java

package com.autohome.turbo.rpc.protocol.dubbo;

import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.serialize.*;
import com.autohome.turbo.common.utils.*;
import com.autohome.turbo.remoting.*;
import com.autohome.turbo.remoting.exchange.Request;
import com.autohome.turbo.remoting.transport.CodecSupport;
import com.autohome.turbo.rpc.RpcInvocation;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

// Referenced classes of package com.autohome.turbo.rpc.protocol.dubbo:
//			DubboCodec, CallbackServiceCodec

public class DecodeableRpcInvocation extends RpcInvocation
	implements Codec, Decodeable
{

	private static final Logger log = LoggerFactory.getLogger(com/autohome/turbo/rpc/protocol/dubbo/DecodeableRpcInvocation);
	private Channel channel;
	private byte serializationType;
	private InputStream inputStream;
	private Request request;
	private volatile boolean hasDecoded;

	public DecodeableRpcInvocation(Channel channel, Request request, InputStream is, byte id)
	{
		Assert.notNull(channel, "channel == null");
		Assert.notNull(request, "request == null");
		Assert.notNull(is, "inputStream == null");
		this.channel = channel;
		this.request = request;
		inputStream = is;
		serializationType = id;
	}

	public void decode()
		throws Exception
	{
		if (hasDecoded || channel == null || inputStream == null)
			break MISSING_BLOCK_LABEL_117;
		decode(channel, inputStream);
		hasDecoded = true;
		break MISSING_BLOCK_LABEL_117;
		Throwable e;
		e;
		if (log.isWarnEnabled())
			log.warn((new StringBuilder()).append("Decode rpc invocation failed: ").append(e.getMessage()).toString(), e);
		request.setBroken(true);
		request.setData(e);
		hasDecoded = true;
		break MISSING_BLOCK_LABEL_117;
		Exception exception;
		exception;
		hasDecoded = true;
		throw exception;
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
		setAttachment("dubbo", in.readUTF());
		setAttachment("path", in.readUTF());
		setAttachment("version", in.readUTF());
		setMethodName(in.readUTF());
		try
		{
			int argNum = -1;
			if (CodecSupport.getSerialization(channel.getUrl(), Byte.valueOf(serializationType)) instanceof OptimizedSerialization)
				argNum = in.readInt();
			Object args[];
			Class pts[];
			if (argNum >= 0)
			{
				if (argNum == 0)
				{
					pts = DubboCodec.EMPTY_CLASS_ARRAY;
					args = DubboCodec.EMPTY_OBJECT_ARRAY;
				} else
				{
					args = new Object[argNum];
					pts = new Class[argNum];
					for (int i = 0; i < args.length; i++)
						try
						{
							args[i] = in.readObject();
							pts[i] = args[i].getClass();
						}
						catch (Exception e)
						{
							if (log.isWarnEnabled())
								log.warn((new StringBuilder()).append("Decode argument failed: ").append(e.getMessage()).toString(), e);
						}

				}
			} else
			{
				String desc = in.readUTF();
				if (desc.length() == 0)
				{
					pts = DubboCodec.EMPTY_CLASS_ARRAY;
					args = DubboCodec.EMPTY_OBJECT_ARRAY;
				} else
				{
					pts = ReflectUtils.desc2classArray(desc);
					args = new Object[pts.length];
					for (int i = 0; i < args.length; i++)
						try
						{
							args[i] = in.readObject(pts[i]);
							continue;
						}
						catch (Exception e)
						{
							if (log.isWarnEnabled())
								log.warn((new StringBuilder()).append("Decode argument failed: ").append(e.getMessage()).toString(), e);
						}

				}
			}
			setParameterTypes(pts);
			Map map = (Map)in.readObject(java/util/Map);
			if (map != null && map.size() > 0)
			{
				Map attachment = getAttachments();
				if (attachment == null)
					attachment = new HashMap();
				attachment.putAll(map);
				setAttachments(attachment);
			}
			for (int i = 0; i < args.length; i++)
				args[i] = CallbackServiceCodec.decodeInvocationArgument(channel, this, pts, i, args[i]);

			setArguments(args);
		}
		catch (ClassNotFoundException e)
		{
			throw new IOException(StringUtils.toString("Read invocation data failed.", e));
		}
		if (in instanceof Cleanable)
			((Cleanable)in).cleanup();
		break MISSING_BLOCK_LABEL_536;
		Exception exception;
		exception;
		if (in instanceof Cleanable)
			((Cleanable)in).cleanup();
		throw exception;
		return this;
	}

}
