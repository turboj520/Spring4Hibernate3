// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ScriptRouter.java

package com.autohome.turbo.rpc.cluster.router.script;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.cluster.Router;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import javax.script.*;

public class ScriptRouter
	implements Router
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/rpc/cluster/router/script/ScriptRouter);
	private static final Map engines = new ConcurrentHashMap();
	private final ScriptEngine engine;
	private final int priority;
	private final String rule;
	private final URL url;

	public URL getUrl()
	{
		return url;
	}

	public ScriptRouter(URL url)
	{
		this.url = url;
		String type = url.getParameter("type");
		priority = url.getParameter("priority", 0);
		String rule = url.getParameterAndDecoded("rule");
		if (type == null || type.length() == 0)
			type = "javascript";
		if (rule == null || rule.length() == 0)
			throw new IllegalStateException(new IllegalStateException((new StringBuilder()).append("route rule can not be empty. rule:").append(rule).toString()));
		ScriptEngine engine = (ScriptEngine)engines.get(type);
		if (engine == null)
		{
			engine = (new ScriptEngineManager()).getEngineByName(type);
			if (engine == null)
				throw new IllegalStateException(new IllegalStateException((new StringBuilder()).append("Unsupported route rule type: ").append(type).append(", rule: ").append(rule).toString()));
			engines.put(type, engine);
		}
		this.engine = engine;
		this.rule = rule;
	}

	public List route(List invokers, URL url, Invocation invocation)
		throws RpcException
	{
		List invokersCopy;
		invokersCopy = new ArrayList(invokers);
		Compilable compilable = (Compilable)engine;
		Bindings bindings = engine.createBindings();
		bindings.put("invokers", invokersCopy);
		bindings.put("invocation", invocation);
		bindings.put("context", RpcContext.getContext());
		CompiledScript function = compilable.compile(rule);
		Object obj = function.eval(bindings);
		if (obj instanceof Invoker[])
			invokersCopy = Arrays.asList((Invoker[])(Invoker[])obj);
		else
		if (obj instanceof Object[])
		{
			invokersCopy = new ArrayList();
			Object arr$[] = (Object[])(Object[])obj;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				Object inv = arr$[i$];
				invokersCopy.add((Invoker)inv);
			}

		} else
		{
			invokersCopy = (List)obj;
		}
		return invokersCopy;
		ScriptException e;
		e;
		logger.error((new StringBuilder()).append("route error , rule has been ignored. rule: ").append(rule).append(", method:").append(invocation.getMethodName()).append(", url: ").append(RpcContext.getContext().getUrl()).toString(), e);
		return invokers;
	}

	public int compareTo(Router o)
	{
		if (o == null || o.getClass() != com/autohome/turbo/rpc/cluster/router/script/ScriptRouter)
		{
			return 1;
		} else
		{
			ScriptRouter c = (ScriptRouter)o;
			return priority != c.priority ? ((byte)(priority <= c.priority ? -1 : 1)) : rule.compareTo(c.rule);
		}
	}

	public volatile int compareTo(Object x0)
	{
		return compareTo((Router)x0);
	}

}
