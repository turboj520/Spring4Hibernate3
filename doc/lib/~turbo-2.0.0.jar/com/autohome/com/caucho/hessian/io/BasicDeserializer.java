// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   BasicDeserializer.java

package com.autohome.com.caucho.hessian.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

// Referenced classes of package com.autohome.com.caucho.hessian.io:
//			AbstractDeserializer, AbstractHessianInput

public class BasicDeserializer extends AbstractDeserializer
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
	private int _code;

	public BasicDeserializer(int code)
	{
		_code = code;
	}

	public Class getType()
	{
		switch (_code)
		{
		case 0: // '\0'
			return Void.TYPE;

		case 1: // '\001'
			return java/lang/Boolean;

		case 2: // '\002'
			return java/lang/Byte;

		case 3: // '\003'
			return java/lang/Short;

		case 4: // '\004'
			return java/lang/Integer;

		case 5: // '\005'
			return java/lang/Long;

		case 6: // '\006'
			return java/lang/Float;

		case 7: // '\007'
			return java/lang/Double;

		case 8: // '\b'
			return java/lang/Character;

		case 9: // '\t'
			return java/lang/Character;

		case 10: // '\n'
			return java/lang/String;

		case 11: // '\013'
			return java/util/Date;

		case 12: // '\f'
			return java/lang/Number;

		case 13: // '\r'
			return java/lang/Object;

		case 14: // '\016'
			return [Z;

		case 15: // '\017'
			return [B;

		case 16: // '\020'
			return [S;

		case 17: // '\021'
			return [I;

		case 18: // '\022'
			return [J;

		case 19: // '\023'
			return [F;

		case 20: // '\024'
			return [D;

		case 21: // '\025'
			return [C;

		case 22: // '\026'
			return [Ljava/lang/String;;

		case 23: // '\027'
			return [Ljava/lang/Object;;
		}
		throw new UnsupportedOperationException();
	}

	public Object readObject(AbstractHessianInput in)
		throws IOException
	{
		switch (_code)
		{
		case 0: // '\0'
		{
			in.readObject();
			return null;
		}

		case 1: // '\001'
		{
			return Boolean.valueOf(in.readBoolean());
		}

		case 2: // '\002'
		{
			return Byte.valueOf((byte)in.readInt());
		}

		case 3: // '\003'
		{
			return Short.valueOf((short)in.readInt());
		}

		case 4: // '\004'
		{
			return Integer.valueOf(in.readInt());
		}

		case 5: // '\005'
		{
			return Long.valueOf(in.readLong());
		}

		case 6: // '\006'
		{
			return Float.valueOf((float)in.readDouble());
		}

		case 7: // '\007'
		{
			return Double.valueOf(in.readDouble());
		}

		case 10: // '\n'
		{
			return in.readString();
		}

		case 13: // '\r'
		{
			return in.readObject();
		}

		case 8: // '\b'
		{
			String s = in.readString();
			if (s == null || s.equals(""))
				return Character.valueOf('\0');
			else
				return Character.valueOf(s.charAt(0));
		}

		case 9: // '\t'
		{
			String s = in.readString();
			if (s == null || s.equals(""))
				return null;
			else
				return Character.valueOf(s.charAt(0));
		}

		case 11: // '\013'
		{
			return new Date(in.readUTCDate());
		}

		case 12: // '\f'
		{
			return in.readObject();
		}

		case 15: // '\017'
		{
			return in.readBytes();
		}

		case 21: // '\025'
		{
			String s = in.readString();
			if (s == null)
			{
				return null;
			} else
			{
				int len = s.length();
				char chars[] = new char[len];
				s.getChars(0, len, chars, 0);
				return chars;
			}
		}

		case 14: // '\016'
		case 16: // '\020'
		case 17: // '\021'
		case 18: // '\022'
		case 19: // '\023'
		case 20: // '\024'
		case 22: // '\026'
		{
			int code = in.readListStart();
			switch (code)
			{
			case 78: // 'N'
			{
				return null;
			}

			case 16: // '\020'
			case 17: // '\021'
			case 18: // '\022'
			case 19: // '\023'
			case 20: // '\024'
			case 21: // '\025'
			case 22: // '\026'
			case 23: // '\027'
			case 24: // '\030'
			case 25: // '\031'
			case 26: // '\032'
			case 27: // '\033'
			case 28: // '\034'
			case 29: // '\035'
			case 30: // '\036'
			case 31: // '\037'
			{
				int length = code - 16;
				in.readInt();
				return readLengthList(in, length);
			}

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
			case 57: // '9'
			case 58: // ':'
			case 59: // ';'
			case 60: // '<'
			case 61: // '='
			case 62: // '>'
			case 63: // '?'
			case 64: // '@'
			case 65: // 'A'
			case 66: // 'B'
			case 67: // 'C'
			case 68: // 'D'
			case 69: // 'E'
			case 70: // 'F'
			case 71: // 'G'
			case 72: // 'H'
			case 73: // 'I'
			case 74: // 'J'
			case 75: // 'K'
			case 76: // 'L'
			case 77: // 'M'
			default:
			{
				String type = in.readType();
				int length = in.readLength();
				return readList(in, length);
			}
			}
		}
		}
		throw new UnsupportedOperationException();
	}

	public Object readList(AbstractHessianInput in, int length)
		throws IOException
	{
		switch (_code)
		{
		case 14: // '\016'
		{
			if (length >= 0)
			{
				boolean data[] = new boolean[length];
				in.addRef(data);
				for (int i = 0; i < data.length; i++)
					data[i] = in.readBoolean();

				in.readEnd();
				return data;
			}
			ArrayList list = new ArrayList();
			for (; !in.isEnd(); list.add(Boolean.valueOf(in.readBoolean())));
			in.readEnd();
			boolean data[] = new boolean[list.size()];
			in.addRef(data);
			for (int i = 0; i < data.length; i++)
				data[i] = ((Boolean)list.get(i)).booleanValue();

			return data;
		}

		case 16: // '\020'
		{
			if (length >= 0)
			{
				short data[] = new short[length];
				in.addRef(data);
				for (int i = 0; i < data.length; i++)
					data[i] = (short)in.readInt();

				in.readEnd();
				return data;
			}
			ArrayList list = new ArrayList();
			for (; !in.isEnd(); list.add(Short.valueOf((short)in.readInt())));
			in.readEnd();
			short data[] = new short[list.size()];
			for (int i = 0; i < data.length; i++)
				data[i] = ((Short)list.get(i)).shortValue();

			in.addRef(data);
			return data;
		}

		case 17: // '\021'
		{
			if (length >= 0)
			{
				int data[] = new int[length];
				in.addRef(data);
				for (int i = 0; i < data.length; i++)
					data[i] = in.readInt();

				in.readEnd();
				return data;
			}
			ArrayList list = new ArrayList();
			for (; !in.isEnd(); list.add(Integer.valueOf(in.readInt())));
			in.readEnd();
			int data[] = new int[list.size()];
			for (int i = 0; i < data.length; i++)
				data[i] = ((Integer)list.get(i)).intValue();

			in.addRef(data);
			return data;
		}

		case 18: // '\022'
		{
			if (length >= 0)
			{
				long data[] = new long[length];
				in.addRef(data);
				for (int i = 0; i < data.length; i++)
					data[i] = in.readLong();

				in.readEnd();
				return data;
			}
			ArrayList list = new ArrayList();
			for (; !in.isEnd(); list.add(Long.valueOf(in.readLong())));
			in.readEnd();
			long data[] = new long[list.size()];
			for (int i = 0; i < data.length; i++)
				data[i] = ((Long)list.get(i)).longValue();

			in.addRef(data);
			return data;
		}

		case 19: // '\023'
		{
			if (length >= 0)
			{
				float data[] = new float[length];
				in.addRef(data);
				for (int i = 0; i < data.length; i++)
					data[i] = (float)in.readDouble();

				in.readEnd();
				return data;
			}
			ArrayList list = new ArrayList();
			for (; !in.isEnd(); list.add(new Float(in.readDouble())));
			in.readEnd();
			float data[] = new float[list.size()];
			for (int i = 0; i < data.length; i++)
				data[i] = ((Float)list.get(i)).floatValue();

			in.addRef(data);
			return data;
		}

		case 20: // '\024'
		{
			if (length >= 0)
			{
				double data[] = new double[length];
				in.addRef(data);
				for (int i = 0; i < data.length; i++)
					data[i] = in.readDouble();

				in.readEnd();
				return data;
			}
			ArrayList list = new ArrayList();
			for (; !in.isEnd(); list.add(new Double(in.readDouble())));
			in.readEnd();
			double data[] = new double[list.size()];
			in.addRef(data);
			for (int i = 0; i < data.length; i++)
				data[i] = ((Double)list.get(i)).doubleValue();

			return data;
		}

		case 22: // '\026'
		{
			if (length >= 0)
			{
				String data[] = new String[length];
				in.addRef(data);
				for (int i = 0; i < data.length; i++)
					data[i] = in.readString();

				in.readEnd();
				return data;
			}
			ArrayList list = new ArrayList();
			for (; !in.isEnd(); list.add(in.readString()));
			in.readEnd();
			String data[] = new String[list.size()];
			in.addRef(data);
			for (int i = 0; i < data.length; i++)
				data[i] = (String)list.get(i);

			return data;
		}

		case 23: // '\027'
		{
			if (length >= 0)
			{
				Object data[] = new Object[length];
				in.addRef(((Object) (data)));
				for (int i = 0; i < data.length; i++)
					data[i] = in.readObject();

				in.readEnd();
				return ((Object) (data));
			}
			ArrayList list = new ArrayList();
			in.addRef(list);
			for (; !in.isEnd(); list.add(in.readObject()));
			in.readEnd();
			Object data[] = new Object[list.size()];
			for (int i = 0; i < data.length; i++)
				data[i] = list.get(i);

			return ((Object) (data));
		}

		case 15: // '\017'
		case 21: // '\025'
		default:
		{
			throw new UnsupportedOperationException(String.valueOf(this));
		}
		}
	}

	public Object readLengthList(AbstractHessianInput in, int length)
		throws IOException
	{
		switch (_code)
		{
		case 14: // '\016'
		{
			boolean data[] = new boolean[length];
			in.addRef(data);
			for (int i = 0; i < data.length; i++)
				data[i] = in.readBoolean();

			return data;
		}

		case 16: // '\020'
		{
			short data[] = new short[length];
			in.addRef(data);
			for (int i = 0; i < data.length; i++)
				data[i] = (short)in.readInt();

			return data;
		}

		case 17: // '\021'
		{
			int data[] = new int[length];
			in.addRef(data);
			for (int i = 0; i < data.length; i++)
				data[i] = in.readInt();

			return data;
		}

		case 18: // '\022'
		{
			long data[] = new long[length];
			in.addRef(data);
			for (int i = 0; i < data.length; i++)
				data[i] = in.readLong();

			return data;
		}

		case 19: // '\023'
		{
			float data[] = new float[length];
			in.addRef(data);
			for (int i = 0; i < data.length; i++)
				data[i] = (float)in.readDouble();

			return data;
		}

		case 20: // '\024'
		{
			double data[] = new double[length];
			in.addRef(data);
			for (int i = 0; i < data.length; i++)
				data[i] = in.readDouble();

			return data;
		}

		case 22: // '\026'
		{
			String data[] = new String[length];
			in.addRef(data);
			for (int i = 0; i < data.length; i++)
				data[i] = in.readString();

			return data;
		}

		case 23: // '\027'
		{
			Object data[] = new Object[length];
			in.addRef(((Object) (data)));
			for (int i = 0; i < data.length; i++)
				data[i] = in.readObject();

			return ((Object) (data));
		}

		case 15: // '\017'
		case 21: // '\025'
		default:
		{
			throw new UnsupportedOperationException(String.valueOf(this));
		}
		}
	}
}
