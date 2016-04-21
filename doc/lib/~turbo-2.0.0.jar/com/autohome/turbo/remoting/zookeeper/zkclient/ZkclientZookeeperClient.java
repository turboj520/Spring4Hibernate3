// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ZkclientZookeeperClient.java

package com.autohome.turbo.remoting.zookeeper.zkclient;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.remoting.zookeeper.ChildListener;
import com.autohome.turbo.remoting.zookeeper.support.AbstractZookeeperClient;
import java.util.List;
import org.I0Itec.zkclient.*;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.zookeeper.Watcher;

public class ZkclientZookeeperClient extends AbstractZookeeperClient
{

	private final ZkClient client;
	private volatile org.apache.zookeeper.Watcher.Event.KeeperState state;

	public ZkclientZookeeperClient(URL url)
	{
		super(url);
		state = org.apache.zookeeper.Watcher.Event.KeeperState.SyncConnected;
		client = new ZkClient(url.getBackupAddress(), url.getParameter("session", 60000), url.getParameter("timeout", 5000));
		client.subscribeStateChanges(new IZkStateListener() {

			final ZkclientZookeeperClient this$0;

			public void handleStateChanged(org.apache.zookeeper.Watcher.Event.KeeperState state)
				throws Exception
			{
				ZkclientZookeeperClient.this.state = state;
				if (state == org.apache.zookeeper.Watcher.Event.KeeperState.Disconnected)
					stateChanged(0);
				else
				if (state == org.apache.zookeeper.Watcher.Event.KeeperState.SyncConnected)
					stateChanged(1);
			}

			public void handleNewSession()
				throws Exception
			{
				stateChanged(2);
			}

			
			{
				this$0 = ZkclientZookeeperClient.this;
				super();
			}
		});
	}

	public void createPersistent(String path)
	{
		try
		{
			client.createPersistent(path, true);
		}
		catch (ZkNodeExistsException e) { }
	}

	public void createEphemeral(String path)
	{
		try
		{
			client.createEphemeral(path);
		}
		catch (ZkNodeExistsException e) { }
	}

	public void delete(String path)
	{
		try
		{
			client.delete(path);
		}
		catch (ZkNoNodeException e) { }
	}

	public List getChildren(String path)
	{
		return client.getChildren(path);
		ZkNoNodeException e;
		e;
		return null;
	}

	public boolean isConnected()
	{
		return state == org.apache.zookeeper.Watcher.Event.KeeperState.SyncConnected;
	}

	public void doClose()
	{
		client.close();
	}

	public IZkChildListener createTargetChildListener(String path, final ChildListener listener)
	{
		return new IZkChildListener() {

			final ChildListener val$listener;
			final ZkclientZookeeperClient this$0;

			public void handleChildChange(String parentPath, List currentChilds)
				throws Exception
			{
				listener.childChanged(parentPath, currentChilds);
			}

			
			{
				this$0 = ZkclientZookeeperClient.this;
				listener = childlistener;
				super();
			}
		};
	}

	public List addTargetChildListener(String path, IZkChildListener listener)
	{
		return client.subscribeChildChanges(path, listener);
	}

	public void removeTargetChildListener(String path, IZkChildListener listener)
	{
		client.unsubscribeChildChanges(path, listener);
	}

	public volatile void removeTargetChildListener(String x0, Object x1)
	{
		removeTargetChildListener(x0, (IZkChildListener)x1);
	}

	public volatile List addTargetChildListener(String x0, Object x1)
	{
		return addTargetChildListener(x0, (IZkChildListener)x1);
	}

	public volatile Object createTargetChildListener(String x0, ChildListener x1)
	{
		return createTargetChildListener(x0, x1);
	}




}
