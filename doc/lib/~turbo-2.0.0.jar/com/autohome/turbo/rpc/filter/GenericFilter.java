// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   GenericFilter.java

package com.autohome.turbo.rpc.filter;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.beanutil.*;
import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.common.io.UnsafeByteArrayInputStream;
import com.autohome.turbo.common.io.UnsafeByteArrayOutputStream;
import com.autohome.turbo.common.serialize.*;
import com.autohome.turbo.common.utils.*;
import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.service.GenericException;
import com.autohome.turbo.rpc.support.ProtocolUtils;
import java.io.IOException;
import java.lang.reflect.Method;

public class GenericFilter
	implements Filter
{

	public GenericFilter()
	{
	}

	public Result invoke(Invoker invoker, Invocation inv)
		throws RpcException
	{
		String name;
		String types[];
		Object args[];
		if (!inv.getMethodName().equals("$invoke") || inv.getArguments() == null || inv.getArguments().length != 3 || ProtocolUtils.isGeneric(invoker.getUrl().getParameter("generic")))
			break MISSING_BLOCK_LABEL_718;
		name = ((String)inv.getArguments()[0]).trim();
		types = (String[])(String[])inv.getArguments()[1];
		args = (Object[])(Object[])inv.getArguments()[2];
		String generic;
		Result result;
		IOException e;
		Method method = ReflectUtils.findMethodByMethodSignature(invoker.getInterface(), name, types);
		Class params[] = method.getParameterTypes();
		if (args == null)
			args = new Object[params.length];
		generic = inv.getAttachment("generic");
		if (StringUtils.isEmpty(generic) || ProtocolUtils.isDefaultGenericSerialization(generic))
			args = PojoUtils.realize(args, params, method.getGenericParameterTypes());
		else
		if (ProtocolUtils.isJavaGenericSerialization(generic))
		{
			for (int i = 0; i < args.length; i++)
				if ([B == args[i].getClass())
					try
					{
						UnsafeByteArrayInputStream is = new UnsafeByteArrayInputStream((byte[])(byte[])args[i]);
						args[i] = ((Serialization)ExtensionLoader.getExtensionLoader(com/autohome/turbo/common/serialize/Serialization).getExtension("nativejava")).deserialize(null, is).readObject();
					}
					// Misplaced declaration of an exception variable
					catch (IOException e)
					{
						throw new RpcException((new StringBuilder()).append("Deserialize argument [").append(i + 1).append("] failed.").toString(), e);
					}
				else
					throw new RpcException((new StringBuilder(32)).append("Generic serialization [").append("nativejava").append("] only support message type ").append([B).append(" and your message type is ").append(args[i].getClass()).toString());

		} else
		if (ProtocolUtils.isBeanGenericSerialization(generic))
		{
			for (int i = 0; i < args.length; i++)
				if (args[i] instanceof JavaBeanDescriptor)
					args[i] = JavaBeanSerializeUtil.deserialize((JavaBeanDescriptor)args[i]);
				else
					throw new RpcException((new StringBuilder(32)).append("Generic serialization [").append("bean").append("] only support message type ").append(com/autohome/turbo/common/beanutil/JavaBeanDescriptor.getName()).append(" and your message type is ").append(args[i].getClass().getName()).toString());

		}
		result = invoker.invoke(new RpcInvocation(method, args, inv.getAttachments()));
		if (result.hasException() && !(result.getException() instanceof GenericException))
			return new RpcResult(new GenericException(result.getException()));
		if (!ProtocolUtils.isJavaGenericSerialization(generic))
			break MISSING_BLOCK_LABEL_637;
		UnsafeByteArrayOutputStream os;
		os = new UnsafeByteArrayOutputStream(512);
		((Serialization)ExtensionLoader.getExtensionLoader(com/autohome/turbo/common/serialize/Serialization).getExtension("nativejava")).serialize(null, os).writeObject(result.getValue());
		return new RpcResult(os.toByteArray());
		os;
		throw new RpcException("Serialize result failed.", os);
		if (ProtocolUtils.isBeanGenericSerialization(generic))
			return new RpcResult(JavaBeanSerializeUtil.serialize(result.getValue(), JavaBeanAccessor.METHOD));
		return new RpcResult(PojoUtils.generalize(result.getValue()));
		NoSuchMethodException e;
		e;
		throw new RpcException(e.getMessage(), e);
		e;
		throw new RpcException(e.getMessage(), e);
		return invoker.invoke(inv);
	}
}
