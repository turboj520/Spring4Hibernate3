// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HessianURLConnection.java

package com.autohome.hessian.client;

import java.io.*;
import java.net.*;

// Referenced classes of package com.autohome.hessian.client:
//			AbstractHessianConnection, HessianConnectionException

public class HessianURLConnection extends AbstractHessianConnection
{

	private URL _url;
	private URLConnection _conn;
	private int _statusCode;
	private String _statusMessage;
	private InputStream _inputStream;
	private InputStream _errorStream;

	HessianURLConnection(URL url, URLConnection conn)
	{
		_url = url;
		_conn = conn;
	}

	public void addHeader(String key, String value)
	{
		_conn.setRequestProperty(key, value);
	}

	public OutputStream getOutputStream()
		throws IOException
	{
		return _conn.getOutputStream();
	}

	public void sendRequest()
		throws IOException
	{
		if (_conn instanceof HttpURLConnection)
		{
			HttpURLConnection httpConn = (HttpURLConnection)_conn;
			_statusCode = 500;
			try
			{
				_statusCode = httpConn.getResponseCode();
			}
			catch (Exception e) { }
			parseResponseHeaders(httpConn);
			InputStream is = null;
			if (_statusCode != 200)
			{
				StringBuffer sb = new StringBuffer();
				try
				{
					is = httpConn.getInputStream();
					int ch;
					if (is != null)
					{
						while ((ch = is.read()) >= 0) 
							sb.append((char)ch);
						is.close();
					}
					is = httpConn.getErrorStream();
					if (is != null)
						while ((ch = is.read()) >= 0) 
							sb.append((char)ch);
					_statusMessage = sb.toString();
				}
				catch (FileNotFoundException e)
				{
					throw new HessianConnectionException((new StringBuilder()).append("HessianProxy cannot connect to '").append(_url).toString(), e);
				}
				catch (IOException e)
				{
					if (is == null)
						throw new HessianConnectionException((new StringBuilder()).append(_statusCode).append(": ").append(e).toString(), e);
					else
						throw new HessianConnectionException((new StringBuilder()).append(_statusCode).append(": ").append(sb).toString(), e);
				}
				if (is != null)
					is.close();
				throw new HessianConnectionException((new StringBuilder()).append(_statusCode).append(": ").append(sb.toString()).toString());
			}
		}
	}

	protected void parseResponseHeaders(HttpURLConnection httpurlconnection)
		throws IOException
	{
	}

	public int getStatusCode()
	{
		return _statusCode;
	}

	public String getStatusMessage()
	{
		return _statusMessage;
	}

	public InputStream getInputStream()
		throws IOException
	{
		return _conn.getInputStream();
	}

	public void close()
	{
	}

	public void destroy()
	{
		URLConnection conn = _conn;
		_conn = null;
		if (conn instanceof HttpURLConnection)
			((HttpURLConnection)conn).disconnect();
	}
}
