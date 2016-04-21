package com.keung.spring4hibernate3;

public class Test2
{
	public static void main(String[] args)
	{
		StringBuffer sb = new StringBuffer("Hello ");
		System.out.println("Before change, sb = " + sb);
		changeData(sb);
		System.out.println("After changeData(n), sb = " + sb);
	}

	public static void changeData(StringBuffer strBuf)
	{
		strBuf.append("World!");
		
		System.out.println(strBuf.toString());
	}
}
