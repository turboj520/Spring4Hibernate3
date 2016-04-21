// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Yylex.java

package com.autohome.turbo.common.json;

import java.io.*;

// Referenced classes of package com.autohome.turbo.common.json:
//			JSONToken, ParseException

public class Yylex
{

	public static final int YYEOF = -1;
	private static final int ZZ_BUFFERSIZE = 16384;
	public static final int STR2 = 4;
	public static final int STR1 = 2;
	public static final int YYINITIAL = 0;
	private static final int ZZ_LEXSTATE[] = {
		0, 0, 1, 1, 2, 2
	};
	private static final String ZZ_CMAP_PACKED = "\t\000\001\013\001\013\002\000\001\013\022\000\001\013\001\000\001\b\001\000\001\002\002\000\001\t\003\000\001\007\001#\001\004\001\005\001\f\n\001\001$\006\000\001\033\003\003\001\006\001\032\005\002\001\034\001\002\001\036\003\002\001\025\001\035\001\024\001\026\005\002\001!\001\n\001\"\001\000\001\002\001\000\001\027\001\r\002\003\001\023\001\016\005\002\001\030\001\002\001\017\003\002\001\020\001\031\001\021\001\022\005\002\001\037\001\000\001 ?\0";
	private static final char ZZ_CMAP[] = zzUnpackCMap("\t\000\001\013\001\013\002\000\001\013\022\000\001\013\001\000\001\b\001\000\001\002\002\000\001\t\003\000\001\007\001#\001\004\001\005\001\f\n\001\001$\006\000\001\033\003\003\001\006\001\032\005\002\001\034\001\002\001\036\003\002\001\025\001\035\001\024\001\026\005\002\001!\001\n\001\"\001\000\001\002\001\000\001\027\001\r\002\003\001\023\001\016\005\002\001\030\001\002\001\017\003\002\001\020\001\031\001\021\001\022\005\002\001\037\001\000\001 ?\0");
	private static final int ZZ_ACTION[] = zzUnpackAction();
	private static final String ZZ_ACTION_PACKED_0 = "\003\000\001\001\001\002\001\003\001\001\001\004\001\005\001\006\006\003\001\007\001\b\001\t\001\n\001\013\001\f\001\r\001\016\001\000\001\r\003\000\006\003\001\017\001\020\001\021\001\022\001\023\001\024\001\025\001\026\001\000\001\027\002\030\001\000\006\003\001\000\001\003\001\031\001\032\001\003\001\000\001\033\001\000\001\034";
	private static final int ZZ_ROWMAP[] = zzUnpackRowMap();
	private static final String ZZ_ROWMAP_PACKED_0 = "\000\000\000%\000J\000o\000\224\000\271\000\336\000o\000o\000?\000?\000身\000?\000?\000?\000?\000o\000o\000o\000o\000o\000o\000?\000o\000?\000?\000?\000?\000?\000?\000?\000?\000?\000?\000早\000o\000o\000o\000o\000o\000o\000o\000o\000?\000o\000?\000?\000?\000忌\000?\000?\000?\000?\000?\000?\000?\000\271\000\271\000?\000?\000\271\000?\000o";
	private static final int ZZ_TRANS[] = {
		3, 4, 5, 5, 6, 3, 5, 3, 7, 8, 
		3, 9, 3, 5, 10, 11, 5, 12, 5, 5, 
		13, 5, 5, 5, 5, 5, 14, 5, 5, 5, 
		15, 16, 17, 18, 19, 20, 21, 22, 22, 22, 
		22, 22, 22, 22, 22, 23, 22, 24, 22, 22, 
		22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 
		22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 
		22, 22, 22, 22, 25, 25, 25, 25, 25, 25, 
		25, 25, 25, 23, 26, 25, 25, 25, 25, 25, 
		25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 
		25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 
		25, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
		-1, -1, -1, -1, -1, -1, -1, -1, -1, 4, 
		-1, -1, -1, 27, 28, -1, -1, -1, -1, -1, 
		-1, -1, -1, -1, -1, -1, -1, 28, -1, -1, 
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
		-1, -1, -1, -1, -1, -1, 5, 5, 5, -1, 
		-1, 5, -1, -1, -1, -1, -1, -1, 5, 5, 
		5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 
		5, 5, 5, 5, 5, 5, -1, -1, -1, -1, 
		-1, -1, -1, 4, -1, -1, -1, -1, -1, -1, 
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
		9, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
		-1, -1, -1, -1, -1, -1, -1, 5, 5, 5, 
		-1, -1, 5, -1, -1, -1, -1, -1, -1, 5, 
		5, 5, 5, 5, 5, 5, 5, 5, 5, 29, 
		5, 5, 5, 5, 5, 5, 5, -1, -1, -1, 
		-1, -1, -1, -1, 5, 5, 5, -1, -1, 5, 
		-1, -1, -1, -1, -1, -1, 5, 5, 5, 5, 
		5, 30, 5, 5, 5, 5, 5, 5, 5, 5, 
		5, 5, 5, 5, -1, -1, -1, -1, -1, -1, 
		-1, 5, 5, 5, -1, -1, 5, -1, -1, -1, 
		-1, -1, -1, 5, 5, 5, 31, 5, 5, 5, 
		5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 
		5, -1, -1, -1, -1, -1, -1, -1, 5, 5, 
		5, -1, -1, 5, -1, -1, -1, -1, -1, -1, 
		5, 5, 5, 5, 5, 5, 5, 5, 32, 5, 
		5, 5, 5, 5, 5, 5, 5, 5, -1, -1, 
		-1, -1, -1, -1, -1, 5, 5, 5, -1, -1, 
		5, -1, -1, -1, -1, -1, -1, 5, 5, 5, 
		5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 
		5, 33, 5, 5, 5, -1, -1, -1, -1, -1, 
		-1, -1, 5, 5, 5, -1, -1, 5, -1, -1, 
		-1, -1, -1, -1, 5, 5, 5, 5, 5, 5, 
		5, 5, 5, 34, 5, 5, 5, 5, 5, 5, 
		5, 5, -1, -1, -1, -1, -1, -1, 22, 22, 
		22, 22, 22, 22, 22, 22, -1, 22, -1, 22, 
		22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 
		22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 
		22, 22, 22, 22, 22, -1, -1, -1, -1, -1, 
		-1, -1, -1, 35, -1, 36, -1, 37, 38, 39, 
		40, 41, 42, 43, -1, -1, -1, -1, -1, -1, 
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
		-1, -1, 25, 25, 25, 25, 25, 25, 25, 25, 
		25, -1, -1, 25, 25, 25, 25, 25, 25, 25, 
		25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 
		25, 25, 25, 25, 25, 25, 25, 25, 25, -1, 
		-1, -1, -1, -1, -1, -1, -1, -1, 44, 36, 
		-1, 37, 38, 39, 40, 41, 42, 43, -1, -1, 
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
		-1, -1, -1, -1, -1, -1, -1, 45, -1, -1, 
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
		-1, -1, -1, -1, 46, -1, -1, 47, -1, -1, 
		47, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
		-1, 5, 5, 5, -1, -1, 5, -1, -1, -1, 
		-1, -1, -1, 5, 5, 5, 5, 5, 5, 5, 
		5, 5, 5, 5, 48, 5, 5, 5, 5, 5, 
		5, -1, -1, -1, -1, -1, -1, -1, 5, 5, 
		5, -1, -1, 5, -1, -1, -1, -1, -1, -1, 
		5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 
		5, 49, 5, 5, 5, 5, 5, 5, -1, -1, 
		-1, -1, -1, -1, -1, 5, 5, 5, -1, -1, 
		5, -1, -1, -1, -1, -1, -1, 5, 5, 5, 
		5, 5, 50, 5, 5, 5, 5, 5, 5, 5, 
		5, 5, 5, 5, 5, -1, -1, -1, -1, -1, 
		-1, -1, 5, 5, 5, -1, -1, 5, -1, -1, 
		-1, -1, -1, -1, 5, 5, 5, 5, 5, 5, 
		5, 5, 5, 51, 5, 5, 5, 5, 5, 5, 
		5, 5, -1, -1, -1, -1, -1, -1, -1, 5, 
		5, 5, -1, -1, 5, -1, -1, -1, -1, -1, 
		-1, 5, 5, 5, 5, 5, 5, 5, 5, 5, 
		5, 5, 5, 5, 5, 5, 52, 5, 5, -1, 
		-1, -1, -1, -1, -1, -1, 5, 5, 5, -1, 
		-1, 5, -1, -1, -1, -1, -1, -1, 5, 5, 
		5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 
		5, 5, 5, 53, 5, 5, -1, -1, -1, -1, 
		-1, -1, -1, 54, -1, 54, -1, -1, 54, -1, 
		-1, -1, -1, -1, -1, 54, 54, -1, -1, -1, 
		-1, 54, -1, -1, -1, 54, -1, -1, 54, 54, 
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
		45, -1, -1, -1, -1, 28, -1, -1, -1, -1, 
		-1, -1, -1, -1, -1, -1, -1, -1, 28, -1, 
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
		-1, -1, -1, -1, -1, -1, -1, 46, -1, -1, 
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
		-1, -1, -1, -1, 5, 5, 5, -1, -1, 5, 
		-1, -1, -1, -1, -1, -1, 5, 5, 5, 5, 
		5, 5, 5, 5, 5, 5, 5, 5, 55, 5, 
		5, 5, 5, 5, -1, -1, -1, -1, -1, -1, 
		-1, 5, 5, 5, -1, -1, 5, -1, -1, -1, 
		-1, -1, -1, 5, 5, 5, 5, 5, 5, 5, 
		5, 5, 5, 5, 56, 5, 5, 5, 5, 5, 
		5, -1, -1, -1, -1, -1, -1, -1, 5, 5, 
		5, -1, -1, 5, -1, -1, -1, -1, -1, -1, 
		5, 5, 5, 5, 5, 5, 57, 5, 5, 5, 
		5, 5, 5, 5, 5, 5, 5, 5, -1, -1, 
		-1, -1, -1, -1, -1, 5, 5, 5, -1, -1, 
		57, -1, -1, -1, -1, -1, -1, 5, 5, 5, 
		5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 
		5, 5, 5, 5, 5, -1, -1, -1, -1, -1, 
		-1, -1, 5, 5, 5, -1, -1, 5, -1, -1, 
		-1, -1, -1, -1, 5, 5, 5, 5, 5, 5, 
		5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 
		58, 5, -1, -1, -1, -1, -1, -1, -1, 5, 
		5, 5, -1, -1, 5, -1, -1, -1, -1, -1, 
		-1, 5, 5, 5, 5, 5, 5, 5, 5, 5, 
		5, 5, 5, 5, 5, 5, 56, 5, 5, -1, 
		-1, -1, -1, -1, -1, -1, 59, -1, 59, -1, 
		-1, 59, -1, -1, -1, -1, -1, -1, 59, 59, 
		-1, -1, -1, -1, 59, -1, -1, -1, 59, -1, 
		-1, 59, 59, -1, -1, -1, -1, -1, -1, -1, 
		-1, -1, -1, 5, 5, 5, -1, -1, 5, -1, 
		-1, -1, -1, -1, -1, 5, 5, 5, 5, 5, 
		5, 60, 5, 5, 5, 5, 5, 5, 5, 5, 
		5, 5, 5, -1, -1, -1, -1, -1, -1, -1, 
		5, 5, 5, -1, -1, 60, -1, -1, -1, -1, 
		-1, -1, 5, 5, 5, 5, 5, 5, 5, 5, 
		5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 
		-1, -1, -1, -1, -1, -1, -1, 61, -1, 61, 
		-1, -1, 61, -1, -1, -1, -1, -1, -1, 61, 
		61, -1, -1, -1, -1, 61, -1, -1, -1, 61, 
		-1, -1, 61, 61, -1, -1, -1, -1, -1, -1, 
		-1, -1, -1, -1, 62, -1, 62, -1, -1, 62, 
		-1, -1, -1, -1, -1, -1, 62, 62, -1, -1, 
		-1, -1, 62, -1, -1, -1, 62, -1, -1, 62, 
		62, -1, -1, -1, -1, -1, -1, -1, -1, -1
	};
	private static final int ZZ_UNKNOWN_ERROR = 0;
	private static final int ZZ_NO_MATCH = 1;
	private static final int ZZ_PUSHBACK_2BIG = 2;
	private static final String ZZ_ERROR_MSG[] = {
		"Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large"
	};
	private static final int ZZ_ATTRIBUTE[] = zzUnpackAttribute();
	private static final String ZZ_ATTRIBUTE_PACKED_0 = "\003\000\001\t\003\001\002\t\007\001\006\t\001\001\001\t\001\000\001\001\003\000\006\001\b\t\001\000\001\t\002\001\001\000\006\001\001\000\004\001\001\000\001\001\001\000\001\t";
	private Reader zzReader;
	private int zzState;
	private int zzLexicalState;
	private char zzBuffer[];
	private int zzMarkedPos;
	private int zzCurrentPos;
	private int zzStartRead;
	private int zzEndRead;
	private boolean zzAtEOF;
	private StringBuffer sb;

	private static int[] zzUnpackAction()
	{
		int result[] = new int[63];
		int offset = 0;
		offset = zzUnpackAction("\003\000\001\001\001\002\001\003\001\001\001\004\001\005\001\006\006\003\001\007\001\b\001\t\001\n\001\013\001\f\001\r\001\016\001\000\001\r\003\000\006\003\001\017\001\020\001\021\001\022\001\023\001\024\001\025\001\026\001\000\001\027\002\030\001\000\006\003\001\000\001\003\001\031\001\032\001\003\001\000\001\033\001\000\001\034", offset, result);
		return result;
	}

	private static int zzUnpackAction(String packed, int offset, int result[])
	{
		int i = 0;
		int j = offset;
		for (int l = packed.length(); i < l;)
		{
			int count = packed.charAt(i++);
			int value = packed.charAt(i++);
			do
				result[j++] = value;
			while (--count > 0);
		}

		return j;
	}

	private static int[] zzUnpackRowMap()
	{
		int result[] = new int[63];
		int offset = 0;
		offset = zzUnpackRowMap("\000\000\000%\000J\000o\000\224\000\271\000\336\000o\000o\000?\000?\000身\000?\000?\000?\000?\000o\000o\000o\000o\000o\000o\000?\000o\000?\000?\000?\000?\000?\000?\000?\000?\000?\000?\000早\000o\000o\000o\000o\000o\000o\000o\000o\000?\000o\000?\000?\000?\000忌\000?\000?\000?\000?\000?\000?\000?\000\271\000\271\000?\000?\000\271\000?\000o", offset, result);
		return result;
	}

	private static int zzUnpackRowMap(String packed, int offset, int result[])
	{
		int i = 0;
		int j = offset;
		for (int l = packed.length(); i < l;)
		{
			int high = packed.charAt(i++) << 16;
			result[j++] = high | packed.charAt(i++);
		}

		return j;
	}

	private static int[] zzUnpackAttribute()
	{
		int result[] = new int[63];
		int offset = 0;
		offset = zzUnpackAttribute("\003\000\001\t\003\001\002\t\007\001\006\t\001\001\001\t\001\000\001\001\003\000\006\001\b\t\001\000\001\t\002\001\001\000\006\001\001\000\004\001\001\000\001\001\001\000\001\t", offset, result);
		return result;
	}

	private static int zzUnpackAttribute(String packed, int offset, int result[])
	{
		int i = 0;
		int j = offset;
		for (int l = packed.length(); i < l;)
		{
			int count = packed.charAt(i++);
			int value = packed.charAt(i++);
			do
				result[j++] = value;
			while (--count > 0);
		}

		return j;
	}

	Yylex(Reader in)
	{
		zzLexicalState = 0;
		zzBuffer = new char[16384];
		zzReader = in;
	}

	Yylex(InputStream in)
	{
		this(((Reader) (new InputStreamReader(in))));
	}

	private static char[] zzUnpackCMap(String packed)
	{
		char map[] = new char[0x10000];
		int i = 0;
		int j = 0;
		while (i < 122) 
		{
			int count = packed.charAt(i++);
			char value = packed.charAt(i++);
			do
				map[j++] = value;
			while (--count > 0);
		}
		return map;
	}

	private boolean zzRefill()
		throws IOException
	{
		if (zzStartRead > 0)
		{
			System.arraycopy(zzBuffer, zzStartRead, zzBuffer, 0, zzEndRead - zzStartRead);
			zzEndRead -= zzStartRead;
			zzCurrentPos -= zzStartRead;
			zzMarkedPos -= zzStartRead;
			zzStartRead = 0;
		}
		if (zzCurrentPos >= zzBuffer.length)
		{
			char newBuffer[] = new char[zzCurrentPos * 2];
			System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
			zzBuffer = newBuffer;
		}
		int numRead = zzReader.read(zzBuffer, zzEndRead, zzBuffer.length - zzEndRead);
		if (numRead > 0)
		{
			zzEndRead += numRead;
			return false;
		}
		if (numRead == 0)
		{
			int c = zzReader.read();
			if (c == -1)
			{
				return true;
			} else
			{
				zzBuffer[zzEndRead++] = (char)c;
				return false;
			}
		} else
		{
			return true;
		}
	}

	public final void yyclose()
		throws IOException
	{
		zzAtEOF = true;
		zzEndRead = zzStartRead;
		if (zzReader != null)
			zzReader.close();
	}

	public final void yyreset(Reader reader)
	{
		zzReader = reader;
		zzAtEOF = false;
		zzEndRead = zzStartRead = 0;
		zzCurrentPos = zzMarkedPos = 0;
		zzLexicalState = 0;
	}

	public final int yystate()
	{
		return zzLexicalState;
	}

	public final void yybegin(int newState)
	{
		zzLexicalState = newState;
	}

	public final String yytext()
	{
		return new String(zzBuffer, zzStartRead, zzMarkedPos - zzStartRead);
	}

	public final char yycharat(int pos)
	{
		return zzBuffer[zzStartRead + pos];
	}

	public final int yylength()
	{
		return zzMarkedPos - zzStartRead;
	}

	private void zzScanError(int errorCode)
	{
		String message;
		try
		{
			message = ZZ_ERROR_MSG[errorCode];
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			message = ZZ_ERROR_MSG[0];
		}
		throw new Error(message);
	}

	public void yypushback(int number)
	{
		if (number > yylength())
			zzScanError(2);
		zzMarkedPos -= number;
	}

	public JSONToken yylex()
		throws IOException, ParseException
	{
		int zzEndReadL = zzEndRead;
		char zzBufferL[] = zzBuffer;
		char zzCMapL[] = ZZ_CMAP;
		int zzTransL[] = ZZ_TRANS;
		int zzRowMapL[] = ZZ_ROWMAP;
		int zzAttrL[] = ZZ_ATTRIBUTE;
		do
		{
			int zzMarkedPosL = zzMarkedPos;
			int zzAction = -1;
			int zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
			zzState = ZZ_LEXSTATE[zzLexicalState];
			int zzInput;
			int zzAttributes;
label0:
			do
			{
				do
				{
					if (zzCurrentPosL < zzEndReadL)
					{
						zzInput = zzBufferL[zzCurrentPosL++];
					} else
					{
						if (zzAtEOF)
						{
							zzInput = -1;
							break label0;
						}
						zzCurrentPos = zzCurrentPosL;
						zzMarkedPos = zzMarkedPosL;
						boolean eof = zzRefill();
						zzCurrentPosL = zzCurrentPos;
						zzMarkedPosL = zzMarkedPos;
						zzBufferL = zzBuffer;
						zzEndReadL = zzEndRead;
						if (eof)
						{
							zzInput = -1;
							break label0;
						}
						zzInput = zzBufferL[zzCurrentPosL++];
					}
					int zzNext = zzTransL[zzRowMapL[zzState] + zzCMapL[zzInput]];
					if (zzNext == -1)
						break label0;
					zzState = zzNext;
					zzAttributes = zzAttrL[zzState];
				} while ((zzAttributes & 1) != 1);
				zzAction = zzState;
				zzMarkedPosL = zzCurrentPosL;
			} while ((zzAttributes & 8) != 8);
			zzMarkedPos = zzMarkedPosL;
			switch (zzAction >= 0 ? ZZ_ACTION[zzAction] : zzAction)
			{
			case 25: // '\031'
			{
				return new JSONToken(16, null);
			}

			case 13: // '\r'
			{
				sb.append(yytext());
				break;
			}

			case 18: // '\022'
			{
				sb.append('\b');
				break;
			}

			case 9: // '\t'
			{
				return new JSONToken(3);
			}

			case 2: // '\002'
			{
				Long val = Long.valueOf(yytext());
				return new JSONToken(18, val);
			}

			case 16: // '\020'
			{
				sb.append('\\');
				break;
			}

			case 8: // '\b'
			{
				return new JSONToken(4);
			}

			case 26: // '\032'
			{
				return new JSONToken(17, Boolean.TRUE);
			}

			case 23: // '\027'
			{
				sb.append('\'');
				break;
			}

			case 5: // '\005'
			{
				sb = new StringBuffer();
				yybegin(4);
				break;
			}

			case 27: // '\033'
			{
				return new JSONToken(17, Boolean.FALSE);
			}

			case 12: // '\f'
			{
				return new JSONToken(7);
			}

			case 21: // '\025'
			{
				sb.append('\r');
				break;
			}

			case 3: // '\003'
			{
				return new JSONToken(1, yytext());
			}

			case 28: // '\034'
			{
				try
				{
					sb.append((char)Integer.parseInt(yytext().substring(2), 16));
				}
				catch (Exception e)
				{
					throw new ParseException(e.getMessage());
				}
				break;
			}

			case 10: // '\n'
			{
				return new JSONToken(5);
			}

			case 17: // '\021'
			{
				sb.append('/');
				break;
			}

			case 11: // '\013'
			{
				return new JSONToken(6);
			}

			case 15: // '\017'
			{
				sb.append('"');
				break;
			}

			case 24: // '\030'
			{
				Double val = Double.valueOf(yytext());
				return new JSONToken(19, val);
			}

			case 1: // '\001'
			{
				throw new ParseException((new StringBuilder()).append("Unexpected char [").append(yytext()).append("]").toString());
			}

			case 19: // '\023'
			{
				sb.append('\f');
				break;
			}

			case 7: // '\007'
			{
				return new JSONToken(2);
			}

			case 14: // '\016'
			{
				yybegin(0);
				return new JSONToken(20, sb.toString());
			}

			case 22: // '\026'
			{
				sb.append('\t');
				break;
			}

			case 4: // '\004'
			{
				sb = new StringBuffer();
				yybegin(2);
				break;
			}

			case 20: // '\024'
			{
				sb.append('\n');
				break;
			}

			default:
			{
				if (zzInput == -1 && zzStartRead == zzCurrentPos)
				{
					zzAtEOF = true;
					return null;
				}
				zzScanError(1);
				break;
			}

			case 6: // '\006'
			case 29: // '\035'
			case 30: // '\036'
			case 31: // '\037'
			case 32: // ' '
			case 33: // '!'
			case 34: // '"'
			case 35: // '#'
			case 36: // '$'
			case 37: // '%'
			case 38: // '&'
			case 39: // '\''
			case 40: // '('
			case 41: // ')'
			case 42: // '*'
			case 43: // '+'
			case 44: // ','
			case 45: // '-'
			case 46: // '.'
			case 47: // '/'
			case 48: // '0'
			case 49: // '1'
			case 50: // '2'
			case 51: // '3'
			case 52: // '4'
			case 53: // '5'
			case 54: // '6'
			case 55: // '7'
			case 56: // '8'
				break;
			}
		} while (true);
	}

}
