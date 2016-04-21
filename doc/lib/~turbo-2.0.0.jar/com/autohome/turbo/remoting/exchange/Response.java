// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Response.java

package com.autohome.turbo.remoting.exchange;


public class Response
{

	public static final String HEARTBEAT_EVENT = null;
	public static final String READONLY_EVENT = "R";
	public static final byte OK = 20;
	public static final byte CLIENT_TIMEOUT = 30;
	public static final byte SERVER_TIMEOUT = 31;
	public static final byte BAD_REQUEST = 40;
	public static final byte BAD_RESPONSE = 50;
	public static final byte SERVICE_NOT_FOUND = 60;
	public static final byte SERVICE_ERROR = 70;
	public static final byte SERVER_ERROR = 80;
	public static final byte CLIENT_ERROR = 90;
	private long mId;
	private String mVersion;
	private byte mStatus;
	private boolean mEvent;
	private String mErrorMsg;
	private Object mResult;

	public Response()
	{
		mId = 0L;
		mStatus = 20;
		mEvent = false;
	}

	public Response(long id)
	{
		mId = 0L;
		mStatus = 20;
		mEvent = false;
		mId = id;
	}

	public Response(long id, String version)
	{
		mId = 0L;
		mStatus = 20;
		mEvent = false;
		mId = id;
		mVersion = version;
	}

	public long getId()
	{
		return mId;
	}

	public void setId(long id)
	{
		mId = id;
	}

	public String getVersion()
	{
		return mVersion;
	}

	public void setVersion(String version)
	{
		mVersion = version;
	}

	public byte getStatus()
	{
		return mStatus;
	}

	public void setStatus(byte status)
	{
		mStatus = status;
	}

	public boolean isEvent()
	{
		return mEvent;
	}

	public void setEvent(String event)
	{
		mEvent = true;
		mResult = event;
	}

	public boolean isHeartbeat()
	{
		return mEvent && HEARTBEAT_EVENT == mResult;
	}

	/**
	 * @deprecated Method setHeartbeat is deprecated
	 */

	public void setHeartbeat(boolean isHeartbeat)
	{
		if (isHeartbeat)
			setEvent(HEARTBEAT_EVENT);
	}

	public Object getResult()
	{
		return mResult;
	}

	public void setResult(Object msg)
	{
		mResult = msg;
	}

	public String getErrorMessage()
	{
		return mErrorMsg;
	}

	public void setErrorMessage(String msg)
	{
		mErrorMsg = msg;
	}

	public String toString()
	{
		return (new StringBuilder()).append("Response [id=").append(mId).append(", version=").append(mVersion).append(", status=").append(mStatus).append(", event=").append(mEvent).append(", error=").append(mErrorMsg).append(", result=").append(mResult != this ? mResult : "this").append("]").toString();
	}

}
