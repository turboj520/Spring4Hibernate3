// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   GenericObjectOutput.java

package com.autohome.turbo.common.serialize.support.dubbo;

import com.autohome.turbo.common.serialize.ObjectOutput;
import com.autohome.turbo.common.utils.ReflectUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Referenced classes of package com.autohome.turbo.common.serialize.support.dubbo:
//			GenericDataOutput, Builder, ClassDescriptorMapper

public class GenericObjectOutput extends GenericDataOutput
	implements ObjectOutput
{

	private ClassDescriptorMapper mMapper;
	private Map mRefs;
	private final boolean isAllowNonSerializable;

	public GenericObjectOutput(OutputStream out)
	{
		this(out, Builder.DEFAULT_CLASS_DESCRIPTOR_MAPPER);
	}

	public GenericObjectOutput(OutputStream out, ClassDescriptorMapper mapper)
	{
		super(out);
		mRefs = new ConcurrentHashMap();
		mMapper = mapper;
		isAllowNonSerializable = false;
	}

	public GenericObjectOutput(OutputStream out, int buffSize)
	{
		this(out, buffSize, Builder.DEFAULT_CLASS_DESCRIPTOR_MAPPER, false);
	}

	public GenericObjectOutput(OutputStream out, int buffSize, ClassDescriptorMapper mapper)
	{
		this(out, buffSize, mapper, false);
	}

	public GenericObjectOutput(OutputStream out, int buffSize, ClassDescriptorMapper mapper, boolean isAllowNonSerializable)
	{
		super(out, buffSize);
		mRefs = new ConcurrentHashMap();
		mMapper = mapper;
		this.isAllowNonSerializable = isAllowNonSerializable;
	}

	public void writeObject(Object obj)
		throws IOException
	{
		if (obj == null)
		{
			write0((byte)-108);
			return;
		}
		Class c = obj.getClass();
		if (c == java/lang/Object)
		{
			write0((byte)-107);
		} else
		{
			String desc = ReflectUtils.getDesc(c);
			int index = mMapper.getDescriptorIndex(desc);
			if (index < 0)
			{
				write0((byte)-118);
				writeUTF(desc);
			} else
			{
				write0((byte)-117);
				writeUInt(index);
			}
			Builder b = Builder.register(c, isAllowNonSerializable);
			b.writeTo(obj, this);
		}
	}

	public void addRef(Object obj)
	{
		mRefs.put(obj, Integer.valueOf(mRefs.size()));
	}

	public int getRef(Object obj)
	{
		Integer ref = (Integer)mRefs.get(obj);
		if (ref == null)
			return -1;
		else
			return ref.intValue();
	}
}
