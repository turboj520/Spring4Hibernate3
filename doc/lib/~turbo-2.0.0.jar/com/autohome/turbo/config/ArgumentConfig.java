// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ArgumentConfig.java

package com.autohome.turbo.config;

import java.io.Serializable;

public class ArgumentConfig
	implements Serializable
{

	private static final long serialVersionUID = 0xe1f2a92c1a666665L;
	private Integer index;
	private String type;
	private Boolean callback;

	public ArgumentConfig()
	{
		index = Integer.valueOf(-1);
	}

	public void setIndex(Integer index)
	{
		this.index = index;
	}

	public Integer getIndex()
	{
		return index;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public void setCallback(Boolean callback)
	{
		this.callback = callback;
	}

	public Boolean isCallback()
	{
		return callback;
	}
}
