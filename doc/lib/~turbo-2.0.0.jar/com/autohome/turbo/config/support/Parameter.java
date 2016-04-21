// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Parameter.java

package com.autohome.turbo.config.support;

import java.lang.annotation.Annotation;

public interface Parameter
	extends Annotation
{

	public abstract String key();

	public abstract boolean required();

	public abstract boolean excluded();

	public abstract boolean escaped();

	public abstract boolean attribute();

	public abstract boolean append();
}
