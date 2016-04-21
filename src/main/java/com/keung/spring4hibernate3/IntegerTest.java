package com.keung.spring4hibernate3;

public class IntegerTest
{
	public static void main(String[] args)
	{
		objPoolTest();
	}

	public static void objPoolTest()
	{
//		Integer i7 = 126;
//		Integer i8 = 126;
//		System.out.println(i7 == i8);
//		Integer i1 = 40;
//		Integer i2 = 40;
//		Integer i3 = 0;
//		Integer i4 = new Integer(100);
//		Integer i5 = new Integer(100);
//		System.out.println("i4==i5:" + (i4 == i5));
//		Integer i6 = new Integer(100);
//
//		System.out.println("i1=i2\t\t\t" + (i1 == i2)); //	false
//		System.out.println("i1=i2+i3\t\t\t" + (i1 == i2 + i3)); //	false
//		System.out.println("i4=i5\t\t\t" + (i4 == i5)); // 	false
//		System.out.println("i4=i5+i6\t" + (i4 == i5 + i6)); // false
//
//		System.out.println();

		Integer int1 = new Integer(1000);
		Integer int2 = new Integer(500);
		Integer int3 = new Integer(500);

		Integer int4 = int2 + int3;
		System.out.println(int1 == int4);
		System.out.println(int1 == (int2 + int3));

	}
}