// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Menu.java

package com.autohome.turbo.container.page;

import java.lang.annotation.Annotation;

public interface Menu
	extends Annotation
{

	public abstract String name();

	public abstract String desc();

	public abstract int order();
}
