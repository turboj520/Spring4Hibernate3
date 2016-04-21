// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Jackson.java

package com.autohome.turbo.common.json;

import com.autohome.turbo.common.extension.ExtensionLoader;
import com.fasterxml.jackson.databind.*;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Referenced classes of package com.autohome.turbo.common.json:
//			JacksonObjectMapperProvider

public class Jackson
{

	private static Logger logger = LoggerFactory.getLogger(com/autohome/turbo/common/json/Jackson);
	private static ObjectMapper objectMapper;

	public Jackson()
	{
	}

	private static JacksonObjectMapperProvider getJacksonProvider()
	{
		return (JacksonObjectMapperProvider)ExtensionLoader.getExtensionLoader(com/autohome/turbo/common/json/JacksonObjectMapperProvider).getDefaultExtension();
	}

	public static ObjectMapper getObjectMapper()
	{
		if (objectMapper == null)
		{
			JacksonObjectMapperProvider jacksonObjectMapperProvider = getJacksonProvider();
			if (jacksonObjectMapperProvider != null)
				objectMapper = jacksonObjectMapperProvider.getObjectMapper();
		}
		if (objectMapper == null)
		{
			logger.warn("load objectMapper failed, use default config.");
			buildDefaultObjectMapper();
		}
		return objectMapper;
	}

	private static synchronized void buildDefaultObjectMapper()
	{
		objectMapper = new ObjectMapper();
		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		objectMapper.setTimeZone(TimeZone.getDefault());
	}

}
