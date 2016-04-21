// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ServiceBean.java

package com.autohome.turbo.config.spring;

import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.config.*;
import com.autohome.turbo.config.annotation.Service;
import com.autohome.turbo.config.spring.extension.SpringExtensionFactory;
import java.lang.reflect.Method;
import java.util.*;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.*;
import org.springframework.context.*;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.AbstractApplicationContext;

public class ServiceBean extends ServiceConfig
	implements InitializingBean, DisposableBean, ApplicationContextAware, ApplicationListener, BeanNameAware
{

	private static final long serialVersionUID = 0x2f56c2c2087abfeL;
	private static transient ApplicationContext SPRING_CONTEXT;
	private transient ApplicationContext applicationContext;
	private transient String beanName;
	private transient boolean supportedApplicationListener;

	public ServiceBean()
	{
	}

	public ServiceBean(Service service)
	{
		super(service);
	}

	public static ApplicationContext getSpringContext()
	{
		return SPRING_CONTEXT;
	}

	public void setApplicationContext(ApplicationContext applicationContext)
	{
		this.applicationContext = applicationContext;
		SpringExtensionFactory.addApplicationContext(applicationContext);
		if (applicationContext != null)
		{
			SPRING_CONTEXT = applicationContext;
			try
			{
				Method method = applicationContext.getClass().getMethod("addApplicationListener", new Class[] {
					org/springframework/context/ApplicationListener
				});
				method.invoke(applicationContext, new Object[] {
					this
				});
				supportedApplicationListener = true;
			}
			catch (Throwable t)
			{
				if (applicationContext instanceof AbstractApplicationContext)
					try
					{
						Method method = org/springframework/context/support/AbstractApplicationContext.getDeclaredMethod("addListener", new Class[] {
							org/springframework/context/ApplicationListener
						});
						if (!method.isAccessible())
							method.setAccessible(true);
						method.invoke(applicationContext, new Object[] {
							this
						});
						supportedApplicationListener = true;
					}
					catch (Throwable t2) { }
			}
		}
	}

	public void setBeanName(String name)
	{
		beanName = name;
	}

	public void onApplicationEvent(ApplicationEvent event)
	{
		if (org/springframework/context/event/ContextRefreshedEvent.getName().equals(event.getClass().getName()) && isDelay() && !isExported() && !isUnexported())
		{
			if (logger.isInfoEnabled())
				logger.info((new StringBuilder()).append("The service ready on spring started. service: ").append(getInterface()).toString());
			export();
		}
	}

	private boolean isDelay()
	{
		Integer delay = getDelay();
		ProviderConfig provider = getProvider();
		if (delay == null && provider != null)
			delay = provider.getDelay();
		return supportedApplicationListener && (delay == null || delay.intValue() == -1);
	}

	public void afterPropertiesSet()
		throws Exception
	{
		if (getProvider() == null)
		{
			Map providerConfigMap = applicationContext != null ? BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, com/autohome/turbo/config/ProviderConfig, false, false) : null;
			if (providerConfigMap != null && providerConfigMap.size() > 0)
			{
				Map protocolConfigMap = applicationContext != null ? BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, com/autohome/turbo/config/ProtocolConfig, false, false) : null;
				if ((protocolConfigMap == null || protocolConfigMap.size() == 0) && providerConfigMap.size() > 1)
				{
					List providerConfigs = new ArrayList();
					Iterator i$ = providerConfigMap.values().iterator();
					do
					{
						if (!i$.hasNext())
							break;
						ProviderConfig config = (ProviderConfig)i$.next();
						if (config.isDefault() != null && config.isDefault().booleanValue())
							providerConfigs.add(config);
					} while (true);
					if (providerConfigs.size() > 0)
						setProviders(providerConfigs);
				} else
				{
					ProviderConfig providerConfig = null;
					Iterator i$ = providerConfigMap.values().iterator();
					do
					{
						if (!i$.hasNext())
							break;
						ProviderConfig config = (ProviderConfig)i$.next();
						if (config.isDefault() == null || config.isDefault().booleanValue())
						{
							if (providerConfig != null)
								throw new IllegalStateException((new StringBuilder()).append("Duplicate provider configs: ").append(providerConfig).append(" and ").append(config).toString());
							providerConfig = config;
						}
					} while (true);
					if (providerConfig != null)
						setProvider(providerConfig);
				}
			}
		}
		if (getApplication() == null && (getProvider() == null || getProvider().getApplication() == null))
		{
			Map applicationConfigMap = applicationContext != null ? BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, com/autohome/turbo/config/ApplicationConfig, false, false) : null;
			if (applicationConfigMap != null && applicationConfigMap.size() > 0)
			{
				ApplicationConfig applicationConfig = null;
				Iterator i$ = applicationConfigMap.values().iterator();
				do
				{
					if (!i$.hasNext())
						break;
					ApplicationConfig config = (ApplicationConfig)i$.next();
					if (config.isDefault() == null || config.isDefault().booleanValue())
					{
						if (applicationConfig != null)
							throw new IllegalStateException((new StringBuilder()).append("Duplicate application configs: ").append(applicationConfig).append(" and ").append(config).toString());
						applicationConfig = config;
					}
				} while (true);
				if (applicationConfig != null)
					setApplication(applicationConfig);
			}
		}
		if (getModule() == null && (getProvider() == null || getProvider().getModule() == null))
		{
			Map moduleConfigMap = applicationContext != null ? BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, com/autohome/turbo/config/ModuleConfig, false, false) : null;
			if (moduleConfigMap != null && moduleConfigMap.size() > 0)
			{
				ModuleConfig moduleConfig = null;
				Iterator i$ = moduleConfigMap.values().iterator();
				do
				{
					if (!i$.hasNext())
						break;
					ModuleConfig config = (ModuleConfig)i$.next();
					if (config.isDefault() == null || config.isDefault().booleanValue())
					{
						if (moduleConfig != null)
							throw new IllegalStateException((new StringBuilder()).append("Duplicate module configs: ").append(moduleConfig).append(" and ").append(config).toString());
						moduleConfig = config;
					}
				} while (true);
				if (moduleConfig != null)
					setModule(moduleConfig);
			}
		}
		if ((getRegistries() == null || getRegistries().size() == 0) && (getProvider() == null || getProvider().getRegistries() == null || getProvider().getRegistries().size() == 0) && (getApplication() == null || getApplication().getRegistries() == null || getApplication().getRegistries().size() == 0))
		{
			Map registryConfigMap = applicationContext != null ? BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, com/autohome/turbo/config/RegistryConfig, false, false) : null;
			if (registryConfigMap != null && registryConfigMap.size() > 0)
			{
				List registryConfigs = new ArrayList();
				Iterator i$ = registryConfigMap.values().iterator();
				do
				{
					if (!i$.hasNext())
						break;
					RegistryConfig config = (RegistryConfig)i$.next();
					if (config.isDefault() == null || config.isDefault().booleanValue())
						registryConfigs.add(config);
				} while (true);
				if (registryConfigs != null && registryConfigs.size() > 0)
					super.setRegistries(registryConfigs);
			}
		}
		if (getMonitor() == null && (getProvider() == null || getProvider().getMonitor() == null) && (getApplication() == null || getApplication().getMonitor() == null))
		{
			Map monitorConfigMap = applicationContext != null ? BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, com/autohome/turbo/config/MonitorConfig, false, false) : null;
			if (monitorConfigMap != null && monitorConfigMap.size() > 0)
			{
				MonitorConfig monitorConfig = null;
				Iterator i$ = monitorConfigMap.values().iterator();
				do
				{
					if (!i$.hasNext())
						break;
					MonitorConfig config = (MonitorConfig)i$.next();
					if (config.isDefault() == null || config.isDefault().booleanValue())
					{
						if (monitorConfig != null)
							throw new IllegalStateException((new StringBuilder()).append("Duplicate monitor configs: ").append(monitorConfig).append(" and ").append(config).toString());
						monitorConfig = config;
					}
				} while (true);
				if (monitorConfig != null)
					setMonitor(monitorConfig);
			}
		}
		if ((getProtocols() == null || getProtocols().size() == 0) && (getProvider() == null || getProvider().getProtocols() == null || getProvider().getProtocols().size() == 0))
		{
			Map protocolConfigMap = applicationContext != null ? BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, com/autohome/turbo/config/ProtocolConfig, false, false) : null;
			if (protocolConfigMap != null && protocolConfigMap.size() > 0)
			{
				List protocolConfigs = new ArrayList();
				Iterator i$ = protocolConfigMap.values().iterator();
				do
				{
					if (!i$.hasNext())
						break;
					ProtocolConfig config = (ProtocolConfig)i$.next();
					if (config.isDefault() == null || config.isDefault().booleanValue())
						protocolConfigs.add(config);
				} while (true);
				if (protocolConfigs != null && protocolConfigs.size() > 0)
					super.setProtocols(protocolConfigs);
			}
		}
		if ((getPath() == null || getPath().length() == 0) && beanName != null && beanName.length() > 0 && getInterface() != null && getInterface().length() > 0 && beanName.startsWith(getInterface()))
			setPath(beanName);
		if (!isDelay())
			export();
	}

	public void destroy()
		throws Exception
	{
		unexport();
	}

	protected Class getServiceClass(Object ref)
	{
		if (AopUtils.isAopProxy(ref))
			return AopUtils.getTargetClass(ref);
		else
			return super.getServiceClass(ref);
	}
}
