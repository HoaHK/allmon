package org.allmon.client.agent.aop.annotations.advice;

import org.allmon.common.MetricMessageFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class AspectJAdvice {

	private final static String pointucExpression = "execution(public * org.allmon.client.agent.aop..*.*(..))";
	
	private long timeStarted;
	
	@Around("allPublicMethods()")
	public Object aroundAdvice(ProceedingJoinPoint pjp) throws Throwable {
		try {
			timeStarted = System.nanoTime();

			return pjp.proceed();
		} finally {
			long durationTime = System.nanoTime() - timeStarted;
			
			System.out.println("AspectJAdvice >> " + pjp.getTarget().getClass().getSimpleName() + "."+ pjp.getSignature().getName() + ": " + durationTime);
			MetricMessageFactory.createClassMessage(
					pjp.getTarget().getClass().getSimpleName(), 
					pjp.getSignature().getName(), null, null, durationTime);
		}
	}

	@Pointcut(pointucExpression)
	public void allPublicMethods() {}

}
