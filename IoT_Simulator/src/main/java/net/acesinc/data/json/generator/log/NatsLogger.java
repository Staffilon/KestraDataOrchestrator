package net.acesinc.data.json.generator.log;

import nats.client.Nats;
import nats.client.NatsConnector;
import net.acesinc.data.json.util.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Queue;

/**
 * Un logger nats invia eventi json al broker gnatsd specificato nella configurazione. 
 * L'esempio seguente mostra una configurazione di esempio che invia eventi JSON a un broker NATS 
 * in esecuzione localmente in ascolto sulla porta NATS predefinita.
{
    "type": "nats",
    "broker.server": "127.0.0.1",
    "broker.port": 4222,
    "topic": "logevent",
    "flatten": false
}
 * Created by betselot on 6/27/16.
 */
public class NatsLogger extends AbstractEventLogger {
    private static final Logger log = LogManager.getLogger(NatsLogger.class);

    public static final String NATS_SERVER_PROP_NAME = "broker.server";
    public static final String NATS_PORT_PROP_NAME = "broker.port";

    private final String topic;
    private final boolean sync;
    private final boolean flatten;
    private JsonUtils jsonUtils;
    private Nats nats;
    private NatsConnector natsConnector = new NatsConnector();
    StringBuilder natsURL = new StringBuilder("nats://");


    public NatsLogger(Queue<String> queue, Map<String, Object> props) {
    	super(queue);
        String brokerHost = (String) props.get(NATS_SERVER_PROP_NAME);
        Integer brokerPort = (Integer) props.get(NATS_PORT_PROP_NAME);

        natsURL.append(brokerHost);
        natsURL.append(":");
        natsURL.append(brokerPort);

        nats= natsConnector.addHost(natsURL.toString()).connect();

        this.topic = props.get("topic").toString();
        this.sync = (Boolean) props.get("sync");
        this.flatten = (Boolean) props.get("flatten");
        this.jsonUtils = new JsonUtils();

    }

    @Override
    public void logEvent(String event, Map<String, Object> producerConfig) {
        logEvent(event);
    }
    
    private void logEvent(String event) {
        String output = event;
        if (flatten) {
            try {
                output = jsonUtils.flattenJson(event);
            } catch (IOException ex) {
                log.error("Error flattening json. Unable to send event [ " + event + " ]", ex);
                return;
            }
        }

        log.debug("Sending event to ["+ topic +"] on gnatsd: [ " + output + " ]");
        nats.publish(topic,output);
        addEventTrace(String.format("%s %s: published on topic %s", LocalDateTime.now(), "nats", topic));
    }

    @Override
    public void shutdown() {
        nats.close();
    }
}
