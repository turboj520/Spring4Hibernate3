// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ClusterUtils.java

package com.autohome.turbo.rpc.cluster.support;

import com.autohome.turbo.common.URL;
import java.util.HashMap;
import java.util.Map;

public class ClusterUtils
{

	public static URL mergeUrl(URL remoteUrl, Map localMap)
	{
		Map map = new HashMap();
		Map remoteMap = remoteUrl.getParameters();
		if (remoteMap != null && remoteMap.size() > 0)
		{
			map.putAll(remoteMap);
			map.remove("threadname");
			map.remove("default.threadname");
			map.remove("threadpool");
			map.remove("default.threadpool");
			map.remove("corethreads");
			map.remove("default.corethreads");
			map.remove("threads");
			map.remove("default.threads");
			map.remove("queues");
			map.remove("default.queues");
			map.remove("alive");
			map.remove("default.alive");
		}
		if (localMap != null && localMap.size() > 0)
			map.putAll(localMap);
		if (remoteMap != null && remoteMap.size() > 0)
		{
			String dubbo = (String)remoteMap.get("dubbo");
			if (dubbo != null && dubbo.length() > 0)
				map.put("dubbo", dubbo);
			String version = (String)remoteMap.get("version");
			if (version != null && version.length() > 0)
				map.put("version", version);
			String group = (String)remoteMap.get("group");
			if (group != null && group.length() > 0)
				map.put("group", group);
			String methods = (String)remoteMap.get("methods");
			if (methods != null && methods.length() > 0)
				map.put("methods", methods);
			String remoteFilter = (String)remoteMap.get("reference.filter");
			String localFilter = (String)localMap.get("reference.filter");
			if (remoteFilter != null && remoteFilter.length() > 0 && localFilter != null && localFilter.length() > 0)
				localMap.put("reference.filter", (new StringBuilder()).append(remoteFilter).append(",").append(localFilter).toString());
			String remoteListener = (String)remoteMap.get("invoker.listener");
			String localListener = (String)localMap.get("invoker.listener");
			if (remoteListener != null && remoteListener.length() > 0 && localListener != null && localListener.length() > 0)
				localMap.put("invoker.listener", (new StringBuilder()).append(remoteListener).append(",").append(localListener).toString());
		}
		return remoteUrl.clearParameters().addParameters(map);
	}

	private ClusterUtils()
	{
	}
}
