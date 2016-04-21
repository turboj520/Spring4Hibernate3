// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   DubboMonitorFactroy.java

package com.autohome.turbo.monitor.dubbo;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.monitor.Monitor;
import com.autohome.turbo.monitor.MonitorService;
import com.autohome.turbo.monitor.support.AbstractMonitorFactory;
import com.autohome.turbo.rpc.Protocol;
import com.autohome.turbo.rpc.ProxyFactory;

// Referenced classes of package com.autohome.turbo.monitor.dubbo:
//			DubboMonitor

public class DubboMonitorFactroy extends AbstractMonitorFactory
{

	private Protocol protocol;
	private ProxyFactory proxyFactory;

	public DubboMonitorFactroy()
	{
	}

	public void setProtocol(Protocol protocol)
	{
		this.protocol = protocol;
	}

	public void setProxyFactory(ProxyFactory proxyFactory)
	{
		this.proxyFactory = proxyFactory;
	}

	protected Monitor createMonitor(URL url)
	{
		url = url.setProtocol(url.getParameter("protocol", "dubbo"));
		if (url.getPath() == null || url.getPath().length() == 0)
			url = url.setPath(com/autohome/turbo/monitor/MonitorService.getName());
		String filter = url.getParameter("reference.filter");
		if (filter == null || filter.length() == 0)
			filter = "";
		else
			filter = (new StringBuilder()).append(filter).append(",").toString();
		url = url.addParameters(new String[] {
			"cluster", "failsafe", "check", String.valueOf(false), "reference.filter", (new StringBuilder()).append(filter).append("-monitor").toString()
		});
		com.autohome.turbo.rpc.Invoker monitorInvoker = protocol.refer(com/autohome/turbo/monitor/MonitorService, url);
		MonitorService monitorService = (MonitorService)proxyFactory.getProxy(monitorInvoker);
		return new DubboMonitor(monitorInvoker, monitorService);
	}
}
