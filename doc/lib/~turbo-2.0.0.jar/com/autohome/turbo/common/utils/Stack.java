// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Stack.java

package com.autohome.turbo.common.utils;

import java.util.*;

public class Stack
{

	private int mSize;
	private List mElements;

	public Stack()
	{
		mSize = 0;
		mElements = new ArrayList();
	}

	public void push(Object ele)
	{
		if (mElements.size() > mSize)
			mElements.set(mSize, ele);
		else
			mElements.add(ele);
		mSize++;
	}

	public Object pop()
	{
		if (mSize == 0)
			throw new EmptyStackException();
		else
			return mElements.set(--mSize, null);
	}

	public Object peek()
	{
		if (mSize == 0)
			throw new EmptyStackException();
		else
			return mElements.get(mSize - 1);
	}

	public Object get(int index)
	{
		if (index >= mSize)
			throw new IndexOutOfBoundsException((new StringBuilder()).append("Index: ").append(index).append(", Size: ").append(mSize).toString());
		else
			return index >= 0 ? mElements.get(index) : mElements.get(index + mSize);
	}

	public Object set(int index, Object value)
	{
		if (index >= mSize)
			throw new IndexOutOfBoundsException((new StringBuilder()).append("Index: ").append(index).append(", Size: ").append(mSize).toString());
		else
			return mElements.set(index >= 0 ? index : index + mSize, value);
	}

	public Object remove(int index)
	{
		if (index >= mSize)
		{
			throw new IndexOutOfBoundsException((new StringBuilder()).append("Index: ").append(index).append(", Size: ").append(mSize).toString());
		} else
		{
			Object ret = mElements.remove(index >= 0 ? index : index + mSize);
			mSize--;
			return ret;
		}
	}

	public int size()
	{
		return mSize;
	}

	public boolean isEmpty()
	{
		return mSize == 0;
	}

	public void clear()
	{
		mSize = 0;
		mElements.clear();
	}
}
