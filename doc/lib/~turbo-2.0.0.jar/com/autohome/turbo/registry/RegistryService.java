// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   RegistryService.java

package com.autohome.turbo.registry;

import com.autohome.turbo.common.URL;
import java.util.List;

// Referenced classes of package com.autohome.turbo.registry:
//			NotifyListener

public interface RegistryService
{

	public abstract void register(URL url);

	public abstract void unregister(URL url);

	public abstract void subscribe(URL url, NotifyListener notifylistener);

	public abstract void unsubscribe(URL url, NotifyListener notifylistener);

	public abstract List lookup(URL url);
}
