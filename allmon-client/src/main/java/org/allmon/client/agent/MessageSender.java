package org.allmon.client.agent;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.allmon.common.AllmonCommonConstants;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

/**
 * This class is responsible for sending serializable data 
 * to client-side JMS broker.
 * 
 */
public class MessageSender {

    private final static Log logger = LogFactory.getLog(MessageSender.class);
    
    private final String destination;
    
    MessageSender() {
    	System.out.println("Connecting to URL: " + AllmonCommonConstants.CLIENT_BROKER_URL);
    	logger.debug("Connecting to URL: " + AllmonCommonConstants.CLIENT_BROKER_URL);
    	destination = AllmonCommonConstants.CLIENT_BROKER_QUEUE_SUBJECT_AGENTSDATA;
    }
    public MessageSender(String destination) {
    	System.out.println("Connecting to URL: " + AllmonCommonConstants.CLIENT_BROKER_URL);
    	logger.debug("Connecting to URL: " + AllmonCommonConstants.CLIENT_BROKER_URL);
    	this.destination = destination;
    }
    
    private static ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
    		new String[] { "classpath:META-INF/allmonAgentAppContext-jms.xml" });
    
    private JmsTemplate jmsTemplate = (JmsTemplate)context.getBean("allmonSenderJmsTemplate");
    
    private Serializable messageObject;
    
	public Serializable getMessageObject() {
		return messageObject;
	}
    
    public void sendMessage(Serializable messageObject) {
    	this.messageObject = messageObject;
    	jmsTemplate.send(
    			destination, 
    			new MessageCreator() {
					public Message createMessage(Session session) throws JMSException {
						return session.createObjectMessage(getMessageObject());
					}
				});
//    	logPoolStats();
    }
    
    void logPoolStats() {
    	PooledConnectionFactory pcf = (PooledConnectionFactory)context.getBean("jmsFactory"); //(PooledConnectionFactory)cf;
    	logger.debug(">>> IdleTimeout: " + pcf.getIdleTimeout());
    	logger.debug(">>> MaxConnections:" + pcf.getMaxConnections());
    	logger.debug(">>> MaximumActive:" + pcf.getMaximumActive());
    }
    
}