package org.allmon.server.receiver;

import org.allmon.common.AllmonCommonConstants;
import org.allmon.common.AllmonLoggerConstants;
import org.allmon.common.MetricMessageWrapper;
import org.allmon.server.loader.LoadRawMetric;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LoaderReceiverRouteBuilder extends RouteBuilder {

    private static final Log logger = LogFactory.getLog(LoaderReceiverRouteBuilder.class);
    
    private static final LoadRawMetric loadRawMetric = new LoadRawMetric();
    
    private boolean verboseLogging = AllmonCommonConstants.ALLMON_SERVER_RECEIVER_VERBOSELOGGING;
    
    public void configure() {
    	logger.debug(AllmonLoggerConstants.ENTERED);
        
    	// receiving data from server-side queue
    	// TODO XXX evaluate running loading (storeMetric) process in a separate thread(s) - .threads(int no).
    	// TODO XXX many concurrent threads with this route should fasten loading process (especially for many independent metrics messages)
        from(AllmonCommonConstants.ALLMON_SERVER_CAMEL_QUEUE_READYFORLOADING).process(new Processor() {
            public void process(Exchange e) {
            	if (verboseLogging) {
	                logger.debug(">>>>> Received exchange: " + e.getIn());
	                logger.debug(">>>>> Received exchange body: " + e.getIn().getBody());
            	}
            	
                MetricMessageWrapper metricMessageWrapper = (MetricMessageWrapper)e.getIn().getBody();
                if (metricMessageWrapper != null) {
                    try {
                        // Store metrics
                        loadRawMetric.storeMetric(metricMessageWrapper);
                    } catch (Throwable t) {
                        logger.error(t.getMessage(), t);
                    }
                } else {
                    logger.debug(">>>>> Received exchange: MetricMessageWrapper is null");
                }
                
            	if (verboseLogging) {
    	            logger.debug(">>>>> Received exchange: End.");
            	}
            }
        });
        
        logger.debug(AllmonLoggerConstants.EXITED);
    }
    
}
