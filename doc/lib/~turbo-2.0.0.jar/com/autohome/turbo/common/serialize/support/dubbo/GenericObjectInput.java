// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   GenericObjectInput.java

package com.autohome.turbo.common.serialize.support.dubbo;

import com.autohome.turbo.common.serialize.ObjectInput;
import com.autohome.turbo.common.utils.ReflectUtils;
import com.autohome.turbo.common.utils.StringUtils;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

// Referenced classes of package com.autohome.turbo.common.serialize.support.dubbo:
//			GenericDataInput, Builder, ClassDescriptorMapper

public class GenericObjectInput extends GenericDataInput
	implements ObjectInput
{

	private static Object SKIPPED_OBJECT = new Object();
	private ClassDescriptorMapper mMapper;
	private List mRefs;

	public GenericObjectInput(InputStream is)
	{
		this(is, Builder.DEFAULT_CLASS_DESCRIPTOR_MAPPER);
	}

	public GenericObjectInput(InputStream is, ClassDescriptorMapper mapper)
	{
		super(is);
		mRefs = new ArrayList();
		mMapper = mapper;
	}

	public GenericObjectInput(InputStream is, int buffSize)
	{
		this(is, buffSize, Builder.DEFAULT_CLASS_DESCRIPTOR_MAPPER);
	}

	public GenericObjectInput(InputStream is, int buffSize, ClassDescriptorMapper mapper)
	{
		super(is, buffSize);
		mRefs = new ArrayList();
		mMapper = mapper;
	}

	public Object readObject()
		throws IOException
	{
		String desc;
		byte b = read0();
		switch (b)
		{
		case -108: 
			return null;

		case -107: 
			return new Object();

		case -118: 
			desc = readUTF();
			break;

		case -117: 
			int index = readUInt();
			desc = mMapper.getDescriptor(index);
			if (desc == null)
				throw new IOException((new StringBuilder()).append("Can not find desc id: ").append(index).toString());
			break;

		default:
			throw new IOException((new StringBuilder()).append("Flag error, expect OBJECT_NULL|OBJECT_DUMMY|OBJECT_DESC|OBJECT_DESC_ID, get ").append(b).toString());
		}
		Class c = ReflectUtils.desc2class(desc);
		return Builder.register(c).parseFrom(this);
		ClassNotFoundException e;
		e;
		throw new IOException((new StringBuilder()).append("Read object failed, class not found. ").append(StringUtils.toString(e)).toString());
	}

	public Object readObject(Class cls)
		throws IOException, ClassNotFoundException
	{
		return readObject();
	}

	public Object readObject(Class cls, Type type)
		throws IOException, ClassNotFoundException
	{
		return readObject();
	}

	public void addRef(Object obj)
	{
		mRefs.add(obj);
	}

	public Object getRef(int index)
		throws IOException
	{
		if (index < 0 || index >= mRefs.size())
			return null;
		Object ret = mRefs.get(index);
		if (ret == SKIPPED_OBJECT)
			throw new IOException("Ref skipped-object.");
		else
			return ret;
	}

	public void skipAny()
		throws IOException
	{
		byte b = read0();
		switch (b)
		{
		case -108: 
		case -107: 
		case 10: // '\n'
		case 11: // '\013'
		case 12: // '\f'
		case 13: // '\r'
		case 14: // '\016'
		case 15: // '\017'
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

		case 0: // '\0'
		{
			read0();
			break;
		}

		case 1: // '\001'
		{
			read0();
			read0();
			break;
		}

		case 2: // '\002'
		{
			read0();
			read0();
			read0();
			break;
		}

		case 3: // '\003'
		{
			read0();
			read0();
			read0();
			read0();
			break;
		}

		case 4: // '\004'
		{
			read0();
			read0();
			read0();
			read0();
			read0();
			break;
		}

		case 5: // '\005'
		{
			read0();
			read0();
			read0();
			read0();
			read0();
			read0();
			break;
		}

		case 6: // '\006'
		{
			read0();
			read0();
			read0();
			read0();
			read0();
			read0();
			read0();
			break;
		}

		case 7: // '\007'
		{
			read0();
			read0();
			read0();
			read0();
			read0();
			read0();
			read0();
			read0();
			break;
		}

		case -128: 
		{
			addRef(SKIPPED_OBJECT);
			int len = readUInt();
			for (int i = 0; i < len; i++)
				skipAny();

			break;
		}

		case -127: 
		{
			readUInt();
			break;
		}

		case -126: 
		case -125: 
		{
			read0(readUInt());
			break;
		}

		case -124: 
		{
			skipAny();
			break;
		}

		case -123: 
		{
			int len = readUInt();
			for (int i = 0; i < len; i++)
				skipAny();

			break;
		}

		case -122: 
		{
			int len = readUInt();
			for (int i = 0; i < len; i++)
			{
				skipAny();
				skipAny();
			}

			break;
		}

		case -118: 
		{
			readUTF();
			int len = readUInt();
			for (int i = 0; i < len; i++)
				skipAny();

			break;
		}

		case -117: 
		{
			readUInt();
			int len = readUInt();
			for (int i = 0; i < len; i++)
				skipAny();

			break;
		}

		case -121: 
		case -120: 
		case -119: 
		case -116: 
		case -115: 
		case -114: 
		case -113: 
		case -112: 
		case -111: 
		case -110: 
		case -109: 
		case -106: 
		case -105: 
		case -104: 
		case -103: 
		case -102: 
		case -101: 
		case -100: 
		case -99: 
		case -98: 
		case -97: 
		case -96: 
		case -95: 
		case -94: 
		case -93: 
		case -92: 
		case -91: 
		case -90: 
		case -89: 
		case -88: 
		case -87: 
		case -86: 
		case -85: 
		case -84: 
		case -83: 
		case -82: 
		case -81: 
		case -80: 
		case -79: 
		case -78: 
		case -77: 
		case -76: 
		case -75: 
		case -74: 
		case -73: 
		case -72: 
		case -71: 
		case -70: 
		case -69: 
		case -68: 
		case -67: 
		case -66: 
		case -65: 
		case -64: 
		case -63: 
		case -62: 
		case -61: 
		case -60: 
		case -59: 
		case -58: 
		case -57: 
		case -56: 
		case -55: 
		case -54: 
		case -53: 
		case -52: 
		case -51: 
		case -50: 
		case -49: 
		case -48: 
		case -47: 
		case -46: 
		case -45: 
		case -44: 
		case -43: 
		case -42: 
		case -41: 
		case -40: 
		case -39: 
		case -38: 
		case -37: 
		case -36: 
		case -35: 
		case -34: 
		case -33: 
		case -32: 
		case -31: 
		case -30: 
		case -29: 
		case -28: 
		case -27: 
		case -26: 
		case -25: 
		case -24: 
		case -23: 
		case -22: 
		case -21: 
		case -20: 
		case -19: 
		case -18: 
		case -17: 
		case -16: 
		case -15: 
		case -14: 
		case -13: 
		case -12: 
		case -11: 
		case -10: 
		case -9: 
		case -8: 
		case -7: 
		case -6: 
		case -5: 
		case -4: 
		case -3: 
		case -2: 
		case -1: 
		case 8: // '\b'
		case 9: // '\t'
		default:
		{
			throw new IOException((new StringBuilder()).append("Flag error, get ").append(b).toString());
		}
		}
	}

}
