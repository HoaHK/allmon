package org.allmon.client.agent.namespace;

import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

abstract class AbstractAgentBeanDefinitionElementParser implements AgentBeanDefinitionElementParser {

	protected static final String ID = "id";
	
	protected static final String AGENT_CONTEXT_REF = "agentContextRef";
	
	private String tagName;
	
//	protected AllmonAgentBeanDefinitionParser parser;
	
//	AbstractAgentBeanDefinitionElementParser(AllmonAgentBeanDefinitionParser parser, String tagName) {
//		this.parser = parser;
//		this.tagName = tagName;
//	}

	abstract AllmonAgentBeanDefinitionParser getParser();
	
	public final void setTagName(String tagName) {
		this.tagName = tagName;
	}
	
	public final String getTagName() {
		return tagName;
	}
	
	protected final Object parseAgentContextProperty(Element element, ParserContext parserContext) {
//		if (element.hasAttribute(AGENT_CONTEXT) && element.hasAttribute(AGENT_CONTEXT_REF)) {
//			parserContext.getReaderContext().error(
//					"Cannot define both 'agentContext' and 'agentContextRef' on <httpUrlCallAgent> tag.",
//					element, this.parseState.snapshot());
//			return null;
//		} else 
//		if (element.hasAttribute(AGENT_CONTEXT)) {
//			String expression = element.getAttribute(AGENT_CONTEXT);
//			AbstractBeanDefinition definition = createPointcutDefinition(expression);
//			pointcutDefinition.setSource(parserContext.extractSource(element));
//			return definition;
//		}
//		else if (element.hasAttribute(AGENT_CONTEXT_REF)) {
//			String agentContextRef = element.getAttribute(AGENT_CONTEXT_REF);
//			if (!StringUtils.hasText(agentContextRef)) {
//				parserContext.getReaderContext().error(
//						"'agentContextRef' attribute contains empty value.", element, this.parseState.snapshot());
//				return null;
//			}
//			return agentContextRef;
//		}
		if (element.hasAttribute(AGENT_CONTEXT_REF)) {
			String agentContextRef = element.getAttribute(AGENT_CONTEXT_REF);
			if (!StringUtils.hasText(agentContextRef)) {
				parserContext.getReaderContext().error(
						"'agentContextRef' attribute contains empty value.", element, getParser().getParseState().snapshot());
				return null;
			}
			return agentContextRef;
		}
		else {
			parserContext.getReaderContext().error(
					"Must define one of 'agentContext' <" + tagName + "> tag.",
					element, getParser().getParseState().snapshot());
			return null;
		}
	}

	protected final String parsePropertyString(Element element, ParserContext parserContext, String attribute, boolean optional, boolean nullable) {
		if (element.hasAttribute(attribute)) {
			String property = element.getAttribute(attribute);
			if (!nullable && !StringUtils.hasText(property)) {
				parserContext.getReaderContext().error(
						"'"+attribute+"' attribute contains empty value.", element, getParser().getParseState().snapshot());
				return null;
			}
			return property;
		}
		else {
			if (!optional) {
				parserContext.getReaderContext().error(
						"Must define one of '" + attribute + "' <" + tagName + "> tag.",
						element, getParser().getParseState().snapshot());
			}
			return null;
		}
	}
	
	protected final String parsePropertyString(Element element, ParserContext parserContext, String attribute, boolean optional) {
		return parsePropertyString(element, parserContext, attribute, optional, false);
	}
	
	protected final String parsePropertyString(Element element, ParserContext parserContext, String attribute) {
		return parsePropertyString(element, parserContext, attribute, false, false);
	}
	
}
