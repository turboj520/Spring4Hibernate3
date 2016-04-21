// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JavassistCompiler.java

package com.autohome.turbo.common.compiler.support;

import com.autohome.turbo.common.utils.ClassHelper;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javassist.*;

// Referenced classes of package com.autohome.turbo.common.compiler.support:
//			AbstractCompiler, ClassUtils

public class JavassistCompiler extends AbstractCompiler
{

	private static final Pattern IMPORT_PATTERN = Pattern.compile("import\\s+([\\w\\.\\*]+);\n");
	private static final Pattern EXTENDS_PATTERN = Pattern.compile("\\s+extends\\s+([\\w\\.]+)[^\\{]*\\{\n");
	private static final Pattern IMPLEMENTS_PATTERN = Pattern.compile("\\s+implements\\s+([\\w\\.]+)\\s*\\{\n");
	private static final Pattern METHODS_PATTERN = Pattern.compile("\n(private|public|protected)\\s+");
	private static final Pattern FIELD_PATTERN = Pattern.compile("[^\n]+=[^\n]+;");

	public JavassistCompiler()
	{
	}

	public Class doCompile(String name, String source)
		throws Throwable
	{
		int i = name.lastIndexOf('.');
		String className = i >= 0 ? name.substring(i + 1) : name;
		ClassPool pool = new ClassPool(true);
		pool.appendClassPath(new LoaderClassPath(ClassHelper.getCallerClassLoader(getClass())));
		Matcher matcher = IMPORT_PATTERN.matcher(source);
		List importPackages = new ArrayList();
		Map fullNames = new HashMap();
		do
		{
			if (!matcher.find())
				break;
			String pkg = matcher.group(1);
			if (pkg.endsWith(".*"))
			{
				String pkgName = pkg.substring(0, pkg.length() - 2);
				pool.importPackage(pkgName);
				importPackages.add(pkgName);
			} else
			{
				int pi = pkg.lastIndexOf('.');
				if (pi > 0)
				{
					String pkgName = pkg.substring(0, pi);
					pool.importPackage(pkgName);
					importPackages.add(pkgName);
					fullNames.put(pkg.substring(pi + 1), pkg);
				}
			}
		} while (true);
		String packages[] = (String[])importPackages.toArray(new String[0]);
		matcher = EXTENDS_PATTERN.matcher(source);
		CtClass cls;
		if (matcher.find())
		{
			String extend = matcher.group(1).trim();
			String extendClass;
			if (extend.contains("."))
				extendClass = extend;
			else
			if (fullNames.containsKey(extend))
				extendClass = (String)fullNames.get(extend);
			else
				extendClass = ClassUtils.forName(packages, extend).getName();
			cls = pool.makeClass(name, pool.get(extendClass));
		} else
		{
			cls = pool.makeClass(name);
		}
		matcher = IMPLEMENTS_PATTERN.matcher(source);
		if (matcher.find())
		{
			String ifaces[] = matcher.group(1).trim().split("\\,");
			String arr$[] = ifaces;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				String iface = arr$[i$];
				iface = iface.trim();
				String ifaceClass;
				if (iface.contains("."))
					ifaceClass = iface;
				else
				if (fullNames.containsKey(iface))
					ifaceClass = (String)fullNames.get(iface);
				else
					ifaceClass = ClassUtils.forName(packages, iface).getName();
				cls.addInterface(pool.get(ifaceClass));
			}

		}
		String body = source.substring(source.indexOf("{") + 1, source.length() - 1);
		String methods[] = METHODS_PATTERN.split(body);
		String arr$[] = methods;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			String method = arr$[i$];
			method = method.trim();
			if (method.length() <= 0)
				continue;
			if (method.startsWith(className))
			{
				cls.addConstructor(CtNewConstructor.make((new StringBuilder()).append("public ").append(method).toString(), cls));
				continue;
			}
			if (FIELD_PATTERN.matcher(method).matches())
				cls.addField(CtField.make((new StringBuilder()).append("private ").append(method).toString(), cls));
			else
				cls.addMethod(CtNewMethod.make((new StringBuilder()).append("public ").append(method).toString(), cls));
		}

		return cls.toClass(ClassHelper.getCallerClassLoader(getClass()), com/autohome/turbo/common/compiler/support/JavassistCompiler.getProtectionDomain());
	}

}
