// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ListenerExporterWrapper.java

package com.autohome.turbo.rpc.listener;

import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.rpc.*;
import java.util.Iterator;
import java.util.List;

public class ListenerExporterWrapper
	implements Exporter
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/rpc/listener/ListenerExporterWrapper);
	private final Exporter exporter;
	private final List listeners;

	public ListenerExporterWrapper(Exporter exporter, List listeners)
	{
		if (exporter == null)
			throw new IllegalArgumentException("exporter == null");
		this.exporter = exporter;
		this.listeners = listeners;
		if (listeners != null && listeners.size() > 0)
		{
			RuntimeException exception = null;
			Iterator i$ = listeners.iterator();
			do
			{
				if (!i$.hasNext())
					break;
				ExporterListener listener = (ExporterListener)i$.next();
				if (listener != null)
					try
					{
						listener.exported(this);
					}
					catch (RuntimeException t)
					{
						logger.error(t.getMessage(), t);
						exception = t;
					}
			} while (true);
			if (exception != null)
				throw exception;
		}
	}

	public Invoker getInvoker()
	{
		return exporter.getInvoker();
	}

	public void unexport()
	{
		exporter.unexport();
		if (listeners != null && listeners.size() > 0)
		{
			RuntimeException exception = null;
			Iterator i$ = listeners.iterator();
			do
			{
				if (!i$.hasNext())
					break;
				ExporterListener listener = (ExporterListener)i$.next();
				if (listener != null)
					try
					{
						listener.unexported(this);
					}
					catch (RuntimeException t)
					{
						logger.error(t.getMessage(), t);
						exception = t;
					}
			} while (true);
			if (exception != null)
				throw exception;
		}
		break MISSING_BLOCK_LABEL_213;
		Exception exception1;
		exception1;
		if (listeners != null && listeners.size() > 0)
		{
			RuntimeException exception = null;
			Iterator i$ = listeners.iterator();
			do
			{
				if (!i$.hasNext())
					break;
				ExporterListener listener = (ExporterListener)i$.next();
				if (listener != null)
					try
					{
						listener.unexported(this);
					}
					catch (RuntimeException t)
					{
						logger.error(t.getMessage(), t);
						exception = t;
					}
			} while (true);
			if (exception != null)
				throw exception;
		}
		throw exception1;
	}

}
