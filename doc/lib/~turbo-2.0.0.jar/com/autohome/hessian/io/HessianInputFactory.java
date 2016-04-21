// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HessianInputFactory.java

package com.autohome.hessian.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

// Referenced classes of package com.autohome.hessian.io:
//			HessianFactory, SerializerFactory, AbstractHessianInput

public class HessianInputFactory
{
	public static final class HeaderType extends Enum
	{

		public static final HeaderType CALL_1_REPLY_1;
		public static final HeaderType CALL_1_REPLY_2;
		public static final HeaderType HESSIAN_2;
		public static final HeaderType REPLY_1;
		public static final HeaderType REPLY_2;
		private static final HeaderType $VALUES[];

		public static HeaderType[] values()
		{
			return (HeaderType[])$VALUES.clone();
		}

		public static HeaderType valueOf(String name)
		{
			return (HeaderType)Enum.valueOf(com/autohome/hessian/io/HessianInputFactory$HeaderType, name);
		}

		public boolean isCall1()
		{
			static class 1
			{

				static final int $SwitchMap$com$autohome$hessian$io$HessianInputFactory$HeaderType[];

				static 
				{
					$SwitchMap$com$autohome$hessian$io$HessianInputFactory$HeaderType = new int[HeaderType.values().length];
					try
					{
						$SwitchMap$com$autohome$hessian$io$HessianInputFactory$HeaderType[HeaderType.CALL_1_REPLY_1.ordinal()] = 1;
					}
					catch (NoSuchFieldError ex) { }
					try
					{
						$SwitchMap$com$autohome$hessian$io$HessianInputFactory$HeaderType[HeaderType.CALL_1_REPLY_2.ordinal()] = 2;
					}
					catch (NoSuchFieldError ex) { }
					try
					{
						$SwitchMap$com$autohome$hessian$io$HessianInputFactory$HeaderType[HeaderType.HESSIAN_2.ordinal()] = 3;
					}
					catch (NoSuchFieldError ex) { }
				}
			}

			switch (1..SwitchMap.com.autohome.hessian.io.HessianInputFactory.HeaderType[ordinal()])
			{
			case 1: // '\001'
			case 2: // '\002'
				return true;
			}
			return false;
		}

		public boolean isCall2()
		{
			switch (1..SwitchMap.com.autohome.hessian.io.HessianInputFactory.HeaderType[ordinal()])
			{
			case 3: // '\003'
				return true;
			}
			return false;
		}

		public boolean isReply1()
		{
			switch (1..SwitchMap.com.autohome.hessian.io.HessianInputFactory.HeaderType[ordinal()])
			{
			case 1: // '\001'
				return true;
			}
			return false;
		}

		public boolean isReply2()
		{
			switch (1..SwitchMap.com.autohome.hessian.io.HessianInputFactory.HeaderType[ordinal()])
			{
			case 2: // '\002'
			case 3: // '\003'
				return true;
			}
			return false;
		}

		static 
		{
			CALL_1_REPLY_1 = new HeaderType("CALL_1_REPLY_1", 0);
			CALL_1_REPLY_2 = new HeaderType("CALL_1_REPLY_2", 1);
			HESSIAN_2 = new HeaderType("HESSIAN_2", 2);
			REPLY_1 = new HeaderType("REPLY_1", 3);
			REPLY_2 = new HeaderType("REPLY_2", 4);
			$VALUES = (new HeaderType[] {
				CALL_1_REPLY_1, CALL_1_REPLY_2, HESSIAN_2, REPLY_1, REPLY_2
			});
		}

		private HeaderType(String s, int i)
		{
			super(s, i);
		}
	}


	public static final Logger log = Logger.getLogger(com/autohome/hessian/io/HessianInputFactory.getName());
	private HessianFactory _factory;

	public HessianInputFactory()
	{
		_factory = new HessianFactory();
	}

	public void setSerializerFactory(SerializerFactory factory)
	{
		_factory.setSerializerFactory(factory);
	}

	public SerializerFactory getSerializerFactory()
	{
		return _factory.getSerializerFactory();
	}

	public HeaderType readHeader(InputStream is)
		throws IOException
	{
		int code = is.read();
		int major = is.read();
		int minor = is.read();
		switch (code)
		{
		case -1: 
			throw new IOException("Unexpected end of file for Hessian message");

		case 99: // 'c'
			if (major >= 2)
				return HeaderType.CALL_1_REPLY_2;
			else
				return HeaderType.CALL_1_REPLY_1;

		case 114: // 'r'
			return HeaderType.REPLY_1;

		case 72: // 'H'
			return HeaderType.HESSIAN_2;
		}
		throw new IOException((new StringBuilder()).append((char)code).append(" 0x").append(Integer.toHexString(code)).append(" is an unknown Hessian message code.").toString());
	}

	public AbstractHessianInput open(InputStream is)
		throws IOException
	{
		int code = is.read();
		int major = is.read();
		int minor = is.read();
		switch (code)
		{
		case 67: // 'C'
		case 82: // 'R'
		case 99: // 'c'
		case 114: // 'r'
			if (major >= 2)
				return _factory.createHessian2Input(is);
			else
				return _factory.createHessianInput(is);
		}
		throw new IOException((new StringBuilder()).append((char)code).append(" is an unknown Hessian message code.").toString());
	}

}
