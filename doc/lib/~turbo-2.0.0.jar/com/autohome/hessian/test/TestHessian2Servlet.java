// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   TestHessian2Servlet.java

package com.autohome.hessian.test;

import com.autohome.hessian.io.HessianDebugInputStream;
import com.autohome.hessian.io.SerializerFactory;
import com.autohome.hessian.server.HessianServlet;
import java.io.*;
import java.util.*;

// Referenced classes of package com.autohome.hessian.test:
//			A0, A1, A2, A3, 
//			A4, A5, A6, A7, 
//			A8, A9, A10, A11, 
//			A12, A13, A14, A15, 
//			A16, TestObject, TestCons, TestHessian2

public class TestHessian2Servlet extends HessianServlet
	implements TestHessian2
{

	private ThreadLocal _threadWriter;

	public TestHessian2Servlet()
	{
		_threadWriter = new ThreadLocal();
	}

	public void methodNull()
	{
	}

	public void replyNull()
	{
	}

	public Object replyTrue()
	{
		return Boolean.valueOf(true);
	}

	public Object replyFalse()
	{
		return Boolean.valueOf(false);
	}

	public int replyInt_0()
	{
		return 0;
	}

	public int replyInt_1()
	{
		return 1;
	}

	public int replyInt_47()
	{
		return 47;
	}

	public int replyInt_m16()
	{
		return -16;
	}

	public int replyInt_0x30()
	{
		return 48;
	}

	public int replyInt_0x7ff()
	{
		return 2047;
	}

	public int replyInt_m17()
	{
		return -17;
	}

	public int replyInt_m0x800()
	{
		return -2048;
	}

	public int replyInt_0x800()
	{
		return 2048;
	}

	public int replyInt_0x3ffff()
	{
		return 0x3ffff;
	}

	public int replyInt_m0x801()
	{
		return -2049;
	}

	public int replyInt_m0x40000()
	{
		return 0xfffc0000;
	}

	public int replyInt_0x40000()
	{
		return 0x40000;
	}

	public int replyInt_0x7fffffff()
	{
		return 0x7fffffff;
	}

	public int replyInt_m0x40001()
	{
		return 0xfffbffff;
	}

	public int replyInt_m0x80000000()
	{
		return 0x80000000;
	}

	public long replyLong_0()
	{
		return 0L;
	}

	public long replyLong_1()
	{
		return 1L;
	}

	public long replyLong_15()
	{
		return 15L;
	}

	public long replyLong_m8()
	{
		return -8L;
	}

	public long replyLong_0x10()
	{
		return 16L;
	}

	public long replyLong_0x7ff()
	{
		return 2047L;
	}

	public long replyLong_m9()
	{
		return -9L;
	}

	public long replyLong_m0x800()
	{
		return -2048L;
	}

	public long replyLong_0x800()
	{
		return 2048L;
	}

	public long replyLong_0x3ffff()
	{
		return 0x3ffffL;
	}

	public long replyLong_m0x801()
	{
		return -2049L;
	}

	public long replyLong_m0x40000()
	{
		return 0xfffffffffffc0000L;
	}

	public long replyLong_0x40000()
	{
		return 0x40000L;
	}

	public long replyLong_0x7fffffff()
	{
		return 0x7fffffffL;
	}

	public long replyLong_m0x40001()
	{
		return 0xfffffffffffbffffL;
	}

	public long replyLong_m0x80000000()
	{
		return 0xffffffff80000000L;
	}

	public long replyLong_0x80000000()
	{
		return 0x80000000L;
	}

	public long replyLong_m0x80000001()
	{
		return 0xffffffff7fffffffL;
	}

	public double replyDouble_0_0()
	{
		return 0.0D;
	}

	public double replyDouble_1_0()
	{
		return 1.0D;
	}

	public double replyDouble_2_0()
	{
		return 2D;
	}

	public double replyDouble_127_0()
	{
		return 127D;
	}

	public double replyDouble_m128_0()
	{
		return -128D;
	}

	public double replyDouble_128_0()
	{
		return 128D;
	}

	public double replyDouble_m129_0()
	{
		return -129D;
	}

	public double replyDouble_32767_0()
	{
		return 32767D;
	}

	public double replyDouble_m32768_0()
	{
		return -32768D;
	}

	public double replyDouble_0_001()
	{
		return 0.001D;
	}

	public double replyDouble_m0_001()
	{
		return -0.001D;
	}

	public double replyDouble_65_536()
	{
		return 65.536000000000001D;
	}

	public double replyDouble_3_14159()
	{
		return 3.1415899999999999D;
	}

	public Object replyDate_0()
	{
		return new Date(0L);
	}

	public Object replyDate_1()
	{
		long time = 0xd04b9284b8L;
		return new Date(time);
	}

	public Object replyDate_2()
	{
		long time = 0xd04b9284b8L;
		time -= time % 60000L;
		return new Date(time);
	}

	public String replyString_0()
	{
		return "";
	}

	public String replyString_null()
	{
		return null;
	}

	public String replyString_1()
	{
		return "0";
	}

	public String replyString_31()
	{
		return "0123456789012345678901234567890";
	}

	public String replyString_32()
	{
		return "01234567890123456789012345678901";
	}

	public String replyString_1023()
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 16; i++)
			sb.append((new StringBuilder()).append("").append(i / 10).append(i % 10).append(" 456789012345678901234567890123456789012345678901234567890123\n").toString());

		sb.setLength(1023);
		return sb.toString();
	}

	public String replyString_1024()
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 16; i++)
			sb.append((new StringBuilder()).append("").append(i / 10).append(i % 10).append(" 456789012345678901234567890123456789012345678901234567890123\n").toString());

		sb.setLength(1024);
		return sb.toString();
	}

	public String replyString_65536()
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 1024; i++)
			sb.append((new StringBuilder()).append("").append(i / 100).append((i / 10) % 10).append(i % 10).append(" 56789012345678901234567890123456789012345678901234567890123\n").toString());

		sb.setLength(0x10000);
		return sb.toString();
	}

	public Object replyBinary_0()
	{
		return new byte[0];
	}

	public Object replyBinary_null()
	{
		return null;
	}

	public Object replyBinary_1()
	{
		return toBinary("0");
	}

	public Object replyBinary_15()
	{
		return toBinary("012345678901234");
	}

	public Object replyBinary_16()
	{
		return toBinary("0123456789012345");
	}

	public Object replyBinary_1023()
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 16; i++)
			sb.append((new StringBuilder()).append("").append(i / 10).append(i % 10).append(" 456789012345678901234567890123456789012345678901234567890123\n").toString());

		sb.setLength(1023);
		return toBinary(sb.toString());
	}

	public Object replyBinary_1024()
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 16; i++)
			sb.append((new StringBuilder()).append("").append(i / 10).append(i % 10).append(" 456789012345678901234567890123456789012345678901234567890123\n").toString());

		sb.setLength(1024);
		return toBinary(sb.toString());
	}

	public Object replyBinary_65536()
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 1024; i++)
			sb.append((new StringBuilder()).append("").append(i / 100).append((i / 10) % 10).append(i % 10).append(" 56789012345678901234567890123456789012345678901234567890123\n").toString());

		sb.setLength(0x10000);
		return toBinary(sb.toString());
	}

	private byte[] toBinary(String s)
	{
		byte buffer[] = new byte[s.length()];
		for (int i = 0; i < s.length(); i++)
			buffer[i] = (byte)s.charAt(i);

		return buffer;
	}

	public Object replyUntypedFixedList_0()
	{
		ArrayList list = new ArrayList();
		return list;
	}

	public Object replyUntypedFixedList_1()
	{
		ArrayList list = new ArrayList();
		list.add("1");
		return list;
	}

	public Object replyUntypedFixedList_7()
	{
		ArrayList list = new ArrayList();
		list.add("1");
		list.add("2");
		list.add("3");
		list.add("4");
		list.add("5");
		list.add("6");
		list.add("7");
		return list;
	}

	public Object replyUntypedFixedList_8()
	{
		ArrayList list = new ArrayList();
		list.add("1");
		list.add("2");
		list.add("3");
		list.add("4");
		list.add("5");
		list.add("6");
		list.add("7");
		list.add("8");
		return list;
	}

	public Object replyTypedFixedList_0()
	{
		return new String[0];
	}

	public Object replyTypedFixedList_1()
	{
		return (new String[] {
			"1"
		});
	}

	public Object replyTypedFixedList_7()
	{
		return (new String[] {
			"1", "2", "3", "4", "5", "6", "7"
		});
	}

	public Object replyTypedFixedList_8()
	{
		return (new String[] {
			"1", "2", "3", "4", "5", "6", "7", "8"
		});
	}

	public Object replyUntypedMap_0()
	{
		return new HashMap();
	}

	public Object replyUntypedMap_1()
	{
		HashMap map = new HashMap();
		map.put("a", Integer.valueOf(0));
		return map;
	}

	public Object replyUntypedMap_2()
	{
		HashMap map = new HashMap();
		map.put(Integer.valueOf(0), "a");
		map.put(Integer.valueOf(1), "b");
		return map;
	}

	public Object replyUntypedMap_3()
	{
		HashMap map = new HashMap();
		ArrayList list = new ArrayList();
		list.add("a");
		map.put(list, Integer.valueOf(0));
		return map;
	}

	public Object replyTypedMap_0()
	{
		Hashtable map = new Hashtable();
		return map;
	}

	public Object replyTypedMap_1()
	{
		Map map = new Hashtable();
		map.put("a", Integer.valueOf(0));
		return map;
	}

	public Object replyTypedMap_2()
	{
		Map map = new Hashtable();
		map.put(Integer.valueOf(0), "a");
		map.put(Integer.valueOf(1), "b");
		return map;
	}

	public Object replyTypedMap_3()
	{
		Map map = new Hashtable();
		ArrayList list = new ArrayList();
		list.add("a");
		map.put(list, Integer.valueOf(0));
		return map;
	}

	public Object replyObject_0()
	{
		return new A0();
	}

	public Object replyObject_16()
	{
		ArrayList list = new ArrayList();
		list.add(new A0());
		list.add(new A1());
		list.add(new A2());
		list.add(new A3());
		list.add(new A4());
		list.add(new A5());
		list.add(new A6());
		list.add(new A7());
		list.add(new A8());
		list.add(new A9());
		list.add(new A10());
		list.add(new A11());
		list.add(new A12());
		list.add(new A13());
		list.add(new A14());
		list.add(new A15());
		list.add(new A16());
		return list;
	}

	public Object replyObject_1()
	{
		return new TestObject(Integer.valueOf(0));
	}

	public Object replyObject_2()
	{
		ArrayList list = new ArrayList();
		list.add(new TestObject(Integer.valueOf(0)));
		list.add(new TestObject(Integer.valueOf(1)));
		return list;
	}

	public Object replyObject_2a()
	{
		ArrayList list = new ArrayList();
		TestObject obj = new TestObject(Integer.valueOf(0));
		list.add(obj);
		list.add(obj);
		return list;
	}

	public Object replyObject_2b()
	{
		ArrayList list = new ArrayList();
		list.add(new TestObject(Integer.valueOf(0)));
		list.add(new TestObject(Integer.valueOf(0)));
		return list;
	}

	public Object replyObject_3()
	{
		TestCons cons = new TestCons();
		cons.setFirst("a");
		cons.setRest(cons);
		return cons;
	}

	public Object argNull(Object v)
	{
		if (v == null)
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argTrue(Object v)
	{
		if (Boolean.TRUE.equals(v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argFalse(Object v)
	{
		if (Boolean.FALSE.equals(v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argInt_0(Object v)
	{
		if (v instanceof Integer)
		{
			Integer value = (Integer)v;
			if (value.intValue() == 0)
				return Boolean.valueOf(true);
		}
		return getInputDebug();
	}

	public Object argInt_1(Object v)
	{
		if (v instanceof Integer)
		{
			Integer value = (Integer)v;
			if (value.intValue() == 1)
				return Boolean.valueOf(true);
		}
		return getInputDebug();
	}

	public Object argInt_47(Object v)
	{
		if (v instanceof Integer)
		{
			Integer value = (Integer)v;
			if (value.intValue() == 47)
				return Boolean.valueOf(true);
		}
		return getInputDebug();
	}

	public Object argInt_m16(Object v)
	{
		if (v instanceof Integer)
		{
			Integer value = (Integer)v;
			if (value.intValue() == -16)
				return Boolean.valueOf(true);
		}
		return getInputDebug();
	}

	public Object argInt_0x30(Object v)
	{
		if (v instanceof Integer)
		{
			Integer value = (Integer)v;
			if (value.intValue() == 48)
				return Boolean.valueOf(true);
		}
		return getInputDebug();
	}

	public Object argInt_0x7ff(Object v)
	{
		if (v instanceof Integer)
		{
			Integer value = (Integer)v;
			if (value.intValue() == 2047)
				return Boolean.valueOf(true);
		}
		return getInputDebug();
	}

	public Object argInt_m17(Object v)
	{
		if (v instanceof Integer)
		{
			Integer value = (Integer)v;
			if (value.intValue() == -17)
				return Boolean.valueOf(true);
		}
		return getInputDebug();
	}

	public Object argInt_m0x800(Object v)
	{
		if (v instanceof Integer)
		{
			Integer value = (Integer)v;
			if (value.intValue() == -2048)
				return Boolean.valueOf(true);
		}
		return getInputDebug();
	}

	public Object argInt_0x800(Object v)
	{
		if (v instanceof Integer)
		{
			Integer value = (Integer)v;
			if (value.intValue() == 2048)
				return Boolean.valueOf(true);
		}
		return getInputDebug();
	}

	public Object argInt_0x3ffff(Object v)
	{
		if (v instanceof Integer)
		{
			Integer value = (Integer)v;
			if (value.intValue() == 0x3ffff)
				return Boolean.valueOf(true);
		}
		return getInputDebug();
	}

	public Object argInt_m0x801(Object v)
	{
		if (v instanceof Integer)
		{
			Integer value = (Integer)v;
			if (value.intValue() == -2049)
				return Boolean.valueOf(true);
		}
		return getInputDebug();
	}

	public Object argInt_m0x40000(Object v)
	{
		if (v instanceof Integer)
		{
			Integer value = (Integer)v;
			if (value.intValue() == 0xfffc0000)
				return Boolean.valueOf(true);
		}
		return getInputDebug();
	}

	public Object argInt_0x40000(Object v)
	{
		if (v instanceof Integer)
		{
			Integer value = (Integer)v;
			if (value.intValue() == 0x40000)
				return Boolean.valueOf(true);
		}
		return getInputDebug();
	}

	public Object argInt_0x7fffffff(Object v)
	{
		if (v instanceof Integer)
		{
			Integer value = (Integer)v;
			if (value.intValue() == 0x7fffffff)
				return Boolean.valueOf(true);
		}
		return getInputDebug();
	}

	public Object argInt_m0x40001(Object v)
	{
		if (v instanceof Integer)
		{
			Integer value = (Integer)v;
			if (value.intValue() == 0xfffbffff)
				return Boolean.valueOf(true);
		}
		return getInputDebug();
	}

	public Object argInt_m0x80000000(Object v)
	{
		if (v instanceof Integer)
		{
			Integer value = (Integer)v;
			if (value.intValue() == 0x80000000)
				return Boolean.valueOf(true);
		}
		return getInputDebug();
	}

	public Object argLong_0(Object v)
	{
		if (v.equals(Long.valueOf(replyLong_0())))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argLong_1(Object v)
	{
		if (v.equals(Long.valueOf(replyLong_1())))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argLong_15(Object v)
	{
		if (v.equals(Long.valueOf(replyLong_15())))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argLong_m8(Object v)
	{
		if (v.equals(Long.valueOf(replyLong_m8())))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argLong_0x10(Object v)
	{
		if (v.equals(Long.valueOf(replyLong_0x10())))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argLong_0x7ff(Object v)
	{
		if (v.equals(Long.valueOf(replyLong_0x7ff())))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argLong_m9(Object v)
	{
		if (v.equals(Long.valueOf(replyLong_m9())))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argLong_m0x800(Object v)
	{
		if (v.equals(Long.valueOf(replyLong_m0x800())))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argLong_0x800(Object v)
	{
		if (v.equals(Long.valueOf(replyLong_0x800())))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argLong_0x3ffff(Object v)
	{
		if (v.equals(Long.valueOf(replyLong_0x3ffff())))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argLong_m0x801(Object v)
	{
		if (v.equals(Long.valueOf(replyLong_m0x801())))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argLong_m0x40000(Object v)
	{
		if (v.equals(Long.valueOf(replyLong_m0x40000())))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argLong_0x40000(Object v)
	{
		if (v.equals(Long.valueOf(replyLong_0x40000())))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argLong_0x7fffffff(Object v)
	{
		if (v.equals(Long.valueOf(replyLong_0x7fffffff())))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argLong_m0x40001(Object v)
	{
		if (v.equals(Long.valueOf(replyLong_m0x40001())))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argLong_m0x80000000(Object v)
	{
		if (v.equals(Long.valueOf(replyLong_m0x80000000())))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argLong_0x80000000(Object v)
	{
		if (v.equals(Long.valueOf(replyLong_0x80000000())))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argLong_m0x80000001(Object v)
	{
		if (v.equals(Long.valueOf(replyLong_m0x80000001())))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argDouble_0_0(Object v)
	{
		if (v.equals(Double.valueOf(replyDouble_0_0())))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argDouble_1_0(Object v)
	{
		if (v.equals(Double.valueOf(replyDouble_1_0())))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argDouble_2_0(Object v)
	{
		if (v.equals(Double.valueOf(replyDouble_2_0())))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argDouble_127_0(Object v)
	{
		if (v.equals(Double.valueOf(replyDouble_127_0())))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argDouble_m128_0(Object v)
	{
		if (v.equals(Double.valueOf(replyDouble_m128_0())))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argDouble_128_0(Object v)
	{
		if (v.equals(Double.valueOf(replyDouble_128_0())))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argDouble_m129_0(Object v)
	{
		if (v.equals(Double.valueOf(replyDouble_m129_0())))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argDouble_32767_0(Object v)
	{
		if (v.equals(Double.valueOf(replyDouble_32767_0())))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argDouble_m32768_0(Object v)
	{
		if (v.equals(Double.valueOf(replyDouble_m32768_0())))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argDouble_0_001(Object v)
	{
		if (v.equals(Double.valueOf(replyDouble_0_001())))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argDouble_m0_001(Object v)
	{
		if (v.equals(Double.valueOf(replyDouble_m0_001())))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argDouble_65_536(Object v)
	{
		if (v.equals(Double.valueOf(replyDouble_65_536())))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argDouble_3_14159(Object v)
	{
		if (v.equals(Double.valueOf(replyDouble_3_14159())))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argDate_0(Object v)
	{
		if (v.equals(replyDate_0()))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argDate_1(Object v)
	{
		if (v.equals(replyDate_1()))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argDate_2(Object v)
	{
		if (v.equals(replyDate_2()))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argString_0(Object v)
	{
		String expect = "";
		if (expect.equals(v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argString_1(Object v)
	{
		String expect = "0";
		if (expect.equals(v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argString_31(Object v)
	{
		String expect = "0123456789012345678901234567890";
		if (expect.equals(v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argString_32(Object v)
	{
		String expect = "01234567890123456789012345678901";
		if (expect.equals(v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argString_1023(Object v)
	{
		String expect = replyString_1023();
		if (expect.equals(v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argString_1024(Object v)
	{
		String expect = replyString_1024();
		if (expect.equals(v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argString_65536(Object v)
	{
		String expect = replyString_65536();
		if (expect.equals(v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argBinary_0(Object v)
	{
		byte expect[] = (byte[])(byte[])replyBinary_0();
		if (equals(expect, v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argBinary_1(Object v)
	{
		byte expect[] = (byte[])(byte[])replyBinary_1();
		if (equals(expect, v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argBinary_15(Object v)
	{
		byte expect[] = (byte[])(byte[])replyBinary_15();
		if (equals(expect, v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argBinary_16(Object v)
	{
		byte expect[] = (byte[])(byte[])replyBinary_16();
		if (equals(expect, v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argBinary_1023(Object v)
	{
		byte expect[] = (byte[])(byte[])replyBinary_1023();
		if (equals(expect, v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argBinary_1024(Object v)
	{
		byte expect[] = (byte[])(byte[])replyBinary_1024();
		if (equals(expect, v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argBinary_65536(Object v)
	{
		byte expect[] = (byte[])(byte[])replyBinary_65536();
		if (equals(expect, v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argUntypedFixedList_0(Object v)
	{
		Object expect = replyUntypedFixedList_0();
		if (expect.equals(v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argUntypedFixedList_1(Object v)
	{
		Object expect = replyUntypedFixedList_1();
		if (expect.equals(v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argUntypedFixedList_7(Object v)
	{
		Object expect = replyUntypedFixedList_7();
		if (expect.equals(v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argUntypedFixedList_8(Object v)
	{
		Object expect = replyUntypedFixedList_8();
		if (expect.equals(v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argTypedFixedList_0(Object v)
	{
		String expect[] = (String[])(String[])replyTypedFixedList_0();
		if ((v instanceof String[]) && equals(expect, (String[])(String[])v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argTypedFixedList_1(Object v)
	{
		String expect[] = (String[])(String[])replyTypedFixedList_1();
		if ((v instanceof String[]) && equals(expect, (String[])(String[])v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argTypedFixedList_7(Object v)
	{
		String expect[] = (String[])(String[])replyTypedFixedList_7();
		if ((v instanceof String[]) && equals(expect, (String[])(String[])v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argTypedFixedList_8(Object v)
	{
		String expect[] = (String[])(String[])replyTypedFixedList_8();
		if ((v instanceof String[]) && equals(expect, (String[])(String[])v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argUntypedMap_0(Object v)
	{
		Object expect = replyUntypedMap_0();
		if (expect.equals(v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argUntypedMap_1(Object v)
	{
		Object expect = replyUntypedMap_1();
		if (expect.equals(v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argUntypedMap_2(Object v)
	{
		Object expect = replyUntypedMap_2();
		if (expect.equals(v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argUntypedMap_3(Object v)
	{
		Object expect = replyUntypedMap_3();
		if (expect.equals(v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argTypedMap_0(Object v)
	{
		Object expect = replyTypedMap_0();
		if (expect.equals(v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argTypedMap_1(Object v)
	{
		Object expect = replyTypedMap_1();
		if (expect.equals(v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argTypedMap_2(Object v)
	{
		Object expect = replyTypedMap_2();
		if (expect.equals(v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argTypedMap_3(Object v)
	{
		Object expect = replyTypedMap_3();
		if (expect.equals(v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argObject_0(Object v)
	{
		Object expect = replyObject_0();
		if (expect.equals(v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argObject_16(Object v)
	{
		Object expect = replyObject_16();
		if (expect.equals(v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argObject_1(Object v)
	{
		Object expect = replyObject_1();
		if (expect.equals(v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argObject_2(Object v)
	{
		Object expect = replyObject_2();
		if (expect.equals(v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argObject_2a(Object v)
	{
		Object expect = replyObject_2a();
		if (expect.equals(v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argObject_2b(Object v)
	{
		Object expect = replyObject_2b();
		if (expect.equals(v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	public Object argObject_3(Object v)
	{
		Object expect = replyObject_3();
		if (expect.equals(v))
			return Boolean.valueOf(true);
		else
			return getInputDebug();
	}

	private boolean equals(String a[], String b[])
	{
		if (a == null || b == null)
			return false;
		if (a.length != b.length)
			return false;
		for (int i = 0; i < a.length; i++)
			if (!a[i].equals(b[i]))
				return false;

		return true;
	}

	private boolean equals(byte a[], Object obj)
	{
		if (!(obj instanceof byte[]))
			return false;
		byte b[] = (byte[])(byte[])obj;
		if (a == null || b == null)
			return false;
		if (a.length != b.length)
			return false;
		for (int i = 0; i < a.length; i++)
			if (a[i] != b[i])
				return false;

		return true;
	}

	protected String getInputDebug()
	{
		CharArrayWriter writer = (CharArrayWriter)_threadWriter.get();
		if (writer != null)
			return writer.toString();
		else
			return null;
	}

	public void invoke(InputStream is, OutputStream os, String objectId, SerializerFactory serializerFactory)
		throws Exception
	{
		CharArrayWriter writer = new CharArrayWriter();
		_threadWriter.set(writer);
		PrintWriter dbg = new PrintWriter(writer);
		HessianDebugInputStream debug = new HessianDebugInputStream(is, dbg);
		debug.startTop2();
		super.invoke(debug, os, objectId, serializerFactory);
	}
}
