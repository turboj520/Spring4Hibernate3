// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   IntMap.java

package com.autohome.com.caucho.hessian.util;


public class IntMap
{

	public static final int NULL = 0xdeadbeef;
	private static final Object DELETED = new Object();
	private Object _keys[];
	private int _values[];
	private int _size;
	private int _mask;

	public IntMap()
	{
		_keys = new Object[256];
		_values = new int[256];
		_mask = _keys.length - 1;
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

	public int size()
	{
		return _size;
	}

	public int get(Object key)
	{
		int mask = _mask;
		int hash = key.hashCode() % mask & mask;
		Object keys[] = _keys;
		do
		{
			Object mapKey = keys[hash];
			if (mapKey == null)
				return 0xdeadbeef;
			if (mapKey == key || mapKey.equals(key))
				return _values[hash];
			hash = (hash + 1) % mask;
		} while (true);
	}

	private void resize(int newSize)
	{
		Object newKeys[] = new Object[newSize];
		int newValues[] = new int[newSize];
		int mask = _mask = newKeys.length - 1;
		Object keys[] = _keys;
		int values[] = _values;
label0:
		for (int i = keys.length - 1; i >= 0; i--)
		{
			Object key = keys[i];
			if (key == null || key == DELETED)
				continue;
			int hash = key.hashCode() % mask & mask;
			do
			{
				if (newKeys[hash] == null)
				{
					newKeys[hash] = key;
					newValues[hash] = values[i];
					continue label0;
				}
				hash = (hash + 1) % mask;
			} while (true);
		}

		_keys = newKeys;
		_values = newValues;
	}

	public int put(Object key, int value)
	{
		int mask = _mask;
		int hash = key.hashCode() % mask & mask;
		Object keys[] = _keys;
		do
		{
			Object testKey = keys[hash];
			if (testKey == null || testKey == DELETED)
			{
				keys[hash] = key;
				_values[hash] = value;
				_size++;
				if (keys.length <= 4 * _size)
					resize(4 * keys.length);
				return 0xdeadbeef;
			}
			if (key != testKey && !key.equals(testKey))
			{
				hash = (hash + 1) % mask;
			} else
			{
				int old = _values[hash];
				_values[hash] = value;
				return old;
			}
		} while (true);
	}

	public int remove(Object key)
	{
		int mask = _mask;
		int hash = key.hashCode() % mask & mask;
		do
		{
			Object mapKey = _keys[hash];
			if (mapKey == null)
				return 0xdeadbeef;
			if (mapKey == key)
			{
				_keys[hash] = DELETED;
				_size--;
				return _values[hash];
			}
			hash = (hash + 1) % mask;
		} while (true);
	}

	public String toString()
	{
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("IntMap[");
		boolean isFirst = true;
		for (int i = 0; i <= _mask; i++)
		{
			if (_keys[i] == null || _keys[i] == DELETED)
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

}
