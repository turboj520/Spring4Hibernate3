// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ExtensionLoader.java

package com.autohome.turbo.common.extension;

import com.autohome.turbo.common.Constants;
import com.autohome.turbo.common.Extension;
import com.autohome.turbo.common.compiler.Compiler;
import com.autohome.turbo.common.extension.support.ActivateComparator;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.ConcurrentHashSet;
import com.autohome.turbo.common.utils.ConfigUtils;
import com.autohome.turbo.common.utils.Holder;
import com.autohome.turbo.common.utils.StringUtils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

// Referenced classes of package com.autohome.turbo.common.extension:
//			SPI, ExtensionFactory, Activate, Adaptive

public class ExtensionLoader
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/common/extension/ExtensionLoader);
	private static final String SERVICES_DIRECTORY = "META-INF/services/";
	private static final String DUBBO_DIRECTORY = "META-INF/dubbo/";
	private static final String DUBBO_INTERNAL_DIRECTORY = "META-INF/dubbo/internal/";
	private static final Pattern NAME_SEPARATOR = Pattern.compile("\\s*[,]+\\s*");
	private static final ConcurrentMap EXTENSION_LOADERS = new ConcurrentHashMap();
	private static final ConcurrentMap EXTENSION_INSTANCES = new ConcurrentHashMap();
	private final Class type;
	private final ExtensionFactory objectFactory;
	private final ConcurrentMap cachedNames = new ConcurrentHashMap();
	private final Holder cachedClasses = new Holder();
	private final Map cachedActivates = new ConcurrentHashMap();
	private volatile Class cachedAdaptiveClass;
	private final ConcurrentMap cachedInstances = new ConcurrentHashMap();
	private String cachedDefaultName;
	private final Holder cachedAdaptiveInstance = new Holder();
	private volatile Throwable createAdaptiveInstanceError;
	private Set cachedWrapperClasses;
	private Map exceptions;

	private static boolean withExtensionAnnotation(Class type)
	{
		return type.isAnnotationPresent(com/autohome/turbo/common/extension/SPI);
	}

	public static ExtensionLoader getExtensionLoader(Class type)
	{
		if (type == null)
			throw new IllegalArgumentException("Extension type == null");
		if (!type.isInterface())
			throw new IllegalArgumentException((new StringBuilder()).append("Extension type(").append(type).append(") is not interface!").toString());
		if (!withExtensionAnnotation(type))
			throw new IllegalArgumentException((new StringBuilder()).append("Extension type(").append(type).append(") is not extension, because WITHOUT @").append(com/autohome/turbo/common/extension/SPI.getSimpleName()).append(" Annotation!").toString());
		ExtensionLoader loader = (ExtensionLoader)EXTENSION_LOADERS.get(type);
		if (loader == null)
		{
			EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader(type));
			loader = (ExtensionLoader)EXTENSION_LOADERS.get(type);
		}
		return loader;
	}

	private ExtensionLoader(Class type)
	{
		cachedAdaptiveClass = null;
		exceptions = new ConcurrentHashMap();
		this.type = type;
		objectFactory = type != com/autohome/turbo/common/extension/ExtensionFactory ? (ExtensionFactory)getExtensionLoader(com/autohome/turbo/common/extension/ExtensionFactory).getAdaptiveExtension() : null;
	}

	public String getExtensionName(Object extensionInstance)
	{
		return getExtensionName(extensionInstance.getClass());
	}

	public String getExtensionName(Class extensionClass)
	{
		return (String)cachedNames.get(extensionClass);
	}

	public List getActivateExtension(com.autohome.turbo.common.URL url, String key)
	{
		return getActivateExtension(url, key, null);
	}

	public List getActivateExtension(com.autohome.turbo.common.URL url, String values[])
	{
		return getActivateExtension(url, values, null);
	}

	public List getActivateExtension(com.autohome.turbo.common.URL url, String key, String group)
	{
		String value = url.getParameter(key);
		return getActivateExtension(url, value != null && value.length() != 0 ? Constants.COMMA_SPLIT_PATTERN.split(value) : null, group);
	}

	public List getActivateExtension(com.autohome.turbo.common.URL url, String values[], String group)
	{
		List exts = new ArrayList();
		List names = ((List) (values != null ? Arrays.asList(values) : ((List) (new ArrayList(0)))));
		if (!names.contains("-default"))
		{
			getExtensionClasses();
			Iterator i$ = cachedActivates.entrySet().iterator();
			do
			{
				if (!i$.hasNext())
					break;
				java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
				String name = (String)entry.getKey();
				Activate activate = (Activate)entry.getValue();
				if (isMatchGroup(group, activate.group()))
				{
					Object ext = getExtension(name);
					if (!names.contains(name) && !names.contains((new StringBuilder()).append("-").append(name).toString()) && isActive(activate, url))
						exts.add(ext);
				}
			} while (true);
			Collections.sort(exts, ActivateComparator.COMPARATOR);
		}
		List usrs = new ArrayList();
		for (int i = 0; i < names.size(); i++)
		{
			String name = (String)names.get(i);
			if (name.startsWith("-") || names.contains((new StringBuilder()).append("-").append(name).toString()))
				continue;
			if ("default".equals(name))
			{
				if (usrs.size() > 0)
				{
					exts.addAll(0, usrs);
					usrs.clear();
				}
			} else
			{
				Object ext = getExtension(name);
				usrs.add(ext);
			}
		}

		if (usrs.size() > 0)
			exts.addAll(usrs);
		return exts;
	}

	private boolean isMatchGroup(String group, String groups[])
	{
		if (group == null || group.length() == 0)
			return true;
		if (groups != null && groups.length > 0)
		{
			String arr$[] = groups;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				String g = arr$[i$];
				if (group.equals(g))
					return true;
			}

		}
		return false;
	}

	private boolean isActive(Activate activate, com.autohome.turbo.common.URL url)
	{
		String keys[] = activate.value();
		if (keys == null || keys.length == 0)
			return true;
		String arr$[] = keys;
		int len$ = arr$.length;
		int i$ = 0;
		do
		{
			if (i$ >= len$)
				break;
			String key = arr$[i$];
			for (Iterator i$ = url.getParameters().entrySet().iterator(); i$.hasNext();)
			{
				java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
				String k = (String)entry.getKey();
				String v = (String)entry.getValue();
				if ((k.equals(key) || k.endsWith((new StringBuilder()).append(".").append(key).toString())) && ConfigUtils.isNotEmpty(v))
					return true;
			}

			i$++;
		} while (true);
		return false;
	}

	public Object getLoadedExtension(String name)
	{
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("Extension name == null");
		Holder holder = (Holder)cachedInstances.get(name);
		if (holder == null)
		{
			cachedInstances.putIfAbsent(name, new Holder());
			holder = (Holder)cachedInstances.get(name);
		}
		return holder.get();
	}

	public Set getLoadedExtensions()
	{
		return Collections.unmodifiableSet(new TreeSet(cachedInstances.keySet()));
	}

	public Object getExtension(String name)
	{
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("Extension name == null");
		if ("true".equals(name))
			return getDefaultExtension();
		Holder holder = (Holder)cachedInstances.get(name);
		if (holder == null)
		{
			cachedInstances.putIfAbsent(name, new Holder());
			holder = (Holder)cachedInstances.get(name);
		}
		Object instance = holder.get();
		if (instance == null)
			synchronized (holder)
			{
				instance = holder.get();
				if (instance == null)
				{
					instance = createExtension(name);
					holder.set(instance);
				}
			}
		return instance;
	}

	public Object getDefaultExtension()
	{
		getExtensionClasses();
		if (null == cachedDefaultName || cachedDefaultName.length() == 0 || "true".equals(cachedDefaultName))
			return null;
		else
			return getExtension(cachedDefaultName);
	}

	public boolean hasExtension(String name)
	{
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("Extension name == null");
		return getExtensionClass(name) != null;
		Throwable t;
		t;
		return false;
	}

	public Set getSupportedExtensions()
	{
		Map clazzes = getExtensionClasses();
		return Collections.unmodifiableSet(new TreeSet(clazzes.keySet()));
	}

	public String getDefaultExtensionName()
	{
		getExtensionClasses();
		return cachedDefaultName;
	}

	public void addExtension(String name, Class clazz)
	{
		getExtensionClasses();
		if (!type.isAssignableFrom(clazz))
			throw new IllegalStateException((new StringBuilder()).append("Input type ").append(clazz).append("not implement Extension ").append(type).toString());
		if (clazz.isInterface())
			throw new IllegalStateException((new StringBuilder()).append("Input type ").append(clazz).append("can not be interface!").toString());
		if (!clazz.isAnnotationPresent(com/autohome/turbo/common/extension/Adaptive))
		{
			if (StringUtils.isBlank(name))
				throw new IllegalStateException((new StringBuilder()).append("Extension name is blank (Extension ").append(type).append(")!").toString());
			if (((Map)cachedClasses.get()).containsKey(name))
				throw new IllegalStateException((new StringBuilder()).append("Extension name ").append(name).append(" already existed(Extension ").append(type).append(")!").toString());
			cachedNames.put(clazz, name);
			((Map)cachedClasses.get()).put(name, clazz);
		} else
		{
			if (cachedAdaptiveClass != null)
				throw new IllegalStateException((new StringBuilder()).append("Adaptive Extension already existed(Extension ").append(type).append(")!").toString());
			cachedAdaptiveClass = clazz;
		}
	}

	/**
	 * @deprecated Method replaceExtension is deprecated
	 */

	public void replaceExtension(String name, Class clazz)
	{
		getExtensionClasses();
		if (!type.isAssignableFrom(clazz))
			throw new IllegalStateException((new StringBuilder()).append("Input type ").append(clazz).append("not implement Extension ").append(type).toString());
		if (clazz.isInterface())
			throw new IllegalStateException((new StringBuilder()).append("Input type ").append(clazz).append("can not be interface!").toString());
		if (!clazz.isAnnotationPresent(com/autohome/turbo/common/extension/Adaptive))
		{
			if (StringUtils.isBlank(name))
				throw new IllegalStateException((new StringBuilder()).append("Extension name is blank (Extension ").append(type).append(")!").toString());
			if (!((Map)cachedClasses.get()).containsKey(name))
				throw new IllegalStateException((new StringBuilder()).append("Extension name ").append(name).append(" not existed(Extension ").append(type).append(")!").toString());
			cachedNames.put(clazz, name);
			((Map)cachedClasses.get()).put(name, clazz);
			cachedInstances.remove(name);
		} else
		{
			if (cachedAdaptiveClass == null)
				throw new IllegalStateException((new StringBuilder()).append("Adaptive Extension not existed(Extension ").append(type).append(")!").toString());
			cachedAdaptiveClass = clazz;
			cachedAdaptiveInstance.set(null);
		}
	}

	public Object getAdaptiveExtension()
	{
		Object instance = cachedAdaptiveInstance.get();
		if (instance == null)
			if (createAdaptiveInstanceError == null)
				synchronized (cachedAdaptiveInstance)
				{
					instance = cachedAdaptiveInstance.get();
					if (instance == null)
						try
						{
							instance = createAdaptiveExtension();
							cachedAdaptiveInstance.set(instance);
						}
						catch (Throwable t)
						{
							createAdaptiveInstanceError = t;
							throw new IllegalStateException((new StringBuilder()).append("fail to create adaptive instance: ").append(t.toString()).toString(), t);
						}
				}
			else
				throw new IllegalStateException((new StringBuilder()).append("fail to create adaptive instance: ").append(createAdaptiveInstanceError.toString()).toString(), createAdaptiveInstanceError);
		return instance;
	}

	private IllegalStateException findException(String name)
	{
		for (Iterator i$ = exceptions.entrySet().iterator(); i$.hasNext();)
		{
			java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
			if (((String)entry.getKey()).toLowerCase().contains(name.toLowerCase()))
				return (IllegalStateException)entry.getValue();
		}

		StringBuilder buf = new StringBuilder((new StringBuilder()).append("No such extension ").append(type.getName()).append(" by name ").append(name).toString());
		int i = 1;
		java.util.Map.Entry entry;
		for (Iterator i$ = exceptions.entrySet().iterator(); i$.hasNext(); buf.append(StringUtils.toString((Throwable)entry.getValue())))
		{
			entry = (java.util.Map.Entry)i$.next();
			if (i == 1)
				buf.append(", possible causes: ");
			buf.append("\r\n(");
			buf.append(i++);
			buf.append(") ");
			buf.append((String)entry.getKey());
			buf.append(":\r\n");
		}

		return new IllegalStateException(buf.toString());
	}

	private Object createExtension(String name)
	{
		Class clazz;
		clazz = (Class)getExtensionClasses().get(name);
		if (clazz == null)
			throw findException(name);
		Object instance;
		instance = EXTENSION_INSTANCES.get(clazz);
		if (instance == null)
		{
			EXTENSION_INSTANCES.putIfAbsent(clazz, clazz.newInstance());
			instance = EXTENSION_INSTANCES.get(clazz);
		}
		injectExtension(instance);
		Set wrapperClasses = cachedWrapperClasses;
		if (wrapperClasses != null && wrapperClasses.size() > 0)
		{
			for (Iterator i$ = wrapperClasses.iterator(); i$.hasNext();)
			{
				Class wrapperClass = (Class)i$.next();
				instance = injectExtension(wrapperClass.getConstructor(new Class[] {
					type
				}).newInstance(new Object[] {
					instance
				}));
			}

		}
		return instance;
		Throwable t;
		t;
		throw new IllegalStateException((new StringBuilder()).append("Extension instance(name: ").append(name).append(", class: ").append(type).append(")  could not be instantiated: ").append(t.getMessage()).toString(), t);
	}

	private Object injectExtension(Object instance)
	{
		try
		{
			if (objectFactory != null)
			{
				Method arr$[] = instance.getClass().getMethods();
				int len$ = arr$.length;
				for (int i$ = 0; i$ < len$; i$++)
				{
					Method method = arr$[i$];
					if (method.getName().startsWith("set") && method.getParameterTypes().length == 1 && Modifier.isPublic(method.getModifiers()))
					{
						Class pt = method.getParameterTypes()[0];
						try
						{
							String property = method.getName().length() <= 3 ? "" : (new StringBuilder()).append(method.getName().substring(3, 4).toLowerCase()).append(method.getName().substring(4)).toString();
							Object object = objectFactory.getExtension(pt, property);
							if (object != null)
								method.invoke(instance, new Object[] {
									object
								});
						}
						catch (Exception e)
						{
							logger.error((new StringBuilder()).append("fail to inject via method ").append(method.getName()).append(" of interface ").append(type.getName()).append(": ").append(e.getMessage()).toString(), e);
						}
					}
				}

			}
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		}
		return instance;
	}

	private Class getExtensionClass(String name)
	{
		if (type == null)
			throw new IllegalArgumentException("Extension type == null");
		if (name == null)
			throw new IllegalArgumentException("Extension name == null");
		Class clazz = (Class)getExtensionClasses().get(name);
		if (clazz == null)
			throw new IllegalStateException((new StringBuilder()).append("No such extension \"").append(name).append("\" for ").append(type.getName()).append("!").toString());
		else
			return clazz;
	}

	private Map getExtensionClasses()
	{
		Map classes = (Map)cachedClasses.get();
		if (classes == null)
			synchronized (cachedClasses)
			{
				classes = (Map)cachedClasses.get();
				if (classes == null)
				{
					classes = loadExtensionClasses();
					cachedClasses.set(classes);
				}
			}
		return classes;
	}

	private Map loadExtensionClasses()
	{
		SPI defaultAnnotation = (SPI)type.getAnnotation(com/autohome/turbo/common/extension/SPI);
		if (defaultAnnotation != null)
		{
			String value = defaultAnnotation.value();
			if (value != null && (value = value.trim()).length() > 0)
			{
				String names[] = NAME_SEPARATOR.split(value);
				if (names.length > 1)
					throw new IllegalStateException((new StringBuilder()).append("more than 1 default extension name on extension ").append(type.getName()).append(": ").append(Arrays.toString(names)).toString());
				if (names.length == 1)
					cachedDefaultName = names[0];
			}
		}
		Map extensionClasses = new HashMap();
		loadFile(extensionClasses, "META-INF/dubbo/internal/");
		loadFile(extensionClasses, "META-INF/dubbo/");
		loadFile(extensionClasses, "META-INF/services/");
		return extensionClasses;
	}

	private void loadFile(Map extensionClasses, String dir)
	{
		String fileName = (new StringBuilder()).append(dir).append(type.getName()).toString();
		Enumeration urls;
		ClassLoader classLoader;
		classLoader = findClassLoader();
		if (classLoader != null)
			urls = classLoader.getResources(fileName);
		else
			urls = ClassLoader.getSystemResources(fileName);
		if (urls == null) goto _L2; else goto _L1
_L1:
		URL url;
		if (!urls.hasMoreElements())
			break; /* Loop/switch isn't completed */
		url = (URL)urls.nextElement();
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
		String line = null;
		do
		{
			if ((line = reader.readLine()) == null)
				break;
			int ci = line.indexOf('#');
			if (ci >= 0)
				line = line.substring(0, ci);
			line = line.trim();
			if (line.length() > 0)
				try
				{
					String name = null;
					int i = line.indexOf('=');
					if (i > 0)
					{
						name = line.substring(0, i).trim();
						line = line.substring(i + 1).trim();
					}
					if (line.length() > 0)
					{
						Class clazz = Class.forName(line, true, classLoader);
						if (!type.isAssignableFrom(clazz))
							throw new IllegalStateException((new StringBuilder()).append("Error when load extension class(interface: ").append(type).append(", class line: ").append(clazz.getName()).append("), class ").append(clazz.getName()).append("is not subtype of interface.").toString());
						if (clazz.isAnnotationPresent(com/autohome/turbo/common/extension/Adaptive))
						{
							if (cachedAdaptiveClass == null)
								cachedAdaptiveClass = clazz;
							else
							if (!cachedAdaptiveClass.equals(clazz))
								throw new IllegalStateException((new StringBuilder()).append("More than 1 adaptive class found: ").append(cachedAdaptiveClass.getClass().getName()).append(", ").append(clazz.getClass().getName()).toString());
						} else
						{
							try
							{
								clazz.getConstructor(new Class[] {
									type
								});
								Set wrappers = cachedWrapperClasses;
								if (wrappers == null)
								{
									cachedWrapperClasses = new ConcurrentHashSet();
									wrappers = cachedWrapperClasses;
								}
								wrappers.add(clazz);
							}
							catch (NoSuchMethodException e)
							{
								clazz.getConstructor(new Class[0]);
								if (name == null || name.length() == 0)
								{
									name = findAnnotationName(clazz);
									if (name == null || name.length() == 0)
										if (clazz.getSimpleName().length() > type.getSimpleName().length() && clazz.getSimpleName().endsWith(type.getSimpleName()))
											name = clazz.getSimpleName().substring(0, clazz.getSimpleName().length() - type.getSimpleName().length()).toLowerCase();
										else
											throw new IllegalStateException((new StringBuilder()).append("No such extension name for the class ").append(clazz.getName()).append(" in the config ").append(url).toString());
								}
								String names[] = NAME_SEPARATOR.split(name);
								if (names != null && names.length > 0)
								{
									Activate activate = (Activate)clazz.getAnnotation(com/autohome/turbo/common/extension/Activate);
									if (activate != null)
										cachedActivates.put(names[0], activate);
									String arr$[] = names;
									int len$ = arr$.length;
									int i$ = 0;
									while (i$ < len$) 
									{
										String n = arr$[i$];
										if (!cachedNames.containsKey(clazz))
											cachedNames.put(clazz, n);
										Class c = (Class)extensionClasses.get(n);
										if (c == null)
											extensionClasses.put(n, clazz);
										else
										if (c != clazz)
											throw new IllegalStateException((new StringBuilder()).append("Duplicate extension ").append(type.getName()).append(" name ").append(n).append(" on ").append(c.getName()).append(" and ").append(clazz.getName()).toString());
										i$++;
									}
								}
							}
						}
					}
				}
				catch (Throwable t)
				{
					IllegalStateException e = new IllegalStateException((new StringBuilder()).append("Failed to load extension class(interface: ").append(type).append(", class line: ").append(line).append(") in ").append(url).append(", cause: ").append(t.getMessage()).toString(), t);
					exceptions.put(line, e);
				}
		} while (true);
		reader.close();
		continue; /* Loop/switch isn't completed */
		Exception exception;
		exception;
		reader.close();
		throw exception;
		Throwable t;
		t;
		logger.error((new StringBuilder()).append("Exception when load extension class(interface: ").append(type).append(", class file: ").append(url).append(") in ").append(url).toString(), t);
		if (true) goto _L1; else goto _L2
		Throwable t;
		t;
		logger.error((new StringBuilder()).append("Exception when load extension class(interface: ").append(type).append(", description file: ").append(fileName).append(").").toString(), t);
_L2:
	}

	private String findAnnotationName(Class clazz)
	{
		Extension extension = (Extension)clazz.getAnnotation(com/autohome/turbo/common/Extension);
		if (extension == null)
		{
			String name = clazz.getSimpleName();
			if (name.endsWith(type.getSimpleName()))
				name = name.substring(0, name.length() - type.getSimpleName().length());
			return name.toLowerCase();
		} else
		{
			return extension.value();
		}
	}

	private Object createAdaptiveExtension()
	{
		return injectExtension(getAdaptiveExtensionClass().newInstance());
		Exception e;
		e;
		throw new IllegalStateException((new StringBuilder()).append("Can not create adaptive extenstion ").append(type).append(", cause: ").append(e.getMessage()).toString(), e);
	}

	private Class getAdaptiveExtensionClass()
	{
		getExtensionClasses();
		if (cachedAdaptiveClass != null)
			return cachedAdaptiveClass;
		else
			return cachedAdaptiveClass = createAdaptiveExtensionClass();
	}

	private Class createAdaptiveExtensionClass()
	{
		String code = createAdaptiveExtensionClassCode();
		ClassLoader classLoader = findClassLoader();
		Compiler compiler = (Compiler)getExtensionLoader(com/autohome/turbo/common/compiler/Compiler).getAdaptiveExtension();
		return compiler.compile(code, classLoader);
	}

	private String createAdaptiveExtensionClassCode()
	{
		StringBuilder codeBuidler = new StringBuilder();
		Method methods[] = type.getMethods();
		boolean hasAdaptiveAnnotation = false;
		Method arr$[] = methods;
		int len$ = arr$.length;
		int i$ = 0;
		do
		{
			if (i$ >= len$)
				break;
			Method m = arr$[i$];
			if (m.isAnnotationPresent(com/autohome/turbo/common/extension/Adaptive))
			{
				hasAdaptiveAnnotation = true;
				break;
			}
			i$++;
		} while (true);
		if (!hasAdaptiveAnnotation)
			throw new IllegalStateException((new StringBuilder()).append("No adaptive method on extension ").append(type.getName()).append(", refuse to create the adaptive class!").toString());
		codeBuidler.append((new StringBuilder()).append("package ").append(type.getPackage().getName()).append(";").toString());
		codeBuidler.append((new StringBuilder()).append("\nimport ").append(com/autohome/turbo/common/extension/ExtensionLoader.getName()).append(";").toString());
		codeBuidler.append((new StringBuilder()).append("\npublic class ").append(type.getSimpleName()).append("$Adpative").append(" implements ").append(type.getCanonicalName()).append(" {").toString());
		arr$ = methods;
		len$ = arr$.length;
		for (i$ = 0; i$ < len$; i$++)
		{
			Method method = arr$[i$];
			Class rt = method.getReturnType();
			Class pts[] = method.getParameterTypes();
			Class ets[] = method.getExceptionTypes();
			Adaptive adaptiveAnnotation = (Adaptive)method.getAnnotation(com/autohome/turbo/common/extension/Adaptive);
			StringBuilder code = new StringBuilder(512);
			if (adaptiveAnnotation == null)
			{
				code.append("throw new UnsupportedOperationException(\"method ").append(method.toString()).append(" of interface ").append(type.getName()).append(" is not adaptive method!\");");
			} else
			{
				int urlTypeIndex = -1;
				int i = 0;
				do
				{
					if (i >= pts.length)
						break;
					if (pts[i].equals(com/autohome/turbo/common/URL))
					{
						urlTypeIndex = i;
						break;
					}
					i++;
				} while (true);
				if (urlTypeIndex != -1)
				{
					String s = String.format("\nif (arg%d == null) throw new IllegalArgumentException(\"url == null\");", new Object[] {
						Integer.valueOf(urlTypeIndex)
					});
					code.append(s);
					s = String.format("\n%s url = arg%d;", new Object[] {
						com/autohome/turbo/common/URL.getName(), Integer.valueOf(urlTypeIndex)
					});
					code.append(s);
				} else
				{
					String attribMethod = null;
label0:
					for (int i = 0; i < pts.length; i++)
					{
						Method ms[] = pts[i].getMethods();
						Method arr$[] = ms;
						int len$ = arr$.length;
						int i$ = 0;
						do
						{
							if (i$ >= len$)
								continue label0;
							Method m = arr$[i$];
							String name = m.getName();
							if ((name.startsWith("get") || name.length() > 3) && Modifier.isPublic(m.getModifiers()) && !Modifier.isStatic(m.getModifiers()) && m.getParameterTypes().length == 0 && m.getReturnType() == com/autohome/turbo/common/URL)
							{
								urlTypeIndex = i;
								attribMethod = name;
								break label0;
							}
							i$++;
						} while (true);
					}

					if (attribMethod == null)
						throw new IllegalStateException((new StringBuilder()).append("fail to create adative class for interface ").append(type.getName()).append(": not found url parameter or url attribute in parameters of method ").append(method.getName()).toString());
					String s = String.format("\nif (arg%d == null) throw new IllegalArgumentException(\"%s argument == null\");", new Object[] {
						Integer.valueOf(urlTypeIndex), pts[urlTypeIndex].getName()
					});
					code.append(s);
					s = String.format("\nif (arg%d.%s() == null) throw new IllegalArgumentException(\"%s argument %s() == null\");", new Object[] {
						Integer.valueOf(urlTypeIndex), attribMethod, pts[urlTypeIndex].getName(), attribMethod
					});
					code.append(s);
					s = String.format("%s url = arg%d.%s();", new Object[] {
						com/autohome/turbo/common/URL.getName(), Integer.valueOf(urlTypeIndex), attribMethod
					});
					code.append(s);
				}
				String value[] = adaptiveAnnotation.value();
				if (value.length == 0)
				{
					char charArray[] = type.getSimpleName().toCharArray();
					StringBuilder sb = new StringBuilder(128);
					for (int i = 0; i < charArray.length; i++)
						if (Character.isUpperCase(charArray[i]))
						{
							if (i != 0)
								sb.append(".");
							sb.append(Character.toLowerCase(charArray[i]));
						} else
						{
							sb.append(charArray[i]);
						}

					value = (new String[] {
						sb.toString()
					});
				}
				boolean hasInvocation = false;
				int i = 0;
				do
				{
					if (i >= pts.length)
						break;
					if (pts[i].getName().equals("com.alibaba.dubbo.rpc.Invocation"))
					{
						String s = String.format("\nif (arg%d == null) throw new IllegalArgumentException(\"invocation == null\");", new Object[] {
							Integer.valueOf(i)
						});
						code.append(s);
						s = String.format("\nString methodName = arg%d.getMethodName();", new Object[] {
							Integer.valueOf(i)
						});
						code.append(s);
						hasInvocation = true;
						break;
					}
					i++;
				} while (true);
				String defaultExtName = cachedDefaultName;
				String getNameCode = null;
				for (int i = value.length - 1; i >= 0; i--)
				{
					if (i == value.length - 1)
					{
						if (null != defaultExtName)
						{
							if (!"protocol".equals(value[i]))
							{
								if (hasInvocation)
									getNameCode = String.format("url.getMethodParameter(methodName, \"%s\", \"%s\")", new Object[] {
										value[i], defaultExtName
									});
								else
									getNameCode = String.format("url.getParameter(\"%s\", \"%s\")", new Object[] {
										value[i], defaultExtName
									});
							} else
							{
								getNameCode = String.format("( url.getProtocol() == null ? \"%s\" : url.getProtocol() )", new Object[] {
									defaultExtName
								});
							}
							continue;
						}
						if (!"protocol".equals(value[i]))
						{
							if (hasInvocation)
								getNameCode = String.format("url.getMethodParameter(methodName, \"%s\", \"%s\")", new Object[] {
									value[i], defaultExtName
								});
							else
								getNameCode = String.format("url.getParameter(\"%s\")", new Object[] {
									value[i]
								});
						} else
						{
							getNameCode = "url.getProtocol()";
						}
						continue;
					}
					if (!"protocol".equals(value[i]))
					{
						if (hasInvocation)
							getNameCode = String.format("url.getMethodParameter(methodName, \"%s\", \"%s\")", new Object[] {
								value[i], defaultExtName
							});
						else
							getNameCode = String.format("url.getParameter(\"%s\", %s)", new Object[] {
								value[i], getNameCode
							});
					} else
					{
						getNameCode = String.format("url.getProtocol() == null ? (%s) : url.getProtocol()", new Object[] {
							getNameCode
						});
					}
				}

				code.append("\nString extName = ").append(getNameCode).append(";");
				String s = String.format("\nif(extName == null) throw new IllegalStateException(\"Fail to get extension(%s) name from url(\" + url.toString() + \") use keys(%s)\");", new Object[] {
					type.getName(), Arrays.toString(value)
				});
				code.append(s);
				s = String.format("\n%s extension = (%<s)%s.getExtensionLoader(%s.class).getExtension(extName);", new Object[] {
					type.getName(), com/autohome/turbo/common/extension/ExtensionLoader.getSimpleName(), type.getName()
				});
				code.append(s);
				if (!rt.equals(Void.TYPE))
					code.append("\nreturn ");
				s = String.format("extension.%s(", new Object[] {
					method.getName()
				});
				code.append(s);
				for (int i = 0; i < pts.length; i++)
				{
					if (i != 0)
						code.append(", ");
					code.append("arg").append(i);
				}

				code.append(");");
			}
			codeBuidler.append((new StringBuilder()).append("\npublic ").append(rt.getCanonicalName()).append(" ").append(method.getName()).append("(").toString());
			for (int i = 0; i < pts.length; i++)
			{
				if (i > 0)
					codeBuidler.append(", ");
				codeBuidler.append(pts[i].getCanonicalName());
				codeBuidler.append(" ");
				codeBuidler.append((new StringBuilder()).append("arg").append(i).toString());
			}

			codeBuidler.append(")");
			if (ets.length > 0)
			{
				codeBuidler.append(" throws ");
				for (int i = 0; i < ets.length; i++)
				{
					if (i > 0)
						codeBuidler.append(", ");
					codeBuidler.append(pts[i].getCanonicalName());
				}

			}
			codeBuidler.append(" {");
			codeBuidler.append(code.toString());
			codeBuidler.append("\n}");
		}

		codeBuidler.append("\n}");
		if (logger.isDebugEnabled())
			logger.debug(codeBuidler.toString());
		return codeBuidler.toString();
	}

	private static ClassLoader findClassLoader()
	{
		return com/autohome/turbo/common/extension/ExtensionLoader.getClassLoader();
	}

	public String toString()
	{
		return (new StringBuilder()).append(getClass().getName()).append("[").append(type.getName()).append("]").toString();
	}

}
