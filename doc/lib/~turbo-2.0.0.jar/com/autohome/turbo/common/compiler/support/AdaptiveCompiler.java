// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AdaptiveCompiler.java

package com.autohome.turbo.common.compiler.support;

import com.autohome.turbo.common.compiler.Compiler;
import com.autohome.turbo.common.extension.ExtensionLoader;

public class AdaptiveCompiler
	implements Compiler
{

	private static volatile String DEFAULT_COMPILER;

	public AdaptiveCompiler()
	{
	}

	public static void setDefaultCompiler(String compiler)
	{
		DEFAULT_COMPILER = compiler;
	}

	public Class compile(String code, ClassLoader classLoader)
	{
		ExtensionLoader loader = ExtensionLoader.getExtensionLoader(com/autohome/turbo/common/compiler/Compiler);
		String name = DEFAULT_COMPILER;
		Compiler compiler;
		if (name != null && name.length() > 0)
			compiler = (Compiler)loader.getExtension(name);
		else
			compiler = (Compiler)loader.getDefaultExtension();
		return compiler.compile(code, classLoader);
	}
}
