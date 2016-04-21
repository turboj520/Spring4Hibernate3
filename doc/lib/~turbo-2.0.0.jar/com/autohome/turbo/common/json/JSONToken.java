// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JSONToken.java

package com.autohome.turbo.common.json;


public class JSONToken
{

	public static final int ANY = 0;
	public static final int IDENT = 1;
	public static final int LBRACE = 2;
	public static final int LSQUARE = 3;
	public static final int RBRACE = 4;
	public static final int RSQUARE = 5;
	public static final int COMMA = 6;
	public static final int COLON = 7;
	public static final int NULL = 16;
	public static final int BOOL = 17;
	public static final int INT = 18;
	public static final int FLOAT = 19;
	public static final int STRING = 20;
	public static final int ARRAY = 21;
	public static final int OBJECT = 22;
	public final int type;
	public final Object value;

	JSONToken(int t)
	{
		this(t, null);
	}

	JSONToken(int t, Object v)
	{
		type = t;
		value = v;
	}

	static String token2string(int t)
	{
		switch (t)
		{
		case 2: // '\002'
			return "{";

		case 4: // '\004'
			return "}";

		case 3: // '\003'
			return "[";

		case 5: // '\005'
			return "]";

		case 6: // '\006'
			return ",";

		case 7: // '\007'
			return ":";

		case 1: // '\001'
			return "IDENT";

		case 16: // '\020'
			return "NULL";

		case 17: // '\021'
			return "BOOL VALUE";

		case 18: // '\022'
			return "INT VALUE";

		case 19: // '\023'
			return "FLOAT VALUE";

		case 20: // '\024'
			return "STRING VALUE";

		case 8: // '\b'
		case 9: // '\t'
		case 10: // '\n'
		case 11: // '\013'
		case 12: // '\f'
		case 13: // '\r'
		case 14: // '\016'
		case 15: // '\017'
		default:
			return "ANY";
		}
	}
}
