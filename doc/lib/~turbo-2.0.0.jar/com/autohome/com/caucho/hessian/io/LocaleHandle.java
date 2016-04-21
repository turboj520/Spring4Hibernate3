// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   LocaleHandle.java

package com.autohome.com.caucho.hessian.io;

import java.io.Serializable;
import java.util.Locale;

// Referenced classes of package com.autohome.com.caucho.hessian.io:
//			HessianHandle

public class LocaleHandle
	implements Serializable, HessianHandle
{

	private String value;

	public LocaleHandle(String locale)
	{
		value = locale;
	}

	private Object readResolve()
	{
		String s = value;
		if (s == null)
			return null;
		int len = s.length();
		char ch = ' ';
		int i;
		for (i = 0; i < len && ('a' <= (ch = s.charAt(i)) && ch <= 'z' || 'A' <= ch && ch <= 'Z' || '0' <= ch && ch <= '9'); i++);
		String language = s.substring(0, i);
		String country = null;
		String var = null;
		if (ch == '-' || ch == '_')
		{
			int head = ++i;
			for (; i < len && ('a' <= (ch = s.charAt(i)) && ch <= 'z' || 'A' <= ch && ch <= 'Z' || '0' <= ch && ch <= '9'); i++);
			country = s.substring(head, i);
		}
		if (ch == '-' || ch == '_')
		{
			int head = ++i;
			for (; i < len && ('a' <= (ch = s.charAt(i)) && ch <= 'z' || 'A' <= ch && ch <= 'Z' || '0' <= ch && ch <= '9'); i++);
			var = s.substring(head, i);
		}
		if (var != null)
			return new Locale(language, country, var);
		if (country != null)
			return new Locale(language, country);
		else
			return new Locale(language);
	}
}
