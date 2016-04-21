package com.keung.spring4hibernate3;

public class SwapTip
{
	public static void main(String[] args)
	{
		Integer a = new Integer(4);
		Integer b = new Integer(100);

		System.out.println("a: " + a);
		System.out.println("b: " + b);

		swap(a, b);

		System.out.println("Swapped!");
		System.out.println("a: " + a);
		System.out.println("b: " + b);
	}

	public static void swap(Object o1, Object o2)
	{
		Object t = o1;
		o1 = o2;
		o2 = t;
	}
}