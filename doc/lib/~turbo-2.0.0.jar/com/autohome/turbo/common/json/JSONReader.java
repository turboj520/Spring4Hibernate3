// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JSONReader.java

package com.autohome.turbo.common.json;

import java.io.*;

// Referenced classes of package com.autohome.turbo.common.json:
//			ParseException, Yylex, JSONToken

public class JSONReader
{

	private static ThreadLocal LOCAL_LEXER = new ThreadLocal() {

	};
	private Yylex mLex;

	public JSONReader(InputStream is, String charset)
		throws UnsupportedEncodingException
	{
		this(((Reader) (new InputStreamReader(is, charset))));
	}

	public JSONReader(Reader reader)
	{
		mLex = getLexer(reader);
	}

	public JSONToken nextToken()
		throws IOException, ParseException
	{
		return mLex.yylex();
	}

	public JSONToken nextToken(int expect)
		throws IOException, ParseException
	{
		JSONToken ret = mLex.yylex();
		if (ret == null)
			throw new ParseException("EOF error.");
		if (expect != 0 && expect != ret.type)
			throw new ParseException("Unexcepted token.");
		else
			return ret;
	}

	private static Yylex getLexer(Reader reader)
	{
		Yylex ret = (Yylex)LOCAL_LEXER.get();
		if (ret == null)
		{
			ret = new Yylex(reader);
			LOCAL_LEXER.set(ret);
		} else
		{
			ret.yyreset(reader);
		}
		return ret;
	}

}
