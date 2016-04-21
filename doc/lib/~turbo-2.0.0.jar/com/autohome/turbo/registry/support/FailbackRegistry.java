// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   FailbackRegistry.java

package com.autohome.turbo.registry.support;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.utils.ConcurrentHashSet;
import com.autohome.turbo.common.utils.NamedThreadFactory;
import com.autohome.turbo.registry.NotifyListener;
import java.util.*;
import java.util.concurrent.*;

// Referenced classes of package com.autohome.turbo.registry.support:
//			AbstractRegistry, SkipFailbackWrapperException

public abstract class FailbackRegistry extends AbstractRegistry
{

	private final ScheduledExecutorService retryExecutor = Executors.newScheduledThreadPool(1, new NamedThreadFactory("DubboRegistryFailedRetryTimer", true));
	private final ScheduledFuture retryFuture;
	private final Set failedRegistered = new ConcurrentHashSet();
	private final Set failedUnregistered = new ConcurrentHashSet();
	private final ConcurrentMap failedSubscribed = new ConcurrentHashMap();
	private final ConcurrentMap failedUnsubscribed = new ConcurrentHashMap();
	private final ConcurrentMap failedNotified = new ConcurrentHashMap();

	public FailbackRegistry(URL url)
	{
		super(url);
		int retryPeriod = url.getParameter("retry.period", 30000);
		retryFuture = retryExecutor.scheduleWithFixedDelay(new Runnable() {

			final FailbackRegistry this$0;

			public void run()
			{
				try
				{
					retry();
				}
				catch (Throwable t)
				{
					logger.error((new StringBuilder()).append("Unexpected error occur at failed retry, cause: ").append(t.getMessage()).toString(), t);
				}
			}

			
			{
				this$0 = FailbackRegistry.this;
				super();
			}
		}, retryPeriod, retryPeriod, TimeUnit.MILLISECONDS);
	}

	public Future getRetryFuture()
	{
		return retryFuture;
	}

	public Set getFailedRegistered()
	{
		return failedRegistered;
	}

	public Set getFailedUnregistered()
	{
		return failedUnregistered;
	}

	public Map getFailedSubscribed()
	{
		return failedSubscribed;
	}

	public Map getFailedUnsubscribed()
	{
		return failedUnsubscribed;
	}

	public Map getFailedNotified()
	{
		return failedNotified;
	}

	private void addFailedSubscribed(URL url, NotifyListener listener)
	{
		Set listeners = (Set)failedSubscribed.get(url);
		if (listeners == null)
		{
			failedSubscribed.putIfAbsent(url, new ConcurrentHashSet());
			listeners = (Set)failedSubscribed.get(url);
		}
		listeners.add(listener);
	}

	private void removeFailedSubscribed(URL url, NotifyListener listener)
	{
		Set listeners = (Set)failedSubscribed.get(url);
		if (listeners != null)
			listeners.remove(listener);
		listeners = (Set)failedUnsubscribed.get(url);
		if (listeners != null)
			listeners.remove(listener);
		Map notified = (Map)failedNotified.get(url);
		if (notified != null)
			notified.remove(listener);
	}

	public void register(URL url)
	{
		super.register(url);
		failedRegistered.remove(url);
		failedUnregistered.remove(url);
		try
		{
			doRegister(url);
		}
		catch (Exception e)
		{
			Throwable t = e;
			boolean check = getUrl().getParameter("check", true) && url.getParameter("check", true) && !"consumer".equals(url.getProtocol());
			boolean skipFailback = t instanceof SkipFailbackWrapperException;
			if (check || skipFailback)
			{
				if (skipFailback)
					t = t.getCause();
				throw new IllegalStateException((new StringBuilder()).append("Failed to register ").append(url).append(" to registry ").append(getUrl().getAddress()).append(", cause: ").append(t.getMessage()).toString(), t);
			}
			logger.error((new StringBuilder()).append("Failed to register ").append(url).append(", waiting for retry, cause: ").append(t.getMessage()).toString(), t);
			failedRegistered.add(url);
		}
	}

	public void unregister(URL url)
	{
		super.unregister(url);
		failedRegistered.remove(url);
		failedUnregistered.remove(url);
		try
		{
			doUnregister(url);
		}
		catch (Exception e)
		{
			Throwable t = e;
			boolean check = getUrl().getParameter("check", true) && url.getParameter("check", true) && !"consumer".equals(url.getProtocol());
			boolean skipFailback = t instanceof SkipFailbackWrapperException;
			if (check || skipFailback)
			{
				if (skipFailback)
					t = t.getCause();
				throw new IllegalStateException((new StringBuilder()).append("Failed to unregister ").append(url).append(" to registry ").append(getUrl().getAddress()).append(", cause: ").append(t.getMessage()).toString(), t);
			}
			logger.error((new StringBuilder()).append("Failed to uregister ").append(url).append(", waiting for retry, cause: ").append(t.getMessage()).toString(), t);
			failedUnregistered.add(url);
		}
	}

	public void subscribe(URL url, NotifyListener listener)
	{
		super.subscribe(url, listener);
		removeFailedSubscribed(url, listener);
		try
		{
			doSubscribe(url, listener);
		}
		catch (Exception e)
		{
			Throwable t = e;
			List urls = getCacheUrls(url);
			if (urls != null && urls.size() > 0)
			{
				notify(url, listener, urls);
				logger.error((new StringBuilder()).append("Failed to subscribe ").append(url).append(", Using cached list: ").append(urls).append(" from cache file: ").append(getUrl().getParameter("file", (new StringBuilder()).append(System.getProperty("user.home")).append("/dubbo-registry-").append(url.getHost()).append(".cache").toString())).append(", cause: ").append(t.getMessage()).toString(), t);
			} else
			{
				boolean check = getUrl().getParameter("check", true) && url.getParameter("check", true);
				boolean skipFailback = t instanceof SkipFailbackWrapperException;
				if (check || skipFailback)
				{
					if (skipFailback)
						t = t.getCause();
					throw new IllegalStateException((new StringBuilder()).append("Failed to subscribe ").append(url).append(", cause: ").append(t.getMessage()).toString(), t);
				}
				logger.error((new StringBuilder()).append("Failed to subscribe ").append(url).append(", waiting for retry, cause: ").append(t.getMessage()).toString(), t);
			}
			addFailedSubscribed(url, listener);
		}
	}

	public void unsubscribe(URL url, NotifyListener listener)
	{
		super.unsubscribe(url, listener);
		removeFailedSubscribed(url, listener);
		try
		{
			doUnsubscribe(url, listener);
		}
		catch (Exception e)
		{
			Throwable t = e;
			boolean check = getUrl().getParameter("check", true) && url.getParameter("check", true);
			boolean skipFailback = t instanceof SkipFailbackWrapperException;
			if (check || skipFailback)
			{
				if (skipFailback)
					t = t.getCause();
				throw new IllegalStateException((new StringBuilder()).append("Failed to unsubscribe ").append(url).append(" to registry ").append(getUrl().getAddress()).append(", cause: ").append(t.getMessage()).toString(), t);
			}
			logger.error((new StringBuilder()).append("Failed to unsubscribe ").append(url).append(", waiting for retry, cause: ").append(t.getMessage()).toString(), t);
			Set listeners = (Set)failedUnsubscribed.get(url);
			if (listeners == null)
			{
				failedUnsubscribed.putIfAbsent(url, new ConcurrentHashSet());
				listeners = (Set)failedUnsubscribed.get(url);
			}
			listeners.add(listener);
		}
	}

	protected void notify(URL url, NotifyListener listener, List urls)
	{
		if (url == null)
			throw new IllegalArgumentException("notify url == null");
		if (listener == null)
			throw new IllegalArgumentException("notify listener == null");
		try
		{
			doNotify(url, listener, urls);
		}
		catch (Exception t)
		{
			Map listeners = (Map)failedNotified.get(url);
			if (listeners == null)
			{
				failedNotified.putIfAbsent(url, new ConcurrentHashMap());
				listeners = (Map)failedNotified.get(url);
			}
			listeners.put(listener, urls);
			logger.error((new StringBuilder()).append("Failed to notify for subscribe ").append(url).append(", waiting for retry, cause: ").append(t.getMessage()).toString(), t);
		}
	}

	protected void doNotify(URL url, NotifyListener listener, List urls)
	{
		super.notify(url, listener, urls);
	}

	protected void recover()
		throws Exception
	{
		Set recoverRegistered = new HashSet(getRegistered());
		if (!recoverRegistered.isEmpty())
		{
			if (logger.isInfoEnabled())
				logger.info((new StringBuilder()).append("Recover register url ").append(recoverRegistered).toString());
			URL url;
			for (Iterator i$ = recoverRegistered.iterator(); i$.hasNext(); failedRegistered.add(url))
				url = (URL)i$.next();

		}
		Map recoverSubscribed = new HashMap(getSubscribed());
		if (!recoverSubscribed.isEmpty())
		{
			if (logger.isInfoEnabled())
				logger.info((new StringBuilder()).append("Recover subscribe url ").append(recoverSubscribed.keySet()).toString());
			for (Iterator i$ = recoverSubscribed.entrySet().iterator(); i$.hasNext();)
			{
				java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
				URL url = (URL)entry.getKey();
				Iterator i$ = ((Set)entry.getValue()).iterator();
				while (i$.hasNext()) 
				{
					NotifyListener listener = (NotifyListener)i$.next();
					addFailedSubscribed(url, listener);
				}
			}

		}
	}

	protected void retry()
	{
		if (!failedRegistered.isEmpty())
		{
			Set failed = new HashSet(failedRegistered);
			if (failed.size() > 0)
			{
				if (logger.isInfoEnabled())
					logger.info((new StringBuilder()).append("Retry register ").append(failed).toString());
				try
				{
					for (Iterator i$ = failed.iterator(); i$.hasNext();)
					{
						URL url = (URL)i$.next();
						try
						{
							doRegister(url);
							failedRegistered.remove(url);
						}
						catch (Throwable t)
						{
							logger.warn((new StringBuilder()).append("Failed to retry register ").append(failed).append(", waiting for again, cause: ").append(t.getMessage()).toString(), t);
						}
					}

				}
				catch (Throwable t)
				{
					logger.warn((new StringBuilder()).append("Failed to retry register ").append(failed).append(", waiting for again, cause: ").append(t.getMessage()).toString(), t);
				}
			}
		}
		if (!failedUnregistered.isEmpty())
		{
			Set failed = new HashSet(failedUnregistered);
			if (failed.size() > 0)
			{
				if (logger.isInfoEnabled())
					logger.info((new StringBuilder()).append("Retry unregister ").append(failed).toString());
				try
				{
					for (Iterator i$ = failed.iterator(); i$.hasNext();)
					{
						URL url = (URL)i$.next();
						try
						{
							doUnregister(url);
							failedUnregistered.remove(url);
						}
						catch (Throwable t)
						{
							logger.warn((new StringBuilder()).append("Failed to retry unregister  ").append(failed).append(", waiting for again, cause: ").append(t.getMessage()).toString(), t);
						}
					}

				}
				catch (Throwable t)
				{
					logger.warn((new StringBuilder()).append("Failed to retry unregister  ").append(failed).append(", waiting for again, cause: ").append(t.getMessage()).toString(), t);
				}
			}
		}
		if (!failedSubscribed.isEmpty())
		{
			Map failed = new HashMap(failedSubscribed);
			Iterator i$ = (new HashMap(failed)).entrySet().iterator();
			do
			{
				if (!i$.hasNext())
					break;
				java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
				if (entry.getValue() == null || ((Set)entry.getValue()).size() == 0)
					failed.remove(entry.getKey());
			} while (true);
			if (failed.size() > 0)
			{
				if (logger.isInfoEnabled())
					logger.info((new StringBuilder()).append("Retry subscribe ").append(failed).toString());
				try
				{
					for (i$ = failed.entrySet().iterator(); i$.hasNext();)
					{
						java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
						URL url = (URL)entry.getKey();
						Set listeners = (Set)entry.getValue();
						Iterator i$ = listeners.iterator();
						while (i$.hasNext()) 
						{
							NotifyListener listener = (NotifyListener)i$.next();
							try
							{
								doSubscribe(url, listener);
								listeners.remove(listener);
							}
							catch (Throwable t)
							{
								logger.warn((new StringBuilder()).append("Failed to retry subscribe ").append(failed).append(", waiting for again, cause: ").append(t.getMessage()).toString(), t);
							}
						}
					}

				}
				catch (Throwable t)
				{
					logger.warn((new StringBuilder()).append("Failed to retry subscribe ").append(failed).append(", waiting for again, cause: ").append(t.getMessage()).toString(), t);
				}
			}
		}
		if (!failedUnsubscribed.isEmpty())
		{
			Map failed = new HashMap(failedUnsubscribed);
			Iterator i$ = (new HashMap(failed)).entrySet().iterator();
			do
			{
				if (!i$.hasNext())
					break;
				java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
				if (entry.getValue() == null || ((Set)entry.getValue()).size() == 0)
					failed.remove(entry.getKey());
			} while (true);
			if (failed.size() > 0)
			{
				if (logger.isInfoEnabled())
					logger.info((new StringBuilder()).append("Retry unsubscribe ").append(failed).toString());
				try
				{
					for (i$ = failed.entrySet().iterator(); i$.hasNext();)
					{
						java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
						URL url = (URL)entry.getKey();
						Set listeners = (Set)entry.getValue();
						Iterator i$ = listeners.iterator();
						while (i$.hasNext()) 
						{
							NotifyListener listener = (NotifyListener)i$.next();
							try
							{
								doUnsubscribe(url, listener);
								listeners.remove(listener);
							}
							catch (Throwable t)
							{
								logger.warn((new StringBuilder()).append("Failed to retry unsubscribe ").append(failed).append(", waiting for again, cause: ").append(t.getMessage()).toString(), t);
							}
						}
					}

				}
				catch (Throwable t)
				{
					logger.warn((new StringBuilder()).append("Failed to retry unsubscribe ").append(failed).append(", waiting for again, cause: ").append(t.getMessage()).toString(), t);
				}
			}
		}
		if (!failedNotified.isEmpty())
		{
			Map failed = new HashMap(failedNotified);
			Iterator i$ = (new HashMap(failed)).entrySet().iterator();
			do
			{
				if (!i$.hasNext())
					break;
				java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
				if (entry.getValue() == null || ((Map)entry.getValue()).size() == 0)
					failed.remove(entry.getKey());
			} while (true);
			if (failed.size() > 0)
			{
				if (logger.isInfoEnabled())
					logger.info((new StringBuilder()).append("Retry notify ").append(failed).toString());
				try
				{
					for (i$ = failed.values().iterator(); i$.hasNext();)
					{
						Map values = (Map)i$.next();
						Iterator i$ = values.entrySet().iterator();
						while (i$.hasNext()) 
						{
							java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
							try
							{
								NotifyListener listener = (NotifyListener)entry.getKey();
								List urls = (List)entry.getValue();
								listener.notify(urls);
								values.remove(listener);
							}
							catch (Throwable t)
							{
								logger.warn((new StringBuilder()).append("Failed to retry notify ").append(failed).append(", waiting for again, cause: ").append(t.getMessage()).toString(), t);
							}
						}
					}

				}
				catch (Throwable t)
				{
					logger.warn((new StringBuilder()).append("Failed to retry notify ").append(failed).append(", waiting for again, cause: ").append(t.getMessage()).toString(), t);
				}
			}
		}
	}

	public void destroy()
	{
		super.destroy();
		try
		{
			retryFuture.cancel(true);
		}
		catch (Throwable t)
		{
			logger.warn(t.getMessage(), t);
		}
	}

	protected abstract void doRegister(URL url);

	protected abstract void doUnregister(URL url);

	protected abstract void doSubscribe(URL url, NotifyListener notifylistener);

	protected abstract void doUnsubscribe(URL url, NotifyListener notifylistener);
}
