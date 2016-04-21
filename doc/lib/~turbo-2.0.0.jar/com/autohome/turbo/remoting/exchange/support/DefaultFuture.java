// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   DefaultFuture.java

package com.autohome.turbo.remoting.exchange.support;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.remoting.*;
import com.autohome.turbo.remoting.exchange.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

public class DefaultFuture
	implements ResponseFuture
{
	private static class RemotingInvocationTimeoutScan
		implements Runnable
	{

		public void run()
		{
			do
				try
				{
					Iterator i$ = DefaultFuture.FUTURES.values().iterator();
					do
					{
						if (!i$.hasNext())
							break;
						DefaultFuture future = (DefaultFuture)i$.next();
						if (future != null && !future.isDone() && System.currentTimeMillis() - future.getStartTimestamp() > (long)future.getTimeout())
						{
							Response timeoutResponse = new Response(future.getId());
							timeoutResponse.setStatus(((byte)(future.isSent() ? 31 : 30)));
							timeoutResponse.setErrorMessage(future.getTimeoutMessage(true));
							DefaultFuture.received(future.getChannel(), timeoutResponse);
						}
					} while (true);
					Thread.sleep(30L);
				}
				catch (Throwable e)
				{
					DefaultFuture.logger.error("Exception when scan the timeout invocation of remoting.", e);
				}
			while (true);
		}

		private RemotingInvocationTimeoutScan()
		{
		}

	}


	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/remoting/exchange/support/DefaultFuture);
	private static final Map CHANNELS = new ConcurrentHashMap();
	private static final Map FUTURES = new ConcurrentHashMap();
	private final long id;
	private final Channel channel;
	private final Request request;
	private final int timeout;
	private final Lock lock = new ReentrantLock();
	private final Condition done;
	private final long start = System.currentTimeMillis();
	private volatile long sent;
	private volatile Response response;
	private volatile ResponseCallback callback;

	public DefaultFuture(Channel channel, Request request, int timeout)
	{
		done = lock.newCondition();
		this.channel = channel;
		this.request = request;
		id = request.getId();
		this.timeout = timeout <= 0 ? channel.getUrl().getPositiveParameter("timeout", 1000) : timeout;
		FUTURES.put(Long.valueOf(id), this);
		CHANNELS.put(Long.valueOf(id), channel);
	}

	public Object get()
		throws RemotingException
	{
		return get(timeout);
	}

	public Object get(int timeout)
		throws RemotingException
	{
		Exception exception;
		if (timeout <= 0)
			timeout = 1000;
		if (isDone())
			break MISSING_BLOCK_LABEL_147;
		long start = System.currentTimeMillis();
		lock.lock();
		try
		{
			do
			{
				if (isDone())
					break;
				done.await(timeout, TimeUnit.MILLISECONDS);
			} while (!isDone() && System.currentTimeMillis() - start <= (long)timeout);
		}
		catch (InterruptedException e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			lock.unlock();
		}
		lock.unlock();
		break MISSING_BLOCK_LABEL_109;
		throw exception;
		if (!isDone())
			throw new TimeoutException(sent > 0L, channel, getTimeoutMessage(false));
		return returnFromResponse();
	}

	public void cancel()
	{
		Response errorResult = new Response(id);
		errorResult.setErrorMessage("request future has been canceled.");
		response = errorResult;
		FUTURES.remove(Long.valueOf(id));
		CHANNELS.remove(Long.valueOf(id));
	}

	public boolean isDone()
	{
		return response != null;
	}

	public void setCallback(ResponseCallback callback)
	{
		boolean isdone;
		if (isDone())
		{
			invokeCallback(callback);
			break MISSING_BLOCK_LABEL_76;
		}
		isdone = false;
		lock.lock();
		if (!isDone())
			this.callback = callback;
		else
			isdone = true;
		lock.unlock();
		break MISSING_BLOCK_LABEL_67;
		Exception exception;
		exception;
		lock.unlock();
		throw exception;
		if (isdone)
			invokeCallback(callback);
	}

	private void invokeCallback(ResponseCallback c)
	{
		ResponseCallback callbackCopy = c;
		if (callbackCopy == null)
			throw new NullPointerException("callback cannot be null.");
		c = null;
		Response res = response;
		if (res == null)
			throw new IllegalStateException((new StringBuilder()).append("response cannot be null. url:").append(channel.getUrl()).toString());
		if (res.getStatus() == 20)
			try
			{
				callbackCopy.done(res.getResult());
			}
			catch (Exception e)
			{
				logger.error((new StringBuilder()).append("callback invoke error .reasult:").append(res.getResult()).append(",url:").append(channel.getUrl()).toString(), e);
			}
		else
		if (res.getStatus() == 30 || res.getStatus() == 31)
			try
			{
				TimeoutException te = new TimeoutException(res.getStatus() == 31, channel, res.getErrorMessage());
				callbackCopy.caught(te);
			}
			catch (Exception e)
			{
				logger.error((new StringBuilder()).append("callback invoke error ,url:").append(channel.getUrl()).toString(), e);
			}
		else
			try
			{
				RuntimeException re = new RuntimeException(res.getErrorMessage());
				callbackCopy.caught(re);
			}
			catch (Exception e)
			{
				logger.error((new StringBuilder()).append("callback invoke error ,url:").append(channel.getUrl()).toString(), e);
			}
	}

	private Object returnFromResponse()
		throws RemotingException
	{
		Response res = response;
		if (res == null)
			throw new IllegalStateException("response cannot be null");
		if (res.getStatus() == 20)
			return res.getResult();
		if (res.getStatus() == 30 || res.getStatus() == 31)
			throw new TimeoutException(res.getStatus() == 31, channel, res.getErrorMessage());
		else
			throw new RemotingException(channel, res.getErrorMessage());
	}

	private long getId()
	{
		return id;
	}

	private Channel getChannel()
	{
		return channel;
	}

	private boolean isSent()
	{
		return sent > 0L;
	}

	public Request getRequest()
	{
		return request;
	}

	private int getTimeout()
	{
		return timeout;
	}

	private long getStartTimestamp()
	{
		return start;
	}

	public static DefaultFuture getFuture(long id)
	{
		return (DefaultFuture)FUTURES.get(Long.valueOf(id));
	}

	public static boolean hasFuture(Channel channel)
	{
		return CHANNELS.containsValue(channel);
	}

	public static void sent(Channel channel, Request request)
	{
		DefaultFuture future = (DefaultFuture)FUTURES.get(Long.valueOf(request.getId()));
		if (future != null)
			future.doSent();
	}

	private void doSent()
	{
		sent = System.currentTimeMillis();
	}

	public static void received(Channel channel, Response response)
	{
		DefaultFuture future = (DefaultFuture)FUTURES.remove(Long.valueOf(response.getId()));
		if (future != null)
			future.doReceived(response);
		else
			logger.warn((new StringBuilder()).append("The timeout response finally returned at ").append((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")).format(new Date())).append(", response ").append(response).append(channel != null ? (new StringBuilder()).append(", channel: ").append(channel.getLocalAddress()).append(" -> ").append(channel.getRemoteAddress()).toString() : "").toString());
		CHANNELS.remove(Long.valueOf(response.getId()));
		break MISSING_BLOCK_LABEL_173;
		Exception exception;
		exception;
		CHANNELS.remove(Long.valueOf(response.getId()));
		throw exception;
	}

	private void doReceived(Response res)
	{
		lock.lock();
		response = res;
		if (done != null)
			done.signal();
		lock.unlock();
		break MISSING_BLOCK_LABEL_54;
		Exception exception;
		exception;
		lock.unlock();
		throw exception;
		if (callback != null)
			invokeCallback(callback);
		return;
	}

	private String getTimeoutMessage(boolean scan)
	{
		long nowTimestamp = System.currentTimeMillis();
		return (new StringBuilder()).append(sent <= 0L ? "Sending request timeout in client-side" : "Waiting server-side response timeout").append(scan ? " by scan timer" : "").append(". start time: ").append((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")).format(new Date(start))).append(", end time: ").append((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")).format(new Date())).append(",").append(sent <= 0L ? (new StringBuilder()).append(" elapsed: ").append(nowTimestamp - start).toString() : (new StringBuilder()).append(" client elapsed: ").append(sent - start).append(" ms, server elapsed: ").append(nowTimestamp - sent).toString()).append(" ms, timeout: ").append(timeout).append(" ms, request: ").append(request).append(", channel: ").append(channel.getLocalAddress()).append(" -> ").append(channel.getRemoteAddress()).toString();
	}

	static 
	{
		Thread th = new Thread(new RemotingInvocationTimeoutScan(), "DubboResponseTimeoutScanTimer");
		th.setDaemon(true);
		th.start();
	}








}
