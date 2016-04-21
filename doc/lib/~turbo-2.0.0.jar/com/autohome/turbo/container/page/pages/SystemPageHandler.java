// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   SystemPageHandler.java

package com.autohome.turbo.container.page.pages;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.Version;
import com.autohome.turbo.common.utils.NetUtils;
import com.autohome.turbo.container.page.Page;
import com.autohome.turbo.container.page.PageHandler;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.*;

public class SystemPageHandler
	implements PageHandler
{

	private static final long SECOND = 1000L;
	private static final long MINUTE = 60000L;
	private static final long HOUR = 0x36ee80L;
	private static final long DAY = 0x5265c00L;

	public SystemPageHandler()
	{
	}

	public Page handle(URL url)
	{
		List rows = new ArrayList();
		List row = new ArrayList();
		row.add("Version");
		row.add(Version.getVersion(com/autohome/turbo/container/page/pages/SystemPageHandler, "2.0.0"));
		rows.add(row);
		row = new ArrayList();
		row.add("Host");
		String address = NetUtils.getLocalHost();
		row.add((new StringBuilder()).append(NetUtils.getHostName(address)).append("/").append(address).toString());
		rows.add(row);
		row = new ArrayList();
		row.add("OS");
		row.add((new StringBuilder()).append(System.getProperty("os.name")).append(" ").append(System.getProperty("os.version")).toString());
		rows.add(row);
		row = new ArrayList();
		row.add("JVM");
		row.add((new StringBuilder()).append(System.getProperty("java.runtime.name")).append(" ").append(System.getProperty("java.runtime.version")).append(",<br/>").append(System.getProperty("java.vm.name")).append(" ").append(System.getProperty("java.vm.version")).append(" ").append(System.getProperty("java.vm.info", "")).toString());
		rows.add(row);
		row = new ArrayList();
		row.add("CPU");
		row.add((new StringBuilder()).append(System.getProperty("os.arch", "")).append(", ").append(String.valueOf(Runtime.getRuntime().availableProcessors())).append(" cores").toString());
		rows.add(row);
		row = new ArrayList();
		row.add("Locale");
		row.add((new StringBuilder()).append(Locale.getDefault().toString()).append("/").append(System.getProperty("file.encoding")).toString());
		rows.add(row);
		row = new ArrayList();
		row.add("Uptime");
		row.add(formatUptime(ManagementFactory.getRuntimeMXBean().getUptime()));
		rows.add(row);
		row = new ArrayList();
		row.add("Time");
		row.add((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z")).format(new Date()));
		rows.add(row);
		return new Page("System", "System", new String[] {
			"Property", "Value"
		}, rows);
	}

	private String formatUptime(long uptime)
	{
		StringBuilder buf = new StringBuilder();
		if (uptime > 0x5265c00L)
		{
			long days = (uptime - uptime % 0x5265c00L) / 0x5265c00L;
			buf.append(days);
			buf.append(" Days");
			uptime %= 0x5265c00L;
		}
		if (uptime > 0x36ee80L)
		{
			long hours = (uptime - uptime % 0x36ee80L) / 0x36ee80L;
			if (buf.length() > 0)
				buf.append(", ");
			buf.append(hours);
			buf.append(" Hours");
			uptime %= 0x36ee80L;
		}
		if (uptime > 60000L)
		{
			long minutes = (uptime - uptime % 60000L) / 60000L;
			if (buf.length() > 0)
				buf.append(", ");
			buf.append(minutes);
			buf.append(" Minutes");
			uptime %= 60000L;
		}
		if (uptime > 1000L)
		{
			long seconds = (uptime - uptime % 1000L) / 1000L;
			if (buf.length() > 0)
				buf.append(", ");
			buf.append(seconds);
			buf.append(" Seconds");
			uptime %= 1000L;
		}
		if (uptime > 0L)
		{
			if (buf.length() > 0)
				buf.append(", ");
			buf.append(uptime);
			buf.append(" Milliseconds");
		}
		return buf.toString();
	}
}
