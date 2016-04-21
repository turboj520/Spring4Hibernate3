// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HessianFreeList.java

package com.autohome.hessian.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

public final class HessianFreeList
{

	private final AtomicReferenceArray _freeStack;
	private final AtomicInteger _top = new AtomicInteger();

	public HessianFreeList(int size)
	{
		_freeStack = new AtomicReferenceArray(size);
	}

	public Object allocate()
	{
		int top = _top.get();
		if (top > 0 && _top.compareAndSet(top, top - 1))
			return _freeStack.getAndSet(top - 1, null);
		else
			return null;
	}

	public boolean free(Object obj)
	{
		int top = _top.get();
		if (top < _freeStack.length())
		{
			boolean isFree = _freeStack.compareAndSet(top, null, obj);
			_top.compareAndSet(top, top + 1);
			return isFree;
		} else
		{
			return false;
		}
	}

	public boolean allowFree(Object obj)
	{
		return _top.get() < _freeStack.length();
	}

	public void freeCareful(Object obj)
	{
		if (checkDuplicate(obj))
		{
			throw new IllegalStateException((new StringBuilder()).append("tried to free object twice: ").append(obj).toString());
		} else
		{
			free(obj);
			return;
		}
	}

	public boolean checkDuplicate(Object obj)
	{
		int top = _top.get();
		for (int i = top - 1; i >= 0; i--)
			if (_freeStack.get(i) == obj)
				return true;

		return false;
	}
}
