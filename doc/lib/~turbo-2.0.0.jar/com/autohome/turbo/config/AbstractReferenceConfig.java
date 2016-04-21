// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractReferenceConfig.java

package com.autohome.turbo.config;

import com.autohome.turbo.rpc.InvokerListener;
import com.autohome.turbo.rpc.support.ProtocolUtils;

// Referenced classes of package com.autohome.turbo.config:
//			AbstractInterfaceConfig

public abstract class AbstractReferenceConfig extends AbstractInterfaceConfig
{

	private static final long serialVersionUID = 0xd9544478ab67af2aL;
	protected Boolean check;
	protected Boolean init;
	protected String generic;
	protected Boolean injvm;
	protected Boolean lazy;
	protected String reconnect;
	protected Boolean sticky;
	protected Boolean stubevent;
	protected String version;
	protected String group;

	public AbstractReferenceConfig()
	{
	}

	public Boolean isCheck()
	{
		return check;
	}

	public void setCheck(Boolean check)
	{
		this.check = check;
	}

	public Boolean isInit()
	{
		return init;
	}

	public void setInit(Boolean init)
	{
		this.init = init;
	}

	public Boolean isGeneric()
	{
		return Boolean.valueOf(ProtocolUtils.isGeneric(generic));
	}

	public void setGeneric(Boolean generic)
	{
		if (generic != null)
			this.generic = generic.toString();
	}

	public void setGeneric(String generic)
	{
		this.generic = generic;
	}

	public String getGeneric()
	{
		return generic;
	}

	/**
	 * @deprecated Method isInjvm is deprecated
	 */

	public Boolean isInjvm()
	{
		return injvm;
	}

	/**
	 * @deprecated Method setInjvm is deprecated
	 */

	public void setInjvm(Boolean injvm)
	{
		this.injvm = injvm;
	}

	public String getFilter()
	{
		return super.getFilter();
	}

	public String getListener()
	{
		return super.getListener();
	}

	public void setListener(String listener)
	{
		checkMultiExtension(com/autohome/turbo/rpc/InvokerListener, "listener", listener);
		super.setListener(listener);
	}

	public Boolean getLazy()
	{
		return lazy;
	}

	public void setLazy(Boolean lazy)
	{
		this.lazy = lazy;
	}

	public void setOnconnect(String onconnect)
	{
		if (onconnect != null && onconnect.length() > 0)
			stubevent = Boolean.valueOf(true);
		super.setOnconnect(onconnect);
	}

	public void setOndisconnect(String ondisconnect)
	{
		if (ondisconnect != null && ondisconnect.length() > 0)
			stubevent = Boolean.valueOf(true);
		super.setOndisconnect(ondisconnect);
	}

	public Boolean getStubevent()
	{
		return stubevent;
	}

	public String getReconnect()
	{
		return reconnect;
	}

	public void setReconnect(String reconnect)
	{
		this.reconnect = reconnect;
	}

	public Boolean getSticky()
	{
		return sticky;
	}

	public void setSticky(Boolean sticky)
	{
		this.sticky = sticky;
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		checkKey("version", version);
		this.version = version;
	}

	public String getGroup()
	{
		return group;
	}

	public void setGroup(String group)
	{
		checkKey("group", group);
		this.group = group;
	}
}
