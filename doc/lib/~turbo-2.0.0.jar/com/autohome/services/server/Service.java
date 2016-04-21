// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Service.java

package com.autohome.services.server;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

public interface Service
{

	public abstract void init(ServletConfig servletconfig)
		throws ServletException;

	public abstract void destroy();
}
