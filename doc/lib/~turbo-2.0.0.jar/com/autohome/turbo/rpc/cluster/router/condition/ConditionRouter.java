// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ConditionRouter.java

package com.autohome.turbo.rpc.cluster.router.condition;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.*;
import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.cluster.Router;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConditionRouter
	implements Router, Comparable
{
	private static final class MatchPair
	{

		final Set matches;
		final Set mismatches;

		public boolean isMatch(String value, URL param)
		{
			for (Iterator i$ = matches.iterator(); i$.hasNext();)
			{
				String match = (String)i$.next();
				if (!UrlUtils.isMatchGlobPattern(match, value, param))
					return false;
			}

			for (Iterator i$ = mismatches.iterator(); i$.hasNext();)
			{
				String mismatch = (String)i$.next();
				if (UrlUtils.isMatchGlobPattern(mismatch, value, param))
					return false;
			}

			return true;
		}

		private MatchPair()
		{
			matches = new HashSet();
			mismatches = new HashSet();
		}

	}


	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/rpc/cluster/router/condition/ConditionRouter);
	private final URL url;
	private final int priority;
	private final boolean force;
	private final Map whenCondition;
	private final Map thenCondition;
	private static Pattern ROUTE_PATTERN = Pattern.compile("([&!=,]*)\\s*([^&!=,\\s]+)");

	public ConditionRouter(URL url)
	{
		this.url = url;
		priority = url.getParameter("priority", 0);
		force = url.getParameter("force", false);
		try
		{
			String rule = url.getParameterAndDecoded("rule");
			if (rule == null || rule.trim().length() == 0)
				throw new IllegalArgumentException("Illegal route rule!");
			rule = rule.replace("consumer.", "").replace("provider.", "");
			int i = rule.indexOf("=>");
			String whenRule = i >= 0 ? rule.substring(0, i).trim() : null;
			String thenRule = i >= 0 ? rule.substring(i + 2).trim() : rule.trim();
			Map when = ((Map) (!StringUtils.isBlank(whenRule) && !"true".equals(whenRule) ? parseRule(whenRule) : ((Map) (new HashMap()))));
			Map then = !StringUtils.isBlank(thenRule) && !"false".equals(thenRule) ? parseRule(thenRule) : null;
			whenCondition = when;
			thenCondition = then;
		}
		catch (ParseException e)
		{
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public List route(List invokers, URL url, Invocation invocation)
		throws RpcException
	{
		if (invokers == null || invokers.size() == 0)
			return invokers;
		if (!matchWhen(url))
			return invokers;
		List result;
		result = new ArrayList();
		if (thenCondition != null)
			break MISSING_BLOCK_LABEL_85;
		logger.warn((new StringBuilder()).append("The current consumer in the service blacklist. consumer: ").append(NetUtils.getLocalHost()).append(", service: ").append(url.getServiceKey()).toString());
		return result;
		Iterator i$ = invokers.iterator();
		do
		{
			if (!i$.hasNext())
				break;
			Invoker invoker = (Invoker)i$.next();
			if (matchThen(invoker.getUrl(), url))
				result.add(invoker);
		} while (true);
		if (result.size() > 0)
			return result;
		if (!force)
			break MISSING_BLOCK_LABEL_280;
		logger.warn((new StringBuilder()).append("The route result is empty and force execute. consumer: ").append(NetUtils.getLocalHost()).append(", service: ").append(url.getServiceKey()).append(", router: ").append(url.getParameterAndDecoded("rule")).toString());
		return result;
		Throwable t;
		t;
		logger.error((new StringBuilder()).append("Failed to execute condition router rule: ").append(getUrl()).append(", invokers: ").append(invokers).append(", cause: ").append(t.getMessage()).toString(), t);
		return invokers;
	}

	public URL getUrl()
	{
		return url;
	}

	public int compareTo(Router o)
	{
		if (o == null || o.getClass() != com/autohome/turbo/rpc/cluster/router/condition/ConditionRouter)
		{
			return 1;
		} else
		{
			ConditionRouter c = (ConditionRouter)o;
			return priority != c.priority ? ((byte)(priority <= c.priority ? -1 : 1)) : url.toFullString().compareTo(c.url.toFullString());
		}
	}

	public boolean matchWhen(URL url)
	{
		return matchCondition(whenCondition, url, null);
	}

	public boolean matchThen(URL url, URL param)
	{
		return thenCondition != null && matchCondition(thenCondition, url, param);
	}

	private boolean matchCondition(Map condition, URL url, URL param)
	{
		Map sample = url.toMap();
		for (Iterator i$ = sample.entrySet().iterator(); i$.hasNext();)
		{
			java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
			String key = (String)entry.getKey();
			MatchPair pair = (MatchPair)condition.get(key);
			if (pair != null && !pair.isMatch((String)entry.getValue(), param))
				return false;
		}

		return true;
	}

	private static Map parseRule(String rule)
		throws ParseException
	{
		Map condition = new HashMap();
		if (StringUtils.isBlank(rule))
			return condition;
		MatchPair pair = null;
		Set values = null;
		for (Matcher matcher = ROUTE_PATTERN.matcher(rule); matcher.find();)
		{
			String separator = matcher.group(1);
			String content = matcher.group(2);
			if (separator == null || separator.length() == 0)
			{
				pair = new MatchPair();
				condition.put(content, pair);
			} else
			if ("&".equals(separator))
			{
				if (condition.get(content) == null)
				{
					pair = new MatchPair();
					condition.put(content, pair);
				} else
				{
					condition.put(content, pair);
				}
			} else
			if ("=".equals(separator))
			{
				if (pair == null)
					throw new ParseException((new StringBuilder()).append("Illegal route rule \"").append(rule).append("\", The error char '").append(separator).append("' at index ").append(matcher.start()).append(" before \"").append(content).append("\".").toString(), matcher.start());
				values = pair.matches;
				values.add(content);
			} else
			if ("!=".equals(separator))
			{
				if (pair == null)
					throw new ParseException((new StringBuilder()).append("Illegal route rule \"").append(rule).append("\", The error char '").append(separator).append("' at index ").append(matcher.start()).append(" before \"").append(content).append("\".").toString(), matcher.start());
				values = pair.mismatches;
				values.add(content);
			} else
			if (",".equals(separator))
			{
				if (values == null || values.size() == 0)
					throw new ParseException((new StringBuilder()).append("Illegal route rule \"").append(rule).append("\", The error char '").append(separator).append("' at index ").append(matcher.start()).append(" before \"").append(content).append("\".").toString(), matcher.start());
				values.add(content);
			} else
			{
				throw new ParseException((new StringBuilder()).append("Illegal route rule \"").append(rule).append("\", The error char '").append(separator).append("' at index ").append(matcher.start()).append(" before \"").append(content).append("\".").toString(), matcher.start());
			}
		}

		return condition;
	}

	public volatile int compareTo(Object x0)
	{
		return compareTo((Router)x0);
	}

}
