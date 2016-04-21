// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Page.java

package com.autohome.turbo.container.page;

import java.util.*;

public class Page
{

	private final String navigation;
	private final String title;
	private final List columns;
	private final List rows;

	public Page(String navigation)
	{
		this(navigation, (String)null, (String[])null, (List)null);
	}

	public Page(String navigation, String title, String column, String row)
	{
		this(navigation, title, column != null ? Arrays.asList(new String[] {
			column
		}) : null, row != null ? stringToList(row) : null);
	}

	private static List stringToList(String str)
	{
		List rows = new ArrayList();
		List row = new ArrayList();
		row.add(str);
		rows.add(row);
		return rows;
	}

	public Page(String navigation, String title, String columns[], List rows)
	{
		this(navigation, title, columns != null ? Arrays.asList(columns) : null, rows);
	}

	public Page(String navigation, String title, List columns, List rows)
	{
		this.navigation = navigation;
		this.title = title;
		this.columns = columns;
		this.rows = rows;
	}

	public String getNavigation()
	{
		return navigation;
	}

	public String getTitle()
	{
		return title;
	}

	public List getColumns()
	{
		return columns;
	}

	public List getRows()
	{
		return rows;
	}
}
