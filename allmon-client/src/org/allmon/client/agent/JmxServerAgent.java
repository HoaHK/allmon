package org.allmon.client.agent;

import java.util.List;

import org.allmon.client.agent.jmx.JmxAttributesReader;
import org.allmon.client.agent.jmx.MBeanAttributeData;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;
import org.allmon.common.MetricMessageWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.tools.jconsole.LocalVirtualMachine;

public class JmxServerAgent extends ActiveAgent {

    private static final Log logger = LogFactory.getLog(JmxServerAgent.class);
    
    private JmxAttributesReader jmxReader = new JmxAttributesReader();
    
    protected String lvmNamesRegexp = ""; // all local JVMs
    protected String mbeansAttributesNamesRegexp = ""; // all attributes
    
	public JmxServerAgent(AgentContext agentContext) {
		super(agentContext);
	}

	public final MetricMessageWrapper collectMetrics() {
	    MetricMessageWrapper metricMessageWrapper = new MetricMessageWrapper();
	    List<LocalVirtualMachine> lvmList = jmxReader.getLocalVirtualMachine(lvmNamesRegexp, true);
        for (LocalVirtualMachine localVirtualMachine : lvmList) {
            List<MBeanAttributeData> attributeDataList;
            try {
                attributeDataList = jmxReader.getMBeansAttributesData(localVirtualMachine, mbeansAttributesNamesRegexp, true);
                // extract all attributes and create messages
                for (MBeanAttributeData beanAttributeData : attributeDataList) {
                    //logger.debug("Creating jmx message: " + beanAttributeData.getJvmId() + ":" + beanAttributeData.getJvmName() + " - " + beanAttributeData.toString());
                    MetricMessage metricMessage = MetricMessageFactory.createJmxMessage(
                            beanAttributeData.getJvmId(), beanAttributeData.getJvmName(),
                            beanAttributeData.getMbeanName(), beanAttributeData.getMbeanAttributeName(),
                            beanAttributeData.getValue(), null);
                    metricMessageWrapper.add(metricMessage);
                    //logger.debug("jmx message created: " + metricMessage.toString());
                }
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage(), e);
            }
        }
	    return metricMessageWrapper;
	}

    void decodeAgentTaskableParams() {
        lvmNamesRegexp = getParamsString(0);
        mbeansAttributesNamesRegexp = getParamsString(1);
    }

}
