package com.keung.spring4hibernate3.common.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * 反射工具类
 */
public class ReflectionUtils
{
	private static Logger logger = LoggerFactory.getLogger(ReflectionUtils.class);

	public static Object newInstance(String className, Object[] args) throws Exception
	{
        Class newoneClass = Class.forName(className);   
        return newInstance(newoneClass, args);
    }
	
	public static Object newInstance(Class clazz, Object[] args) throws Exception
	{  
        Class[] argsClass = new Class[args.length];   
        for (int i = 0, j = args.length; i < j; i++) {   
            argsClass[i] = args[i].getClass();   
        }   
        Constructor cons = clazz.getConstructor(argsClass);  
        return cons.newInstance(args);
    }
	
	/**
	 * 循环向上转型, 获取对象的DeclaredField.
	 * @param object
	 * @param fieldName
	 * @return 如向上转型到Object仍无法找到, 返回null.
	 */
	private static Field getDeclaredField(Object object, String fieldName)
	{
		Assert.notNull(object, "object不能为空");
		Assert.hasText(fieldName, "fieldName");
		for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz.getSuperclass())
		{
			try
			{
				return clazz.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e)
			{
				// Field不在当前类定义,继续向上转型
			}
		}
		return null;
	}

	/**
	 * 循环向上转型, 获取对象的DeclaredMethod.
	 * @param object
	 * @param methodName
	 * @param parameterTypes
	 * @return 如向上转型到Object仍无法找到, 返回null.
	 */
	private static Method getDeclaredMethod(Object object, String methodName, Class<?>[] parameterTypes)
	{
		Assert.notNull(object, "object不能为空");
		Assert.hasText(methodName, "methodName");

		for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz.getSuperclass())
		{
			try
			{
				return clazz.getDeclaredMethod(methodName, parameterTypes);
			} catch (NoSuchMethodException e)
			{
				// Method不在当前类定义,继续向上转型
			}
		}
		return null;
	}

	/**
	 * 直接读取对象属性值, 无视private/protected修饰符, 不经过getter函数.
	 * @param object
	 * @param fieldName
	 * @return
	 */
	public static Object getFieldValue(Object object, String fieldName)
	{
		Field field = getDeclaredField(object, fieldName);

		if (field == null)
		{
			throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");
		}

		makeAccessible(field);

		Object result = null;
		try
		{
			result = field.get(object);
		} catch (IllegalAccessException e)
		{
			logger.error("不可能抛出的异常{}", e.getMessage());
		}
		return result;
	}

	/**
	 * 直接设置对象属性值, 无视private/protected修饰符, 不经过setter函数.
	 * @param object
	 * @param fieldName
	 * @param fieldValue
	 */
	public static void setFieldValue(Object object, String fieldName, Object fieldValue)
	{
		Field field = getDeclaredField(object, fieldName);

		if (field == null)
		{
			throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");
		}

		makeAccessible(field);

		try
		{
			field.set(object, fieldValue);
		} catch (IllegalAccessException e)
		{
			logger.error("不可能抛出的异常:{}", e.getMessage());
		}
	}
	
	//调用静态属性
	public Object getStaticProperty(String className, String fieldName) throws Exception
	{                                         
		Class ownerClass = Class.forName(className);                       
		Field field = ownerClass.getField(fieldName);                      
		Object property = field.get(ownerClass);                           
		return property;                                                   
	}

	//调用静态方法
	public static Object invokeStaticMethod(String className, String methodName, Object[] args) throws Exception
	{                                    
		Class ownerClass = Class.forName(className);                                 
		Class[] argsClass = new Class[args.length];                                  
		for (int i = 0, j = args.length; i < j; i++) {                               
			argsClass[i] = args[i].getClass();                                       
		}                                                                            
		Method method = ownerClass.getMethod(methodName, argsClass);                 
		return method.invoke(null, args);                                            
	}
	
	public static Object invokeClazzMethod(String className, String methodName, Object[] args) throws Exception
	{                                    
		Object object = newInstance(className, (new Object[0]));
		
		Class[] argsClass = new Class[args.length];                                  
		for (int i = 0, j = args.length; i < j; i++) {                               
			argsClass[i] = args[i].getClass();                                       
		}
		
		return invokeMethod(object, methodName, argsClass, args);                    
	}
	
	/**
	 * 直接调用对象方法, 无视private/protected修饰符.
	 * @param object
	 * @param methodName
	 * @param parameterTypes
	 * @param parameterValues
	 * @return
	 */
	public static Object invokeMethod(Object object, String methodName, Class<?>[] parameterTypes, Object[] parameterValues)
	{
		Method method = getDeclaredMethod(object, methodName, parameterTypes);

		if (method == null)
		{
			throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + object + "]");
		}

		method.setAccessible(true);

		try
		{
			return method.invoke(object, parameterValues);
		} catch (Exception e)
		{
			throw convertReflectionExceptionToUnchecked(e);
		}
	}

	/**
	 * 调用Getter方法.
	 * @param object
	 * @param fieldName
	 * @return
	 */
	public static Object invokeGetterMethod(Object object, String fieldName)
	{
		String getterMethodName = "get" + StringUtils.capitalize(fieldName);
		return invokeMethod(object, getterMethodName, new Class[] {}, new Object[] {});
	}

	/**
	 * 调用Setter方法.
	 * @param target
	 * @param fieldName
	 * @param parameterValue
	 * @param parameterType 用于查找Setter方法,为空时使用value的Class替代.
	 */
	public static void invokeSetterMethod(Object target, String fieldName, Class<?> parameterType, Object parameterValue)
	{
		String setterMethodName = "set" + StringUtils.capitalize(fieldName);
		Class<?> type = (parameterType != null ? parameterType : parameterValue.getClass());
		invokeMethod(target, setterMethodName, new Class[] { type }, new Object[] { parameterValue });
	}

	/**
	 * 调用Setter方法.使用value的Class来查找Setter方法.
	 * @param target
	 * @param propertyName
	 * @param parameterValue
	 */
	public static void invokeSetterMethod(Object target, String propertyName, Object parameterValue)
	{
		invokeSetterMethod(target, propertyName, null, parameterValue);
	}

	/**
	 * 强行设置Field可访问.
	 */
	private static void makeAccessible(Field field)
	{
		if (!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()))
		{
			field.setAccessible(true);
		}
	}

	/**
	 *  通过反射, 获得Class定义中声明的父类的泛型参数的类型.
	 *  如无法找到, 返回Object.class.
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> getSuperClassGenricType(Class clazz)
	{
		return getSuperClassGenricType(clazz, 0);
	}

	/**
	 * 通过反射, 获得定义Class时声明的父类的泛型参数的类型.
	 * 如无法找到, 返回Object.class.
	 * @param clazz
	 * @param index
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Class getSuperClassGenricType(Class clazz, int index)
	{

		Type genType = clazz.getGenericSuperclass();

		if (!(genType instanceof ParameterizedType))
		{
			logger.warn(clazz.getSimpleName() + "'s superclass not ParameterizedType");
			return Object.class;
		} else
		{
			Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

			if (index >= params.length || index < 0)
			{
				logger.warn("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: " + params.length);
				return Object.class;
			}
			if (!(params[index] instanceof Class))
			{
				logger.warn(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
				return Object.class;
			}

			return (Class) params[index];
		}
	}

	/**
	 * 将反射时的checked exception转换为unchecked exception.
	 */
	public static RuntimeException convertReflectionExceptionToUnchecked(Exception e)
	{
		if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException || e instanceof NoSuchMethodException)
		{
			return new IllegalArgumentException("Reflection Exception.", e);
		} else if (e instanceof InvocationTargetException)
		{
			return new RuntimeException("Reflection Exception.", ((InvocationTargetException) e).getTargetException());
		} else if (e instanceof RuntimeException)
		{
			return (RuntimeException) e;
		}
		return new RuntimeException("Unexpected Checked Exception.", e);
	}
}
