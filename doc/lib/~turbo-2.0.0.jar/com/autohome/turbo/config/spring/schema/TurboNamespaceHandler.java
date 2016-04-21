// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   TurboNamespaceHandler.java

package com.autohome.turbo.config.spring.schema;

import com.autohome.turbo.common.Version;
import com.autohome.turbo.config.*;
import com.autohome.turbo.config.spring.*;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

// Referenced classes of package com.autohome.turbo.config.spring.schema:
//			TurboBeanDefinitionParser

public class TurboNamespaceHandler extends NamespaceHandlerSupport
{

	public TurboNamespaceHandler()
	{
	}

	public void init()
	{
		registerBeanDefinitionParser("application", new TurboBeanDefinitionParser(com/autohome/turbo/config/ApplicationConfig, true));
		registerBeanDefinitionParser("module", new TurboBeanDefinitionParser(com/autohome/turbo/config/ModuleConfig, true));
		registerBeanDefinitionParser("registry", new TurboBeanDefinitionParser(com/autohome/turbo/config/RegistryConfig, true));
		registerBeanDefinitionParser("monitor", new TurboBeanDefinitionParser(com/autohome/turbo/config/MonitorConfig, true));
		registerBeanDefinitionParser("provider", new TurboBeanDefinitionParser(com/autohome/turbo/config/ProviderConfig, true));
		registerBeanDefinitionParser("consumer", new TurboBeanDefinitionParser(com/autohome/turbo/config/ConsumerConfig, true));
		registerBeanDefinitionParser("protocol", new TurboBeanDefinitionParser(com/autohome/turbo/config/ProtocolConfig, true));
		registerBeanDefinitionParser("service", new TurboBeanDefinitionParser(com/autohome/turbo/config/spring/ServiceBean, true));
		registerBeanDefinitionParser("reference", new TurboBeanDefinitionParser(com/autohome/turbo/config/spring/ReferenceBean, false));
		registerBeanDefinitionParser("annotation", new TurboBeanDefinitionParser(com/autohome/turbo/config/spring/AnnotationBean, true));
	}

	static 
	{
		Version.checkDuplicate(com/autohome/turbo/config/spring/schema/TurboNamespaceHandler);
	}
}
