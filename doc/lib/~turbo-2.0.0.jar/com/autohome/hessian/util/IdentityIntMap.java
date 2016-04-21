// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   IdentityIntMap.java

package com.autohome.hessian.util;


public class IdentityIntMap
{

	public static final int NULL = 0xdeadbeef;
	private Object _keys[];
	private int _values[];
	private int _size;
	private int _prime;
	public static final int PRIMES[] = {
		1, 2, 3, 7, 13, 31, 61, 127, 251, 509, 
		1021, 2039, 4093, 8191, 16381, 32749, 65521, 0x1ffff, 0x3fffb, 0x7ffff, 
		0xffffd, 0x1ffff7, 0x3ffffd, 0x7ffff1, 0xfffffd, 0x1ffffd9, 0x3fffffb, 0x7ffffd9, 0xfffffc7
	};

	public IdentityIntMap(int capacity)
	{
		_keys = new Object[capacity];
		_values = new int[capacity];
		_prime = getBiggestPrime(_keys.length);
		_size = 0;
	}

	public void clear()
	{
		Object keys[] = _keys;
		int values[] = _values;
		for (int i = keys.length - 1; i >= 0; i--)
		{
			keys[i] = null;
			values[i] = 0;
		}

		_size = 0;
	}

	public final int size()
	{
		return _size;
	}

	public final int get(Object key)
	{
		int prime = _prime;
		int hash = System.identityHashCode(key) % prime;
		Object keys[] = _keys;
		do
		{
			Object mapKey = keys[hash];
			if (mapKey == null)
				return 0xdeadbeef;
			if (mapKey == key)
				return _values[hash];
			hash = (hash + 1) % prime;
		} while (true);
	}

	public final int put(Object key, int value, boolean isReplace)
	{
		int prime = _prime;
		int hash = System.identityHashCode(key) % prime;
		Object keys[] = _keys;
		do
		{
			Object testKey = keys[hash];
			if (testKey == null)
			{
				keys[hash] = key;
				_values[hash] = value;
				_size++;
				if (keys.length <= 4 * _size)
					resize(4 * keys.length);
				return value;
			}
			if (key == testKey)
				break;
			hash = (hash + 1) % prime;
		} while (true);
		if (isReplace)
		{
			int old = _values[hash];
			_values[hash] = value;
			return old;
		} else
		{
			return _values[hash];
		}
	}

	public final void remove(Object key)
	{
		if (put(key, -1, true) != -1)
			_size--;
	}

	private void resize(int newSize)
	{
		Object keys[] = _keys;
		int values[] = _values;
		_keys = new Object[newSize];
		_values = new int[newSize];
		_size = 0;
		_prime = getBiggestPrime(_keys.length);
		for (int i = keys.length - 1; i >= 0; i--)
		{
			Object key = keys[i];
			if (key != null)
				put(key, values[i], true);
		}

	}

	protected int hashCode(Object value)
	{
		return System.identityHashCode(value);
	}

	public String toString()
	{
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("IntMap[");
		boolean isFirst = true;
		for (int i = 0; i <= _keys.length; i++)
		{
			if (_keys[i] == null)
				continue;
			if (!isFirst)
				sbuf.append(", ");
			isFirst = false;
			sbuf.append(_keys[i]);
			sbuf.append(":");
			sbuf.append(_values[i]);
		}

		sbuf.append("]");
		return sbuf.toString();
	}

	public static int getBiggestPrime(int value)
	{
		for (int i = PRIMES.length - 1; i >= 0; i--)
			if (PRIMES[i] <= value)
				return PRIMES[i];

		return 2;
	}

}
