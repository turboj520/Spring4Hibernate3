// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Test.java

package com.autohome.hessian.test;

import java.io.IOException;

public interface Test
{

	public abstract void nullCall();

	public abstract String hello();

	public abstract int subtract(int i, int j);

	public abstract Object echo(Object obj);

	public abstract void fault()
		throws IOException;
}
