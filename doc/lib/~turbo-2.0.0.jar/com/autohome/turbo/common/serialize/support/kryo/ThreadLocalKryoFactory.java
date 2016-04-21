// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ThreadLocalKryoFactory.java

package com.autohome.turbo.common.serialize.support.kryo;

import com.esotericsoftware.kryo.Kryo;

// Referenced classes of package com.autohome.turbo.common.serialize.support.kryo:
//			KryoFactory

public class ThreadLocalKryoFactory extends KryoFactory
{

	private final ThreadLocal holder = new ThreadLocal() {

		final ThreadLocalKryoFactory this$0;

		protected Kryo initialValue()
		{
			return createKryo();
		}

		protected volatile Object initialValue()
		{
			return initialValue();
		}

			
			{
				this$0 = ThreadLocalKryoFactory.this;
				super();
			}
	};

	public ThreadLocalKryoFactory()
	{
	}

	public Kryo getKryo()
	{
		return (Kryo)holder.get();
	}
}
