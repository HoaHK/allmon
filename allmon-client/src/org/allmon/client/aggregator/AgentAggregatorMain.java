package org.allmon.client.aggregator;

import javax.jms.ConnectionFactory;

import org.allmon.client.agent.MetricMessageFactory;
import org.allmon.common.AllmonActiveMQConnectionFactory;
import org.allmon.common.AllmonCommonConstants;
import org.allmon.common.MetricMessage;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

public class AgentAggregatorMain {

    public static void main(String args[]) throws Exception {
        CamelContext context = new DefaultCamelContext();
        // Set up the ActiveMQ JMS Components
        ConnectionFactory connectionFactory = AllmonActiveMQConnectionFactory.client();
        context.addComponent(AllmonCommonConstants.CLIENT_CAMEL_JMSQUEUE, JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
        context.addRoutes(new AgentAggreagatorRouteBuilder());
        context.start();
        
        // creating messages
        Thread.sleep(1000);
        ProducerTemplate template = context.createProducerTemplate();
        for (int i = 0; i < 100; i++) {
            // for strings
//            template.sendBodyAndHeader(AllmonCommonConstants.CLIENT_CAMEL_QUEUE_AGENTSDATA, "M:" + i + " ", "MyMessage", "MyMessage");
            // for metrics
            MetricMessage metricMessage = MetricMessageFactory.createClassMessage("class-" + i, "method", "user", (long)(Math.random() * 1000));
            template.sendBodyAndHeader(AllmonCommonConstants.CLIENT_CAMEL_QUEUE_AGENTSDATA, metricMessage, "", "");
            Thread.sleep((long)(Math.random() * 100));
        }
        //Thread.sleep(100 * 365 * 60 * 60 * 1000); // 100 years
        //context.stop();
    }

}
