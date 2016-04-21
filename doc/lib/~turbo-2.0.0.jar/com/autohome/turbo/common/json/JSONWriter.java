// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JSONWriter.java

package com.autohome.turbo.common.json;

import com.autohome.turbo.common.utils.Stack;
import java.io.*;

public class JSONWriter
{
	private static class State
	{

		private byte type;
		private int itemCount;




		State(byte t)
		{
			itemCount = 0;
			type = t;
		}
	}


	private static final byte UNKNOWN = 0;
	private static final byte ARRAY = 1;
	private static final byte OBJECT = 2;
	private static final byte OBJECT_VALUE = 3;
	private Writer mWriter;
	private State mState;
	private Stack mStack;
	private static final String CONTROL_CHAR_MAP[] = {
		"\\u0000", "\\u0001", "\\u0002", "\\u0003", "\\u0004", "\\u0005", "\\u0006", "\\u0007", "\\b", "\\t", 
		"\\n", "\\u000b", "\\f", "\\r", "\\u000e", "\\u000f", "\\u0010", "\\u0011", "\\u0012", "\\u0013", 
		"\\u0014", "\\u0015", "\\u0016", "\\u0017", "\\u0018", "\\u0019", "\\u001a", "\\u001b", "\\u001c", "\\u001d", 
		"\\u001e", "\\u001f"
	};

	public JSONWriter(Writer writer)
	{
		mState = new State((byte)0);
		mStack = new Stack();
		mWriter = writer;
	}

	public JSONWriter(OutputStream is, String charset)
		throws UnsupportedEncodingException
	{
		mState = new State((byte)0);
		mStack = new Stack();
		mWriter = new OutputStreamWriter(is, charset);
	}

	public JSONWriter objectBegin()
		throws IOException
	{
		beforeValue();
		mWriter.write(123);
		mStack.push(mState);
		mState = new State((byte)2);
		return this;
	}

	public JSONWriter objectEnd()
		throws IOException
	{
		mWriter.write(125);
		mState = (State)mStack.pop();
		return this;
	}

	public JSONWriter objectItem(String name)
		throws IOException
	{
		beforeObjectItem();
		mWriter.write(34);
		mWriter.write(escape(name));
		mWriter.write(34);
		mWriter.write(58);
		return this;
	}

	public JSONWriter arrayBegin()
		throws IOException
	{
		beforeValue();
		mWriter.write(91);
		mStack.push(mState);
		mState = new State((byte)1);
		return this;
	}

	public JSONWriter arrayEnd()
		throws IOException
	{
		mWriter.write(93);
		mState = (State)mStack.pop();
		return this;
	}

	public JSONWriter valueNull()
		throws IOException
	{
		beforeValue();
		mWriter.write("null");
		return this;
	}

	public JSONWriter valueString(String value)
		throws IOException
	{
		beforeValue();
		mWriter.write(34);
		mWriter.write(escape(value));
		mWriter.write(34);
		return this;
	}

	public JSONWriter valueBoolean(boolean value)
		throws IOException
	{
		beforeValue();
		mWriter.write(value ? "true" : "false");
		return this;
	}

	public JSONWriter valueInt(int value)
		throws IOException
	{
		beforeValue();
		mWriter.write(String.valueOf(value));
		return this;
	}

	public JSONWriter valueLong(long value)
		throws IOException
	{
		beforeValue();
		mWriter.write(String.valueOf(value));
		return this;
	}

	public JSONWriter valueFloat(float value)
		throws IOException
	{
		beforeValue();
		mWriter.write(String.valueOf(value));
		return this;
	}

	public JSONWriter valueDouble(double value)
		throws IOException
	{
		beforeValue();
		mWriter.write(String.valueOf(value));
		return this;
	}

	private void beforeValue()
		throws IOException
	{
		switch (mState.type)
		{
		case 1: // '\001'
			if (mState.itemCount++ > 0)
				mWriter.write(44);
			return;

		case 2: // '\002'
			throw new IOException("Must call objectItem first.");

		case 3: // '\003'
			mState.type = 2;
			return;
		}
	}

	private void beforeObjectItem()
		throws IOException
	{
		switch (mState.type)
		{
		case 3: // '\003'
			mWriter.write("null");
			// fall through

		case 2: // '\002'
			mState.type = 3;
			if (mState.itemCount++ > 0)
				mWriter.write(44);
			return;

		default:
			throw new IOException("Must call objectBegin first.");
		}
	}

	private static String escape(String str)
	{
		if (str == null)
			return str;
		int len = str.length();
		if (len == 0)
			return str;
		StringBuilder sb = null;
		for (int i = 0; i < len; i++)
		{
			char c = str.charAt(i);
			if (c < ' ')
			{
				if (sb == null)
				{
					sb = new StringBuilder(len << 1);
					sb.append(str, 0, i);
				}
				sb.append(CONTROL_CHAR_MAP[c]);
				continue;
			}
			switch (c)
			{
			case 34: // '"'
			case 47: // '/'
			case 92: // '\\'
				if (sb == null)
				{
					sb = new StringBuilder(len << 1);
					sb.append(str, 0, i);
				}
				sb.append('\\').append(c);
				break;

			default:
				if (sb != null)
					sb.append(c);
				break;
			}
		}

		return sb != null ? sb.toString() : str;
	}

}
