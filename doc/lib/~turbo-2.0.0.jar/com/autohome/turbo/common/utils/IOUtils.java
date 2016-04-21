// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   IOUtils.java

package com.autohome.turbo.common.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class IOUtils
{

	private static final int BUFFER_SIZE = 8192;

	private IOUtils()
	{
	}

	public static long write(InputStream is, OutputStream os)
		throws IOException
	{
		return write(is, os, 8192);
	}

	public static long write(InputStream is, OutputStream os, int bufferSize)
		throws IOException
	{
		long total = 0L;
		byte buff[] = new byte[bufferSize];
		do
		{
			if (is.available() <= 0)
				break;
			int read = is.read(buff, 0, buff.length);
			if (read > 0)
			{
				os.write(buff, 0, read);
				total += read;
			}
		} while (true);
		return total;
	}

	public static String read(Reader reader)
		throws IOException
	{
		StringWriter writer = new StringWriter();
		String s;
		write(reader, writer);
		s = writer.getBuffer().toString();
		writer.close();
		return s;
		Exception exception;
		exception;
		writer.close();
		throw exception;
	}

	public static long write(Writer writer, String string)
		throws IOException
	{
		Reader reader = new StringReader(string);
		long l = write(reader, writer);
		reader.close();
		return l;
		Exception exception;
		exception;
		reader.close();
		throw exception;
	}

	public static long write(Reader reader, Writer writer)
		throws IOException
	{
		return write(reader, writer, 8192);
	}

	public static long write(Reader reader, Writer writer, int bufferSize)
		throws IOException
	{
		long total = 0L;
		char buf[] = new char[8192];
		int read;
		while ((read = reader.read(buf)) != -1) 
		{
			writer.write(buf, 0, read);
			total += read;
		}
		return total;
	}

	public static String[] readLines(File file)
		throws IOException
	{
		if (file == null || !file.exists() || !file.canRead())
			return new String[0];
		else
			return readLines(((InputStream) (new FileInputStream(file))));
	}

	public static String[] readLines(InputStream is)
		throws IOException
	{
		List lines;
		BufferedReader reader;
		lines = new ArrayList();
		reader = new BufferedReader(new InputStreamReader(is));
		String as[];
		String line;
		while ((line = reader.readLine()) != null) 
			lines.add(line);
		as = (String[])lines.toArray(new String[0]);
		reader.close();
		return as;
		Exception exception;
		exception;
		reader.close();
		throw exception;
	}

	public static void writeLines(OutputStream os, String lines[])
		throws IOException
	{
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(os));
		String arr$[] = lines;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			String line = arr$[i$];
			writer.println(line);
		}

		writer.flush();
		writer.close();
		break MISSING_BLOCK_LABEL_70;
		Exception exception;
		exception;
		writer.close();
		throw exception;
	}

	public static void writeLines(File file, String lines[])
		throws IOException
	{
		if (file == null)
		{
			throw new IOException("File is null.");
		} else
		{
			writeLines(((OutputStream) (new FileOutputStream(file))), lines);
			return;
		}
	}

	public static void appendLines(File file, String lines[])
		throws IOException
	{
		if (file == null)
		{
			throw new IOException("File is null.");
		} else
		{
			writeLines(new FileOutputStream(file, true), lines);
			return;
		}
	}
}
