// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HessianRemote.java

package com.autohome.hessian.io;

import java.io.Serializable;

public class HessianRemote
	implements Serializable
{

	private String type;
	private String url;

	public HessianRemote(String type, String url)
	{
		this.type = type;
		this.url = url;
	}

	public HessianRemote()
	{
	}

	public String getType()
	{
		return type;
	}

	public String getURL()
	{
		return url;
	}

	public void setURL(String url)
	{
		this.url = url;
	}

	public int hashCode()
	{
		return url.hashCode();
	}

	public boolean equals(Object obj)
	{
		if (!(obj instanceof HessianRemote))
		{
			return false;
		} else
		{
			HessianRemote remote = (HessianRemote)obj;
			return url.equals(remote.url);
		}
	}

	public String toString()
	{
		return (new StringBuilder()).append("HessianRemote[").append(url).append("]").toString();
	}
}
