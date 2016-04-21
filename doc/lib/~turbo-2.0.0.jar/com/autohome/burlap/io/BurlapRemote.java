// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   BurlapRemote.java

package com.autohome.burlap.io;


public class BurlapRemote
{

	private String type;
	private String url;

	public BurlapRemote(String type, String url)
	{
		this.type = type;
		this.url = url;
	}

	public BurlapRemote()
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
		if (!(obj instanceof BurlapRemote))
		{
			return false;
		} else
		{
			BurlapRemote remote = (BurlapRemote)obj;
			return url.equals(remote.url);
		}
	}

	public String toString()
	{
		return (new StringBuilder()).append("[BurlapRemote ").append(url).append("]").toString();
	}
}
