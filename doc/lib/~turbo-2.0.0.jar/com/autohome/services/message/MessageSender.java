// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   MessageSender.java

package com.autohome.services.message;

import java.util.HashMap;

// Referenced classes of package com.autohome.services.message:
//			MessageServiceException

public interface MessageSender
{

	public abstract void send(HashMap hashmap, Object obj)
		throws MessageServiceException;
}
