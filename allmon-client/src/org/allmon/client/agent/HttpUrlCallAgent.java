package org.allmon.client.agent;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

import org.allmon.common.MetricMessageWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.net.www.protocol.http.HttpURLConnection;

public class HttpUrlCallAgent extends UrlCallAgent {

	private static final Log logger = LogFactory.getLog(HttpUrlCallAgent.class);
    
    private String requestMethod = "POST";
    private String contentType = "application/json; charset=utf-8";
    private String urlParameters = "{ 'componentChecker': 'TTC.iTropics.ComponentCheckers.TropicsDawsComponentChecker, TTC.iTropics.ComponentCheckers' }";
    
    private HttpUrlCallAgentAbstractStrategy strategy = new HttpUrlCallAgentBooleanStrategy(); // default strategy
    
    public HttpUrlCallAgent(AgentContext agentContext) {
		super(agentContext);
	}
    
    public void setStrategy(HttpUrlCallAgentAbstractStrategy strategy) {
        this.strategy = strategy;
    }
    
    MetricMessageWrapper collectMetrics() {
        String metric = "0";
        
        MetricMessageWrapper messageWrapper = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection)makeConnection();
            
            connection.setRequestMethod(requestMethod);
            connection.setRequestProperty("Content-Type", contentType);
            //"application/x-www-form-urlencoded"; "application/json; charset=utf-8";
            
            connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");
            
            connection.setUseCaches(false);
            connection.setDoInput(true);
            
            //Send request
            if ("POST".equals(requestMethod)) {
                //using "doSetOutput(true)" which forces HttpURLConnection to use POST, not GET, 
                connection.setDoOutput(true);
                
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();
            }
            
            //Get Response    
            //DataInputStream dis = new DataInputStream(connection.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            
            // process the response depending on used strategy
            try {
                strategy.setUp(this, br);
                messageWrapper = strategy.extractMetrics();
            } finally {
                br.close();
            }
            
        } catch (MalformedURLException me) {
            //fullSearchResults.append(me.getMessage());
            logger.debug("MalformedURLException: " + me, me);
        } catch (IOException ioe) {
            //fullSearchResults.append(ioe.getMessage());
            logger.debug("IOException: " + ioe, ioe);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        
        return messageWrapper;
    }
   
    void decodeAgentTaskableParams() {
        urlAddress = getParamsString(0);
        searchPhrase = getParamsString(1);
        contentType = getParamsString(2);
        urlParameters = getParamsString(3);
        useProxy = false;
    }
    
}
