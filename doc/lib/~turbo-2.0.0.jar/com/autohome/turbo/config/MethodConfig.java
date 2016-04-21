// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   MethodConfig.java

package com.autohome.turbo.config;

import java.util.List;

// Referenced classes of package com.autohome.turbo.config:
//			AbstractMethodConfig

public class MethodConfig extends AbstractMethodConfig
{

	private static final long serialVersionUID = 0xc47d3ea6210ebe5L;
	private String name;
	private Integer stat;
	private Boolean retry;
	private Boolean reliable;
	private Integer executes;
	private Boolean deprecated;
	private Boolean sticky;
	private Boolean isReturn;
	private Object oninvoke;
	private String oninvokeMethod;
	private Object onreturn;
	private String onreturnMethod;
	private Object onthrow;
	private String onthrowMethod;
	private List arguments;

	public MethodConfig()
	{
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		checkMethodName("name", name);
		this.name = name;
		if (id == null || id.length() == 0)
			id = name;
	}

	public Integer getStat()
	{
		return stat;
	}

	/**
	 * @deprecated Method setStat is deprecated
	 */

	public void setStat(Integer stat)
	{
		this.stat = stat;
	}

	/**
	 * @deprecated Method isRetry is deprecated
	 */

	public Boolean isRetry()
	{
		return retry;
	}

	/**
	 * @deprecated Method setRetry is deprecated
	 */

	public void setRetry(Boolean retry)
	{
		this.retry = retry;
	}

	/**
	 * @deprecated Method isReliable is deprecated
	 */

	public Boolean isReliable()
	{
		return reliable;
	}

	/**
	 * @deprecated Method setReliable is deprecated
	 */

	public void setReliable(Boolean reliable)
	{
		this.reliable = reliable;
	}

	public Integer getExecutes()
	{
		return executes;
	}

	public void setExecutes(Integer executes)
	{
		this.executes = executes;
	}

	public Boolean getDeprecated()
	{
		return deprecated;
	}

	public void setDeprecated(Boolean deprecated)
	{
		this.deprecated = deprecated;
	}

	public void setArguments(List arguments)
	{
		this.arguments = arguments;
	}

	public List getArguments()
	{
		return arguments;
	}

	public Boolean getSticky()
	{
		return sticky;
	}

	public void setSticky(Boolean sticky)
	{
		this.sticky = sticky;
	}

	public Object getOnreturn()
	{
		return onreturn;
	}

	public void setOnreturn(Object onreturn)
	{
		this.onreturn = onreturn;
	}

	public String getOnreturnMethod()
	{
		return onreturnMethod;
	}

	public void setOnreturnMethod(String onreturnMethod)
	{
		this.onreturnMethod = onreturnMethod;
	}

	public Object getOnthrow()
	{
		return onthrow;
	}

	public void setOnthrow(Object onthrow)
	{
		this.onthrow = onthrow;
	}

	public String getOnthrowMethod()
	{
		return onthrowMethod;
	}

	public void setOnthrowMethod(String onthrowMethod)
	{
		this.onthrowMethod = onthrowMethod;
	}

	public Object getOninvoke()
	{
		return oninvoke;
	}

	public void setOninvoke(Object oninvoke)
	{
		this.oninvoke = oninvoke;
	}

	public String getOninvokeMethod()
	{
		return oninvokeMethod;
	}

	public void setOninvokeMethod(String oninvokeMethod)
	{
		this.oninvokeMethod = oninvokeMethod;
	}

	public Boolean isReturn()
	{
		return isReturn;
	}

	public void setReturn(Boolean isReturn)
	{
		this.isReturn = isReturn;
	}
}
