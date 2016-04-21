// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ZookeeperClient.java

package com.autohome.turbo.remoting.zookeeper;

import com.autohome.turbo.common.URL;
import java.util.List;

// Referenced classes of package com.autohome.turbo.remoting.zookeeper:
//			ChildListener, StateListener

public interface ZookeeperClient
{

	public abstract void create(String s, boolean flag);

	public abstract void delete(String s);

	public abstract List getChildren(String s);

	public abstract List addChildListener(String s, ChildListener childlistener);

	public abstract void removeChildListener(String s, ChildListener childlistener);

	public abstract void addStateListener(StateListener statelistener);

	public abstract void removeStateListener(StateListener statelistener);

	public abstract boolean isConnected();

	public abstract void close();

	public abstract URL getUrl();
}
