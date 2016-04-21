// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ExchangeCodec.java

package com.autohome.turbo.remoting.exchange.codec;

import com.autohome.turbo.common.io.Bytes;
import com.autohome.turbo.common.io.StreamUtils;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.serialize.*;
import com.autohome.turbo.common.utils.StringUtils;
import com.autohome.turbo.remoting.*;
import com.autohome.turbo.remoting.buffer.*;
import com.autohome.turbo.remoting.exchange.Request;
import com.autohome.turbo.remoting.exchange.Response;
import com.autohome.turbo.remoting.exchange.support.DefaultFuture;
import com.autohome.turbo.remoting.telnet.codec.TelnetCodec;
import com.autohome.turbo.remoting.transport.CodecSupport;
import java.io.IOException;
import java.io.InputStream;

public class ExchangeCodec extends TelnetCodec
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/remoting/exchange/codec/ExchangeCodec);
	protected static final int HEADER_LENGTH = 16;
	protected static final short MAGIC = -9541;
	protected static final byte MAGIC_HIGH = Bytes.short2bytes((short)-9541)[0];
	protected static final byte MAGIC_LOW = Bytes.short2bytes((short)-9541)[1];
	protected static final byte FLAG_REQUEST = -128;
	protected static final byte FLAG_TWOWAY = 64;
	protected static final byte FLAG_EVENT = 32;
	protected static final int SERIALIZATION_MASK = 31;

	public ExchangeCodec()
	{
	}

	public Short getMagicCode()
	{
		return Short.valueOf((short)-9541);
	}

	public void encode(Channel channel, ChannelBuffer buffer, Object msg)
		throws IOException
	{
		if (msg instanceof Request)
			encodeRequest(channel, buffer, (Request)msg);
		else
		if (msg instanceof Response)
			encodeResponse(channel, buffer, (Response)msg);
		else
			super.encode(channel, buffer, msg);
		if (logger.isTraceEnabled())
			logger.trace((new StringBuilder()).append("the resulting byte size of encoding is ").append(buffer.readableBytes()).toString());
	}

	public Object decode(Channel channel, ChannelBuffer buffer)
		throws IOException
	{
		int readable = buffer.readableBytes();
		byte header[] = new byte[Math.min(readable, 16)];
		buffer.readBytes(header);
		return decode(channel, buffer, readable, header);
	}

	protected Object decode(Channel channel, ChannelBuffer buffer, int readable, byte header[])
		throws IOException
	{
		ChannelBufferInputStream is;
		if (readable > 0 && header[0] != MAGIC_HIGH || readable > 1 && header[1] != MAGIC_LOW)
		{
			int length = header.length;
			if (header.length < readable)
			{
				header = Bytes.copyOf(header, readable);
				buffer.readBytes(header, length, readable - length);
			}
			int i = 1;
			do
			{
				if (i >= header.length - 1)
					break;
				if (header[i] == MAGIC_HIGH && header[i + 1] == MAGIC_LOW)
				{
					buffer.readerIndex((buffer.readerIndex() - header.length) + i);
					header = Bytes.copyOf(header, i);
					break;
				}
				i++;
			} while (true);
			return super.decode(channel, buffer, readable, header);
		}
		if (readable < 16)
			return com.autohome.turbo.remoting.Codec2.DecodeResult.NEED_MORE_INPUT;
		int len = Bytes.bytes2int(header, 12);
		checkPayload(channel, len);
		int tt = len + 16;
		if (readable < tt)
			return com.autohome.turbo.remoting.Codec2.DecodeResult.NEED_MORE_INPUT;
		is = new ChannelBufferInputStream(buffer, len);
		Object obj = decodeBody(channel, is, header);
		if (is.available() > 0)
			try
			{
				if (logger.isWarnEnabled())
					logger.warn((new StringBuilder()).append("Skip input stream ").append(is.available()).toString());
				StreamUtils.skipUnusedStream(is);
			}
			catch (IOException e)
			{
				logger.warn(e.getMessage(), e);
			}
		return obj;
		Exception exception;
		exception;
		if (is.available() > 0)
			try
			{
				if (logger.isWarnEnabled())
					logger.warn((new StringBuilder()).append("Skip input stream ").append(is.available()).toString());
				StreamUtils.skipUnusedStream(is);
			}
			catch (IOException e)
			{
				logger.warn(e.getMessage(), e);
			}
		throw exception;
	}

	protected Object decodeBody(Channel channel, InputStream is, byte header[])
		throws IOException
	{
		byte flag = header[2];
		byte proto = (byte)(flag & 0x1f);
		Serialization s = CodecSupport.getSerialization(channel.getUrl(), Byte.valueOf(proto));
		ObjectInput in = s.deserialize(channel.getUrl(), is);
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
						data = decodeHeartbeatData(channel, in);
					else
					if (res.isEvent())
						data = decodeEventData(channel, in);
					else
						data = decodeResponseData(channel, in, getRequestData(id));
					res.setResult(data);
				}
				catch (Throwable t)
				{
					res.setStatus((byte)90);
					res.setErrorMessage(StringUtils.toString(t));
				}
			else
				res.setErrorMessage(in.readUTF());
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
				data = decodeHeartbeatData(channel, in);
			else
			if (req.isEvent())
				data = decodeEventData(channel, in);
			else
				data = decodeRequestData(channel, in);
			req.setData(data);
		}
		catch (Throwable t)
		{
			req.setBroken(true);
			req.setData(t);
		}
		return req;
	}

	protected Object getRequestData(long id)
	{
		DefaultFuture future = DefaultFuture.getFuture(id);
		if (future == null)
			return null;
		Request req = future.getRequest();
		if (req == null)
			return null;
		else
			return req.getData();
	}

	protected void encodeRequest(Channel channel, ChannelBuffer buffer, Request req)
		throws IOException
	{
		byte header[];
		int savedWriteIndex;
		ChannelBufferOutputStream bos;
		ObjectOutput out;
		Serialization serialization = getSerialization(channel);
		header = new byte[16];
		Bytes.short2bytes((short)-9541, header);
		header[2] = (byte)(0xffffff80 | serialization.getContentTypeId());
		if (req.isTwoWay())
			header[2] |= 0x40;
		if (req.isEvent())
			header[2] |= 0x20;
		Bytes.long2bytes(req.getId(), header, 4);
		savedWriteIndex = buffer.writerIndex();
		buffer.writerIndex(savedWriteIndex + 16);
		bos = new ChannelBufferOutputStream(buffer);
		out = serialization.serialize(channel.getUrl(), bos);
		if (req.isEvent())
			encodeEventData(channel, out, req.getData());
		else
			encodeRequestData(channel, out, req.getData());
		out.flushBuffer();
		if (out instanceof Cleanable)
			((Cleanable)out).cleanup();
		break MISSING_BLOCK_LABEL_209;
		Exception exception;
		exception;
		if (out instanceof Cleanable)
			((Cleanable)out).cleanup();
		throw exception;
		bos.flush();
		bos.close();
		int len = bos.writtenBytes();
		checkPayload(channel, len);
		Bytes.int2bytes(len, header, 12);
		buffer.writerIndex(savedWriteIndex);
		buffer.writeBytes(header);
		buffer.writerIndex(savedWriteIndex + 16 + len);
		return;
	}

	protected void encodeResponse(Channel channel, ChannelBuffer buffer, Response res)
		throws IOException
	{
		byte header[];
		byte status;
		int savedWriteIndex;
		ChannelBufferOutputStream bos;
		ObjectOutput out;
		Serialization serialization = getSerialization(channel);
		header = new byte[16];
		Bytes.short2bytes((short)-9541, header);
		header[2] = serialization.getContentTypeId();
		if (res.isHeartbeat())
			header[2] |= 0x20;
		status = res.getStatus();
		header[3] = status;
		Bytes.long2bytes(res.getId(), header, 4);
		savedWriteIndex = buffer.writerIndex();
		buffer.writerIndex(savedWriteIndex + 16);
		bos = new ChannelBufferOutputStream(buffer);
		out = serialization.serialize(channel.getUrl(), bos);
		if (status == 20)
		{
			if (res.isHeartbeat())
				encodeHeartbeatData(channel, out, res.getResult());
			else
				encodeResponseData(channel, out, res.getResult());
		} else
		{
			out.writeUTF(res.getErrorMessage());
		}
		out.flushBuffer();
		if (out instanceof Cleanable)
			((Cleanable)out).cleanup();
		break MISSING_BLOCK_LABEL_221;
		Exception exception;
		exception;
		if (out instanceof Cleanable)
			((Cleanable)out).cleanup();
		throw exception;
		bos.flush();
		bos.close();
		int len = bos.writtenBytes();
		checkPayload(channel, len);
		Bytes.int2bytes(len, header, 12);
		buffer.writerIndex(savedWriteIndex);
		buffer.writeBytes(header);
		buffer.writerIndex(savedWriteIndex + 16 + len);
		break MISSING_BLOCK_LABEL_518;
		Throwable t;
		t;
		if (!res.isEvent() && res.getStatus() != 50)
			try
			{
				logger.warn((new StringBuilder()).append("Fail to encode response: ").append(res).append(", send bad_response info instead, cause: ").append(t.getMessage()).toString(), t);
				Response r = new Response(res.getId(), res.getVersion());
				r.setStatus((byte)50);
				r.setErrorMessage((new StringBuilder()).append("Failed to send response: ").append(res).append(", cause: ").append(StringUtils.toString(t)).toString());
				channel.send(r);
				return;
			}
			catch (RemotingException e)
			{
				logger.warn((new StringBuilder()).append("Failed to send bad_response info back: ").append(res).append(", cause: ").append(e.getMessage()).toString(), e);
			}
		if (t instanceof IOException)
			throw (IOException)t;
		if (t instanceof RuntimeException)
			throw (RuntimeException)t;
		if (t instanceof Error)
			throw (Error)t;
		else
			throw new RuntimeException(t.getMessage(), t);
	}

	protected Object decodeData(ObjectInput in)
		throws IOException
	{
		return decodeRequestData(in);
	}

	/**
	 * @deprecated Method decodeHeartbeatData is deprecated
	 */

	protected Object decodeHeartbeatData(ObjectInput in)
		throws IOException
	{
		return in.readObject();
		ClassNotFoundException e;
		e;
		throw new IOException(StringUtils.toString("Read object failed.", e));
	}

	protected Object decodeRequestData(ObjectInput in)
		throws IOException
	{
		return in.readObject();
		ClassNotFoundException e;
		e;
		throw new IOException(StringUtils.toString("Read object failed.", e));
	}

	protected Object decodeResponseData(ObjectInput in)
		throws IOException
	{
		return in.readObject();
		ClassNotFoundException e;
		e;
		throw new IOException(StringUtils.toString("Read object failed.", e));
	}

	protected void encodeData(ObjectOutput out, Object data)
		throws IOException
	{
		encodeRequestData(out, data);
	}

	private void encodeEventData(ObjectOutput out, Object data)
		throws IOException
	{
		out.writeObject(data);
	}

	/**
	 * @deprecated Method encodeHeartbeatData is deprecated
	 */

	protected void encodeHeartbeatData(ObjectOutput out, Object data)
		throws IOException
	{
		encodeEventData(out, data);
	}

	protected void encodeRequestData(ObjectOutput out, Object data)
		throws IOException
	{
		out.writeObject(data);
	}

	protected void encodeResponseData(ObjectOutput out, Object data)
		throws IOException
	{
		out.writeObject(data);
	}

	protected Object decodeData(Channel channel, ObjectInput in)
		throws IOException
	{
		return decodeRequestData(channel, in);
	}

	protected Object decodeEventData(Channel channel, ObjectInput in)
		throws IOException
	{
		return in.readObject();
		ClassNotFoundException e;
		e;
		throw new IOException(StringUtils.toString("Read object failed.", e));
	}

	/**
	 * @deprecated Method decodeHeartbeatData is deprecated
	 */

	protected Object decodeHeartbeatData(Channel channel, ObjectInput in)
		throws IOException
	{
		return in.readObject();
		ClassNotFoundException e;
		e;
		throw new IOException(StringUtils.toString("Read object failed.", e));
	}

	protected Object decodeRequestData(Channel channel, ObjectInput in)
		throws IOException
	{
		return decodeRequestData(in);
	}

	protected Object decodeResponseData(Channel channel, ObjectInput in)
		throws IOException
	{
		return decodeResponseData(in);
	}

	protected Object decodeResponseData(Channel channel, ObjectInput in, Object requestData)
		throws IOException
	{
		return decodeResponseData(channel, in);
	}

	protected void encodeData(Channel channel, ObjectOutput out, Object data)
		throws IOException
	{
		encodeRequestData(channel, out, data);
	}

	private void encodeEventData(Channel channel, ObjectOutput out, Object data)
		throws IOException
	{
		encodeEventData(out, data);
	}

	/**
	 * @deprecated Method encodeHeartbeatData is deprecated
	 */

	protected void encodeHeartbeatData(Channel channel, ObjectOutput out, Object data)
		throws IOException
	{
		encodeHeartbeatData(out, data);
	}

	protected void encodeRequestData(Channel channel, ObjectOutput out, Object data)
		throws IOException
	{
		encodeRequestData(out, data);
	}

	protected void encodeResponseData(Channel channel, ObjectOutput out, Object data)
		throws IOException
	{
		encodeResponseData(out, data);
	}

}
