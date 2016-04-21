// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ConfigUtils.java

package com.autohome.turbo.common.utils;

import com.autohome.turbo.common.Constants;
import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Referenced classes of package com.autohome.turbo.common.utils:
//			ClassHelper

public class ConfigUtils
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/common/utils/ConfigUtils);
	private static Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\s*\\{?\\s*([\\._0-9a-zA-Z]+)\\s*\\}?");
	private static volatile Properties PROPERTIES;
	private static int PID = -1;

	public static boolean isNotEmpty(String value)
	{
		return !isEmpty(value);
	}

	public static boolean isEmpty(String value)
	{
		return value == null || value.length() == 0 || "false".equalsIgnoreCase(value) || "0".equalsIgnoreCase(value) || "null".equalsIgnoreCase(value) || "N/A".equalsIgnoreCase(value);
	}

	public static boolean isDefault(String value)
	{
		return "true".equalsIgnoreCase(value) || "default".equalsIgnoreCase(value);
	}

	public static List mergeValues(Class type, String cfg, List def)
	{
		List defaults = new ArrayList();
		if (def != null)
		{
			Iterator i$ = def.iterator();
			do
			{
				if (!i$.hasNext())
					break;
				String name = (String)i$.next();
				if (ExtensionLoader.getExtensionLoader(type).hasExtension(name))
					defaults.add(name);
			} while (true);
		}
		List names = new ArrayList();
		String configs[] = cfg != null && cfg.trim().length() != 0 ? Constants.COMMA_SPLIT_PATTERN.split(cfg) : new String[0];
		String arr$[] = configs;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			String config = arr$[i$];
			if (config != null && config.trim().length() > 0)
				names.add(config);
		}

		if (!names.contains("-default"))
		{
			int i = names.indexOf("default");
			if (i > 0)
				names.addAll(i, defaults);
			else
				names.addAll(0, defaults);
			names.remove("default");
		} else
		{
			names.remove("default");
		}
		Iterator i$ = (new ArrayList(names)).iterator();
		do
		{
			if (!i$.hasNext())
				break;
			String name = (String)i$.next();
			if (name.startsWith("-"))
			{
				names.remove(name);
				names.remove(name.substring(1));
			}
		} while (true);
		return names;
	}

	public static String replaceProperty(String expression, Map params)
	{
		if (expression == null || expression.length() == 0 || expression.indexOf('$') < 0)
			return expression;
		Matcher matcher = VARIABLE_PATTERN.matcher(expression);
		StringBuffer sb = new StringBuffer();
		String value;
		for (; matcher.find(); matcher.appendReplacement(sb, Matcher.quoteReplacement(value)))
		{
			String key = matcher.group(1);
			value = System.getProperty(key);
			if (value == null && params != null)
				value = (String)params.get(key);
			if (value == null)
				value = "";
		}

		matcher.appendTail(sb);
		return sb.toString();
	}

	public static Properties getProperties()
	{
		if (PROPERTIES == null)
			synchronized (com/autohome/turbo/common/utils/ConfigUtils)
			{
				if (PROPERTIES == null)
				{
					String path = System.getProperty("dubbo.properties.file");
					if (path == null || path.length() == 0)
					{
						path = System.getenv("dubbo.properties.file");
						if (path == null || path.length() == 0)
							path = "dubbo.properties";
					}
					PROPERTIES = loadProperties(path, false, true);
				}
			}
		return PROPERTIES;
	}

	public static void addProperties(Properties properties)
	{
		if (properties != null)
			getProperties().putAll(properties);
	}

	public static void setProperties(Properties properties)
	{
		if (properties != null)
			PROPERTIES = properties;
	}

	public static String getProperty(String key)
	{
		return getProperty(key, null);
	}

	public static String getProperty(String key, String defaultValue)
	{
		String value = System.getProperty(key);
		if (value != null && value.length() > 0)
		{
			return value;
		} else
		{
			Properties properties = getProperties();
			return replaceProperty(properties.getProperty(key, defaultValue), properties);
		}
	}

	public static Properties loadProperties(String fileName)
	{
		return loadProperties(fileName, false, false);
	}

	public static Properties loadProperties(String fileName, boolean allowMultiFile)
	{
		return loadProperties(fileName, allowMultiFile, false);
	}

	public static Properties loadProperties(String fileName, boolean allowMultiFile, boolean optional)
	{
		Properties properties;
		properties = new Properties();
		if (!fileName.startsWith("/"))
			break MISSING_BLOCK_LABEL_109;
		FileInputStream input = new FileInputStream(fileName);
		properties.load(input);
		input.close();
		break MISSING_BLOCK_LABEL_107;
		Exception exception;
		exception;
		input.close();
		throw exception;
		Throwable e;
		e;
		logger.warn((new StringBuilder()).append("Failed to load ").append(fileName).append(" file from ").append(fileName).append("(ingore this file): ").append(e.getMessage()).toString(), e);
		return properties;
		Iterator i$;
		List list = new ArrayList();
		try
		{
			Enumeration urls = ClassHelper.getClassLoader().getResources(fileName);
			list = new ArrayList();
			for (; urls.hasMoreElements(); list.add(urls.nextElement()));
		}
		catch (Throwable t)
		{
			logger.warn((new StringBuilder()).append("Fail to load ").append(fileName).append(" file: ").append(t.getMessage()).toString(), t);
		}
		if (list.size() == 0)
		{
			if (!optional)
				logger.warn((new StringBuilder()).append("No ").append(fileName).append(" found on the class path.").toString());
			return properties;
		}
		if (!allowMultiFile)
		{
			if (list.size() > 1)
			{
				String errMsg = String.format("only 1 %s file is expected, but %d dubbo.properties files found on class path: %s", new Object[] {
					fileName, Integer.valueOf(list.size()), list.toString()
				});
				logger.warn(errMsg);
			}
			try
			{
				properties.load(ClassHelper.getClassLoader().getResourceAsStream(fileName));
			}
			catch (Throwable e)
			{
				logger.warn((new StringBuilder()).append("Failed to load ").append(fileName).append(" file from ").append(fileName).append("(ingore this file): ").append(e.getMessage()).toString(), e);
			}
			return properties;
		}
		logger.info((new StringBuilder()).append("load ").append(fileName).append(" properties file from ").append(list).toString());
		i$ = list.iterator();
_L2:
		URL url;
		if (!i$.hasNext())
			break; /* Loop/switch isn't completed */
		url = (URL)i$.next();
		Properties p;
		InputStream input;
		p = new Properties();
		input = url.openStream();
		if (input == null)
			continue; /* Loop/switch isn't completed */
		p.load(input);
		properties.putAll(p);
		try
		{
			input.close();
		}
		catch (Throwable t) { }
		continue; /* Loop/switch isn't completed */
		Exception exception1;
		exception1;
		try
		{
			input.close();
		}
		catch (Throwable t) { }
		throw exception1;
		Throwable e;
		e;
		logger.warn((new StringBuilder()).append("Fail to load ").append(fileName).append(" file from ").append(url).append("(ingore this file): ").append(e.getMessage()).toString(), e);
		if (true) goto _L2; else goto _L1
_L1:
		return properties;
	}

	public static int getPid()
	{
		if (PID < 0)
			try
			{
				RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
				String name = runtime.getName();
				PID = Integer.parseInt(name.substring(0, name.indexOf('@')));
			}
			catch (Throwable e)
			{
				PID = 0;
			}
		return PID;
	}

	private ConfigUtils()
	{
	}

}
