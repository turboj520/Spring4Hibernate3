// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   MultiMessage.java

package com.autohome.turbo.remoting.exchange.support;

import java.util.*;

public final class MultiMessage
	implements Iterable
{

	private final List messages = new ArrayList();

	public static MultiMessage createFromCollection(Collection collection)
	{
		MultiMessage result = new MultiMessage();
		result.addMessages(collection);
		return result;
	}

	public static transient MultiMessage createFromArray(Object args[])
	{
		return createFromCollection(Arrays.asList(args));
	}

	public static MultiMessage create()
	{
		return new MultiMessage();
	}

	private MultiMessage()
	{
	}

	public void addMessage(Object msg)
	{
		messages.add(msg);
	}

	public void addMessages(Collection collection)
	{
		messages.addAll(collection);
	}

	public Collection getMessages()
	{
		return Collections.unmodifiableCollection(messages);
	}

	public int size()
	{
		return messages.size();
	}

	public Object get(int index)
	{
		return messages.get(index);
	}

	public boolean isEmpty()
	{
		return messages.isEmpty();
	}

	public Collection removeMessages()
	{
		Collection result = Collections.unmodifiableCollection(messages);
		messages.clear();
		return result;
	}

	public Iterator iterator()
	{
		return messages.iterator();
	}
}
