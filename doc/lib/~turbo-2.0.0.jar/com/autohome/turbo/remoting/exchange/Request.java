// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Request.java

package com.autohome.turbo.remoting.exchange;

import com.autohome.turbo.common.utils.StringUtils;
import java.util.concurrent.atomic.AtomicLong;

public class Request
{

	public static final String HEARTBEAT_EVENT = null;
	public static final String READONLY_EVENT = "R";
	private static final AtomicLong INVOKE_ID = new AtomicLong(0L);
	private final long mId;
	private String mVersion;
	private boolean mTwoWay;
	private boolean mEvent;
	private boolean mBroken;
	private Object mData;

	public Request()
	{
		mTwoWay = true;
		mEvent = false;
		mBroken = false;
		mId = newId();
	}

	public Request(long id)
	{
		mTwoWay = true;
		mEvent = false;
		mBroken = false;
		mId = id;
	}

	public long getId()
	{
		return mId;
	}

	public String getVersion()
	{
		return mVersion;
	}

	public void setVersion(String version)
	{
		mVersion = version;
	}

	public boolean isTwoWay()
	{
		return mTwoWay;
	}

	public void setTwoWay(boolean twoWay)
	{
		mTwoWay = twoWay;
	}

	public boolean isEvent()
	{
		return mEvent;
	}

	public void setEvent(String event)
	{
		mEvent = true;
		mData = event;
	}

	public boolean isBroken()
	{
		return mBroken;
	}

	public void setBroken(boolean mBroken)
	{
		this.mBroken = mBroken;
	}

	public Object getData()
	{
		return mData;
	}

	public void setData(Object msg)
	{
		mData = msg;
	}

	public boolean isHeartbeat()
	{
		return mEvent && HEARTBEAT_EVENT == mData;
	}

	public void setHeartbeat(boolean isHeartbeat)
	{
		if (isHeartbeat)
			setEvent(HEARTBEAT_EVENT);
	}

	private static long newId()
	{
		return INVOKE_ID.getAndIncrement();
	}

	public String toString()
	{
		return (new StringBuilder()).append("Request [id=").append(mId).append(", version=").append(mVersion).append(", twoway=").append(mTwoWay).append(", event=").append(mEvent).append(", broken=").append(mBroken).append(", data=").append(mData != this ? safeToString(mData) : "this").append("]").toString();
	}

	private static String safeToString(Object data)
	{
		if (data == null)
			return null;
		String dataStr;
		try
		{
			dataStr = data.toString();
		}
		catch (Throwable e)
		{
			dataStr = (new StringBuilder()).append("<Fail toString of ").append(data.getClass()).append(", cause: ").append(StringUtils.toString(e)).append(">").toString();
		}
		return dataStr;
	}

}
