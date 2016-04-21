// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AnnotationBean.java

package com.autohome.turbo.config.spring;

import com.autohome.turbo.common.Constants;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.ConcurrentHashSet;
import com.autohome.turbo.common.utils.ReflectUtils;
import com.autohome.turbo.config.*;
import com.autohome.turbo.config.annotation.Reference;
import com.autohome.turbo.config.annotation.Service;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

// Referenced classes of package com.autohome.turbo.config.spring:
//			ServiceBean, ReferenceBean

public class AnnotationBean extends AbstractConfig
	implements DisposableBean, BeanFactoryPostProcessor, BeanPostProcessor, ApplicationContextAware
{

	private static final long serialVersionUID = 0x96c47957de3a5f50L;
	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/common/logger/Logger);
	private String annotationPackage;
	private String annotationPackages[];
	private final Set serviceConfigs = new ConcurrentHashSet();
	private final ConcurrentMap referenceConfigs = new ConcurrentHashMap();
	private ApplicationContext applicationContext;

	public AnnotationBean()
	{
	}

	public String getPackage()
	{
		return annotationPackage;
	}

	public void setPackage(String annotationPackage)
	{
		this.annotationPackage = annotationPackage;
		annotationPackages = annotationPackage != null && annotationPackage.length() != 0 ? Constants.COMMA_SPLIT_PATTERN.split(annotationPackage) : null;
	}

	public void setApplicationContext(ApplicationContext applicationContext)
		throws BeansException
	{
		this.applicationContext = applicationContext;
	}

	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
		throws BeansException
	{
		if (annotationPackage == null || annotationPackage.length() == 0)
			return;
		if (beanFactory instanceof BeanDefinitionRegistry)
			try
			{
				Class scannerClass = ReflectUtils.forName("org.springframework.context.annotation.ClassPathBeanDefinitionScanner");
				Object scanner = scannerClass.getConstructor(new Class[] {
					org/springframework/beans/factory/support/BeanDefinitionRegistry, Boolean.TYPE
				}).newInstance(new Object[] {
					(BeanDefinitionRegistry)beanFactory, Boolean.valueOf(true)
				});
				Class filterClass = ReflectUtils.forName("org.springframework.core.type.filter.AnnotationTypeFilter");
				Object filter = filterClass.getConstructor(new Class[] {
					java/lang/Class
				}).newInstance(new Object[] {
					com/autohome/turbo/config/annotation/Service
				});
				Method addIncludeFilter = scannerClass.getMethod("addIncludeFilter", new Class[] {
					ReflectUtils.forName("org.springframework.core.type.filter.TypeFilter")
				});
				addIncludeFilter.invoke(scanner, new Object[] {
					filter
				});
				String packages[] = Constants.COMMA_SPLIT_PATTERN.split(annotationPackage);
				Method scan = scannerClass.getMethod("scan", new Class[] {
					[Ljava/lang/String;
				});
				scan.invoke(scanner, new Object[] {
					packages
				});
			}
			catch (Throwable e) { }
	}

	public void destroy()
		throws Exception
	{
		for (Iterator i$ = serviceConfigs.iterator(); i$.hasNext();)
		{
			ServiceConfig serviceConfig = (ServiceConfig)i$.next();
			try
			{
				serviceConfig.unexport();
			}
			catch (Throwable e)
			{
				logger.error(e.getMessage(), e);
			}
		}

		for (Iterator i$ = referenceConfigs.values().iterator(); i$.hasNext();)
		{
			ReferenceConfig referenceConfig = (ReferenceConfig)i$.next();
			try
			{
				referenceConfig.destroy();
			}
			catch (Throwable e)
			{
				logger.error(e.getMessage(), e);
			}
		}

	}

	public Object postProcessAfterInitialization(Object bean, String beanName)
		throws BeansException
	{
		if (!isMatchPackage(bean))
			return bean;
		Class clazz = bean.getClass();
		if (isProxyBean(bean))
			clazz = AopUtils.getTargetClass(bean);
		Service service = (Service)clazz.getAnnotation(com/autohome/turbo/config/annotation/Service);
		if (service != null)
		{
			ServiceBean serviceConfig = new ServiceBean(service);
			if (Void.TYPE.equals(service.interfaceClass()) && "".equals(service.interfaceName()))
				if (clazz.getInterfaces().length > 0)
					serviceConfig.setInterface(clazz.getInterfaces()[0]);
				else
					throw new IllegalStateException((new StringBuilder()).append("Failed to export remote service class ").append(clazz.getName()).append(", cause: The @Service undefined interfaceClass or interfaceName, and the service class unimplemented any interfaces.").toString());
			if (applicationContext != null)
			{
				serviceConfig.setApplicationContext(applicationContext);
				if (service.registry() != null && service.registry().length > 0)
				{
					List registryConfigs = new ArrayList();
					String arr$[] = service.registry();
					int len$ = arr$.length;
					for (int i$ = 0; i$ < len$; i$++)
					{
						String registryId = arr$[i$];
						if (registryId != null && registryId.length() > 0)
							registryConfigs.add((RegistryConfig)applicationContext.getBean(registryId, com/autohome/turbo/config/RegistryConfig));
					}

					serviceConfig.setRegistries(registryConfigs);
				}
				if (service.provider() != null && service.provider().length() > 0)
					serviceConfig.setProvider((ProviderConfig)applicationContext.getBean(service.provider(), com/autohome/turbo/config/ProviderConfig));
				if (service.monitor() != null && service.monitor().length() > 0)
					serviceConfig.setMonitor((MonitorConfig)applicationContext.getBean(service.monitor(), com/autohome/turbo/config/MonitorConfig));
				if (service.application() != null && service.application().length() > 0)
					serviceConfig.setApplication((ApplicationConfig)applicationContext.getBean(service.application(), com/autohome/turbo/config/ApplicationConfig));
				if (service.module() != null && service.module().length() > 0)
					serviceConfig.setModule((ModuleConfig)applicationContext.getBean(service.module(), com/autohome/turbo/config/ModuleConfig));
				if (service.provider() != null && service.provider().length() > 0)
					serviceConfig.setProvider((ProviderConfig)applicationContext.getBean(service.provider(), com/autohome/turbo/config/ProviderConfig));
				if (service.protocol() != null && service.protocol().length > 0)
				{
					List protocolConfigs = new ArrayList();
					String arr$[] = service.protocol();
					int len$ = arr$.length;
					for (int i$ = 0; i$ < len$; i$++)
					{
						String protocolId = arr$[i$];
						if (protocolId != null && protocolId.length() > 0)
							protocolConfigs.add((ProtocolConfig)applicationContext.getBean(protocolId, com/autohome/turbo/config/ProtocolConfig));
					}

					serviceConfig.setProtocols(protocolConfigs);
				}
				try
				{
					serviceConfig.afterPropertiesSet();
				}
				catch (RuntimeException e)
				{
					throw e;
				}
				catch (Exception e)
				{
					throw new IllegalStateException(e.getMessage(), e);
				}
			}
			serviceConfig.setRef(bean);
			serviceConfigs.add(serviceConfig);
			serviceConfig.export();
		}
		return bean;
	}

	public Object postProcessBeforeInitialization(Object bean, String beanName)
		throws BeansException
	{
		if (!isMatchPackage(bean))
			return bean;
		Class clazz = bean.getClass();
		if (isProxyBean(bean))
			clazz = AopUtils.getTargetClass(bean);
		Method methods[] = clazz.getMethods();
		Method arr$[] = methods;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			Method method = arr$[i$];
			String name = method.getName();
			if (name.length() <= 3 || !name.startsWith("set") || method.getParameterTypes().length != 1 || !Modifier.isPublic(method.getModifiers()) || Modifier.isStatic(method.getModifiers()))
				continue;
			try
			{
				Reference reference = (Reference)method.getAnnotation(com/autohome/turbo/config/annotation/Reference);
				if (reference == null)
					continue;
				Object value = refer(reference, method.getParameterTypes()[0]);
				if (value != null)
					method.invoke(bean, new Object[] {
						value
					});
			}
			catch (Exception e)
			{
				throw new BeanInitializationException((new StringBuilder()).append("Failed to init remote service reference at method ").append(name).append(" in class ").append(bean.getClass().getName()).toString(), e);
			}
		}

		Field fields[] = clazz.getDeclaredFields();
		Field arr$[] = fields;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			Field field = arr$[i$];
			try
			{
				if (!field.isAccessible())
					field.setAccessible(true);
				Reference reference = (Reference)field.getAnnotation(com/autohome/turbo/config/annotation/Reference);
				if (reference == null)
					continue;
				Object value = refer(reference, field.getType());
				if (value != null)
					field.set(bean, value);
			}
			catch (Exception e)
			{
				throw new BeanInitializationException((new StringBuilder()).append("Failed to init remote service reference at filed ").append(field.getName()).append(" in class ").append(bean.getClass().getName()).toString(), e);
			}
		}

		return bean;
	}

	private Object refer(Reference reference, Class referenceClass)
	{
		String interfaceName;
		if (!"".equals(reference.interfaceName()))
			interfaceName = reference.interfaceName();
		else
		if (!Void.TYPE.equals(reference.interfaceClass()))
			interfaceName = reference.interfaceClass().getName();
		else
		if (referenceClass.isInterface())
			interfaceName = referenceClass.getName();
		else
			throw new IllegalStateException((new StringBuilder()).append("The @Reference undefined interfaceClass or interfaceName, and the property type ").append(referenceClass.getName()).append(" is not a interface.").toString());
		String key = (new StringBuilder()).append(reference.group()).append("/").append(interfaceName).append(":").append(reference.version()).toString();
		ReferenceBean referenceConfig = (ReferenceBean)referenceConfigs.get(key);
		if (referenceConfig == null)
		{
			referenceConfig = new ReferenceBean(reference);
			if (Void.TYPE.equals(reference.interfaceClass()) && "".equals(reference.interfaceName()) && referenceClass.isInterface())
				referenceConfig.setInterface(referenceClass);
			if (applicationContext != null)
			{
				referenceConfig.setApplicationContext(applicationContext);
				if (reference.registry() != null && reference.registry().length > 0)
				{
					List registryConfigs = new ArrayList();
					String arr$[] = reference.registry();
					int len$ = arr$.length;
					for (int i$ = 0; i$ < len$; i$++)
					{
						String registryId = arr$[i$];
						if (registryId != null && registryId.length() > 0)
							registryConfigs.add((RegistryConfig)applicationContext.getBean(registryId, com/autohome/turbo/config/RegistryConfig));
					}

					referenceConfig.setRegistries(registryConfigs);
				}
				if (reference.consumer() != null && reference.consumer().length() > 0)
					referenceConfig.setConsumer((ConsumerConfig)applicationContext.getBean(reference.consumer(), com/autohome/turbo/config/ConsumerConfig));
				if (reference.monitor() != null && reference.monitor().length() > 0)
					referenceConfig.setMonitor((MonitorConfig)applicationContext.getBean(reference.monitor(), com/autohome/turbo/config/MonitorConfig));
				if (reference.application() != null && reference.application().length() > 0)
					referenceConfig.setApplication((ApplicationConfig)applicationContext.getBean(reference.application(), com/autohome/turbo/config/ApplicationConfig));
				if (reference.module() != null && reference.module().length() > 0)
					referenceConfig.setModule((ModuleConfig)applicationContext.getBean(reference.module(), com/autohome/turbo/config/ModuleConfig));
				if (reference.consumer() != null && reference.consumer().length() > 0)
					referenceConfig.setConsumer((ConsumerConfig)applicationContext.getBean(reference.consumer(), com/autohome/turbo/config/ConsumerConfig));
				try
				{
					referenceConfig.afterPropertiesSet();
				}
				catch (RuntimeException e)
				{
					throw e;
				}
				catch (Exception e)
				{
					throw new IllegalStateException(e.getMessage(), e);
				}
			}
			referenceConfigs.putIfAbsent(key, referenceConfig);
			referenceConfig = (ReferenceBean)referenceConfigs.get(key);
		}
		return referenceConfig.get();
	}

	private boolean isMatchPackage(Object bean)
	{
		if (annotationPackages == null || annotationPackages.length == 0)
			return true;
		Class clazz = bean.getClass();
		if (isProxyBean(bean))
			clazz = AopUtils.getTargetClass(bean);
		String beanClassName = clazz.getName();
		String arr$[] = annotationPackages;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			String pkg = arr$[i$];
			if (beanClassName.startsWith(pkg))
				return true;
		}

		return false;
	}

	private boolean isProxyBean(Object bean)
	{
		return AopUtils.isAopProxy(bean);
	}

}
