// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   BasicSerializer.java

package com.autohome.hessian.io;

import java.io.IOException;
import java.util.Date;

// Referenced classes of package com.autohome.hessian.io:
//			AbstractSerializer, ByteHandle, ShortHandle, FloatHandle, 
//			ObjectSerializer, AbstractHessianOutput, ObjectHandleSerializer, Serializer

public class BasicSerializer extends AbstractSerializer
	implements ObjectSerializer
{

	public static final int NULL = 0;
	public static final int BOOLEAN = 1;
	public static final int BYTE = 2;
	public static final int SHORT = 3;
	public static final int INTEGER = 4;
	public static final int LONG = 5;
	public static final int FLOAT = 6;
	public static final int DOUBLE = 7;
	public static final int CHARACTER = 8;
	public static final int CHARACTER_OBJECT = 9;
	public static final int STRING = 10;
	public static final int DATE = 11;
	public static final int NUMBER = 12;
	public static final int OBJECT = 13;
	public static final int BOOLEAN_ARRAY = 14;
	public static final int BYTE_ARRAY = 15;
	public static final int SHORT_ARRAY = 16;
	public static final int INTEGER_ARRAY = 17;
	public static final int LONG_ARRAY = 18;
	public static final int FLOAT_ARRAY = 19;
	public static final int DOUBLE_ARRAY = 20;
	public static final int CHARACTER_ARRAY = 21;
	public static final int STRING_ARRAY = 22;
	public static final int OBJECT_ARRAY = 23;
	public static final int BYTE_HANDLE = 24;
	public static final int SHORT_HANDLE = 25;
	public static final int FLOAT_HANDLE = 26;
	private static final BasicSerializer BYTE_HANDLE_SERIALIZER = new BasicSerializer(24);
	private static final BasicSerializer SHORT_HANDLE_SERIALIZER = new BasicSerializer(25);
	private static final BasicSerializer FLOAT_HANDLE_SERIALIZER = new BasicSerializer(26);
	private int _code;

	public BasicSerializer(int code)
	{
		_code = code;
	}

	public Serializer getObjectSerializer()
	{
		switch (_code)
		{
		case 2: // '\002'
			return BYTE_HANDLE_SERIALIZER;

		case 3: // '\003'
			return SHORT_HANDLE_SERIALIZER;

		case 6: // '\006'
			return FLOAT_HANDLE_SERIALIZER;

		case 4: // '\004'
		case 5: // '\005'
		default:
			return this;
		}
	}

	public void writeObject(Object obj, AbstractHessianOutput out)
		throws IOException
	{
		switch (_code)
		{
		case 1: // '\001'
		{
			out.writeBoolean(((Boolean)obj).booleanValue());
			break;
		}

		case 2: // '\002'
		case 3: // '\003'
		case 4: // '\004'
		{
			out.writeInt(((Number)obj).intValue());
			break;
		}

		case 5: // '\005'
		{
			out.writeLong(((Number)obj).longValue());
			break;
		}

		case 6: // '\006'
		case 7: // '\007'
		{
			out.writeDouble(((Number)obj).doubleValue());
			break;
		}

		case 8: // '\b'
		case 9: // '\t'
		{
			out.writeString(String.valueOf(obj));
			break;
		}

		case 10: // '\n'
		{
			out.writeString((String)obj);
			break;
		}

		case 11: // '\013'
		{
			out.writeUTCDate(((Date)obj).getTime());
			break;
		}

		case 14: // '\016'
		{
			if (out.addRef(obj))
				return;
			boolean data[] = (boolean[])(boolean[])obj;
			boolean hasEnd = out.writeListBegin(data.length, "[boolean");
			for (int i = 0; i < data.length; i++)
				out.writeBoolean(data[i]);

			if (hasEnd)
				out.writeListEnd();
			break;
		}

		case 15: // '\017'
		{
			byte data[] = (byte[])(byte[])obj;
			out.writeBytes(data, 0, data.length);
			break;
		}

		case 16: // '\020'
		{
			if (out.addRef(obj))
				return;
			short data[] = (short[])(short[])obj;
			boolean hasEnd = out.writeListBegin(data.length, "[short");
			for (int i = 0; i < data.length; i++)
				out.writeInt(data[i]);

			if (hasEnd)
				out.writeListEnd();
			break;
		}

		case 17: // '\021'
		{
			if (out.addRef(obj))
				return;
			int data[] = (int[])(int[])obj;
			boolean hasEnd = out.writeListBegin(data.length, "[int");
			for (int i = 0; i < data.length; i++)
				out.writeInt(data[i]);

			if (hasEnd)
				out.writeListEnd();
			break;
		}

		case 18: // '\022'
		{
			if (out.addRef(obj))
				return;
			long data[] = (long[])(long[])obj;
			boolean hasEnd = out.writeListBegin(data.length, "[long");
			for (int i = 0; i < data.length; i++)
				out.writeLong(data[i]);

			if (hasEnd)
				out.writeListEnd();
			break;
		}

		case 19: // '\023'
		{
			if (out.addRef(obj))
				return;
			float data[] = (float[])(float[])obj;
			boolean hasEnd = out.writeListBegin(data.length, "[float");
			for (int i = 0; i < data.length; i++)
				out.writeDouble(data[i]);

			if (hasEnd)
				out.writeListEnd();
			break;
		}

		case 20: // '\024'
		{
			if (out.addRef(obj))
				return;
			double data[] = (double[])(double[])obj;
			boolean hasEnd = out.writeListBegin(data.length, "[double");
			for (int i = 0; i < data.length; i++)
				out.writeDouble(data[i]);

			if (hasEnd)
				out.writeListEnd();
			break;
		}

		case 22: // '\026'
		{
			if (out.addRef(obj))
				return;
			String data[] = (String[])(String[])obj;
			boolean hasEnd = out.writeListBegin(data.length, "[string");
			for (int i = 0; i < data.length; i++)
				out.writeString(data[i]);

			if (hasEnd)
				out.writeListEnd();
			break;
		}

		case 21: // '\025'
		{
			char data[] = (char[])(char[])obj;
			out.writeString(data, 0, data.length);
			break;
		}

		case 23: // '\027'
		{
			if (out.addRef(obj))
				return;
			Object data[] = (Object[])(Object[])obj;
			boolean hasEnd = out.writeListBegin(data.length, "[object");
			for (int i = 0; i < data.length; i++)
				out.writeObject(data[i]);

			if (hasEnd)
				out.writeListEnd();
			break;
		}

		case 0: // '\0'
		{
			out.writeNull();
			break;
		}

		case 13: // '\r'
		{
			ObjectHandleSerializer.SER.writeObject(obj, out);
			break;
		}

		case 24: // '\030'
		{
			out.writeObject(new ByteHandle(((Byte)obj).byteValue()));
			break;
		}

		case 25: // '\031'
		{
			out.writeObject(new ShortHandle(((Short)obj).shortValue()));
			break;
		}

		case 26: // '\032'
		{
			out.writeObject(new FloatHandle(((Float)obj).floatValue()));
			break;
		}

		case 12: // '\f'
		default:
		{
			throw new RuntimeException((new StringBuilder()).append(_code).append(" unknown code for ").append(obj.getClass()).toString());
		}
		}
	}

}
