// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   CodecSupport.java

package com.autohome.turbo.remoting.transport;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.serialize.Serialization;
import java.util.*;

public class CodecSupport
{

	private static final Logger logger;
	private static Map ID_SERIALIZATION_MAP;

	private CodecSupport()
	{
	}

	public static Serialization getSerializationById(Byte id)
	{
		return (Serialization)ID_SERIALIZATION_MAP.get(id);
	}

	public static Serialization getSerialization(URL url)
	{
		return (Serialization)ExtensionLoader.getExtensionLoader(com/autohome/turbo/common/serialize/Serialization).getExtension(url.getParameter("serialization", "hessian2"));
	}

	public static Serialization getSerialization(URL url, Byte id)
	{
		Serialization result = getSerializationById(id);
		if (result == null)
			result = getSerialization(url);
		return result;
	}

	static 
	{
		logger = LoggerFactory.getLogger(com/autohome/turbo/remoting/transport/CodecSupport);
		ID_SERIALIZATION_MAP = new HashMap();
		Set supportedExtensions = ExtensionLoader.getExtensionLoader(com/autohome/turbo/common/serialize/Serialization).getSupportedExtensions();
		for (Iterator i$ = supportedExtensions.iterator(); i$.hasNext();)
		{
			String name = (String)i$.next();
			Serialization serialization = (Serialization)ExtensionLoader.getExtensionLoader(com/autohome/turbo/common/serialize/Serialization).getExtension(name);
			byte idByte = serialization.getContentTypeId();
			if (ID_SERIALIZATION_MAP.containsKey(Byte.valueOf(idByte)))
				logger.error((new StringBuilder()).append("Serialization extension ").append(serialization.getClass().getName()).append(" has duplicate id to Serialization extension ").append(((Serialization)ID_SERIALIZATION_MAP.get(Byte.valueOf(idByte))).getClass().getName()).append(", ignore this Serialization extension").toString());
			else
				ID_SERIALIZATION_MAP.put(Byte.valueOf(idByte), serialization);
		}

	}
}
