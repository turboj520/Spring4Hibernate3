// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HttpClientConnection.java

package com.autohome.turbo.rpc.protocol.hessian;

import com.autohome.hessian.client.HessianConnection;
import java.io.*;
import java.net.URL;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;

public class HttpClientConnection
	implements HessianConnection
{

	private final HttpClient httpClient;
	private final ByteArrayOutputStream output = new ByteArrayOutputStream();
	private final HttpPost request;
	private volatile HttpResponse response;

	public HttpClientConnection(HttpClient httpClient, URL url)
	{
		this.httpClient = httpClient;
		request = new HttpPost(url.toString());
	}

	public void addHeader(String key, String value)
	{
		request.addHeader(new BasicHeader(key, value));
	}

	public OutputStream getOutputStream()
		throws IOException
	{
		return output;
	}

	public void sendRequest()
		throws IOException
	{
		request.setEntity(new ByteArrayEntity(output.toByteArray()));
		response = httpClient.execute(request);
	}

	public int getStatusCode()
	{
		return response != null && response.getStatusLine() != null ? response.getStatusLine().getStatusCode() : 0;
	}

	public String getStatusMessage()
	{
		return response != null && response.getStatusLine() != null ? response.getStatusLine().getReasonPhrase() : null;
	}

	public InputStream getInputStream()
		throws IOException
	{
		return response != null && response.getEntity() != null ? response.getEntity().getContent() : null;
	}

	public void close()
		throws IOException
	{
		HttpPost request = this.request;
		if (request != null)
			request.abort();
	}

	public void destroy()
		throws IOException
	{
	}
}
