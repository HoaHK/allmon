package org.allmon.client.agent.namespace;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.parsing.ParseState;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PassiveAgentBeanDefinitionParser implements BeanDefinitionParser {

	private static final String JAVA_CALL_AGENT = "javaCallAgent";
	private static final String ACTION_CLASS_AGENT = "actionClassAgent";
	private static final String SERVLET_CALL_AGENT = "servletCallAgent";
	
	private ParseState parseState = new ParseState();
	
//	private BeanDefinition activeAgentScheduletDef;
	
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		CompositeComponentDefinition compositeDef =
				new CompositeComponentDefinition(element.getTagName(), parserContext.extractSource(element));
		parserContext.pushContainingComponent(compositeDef);

//		activeAgentScheduletDef = getActiveAgentScheduler(parserContext);
		
		NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				String localName = node.getLocalName();
				AbstractPassiveAgentBeanDefinitionParser parser = null;
				if (JAVA_CALL_AGENT.equals(localName)) {
					parser = new JavaCallAgentBeanDefinitionParser(this);
				} else {
					// TODO add others
				}
				parser.parse((Element) node, parserContext);
			}
		}

		parserContext.popAndRegisterContainingComponent();
		return null;
	}

	public ParseState getParseState() {
		return parseState;
	}

//	public BeanDefinition getActiveAgentScheduletDef() {
//		return activeAgentScheduletDef;
//	}
	
}

class PassiveAgentEntry implements ParseState.Entry {

	private final String name;

	public PassiveAgentEntry(String name) {
		this.name = name;
	}

	public String toString() {
		return "Agent '" + this.name + "'";
	}

}