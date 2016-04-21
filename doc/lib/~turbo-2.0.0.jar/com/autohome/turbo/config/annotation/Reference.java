// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Reference.java

package com.autohome.turbo.config.annotation;

import java.lang.annotation.Annotation;

public interface Reference
	extends Annotation
{

	public abstract Class interfaceClass();

	public abstract String interfaceName();

	public abstract String version();

	public abstract String group();

	public abstract String url();

	public abstract String client();

	public abstract boolean generic();

	public abstract boolean injvm();

	public abstract boolean check();

	public abstract boolean init();

	public abstract boolean lazy();

	public abstract boolean stubevent();

	public abstract String reconnect();

	public abstract boolean sticky();

	public abstract String proxy();

	public abstract String stub();

	public abstract String cluster();

	public abstract int connections();

	public abstract int callbacks();

	public abstract String onconnect();

	public abstract String ondisconnect();

	public abstract String owner();

	public abstract String layer();

	public abstract int retries();

	public abstract String loadbalance();

	public abstract boolean async();

	public abstract int actives();

	public abstract boolean sent();

	public abstract String mock();

	public abstract String validation();

	public abstract int timeout();

	public abstract String cache();

	public abstract String[] filter();

	public abstract String[] listener();

	public abstract String[] parameters();

	public abstract String application();

	public abstract String module();

	public abstract String consumer();

	public abstract String monitor();

	public abstract String protocol();

	public abstract String[] registry();
}
