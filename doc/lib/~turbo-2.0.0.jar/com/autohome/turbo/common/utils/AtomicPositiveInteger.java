// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AtomicPositiveInteger.java

package com.autohome.turbo.common.utils;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicPositiveInteger extends Number
{

	private static final long serialVersionUID = 0xd5d4f589c5e04decL;
	private final AtomicInteger i;

	public AtomicPositiveInteger()
	{
		i = new AtomicInteger();
	}

	public AtomicPositiveInteger(int initialValue)
	{
		i = new AtomicInteger(initialValue);
	}

	public final int getAndIncrement()
	{
		int current;
		int next;
		do
		{
			current = i.get();
			next = current < 0x7fffffff ? current + 1 : 0;
		} while (!i.compareAndSet(current, next));
		return current;
	}

	public final int getAndDecrement()
	{
		int current;
		int next;
		do
		{
			current = i.get();
			next = current > 0 ? current - 1 : 0x7fffffff;
		} while (!i.compareAndSet(current, next));
		return current;
	}

	public final int incrementAndGet()
	{
		int current;
		int next;
		do
		{
			current = i.get();
			next = current < 0x7fffffff ? current + 1 : 0;
		} while (!i.compareAndSet(current, next));
		return next;
	}

	public final int decrementAndGet()
	{
		int current;
		int next;
		do
		{
			current = i.get();
			next = current > 0 ? current - 1 : 0x7fffffff;
		} while (!i.compareAndSet(current, next));
		return next;
	}

	public final int get()
	{
		return i.get();
	}

	public final void set(int newValue)
	{
		if (newValue < 0)
		{
			throw new IllegalArgumentException((new StringBuilder()).append("new value ").append(newValue).append(" < 0").toString());
		} else
		{
			i.set(newValue);
			return;
		}
	}

	public final int getAndSet(int newValue)
	{
		if (newValue < 0)
			throw new IllegalArgumentException((new StringBuilder()).append("new value ").append(newValue).append(" < 0").toString());
		else
			return i.getAndSet(newValue);
	}

	public final int getAndAdd(int delta)
	{
		if (delta < 0)
			throw new IllegalArgumentException((new StringBuilder()).append("delta ").append(delta).append(" < 0").toString());
		int current;
		int next;
		do
		{
			current = i.get();
			next = current < (0x7fffffff - delta) + 1 ? current + delta : delta - 1;
		} while (!i.compareAndSet(current, next));
		return current;
	}

	public final int addAndGet(int delta)
	{
		if (delta < 0)
			throw new IllegalArgumentException((new StringBuilder()).append("delta ").append(delta).append(" < 0").toString());
		int current;
		int next;
		do
		{
			current = i.get();
			next = current < (0x7fffffff - delta) + 1 ? current + delta : delta - 1;
		} while (!i.compareAndSet(current, next));
		return next;
	}

	public final boolean compareAndSet(int expect, int update)
	{
		if (update < 0)
			throw new IllegalArgumentException((new StringBuilder()).append("update value ").append(update).append(" < 0").toString());
		else
			return i.compareAndSet(expect, update);
	}

	public final boolean weakCompareAndSet(int expect, int update)
	{
		if (update < 0)
			throw new IllegalArgumentException((new StringBuilder()).append("update value ").append(update).append(" < 0").toString());
		else
			return i.weakCompareAndSet(expect, update);
	}

	public byte byteValue()
	{
		return i.byteValue();
	}

	public short shortValue()
	{
		return i.shortValue();
	}

	public int intValue()
	{
		return i.intValue();
	}

	public long longValue()
	{
		return i.longValue();
	}

	public float floatValue()
	{
		return i.floatValue();
	}

	public double doubleValue()
	{
		return i.doubleValue();
	}

	public String toString()
	{
		return i.toString();
	}

	public int hashCode()
	{
		int prime = 31;
		int result = 1;
		result = 31 * result + (i != null ? i.hashCode() : 0);
		return result;
	}

	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AtomicPositiveInteger other = (AtomicPositiveInteger)obj;
		if (i == null)
		{
			if (other.i != null)
				return false;
		} else
		if (!i.equals(other.i))
			return false;
		return true;
	}
}
