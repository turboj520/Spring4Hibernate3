// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Validator.java

package com.autohome.turbo.validation;


public interface Validator
{

	public abstract void validate(String s, Class aclass[], Object aobj[])
		throws Exception;
}
