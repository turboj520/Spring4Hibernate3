// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Deserializer.java

package com.autohome.hessian.io;

import java.io.IOException;

// Referenced classes of package com.autohome.hessian.io:
//			AbstractHessianInput

public interface Deserializer
{

	public abstract Class getType();

	public abstract boolean isReadResolve();

	public abstract Object readObject(AbstractHessianInput abstracthessianinput)
		throws IOException;

	public abstract Object readList(AbstractHessianInput abstracthessianinput, int i)
		throws IOException;

	public abstract Object readLengthList(AbstractHessianInput abstracthessianinput, int i)
		throws IOException;

	public abstract Object readMap(AbstractHessianInput abstracthessianinput)
		throws IOException;

	public abstract Object[] createFields(int i);

	public abstract Object createField(String s);

	public abstract Object readObject(AbstractHessianInput abstracthessianinput, Object aobj[])
		throws IOException;

	public abstract Object readObject(AbstractHessianInput abstracthessianinput, String as[])
		throws IOException;
}
