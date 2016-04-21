// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ConditionRouterFactory.java

package com.autohome.turbo.rpc.cluster.router.condition;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.rpc.cluster.Router;
import com.autohome.turbo.rpc.cluster.RouterFactory;

// Referenced classes of package com.autohome.turbo.rpc.cluster.router.condition:
//			ConditionRouter

public class ConditionRouterFactory
	implements RouterFactory
{

	public static final String NAME = "condition";

	public ConditionRouterFactory()
	{
	}

	public Router getRouter(URL url)
	{
		return new ConditionRouter(url);
	}
}
