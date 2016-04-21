// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ReferenceBean.java

package com.autohome.turbo.config.spring;

import com.autohome.turbo.config.*;
import com.autohome.turbo.config.annotation.Reference;
import com.autohome.turbo.config.spring.extension.SpringExtensionFactory;
import java.util.*;
import org.springframework.beans.factory.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ReferenceBean extends ReferenceConfig
	implements FactoryBean, ApplicationContextAware, InitializingBean, DisposableBean
{

	private static final long serialVersionUID = 0x2f56c2c2087abfeL;
	private transient ApplicationContext applicationContext;

	public ReferenceBean()
	{
	}

	public ReferenceBean(Reference reference)
	{
		super(reference);
	}

	public void setApplicationContext(ApplicationContext applicationContext)
	{
		this.applicationContext = applicationContext;
		SpringExtensionFactory.addApplicationContext(applicationContext);
	}

	public Object getObject()
		throws Exception
	{
		return get();
	}

	public Class getObjectType()
	{
		return getInterfaceClass();
	}

	public boolean isSingleton()
	{
		return true;
	}

	public void afterPropertiesSet()
		throws Exception
	{
		if (getConsumer() == null)
		{
			Map consumerConfigMap = applicationContext != null ? BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, com/autohome/turbo/config/ConsumerConfig, false, false) : null;
			if (consumerConfigMap != null && consumerConfigMap.size() > 0)
			{
				ConsumerConfig consumerConfig = null;
				Iterator i$ = consumerConfigMap.values().iterator();
				do
				{
					if (!i$.hasNext())
						break;
					ConsumerConfig config = (ConsumerConfig)i$.next();
					if (config.isDefault() == null || config.isDefault().booleanValue())
					{
						if (consumerConfig != null)
							throw new IllegalStateException((new StringBuilder()).append("Duplicate consumer configs: ").append(consumerConfig).append(" and ").append(config).toString());
						consumerConfig = config;
					}
				} while (true);
				if (consumerConfig != null)
					setConsumer(consumerConfig);
			}
		}
		if (getApplication() == null && (getConsumer() == null || getConsumer().getApplication() == null))
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
		if (getModule() == null && (getConsumer() == null || getConsumer().getModule() == null))
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
		if ((getRegistries() == null || getRegistries().size() == 0) && (getConsumer() == null || getConsumer().getRegistries() == null || getConsumer().getRegistries().size() == 0) && (getApplication() == null || getApplication().getRegistries() == null || getApplication().getRegistries().size() == 0))
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
		if (getMonitor() == null && (getConsumer() == null || getConsumer().getMonitor() == null) && (getApplication() == null || getApplication().getMonitor() == null))
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
		Boolean b = isInit();
		if (b == null && getConsumer() != null)
			b = getConsumer().isInit();
		if (b != null && b.booleanValue())
			getObject();
	}
}
