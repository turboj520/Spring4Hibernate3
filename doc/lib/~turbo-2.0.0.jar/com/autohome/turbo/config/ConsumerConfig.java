// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ConsumerConfig.java

package com.autohome.turbo.config;


// Referenced classes of package com.autohome.turbo.config:
//			AbstractReferenceConfig

public class ConsumerConfig extends AbstractReferenceConfig
{

	private static final long serialVersionUID = 0x273c7f5ec82a0a58L;
	private Boolean isDefault;

	public ConsumerConfig()
	{
	}

	public void setTimeout(Integer timeout)
	{
		super.setTimeout(timeout);
		String rmiTimeout = System.getProperty("sun.rmi.transport.tcp.responseTimeout");
		if (timeout != null && timeout.intValue() > 0 && (rmiTimeout == null || rmiTimeout.length() == 0))
			System.setProperty("sun.rmi.transport.tcp.responseTimeout", String.valueOf(timeout));
	}

	public Boolean isDefault()
	{
		return isDefault;
	}

	public void setDefault(Boolean isDefault)
	{
		this.isDefault = isDefault;
	}
}
