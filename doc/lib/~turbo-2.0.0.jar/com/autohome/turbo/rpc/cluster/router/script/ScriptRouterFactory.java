// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ScriptRouterFactory.java

package com.autohome.turbo.rpc.cluster.router.script;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.rpc.cluster.Router;
import com.autohome.turbo.rpc.cluster.RouterFactory;

// Referenced classes of package com.autohome.turbo.rpc.cluster.router.script:
//			ScriptRouter

public class ScriptRouterFactory
	implements RouterFactory
{

	public static final String NAME = "script";

	public ScriptRouterFactory()
	{
	}

	public Router getRouter(URL url)
	{
		return new ScriptRouter(url);
	}
}
