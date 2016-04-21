// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Help.java

package com.autohome.turbo.remoting.telnet.support;

import java.lang.annotation.Annotation;

public interface Help
	extends Annotation
{

	public abstract String parameter();

	public abstract String summary();

	public abstract String detail();
}
