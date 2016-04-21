// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractZookeeperClient.java

package com.autohome.turbo.remoting.zookeeper.support;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.remoting.zookeeper.*;
import java.util.*;
import java.util.concurrent.*;

public abstract class AbstractZookeeperClient
	implements ZookeeperClient
{

	protected static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/remoting/zookeeper/support/AbstractZookeeperClient);
	private final URL url;
	private final Set stateListeners = new CopyOnWriteArraySet();
	private final ConcurrentMap childListeners = new ConcurrentHashMap();
	private volatile boolean closed;

	public AbstractZookeeperClient(URL url)
	{
		closed = false;
		this.url = url;
	}

	public URL getUrl()
	{
		return url;
	}

	public void create(String path, boolean ephemeral)
	{
		int i = path.lastIndexOf('/');
		if (i > 0)
			create(path.substring(0, i), false);
		if (ephemeral)
			createEphemeral(path);
		else
			createPersistent(path);
	}

	public void addStateListener(StateListener listener)
	{
		stateListeners.add(listener);
	}

	public void removeStateListener(StateListener listener)
	{
		stateListeners.remove(listener);
	}

	public Set getSessionListeners()
	{
		return stateListeners;
	}

	public List addChildListener(String path, ChildListener listener)
	{
		ConcurrentMap listeners = (ConcurrentMap)childListeners.get(path);
		if (listeners == null)
		{
			childListeners.putIfAbsent(path, new ConcurrentHashMap());
			listeners = (ConcurrentMap)childListeners.get(path);
		}
		Object targetListener = listeners.get(listener);
		if (targetListener == null)
		{
			listeners.putIfAbsent(listener, createTargetChildListener(path, listener));
			targetListener = listeners.get(listener);
		}
		return addTargetChildListener(path, targetListener);
	}

	public void removeChildListener(String path, ChildListener listener)
	{
		ConcurrentMap listeners = (ConcurrentMap)childListeners.get(path);
		if (listeners != null)
		{
			Object targetListener = listeners.remove(listener);
			if (targetListener != null)
				removeTargetChildListener(path, targetListener);
		}
	}

	protected void stateChanged(int state)
	{
		StateListener sessionListener;
		for (Iterator i$ = getSessionListeners().iterator(); i$.hasNext(); sessionListener.stateChanged(state))
			sessionListener = (StateListener)i$.next();

	}

	public void close()
	{
		if (closed)
			return;
		closed = true;
		try
		{
			doClose();
		}
		catch (Throwable t)
		{
			logger.warn(t.getMessage(), t);
		}
	}

	protected abstract void doClose();

	protected abstract void createPersistent(String s);

	protected abstract void createEphemeral(String s);

	protected abstract Object createTargetChildListener(String s, ChildListener childlistener);

	protected abstract List addTargetChildListener(String s, Object obj);

	protected abstract void removeTargetChildListener(String s, Object obj);

}
