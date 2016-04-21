// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HessianEnvelope.java

package com.autohome.com.caucho.hessian.io;

import java.io.IOException;

// Referenced classes of package com.autohome.com.caucho.hessian.io:
//			Hessian2Output, Hessian2Input

public abstract class HessianEnvelope
{

	public HessianEnvelope()
	{
	}

	public abstract Hessian2Output wrap(Hessian2Output hessian2output)
		throws IOException;

	public abstract Hessian2Input unwrap(Hessian2Input hessian2input)
		throws IOException;

	public abstract Hessian2Input unwrapHeaders(Hessian2Input hessian2input)
		throws IOException;
}
