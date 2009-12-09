package org.allmon.client.agent;

import junit.framework.TestCase;

public class SnmpHostAgentTest extends TestCase {

    public void testCpu() throws Exception {
        AgentContext agentContext = new AgentContext();
        try {
            SnmpHostAgent agent = new SnmpHostAgent(agentContext);
            agent.setParameters(new String[]{
                    "10.1.132.99"});
            agent.execute();
            Thread.sleep(1000);
        } finally {
            agentContext.stop();
        }
    }
    
}