/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.acesinc.data.json.generator.log;

import net.acesinc.data.json.util.JsonUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ExecutionException;

/**
 *Un produttore Kafka invia eventi json al broker e all'argomento Kafka specificati come stringa.
 *Configuralo in questo modo:
{
    "type": "kafka",
    "broker.server": "192.168.59.103",
    "broker.port": 9092,
    "topic": "logevent",
    "flatten": false,
    "sync": false
}
 *Se sync=false, tutti gli eventi vengono inviati in modo asincrono. 
 *Se per qualche motivo desideri inviare eventi in modo sincrono, imposta sync=true.
 *Se flatten=true, il produttore appiattir� il json prima di inviarlo alla coda Kafka, il che significa che 
 *invece di avere oggetti json nidificati, avrai tutte le propriet� al livello superiore usando la notazione a punti in questo modo:
 {
    "test": {
    	"one": "one",
    	"two": "two"
   	},
   	"array": [
   		{
   			"element1": "one"
   		},{
   			"element2": "two"
   		}]
}
Potrebbe diventare
{
	"test.one": "one",
	"test.two": "two",
	"array[0].element1": "one",
	"array[1].element2": "two"
}
 * @author andrewserff
 */
public class KafkaLogger extends AbstractEventLogger {

    private static final Logger log = LogManager.getLogger(KafkaLogger.class);
    public static final String BROKER_SERVER_PROP_NAME = "broker.server";
    public static final String BROKER_PORT_PROP_NAME = "broker.port";
    public static final String KERBEROS_CONF = "kerberos";

    private final KafkaProducer<String, String> producer;
    private final String topic;
    private final boolean sync;
    private final boolean flatten;
    private final Properties props = new Properties();
    private JsonUtils jsonUtils;

    public KafkaLogger(Queue<String> queue, Map<String, Object> props) {
    	super(queue);
        String brokerHost = (String) props.get(BROKER_SERVER_PROP_NAME);
        Integer brokerPort = (Integer) props.get(BROKER_PORT_PROP_NAME);

        this.props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        this.props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        String bootstrapServerAsString = "";

        if (props.get(KERBEROS_CONF) != null) {
            Map<String, String> kerberosConf = (Map<String, String>) props.get(KERBEROS_CONF);

            bootstrapServerAsString = kerberosConf.get("kafka.brokers.servers");
            this.props.put("security.protocol", kerberosConf.get("kafka.security.protocol"));
            this.props.put("sasl.kerberos.service.name", kerberosConf.get("kafka.service.name"));
            System.setProperty("java.security.auth.login.config", kerberosConf.get("kafka.jaas.file"));
            System.setProperty("java.security.krb5.conf", kerberosConf.get("kerberos.conf.file"));
        } else {
            bootstrapServerAsString = brokerHost + ":" + brokerPort.toString();
        }
        this.props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServerAsString);

        producer = new KafkaProducer<>(this.props);

        this.topic = (String) props.get("topic");
        if (props.get("sync") != null) {
            this.sync = (Boolean) props.get("sync");
        } else {
            this.sync = false;
        }

        if (props.get("flatten") != null) {
            this.flatten = (Boolean) props.get("flatten");
        } else {
            this.flatten = false;
        }

        this.jsonUtils = new JsonUtils();
    }

    @Override
    public void logEvent(String event, Map<String, Object> producerConfig) {
        logEvent(event);
        addEventTrace(String.format("%s %s: published on topic %s", LocalDateTime.now(), "kafka", topic));
    }

    private void logEvent(String event) {
        boolean sync = false;

        String output = event;
        if (flatten) {
            try {
                output = jsonUtils.flattenJson(event);
            } catch (IOException ex) {
                log.error("Error flattening json. Unable to send event [ " + event + " ]", ex);
                return;
            }
        }

        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, output);
        if (sync) {
            try {
                producer.send(producerRecord).get();
            } catch (InterruptedException | ExecutionException ex) {
                //got interrupted while waiting
                log.warn("Thread interrupted while waiting for synchronous response from producer", ex);
            }
        } else {
            log.debug("Sending event to Kafka: [ " + output + " ]");
            producer.send(producerRecord);
        }
    }

    @Override
    public void shutdown() {
        producer.close();
    }

}
