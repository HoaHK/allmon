/**
 * 
 */
package org.allmon.client.agent;

import org.allmon.common.AllmonPropertiesReader;
import org.allmon.common.MetricMessage;

/**
 * Top abstract level of agents definition.<br><br>
 * 
 * <b>Every JVM instance which uses <u>an agent</u> has AgentMetricBuffer 
 * class instantiated for the whole live time.</b>
 * 
 */
abstract class Agent {

    static {
        AllmonPropertiesReader.readLog4jProperties();
    }
    
    final AgentContext agentContext;
    
	Agent(AgentContext agentContext) {
		this.agentContext = agentContext;
	}
	
    void addMetricMessage(MetricMessage metricMessage) {
        agentContext.getMetricBuffer().add(metricMessage);
    }
    
    public final AgentMetricBuffer getMetricBuffer() {
        return agentContext.getMetricBuffer();
    }
	
    String getAgentContextName() {
        return agentContext.getName();
    }
    
}