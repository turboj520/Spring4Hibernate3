// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   CuratorZookeeperClient.java

package com.autohome.turbo.remoting.zookeeper.curator;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.remoting.zookeeper.ChildListener;
import com.autohome.turbo.remoting.zookeeper.support.AbstractZookeeperClient;
import java.util.List;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLBackgroundPathAndBytesable;
import org.apache.curator.framework.api.BackgroundPathable;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.api.DeleteBuilder;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.framework.listen.Listenable;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;

public class CuratorZookeeperClient extends AbstractZookeeperClient
{
	private class CuratorWatcherImpl
		implements CuratorWatcher
	{

		private volatile ChildListener listener;
		final CuratorZookeeperClient this$0;

		public void unwatch()
		{
			listener = null;
		}

		public void process(WatchedEvent event)
			throws Exception
		{
			if (listener != null)
				listener.childChanged(event.getPath(), (List)((BackgroundPathable)client.getChildren().usingWatcher(this)).forPath(event.getPath()));
		}

		public CuratorWatcherImpl(ChildListener listener)
		{
			this$0 = CuratorZookeeperClient.this;
			super();
			this.listener = listener;
		}
	}


	private final CuratorFramework client;

	public CuratorZookeeperClient(URL url)
	{
		super(url);
		org.apache.curator.framework.CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder().connectString(url.getBackupAddress()).retryPolicy(new RetryNTimes(0x7fffffff, 1000)).connectionTimeoutMs(url.getParameter("timeout", 5000)).sessionTimeoutMs(url.getParameter("session", 60000));
		String authority = url.getAuthority();
		if (authority != null && authority.length() > 0)
			builder = builder.authorization("digest", authority.getBytes());
		client = builder.build();
		client.getConnectionStateListenable().addListener(new ConnectionStateListener() {

			final CuratorZookeeperClient this$0;

			public void stateChanged(CuratorFramework client, ConnectionState state)
			{
				if (state == ConnectionState.LOST)
					CuratorZookeeperClient.this.stateChanged(0);
				else
				if (state == ConnectionState.CONNECTED)
					CuratorZookeeperClient.this.stateChanged(1);
				else
				if (state == ConnectionState.RECONNECTED)
					CuratorZookeeperClient.this.stateChanged(2);
			}

			
			{
				this$0 = CuratorZookeeperClient.this;
				super();
			}
		});
		client.start();
	}

	public void createPersistent(String path)
	{
		try
		{
			client.create().forPath(path);
		}
		catch (org.apache.zookeeper.KeeperException.NodeExistsException e) { }
		catch (Exception e)
		{
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public void createEphemeral(String path)
	{
		try
		{
			((ACLBackgroundPathAndBytesable)client.create().withMode(CreateMode.EPHEMERAL)).forPath(path);
		}
		catch (org.apache.zookeeper.KeeperException.NodeExistsException e) { }
		catch (Exception e)
		{
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public void delete(String path)
	{
		try
		{
			client.delete().forPath(path);
		}
		catch (org.apache.zookeeper.KeeperException.NoNodeException e) { }
		catch (Exception e)
		{
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public List getChildren(String path)
	{
		return (List)client.getChildren().forPath(path);
		org.apache.zookeeper.KeeperException.NoNodeException e;
		e;
		return null;
		e;
		throw new IllegalStateException(e.getMessage(), e);
	}

	public boolean isConnected()
	{
		return client.getZookeeperClient().isConnected();
	}

	public void doClose()
	{
		client.close();
	}

	public CuratorWatcher createTargetChildListener(String path, ChildListener listener)
	{
		return new CuratorWatcherImpl(listener);
	}

	public List addTargetChildListener(String path, CuratorWatcher listener)
	{
		return (List)((BackgroundPathable)client.getChildren().usingWatcher(listener)).forPath(path);
		org.apache.zookeeper.KeeperException.NoNodeException e;
		e;
		return null;
		e;
		throw new IllegalStateException(e.getMessage(), e);
	}

	public void removeTargetChildListener(String path, CuratorWatcher listener)
	{
		((CuratorWatcherImpl)listener).unwatch();
	}

	public volatile void removeTargetChildListener(String x0, Object x1)
	{
		removeTargetChildListener(x0, (CuratorWatcher)x1);
	}

	public volatile List addTargetChildListener(String x0, Object x1)
	{
		return addTargetChildListener(x0, (CuratorWatcher)x1);
	}

	public volatile Object createTargetChildListener(String x0, ChildListener x1)
	{
		return createTargetChildListener(x0, x1);
	}




}
