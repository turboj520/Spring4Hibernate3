// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JavaBeanAccessor.java

package com.autohome.turbo.common.beanutil;


public final class JavaBeanAccessor extends Enum
{

	public static final JavaBeanAccessor FIELD;
	public static final JavaBeanAccessor METHOD;
	public static final JavaBeanAccessor ALL;
	private static final JavaBeanAccessor $VALUES[];

	public static JavaBeanAccessor[] values()
	{
		return (JavaBeanAccessor[])$VALUES.clone();
	}

	public static JavaBeanAccessor valueOf(String name)
	{
		return (JavaBeanAccessor)Enum.valueOf(com/autohome/turbo/common/beanutil/JavaBeanAccessor, name);
	}

	private JavaBeanAccessor(String s, int i)
	{
		super(s, i);
	}

	public static boolean isAccessByMethod(JavaBeanAccessor accessor)
	{
		return METHOD.equals(accessor) || ALL.equals(accessor);
	}

	public static boolean isAccessByField(JavaBeanAccessor accessor)
	{
		return FIELD.equals(accessor) || ALL.equals(accessor);
	}

	static 
	{
		FIELD = new JavaBeanAccessor("FIELD", 0);
		METHOD = new JavaBeanAccessor("METHOD", 1);
		ALL = new JavaBeanAccessor("ALL", 2);
		$VALUES = (new JavaBeanAccessor[] {
			FIELD, METHOD, ALL
		});
	}
}
