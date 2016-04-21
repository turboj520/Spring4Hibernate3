package com.keung.spring4hibernate3.common.persistence.cache;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

/**
 * 
 * @author wangzhen
 * @ctime：2014-12-18 下午9:09:45
 * 
 */
@Component
public class BaseAspect
{

	/**
	 * 获取待执行方法的返回值类型
	 * 
	 * @param joinPoint
	 * @return
	 */
	public Class<?> getReturnType(JoinPoint joinPoint)
	{
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		return signature.getReturnType();
	}

	/**
	 * key 定义在注解上，支持SPEL表达式
	 * 
	 * @param key
	 * @param method
	 * @param args
	 * @return
	 */
	public <T> T parseKey(String key, Method method, Object[] args, Class<T> t)
	{
		// 获取被拦截方法参数名列表(使用Spring支持类库)
		LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
		String[] paraNameArr = u.getParameterNames(method);
		// 使用SPEL进行key的解析
		ExpressionParser parser = new SpelExpressionParser();
		// SPEL上下文
		StandardEvaluationContext context = new StandardEvaluationContext();
		// 把方法参数放入SPEL上下文中
		for (int i = 0; i < paraNameArr.length; i++)
		{
			context.setVariable(paraNameArr[i], args[i]);
		}
		return parser.parseExpression(key).getValue(context, t);
	}

	/**
	 * 获取顶层接口或者父类的方法对象
	 * @param pjp
	 * @return
	 */
	public Method getMethod(ProceedingJoinPoint pjp)
	{
		// 获取参数的类型
		Object[] args = pjp.getArgs();
		Class[] argTypes = new Class[pjp.getArgs().length];
		for (int i = 0; i < args.length; i++)
		{
			argTypes[i] = args[i].getClass();
		}
		Method method = null;
		try
		{
			method = pjp.getTarget().getClass().getMethod(pjp.getSignature().getName(), argTypes);
		} catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		} catch (SecurityException e)
		{
			e.printStackTrace();
		}
		return method;
	}
}
