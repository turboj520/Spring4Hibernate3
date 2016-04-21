// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractRegistry.java

package com.autohome.turbo.registry.support;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.*;
import com.autohome.turbo.registry.NotifyListener;
import com.autohome.turbo.registry.Registry;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractRegistry
	implements Registry
{
	private class SaveProperties
		implements Runnable
	{

		private long version;
		final AbstractRegistry this$0;

		public void run()
		{
			doSaveProperties(version);
		}

		private SaveProperties(long version)
		{
			this$0 = AbstractRegistry.this;
			super();
			this.version = version;
		}

		SaveProperties(long x1, 1 x2)
		{
			this(x1);
		}
	}


	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private static final char URL_SEPARATOR = 32;
	private static final String URL_SPLIT = "\\s+";
	private URL registryUrl;
	private File file;
	private final Properties properties = new Properties();
	private final ExecutorService registryCacheExecutor = Executors.newFixedThreadPool(1, new NamedThreadFactory("DubboSaveRegistryCache", true));
	private final boolean syncSaveFile;
	private final AtomicLong lastCacheChanged = new AtomicLong();
	private final Set registered = new ConcurrentHashSet();
	private final ConcurrentMap subscribed = new ConcurrentHashMap();
	private final ConcurrentMap notified = new ConcurrentHashMap();

	public AbstractRegistry(URL url)
	{
		setUrl(url);
		syncSaveFile = url.getParameter("save.file", false);
		String filename = url.getParameter("file", (new StringBuilder()).append(System.getProperty("user.home")).append("/.dubbo/dubbo-registry-").append(url.getHost()).append(".cache").toString());
		File file = null;
		if (ConfigUtils.isNotEmpty(filename))
		{
			file = new File(filename);
			if (!file.exists() && file.getParentFile() != null && !file.getParentFile().exists() && !file.getParentFile().mkdirs())
				throw new IllegalArgumentException((new StringBuilder()).append("Invalid registry store file ").append(file).append(", cause: Failed to create directory ").append(file.getParentFile()).append("!").toString());
		}
		this.file = file;
		loadProperties();
		notify(url.getBackupUrls());
	}

	protected void setUrl(URL url)
	{
		if (url == null)
		{
			throw new IllegalArgumentException("registry url == null");
		} else
		{
			registryUrl = url;
			return;
		}
	}

	public URL getUrl()
	{
		return registryUrl;
	}

	public Set getRegistered()
	{
		return registered;
	}

	public Map getSubscribed()
	{
		return subscribed;
	}

	public Map getNotified()
	{
		return notified;
	}

	public File getCacheFile()
	{
		return file;
	}

	public Properties getCacheProperties()
	{
		return properties;
	}

	public AtomicLong getLastCacheChanged()
	{
		return lastCacheChanged;
	}

	public void doSaveProperties(long version)
	{
		Properties newProperties;
		InputStream in;
		if (version < lastCacheChanged.get())
			return;
		if (file == null)
			return;
		newProperties = new Properties();
		in = null;
		if (file.exists())
		{
			in = new FileInputStream(file);
			newProperties.load(in);
		}
		Throwable e;
		if (in != null)
			try
			{
				in.close();
			}
			// Misplaced declaration of an exception variable
			catch (Throwable e)
			{
				logger.warn(e.getMessage(), e);
			}
		break MISSING_BLOCK_LABEL_201;
		e;
		logger.warn((new StringBuilder()).append("Failed to load registry store file, cause: ").append(e.getMessage()).toString(), e);
		if (in != null)
			try
			{
				in.close();
			}
			// Misplaced declaration of an exception variable
			catch (Throwable e)
			{
				logger.warn(e.getMessage(), e);
			}
		break MISSING_BLOCK_LABEL_201;
		Exception exception;
		exception;
		if (in != null)
			try
			{
				in.close();
			}
			catch (IOException e)
			{
				logger.warn(e.getMessage(), e);
			}
		throw exception;
		RandomAccessFile raf;
		newProperties.putAll(properties);
		File lockfile = new File((new StringBuilder()).append(file.getAbsolutePath()).append(".lock").toString());
		if (!lockfile.exists())
			lockfile.createNewFile();
		raf = new RandomAccessFile(lockfile, "rw");
		FileChannel channel = raf.getChannel();
		FileLock lock;
		lock = channel.tryLock();
		if (lock == null)
			throw new IOException((new StringBuilder()).append("Can not lock the registry cache file ").append(file.getAbsolutePath()).append(", ignore and retry later, maybe multi java process use the file, please config: dubbo.registry.file=xxx.properties").toString());
		FileOutputStream outputFile;
		if (!file.exists())
			file.createNewFile();
		outputFile = new FileOutputStream(file);
		newProperties.store(outputFile, "Dubbo Registry Cache");
		outputFile.close();
		break MISSING_BLOCK_LABEL_384;
		Exception exception1;
		exception1;
		outputFile.close();
		throw exception1;
		lock.release();
		break MISSING_BLOCK_LABEL_402;
		Exception exception2;
		exception2;
		lock.release();
		throw exception2;
		channel.close();
		break MISSING_BLOCK_LABEL_420;
		Exception exception3;
		exception3;
		channel.close();
		throw exception3;
		raf.close();
		break MISSING_BLOCK_LABEL_515;
		Exception exception4;
		exception4;
		raf.close();
		throw exception4;
		lockfile;
		if (version < lastCacheChanged.get())
			return;
		registryCacheExecutor.execute(new SaveProperties(lastCacheChanged.incrementAndGet()));
		logger.warn((new StringBuilder()).append("Failed to save registry store file, cause: ").append(lockfile.getMessage()).toString(), lockfile);
	}

	private void loadProperties()
	{
		InputStream in;
		if (file == null || !file.exists())
			break MISSING_BLOCK_LABEL_217;
		in = null;
		in = new FileInputStream(file);
		properties.load(in);
		if (logger.isInfoEnabled())
			logger.info((new StringBuilder()).append("Load registry store file ").append(file).append(", data: ").append(properties).toString());
		Throwable e;
		if (in != null)
			try
			{
				in.close();
			}
			// Misplaced declaration of an exception variable
			catch (Throwable e)
			{
				logger.warn(e.getMessage(), e);
			}
		break MISSING_BLOCK_LABEL_217;
		e;
		logger.warn((new StringBuilder()).append("Failed to load registry store file ").append(file).toString(), e);
		if (in != null)
			try
			{
				in.close();
			}
			// Misplaced declaration of an exception variable
			catch (Throwable e)
			{
				logger.warn(e.getMessage(), e);
			}
		break MISSING_BLOCK_LABEL_217;
		Exception exception;
		exception;
		if (in != null)
			try
			{
				in.close();
			}
			catch (IOException e)
			{
				logger.warn(e.getMessage(), e);
			}
		throw exception;
	}

	public List getCacheUrls(URL url)
	{
		for (Iterator i$ = properties.entrySet().iterator(); i$.hasNext();)
		{
			java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
			String key = (String)entry.getKey();
			String value = (String)entry.getValue();
			if (key != null && key.length() > 0 && key.equals(url.getServiceKey()) && (Character.isLetter(key.charAt(0)) || key.charAt(0) == '_') && value != null && value.length() > 0)
			{
				String arr[] = value.trim().split("\\s+");
				List urls = new ArrayList();
				String arr$[] = arr;
				int len$ = arr$.length;
				for (int i$ = 0; i$ < len$; i$++)
				{
					String u = arr$[i$];
					urls.add(URL.valueOf(u));
				}

				return urls;
			}
		}

		return null;
	}

	public List lookup(URL url)
	{
		List result = new ArrayList();
		Map notifiedUrls = (Map)getNotified().get(url);
		if (notifiedUrls != null && notifiedUrls.size() > 0)
		{
			for (Iterator i$ = notifiedUrls.values().iterator(); i$.hasNext();)
			{
				List urls = (List)i$.next();
				Iterator i$ = urls.iterator();
				while (i$.hasNext()) 
				{
					URL u = (URL)i$.next();
					if (!"empty".equals(u.getProtocol()))
						result.add(u);
				}
			}

		} else
		{
			final AtomicReference reference = new AtomicReference();
			NotifyListener listener = new NotifyListener() {

				final AtomicReference val$reference;
				final AbstractRegistry this$0;

				public void notify(List urls)
				{
					reference.set(urls);
				}

			
			{
				this$0 = AbstractRegistry.this;
				reference = atomicreference;
				super();
			}
			};
			subscribe(url, listener);
			List urls = (List)reference.get();
			if (urls != null && urls.size() > 0)
			{
				Iterator i$ = urls.iterator();
				do
				{
					if (!i$.hasNext())
						break;
					URL u = (URL)i$.next();
					if (!"empty".equals(u.getProtocol()))
						result.add(u);
				} while (true);
			}
		}
		return result;
	}

	public void register(URL url)
	{
		if (url == null)
			throw new IllegalArgumentException("register url == null");
		if (logger.isInfoEnabled())
			logger.info((new StringBuilder()).append("Register: ").append(url).toString());
		registered.add(url);
	}

	public void unregister(URL url)
	{
		if (url == null)
			throw new IllegalArgumentException("unregister url == null");
		if (logger.isInfoEnabled())
			logger.info((new StringBuilder()).append("Unregister: ").append(url).toString());
		registered.remove(url);
	}

	public void subscribe(URL url, NotifyListener listener)
	{
		if (url == null)
			throw new IllegalArgumentException("subscribe url == null");
		if (listener == null)
			throw new IllegalArgumentException("subscribe listener == null");
		if (logger.isInfoEnabled())
			logger.info((new StringBuilder()).append("Subscribe: ").append(url).toString());
		Set listeners = (Set)subscribed.get(url);
		if (listeners == null)
		{
			subscribed.putIfAbsent(url, new ConcurrentHashSet());
			listeners = (Set)subscribed.get(url);
		}
		listeners.add(listener);
	}

	public void unsubscribe(URL url, NotifyListener listener)
	{
		if (url == null)
			throw new IllegalArgumentException("unsubscribe url == null");
		if (listener == null)
			throw new IllegalArgumentException("unsubscribe listener == null");
		if (logger.isInfoEnabled())
			logger.info((new StringBuilder()).append("Unsubscribe: ").append(url).toString());
		Set listeners = (Set)subscribed.get(url);
		if (listeners != null)
			listeners.remove(listener);
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
			for (Iterator i$ = recoverRegistered.iterator(); i$.hasNext(); register(url))
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
					subscribe(url, listener);
				}
			}

		}
	}

	protected static List filterEmpty(URL url, List urls)
	{
		if (urls == null || urls.size() == 0)
		{
			List result = new ArrayList(1);
			result.add(url.setProtocol("empty"));
			return result;
		} else
		{
			return urls;
		}
	}

	protected void notify(List urls)
	{
		if (urls == null || urls.isEmpty())
			return;
		Iterator i$ = getSubscribed().entrySet().iterator();
		do
		{
			if (!i$.hasNext())
				break;
			java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
			URL url = (URL)entry.getKey();
			if (UrlUtils.isMatch(url, (URL)urls.get(0)))
			{
				Set listeners = (Set)entry.getValue();
				if (listeners != null)
				{
					Iterator i$ = listeners.iterator();
					while (i$.hasNext()) 
					{
						NotifyListener listener = (NotifyListener)i$.next();
						try
						{
							notify(url, listener, filterEmpty(url, urls));
						}
						catch (Throwable t)
						{
							logger.error((new StringBuilder()).append("Failed to notify registry event, urls: ").append(urls).append(", cause: ").append(t.getMessage()).toString(), t);
						}
					}
				}
			}
		} while (true);
	}

	protected void notify(URL url, NotifyListener listener, List urls)
	{
		if (url == null)
			throw new IllegalArgumentException("notify url == null");
		if (listener == null)
			throw new IllegalArgumentException("notify listener == null");
		if ((urls == null || urls.size() == 0) && !"*".equals(url.getServiceInterface()))
		{
			logger.warn((new StringBuilder()).append("Ignore empty notify urls for subscribe url ").append(url).toString());
			return;
		}
		if (logger.isInfoEnabled())
			logger.info((new StringBuilder()).append("Notify urls for subscribe url ").append(url).append(", urls: ").append(urls).toString());
		Map result = new HashMap();
		Iterator i$ = urls.iterator();
		do
		{
			if (!i$.hasNext())
				break;
			URL u = (URL)i$.next();
			if (UrlUtils.isMatch(url, u))
			{
				String category = u.getParameter("category", "providers");
				List categoryList = (List)result.get(category);
				if (categoryList == null)
				{
					categoryList = new ArrayList();
					result.put(category, categoryList);
				}
				categoryList.add(u);
			}
		} while (true);
		if (result.size() == 0)
			return;
		Map categoryNotified = (Map)notified.get(url);
		if (categoryNotified == null)
		{
			notified.putIfAbsent(url, new ConcurrentHashMap());
			categoryNotified = (Map)notified.get(url);
		}
		List categoryList;
		for (Iterator i$ = result.entrySet().iterator(); i$.hasNext(); listener.notify(categoryList))
		{
			java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
			String category = (String)entry.getKey();
			categoryList = (List)entry.getValue();
			categoryNotified.put(category, categoryList);
			saveProperties(url);
		}

	}

	private void saveProperties(URL url)
	{
		if (file == null)
			return;
		try
		{
			StringBuilder buf = new StringBuilder();
			Map categoryNotified = (Map)notified.get(url);
			if (categoryNotified != null)
			{
				for (Iterator i$ = categoryNotified.values().iterator(); i$.hasNext();)
				{
					List us = (List)i$.next();
					Iterator i$ = us.iterator();
					while (i$.hasNext()) 
					{
						URL u = (URL)i$.next();
						if (buf.length() > 0)
							buf.append(' ');
						buf.append(u.toFullString());
					}
				}

			}
			properties.setProperty(url.getServiceKey(), buf.toString());
			long version = lastCacheChanged.incrementAndGet();
			if (syncSaveFile)
				doSaveProperties(version);
			else
				registryCacheExecutor.execute(new SaveProperties(version));
		}
		catch (Throwable t)
		{
			logger.warn(t.getMessage(), t);
		}
	}

	public void destroy()
	{
		if (logger.isInfoEnabled())
			logger.info((new StringBuilder()).append("Destroy registry:").append(getUrl()).toString());
		Set destroyRegistered = new HashSet(getRegistered());
		if (!destroyRegistered.isEmpty())
		{
			Iterator i$ = (new HashSet(getRegistered())).iterator();
			do
			{
				if (!i$.hasNext())
					break;
				URL url = (URL)i$.next();
				if (url.getParameter("dynamic", true))
					try
					{
						unregister(url);
						if (logger.isInfoEnabled())
							logger.info((new StringBuilder()).append("Destroy unregister url ").append(url).toString());
					}
					catch (Throwable t)
					{
						logger.warn((new StringBuilder()).append("Failed to unregister url ").append(url).append(" to registry ").append(getUrl()).append(" on destroy, cause: ").append(t.getMessage()).toString(), t);
					}
			} while (true);
		}
		Map destroySubscribed = new HashMap(getSubscribed());
		if (!destroySubscribed.isEmpty())
		{
			for (Iterator i$ = destroySubscribed.entrySet().iterator(); i$.hasNext();)
			{
				java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
				URL url = (URL)entry.getKey();
				Iterator i$ = ((Set)entry.getValue()).iterator();
				while (i$.hasNext()) 
				{
					NotifyListener listener = (NotifyListener)i$.next();
					try
					{
						unsubscribe(url, listener);
						if (logger.isInfoEnabled())
							logger.info((new StringBuilder()).append("Destroy unsubscribe url ").append(url).toString());
					}
					catch (Throwable t)
					{
						logger.warn((new StringBuilder()).append("Failed to unsubscribe url ").append(url).append(" to registry ").append(getUrl()).append(" on destroy, cause: ").append(t.getMessage()).toString(), t);
					}
				}
			}

		}
	}

	public String toString()
	{
		return getUrl().toString();
	}
}
