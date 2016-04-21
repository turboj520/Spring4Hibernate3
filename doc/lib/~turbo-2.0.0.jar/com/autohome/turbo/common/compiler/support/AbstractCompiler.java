// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractCompiler.java

package com.autohome.turbo.common.compiler.support;

import com.autohome.turbo.common.compiler.Compiler;
import com.autohome.turbo.common.utils.ClassHelper;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Referenced classes of package com.autohome.turbo.common.compiler.support:
//			ClassUtils

public abstract class AbstractCompiler
	implements Compiler
{

	private static final Pattern PACKAGE_PATTERN = Pattern.compile("package\\s+([$_a-zA-Z][$_a-zA-Z0-9\\.]*);");
	private static final Pattern CLASS_PATTERN = Pattern.compile("class\\s+([$_a-zA-Z][$_a-zA-Z0-9]*)\\s+");

	public AbstractCompiler()
	{
	}

	public Class compile(String code, ClassLoader classLoader)
	{
		String className;
		code = code.trim();
		Matcher matcher = PACKAGE_PATTERN.matcher(code);
		String pkg;
		if (matcher.find())
			pkg = matcher.group(1);
		else
			pkg = "";
		matcher = CLASS_PATTERN.matcher(code);
		String cls;
		if (matcher.find())
			cls = matcher.group(1);
		else
			throw new IllegalArgumentException((new StringBuilder()).append("No such class name in ").append(code).toString());
		className = pkg == null || pkg.length() <= 0 ? cls : (new StringBuilder()).append(pkg).append(".").append(cls).toString();
		return Class.forName(className, true, ClassHelper.getCallerClassLoader(getClass()));
		ClassNotFoundException e;
		e;
		if (!code.endsWith("}"))
			throw new IllegalStateException((new StringBuilder()).append("The java code not endsWith \"}\", code: \n").append(code).append("\n").toString());
		return doCompile(className, code);
		RuntimeException t;
		t;
		throw t;
		t;
		throw new IllegalStateException((new StringBuilder()).append("Failed to compile class, cause: ").append(t.getMessage()).append(", class: ").append(className).append(", code: \n").append(code).append("\n, stack: ").append(ClassUtils.toString(t)).toString());
	}

	protected abstract Class doCompile(String s, String s1)
		throws Throwable;

}
