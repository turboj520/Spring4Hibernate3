// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Activate.java

package com.autohome.turbo.common.extension;

import java.lang.annotation.Annotation;

public interface Activate
	extends Annotation
{

	public abstract String[] group();

	public abstract String[] value();

	public abstract String[] before();

	public abstract String[] after();

	public abstract int order();
}
