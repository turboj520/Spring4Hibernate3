// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   RpcException.java

package com.autohome.turbo.rpc;


public final class RpcException extends RuntimeException
{

	private static final long serialVersionUID = 0x6c75f9393bd545deL;
	public static final int UNKNOWN_EXCEPTION = 0;
	public static final int NETWORK_EXCEPTION = 1;
	public static final int TIMEOUT_EXCEPTION = 2;
	public static final int BIZ_EXCEPTION = 3;
	public static final int FORBIDDEN_EXCEPTION = 4;
	public static final int SERIALIZATION_EXCEPTION = 5;
	private int code;

	public RpcException()
	{
	}

	public RpcException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public RpcException(String message)
	{
		super(message);
	}

	public RpcException(Throwable cause)
	{
		super(cause);
	}

	public RpcException(int code)
	{
		this.code = code;
	}

	public RpcException(int code, String message, Throwable cause)
	{
		super(message, cause);
		this.code = code;
	}

	public RpcException(int code, String message)
	{
		super(message);
		this.code = code;
	}

	public RpcException(int code, Throwable cause)
	{
		super(cause);
		this.code = code;
	}

	public void setCode(int code)
	{
		this.code = code;
	}

	public int getCode()
	{
		return code;
	}

	public boolean isBiz()
	{
		return code == 3;
	}

	public boolean isForbidded()
	{
		return code == 4;
	}

	public boolean isTimeout()
	{
		return code == 2;
	}

	public boolean isNetwork()
	{
		return code == 1;
	}

	public boolean isSerialization()
	{
		return code == 5;
	}
}
