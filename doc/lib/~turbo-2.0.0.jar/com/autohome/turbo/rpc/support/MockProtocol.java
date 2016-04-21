// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   MockProtocol.java

package com.autohome.turbo.rpc.support;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.protocol.AbstractProtocol;

// Referenced classes of package com.autohome.turbo.rpc.support:
//			MockInvoker

public final class MockProtocol extends AbstractProtocol
{

	public MockProtocol()
	{
	}

	public int getDefaultPort()
	{
		return 0;
	}

	public Exporter export(Invoker invoker)
		throws RpcException
	{
		throw new UnsupportedOperationException();
	}

	public Invoker refer(Class type, URL url)
		throws RpcException
	{
		return new MockInvoker(url);
	}
}
