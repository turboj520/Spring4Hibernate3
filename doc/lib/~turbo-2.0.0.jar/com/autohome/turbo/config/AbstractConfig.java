// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractConfig.java

package com.autohome.turbo.config;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.*;
import com.autohome.turbo.config.support.Parameter;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Referenced classes of package com.autohome.turbo.config:
//			ProtocolConfig

public abstract class AbstractConfig
	implements Serializable
{

	private static final long serialVersionUID = 0x3b39531ce8a291c2L;
	protected static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/config/AbstractConfig);
	private static final int MAX_LENGTH = 100;
	private static final int MAX_PATH_LENGTH = 200;
	private static final Pattern PATTERN_NAME = Pattern.compile("[\\-._0-9a-zA-Z]+");
	private static final Pattern PATTERN_MULTI_NAME = Pattern.compile("[,\\-._0-9a-zA-Z]+");
	private static final Pattern PATTERN_METHOD_NAME = Pattern.compile("[a-zA-Z][0-9a-zA-Z]*");
	private static final Pattern PATTERN_PATH = Pattern.compile("[/\\-$._0-9a-zA-Z]+");
	private static final Pattern PATTERN_NAME_HAS_SYMBOL = Pattern.compile("[:*,/\\-._0-9a-zA-Z]+");
	private static final Pattern PATTERN_KEY = Pattern.compile("[*,\\-._0-9a-zA-Z]+");
	protected String id;
	private static final Map legacyProperties;
	private static final String SUFFIXS[] = {
		"Config", "Bean"
	};

	public AbstractConfig()
	{
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	private static String convertLegacyValue(String key, String value)
	{
		if (value != null && value.length() > 0)
		{
			if ("dubbo.service.max.retry.providers".equals(key))
				return String.valueOf(Integer.parseInt(value) - 1);
			if ("dubbo.service.allow.no.provider".equals(key))
				return String.valueOf(!Boolean.parseBoolean(value));
		}
		return value;
	}

	protected void appendAnnotation(Class annotationClass, Object annotation)
	{
		Method methods[] = annotationClass.getMethods();
		Method arr$[] = methods;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			Method method = arr$[i$];
			if (method.getDeclaringClass() == java/lang/Object || method.getReturnType() == Void.TYPE || method.getParameterTypes().length != 0 || !Modifier.isPublic(method.getModifiers()) || Modifier.isStatic(method.getModifiers()))
				continue;
			try
			{
				String property = method.getName();
				if ("interfaceClass".equals(property) || "interfaceName".equals(property))
					property = "interface";
				String setter = (new StringBuilder()).append("set").append(property.substring(0, 1).toUpperCase()).append(property.substring(1)).toString();
				Object value = method.invoke(annotation, new Object[0]);
				if (value == null || value.equals(method.getDefaultValue()))
					continue;
				Class parameterType = ReflectUtils.getBoxedClass(method.getReturnType());
				if ("filter".equals(property) || "listener".equals(property))
				{
					parameterType = java/lang/String;
					value = StringUtils.join((String[])(String[])value, ",");
				} else
				if ("parameters".equals(property))
				{
					parameterType = java/util/Map;
					value = CollectionUtils.toStringMap((String[])(String[])value);
				}
				try
				{
					Method setterMethod = getClass().getMethod(setter, new Class[] {
						parameterType
					});
					setterMethod.invoke(this, new Object[] {
						value
					});
				}
				catch (NoSuchMethodException e) { }
			}
			catch (Throwable e)
			{
				logger.error(e.getMessage(), e);
			}
		}

	}

	protected static void appendProperties(AbstractConfig config)
	{
		if (config == null)
			return;
		String prefix = (new StringBuilder()).append("dubbo.").append(getTagName(config.getClass())).append(".").toString();
		Method methods[] = config.getClass().getMethods();
		Method arr$[] = methods;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			Method method = arr$[i$];
			try
			{
				String name = method.getName();
				if (name.length() <= 3 || !name.startsWith("set") || !Modifier.isPublic(method.getModifiers()) || method.getParameterTypes().length != 1 || !isPrimitive(method.getParameterTypes()[0]))
					continue;
				String property = StringUtils.camelToSplitName((new StringBuilder()).append(name.substring(3, 4).toLowerCase()).append(name.substring(4)).toString(), "-");
				String value = null;
				if (config.getId() != null && config.getId().length() > 0)
				{
					String pn = (new StringBuilder()).append(prefix).append(config.getId()).append(".").append(property).toString();
					value = System.getProperty(pn);
					if (!StringUtils.isBlank(value))
						logger.info((new StringBuilder()).append("Use System Property ").append(pn).append(" to config dubbo").toString());
				}
				if (value == null || value.length() == 0)
				{
					String pn = (new StringBuilder()).append(prefix).append(property).toString();
					value = System.getProperty(pn);
					if (!StringUtils.isBlank(value))
						logger.info((new StringBuilder()).append("Use System Property ").append(pn).append(" to config dubbo").toString());
				}
				if (value == null || value.length() == 0)
				{
					Method getter;
					try
					{
						getter = config.getClass().getMethod((new StringBuilder()).append("get").append(name.substring(3)).toString(), new Class[0]);
					}
					catch (NoSuchMethodException e)
					{
						try
						{
							getter = config.getClass().getMethod((new StringBuilder()).append("is").append(name.substring(3)).toString(), new Class[0]);
						}
						catch (NoSuchMethodException e2)
						{
							getter = null;
						}
					}
					if (getter != null && getter.invoke(config, new Object[0]) == null)
					{
						if (config.getId() != null && config.getId().length() > 0)
							value = ConfigUtils.getProperty((new StringBuilder()).append(prefix).append(config.getId()).append(".").append(property).toString());
						if (value == null || value.length() == 0)
							value = ConfigUtils.getProperty((new StringBuilder()).append(prefix).append(property).toString());
						if (value == null || value.length() == 0)
						{
							String legacyKey = (String)legacyProperties.get((new StringBuilder()).append(prefix).append(property).toString());
							if (legacyKey != null && legacyKey.length() > 0)
								value = convertLegacyValue(legacyKey, ConfigUtils.getProperty(legacyKey));
						}
					}
				}
				if (value != null && value.length() > 0)
					method.invoke(config, new Object[] {
						convertPrimitive(method.getParameterTypes()[0], value)
					});
			}
			catch (Exception e)
			{
				logger.error(e.getMessage(), e);
			}
		}

	}

	private static String getTagName(Class cls)
	{
		String tag = cls.getSimpleName();
		String arr$[] = SUFFIXS;
		int len$ = arr$.length;
		int i$ = 0;
		do
		{
			if (i$ >= len$)
				break;
			String suffix = arr$[i$];
			if (tag.endsWith(suffix))
			{
				tag = tag.substring(0, tag.length() - suffix.length());
				break;
			}
			i$++;
		} while (true);
		tag = tag.toLowerCase();
		return tag;
	}

	protected static void appendParameters(Map parameters, Object config)
	{
		appendParameters(parameters, config, null);
	}

	protected static void appendParameters(Map parameters, Object config, String prefix)
	{
		Method arr$[];
		int len$;
		int i$;
		if (config == null)
			return;
		Method methods[] = config.getClass().getMethods();
		arr$ = methods;
		len$ = arr$.length;
		i$ = 0;
_L3:
		if (i$ >= len$) goto _L2; else goto _L1
_L1:
		Method method = arr$[i$];
		String name;
		Parameter parameter;
		Map map;
		int i;
		String pre;
		String prop;
		Iterator i$;
		String key;
		java.util.Map.Entry entry;
		Object value;
		String str;
		String pre;
		try
		{
			name = method.getName();
			if (!name.startsWith("get") && !name.startsWith("is") || "getClass".equals(name) || !Modifier.isPublic(method.getModifiers()) || method.getParameterTypes().length != 0 || !isPrimitive(method.getReturnType()))
				break MISSING_BLOCK_LABEL_563;
			parameter = (Parameter)method.getAnnotation(com/autohome/turbo/config/support/Parameter);
			if (method.getReturnType() == java/lang/Object || parameter != null && parameter.excluded())
				continue; /* Loop/switch isn't completed */
		}
		catch (Exception e)
		{
			throw new IllegalStateException(e.getMessage(), e);
		}
		i = name.startsWith("get") ? 3 : 2;
		prop = StringUtils.camelToSplitName((new StringBuilder()).append(name.substring(i, i + 1).toLowerCase()).append(name.substring(i + 1)).toString(), ".");
		if (parameter != null && parameter.key() != null && parameter.key().length() > 0)
			key = parameter.key();
		else
			key = prop;
		value = method.invoke(config, new Object[0]);
		str = String.valueOf(value).trim();
		if (value != null && str.length() > 0)
		{
			if (parameter != null && parameter.escaped())
				str = URL.encode(str);
			if (parameter != null && parameter.append())
			{
				pre = (String)parameters.get((new StringBuilder()).append("default.").append(key).toString());
				if (pre != null && pre.length() > 0)
					str = (new StringBuilder()).append(pre).append(",").append(str).toString();
				pre = (String)parameters.get(key);
				if (pre != null && pre.length() > 0)
					str = (new StringBuilder()).append(pre).append(",").append(str).toString();
			}
			if (prefix != null && prefix.length() > 0)
				key = (new StringBuilder()).append(prefix).append(".").append(key).toString();
			parameters.put(key, str);
			continue; /* Loop/switch isn't completed */
		}
		if (parameter != null && parameter.required())
			throw new IllegalStateException((new StringBuilder()).append(config.getClass().getSimpleName()).append(".").append(key).append(" == null").toString());
		continue; /* Loop/switch isn't completed */
		if (!"getParameters".equals(name) || !Modifier.isPublic(method.getModifiers()) || method.getParameterTypes().length != 0 || method.getReturnType() != java/util/Map)
			continue; /* Loop/switch isn't completed */
		map = (Map)method.invoke(config, new Object[0]);
		if (map == null || map.size() <= 0)
			continue; /* Loop/switch isn't completed */
		pre = prefix == null || prefix.length() <= 0 ? "" : (new StringBuilder()).append(prefix).append(".").toString();
		for (i$ = map.entrySet().iterator(); i$.hasNext(); parameters.put((new StringBuilder()).append(pre).append(((String)entry.getKey()).replace('-', '.')).toString(), entry.getValue()))
			entry = (java.util.Map.Entry)i$.next();

		continue; /* Loop/switch isn't completed */
		i$++;
		  goto _L3
_L2:
	}

	protected static void appendAttributes(Map parameters, Object config)
	{
		appendAttributes(parameters, config, null);
	}

	protected static void appendAttributes(Map parameters, Object config, String prefix)
	{
		Method arr$[];
		int len$;
		int i$;
		if (config == null)
			return;
		Method methods[] = config.getClass().getMethods();
		arr$ = methods;
		len$ = arr$.length;
		i$ = 0;
_L3:
		if (i$ >= len$) goto _L2; else goto _L1
_L1:
		Method method = arr$[i$];
		String name;
		Parameter parameter;
		String key;
		int i;
		Object value;
		try
		{
			name = method.getName();
			if (!name.startsWith("get") && !name.startsWith("is") || "getClass".equals(name) || !Modifier.isPublic(method.getModifiers()) || method.getParameterTypes().length != 0 || !isPrimitive(method.getReturnType()))
				continue; /* Loop/switch isn't completed */
			parameter = (Parameter)method.getAnnotation(com/autohome/turbo/config/support/Parameter);
			if (parameter == null || !parameter.attribute())
				continue; /* Loop/switch isn't completed */
		}
		catch (Exception e)
		{
			throw new IllegalStateException(e.getMessage(), e);
		}
		if (parameter != null && parameter.key() != null && parameter.key().length() > 0)
		{
			key = parameter.key();
		} else
		{
			i = name.startsWith("get") ? 3 : 2;
			key = (new StringBuilder()).append(name.substring(i, i + 1).toLowerCase()).append(name.substring(i + 1)).toString();
		}
		value = method.invoke(config, new Object[0]);
		if (value == null)
			continue; /* Loop/switch isn't completed */
		if (prefix != null && prefix.length() > 0)
			key = (new StringBuilder()).append(prefix).append(".").append(key).toString();
		parameters.put(key, value);
		i$++;
		  goto _L3
_L2:
	}

	private static boolean isPrimitive(Class type)
	{
		return type.isPrimitive() || type == java/lang/String || type == java/lang/Character || type == java/lang/Boolean || type == java/lang/Byte || type == java/lang/Short || type == java/lang/Integer || type == java/lang/Long || type == java/lang/Float || type == java/lang/Double || type == java/lang/Object;
	}

	private static Object convertPrimitive(Class type, String value)
	{
		if (type == Character.TYPE || type == java/lang/Character)
			return Character.valueOf(value.length() <= 0 ? '\0' : value.charAt(0));
		if (type == Boolean.TYPE || type == java/lang/Boolean)
			return Boolean.valueOf(value);
		if (type == Byte.TYPE || type == java/lang/Byte)
			return Byte.valueOf(value);
		if (type == Short.TYPE || type == java/lang/Short)
			return Short.valueOf(value);
		if (type == Integer.TYPE || type == java/lang/Integer)
			return Integer.valueOf(value);
		if (type == Long.TYPE || type == java/lang/Long)
			return Long.valueOf(value);
		if (type == Float.TYPE || type == java/lang/Float)
			return Float.valueOf(value);
		if (type == Double.TYPE || type == java/lang/Double)
			return Double.valueOf(value);
		else
			return value;
	}

	protected static void checkExtension(Class type, String property, String value)
	{
		checkName(property, value);
		if (value != null && value.length() > 0 && !ExtensionLoader.getExtensionLoader(type).hasExtension(value))
			throw new IllegalStateException((new StringBuilder()).append("No such extension ").append(value).append(" for ").append(property).append("/").append(type.getName()).toString());
		else
			return;
	}

	protected static void checkMultiExtension(Class type, String property, String value)
	{
		checkMultiName(property, value);
		if (value != null && value.length() > 0)
		{
			String values[] = value.split("\\s*[,]+\\s*");
			String arr$[] = values;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				String v = arr$[i$];
				if (v.startsWith("-"))
					v = v.substring(1);
				if (!"default".equals(v) && !ExtensionLoader.getExtensionLoader(type).hasExtension(v))
					throw new IllegalStateException((new StringBuilder()).append("No such extension ").append(v).append(" for ").append(property).append("/").append(type.getName()).toString());
			}

		}
	}

	protected static void checkLength(String property, String value)
	{
		checkProperty(property, value, 100, null);
	}

	protected static void checkPathLength(String property, String value)
	{
		checkProperty(property, value, 200, null);
	}

	protected static void checkName(String property, String value)
	{
		checkProperty(property, value, 100, PATTERN_NAME);
	}

	protected static void checkNameHasSymbol(String property, String value)
	{
		checkProperty(property, value, 100, PATTERN_NAME_HAS_SYMBOL);
	}

	protected static void checkKey(String property, String value)
	{
		checkProperty(property, value, 100, PATTERN_KEY);
	}

	protected static void checkMultiName(String property, String value)
	{
		checkProperty(property, value, 100, PATTERN_MULTI_NAME);
	}

	protected static void checkPathName(String property, String value)
	{
		checkProperty(property, value, 200, PATTERN_PATH);
	}

	protected static void checkMethodName(String property, String value)
	{
		checkProperty(property, value, 100, PATTERN_METHOD_NAME);
	}

	protected static void checkParameterName(Map parameters)
	{
		if (parameters == null || parameters.size() == 0)
			return;
		java.util.Map.Entry entry;
		for (Iterator i$ = parameters.entrySet().iterator(); i$.hasNext(); checkNameHasSymbol((String)entry.getKey(), (String)entry.getValue()))
			entry = (java.util.Map.Entry)i$.next();

	}

	protected static void checkProperty(String property, String value, int maxlength, Pattern pattern)
	{
		if (value == null || value.length() == 0)
			return;
		if (value.length() > maxlength)
			throw new IllegalStateException((new StringBuilder()).append("Invalid ").append(property).append("=\"").append(value).append("\" is longer than ").append(maxlength).toString());
		if (pattern != null)
		{
			Matcher matcher = pattern.matcher(value);
			if (!matcher.matches())
				throw new IllegalStateException((new StringBuilder()).append("Invalid ").append(property).append("=\"").append(value).append("\" contain illegal charactor, only digit, letter, '-', '_' and '.' is legal.").toString());
		}
	}

	public String toString()
	{
		StringBuilder buf;
		buf = new StringBuilder();
		buf.append("<dubbo:");
		buf.append(getTagName(getClass()));
		Method methods[] = getClass().getMethods();
		Method arr$[] = methods;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			Method method = arr$[i$];
			try
			{
				String name = method.getName();
				if (!name.startsWith("get") && !name.startsWith("is") || "getClass".equals(name) || "get".equals(name) || "is".equals(name) || !Modifier.isPublic(method.getModifiers()) || method.getParameterTypes().length != 0 || !isPrimitive(method.getReturnType()))
					continue;
				int i = name.startsWith("get") ? 3 : 2;
				String key = (new StringBuilder()).append(name.substring(i, i + 1).toLowerCase()).append(name.substring(i + 1)).toString();
				Object value = method.invoke(this, new Object[0]);
				if (value != null)
				{
					buf.append(" ");
					buf.append(key);
					buf.append("=\"");
					buf.append(value);
					buf.append("\"");
				}
			}
			catch (Exception e)
			{
				logger.warn(e.getMessage(), e);
			}
		}

		buf.append(" />");
		return buf.toString();
		Throwable t;
		t;
		logger.warn(t.getMessage(), t);
		return super.toString();
	}

	static 
	{
		legacyProperties = new HashMap();
		legacyProperties.put("dubbo.protocol.name", "dubbo.service.protocol");
		legacyProperties.put("dubbo.protocol.host", "dubbo.service.server.host");
		legacyProperties.put("dubbo.protocol.port", "dubbo.service.server.port");
		legacyProperties.put("dubbo.protocol.threads", "dubbo.service.max.thread.pool.size");
		legacyProperties.put("dubbo.consumer.timeout", "dubbo.service.invoke.timeout");
		legacyProperties.put("dubbo.consumer.retries", "dubbo.service.max.retry.providers");
		legacyProperties.put("dubbo.consumer.check", "dubbo.service.allow.no.provider");
		legacyProperties.put("dubbo.service.url", "dubbo.service.address");
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			public void run()
			{
				if (AbstractConfig.logger.isInfoEnabled())
					AbstractConfig.logger.info("Run shutdown hook now.");
				ProtocolConfig.destroyAll();
			}

		}, "DubboShutdownHook"));
	}
}
