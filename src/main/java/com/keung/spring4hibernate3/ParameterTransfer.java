package com.keung.spring4hibernate3;

public class ParameterTransfer
{
	public static void main(String[] args)
	{
		String[] array = new String[] { "huixin" };
		System.out.println("调用reset方法前array中的第0个元素的值是:" + array[0]);
		reset(array);
		System.out.println("调用reset方法后array中的第0个元素的值是:" + array[0]);
	}

	// 传过来的参数是 array 引用的 copy
	public static void reset(String[] param)
	{
		param[0] = "hello, world!";
	}

}