// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   PrototypeKryoFactory.java

package com.autohome.turbo.common.serialize.support.kryo;

import com.esotericsoftware.kryo.Kryo;

// Referenced classes of package com.autohome.turbo.common.serialize.support.kryo:
//			KryoFactory

public class PrototypeKryoFactory extends KryoFactory
{

	public PrototypeKryoFactory()
	{
	}

	public Kryo getKryo()
	{
		return createKryo();
	}
}
