// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ResourceFilter.java

package com.autohome.turbo.container.page;

import com.autohome.turbo.common.Constants;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ResourceFilter
	implements Filter
{

	private static final String CLASSPATH_PREFIX = "classpath:";
	private final long start = System.currentTimeMillis();
	private final List resources = new ArrayList();

	public ResourceFilter()
	{
	}

	public void init(FilterConfig filterConfig)
		throws ServletException
	{
		String config = filterConfig.getInitParameter("resources");
		if (config != null && config.length() > 0)
		{
			String configs[] = Constants.COMMA_SPLIT_PATTERN.split(config);
			String arr$[] = configs;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				String c = arr$[i$];
				if (c == null || c.length() <= 0)
					continue;
				c = c.replace('\\', '/');
				if (c.endsWith("/"))
					c = c.substring(0, c.length() - 1);
				resources.add(c);
			}

		}
	}

	public void destroy()
	{
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
		throws IOException, ServletException
	{
		HttpServletResponse response;
		long lastModified;
		InputStream input;
		HttpServletRequest request = (HttpServletRequest)req;
		response = (HttpServletResponse)res;
		if (response.isCommitted())
			return;
		String uri = request.getRequestURI();
		String context = request.getContextPath();
		if (uri.endsWith("/favicon.ico"))
			uri = "/favicon.ico";
		else
		if (context != null && !"/".equals(context))
			uri = uri.substring(context.length());
		if (!uri.startsWith("/"))
			uri = (new StringBuilder()).append("/").append(uri).toString();
		lastModified = getLastModified(uri);
		long since = request.getDateHeader("If-Modified-Since");
		if (since >= lastModified)
		{
			response.sendError(304);
			return;
		}
		input = getInputStream(uri);
		if (input == null)
		{
			chain.doFilter(req, res);
			return;
		}
		byte data[];
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte buffer[] = new byte[8192];
		for (int n = 0; -1 != (n = input.read(buffer));)
			output.write(buffer, 0, n);

		data = output.toByteArray();
		input.close();
		break MISSING_BLOCK_LABEL_248;
		Exception exception;
		exception;
		input.close();
		throw exception;
		response.setDateHeader("Last-Modified", lastModified);
		OutputStream output = response.getOutputStream();
		output.write(data);
		output.flush();
		return;
	}

	private boolean isFile(String path)
	{
		return path.startsWith("/") || path.indexOf(":") <= 1;
	}

	private long getLastModified(String uri)
	{
		for (Iterator i$ = resources.iterator(); i$.hasNext();)
		{
			String resource = (String)i$.next();
			if (resource != null && resource.length() > 0)
			{
				String path = (new StringBuilder()).append(resource).append(uri).toString();
				if (isFile(path))
				{
					File file = new File(path);
					if (file.exists())
						return file.lastModified();
				}
			}
		}

		return start;
	}

	private InputStream getInputStream(String uri)
	{
		Iterator i$ = resources.iterator();
_L2:
		String path;
		if (!i$.hasNext())
			break; /* Loop/switch isn't completed */
		String resource = (String)i$.next();
		path = (new StringBuilder()).append(resource).append(uri).toString();
		if (isFile(path))
			return new FileInputStream(path);
		if (path.startsWith("classpath:"))
			return Thread.currentThread().getContextClassLoader().getResourceAsStream(path.substring("classpath:".length()));
		return (new URL(path)).openStream();
		IOException e;
		e;
		if (true) goto _L2; else goto _L1
_L1:
		return null;
	}
}
