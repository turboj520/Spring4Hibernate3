// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   TelnetUtils.java

package com.autohome.turbo.remoting.telnet.support;

import java.util.*;

public class TelnetUtils
{

	public TelnetUtils()
	{
	}

	public static String toList(List table)
	{
		int widths[] = new int[((List)table.get(0)).size()];
		for (int j = 0; j < widths.length; j++)
		{
			for (Iterator i$ = table.iterator(); i$.hasNext();)
			{
				List row = (List)i$.next();
				widths[j] = Math.max(widths[j], ((String)row.get(j)).length());
			}

		}

		StringBuilder buf = new StringBuilder();
		for (Iterator i$ = table.iterator(); i$.hasNext();)
		{
			List row = (List)i$.next();
			if (buf.length() > 0)
				buf.append("\r\n");
			int j = 0;
			while (j < widths.length) 
			{
				if (j > 0)
					buf.append(" - ");
				String value = (String)row.get(j);
				buf.append(value);
				if (j < widths.length - 1)
				{
					int pad = widths[j] - value.length();
					if (pad > 0)
					{
						for (int k = 0; k < pad; k++)
							buf.append(" ");

					}
				}
				j++;
			}
		}

		return buf.toString();
	}

	public static String toTable(String header[], List table)
	{
		return toTable(Arrays.asList(header), table);
	}

	public static String toTable(List header, List table)
	{
		int totalWidth = 0;
		int widths[] = new int[header.size()];
		int maxwidth = 70;
		int maxcountbefore = 0;
		for (int j = 0; j < widths.length; j++)
			widths[j] = Math.max(widths[j], ((String)header.get(j)).length());

		for (Iterator i$ = table.iterator(); i$.hasNext();)
		{
			List row = (List)i$.next();
			int countbefore = 0;
			for (int j = 0; j < widths.length; j++)
			{
				widths[j] = Math.max(widths[j], ((String)row.get(j)).length());
				totalWidth = totalWidth + widths[j] <= maxwidth ? totalWidth + widths[j] : maxwidth;
				if (j < widths.length - 1)
					countbefore += widths[j];
			}

			maxcountbefore = Math.max(countbefore, maxcountbefore);
		}

		widths[widths.length - 1] = Math.min(widths[widths.length - 1], maxwidth - maxcountbefore);
		StringBuilder buf = new StringBuilder();
		buf.append("+");
		for (int j = 0; j < widths.length; j++)
		{
			for (int k = 0; k < widths[j] + 2; k++)
				buf.append("-");

			buf.append("+");
		}

		buf.append("\r\n");
		buf.append("|");
		for (int j = 0; j < widths.length; j++)
		{
			String cell = (String)header.get(j);
			buf.append(" ");
			buf.append(cell);
			int pad = widths[j] - cell.length();
			if (pad > 0)
			{
				for (int k = 0; k < pad; k++)
					buf.append(" ");

			}
			buf.append(" |");
		}

		buf.append("\r\n");
		buf.append("+");
		for (int j = 0; j < widths.length; j++)
		{
			for (int k = 0; k < widths[j] + 2; k++)
				buf.append("-");

			buf.append("+");
		}

		buf.append("\r\n");
		StringBuffer rowbuf;
		for (Iterator i$ = table.iterator(); i$.hasNext(); buf.append(rowbuf).append("\r\n"))
		{
			List row = (List)i$.next();
			rowbuf = new StringBuffer();
			rowbuf.append("|");
			for (int j = 0; j < widths.length; j++)
			{
				String cell = (String)row.get(j);
				rowbuf.append(" ");
				for (int remaing = cell.length(); remaing > 0; remaing--)
				{
					if (rowbuf.length() >= totalWidth)
					{
						buf.append(rowbuf.toString());
						rowbuf = new StringBuffer();
					}
					rowbuf.append(cell.substring(cell.length() - remaing, (cell.length() - remaing) + 1));
				}

				int pad = widths[j] - cell.length();
				if (pad > 0)
				{
					for (int k = 0; k < pad; k++)
						rowbuf.append(" ");

				}
				rowbuf.append(" |");
			}

		}

		buf.append("+");
		for (int j = 0; j < widths.length; j++)
		{
			for (int k = 0; k < widths[j] + 2; k++)
				buf.append("-");

			buf.append("+");
		}

		buf.append("\r\n");
		return buf.toString();
	}
}
