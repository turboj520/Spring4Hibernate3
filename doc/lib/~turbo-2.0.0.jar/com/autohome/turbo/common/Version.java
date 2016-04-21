// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Version.java

package com.autohome.turbo.common;

import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.ClassHelper;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.*;

public final class Version
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/common/Version);
	private static final String VERSION = getVersion(com/autohome/turbo/common/Version, "2.0.0");
	private static final boolean INTERNAL = hasResource("com/alibaba/dubbo/registry/internal/RemoteRegistry.class");
	private static final boolean COMPATIBLE = hasResource("com/taobao/remoting/impl/ConnectionRequest.class");

	private Version()
	{
	}

	public static String getVersion()
	{
		return VERSION;
	}

	public static boolean isInternalVersion()
	{
		return INTERNAL;
	}

	public static boolean isCompatibleVersion()
	{
		return COMPATIBLE;
	}

	private static boolean hasResource(String path)
	{
		return com/autohome/turbo/common/Version.getClassLoader().getResource(path) != null;
		Throwable t;
		t;
		return false;
	}

	public static String getVersion(Class cls, String defaultVersion)
	{
		String version;
		version = cls.getPackage().getImplementationVersion();
		if (version == null || version.length() == 0)
			version = cls.getPackage().getSpecificationVersion();
		if (version == null || version.length() == 0)
		{
			CodeSource codeSource = cls.getProtectionDomain().getCodeSource();
			if (codeSource == null)
			{
				logger.info((new StringBuilder()).append("No codeSource for class ").append(cls.getName()).append(" when getVersion, use default version ").append(defaultVersion).toString());
			} else
			{
				String file = codeSource.getLocation().getFile();
				if (file != null && file.length() > 0 && file.endsWith(".jar"))
				{
					file = file.substring(0, file.length() - 4);
					int i = file.lastIndexOf('/');
					if (i >= 0)
						file = file.substring(i + 1);
					i = file.indexOf("-");
					if (i >= 0)
						file = file.substring(i + 1);
					do
					{
						if (file.length() <= 0 || Character.isDigit(file.charAt(0)))
							break;
						i = file.indexOf("-");
						if (i < 0)
							break;
						file = file.substring(i + 1);
					} while (true);
					version = file;
				}
			}
		}
		return version != null && version.length() != 0 ? version : defaultVersion;
		Throwable e;
		e;
		logger.error((new StringBuilder()).append("return default version, ignore exception ").append(e.getMessage()).toString(), e);
		return defaultVersion;
	}

	public static void checkDuplicate(Class cls, boolean failOnError)
	{
		checkDuplicate((new StringBuilder()).append(cls.getName().replace('.', '/')).append(".class").toString(), failOnError);
	}

	public static void checkDuplicate(Class cls)
	{
		checkDuplicate(cls, false);
	}

	public static void checkDuplicate(String path, boolean failOnError)
	{
		try
		{
			Enumeration urls = ClassHelper.getCallerClassLoader(com/autohome/turbo/common/Version).getResources(path);
			Set files = new HashSet();
			do
			{
				if (!urls.hasMoreElements())
					break;
				URL url = (URL)urls.nextElement();
				if (url != null)
				{
					String file = url.getFile();
					if (file != null && file.length() > 0)
						files.add(file);
				}
			} while (true);
			if (files.size() > 1)
			{
				String error = (new StringBuilder()).append("Duplicate class ").append(path).append(" in ").append(files.size()).append(" jar ").append(files).toString();
				if (failOnError)
					throw new IllegalStateException(error);
				logger.error(error);
			}
		}
		catch (Throwable e)
		{
			logger.error(e.getMessage(), e);
		}
	}

	static 
	{
		checkDuplicate(com/autohome/turbo/common/Version);
	}
}
