package org.allmon.client.agent.advices;

import org.allmon.client.agent.JavaCallAgent;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;

public class JavaCallAdvice extends AllmonAdvice {
	
	private static final Log logger = LogFactory.getLog(JavaCallAdvice.class);
	
	public JavaCallAdvice() {
		logger.debug("JavaCallAdvice created - name " + getName());
	}
	
	private JavaCallAgent agent;
	
	public Object profile(ProceedingJoinPoint call) throws Throwable {
		if (!isSilentMode()) {
			logger.debug(getName() + " >>> before method call");
		}
		try {
			String className = call.getSignature().getDeclaringTypeName();
			String methodName = call.getSignature().getName();
			call.getSourceLocation().getWithinType();
			
			// FIXME add a parameter which switch this method on/off
			Caller caller = getOriginalCaller(className, methodName);
			//Caller caller = new Caller();
			
    		MetricMessage metricMessage = MetricMessageFactory.createClassMessage(
	                className, methodName, caller.className, caller.methodName, 0); // TODO review duration time param
    		
    		// FIXME add a parameter which switch storing calls parameters on/off
    		Object [] args = call.getArgs();
			metricMessage.setParameters(args);
			
    		agent = new JavaCallAgent(getAgentContext(), metricMessage);
	        agent.entryPoint();
    	} catch (Throwable t) {
    	}
    	
    	// execute an advised method
    	Exception e = null;
		try {
			return call.proceed();
		} catch (Exception ex) {
			e = ex;
			throw ex;
		} finally {
			if (!isSilentMode()) {
				logger.debug(getName() + " >>> after method call");
			}
			agent.exitPoint(e);
		}
	}

    class Caller {
    	String className = "";
    	String methodName = "";
    }
    
    private Caller getOriginalCaller(String className, String methodName) {
		Caller caller = new Caller(); // String [] caller = {"", ""}; //{"callerClass", "callerMethod"};
    	
    	StackTraceElement[] elements = new Throwable().getStackTrace();
		for (int i = 1; i < elements.length; i++) {
			String iclassName = elements[i].getClassName();
			String imethodName = elements[i].getMethodName();
			//String ifileName = elements[i].getFileName();
			
			if (imethodName.equals(methodName) 
				&& iclassName.substring(0, iclassName.indexOf("$$")).equals(className)) {
				caller.className = elements[i+1].getClassName();
				caller.methodName = elements[i+1].getMethodName();
				return caller;
			}
		}
		
    	return caller;
    }
    
}
