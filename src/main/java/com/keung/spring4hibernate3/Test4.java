package com.keung.spring4hibernate3;

import java.util.ArrayList;
import java.util.List;

public class Test4
{
	public static void main(String[] args)
	{
		int n = 3;
		System.out.println("Before change, n = " + n);
		changeData(n);
		System.out.println("After changeData(n), n = " + n);
	}

	public static void changeData(int nn)
	{
		nn = 10;
	}

	public void changeList(List<Integer> list)
	{
		List<Integer> list2 = new ArrayList<Integer>();
		list2.add(1);
		list2.add(2);
		System.out.println("[1]:" + list2.size());
		list = list2;
		System.out.println("[2]:" + list.size());
	}

	@org.junit.Test
	public void testList()
	{
		List<Integer> l = new ArrayList<Integer>();

		changeList(l);

		System.out.println("[3]:" + l.size());//0  
	}

}
