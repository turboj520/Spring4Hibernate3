// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   PooledKryoFactory.java

package com.autohome.turbo.common.serialize.support.kryo;

import com.esotericsoftware.kryo.Kryo;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

// Referenced classes of package com.autohome.turbo.common.serialize.support.kryo:
//			KryoFactory

public class PooledKryoFactory extends KryoFactory
{

	private final Queue pool = new ConcurrentLinkedQueue();

	public PooledKryoFactory()
	{
	}

	public void returnKryo(Kryo kryo)
	{
		pool.offer(kryo);
	}

	public void close()
	{
		pool.clear();
	}

	public Kryo getKryo()
	{
		Kryo kryo = (Kryo)pool.poll();
		if (kryo == null)
			kryo = createKryo();
		return kryo;
	}
}
