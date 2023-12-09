package net.acesinc.data.json.generator.log;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.crossbar.autobahn.wamp.Session;
import net.acesinc.data.json.util.OnConnectHandler;
import net.acesinc.data.json.util.WampClient;

public class WampLogger extends AbstractEventLogger {
	private static final Logger log = LogManager.getLogger(WampLogger.class);
	
	 private static final String PRODUCER_TYPE_NAME = "wamp";
	    private static final String URL = "url";
	    private static final String REALM = "realm";
	    private static final String TOPIC = "topic";
	    
	    private WampClient wampClient=null;
	    private String url;
	    private String realm;
	    private String topic;

	public WampLogger(Queue<String> queue, Map<String, Object> props) {
		super(queue);
		 url = (String) props.get(URL);
		 realm = (String) props.get(REALM);
		 topic = (String) props.get(TOPIC);
		 
	        try {
	        	
	            wampClient = new WampClient(url, realm, new OnConnectHandler() {
					
					@Override
					public void connected(Session session) {
						 session.publish("/test.unicam.it/test", "A message");
	                    System.out.println("done pub");
					}
				});
	        }catch (Exception e){
	            e.printStackTrace();
	        }
//		 wampClient = new WampClient(url, realm, null );
		 System.out.println("url: "+url);
		 System.out.println("realm: "+realm);
		 System.out.println(wampClient.isConnected());
		 
		 
	}

	@Override
	public void logEvent(String event, Map<String, Object> producerConfig) {
		
		try {
			Thread.sleep(10000);
			if(wampClient != null){
	                wampClient.publish(topic, event);
	                addEventTrace(String.format("%s %s: published on topic %s \n\n", LocalDateTime.now(),PRODUCER_TYPE_NAME, topic));
	                Thread.sleep(3000);

	        }
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

	}

	@Override
	public void shutdown() {
		wampClient.close();
	}

}
