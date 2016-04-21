// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   DefaultJacksonObjectMapperProvider.java

package com.autohome.turbo.common.json;

import com.fasterxml.jackson.databind.*;
import java.util.TimeZone;

// Referenced classes of package com.autohome.turbo.common.json:
//			JacksonObjectMapperProvider

public class DefaultJacksonObjectMapperProvider
	implements JacksonObjectMapperProvider
{

	public DefaultJacksonObjectMapperProvider()
	{
	}

	public ObjectMapper getObjectMapper()
	{
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		objectMapper.setTimeZone(TimeZone.getDefault());
		return objectMapper;
	}
}
