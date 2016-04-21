// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   RpcResult.java

package com.autohome.turbo.rpc;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

// Referenced classes of package com.autohome.turbo.rpc:
//			Result

public class RpcResult
	implements Result, Serializable
{

	private static final long serialVersionUID = 0x9fe22bf265f15d09L;
	private Object result;
	private Throwable exception;
	private Map attachments;

	public RpcResult()
	{
		attachments = new HashMap();
	}

	public RpcResult(Object result)
	{
		attachments = new HashMap();
		this.result = result;
	}

	public RpcResult(Throwable exception)
	{
		attachments = new HashMap();
		this.exception = exception;
	}

	public Object recreate()
		throws Throwable
	{
		if (exception != null)
			throw exception;
		else
			return result;
	}

	/**
	 * @deprecated Method getResult is deprecated
	 */

	public Object getResult()
	{
		return getValue();
	}

	/**
	 * @deprecated Method setResult is deprecated
	 */

	public void setResult(Object result)
	{
		setValue(result);
	}

	public Object getValue()
	{
		return result;
	}

	public void setValue(Object value)
	{
		result = value;
	}

	public Throwable getException()
	{
		return exception;
	}

	public void setException(Throwable e)
	{
		exception = e;
	}

	public boolean hasException()
	{
		return exception != null;
	}

	public Map getAttachments()
	{
		return attachments;
	}

	public String getAttachment(String key)
	{
		return (String)attachments.get(key);
	}

	public String getAttachment(String key, String defaultValue)
	{
		String result = (String)attachments.get(key);
		if (result == null || result.length() == 0)
			result = defaultValue;
		return result;
	}

	public void setAttachments(Map map)
	{
		if (map != null && map.size() > 0)
			attachments.putAll(map);
	}

	public void setAttachment(String key, String value)
	{
		attachments.put(key, value);
	}

	public String toString()
	{
		return (new StringBuilder()).append("RpcResult [result=").append(result).append(", exception=").append(exception).append("]").toString();
	}
}
