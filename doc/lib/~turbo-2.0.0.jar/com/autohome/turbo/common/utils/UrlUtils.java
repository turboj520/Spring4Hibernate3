// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   UrlUtils.java

package com.autohome.turbo.common.utils;

import com.autohome.turbo.common.Constants;
import com.autohome.turbo.common.URL;
import java.util.*;
import java.util.regex.Pattern;

// Referenced classes of package com.autohome.turbo.common.utils:
//			StringUtils

public class UrlUtils
{

	public UrlUtils()
	{
	}

	public static URL parseURL(String address, Map defaults)
	{
		if (address == null || address.length() == 0)
			return null;
		String url;
		if (address.indexOf("://") >= 0)
		{
			url = address;
		} else
		{
			String addresses[] = Constants.COMMA_SPLIT_PATTERN.split(address);
			url = addresses[0];
			if (addresses.length > 1)
			{
				StringBuilder backup = new StringBuilder();
				for (int i = 1; i < addresses.length; i++)
				{
					if (i > 1)
						backup.append(",");
					backup.append(addresses[i]);
				}

				url = (new StringBuilder()).append(url).append("?backup=").append(backup.toString()).toString();
			}
		}
		String defaultProtocol = defaults != null ? (String)defaults.get("protocol") : null;
		if (defaultProtocol == null || defaultProtocol.length() == 0)
			defaultProtocol = "dubbo";
		String defaultUsername = defaults != null ? (String)defaults.get("username") : null;
		String defaultPassword = defaults != null ? (String)defaults.get("password") : null;
		int defaultPort = StringUtils.parseInteger(defaults != null ? (String)defaults.get("port") : null);
		String defaultPath = defaults != null ? (String)defaults.get("path") : null;
		Map defaultParameters = defaults != null ? ((Map) (new HashMap(defaults))) : null;
		if (defaultParameters != null)
		{
			defaultParameters.remove("protocol");
			defaultParameters.remove("username");
			defaultParameters.remove("password");
			defaultParameters.remove("host");
			defaultParameters.remove("port");
			defaultParameters.remove("path");
		}
		URL u = URL.valueOf(url);
		boolean changed = false;
		String protocol = u.getProtocol();
		String username = u.getUsername();
		String password = u.getPassword();
		String host = u.getHost();
		int port = u.getPort();
		String path = u.getPath();
		Map parameters = new HashMap(u.getParameters());
		if ((protocol == null || protocol.length() == 0) && defaultProtocol != null && defaultProtocol.length() > 0)
		{
			changed = true;
			protocol = defaultProtocol;
		}
		if ((username == null || username.length() == 0) && defaultUsername != null && defaultUsername.length() > 0)
		{
			changed = true;
			username = defaultUsername;
		}
		if ((password == null || password.length() == 0) && defaultPassword != null && defaultPassword.length() > 0)
		{
			changed = true;
			password = defaultPassword;
		}
		if (port <= 0)
			if (defaultPort > 0)
			{
				changed = true;
				port = defaultPort;
			} else
			{
				changed = true;
				port = 9090;
			}
		if ((path == null || path.length() == 0) && defaultPath != null && defaultPath.length() > 0)
		{
			changed = true;
			path = defaultPath;
		}
		if (defaultParameters != null && defaultParameters.size() > 0)
		{
			Iterator i$ = defaultParameters.entrySet().iterator();
			do
			{
				if (!i$.hasNext())
					break;
				java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
				String key = (String)entry.getKey();
				String defaultValue = (String)entry.getValue();
				if (defaultValue != null && defaultValue.length() > 0)
				{
					String value = (String)parameters.get(key);
					if (value == null || value.length() == 0)
					{
						changed = true;
						parameters.put(key, defaultValue);
					}
				}
			} while (true);
		}
		if (changed)
			u = new URL(protocol, username, password, host, port, path, parameters);
		return u;
	}

	public static List parseURLs(String address, Map defaults)
	{
		if (address == null || address.length() == 0)
			return null;
		String addresses[] = Constants.REGISTRY_SPLIT_PATTERN.split(address);
		if (addresses == null || addresses.length == 0)
			return null;
		List registries = new ArrayList();
		String arr$[] = addresses;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			String addr = arr$[i$];
			registries.add(parseURL(addr, defaults));
		}

		return registries;
	}

	public static Map convertRegister(Map register)
	{
		Map newRegister = new HashMap();
		for (Iterator i$ = register.entrySet().iterator(); i$.hasNext();)
		{
			java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
			String serviceName = (String)entry.getKey();
			Map serviceUrls = (Map)entry.getValue();
			if (!serviceName.contains(":") && !serviceName.contains("/"))
			{
				Iterator i$ = serviceUrls.entrySet().iterator();
				while (i$.hasNext()) 
				{
					java.util.Map.Entry entry2 = (java.util.Map.Entry)i$.next();
					String serviceUrl = (String)entry2.getKey();
					String serviceQuery = (String)entry2.getValue();
					Map params = StringUtils.parseQueryString(serviceQuery);
					String group = (String)params.get("group");
					String version = (String)params.get("version");
					String name = serviceName;
					if (group != null && group.length() > 0)
						name = (new StringBuilder()).append(group).append("/").append(name).toString();
					if (version != null && version.length() > 0)
						name = (new StringBuilder()).append(name).append(":").append(version).toString();
					Map newUrls = (Map)newRegister.get(name);
					if (newUrls == null)
					{
						newUrls = new HashMap();
						newRegister.put(name, newUrls);
					}
					newUrls.put(serviceUrl, StringUtils.toQueryString(params));
				}
			} else
			{
				newRegister.put(serviceName, serviceUrls);
			}
		}

		return newRegister;
	}

	public static Map convertSubscribe(Map subscribe)
	{
		Map newSubscribe = new HashMap();
		for (Iterator i$ = subscribe.entrySet().iterator(); i$.hasNext();)
		{
			java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
			String serviceName = (String)entry.getKey();
			String serviceQuery = (String)entry.getValue();
			if (!serviceName.contains(":") && !serviceName.contains("/"))
			{
				Map params = StringUtils.parseQueryString(serviceQuery);
				String group = (String)params.get("group");
				String version = (String)params.get("version");
				String name = serviceName;
				if (group != null && group.length() > 0)
					name = (new StringBuilder()).append(group).append("/").append(name).toString();
				if (version != null && version.length() > 0)
					name = (new StringBuilder()).append(name).append(":").append(version).toString();
				newSubscribe.put(name, StringUtils.toQueryString(params));
			} else
			{
				newSubscribe.put(serviceName, serviceQuery);
			}
		}

		return newSubscribe;
	}

	public static Map revertRegister(Map register)
	{
		Map newRegister = new HashMap();
		for (Iterator i$ = register.entrySet().iterator(); i$.hasNext();)
		{
			java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
			String serviceName = (String)entry.getKey();
			Map serviceUrls = (Map)entry.getValue();
			if (serviceName.contains(":") || serviceName.contains("/"))
			{
				Iterator i$ = serviceUrls.entrySet().iterator();
				while (i$.hasNext()) 
				{
					java.util.Map.Entry entry2 = (java.util.Map.Entry)i$.next();
					String serviceUrl = (String)entry2.getKey();
					String serviceQuery = (String)entry2.getValue();
					Map params = StringUtils.parseQueryString(serviceQuery);
					String name = serviceName;
					int i = name.indexOf('/');
					if (i >= 0)
					{
						params.put("group", name.substring(0, i));
						name = name.substring(i + 1);
					}
					i = name.lastIndexOf(':');
					if (i >= 0)
					{
						params.put("version", name.substring(i + 1));
						name = name.substring(0, i);
					}
					Map newUrls = (Map)newRegister.get(name);
					if (newUrls == null)
					{
						newUrls = new HashMap();
						newRegister.put(name, newUrls);
					}
					newUrls.put(serviceUrl, StringUtils.toQueryString(params));
				}
			} else
			{
				newRegister.put(serviceName, serviceUrls);
			}
		}

		return newRegister;
	}

	public static Map revertSubscribe(Map subscribe)
	{
		Map newSubscribe = new HashMap();
		for (Iterator i$ = subscribe.entrySet().iterator(); i$.hasNext();)
		{
			java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
			String serviceName = (String)entry.getKey();
			String serviceQuery = (String)entry.getValue();
			if (serviceName.contains(":") || serviceName.contains("/"))
			{
				Map params = StringUtils.parseQueryString(serviceQuery);
				String name = serviceName;
				int i = name.indexOf('/');
				if (i >= 0)
				{
					params.put("group", name.substring(0, i));
					name = name.substring(i + 1);
				}
				i = name.lastIndexOf(':');
				if (i >= 0)
				{
					params.put("version", name.substring(i + 1));
					name = name.substring(0, i);
				}
				newSubscribe.put(name, StringUtils.toQueryString(params));
			} else
			{
				newSubscribe.put(serviceName, serviceQuery);
			}
		}

		return newSubscribe;
	}

	public static Map revertNotify(Map notify)
	{
		if (notify != null && notify.size() > 0)
		{
			Map newNotify = new HashMap();
			Iterator i$ = notify.entrySet().iterator();
			do
			{
				if (!i$.hasNext())
					break;
				java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
				String serviceName = (String)entry.getKey();
				Map serviceUrls = (Map)entry.getValue();
				if (!serviceName.contains(":") && !serviceName.contains("/"))
				{
					if (serviceUrls != null && serviceUrls.size() > 0)
					{
						Iterator i$ = serviceUrls.entrySet().iterator();
						while (i$.hasNext()) 
						{
							java.util.Map.Entry entry2 = (java.util.Map.Entry)i$.next();
							String url = (String)entry2.getKey();
							String query = (String)entry2.getValue();
							Map params = StringUtils.parseQueryString(query);
							String group = (String)params.get("group");
							String version = (String)params.get("version");
							String name = serviceName;
							if (group != null && group.length() > 0)
								name = (new StringBuilder()).append(group).append("/").append(name).toString();
							if (version != null && version.length() > 0)
								name = (new StringBuilder()).append(name).append(":").append(version).toString();
							Map newUrls = (Map)newNotify.get(name);
							if (newUrls == null)
							{
								newUrls = new HashMap();
								newNotify.put(name, newUrls);
							}
							newUrls.put(url, StringUtils.toQueryString(params));
						}
					}
				} else
				{
					newNotify.put(serviceName, serviceUrls);
				}
			} while (true);
			return newNotify;
		} else
		{
			return notify;
		}
	}

	public static List revertForbid(List forbid, Set subscribed)
	{
		if (forbid != null && forbid.size() > 0)
		{
			List newForbid = new ArrayList();
			Iterator i$ = forbid.iterator();
label0:
			do
			{
				if (!i$.hasNext())
					break;
				String serviceName = (String)i$.next();
				if (!serviceName.contains(":") && !serviceName.contains("/"))
				{
					Iterator i$ = subscribed.iterator();
					URL url;
					do
					{
						if (!i$.hasNext())
							continue label0;
						url = (URL)i$.next();
					} while (!serviceName.equals(url.getServiceInterface()));
					newForbid.add(url.getServiceKey());
				} else
				{
					newForbid.add(serviceName);
				}
			} while (true);
			return newForbid;
		} else
		{
			return forbid;
		}
	}

	public static URL getEmptyUrl(String service, String category)
	{
		String group = null;
		String version = null;
		int i = service.indexOf('/');
		if (i > 0)
		{
			group = service.substring(0, i);
			service = service.substring(i + 1);
		}
		i = service.lastIndexOf(':');
		if (i > 0)
		{
			version = service.substring(i + 1);
			service = service.substring(0, i);
		}
		return URL.valueOf((new StringBuilder()).append("empty://0.0.0.0/").append(service).append("?").append("category").append("=").append(category).append(group != null ? (new StringBuilder()).append("&group=").append(group).toString() : "").append(version != null ? (new StringBuilder()).append("&version=").append(version).toString() : "").toString());
	}

	public static boolean isMatchCategory(String category, String categories)
	{
		if (categories == null || categories.length() == 0)
			return "providers".equals(category);
		if (categories.contains("*"))
			return true;
		if (categories.contains("-"))
			return !categories.contains((new StringBuilder()).append("-").append(category).toString());
		else
			return categories.contains(category);
	}

	public static boolean isMatch(URL consumerUrl, URL providerUrl)
	{
		if (StringUtils.isNotEmpty(consumerUrl.getParameter("csharp")))
			if ("route".equalsIgnoreCase(providerUrl.getProtocol()))
				return consumerUrl.getHost().equalsIgnoreCase(providerUrl.getHost());
			else
				return true;
		String consumerInterface = consumerUrl.getServiceInterface();
		String providerInterface = providerUrl.getServiceInterface();
		if (!"*".equals(consumerInterface) && !StringUtils.isEquals(consumerInterface, providerInterface))
			return false;
		if (!isMatchCategory(providerUrl.getParameter("category", "providers"), consumerUrl.getParameter("category", "providers")))
			return false;
		if (!providerUrl.getParameter("enabled", true) && !"*".equals(consumerUrl.getParameter("enabled")))
		{
			return false;
		} else
		{
			String consumerGroup = consumerUrl.getParameter("group");
			String consumerVersion = consumerUrl.getParameter("version");
			String consumerClassifier = consumerUrl.getParameter("classifier", "*");
			String providerGroup = providerUrl.getParameter("group");
			String providerVersion = providerUrl.getParameter("version");
			String providerClassifier = providerUrl.getParameter("classifier", "*");
			return ("*".equals(consumerGroup) || StringUtils.isEquals(consumerGroup, providerGroup) || StringUtils.isContains(consumerGroup, providerGroup)) && ("*".equals(consumerVersion) || StringUtils.isEquals(consumerVersion, providerVersion)) && (consumerClassifier == null || "*".equals(consumerClassifier) || StringUtils.isEquals(consumerClassifier, providerClassifier));
		}
	}

	public static boolean isMatchGlobPattern(String pattern, String value, URL param)
	{
		if (param != null && pattern.startsWith("$"))
			pattern = param.getRawParameter(pattern.substring(1));
		return isMatchGlobPattern(pattern, value);
	}

	public static boolean isMatchGlobPattern(String pattern, String value)
	{
		if ("*".equals(pattern))
			return true;
		if ((pattern == null || pattern.length() == 0) && (value == null || value.length() == 0))
			return true;
		if (pattern == null || pattern.length() == 0 || value == null || value.length() == 0)
			return false;
		int i = pattern.lastIndexOf('*');
		if (i == -1)
			return value.equals(pattern);
		if (i == pattern.length() - 1)
			return value.startsWith(pattern.substring(0, i));
		if (i == 0)
		{
			return value.endsWith(pattern.substring(i + 1));
		} else
		{
			String prefix = pattern.substring(0, i);
			String suffix = pattern.substring(i + 1);
			return value.startsWith(prefix) && value.endsWith(suffix);
		}
	}

	public static boolean isServiceKeyMatch(URL pattern, URL value)
	{
		return pattern.getParameter("interface").equals(value.getParameter("interface")) && isItemMatch(pattern.getParameter("group"), value.getParameter("group")) && isItemMatch(pattern.getParameter("version"), value.getParameter("version"));
	}

	static boolean isItemMatch(String pattern, String value)
	{
		if (pattern == null)
			return value == null;
		else
			return "*".equals(pattern) || pattern.equals(value);
	}
}
