// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Builder.java

package com.autohome.turbo.common.serialize.support.dubbo;

import com.autohome.turbo.common.bytecode.ClassGenerator;
import com.autohome.turbo.common.io.UnsafeByteArrayInputStream;
import com.autohome.turbo.common.io.UnsafeByteArrayOutputStream;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.serialize.support.java.CompactedObjectInputStream;
import com.autohome.turbo.common.serialize.support.java.CompactedObjectOutputStream;
import com.autohome.turbo.common.utils.ClassHelper;
import com.autohome.turbo.common.utils.IOUtils;
import com.autohome.turbo.common.utils.ReflectUtils;
import com.autohome.turbo.common.utils.StringUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Referenced classes of package com.autohome.turbo.common.serialize.support.dubbo:
//			GenericObjectOutput, GenericObjectInput, GenericDataFlags, ClassDescriptorMapper

public abstract class Builder
	implements GenericDataFlags
{
	public static abstract class AbstractObjectBuilder extends Builder
	{

		public abstract Class getType();

		public void writeTo(Object obj, GenericObjectOutput out)
			throws IOException
		{
			if (obj == null)
			{
				out.write0((byte)-108);
			} else
			{
				int ref = out.getRef(obj);
				if (ref < 0)
				{
					out.addRef(obj);
					out.write0((byte)-128);
					writeObject(obj, out);
				} else
				{
					out.write0((byte)-127);
					out.writeUInt(ref);
				}
			}
		}

		public Object parseFrom(GenericObjectInput in)
			throws IOException
		{
			byte b = in.read0();
			switch (b)
			{
			case -128: 
				Object ret = newInstance(in);
				in.addRef(ret);
				readObject(ret, in);
				return ret;

			case -127: 
				return in.getRef(in.readUInt());

			case -108: 
				return null;
			}
			throw new IOException((new StringBuilder()).append("Input format error, expect OBJECT|OBJECT_REF|OBJECT_NULL, get ").append(b).toString());
		}

		protected abstract void writeObject(Object obj, GenericObjectOutput genericobjectoutput)
			throws IOException;

		protected abstract Object newInstance(GenericObjectInput genericobjectinput)
			throws IOException;

		protected abstract void readObject(Object obj, GenericObjectInput genericobjectinput)
			throws IOException;

		public AbstractObjectBuilder()
		{
		}
	}

	static class PropertyMetadata
	{

		Class type;
		String setter;
		String getter;

		PropertyMetadata()
		{
		}
	}


	protected static Logger logger = LoggerFactory.getLogger(com/autohome/turbo/common/serialize/support/dubbo/Builder);
	private static final AtomicLong BUILDER_CLASS_COUNTER = new AtomicLong(0L);
	private static final String BUILDER_CLASS_NAME = com/autohome/turbo/common/serialize/support/dubbo/Builder.getName();
	private static final Map BuilderMap = new ConcurrentHashMap();
	private static final Map nonSerializableBuilderMap = new ConcurrentHashMap();
	private static final String FIELD_CONFIG_SUFFIX = ".fc";
	private static final int MAX_FIELD_CONFIG_FILE_SIZE = 16384;
	private static final Comparator FNC = new Comparator() {

		public int compare(String n1, String n2)
		{
			return Builder.compareFieldName(n1, n2);
		}

		public volatile int compare(Object x0, Object x1)
		{
			return compare((String)x0, (String)x1);
		}

	};
	private static final Comparator FC = new Comparator() {

		public int compare(Field f1, Field f2)
		{
			return Builder.compareFieldName(f1.getName(), f2.getName());
		}

		public volatile int compare(Object x0, Object x1)
		{
			return compare((Field)x0, (Field)x1);
		}

	};
	private static final Comparator CC = new Comparator() {

		public int compare(Constructor o1, Constructor o2)
		{
			return o1.getParameterTypes().length - o2.getParameterTypes().length;
		}

		public volatile int compare(Object x0, Object x1)
		{
			return compare((Constructor)x0, (Constructor)x1);
		}

	};
	private static final List mDescList = new ArrayList();
	private static final Map mDescMap = new ConcurrentHashMap();
	public static ClassDescriptorMapper DEFAULT_CLASS_DESCRIPTOR_MAPPER = new ClassDescriptorMapper() {

		public String getDescriptor(int index)
		{
			if (index < 0 || index >= Builder.mDescList.size())
				return null;
			else
				return (String)Builder.mDescList.get(index);
		}

		public int getDescriptorIndex(String desc)
		{
			Integer ret = (Integer)Builder.mDescMap.get(desc);
			return ret != null ? ret.intValue() : -1;
		}

	};
	static final Builder GenericBuilder = new Builder() {

		public Class getType()
		{
			return java/lang/Object;
		}

		public void writeTo(Object obj, GenericObjectOutput out)
			throws IOException
		{
			out.writeObject(obj);
		}

		public Object parseFrom(GenericObjectInput in)
			throws IOException
		{
			return in.readObject();
		}

	};
	static final Builder GenericArrayBuilder = new AbstractObjectBuilder() {

		public Class getType()
		{
			return [Ljava/lang/Object;;
		}

		protected Object[] newInstance(GenericObjectInput in)
			throws IOException
		{
			return new Object[in.readUInt()];
		}

		protected void readObject(Object ret[], GenericObjectInput in)
			throws IOException
		{
			for (int i = 0; i < ret.length; i++)
				ret[i] = in.readObject();

		}

		protected void writeObject(Object obj[], GenericObjectOutput out)
			throws IOException
		{
			out.writeUInt(obj.length);
			Object arr$[] = obj;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				Object item = arr$[i$];
				out.writeObject(item);
			}

		}

		protected volatile void readObject(Object x0, GenericObjectInput x1)
			throws IOException
		{
			readObject((Object[])x0, x1);
		}

		protected volatile Object newInstance(GenericObjectInput x0)
			throws IOException
		{
			return ((Object) (newInstance(x0)));
		}

		protected volatile void writeObject(Object x0, GenericObjectOutput x1)
			throws IOException
		{
			writeObject((Object[])x0, x1);
		}

	};
	static final Builder SerializableBuilder = new Builder() {

		public Class getType()
		{
			return java/io/Serializable;
		}

		public void writeTo(Serializable obj, GenericObjectOutput out)
			throws IOException
		{
			if (obj == null)
			{
				out.write0((byte)-108);
			} else
			{
				out.write0((byte)-126);
				UnsafeByteArrayOutputStream bos = new UnsafeByteArrayOutputStream();
				CompactedObjectOutputStream oos = new CompactedObjectOutputStream(bos);
				oos.writeObject(obj);
				oos.flush();
				bos.close();
				byte b[] = bos.toByteArray();
				out.writeUInt(b.length);
				out.write0(b, 0, b.length);
			}
		}

		public Serializable parseFrom(GenericObjectInput in)
			throws IOException
		{
			CompactedObjectInputStream ois;
			byte b = in.read0();
			if (b == -108)
				return null;
			if (b != -126)
				throw new IOException((new StringBuilder()).append("Input format error, expect OBJECT_NULL|OBJECT_STREAM, get ").append(b).append(".").toString());
			UnsafeByteArrayInputStream bis = new UnsafeByteArrayInputStream(in.read0(in.readUInt()));
			ois = new CompactedObjectInputStream(bis);
			return (Serializable)ois.readObject();
			ClassNotFoundException e;
			e;
			throw new IOException(StringUtils.toString(e));
		}

		public volatile Object parseFrom(GenericObjectInput x0)
			throws IOException
		{
			return parseFrom(x0);
		}

		public volatile void writeTo(Object x0, GenericObjectOutput x1)
			throws IOException
		{
			writeTo((Serializable)x0, x1);
		}

	};

	protected Builder()
	{
	}

	public abstract Class getType();

	public void writeTo(Object obj, OutputStream os)
		throws IOException
	{
		GenericObjectOutput out = new GenericObjectOutput(os);
		writeTo(obj, out);
		out.flushBuffer();
	}

	public Object parseFrom(byte b[])
		throws IOException
	{
		return parseFrom(((InputStream) (new UnsafeByteArrayInputStream(b))));
	}

	public Object parseFrom(InputStream is)
		throws IOException
	{
		return parseFrom(new GenericObjectInput(is));
	}

	public abstract void writeTo(Object obj, GenericObjectOutput genericobjectoutput)
		throws IOException;

	public abstract Object parseFrom(GenericObjectInput genericobjectinput)
		throws IOException;

	public static Builder register(Class c, boolean isAllowNonSerializable)
	{
		if (c == java/lang/Object || c.isInterface())
			return GenericBuilder;
		if (c == [Ljava/lang/Object;)
			return GenericArrayBuilder;
		Builder b = (Builder)BuilderMap.get(c);
		if (null != b)
			return b;
		boolean isSerializable = java/io/Serializable.isAssignableFrom(c);
		if (!isAllowNonSerializable && !isSerializable)
			throw new IllegalStateException((new StringBuilder()).append("Serialized class ").append(c.getName()).append(" must implement java.io.Serializable (dubbo codec setting: isAllowNonSerializable = false)").toString());
		b = (Builder)nonSerializableBuilderMap.get(c);
		if (null != b)
			return b;
		b = newBuilder(c);
		if (isSerializable)
			BuilderMap.put(c, b);
		else
			nonSerializableBuilderMap.put(c, b);
		return b;
	}

	public static Builder register(Class c)
	{
		return register(c, false);
	}

	public static void register(Class c, Builder b)
	{
		if (java/io/Serializable.isAssignableFrom(c))
			BuilderMap.put(c, b);
		else
			nonSerializableBuilderMap.put(c, b);
	}

	private static Builder newBuilder(Class c)
	{
		if (c.isPrimitive())
			throw new RuntimeException((new StringBuilder()).append("Can not create builder for primitive type: ").append(c).toString());
		if (logger.isInfoEnabled())
			logger.info((new StringBuilder()).append("create Builder for class: ").append(c).toString());
		Builder builder;
		if (c.isArray())
			builder = newArrayBuilder(c);
		else
			builder = newObjectBuilder(c);
		return builder;
	}

	private static Builder newArrayBuilder(Class c)
	{
		Exception exception;
		Class cc = c.getComponentType();
		if (cc.isInterface())
			return GenericArrayBuilder;
		ClassLoader cl = ClassHelper.getCallerClassLoader(com/autohome/turbo/common/serialize/support/dubbo/Builder);
		String cn = ReflectUtils.getName(c);
		String ccn = ReflectUtils.getName(cc);
		String bcn = (new StringBuilder()).append(BUILDER_CLASS_NAME).append("$bc").append(BUILDER_CLASS_COUNTER.getAndIncrement()).toString();
		int ix = cn.indexOf(']');
		String s1 = cn.substring(0, ix);
		String s2 = cn.substring(ix);
		StringBuilder cwt = (new StringBuilder("public void writeTo(Object obj, ")).append(com/autohome/turbo/common/serialize/support/dubbo/GenericObjectOutput.getName()).append(" out) throws java.io.IOException{");
		StringBuilder cpf = (new StringBuilder("public Object parseFrom(")).append(com/autohome/turbo/common/serialize/support/dubbo/GenericObjectInput.getName()).append(" in) throws java.io.IOException{");
		cwt.append("if( $1 == null ){ $2.write0(OBJECT_NULL); return; }");
		cwt.append(cn).append(" v = (").append(cn).append(")$1; int len = v.length; $2.write0(OBJECT_VALUES); $2.writeUInt(len); for(int i=0;i<len;i++){ ");
		cpf.append("byte b = $1.read0(); if( b == OBJECT_NULL ) return null; if( b != OBJECT_VALUES ) throw new java.io.IOException(\"Input format error, expect OBJECT_NULL|OBJECT_VALUES, get \" + b + \".\");");
		cpf.append("int len = $1.readUInt(); if( len == 0 ) return new ").append(s1).append('0').append(s2).append("; ");
		cpf.append(cn).append(" ret = new ").append(s1).append("len").append(s2).append("; for(int i=0;i<len;i++){ ");
		Builder builder = null;
		if (cc.isPrimitive())
		{
			if (cc == Boolean.TYPE)
			{
				cwt.append("$2.writeBool(v[i]);");
				cpf.append("ret[i] = $1.readBool();");
			} else
			if (cc == Byte.TYPE)
			{
				cwt.append("$2.writeByte(v[i]);");
				cpf.append("ret[i] = $1.readByte();");
			} else
			if (cc == Character.TYPE)
			{
				cwt.append("$2.writeShort((short)v[i]);");
				cpf.append("ret[i] = (char)$1.readShort();");
			} else
			if (cc == Short.TYPE)
			{
				cwt.append("$2.writeShort(v[i]);");
				cpf.append("ret[i] = $1.readShort();");
			} else
			if (cc == Integer.TYPE)
			{
				cwt.append("$2.writeInt(v[i]);");
				cpf.append("ret[i] = $1.readInt();");
			} else
			if (cc == Long.TYPE)
			{
				cwt.append("$2.writeLong(v[i]);");
				cpf.append("ret[i] = $1.readLong();");
			} else
			if (cc == Float.TYPE)
			{
				cwt.append("$2.writeFloat(v[i]);");
				cpf.append("ret[i] = $1.readFloat();");
			} else
			if (cc == Double.TYPE)
			{
				cwt.append("$2.writeDouble(v[i]);");
				cpf.append("ret[i] = $1.readDouble();");
			}
		} else
		{
			builder = register(cc);
			cwt.append("builder.writeTo(v[i], $2);");
			cpf.append("ret[i] = (").append(ccn).append(")builder.parseFrom($1);");
		}
		cwt.append(" } }");
		cpf.append(" } return ret; }");
		ClassGenerator cg = ClassGenerator.newInstance(cl);
		cg.setClassName(bcn);
		cg.setSuperClass(com/autohome/turbo/common/serialize/support/dubbo/Builder);
		cg.addDefaultConstructor();
		if (builder != null)
			cg.addField((new StringBuilder()).append("public static ").append(BUILDER_CLASS_NAME).append(" builder;").toString());
		cg.addMethod((new StringBuilder()).append("public Class getType(){ return ").append(cn).append(".class; }").toString());
		cg.addMethod(cwt.toString());
		cg.addMethod(cpf.toString());
		Builder builder1;
		try
		{
			Class wc = cg.toClass();
			if (builder != null)
				wc.getField("builder").set(null, builder);
			builder1 = (Builder)wc.newInstance();
		}
		catch (RuntimeException e)
		{
			throw e;
		}
		catch (Throwable e)
		{
			throw new RuntimeException(e.getMessage());
		}
		finally
		{
			cg.release();
		}
		cg.release();
		return builder1;
		throw exception;
	}

	private static Builder newObjectBuilder(Class c)
	{
		ClassLoader cl;
		boolean isp;
		String cn;
		String bcn;
		boolean isc;
		boolean ism;
		boolean iss;
		String fns[];
		InputStream is;
		Exception exception;
		if (c.isEnum())
			return newEnumBuilder(c);
		if (c.isAnonymousClass())
			throw new RuntimeException((new StringBuilder()).append("Can not instantiation anonymous class: ").append(c).toString());
		if (c.getEnclosingClass() != null && !Modifier.isStatic(c.getModifiers()))
			throw new RuntimeException((new StringBuilder()).append("Can not instantiation inner and non-static class: ").append(c).toString());
		if (java/lang/Throwable.isAssignableFrom(c))
			return SerializableBuilder;
		cl = ClassHelper.getCallerClassLoader(com/autohome/turbo/common/serialize/support/dubbo/Builder);
		cn = c.getName();
		if (c.getClassLoader() == null)
		{
			isp = false;
			bcn = (new StringBuilder()).append(BUILDER_CLASS_NAME).append("$bc").append(BUILDER_CLASS_COUNTER.getAndIncrement()).toString();
		} else
		{
			isp = true;
			bcn = (new StringBuilder()).append(cn).append("$bc").append(BUILDER_CLASS_COUNTER.getAndIncrement()).toString();
		}
		isc = java/util/Collection.isAssignableFrom(c);
		ism = !isc && java/util/Map.isAssignableFrom(c);
		iss = !isc && !ism && java/io/Serializable.isAssignableFrom(c);
		fns = null;
		is = c.getResourceAsStream((new StringBuilder()).append(c.getSimpleName()).append(".fc").toString());
		if (is == null)
			break MISSING_BLOCK_LABEL_524;
		try
		{
			int len = is.available();
			if (len > 0)
			{
				if (len > 16384)
					throw new RuntimeException((new StringBuilder()).append("Load [").append(c.getName()).append("] field-config file error: File-size too larger").toString());
				String lines[] = IOUtils.readLines(is);
				if (lines != null && lines.length > 0)
				{
					List list = new ArrayList();
					for (int i = 0; i < lines.length; i++)
					{
						fns = lines[i].split(",");
						Arrays.sort(fns, FNC);
						for (int j = 0; j < fns.length; j++)
							list.add(fns[j]);

					}

					fns = (String[])list.toArray(new String[0]);
				}
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException((new StringBuilder()).append("Load [").append(c.getName()).append("] field-config file error: ").append(e.getMessage()).toString());
		}
		finally { }
		try
		{
			is.close();
			break MISSING_BLOCK_LABEL_524;
		}
		catch (IOException e) { }
		  goto _L1
		try
		{
			is.close();
		}
		catch (IOException e) { }
		throw exception;
_L1:
		if (fns == null) goto _L3; else goto _L2
_L2:
		Field fs[];
		int i;
		fs = new Field[fns.length];
		i = 0;
_L5:
		String fn;
		if (i >= fns.length)
			break; /* Loop/switch isn't completed */
		fn = fns[i];
		Field f;
		f = c.getDeclaredField(fn);
		int mod = f.getModifiers();
		if (Modifier.isStatic(mod) || serializeIgnoreFinalModifier(c) && Modifier.isFinal(mod))
			throw new RuntimeException((new StringBuilder()).append("Field [").append(c.getName()).append(".").append(fn).append("] is static/final field.").toString());
		if (!Modifier.isTransient(mod))
			break MISSING_BLOCK_LABEL_700;
		if (iss)
			return SerializableBuilder;
		throw new RuntimeException((new StringBuilder()).append("Field [").append(c.getName()).append(".").append(fn).append("] is transient field.").toString());
		f.setAccessible(true);
		fs[i] = f;
		break MISSING_BLOCK_LABEL_778;
		SecurityException e;
		e;
		throw new RuntimeException(e.getMessage());
		e;
		throw new RuntimeException((new StringBuilder()).append("Field [").append(c.getName()).append(".").append(fn).append("] not found.").toString());
		i++;
		if (true) goto _L5; else goto _L4
_L3:
		Class t = c;
		List fl = new ArrayList();
		do
		{
			fs = t.getDeclaredFields();
			Field arr$[] = fs;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				Field tf = arr$[i$];
				int mod = tf.getModifiers();
				if (Modifier.isStatic(mod) || serializeIgnoreFinalModifier(c) && Modifier.isFinal(mod) || tf.getName().equals("this$0") || !Modifier.isPublic(tf.getType().getModifiers()))
					continue;
				if (Modifier.isTransient(mod))
				{
					if (iss)
						return SerializableBuilder;
				} else
				{
					tf.setAccessible(true);
					fl.add(tf);
				}
			}

			t = t.getSuperclass();
		} while (t != java/lang/Object);
		fs = (Field[])fl.toArray(new Field[0]);
		if (fs.length > 1)
			Arrays.sort(fs, FC);
_L4:
		Exception exception1;
		Constructor cs[] = c.getDeclaredConstructors();
		if (cs.length == 0)
		{
			Class t = c;
			do
			{
				t = t.getSuperclass();
				if (t == null)
					throw new RuntimeException("Can not found Constructor?");
				cs = c.getDeclaredConstructors();
			} while (cs.length == 0);
		}
		if (cs.length > 1)
			Arrays.sort(cs, CC);
		StringBuilder cwf = (new StringBuilder("protected void writeObject(Object obj, ")).append(com/autohome/turbo/common/serialize/support/dubbo/GenericObjectOutput.getName()).append(" out) throws java.io.IOException{");
		cwf.append(cn).append(" v = (").append(cn).append(")$1; ");
		cwf.append("$2.writeInt(fields.length);");
		StringBuilder crf = (new StringBuilder("protected void readObject(Object ret, ")).append(com/autohome/turbo/common/serialize/support/dubbo/GenericObjectInput.getName()).append(" in) throws java.io.IOException{");
		crf.append("int fc = $2.readInt();");
		crf.append("if( fc != ").append(fs.length).append(" ) throw new IllegalStateException(\"Deserialize Class [").append(cn).append("], field count not matched. Expect ").append(fs.length).append(" but get \" + fc +\".\");");
		crf.append(cn).append(" ret = (").append(cn).append(")$1;");
		StringBuilder cni = (new StringBuilder("protected Object newInstance(")).append(com/autohome/turbo/common/serialize/support/dubbo/GenericObjectInput.getName()).append(" in){ return ");
		Constructor con = cs[0];
		int mod = con.getModifiers();
		boolean dn = Modifier.isPublic(mod) || isp && !Modifier.isPrivate(mod);
		if (dn)
		{
			cni.append("new ").append(cn).append("(");
		} else
		{
			con.setAccessible(true);
			cni.append("constructor.newInstance(new Object[]{");
		}
		Class pts[] = con.getParameterTypes();
		for (int i = 0; i < pts.length; i++)
		{
			if (i > 0)
				cni.append(',');
			cni.append(defaultArg(pts[i]));
		}

		if (!dn)
			cni.append("}");
		cni.append("); }");
		Map pms = propertyMetadatas(c);
		List builders = new ArrayList(fs.length);
		for (int i = 0; i < fs.length; i++)
		{
			f = fs[i];
			String fn = f.getName();
			Class ft = f.getType();
			String ftn = ReflectUtils.getName(ft);
			boolean da = isp && f.getDeclaringClass() == c && !Modifier.isPrivate(f.getModifiers());
			PropertyMetadata pm;
			if (da)
			{
				pm = null;
			} else
			{
				pm = (PropertyMetadata)pms.get(fn);
				if (pm != null && (pm.type != ft || pm.setter == null || pm.getter == null))
					pm = null;
			}
			crf.append("if( fc == ").append(i).append(" ) return;");
			if (ft.isPrimitive())
			{
				if (ft == Boolean.TYPE)
				{
					if (da)
					{
						cwf.append("$2.writeBool(v.").append(fn).append(");");
						crf.append("ret.").append(fn).append(" = $2.readBool();");
						continue;
					}
					if (pm != null)
					{
						cwf.append("$2.writeBool(v.").append(pm.getter).append("());");
						crf.append("ret.").append(pm.setter).append("($2.readBool());");
					} else
					{
						cwf.append("$2.writeBool(((Boolean)fields[").append(i).append("].get($1)).booleanValue());");
						crf.append("fields[").append(i).append("].set(ret, ($w)$2.readBool());");
					}
					continue;
				}
				if (ft == Byte.TYPE)
				{
					if (da)
					{
						cwf.append("$2.writeByte(v.").append(fn).append(");");
						crf.append("ret.").append(fn).append(" = $2.readByte();");
						continue;
					}
					if (pm != null)
					{
						cwf.append("$2.writeByte(v.").append(pm.getter).append("());");
						crf.append("ret.").append(pm.setter).append("($2.readByte());");
					} else
					{
						cwf.append("$2.writeByte(((Byte)fields[").append(i).append("].get($1)).byteValue());");
						crf.append("fields[").append(i).append("].set(ret, ($w)$2.readByte());");
					}
					continue;
				}
				if (ft == Character.TYPE)
				{
					if (da)
					{
						cwf.append("$2.writeShort((short)v.").append(fn).append(");");
						crf.append("ret.").append(fn).append(" = (char)$2.readShort();");
						continue;
					}
					if (pm != null)
					{
						cwf.append("$2.writeShort((short)v.").append(pm.getter).append("());");
						crf.append("ret.").append(pm.setter).append("((char)$2.readShort());");
					} else
					{
						cwf.append("$2.writeShort((short)((Character)fields[").append(i).append("].get($1)).charValue());");
						crf.append("fields[").append(i).append("].set(ret, ($w)((char)$2.readShort()));");
					}
					continue;
				}
				if (ft == Short.TYPE)
				{
					if (da)
					{
						cwf.append("$2.writeShort(v.").append(fn).append(");");
						crf.append("ret.").append(fn).append(" = $2.readShort();");
						continue;
					}
					if (pm != null)
					{
						cwf.append("$2.writeShort(v.").append(pm.getter).append("());");
						crf.append("ret.").append(pm.setter).append("($2.readShort());");
					} else
					{
						cwf.append("$2.writeShort(((Short)fields[").append(i).append("].get($1)).shortValue());");
						crf.append("fields[").append(i).append("].set(ret, ($w)$2.readShort());");
					}
					continue;
				}
				if (ft == Integer.TYPE)
				{
					if (da)
					{
						cwf.append("$2.writeInt(v.").append(fn).append(");");
						crf.append("ret.").append(fn).append(" = $2.readInt();");
						continue;
					}
					if (pm != null)
					{
						cwf.append("$2.writeInt(v.").append(pm.getter).append("());");
						crf.append("ret.").append(pm.setter).append("($2.readInt());");
					} else
					{
						cwf.append("$2.writeInt(((Integer)fields[").append(i).append("].get($1)).intValue());");
						crf.append("fields[").append(i).append("].set(ret, ($w)$2.readInt());");
					}
					continue;
				}
				if (ft == Long.TYPE)
				{
					if (da)
					{
						cwf.append("$2.writeLong(v.").append(fn).append(");");
						crf.append("ret.").append(fn).append(" = $2.readLong();");
						continue;
					}
					if (pm != null)
					{
						cwf.append("$2.writeLong(v.").append(pm.getter).append("());");
						crf.append("ret.").append(pm.setter).append("($2.readLong());");
					} else
					{
						cwf.append("$2.writeLong(((Long)fields[").append(i).append("].get($1)).longValue());");
						crf.append("fields[").append(i).append("].set(ret, ($w)$2.readLong());");
					}
					continue;
				}
				if (ft == Float.TYPE)
				{
					if (da)
					{
						cwf.append("$2.writeFloat(v.").append(fn).append(");");
						crf.append("ret.").append(fn).append(" = $2.readFloat();");
						continue;
					}
					if (pm != null)
					{
						cwf.append("$2.writeFloat(v.").append(pm.getter).append("());");
						crf.append("ret.").append(pm.setter).append("($2.readFloat());");
					} else
					{
						cwf.append("$2.writeFloat(((Float)fields[").append(i).append("].get($1)).floatValue());");
						crf.append("fields[").append(i).append("].set(ret, ($w)$2.readFloat());");
					}
					continue;
				}
				if (ft != Double.TYPE)
					continue;
				if (da)
				{
					cwf.append("$2.writeDouble(v.").append(fn).append(");");
					crf.append("ret.").append(fn).append(" = $2.readDouble();");
					continue;
				}
				if (pm != null)
				{
					cwf.append("$2.writeDouble(v.").append(pm.getter).append("());");
					crf.append("ret.").append(pm.setter).append("($2.readDouble());");
				} else
				{
					cwf.append("$2.writeDouble(((Double)fields[").append(i).append("].get($1)).doubleValue());");
					crf.append("fields[").append(i).append("].set(ret, ($w)$2.readDouble());");
				}
				continue;
			}
			if (ft == c)
			{
				if (da)
				{
					cwf.append("this.writeTo(v.").append(fn).append(", $2);");
					crf.append("ret.").append(fn).append(" = (").append(ftn).append(")this.parseFrom($2);");
					continue;
				}
				if (pm != null)
				{
					cwf.append("this.writeTo(v.").append(pm.getter).append("(), $2);");
					crf.append("ret.").append(pm.setter).append("((").append(ftn).append(")this.parseFrom($2));");
				} else
				{
					cwf.append("this.writeTo((").append(ftn).append(")fields[").append(i).append("].get($1), $2);");
					crf.append("fields[").append(i).append("].set(ret, this.parseFrom($2));");
				}
				continue;
			}
			int bc = builders.size();
			builders.add(register(ft));
			if (da)
			{
				cwf.append("builders[").append(bc).append("].writeTo(v.").append(fn).append(", $2);");
				crf.append("ret.").append(fn).append(" = (").append(ftn).append(")builders[").append(bc).append("].parseFrom($2);");
				continue;
			}
			if (pm != null)
			{
				cwf.append("builders[").append(bc).append("].writeTo(v.").append(pm.getter).append("(), $2);");
				crf.append("ret.").append(pm.setter).append("((").append(ftn).append(")builders[").append(bc).append("].parseFrom($2));");
			} else
			{
				cwf.append("builders[").append(bc).append("].writeTo((").append(ftn).append(")fields[").append(i).append("].get($1), $2);");
				crf.append("fields[").append(i).append("].set(ret, builders[").append(bc).append("].parseFrom($2));");
			}
		}

		crf.append("for(int i=").append(fs.length).append(";i<fc;i++) $2.skipAny();");
		if (isc)
		{
			cwf.append("$2.writeInt(v.size()); for(java.util.Iterator it=v.iterator();it.hasNext();){ $2.writeObject(it.next()); }");
			crf.append("int len = $2.readInt(); for(int i=0;i<len;i++) ret.add($2.readObject());");
		} else
		if (ism)
		{
			cwf.append("$2.writeInt(v.size()); for(java.util.Iterator it=v.entrySet().iterator();it.hasNext();){ java.util.Map.Entry entry = (java.util.Map.Entry)it.next(); $2.writeObject(entry.getKey()); $2.writeObject(entry.getValue()); }");
			crf.append("int len = $2.readInt(); for(int i=0;i<len;i++) ret.put($2.readObject(), $2.readObject());");
		}
		cwf.append(" }");
		crf.append(" }");
		ClassGenerator cg = ClassGenerator.newInstance(cl);
		cg.setClassName(bcn);
		cg.setSuperClass(com/autohome/turbo/common/serialize/support/dubbo/Builder$AbstractObjectBuilder);
		cg.addDefaultConstructor();
		cg.addField("public static java.lang.reflect.Field[] fields;");
		cg.addField((new StringBuilder()).append("public static ").append(BUILDER_CLASS_NAME).append("[] builders;").toString());
		if (!dn)
			cg.addField("public static java.lang.reflect.Constructor constructor;");
		cg.addMethod((new StringBuilder()).append("public Class getType(){ return ").append(cn).append(".class; }").toString());
		cg.addMethod(cwf.toString());
		cg.addMethod(crf.toString());
		cg.addMethod(cni.toString());
		Builder builder;
		try
		{
			Class wc = cg.toClass();
			wc.getField("fields").set(null, fs);
			wc.getField("builders").set(null, ((Object) (builders.toArray(new Builder[0]))));
			if (!dn)
				wc.getField("constructor").set(null, con);
			builder = (Builder)wc.newInstance();
		}
		catch (RuntimeException e)
		{
			throw e;
		}
		catch (Throwable e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}
		finally
		{
			cg.release();
		}
		cg.release();
		return builder;
		throw exception1;
	}

	private static Builder newEnumBuilder(Class c)
	{
		Exception exception;
		ClassLoader cl = ClassHelper.getCallerClassLoader(com/autohome/turbo/common/serialize/support/dubbo/Builder);
		String cn = c.getName();
		String bcn = (new StringBuilder()).append(BUILDER_CLASS_NAME).append("$bc").append(BUILDER_CLASS_COUNTER.getAndIncrement()).toString();
		StringBuilder cwt = (new StringBuilder("public void writeTo(Object obj, ")).append(com/autohome/turbo/common/serialize/support/dubbo/GenericObjectOutput.getName()).append(" out) throws java.io.IOException{");
		cwt.append(cn).append(" v = (").append(cn).append(")$1;");
		cwt.append("if( $1 == null ){ $2.writeUTF(null); }else{ $2.writeUTF(v.name()); } }");
		StringBuilder cpf = (new StringBuilder("public Object parseFrom(")).append(com/autohome/turbo/common/serialize/support/dubbo/GenericObjectInput.getName()).append(" in) throws java.io.IOException{");
		cpf.append("String name = $1.readUTF(); if( name == null ) return null; return (").append(cn).append(")Enum.valueOf(").append(cn).append(".class, name); }");
		ClassGenerator cg = ClassGenerator.newInstance(cl);
		cg.setClassName(bcn);
		cg.setSuperClass(com/autohome/turbo/common/serialize/support/dubbo/Builder);
		cg.addDefaultConstructor();
		cg.addMethod((new StringBuilder()).append("public Class getType(){ return ").append(cn).append(".class; }").toString());
		cg.addMethod(cwt.toString());
		cg.addMethod(cpf.toString());
		Builder builder;
		try
		{
			Class wc = cg.toClass();
			builder = (Builder)wc.newInstance();
		}
		catch (RuntimeException e)
		{
			throw e;
		}
		catch (Throwable e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}
		finally
		{
			cg.release();
		}
		cg.release();
		return builder;
		throw exception;
	}

	private static Map propertyMetadatas(Class c)
	{
		Map mm = new HashMap();
		Map ret = new HashMap();
		Method arr$[] = c.getMethods();
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			Method m = arr$[i$];
			if (m.getDeclaringClass() != java/lang/Object)
				mm.put(ReflectUtils.getDesc(m), m);
		}

		Iterator i$ = mm.entrySet().iterator();
		do
		{
			if (!i$.hasNext())
				break;
			java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
			String desc = (String)entry.getKey();
			Method method = (Method)entry.getValue();
			Matcher matcher;
			String pn;
			Class pt;
			PropertyMetadata pm;
			if ((matcher = ReflectUtils.GETTER_METHOD_DESC_PATTERN.matcher(desc)).matches() || (matcher = ReflectUtils.IS_HAS_CAN_METHOD_DESC_PATTERN.matcher(desc)).matches())
			{
				pn = propertyName(matcher.group(1));
				pt = method.getReturnType();
				pm = (PropertyMetadata)ret.get(pn);
				if (pm == null)
				{
					pm = new PropertyMetadata();
					pm.type = pt;
					ret.put(pn, pm);
				} else
				if (pm.type != pt)
					continue;
				pm.getter = method.getName();
				continue;
			}
			if (!(matcher = ReflectUtils.SETTER_METHOD_DESC_PATTERN.matcher(desc)).matches())
				continue;
			pn = propertyName(matcher.group(1));
			pt = method.getParameterTypes()[0];
			pm = (PropertyMetadata)ret.get(pn);
			if (pm == null)
			{
				pm = new PropertyMetadata();
				pm.type = pt;
				ret.put(pn, pm);
			} else
			if (pm.type != pt)
				continue;
			pm.setter = method.getName();
		} while (true);
		return ret;
	}

	private static String propertyName(String s)
	{
		return s.length() != 1 && !Character.isLowerCase(s.charAt(1)) ? s : (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
	}

	private static boolean serializeIgnoreFinalModifier(Class cl)
	{
		return false;
	}

	private static boolean isPrimitiveOrPrimitiveArray1(Class cl)
	{
		if (cl.isPrimitive())
			return true;
		Class clazz = cl.getClass().getComponentType();
		return clazz != null && clazz.isPrimitive();
	}

	private static String defaultArg(Class cl)
	{
		if (Boolean.TYPE == cl)
			return "false";
		if (Integer.TYPE == cl)
			return "0";
		if (Long.TYPE == cl)
			return "0l";
		if (Double.TYPE == cl)
			return "(double)0";
		if (Float.TYPE == cl)
			return "(float)0";
		if (Short.TYPE == cl)
			return "(short)0";
		if (Character.TYPE == cl)
			return "(char)0";
		if (Byte.TYPE == cl)
			return "(byte)0";
		if ([B == cl)
			return "new byte[]{0}";
		if (!cl.isPrimitive())
			return "null";
		else
			throw new UnsupportedOperationException();
	}

	private static int compareFieldName(String n1, String n2)
	{
		int l = Math.min(n1.length(), n2.length());
		for (int i = 0; i < l; i++)
		{
			int t = n1.charAt(i) - n2.charAt(i);
			if (t != 0)
				return t;
		}

		return n1.length() - n2.length();
	}

	private static void addDesc(Class c)
	{
		String desc = ReflectUtils.getDesc(c);
		int index = mDescList.size();
		mDescList.add(desc);
		mDescMap.put(desc, Integer.valueOf(index));
	}

	static 
	{
		addDesc([Z);
		addDesc([B);
		addDesc([C);
		addDesc([S);
		addDesc([I);
		addDesc([J);
		addDesc([F);
		addDesc([D);
		addDesc(java/lang/Boolean);
		addDesc(java/lang/Byte);
		addDesc(java/lang/Character);
		addDesc(java/lang/Short);
		addDesc(java/lang/Integer);
		addDesc(java/lang/Long);
		addDesc(java/lang/Float);
		addDesc(java/lang/Double);
		addDesc(java/lang/String);
		addDesc([Ljava/lang/String;);
		addDesc(java/util/ArrayList);
		addDesc(java/util/HashMap);
		addDesc(java/util/HashSet);
		addDesc(java/util/Date);
		addDesc(java/sql/Date);
		addDesc(java/sql/Time);
		addDesc(java/sql/Timestamp);
		addDesc(java/util/LinkedList);
		addDesc(java/util/LinkedHashMap);
		addDesc(java/util/LinkedHashSet);
		register([B, new Builder() {

			public Class getType()
			{
				return [B;
			}

			public void writeTo(byte obj[], GenericObjectOutput out)
				throws IOException
			{
				out.writeBytes(obj);
			}

			public byte[] parseFrom(GenericObjectInput in)
				throws IOException
			{
				return in.readBytes();
			}

			public volatile Object parseFrom(GenericObjectInput x0)
				throws IOException
			{
				return parseFrom(x0);
			}

			public volatile void writeTo(Object x0, GenericObjectOutput x1)
				throws IOException
			{
				writeTo((byte[])x0, x1);
			}

		});
		register(java/lang/Boolean, new Builder() {

			public Class getType()
			{
				return java/lang/Boolean;
			}

			public void writeTo(Boolean obj, GenericObjectOutput out)
				throws IOException
			{
				if (obj == null)
					out.write0((byte)24);
				else
				if (obj.booleanValue())
					out.write0((byte)26);
				else
					out.write0((byte)25);
			}

			public Boolean parseFrom(GenericObjectInput in)
				throws IOException
			{
				byte b = in.read0();
				switch (b)
				{
				case 24: // '\030'
					return null;

				case 25: // '\031'
					return Boolean.FALSE;

				case 26: // '\032'
					return Boolean.TRUE;
				}
				throw new IOException((new StringBuilder()).append("Input format error, expect VARINT_N1|VARINT_0|VARINT_1, get ").append(b).append(".").toString());
			}

			public volatile Object parseFrom(GenericObjectInput x0)
				throws IOException
			{
				return parseFrom(x0);
			}

			public volatile void writeTo(Object x0, GenericObjectOutput x1)
				throws IOException
			{
				writeTo((Boolean)x0, x1);
			}

		});
		register(java/lang/Byte, new Builder() {

			public Class getType()
			{
				return java/lang/Byte;
			}

			public void writeTo(Byte obj, GenericObjectOutput out)
				throws IOException
			{
				if (obj == null)
				{
					out.write0((byte)-108);
				} else
				{
					out.write0((byte)-124);
					out.writeByte(obj.byteValue());
				}
			}

			public Byte parseFrom(GenericObjectInput in)
				throws IOException
			{
				byte b = in.read0();
				if (b == -108)
					return null;
				if (b != -124)
					throw new IOException((new StringBuilder()).append("Input format error, expect OBJECT_NULL|OBJECT_VALUE, get ").append(b).append(".").toString());
				else
					return Byte.valueOf(in.readByte());
			}

			public volatile Object parseFrom(GenericObjectInput x0)
				throws IOException
			{
				return parseFrom(x0);
			}

			public volatile void writeTo(Object x0, GenericObjectOutput x1)
				throws IOException
			{
				writeTo((Byte)x0, x1);
			}

		});
		register(java/lang/Character, new Builder() {

			public Class getType()
			{
				return java/lang/Character;
			}

			public void writeTo(Character obj, GenericObjectOutput out)
				throws IOException
			{
				if (obj == null)
				{
					out.write0((byte)-108);
				} else
				{
					out.write0((byte)-124);
					out.writeShort((short)obj.charValue());
				}
			}

			public Character parseFrom(GenericObjectInput in)
				throws IOException
			{
				byte b = in.read0();
				if (b == -108)
					return null;
				if (b != -124)
					throw new IOException((new StringBuilder()).append("Input format error, expect OBJECT_NULL|OBJECT_VALUE, get ").append(b).append(".").toString());
				else
					return Character.valueOf((char)in.readShort());
			}

			public volatile Object parseFrom(GenericObjectInput x0)
				throws IOException
			{
				return parseFrom(x0);
			}

			public volatile void writeTo(Object x0, GenericObjectOutput x1)
				throws IOException
			{
				writeTo((Character)x0, x1);
			}

		});
		register(java/lang/Short, new Builder() {

			public Class getType()
			{
				return java/lang/Short;
			}

			public void writeTo(Short obj, GenericObjectOutput out)
				throws IOException
			{
				if (obj == null)
				{
					out.write0((byte)-108);
				} else
				{
					out.write0((byte)-124);
					out.writeShort(obj.shortValue());
				}
			}

			public Short parseFrom(GenericObjectInput in)
				throws IOException
			{
				byte b = in.read0();
				if (b == -108)
					return null;
				if (b != -124)
					throw new IOException((new StringBuilder()).append("Input format error, expect OBJECT_NULL|OBJECT_VALUE, get ").append(b).append(".").toString());
				else
					return Short.valueOf(in.readShort());
			}

			public volatile Object parseFrom(GenericObjectInput x0)
				throws IOException
			{
				return parseFrom(x0);
			}

			public volatile void writeTo(Object x0, GenericObjectOutput x1)
				throws IOException
			{
				writeTo((Short)x0, x1);
			}

		});
		register(java/lang/Integer, new Builder() {

			public Class getType()
			{
				return java/lang/Integer;
			}

			public void writeTo(Integer obj, GenericObjectOutput out)
				throws IOException
			{
				if (obj == null)
				{
					out.write0((byte)-108);
				} else
				{
					out.write0((byte)-124);
					out.writeInt(obj.intValue());
				}
			}

			public Integer parseFrom(GenericObjectInput in)
				throws IOException
			{
				byte b = in.read0();
				if (b == -108)
					return null;
				if (b != -124)
					throw new IOException((new StringBuilder()).append("Input format error, expect OBJECT_NULL|OBJECT_VALUE, get ").append(b).append(".").toString());
				else
					return Integer.valueOf(in.readInt());
			}

			public volatile Object parseFrom(GenericObjectInput x0)
				throws IOException
			{
				return parseFrom(x0);
			}

			public volatile void writeTo(Object x0, GenericObjectOutput x1)
				throws IOException
			{
				writeTo((Integer)x0, x1);
			}

		});
		register(java/lang/Long, new Builder() {

			public Class getType()
			{
				return java/lang/Long;
			}

			public void writeTo(Long obj, GenericObjectOutput out)
				throws IOException
			{
				if (obj == null)
				{
					out.write0((byte)-108);
				} else
				{
					out.write0((byte)-124);
					out.writeLong(obj.longValue());
				}
			}

			public Long parseFrom(GenericObjectInput in)
				throws IOException
			{
				byte b = in.read0();
				if (b == -108)
					return null;
				if (b != -124)
					throw new IOException((new StringBuilder()).append("Input format error, expect OBJECT_NULL|OBJECT_VALUE, get ").append(b).append(".").toString());
				else
					return Long.valueOf(in.readLong());
			}

			public volatile Object parseFrom(GenericObjectInput x0)
				throws IOException
			{
				return parseFrom(x0);
			}

			public volatile void writeTo(Object x0, GenericObjectOutput x1)
				throws IOException
			{
				writeTo((Long)x0, x1);
			}

		});
		register(java/lang/Float, new Builder() {

			public Class getType()
			{
				return java/lang/Float;
			}

			public void writeTo(Float obj, GenericObjectOutput out)
				throws IOException
			{
				if (obj == null)
				{
					out.write0((byte)-108);
				} else
				{
					out.write0((byte)-124);
					out.writeFloat(obj.floatValue());
				}
			}

			public Float parseFrom(GenericObjectInput in)
				throws IOException
			{
				byte b = in.read0();
				if (b == -108)
					return null;
				if (b != -124)
					throw new IOException((new StringBuilder()).append("Input format error, expect OBJECT_NULL|OBJECT_VALUE, get ").append(b).append(".").toString());
				else
					return new Float(in.readFloat());
			}

			public volatile Object parseFrom(GenericObjectInput x0)
				throws IOException
			{
				return parseFrom(x0);
			}

			public volatile void writeTo(Object x0, GenericObjectOutput x1)
				throws IOException
			{
				writeTo((Float)x0, x1);
			}

		});
		register(java/lang/Double, new Builder() {

			public Class getType()
			{
				return java/lang/Double;
			}

			public void writeTo(Double obj, GenericObjectOutput out)
				throws IOException
			{
				if (obj == null)
				{
					out.write0((byte)-108);
				} else
				{
					out.write0((byte)-124);
					out.writeDouble(obj.doubleValue());
				}
			}

			public Double parseFrom(GenericObjectInput in)
				throws IOException
			{
				byte b = in.read0();
				if (b == -108)
					return null;
				if (b != -124)
					throw new IOException((new StringBuilder()).append("Input format error, expect OBJECT_NULL|OBJECT_VALUE, get ").append(b).append(".").toString());
				else
					return new Double(in.readDouble());
			}

			public volatile Object parseFrom(GenericObjectInput x0)
				throws IOException
			{
				return parseFrom(x0);
			}

			public volatile void writeTo(Object x0, GenericObjectOutput x1)
				throws IOException
			{
				writeTo((Double)x0, x1);
			}

		});
		register(java/lang/String, new Builder() {

			public Class getType()
			{
				return java/lang/String;
			}

			public String parseFrom(GenericObjectInput in)
				throws IOException
			{
				return in.readUTF();
			}

			public void writeTo(String obj, GenericObjectOutput out)
				throws IOException
			{
				out.writeUTF(obj);
			}

			public volatile Object parseFrom(GenericObjectInput x0)
				throws IOException
			{
				return parseFrom(x0);
			}

			public volatile void writeTo(Object x0, GenericObjectOutput x1)
				throws IOException
			{
				writeTo((String)x0, x1);
			}

		});
		register(java/lang/StringBuilder, new Builder() {

			public Class getType()
			{
				return java/lang/StringBuilder;
			}

			public StringBuilder parseFrom(GenericObjectInput in)
				throws IOException
			{
				return new StringBuilder(in.readUTF());
			}

			public void writeTo(StringBuilder obj, GenericObjectOutput out)
				throws IOException
			{
				out.writeUTF(obj.toString());
			}

			public volatile Object parseFrom(GenericObjectInput x0)
				throws IOException
			{
				return parseFrom(x0);
			}

			public volatile void writeTo(Object x0, GenericObjectOutput x1)
				throws IOException
			{
				writeTo((StringBuilder)x0, x1);
			}

		});
		register(java/lang/StringBuffer, new Builder() {

			public Class getType()
			{
				return java/lang/StringBuffer;
			}

			public StringBuffer parseFrom(GenericObjectInput in)
				throws IOException
			{
				return new StringBuffer(in.readUTF());
			}

			public void writeTo(StringBuffer obj, GenericObjectOutput out)
				throws IOException
			{
				out.writeUTF(obj.toString());
			}

			public volatile Object parseFrom(GenericObjectInput x0)
				throws IOException
			{
				return parseFrom(x0);
			}

			public volatile void writeTo(Object x0, GenericObjectOutput x1)
				throws IOException
			{
				writeTo((StringBuffer)x0, x1);
			}

		});
		register(java/util/ArrayList, new Builder() {

			public Class getType()
			{
				return java/util/ArrayList;
			}

			public void writeTo(ArrayList obj, GenericObjectOutput out)
				throws IOException
			{
				if (obj == null)
				{
					out.write0((byte)-108);
				} else
				{
					out.write0((byte)-123);
					out.writeUInt(obj.size());
					Object item;
					for (Iterator i$ = obj.iterator(); i$.hasNext(); out.writeObject(item))
						item = i$.next();

				}
			}

			public ArrayList parseFrom(GenericObjectInput in)
				throws IOException
			{
				byte b = in.read0();
				if (b == -108)
					return null;
				if (b != -123)
					throw new IOException((new StringBuilder()).append("Input format error, expect OBJECT_NULL|OBJECT_VALUES, get ").append(b).append(".").toString());
				int len = in.readUInt();
				ArrayList ret = new ArrayList(len);
				for (int i = 0; i < len; i++)
					ret.add(in.readObject());

				return ret;
			}

			public volatile Object parseFrom(GenericObjectInput x0)
				throws IOException
			{
				return parseFrom(x0);
			}

			public volatile void writeTo(Object x0, GenericObjectOutput x1)
				throws IOException
			{
				writeTo((ArrayList)x0, x1);
			}

		});
		register(java/util/HashMap, new Builder() {

			public Class getType()
			{
				return java/util/HashMap;
			}

			public void writeTo(HashMap obj, GenericObjectOutput out)
				throws IOException
			{
				if (obj == null)
				{
					out.write0((byte)-108);
				} else
				{
					out.write0((byte)-122);
					out.writeUInt(obj.size());
					java.util.Map.Entry entry;
					for (Iterator i$ = obj.entrySet().iterator(); i$.hasNext(); out.writeObject(entry.getValue()))
					{
						entry = (java.util.Map.Entry)i$.next();
						out.writeObject(entry.getKey());
					}

				}
			}

			public HashMap parseFrom(GenericObjectInput in)
				throws IOException
			{
				byte b = in.read0();
				if (b == -108)
					return null;
				if (b != -122)
					throw new IOException((new StringBuilder()).append("Input format error, expect OBJECT_NULL|OBJECT_MAP, get ").append(b).append(".").toString());
				int len = in.readUInt();
				HashMap ret = new HashMap(len);
				for (int i = 0; i < len; i++)
					ret.put(in.readObject(), in.readObject());

				return ret;
			}

			public volatile Object parseFrom(GenericObjectInput x0)
				throws IOException
			{
				return parseFrom(x0);
			}

			public volatile void writeTo(Object x0, GenericObjectOutput x1)
				throws IOException
			{
				writeTo((HashMap)x0, x1);
			}

		});
		register(java/util/HashSet, new Builder() {

			public Class getType()
			{
				return java/util/HashSet;
			}

			public void writeTo(HashSet obj, GenericObjectOutput out)
				throws IOException
			{
				if (obj == null)
				{
					out.write0((byte)-108);
				} else
				{
					out.write0((byte)-123);
					out.writeUInt(obj.size());
					Object item;
					for (Iterator i$ = obj.iterator(); i$.hasNext(); out.writeObject(item))
						item = i$.next();

				}
			}

			public HashSet parseFrom(GenericObjectInput in)
				throws IOException
			{
				byte b = in.read0();
				if (b == -108)
					return null;
				if (b != -123)
					throw new IOException((new StringBuilder()).append("Input format error, expect OBJECT_NULL|OBJECT_VALUES, get ").append(b).append(".").toString());
				int len = in.readUInt();
				HashSet ret = new HashSet(len);
				for (int i = 0; i < len; i++)
					ret.add(in.readObject());

				return ret;
			}

			public volatile Object parseFrom(GenericObjectInput x0)
				throws IOException
			{
				return parseFrom(x0);
			}

			public volatile void writeTo(Object x0, GenericObjectOutput x1)
				throws IOException
			{
				writeTo((HashSet)x0, x1);
			}

		});
		register(java/util/Date, new Builder() {

			public Class getType()
			{
				return java/util/Date;
			}

			public void writeTo(Date obj, GenericObjectOutput out)
				throws IOException
			{
				if (obj == null)
				{
					out.write0((byte)-108);
				} else
				{
					out.write0((byte)-124);
					out.writeLong(obj.getTime());
				}
			}

			public Date parseFrom(GenericObjectInput in)
				throws IOException
			{
				byte b = in.read0();
				if (b == -108)
					return null;
				if (b != -124)
					throw new IOException((new StringBuilder()).append("Input format error, expect OBJECT_NULL|OBJECT_VALUE, get ").append(b).append(".").toString());
				else
					return new Date(in.readLong());
			}

			public volatile Object parseFrom(GenericObjectInput x0)
				throws IOException
			{
				return parseFrom(x0);
			}

			public volatile void writeTo(Object x0, GenericObjectOutput x1)
				throws IOException
			{
				writeTo((Date)x0, x1);
			}

		});
		register(java/sql/Date, new Builder() {

			public Class getType()
			{
				return java/sql/Date;
			}

			public void writeTo(java.sql.Date obj, GenericObjectOutput out)
				throws IOException
			{
				if (obj == null)
				{
					out.write0((byte)-108);
				} else
				{
					out.write0((byte)-124);
					out.writeLong(obj.getTime());
				}
			}

			public java.sql.Date parseFrom(GenericObjectInput in)
				throws IOException
			{
				byte b = in.read0();
				if (b == -108)
					return null;
				if (b != -124)
					throw new IOException((new StringBuilder()).append("Input format error, expect OBJECT_NULL|OBJECT_VALUE, get ").append(b).append(".").toString());
				else
					return new java.sql.Date(in.readLong());
			}

			public volatile Object parseFrom(GenericObjectInput x0)
				throws IOException
			{
				return parseFrom(x0);
			}

			public volatile void writeTo(Object x0, GenericObjectOutput x1)
				throws IOException
			{
				writeTo((java.sql.Date)x0, x1);
			}

		});
		register(java/sql/Timestamp, new Builder() {

			public Class getType()
			{
				return java/sql/Timestamp;
			}

			public void writeTo(Timestamp obj, GenericObjectOutput out)
				throws IOException
			{
				if (obj == null)
				{
					out.write0((byte)-108);
				} else
				{
					out.write0((byte)-124);
					out.writeLong(obj.getTime());
				}
			}

			public Timestamp parseFrom(GenericObjectInput in)
				throws IOException
			{
				byte b = in.read0();
				if (b == -108)
					return null;
				if (b != -124)
					throw new IOException((new StringBuilder()).append("Input format error, expect OBJECT_NULL|OBJECT_VALUE, get ").append(b).append(".").toString());
				else
					return new Timestamp(in.readLong());
			}

			public volatile Object parseFrom(GenericObjectInput x0)
				throws IOException
			{
				return parseFrom(x0);
			}

			public volatile void writeTo(Object x0, GenericObjectOutput x1)
				throws IOException
			{
				writeTo((Timestamp)x0, x1);
			}

		});
		register(java/sql/Time, new Builder() {

			public Class getType()
			{
				return java/sql/Time;
			}

			public void writeTo(Time obj, GenericObjectOutput out)
				throws IOException
			{
				if (obj == null)
				{
					out.write0((byte)-108);
				} else
				{
					out.write0((byte)-124);
					out.writeLong(obj.getTime());
				}
			}

			public Time parseFrom(GenericObjectInput in)
				throws IOException
			{
				byte b = in.read0();
				if (b == -108)
					return null;
				if (b != -124)
					throw new IOException((new StringBuilder()).append("Input format error, expect OBJECT_NULL|OBJECT_VALUE, get ").append(b).append(".").toString());
				else
					return new Time(in.readLong());
			}

			public volatile Object parseFrom(GenericObjectInput x0)
				throws IOException
			{
				return parseFrom(x0);
			}

			public volatile void writeTo(Object x0, GenericObjectOutput x1)
				throws IOException
			{
				writeTo((Time)x0, x1);
			}

		});
	}



}
