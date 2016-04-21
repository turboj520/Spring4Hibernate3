// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   DubboCodec.java

package com.autohome.turbo.rpc.protocol.dubbo;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.Version;
import com.autohome.turbo.common.io.Bytes;
import com.autohome.turbo.common.io.UnsafeByteArrayInputStream;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.serialize.*;
import com.autohome.turbo.common.utils.ReflectUtils;
import com.autohome.turbo.common.utils.StringUtils;
import com.autohome.turbo.remoting.Channel;
import com.autohome.turbo.remoting.Codec2;
import com.autohome.turbo.remoting.exchange.Request;
import com.autohome.turbo.remoting.exchange.Response;
import com.autohome.turbo.remoting.exchange.codec.ExchangeCodec;
import com.autohome.turbo.remoting.transport.CodecSupport;
import com.autohome.turbo.rpc.*;
import java.io.IOException;
import java.io.InputStream;

// Referenced classes of package com.autohome.turbo.rpc.protocol.dubbo:
//			DecodeableRpcResult, DecodeableRpcInvocation, CallbackServiceCodec

public class DubboCodec extends ExchangeCodec
	implements Codec2
{

	private static final Logger log = LoggerFactory.getLogger(com/autohome/turbo/rpc/protocol/dubbo/DubboCodec);
	public static final String NAME = "dubbo";
	public static final String DUBBO_VERSION = Version.getVersion(com/autohome/turbo/rpc/protocol/dubbo/DubboCodec, Version.getVersion());
	public static final byte RESPONSE_WITH_EXCEPTION = 0;
	public static final byte RESPONSE_VALUE = 1;
	public static final byte RESPONSE_NULL_VALUE = 2;
	public static final Object EMPTY_OBJECT_ARRAY[] = new Object[0];
	public static final Class EMPTY_CLASS_ARRAY[] = new Class[0];

	public DubboCodec()
	{
	}

	protected Object decodeBody(Channel channel, InputStream is, byte header[])
		throws IOException
	{
		byte flag = header[2];
		byte proto = (byte)(flag & 0x1f);
		Serialization s = CodecSupport.getSerialization(channel.getUrl(), Byte.valueOf(proto));
		long id = Bytes.bytes2long(header, 4);
		if ((flag & 0xffffff80) == 0)
		{
			Response res = new Response(id);
			if ((flag & 0x20) != 0)
				res.setEvent(Response.HEARTBEAT_EVENT);
			byte status = header[3];
			res.setStatus(status);
			if (status == 20)
				try
				{
					Object data;
					if (res.isHeartbeat())
						data = decodeHeartbeatData(channel, deserialize(s, channel.getUrl(), is));
					else
					if (res.isEvent())
					{
						data = decodeEventData(channel, deserialize(s, channel.getUrl(), is));
					} else
					{
						DecodeableRpcResult result;
						if (channel.getUrl().getParameter("decode.in.io", true))
						{
							result = new DecodeableRpcResult(channel, res, is, (Invocation)getRequestData(id), proto);
							result.decode();
						} else
						{
							result = new DecodeableRpcResult(channel, res, new UnsafeByteArrayInputStream(readMessageData(is)), (Invocation)getRequestData(id), proto);
						}
						data = result;
					}
					res.setResult(data);
				}
				catch (Throwable t)
				{
					if (log.isWarnEnabled())
						log.warn((new StringBuilder()).append("Decode response failed: ").append(t.getMessage()).toString(), t);
					res.setStatus((byte)90);
					res.setErrorMessage(StringUtils.toString(t));
				}
			else
				res.setErrorMessage(deserialize(s, channel.getUrl(), is).readUTF());
			return res;
		}
		Request req = new Request(id);
		req.setVersion("2.0.0");
		req.setTwoWay((flag & 0x40) != 0);
		if ((flag & 0x20) != 0)
			req.setEvent(Request.HEARTBEAT_EVENT);
		try
		{
			Object data;
			if (req.isHeartbeat())
				data = decodeHeartbeatData(channel, deserialize(s, channel.getUrl(), is));
			else
			if (req.isEvent())
			{
				data = decodeEventData(channel, deserialize(s, channel.getUrl(), is));
			} else
			{
				DecodeableRpcInvocation inv;
				if (channel.getUrl().getParameter("decode.in.io", true))
				{
					inv = new DecodeableRpcInvocation(channel, req, is, proto);
					inv.decode();
				} else
				{
					inv = new DecodeableRpcInvocation(channel, req, new UnsafeByteArrayInputStream(readMessageData(is)), proto);
				}
				data = inv;
			}
			req.setData(data);
		}
		catch (Throwable t)
		{
			if (log.isWarnEnabled())
				log.warn((new StringBuilder()).append("Decode request failed: ").append(t.getMessage()).toString(), t);
			req.setBroken(true);
			req.setData(t);
		}
		return req;
	}

	private ObjectInput deserialize(Serialization serialization, URL url, InputStream is)
		throws IOException
	{
		return serialization.deserialize(url, is);
	}

	private byte[] readMessageData(InputStream is)
		throws IOException
	{
		if (is.available() > 0)
		{
			byte result[] = new byte[is.available()];
			is.read(result);
			return result;
		} else
		{
			return new byte[0];
		}
	}

	protected void encodeRequestData(Channel channel, ObjectOutput out, Object data)
		throws IOException
	{
		RpcInvocation inv = (RpcInvocation)data;
		out.writeUTF(inv.getAttachment("dubbo", DUBBO_VERSION));
		out.writeUTF(inv.getAttachment("path"));
		out.writeUTF(inv.getAttachment("version"));
		out.writeUTF(inv.getMethodName());
		if (getSerialization(channel) instanceof OptimizedSerialization)
		{
			if (!containComplexArguments(inv))
				out.writeInt(inv.getParameterTypes().length);
			else
				out.writeInt(-1);
		} else
		{
			out.writeUTF(ReflectUtils.getDesc(inv.getParameterTypes()));
		}
		Object args[] = inv.getArguments();
		if (args != null)
		{
			for (int i = 0; i < args.length; i++)
				out.writeObject(CallbackServiceCodec.encodeInvocationArgument(channel, inv, i));

		}
		out.writeObject(inv.getAttachments());
	}

	protected void encodeResponseData(Channel channel, ObjectOutput out, Object data)
		throws IOException
	{
		Result result = (Result)data;
		Throwable th = result.getException();
		if (th == null)
		{
			Object ret = result.getValue();
			if (ret == null)
			{
				out.writeByte((byte)2);
			} else
			{
				out.writeByte((byte)1);
				out.writeObject(ret);
			}
		} else
		{
			out.writeByte((byte)0);
			out.writeObject(th);
		}
	}

	private boolean containComplexArguments(RpcInvocation invocation)
	{
		for (int i = 0; i < invocation.getParameterTypes().length; i++)
			if (invocation.getArguments()[i] == null || invocation.getParameterTypes()[i] != invocation.getArguments()[i].getClass())
				return true;

		return false;
	}

}
