// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   TurboBeanDefinitionParser.java

package com.autohome.turbo.config.spring.schema;

import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.ReflectUtils;
import com.autohome.turbo.common.utils.StringUtils;
import com.autohome.turbo.config.*;
import com.autohome.turbo.config.spring.ReferenceBean;
import com.autohome.turbo.config.spring.ServiceBean;
import com.autohome.turbo.rpc.Protocol;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.*;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.*;

public class TurboBeanDefinitionParser
	implements BeanDefinitionParser
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/config/spring/schema/TurboBeanDefinitionParser);
	private final Class beanClass;
	private final boolean required;
	private static final Pattern GROUP_AND_VERION = Pattern.compile("^[\\-.0-9_a-zA-Z]+(\\:[\\-.0-9_a-zA-Z]+)?$");

	public TurboBeanDefinitionParser(Class beanClass, boolean required)
	{
		this.beanClass = beanClass;
		this.required = required;
	}

	public BeanDefinition parse(Element element, ParserContext parserContext)
	{
		return parse(element, parserContext, beanClass, required);
	}

	private static BeanDefinition parse(Element element, ParserContext parserContext, Class beanClass, boolean required)
	{
		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		beanDefinition.setBeanClass(beanClass);
		beanDefinition.setLazyInit(false);
		String id = element.getAttribute("id");
		if ((id == null || id.length() == 0) && required)
		{
			String generatedBeanName = element.getAttribute("name");
			if (generatedBeanName == null || generatedBeanName.length() == 0)
				if (com/autohome/turbo/config/ProtocolConfig.equals(beanClass))
					generatedBeanName = "dubbo";
				else
					generatedBeanName = element.getAttribute("interface");
			if (generatedBeanName == null || generatedBeanName.length() == 0)
				generatedBeanName = beanClass.getName();
			id = generatedBeanName;
			int counter = 2;
			for (; parserContext.getRegistry().containsBeanDefinition(id); id = (new StringBuilder()).append(generatedBeanName).append(counter++).toString());
		}
		if (id != null && id.length() > 0)
		{
			if (parserContext.getRegistry().containsBeanDefinition(id))
				throw new IllegalStateException((new StringBuilder()).append("Duplicate spring bean id ").append(id).toString());
			parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
			beanDefinition.getPropertyValues().addPropertyValue("id", id);
		}
		if (com/autohome/turbo/config/ProtocolConfig.equals(beanClass))
		{
			String arr$[] = parserContext.getRegistry().getBeanDefinitionNames();
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				String name = arr$[i$];
				BeanDefinition definition = parserContext.getRegistry().getBeanDefinition(name);
				PropertyValue property = definition.getPropertyValues().getPropertyValue("protocol");
				if (property != null)
				{
					Object value = property.getValue();
					if ((value instanceof ProtocolConfig) && id.equals(((ProtocolConfig)value).getName()))
						definition.getPropertyValues().addPropertyValue("protocol", new RuntimeBeanReference(id));
				}
			}

		} else
		if (com/autohome/turbo/config/spring/ServiceBean.equals(beanClass))
		{
			String className = element.getAttribute("class");
			if (className != null && className.length() > 0)
			{
				RootBeanDefinition classDefinition = new RootBeanDefinition();
				classDefinition.setBeanClass(ReflectUtils.forName(className));
				classDefinition.setLazyInit(false);
				parseProperties(element.getChildNodes(), classDefinition);
				beanDefinition.getPropertyValues().addPropertyValue("ref", new BeanDefinitionHolder(classDefinition, (new StringBuilder()).append(id).append("Impl").toString()));
			}
		} else
		if (com/autohome/turbo/config/ProviderConfig.equals(beanClass))
			parseNested(element, parserContext, com/autohome/turbo/config/spring/ServiceBean, true, "service", "provider", id, beanDefinition);
		else
		if (com/autohome/turbo/config/ConsumerConfig.equals(beanClass))
			parseNested(element, parserContext, com/autohome/turbo/config/spring/ReferenceBean, false, "reference", "consumer", id, beanDefinition);
		Set props = new HashSet();
		ManagedMap parameters = null;
		Method arr$[] = beanClass.getMethods();
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			Method setter = arr$[i$];
			String name = setter.getName();
			if (name.length() <= 3 || !name.startsWith("set") || !Modifier.isPublic(setter.getModifiers()) || setter.getParameterTypes().length != 1)
				continue;
			Class type = setter.getParameterTypes()[0];
			String property = StringUtils.camelToSplitName((new StringBuilder()).append(name.substring(3, 4).toLowerCase()).append(name.substring(4)).toString(), "-");
			props.add(property);
			Method getter = null;
			try
			{
				getter = beanClass.getMethod((new StringBuilder()).append("get").append(name.substring(3)).toString(), new Class[0]);
			}
			catch (NoSuchMethodException e)
			{
				try
				{
					getter = beanClass.getMethod((new StringBuilder()).append("is").append(name.substring(3)).toString(), new Class[0]);
				}
				catch (NoSuchMethodException e2) { }
			}
			if (getter == null || !Modifier.isPublic(getter.getModifiers()) || !type.equals(getter.getReturnType()))
				continue;
			if ("parameters".equals(property))
			{
				parameters = parseParameters(element.getChildNodes(), beanDefinition);
				continue;
			}
			if ("methods".equals(property))
			{
				parseMethods(id, element.getChildNodes(), beanDefinition, parserContext);
				continue;
			}
			if ("arguments".equals(property))
			{
				parseArguments(id, element.getChildNodes(), beanDefinition, parserContext);
				continue;
			}
			String value = element.getAttribute(property);
			if (value == null)
				continue;
			value = value.trim();
			if (value.length() <= 0)
				continue;
			if ("registry".equals(property) && "N/A".equalsIgnoreCase(value))
			{
				RegistryConfig registryConfig = new RegistryConfig();
				registryConfig.setAddress("N/A");
				beanDefinition.getPropertyValues().addPropertyValue(property, registryConfig);
				continue;
			}
			if ("registry".equals(property) && value.indexOf(',') != -1)
			{
				parseMultiRef("registries", value, beanDefinition, parserContext);
				continue;
			}
			if ("provider".equals(property) && value.indexOf(',') != -1)
			{
				parseMultiRef("providers", value, beanDefinition, parserContext);
				continue;
			}
			if ("protocol".equals(property) && value.indexOf(',') != -1)
			{
				parseMultiRef("protocols", value, beanDefinition, parserContext);
				continue;
			}
			Object reference;
			if (isPrimitive(type))
			{
				if ("async".equals(property) && "false".equals(value) || "timeout".equals(property) && "0".equals(value) || "delay".equals(property) && "0".equals(value) || "version".equals(property) && "0.0.0".equals(value) || "stat".equals(property) && "-1".equals(value) || "reliable".equals(property) && "false".equals(value))
					value = null;
				reference = value;
			} else
			if ("protocol".equals(property) && ExtensionLoader.getExtensionLoader(com/autohome/turbo/rpc/Protocol).hasExtension(value) && (!parserContext.getRegistry().containsBeanDefinition(value) || !com/autohome/turbo/config/ProtocolConfig.getName().equals(parserContext.getRegistry().getBeanDefinition(value).getBeanClassName())))
			{
				if ("dubbo:provider".equals(element.getTagName()))
					logger.warn((new StringBuilder()).append("Recommended replace <dubbo:provider protocol=\"").append(value).append("\" ... /> to <dubbo:protocol name=\"").append(value).append("\" ... />").toString());
				ProtocolConfig protocol = new ProtocolConfig();
				protocol.setName(value);
				reference = protocol;
			} else
			if ("monitor".equals(property) && (!parserContext.getRegistry().containsBeanDefinition(value) || !com/autohome/turbo/config/MonitorConfig.getName().equals(parserContext.getRegistry().getBeanDefinition(value).getBeanClassName())))
				reference = convertMonitor(value);
			else
			if ("onreturn".equals(property))
			{
				int index = value.lastIndexOf(".");
				String returnRef = value.substring(0, index);
				String returnMethod = value.substring(index + 1);
				reference = new RuntimeBeanReference(returnRef);
				beanDefinition.getPropertyValues().addPropertyValue("onreturnMethod", returnMethod);
			} else
			if ("onthrow".equals(property))
			{
				int index = value.lastIndexOf(".");
				String throwRef = value.substring(0, index);
				String throwMethod = value.substring(index + 1);
				reference = new RuntimeBeanReference(throwRef);
				beanDefinition.getPropertyValues().addPropertyValue("onthrowMethod", throwMethod);
			} else
			{
				if ("ref".equals(property) && parserContext.getRegistry().containsBeanDefinition(value))
				{
					BeanDefinition refBean = parserContext.getRegistry().getBeanDefinition(value);
					if (!refBean.isSingleton())
						throw new IllegalStateException((new StringBuilder()).append("The exported service ref ").append(value).append(" must be singleton! Please set the ").append(value).append(" bean scope to singleton, eg: <bean id=\"").append(value).append("\" scope=\"singleton\" ...>").toString());
				}
				reference = new RuntimeBeanReference(value);
			}
			beanDefinition.getPropertyValues().addPropertyValue(property, reference);
		}

		NamedNodeMap attributes = element.getAttributes();
		int len = attributes.getLength();
		for (int i = 0; i < len; i++)
		{
			Node node = attributes.item(i);
			String name = node.getLocalName();
			if (props.contains(name))
				continue;
			if (parameters == null)
				parameters = new ManagedMap();
			String value = node.getNodeValue();
			parameters.put(name, new TypedStringValue(value, java/lang/String));
		}

		if (parameters != null)
			beanDefinition.getPropertyValues().addPropertyValue("parameters", parameters);
		return beanDefinition;
	}

	protected static MonitorConfig convertMonitor(String monitor)
	{
		if (monitor == null || monitor.length() == 0)
			return null;
		if (GROUP_AND_VERION.matcher(monitor).matches())
		{
			int i = monitor.indexOf(':');
			String group;
			String version;
			if (i > 0)
			{
				group = monitor.substring(0, i);
				version = monitor.substring(i + 1);
			} else
			{
				group = monitor;
				version = null;
			}
			MonitorConfig monitorConfig = new MonitorConfig();
			monitorConfig.setGroup(group);
			monitorConfig.setVersion(version);
			return monitorConfig;
		} else
		{
			return null;
		}
	}

	private static boolean isPrimitive(Class cls)
	{
		return cls.isPrimitive() || cls == java/lang/Boolean || cls == java/lang/Byte || cls == java/lang/Character || cls == java/lang/Short || cls == java/lang/Integer || cls == java/lang/Long || cls == java/lang/Float || cls == java/lang/Double || cls == java/lang/String || cls == java/util/Date || cls == java/lang/Class;
	}

	private static void parseMultiRef(String property, String value, RootBeanDefinition beanDefinition, ParserContext parserContext)
	{
		String values[] = value.split("\\s*[,]+\\s*");
		ManagedList list = null;
		for (int i = 0; i < values.length; i++)
		{
			String v = values[i];
			if (v == null || v.length() <= 0)
				continue;
			if (list == null)
				list = new ManagedList();
			list.add(new RuntimeBeanReference(v));
		}

		beanDefinition.getPropertyValues().addPropertyValue(property, list);
	}

	private static void parseNested(Element element, ParserContext parserContext, Class beanClass, boolean required, String tag, String property, String ref, BeanDefinition beanDefinition)
	{
		NodeList nodeList = element.getChildNodes();
		if (nodeList != null && nodeList.getLength() > 0)
		{
			boolean first = true;
			for (int i = 0; i < nodeList.getLength(); i++)
			{
				Node node = nodeList.item(i);
				if (!(node instanceof Element) || !tag.equals(node.getNodeName()) && !tag.equals(node.getLocalName()))
					continue;
				if (first)
				{
					first = false;
					String isDefault = element.getAttribute("default");
					if (isDefault == null || isDefault.length() == 0)
						beanDefinition.getPropertyValues().addPropertyValue("default", "false");
				}
				BeanDefinition subDefinition = parse((Element)node, parserContext, beanClass, required);
				if (subDefinition != null && ref != null && ref.length() > 0)
					subDefinition.getPropertyValues().addPropertyValue(property, new RuntimeBeanReference(ref));
			}

		}
	}

	private static void parseProperties(NodeList nodeList, RootBeanDefinition beanDefinition)
	{
		if (nodeList != null && nodeList.getLength() > 0)
		{
			for (int i = 0; i < nodeList.getLength(); i++)
			{
				Node node = nodeList.item(i);
				if (!(node instanceof Element) || !"property".equals(node.getNodeName()) && !"property".equals(node.getLocalName()))
					continue;
				String name = ((Element)node).getAttribute("name");
				if (name == null || name.length() <= 0)
					continue;
				String value = ((Element)node).getAttribute("value");
				String ref = ((Element)node).getAttribute("ref");
				if (value != null && value.length() > 0)
				{
					beanDefinition.getPropertyValues().addPropertyValue(name, value);
					continue;
				}
				if (ref != null && ref.length() > 0)
					beanDefinition.getPropertyValues().addPropertyValue(name, new RuntimeBeanReference(ref));
				else
					throw new UnsupportedOperationException((new StringBuilder()).append("Unsupported <property name=\"").append(name).append("\"> sub tag, Only supported <property name=\"").append(name).append("\" ref=\"...\" /> or <property name=\"").append(name).append("\" value=\"...\" />").toString());
			}

		}
	}

	private static ManagedMap parseParameters(NodeList nodeList, RootBeanDefinition beanDefinition)
	{
		if (nodeList != null && nodeList.getLength() > 0)
		{
			ManagedMap parameters = null;
			for (int i = 0; i < nodeList.getLength(); i++)
			{
				Node node = nodeList.item(i);
				if (!(node instanceof Element) || !"parameter".equals(node.getNodeName()) && !"parameter".equals(node.getLocalName()))
					continue;
				if (parameters == null)
					parameters = new ManagedMap();
				String key = ((Element)node).getAttribute("key");
				String value = ((Element)node).getAttribute("value");
				boolean hide = "true".equals(((Element)node).getAttribute("hide"));
				if (hide)
					key = (new StringBuilder()).append(".").append(key).toString();
				parameters.put(key, new TypedStringValue(value, java/lang/String));
			}

			return parameters;
		} else
		{
			return null;
		}
	}

	private static void parseMethods(String id, NodeList nodeList, RootBeanDefinition beanDefinition, ParserContext parserContext)
	{
		if (nodeList != null && nodeList.getLength() > 0)
		{
			ManagedList methods = null;
			for (int i = 0; i < nodeList.getLength(); i++)
			{
				Node node = nodeList.item(i);
				if (!(node instanceof Element))
					continue;
				Element element = (Element)node;
				if (!"method".equals(node.getNodeName()) && !"method".equals(node.getLocalName()))
					continue;
				String methodName = element.getAttribute("name");
				if (methodName == null || methodName.length() == 0)
					throw new IllegalStateException("<dubbo:method> name attribute == null");
				if (methods == null)
					methods = new ManagedList();
				BeanDefinition methodBeanDefinition = parse((Element)node, parserContext, com/autohome/turbo/config/MethodConfig, false);
				String name = (new StringBuilder()).append(id).append(".").append(methodName).toString();
				BeanDefinitionHolder methodBeanDefinitionHolder = new BeanDefinitionHolder(methodBeanDefinition, name);
				methods.add(methodBeanDefinitionHolder);
			}

			if (methods != null)
				beanDefinition.getPropertyValues().addPropertyValue("methods", methods);
		}
	}

	private static void parseArguments(String id, NodeList nodeList, RootBeanDefinition beanDefinition, ParserContext parserContext)
	{
		if (nodeList != null && nodeList.getLength() > 0)
		{
			ManagedList arguments = null;
			for (int i = 0; i < nodeList.getLength(); i++)
			{
				Node node = nodeList.item(i);
				if (!(node instanceof Element))
					continue;
				Element element = (Element)node;
				if (!"argument".equals(node.getNodeName()) && !"argument".equals(node.getLocalName()))
					continue;
				String argumentIndex = element.getAttribute("index");
				if (arguments == null)
					arguments = new ManagedList();
				BeanDefinition argumentBeanDefinition = parse((Element)node, parserContext, com/autohome/turbo/config/ArgumentConfig, false);
				String name = (new StringBuilder()).append(id).append(".").append(argumentIndex).toString();
				BeanDefinitionHolder argumentBeanDefinitionHolder = new BeanDefinitionHolder(argumentBeanDefinition, name);
				arguments.add(argumentBeanDefinitionHolder);
			}

			if (arguments != null)
				beanDefinition.getPropertyValues().addPropertyValue("arguments", arguments);
		}
	}

}
