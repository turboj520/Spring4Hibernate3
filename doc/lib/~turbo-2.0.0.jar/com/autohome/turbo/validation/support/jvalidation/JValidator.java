// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JValidator.java

package com.autohome.turbo.validation.support.jvalidation;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.bytecode.ClassGenerator;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.ReflectUtils;
import com.autohome.turbo.validation.Validator;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewConstructor;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.ByteMemberValue;
import javassist.bytecode.annotation.CharMemberValue;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.DoubleMemberValue;
import javassist.bytecode.annotation.EnumMemberValue;
import javassist.bytecode.annotation.FloatMemberValue;
import javassist.bytecode.annotation.IntegerMemberValue;
import javassist.bytecode.annotation.LongMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.ShortMemberValue;
import javassist.bytecode.annotation.StringMemberValue;
import javax.validation.Configuration;
import javax.validation.Constraint;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.bootstrap.ProviderSpecificBootstrap;
import javax.validation.groups.Default;

public class JValidator
	implements Validator
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/validation/support/jvalidation/JValidator);
	private final Class clazz;
	private final javax.validation.Validator validator;

	public JValidator(URL url)
	{
		clazz = ReflectUtils.forName(url.getServiceInterface());
		String jvalidation = url.getParameter("jvalidation");
		ValidatorFactory factory;
		if (jvalidation != null && jvalidation.length() > 0)
			factory = Validation.byProvider(ReflectUtils.forName(jvalidation)).configure().buildValidatorFactory();
		else
			factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	public void validate(String methodName, Class parameterTypes[], Object arguments[])
		throws Exception
	{
		String methodClassName = (new StringBuilder()).append(clazz.getName()).append("_").append(toUpperMethoName(methodName)).toString();
		Class methodClass = null;
		try
		{
			methodClass = Class.forName(methodClassName, false, Thread.currentThread().getContextClassLoader());
		}
		catch (ClassNotFoundException e) { }
		Set violations = new HashSet();
		Method method = clazz.getMethod(methodName, parameterTypes);
		Object parameterBean = getMethodParameterBean(clazz, method, arguments);
		if (parameterBean != null)
			if (methodClass != null)
				violations.addAll(validator.validate(parameterBean, new Class[] {
					javax/validation/groups/Default, clazz, methodClass
				}));
			else
				violations.addAll(validator.validate(parameterBean, new Class[] {
					javax/validation/groups/Default, clazz
				}));
		Object arr$[] = arguments;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			Object arg = arr$[i$];
			validate(violations, arg, clazz, methodClass);
		}

		if (violations.size() > 0)
			throw new ConstraintViolationException((new StringBuilder()).append("Failed to validate service: ").append(clazz.getName()).append(", method: ").append(methodName).append(", cause: ").append(violations).toString(), violations);
		else
			return;
	}

	private void validate(Set violations, Object arg, Class clazz, Class methodClass)
	{
		if (arg != null && !isPrimitives(arg.getClass()))
			if ([Ljava/lang/Object;.isInstance(arg))
			{
				Object arr$[] = (Object[])(Object[])arg;
				int len$ = arr$.length;
				for (int i$ = 0; i$ < len$; i$++)
				{
					Object item = arr$[i$];
					validate(violations, item, clazz, methodClass);
				}

			} else
			if (java/util/Collection.isInstance(arg))
			{
				Object item;
				for (Iterator i$ = ((Collection)arg).iterator(); i$.hasNext(); validate(violations, item, clazz, methodClass))
					item = i$.next();

			} else
			if (java/util/Map.isInstance(arg))
			{
				java.util.Map.Entry entry;
				for (Iterator i$ = ((Map)arg).entrySet().iterator(); i$.hasNext(); validate(violations, entry.getValue(), clazz, methodClass))
				{
					entry = (java.util.Map.Entry)i$.next();
					validate(violations, entry.getKey(), clazz, methodClass);
				}

			} else
			if (methodClass != null)
				violations.addAll(validator.validate(arg, new Class[] {
					javax/validation/groups/Default, clazz, methodClass
				}));
			else
				violations.addAll(validator.validate(arg, new Class[] {
					javax/validation/groups/Default, clazz
				}));
	}

	private static boolean isPrimitives(Class cls)
	{
		if (cls.isArray())
			return isPrimitive(cls.getComponentType());
		else
			return isPrimitive(cls);
	}

	private static boolean isPrimitive(Class cls)
	{
		return cls.isPrimitive() || cls == java/lang/String || cls == java/lang/Boolean || cls == java/lang/Character || java/lang/Number.isAssignableFrom(cls) || java/util/Date.isAssignableFrom(cls);
	}

	private static Object getMethodParameterBean(Class clazz, Method method, Object args[])
	{
		if (!hasConstraintParameter(method))
			return null;
		Object parameterBean;
		String upperName = toUpperMethoName(method.getName());
		String parameterSimpleName = (new StringBuilder()).append(upperName).append("Parameter").toString();
		String parameterClassName = (new StringBuilder()).append(clazz.getName()).append("_").append(parameterSimpleName).toString();
		Class parameterClass;
		try
		{
			parameterClass = Class.forName(parameterClassName, true, clazz.getClassLoader());
		}
		catch (ClassNotFoundException e)
		{
			ClassPool pool = ClassGenerator.getClassPool(clazz.getClassLoader());
			CtClass ctClass = pool.makeClass(parameterClassName);
			ClassFile classFile = ctClass.getClassFile();
			classFile.setVersionToJava5();
			ctClass.addConstructor(CtNewConstructor.defaultConstructor(pool.getCtClass(parameterClassName)));
			Class parameterTypes[] = method.getParameterTypes();
			java.lang.annotation.Annotation parameterAnnotations[][] = method.getParameterAnnotations();
			for (int i = 0; i < parameterTypes.length; i++)
			{
				Class type = parameterTypes[i];
				java.lang.annotation.Annotation annotations[] = parameterAnnotations[i];
				AnnotationsAttribute attribute = new AnnotationsAttribute(classFile.getConstPool(), "RuntimeVisibleAnnotations");
				java.lang.annotation.Annotation arr$[] = annotations;
				int len$ = arr$.length;
				for (int i$ = 0; i$ < len$; i$++)
				{
					java.lang.annotation.Annotation annotation = arr$[i$];
					if (!annotation.annotationType().isAnnotationPresent(javax/validation/Constraint))
						continue;
					Annotation ja = new Annotation(classFile.getConstPool(), pool.getCtClass(annotation.annotationType().getName()));
					Method members[] = annotation.annotationType().getMethods();
					Method arr$[] = members;
					int len$ = arr$.length;
					for (int i$ = 0; i$ < len$; i$++)
					{
						Method member = arr$[i$];
						if (!Modifier.isPublic(member.getModifiers()) || member.getParameterTypes().length != 0 || member.getDeclaringClass() != annotation.annotationType())
							continue;
						Object value = member.invoke(annotation, new Object[0]);
						if (value != null && !value.equals(member.getDefaultValue()))
						{
							MemberValue memberValue = createMemberValue(classFile.getConstPool(), pool.get(member.getReturnType().getName()), value);
							ja.addMemberValue(member.getName(), memberValue);
						}
					}

					attribute.addAnnotation(ja);
				}

				String fieldName = (new StringBuilder()).append(method.getName()).append("Argument").append(i).toString();
				CtField ctField = CtField.make((new StringBuilder()).append("public ").append(type.getCanonicalName()).append(" ").append(fieldName).append(";").toString(), pool.getCtClass(parameterClassName));
				ctField.getFieldInfo().addAttribute(attribute);
				ctClass.addField(ctField);
			}

			parameterClass = ctClass.toClass();
		}
		parameterBean = parameterClass.newInstance();
		for (int i = 0; i < args.length; i++)
		{
			Field field = parameterClass.getField((new StringBuilder()).append(method.getName()).append("Argument").append(i).toString());
			field.set(parameterBean, args[i]);
		}

		return parameterBean;
		Throwable e;
		e;
		logger.warn(e.getMessage(), e);
		return null;
	}

	private static boolean hasConstraintParameter(Method method)
	{
		java.lang.annotation.Annotation parameterAnnotations[][] = method.getParameterAnnotations();
		if (parameterAnnotations != null && parameterAnnotations.length > 0)
		{
			java.lang.annotation.Annotation arr$[][] = parameterAnnotations;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				java.lang.annotation.Annotation annotations[] = arr$[i$];
				java.lang.annotation.Annotation arr$[] = annotations;
				int len$ = arr$.length;
				for (int i$ = 0; i$ < len$; i$++)
				{
					java.lang.annotation.Annotation annotation = arr$[i$];
					if (annotation.annotationType().isAnnotationPresent(javax/validation/Constraint))
						return true;
				}

			}

		}
		return false;
	}

	private static String toUpperMethoName(String methodName)
	{
		return (new StringBuilder()).append(methodName.substring(0, 1).toUpperCase()).append(methodName.substring(1)).toString();
	}

	private static MemberValue createMemberValue(ConstPool cp, CtClass type, Object value)
		throws NotFoundException
	{
		MemberValue memberValue = Annotation.createMemberValue(cp, type);
		if (memberValue instanceof BooleanMemberValue)
			((BooleanMemberValue)memberValue).setValue(((Boolean)value).booleanValue());
		else
		if (memberValue instanceof ByteMemberValue)
			((ByteMemberValue)memberValue).setValue(((Byte)value).byteValue());
		else
		if (memberValue instanceof CharMemberValue)
			((CharMemberValue)memberValue).setValue(((Character)value).charValue());
		else
		if (memberValue instanceof ShortMemberValue)
			((ShortMemberValue)memberValue).setValue(((Short)value).shortValue());
		else
		if (memberValue instanceof IntegerMemberValue)
			((IntegerMemberValue)memberValue).setValue(((Integer)value).intValue());
		else
		if (memberValue instanceof LongMemberValue)
			((LongMemberValue)memberValue).setValue(((Long)value).longValue());
		else
		if (memberValue instanceof FloatMemberValue)
			((FloatMemberValue)memberValue).setValue(((Float)value).floatValue());
		else
		if (memberValue instanceof DoubleMemberValue)
			((DoubleMemberValue)memberValue).setValue(((Double)value).doubleValue());
		else
		if (memberValue instanceof ClassMemberValue)
			((ClassMemberValue)memberValue).setValue(((Class)value).getName());
		else
		if (memberValue instanceof StringMemberValue)
			((StringMemberValue)memberValue).setValue((String)value);
		else
		if (memberValue instanceof EnumMemberValue)
			((EnumMemberValue)memberValue).setValue(((Enum)value).name());
		else
		if (memberValue instanceof ArrayMemberValue)
		{
			CtClass arrayType = type.getComponentType();
			int len = Array.getLength(value);
			MemberValue members[] = new MemberValue[len];
			for (int i = 0; i < len; i++)
				members[i] = createMemberValue(cp, arrayType, Array.get(value, i));

			((ArrayMemberValue)memberValue).setValue(members);
		}
		return memberValue;
	}

}
